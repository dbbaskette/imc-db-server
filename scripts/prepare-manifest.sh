#!/bin/bash

# =============================================================================
# Prepare Manifest for Cloud Foundry Deployment
# =============================================================================
# This script dynamically updates manifest.yml with:
# 1. Environment variables from config.env
# 2. Correct domain from cf domains
# 3. Proper route configuration
# =============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
CONFIG_FILE="$SCRIPT_DIR/config.env"
MANIFEST_FILE="$PROJECT_ROOT/manifest.yml"
MANIFEST_TEMP="$PROJECT_ROOT/manifest.temp.yml"
APP_NAME="imc-db-server"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

log() {
    echo -e "$1"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# Check prerequisites
check_prerequisites() {
    log_info "Checking prerequisites..."
    
    # Check if config.env exists
    if [ ! -f "$CONFIG_FILE" ]; then
        log_error "Config file not found: $CONFIG_FILE"
        log "Please copy scripts/config.env.template to scripts/config.env and configure it"
        exit 1
    fi
    
    # Check if manifest.yml exists
    if [ ! -f "$MANIFEST_FILE" ]; then
        log_error "Manifest file not found: $MANIFEST_FILE"
        exit 1
    fi
    
    # Check if cf CLI is available
    if ! command -v cf &> /dev/null; then
        log_error "Cloud Foundry CLI is not installed"
        exit 1
    fi
    
    # Check if we're logged into Cloud Foundry
    if ! cf api >/dev/null 2>&1; then
        log_error "Not logged into Cloud Foundry. Please run: cf login"
        exit 1
    fi
    
    log_success "Prerequisites check passed"
}

    # Get the first available shared domain
    get_cf_domain() {
        # Get the first shared domain (usually the apps domain)
        local domain
        domain=$(cf domains | grep "shared" | head -1 | awk '{print $1}')
        
        if [ -z "$domain" ]; then
            if [ "$CALLED_FROM_BUILD" != "true" ]; then
                log_error "No shared domains found"
            fi
            exit 1
        fi
        
        if [ "$CALLED_FROM_BUILD" != "true" ]; then
            log_success "Using domain: $domain"
        fi
        echo "$domain"
    }

# Load environment variables from config.env
load_config() {
    log_info "Loading configuration from $CONFIG_FILE..."
    
    # Source the config file
    if source "$CONFIG_FILE"; then
        log_success "Configuration loaded"
    else
        log_error "Failed to load configuration"
        exit 1
    fi
    
    # Validate required variables
    local required_vars=("DB01_HOST" "DB01_PORT" "DB01_DATABASE" "DB01_USER" "DB01_PASSWORD")
    local missing_vars=()
    
    for var in "${required_vars[@]}"; do
        if [ -z "${!var}" ]; then
            missing_vars+=("$var")
        fi
    done
    
    if [ ${#missing_vars[@]} -gt 0 ]; then
        log_error "Missing required environment variables: ${missing_vars[*]}"
        exit 1
    fi
    
    log "Database Host: $DB01_HOST"
    log "Database Port: $DB01_PORT"
    log "Database Name: $DB01_DATABASE"
    log "Database User: $DB01_USER"
    log "Database Password: [HIDDEN]"
}

# Create temporary manifest with actual values
create_temp_manifest() {
    local domain="$1"
    
    log_info "Creating temporary manifest with environment variables..."
    
    # Create a copy of the manifest
    cp "$MANIFEST_FILE" "$MANIFEST_TEMP"
    
    # Replace placeholder values with actual environment variables
    sed -i.bak "s/PLACEHOLDER_HOST/$DB01_HOST/g" "$MANIFEST_TEMP"
    sed -i.bak "s/PLACEHOLDER_PORT/$DB01_PORT/g" "$MANIFEST_TEMP"
    sed -i.bak "s/PLACEHOLDER_DATABASE/$DB01_DATABASE/g" "$MANIFEST_TEMP"
    sed -i.bak "s/PLACEHOLDER_USER/$DB01_USER/g" "$MANIFEST_TEMP"
    sed -i.bak "s/PLACEHOLDER_PASSWORD/$DB01_PASSWORD/g" "$MANIFEST_TEMP"
    
    # Add the route with the correct domain
    # First, remove the comment lines
    sed -i.bak "/# Routes will be set dynamically by build script/d" "$MANIFEST_TEMP"
    sed -i.bak "/# using cf domains to get the available domain/d" "$MANIFEST_TEMP"
    
    # Then add the routes section at the end (before the closing ---)
    # Use a simpler approach - just append the routes before the last ---
    local temp_content
    temp_content=$(cat "$MANIFEST_TEMP")
    
    # Remove the last line (which should be ---) and add routes + ---
    echo "${temp_content%---}" > "$MANIFEST_TEMP"
    echo "  routes:" >> "$MANIFEST_TEMP"
    echo "      - route: $APP_NAME.$domain" >> "$MANIFEST_TEMP"
    echo "---" >> "$MANIFEST_TEMP"
    
    # Remove backup files
    rm -f "$MANIFEST_TEMP.bak"
    
    log_success "Temporary manifest created: $MANIFEST_TEMP"
}

# Clean up temporary files
cleanup() {
    # Only clean up if we're not being called from build script
    if [ "$CALLED_FROM_BUILD" != "true" ] && [ -f "$MANIFEST_TEMP" ]; then
        rm -f "$MANIFEST_TEMP"
        log_info "Cleaned up temporary manifest"
    fi
}

# Main execution
main() {
    log_info "Preparing Cloud Foundry manifest..."
    echo "=========================================="
    
    # Set up cleanup on exit
    trap cleanup EXIT
    
    check_prerequisites
    load_config
    
    local domain
    domain=$(get_cf_domain)
    
    create_temp_manifest "$domain"
    
    log_success "Manifest preparation complete!"
    log_info "You can now deploy with: cf push -f $MANIFEST_TEMP"
    log_info "Or use the build script: ./scripts/build-and-push.sh"
    
    # Keep the temp file for the build script to use
    # Output the full path for the build script
    echo "$MANIFEST_TEMP"
    
    # Don't clean up if called from build script
    if [ "$CALLED_FROM_BUILD" = "true" ]; then
        log_info "Keeping temporary manifest for build script: $MANIFEST_TEMP"
        # Remove the cleanup trap
        trap - EXIT
    fi
}

# Run main function
main "$@"
