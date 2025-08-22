#!/bin/bash

# =============================================================================
# Cloud Foundry Setup and Troubleshooting Script
# =============================================================================

set -e

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

check_cf_cli() {
    log_info "Checking Cloud Foundry CLI installation..."
    
    if ! command -v cf &> /dev/null; then
        log_error "Cloud Foundry CLI is not installed!"
        echo ""
        echo "Installation instructions:"
        echo "  macOS: brew install cloudfoundry/tap/cf-cli"
        echo "  Linux: Download from https://github.com/cloudfoundry/cli/wiki/V8-CLI-Installation-Guide"
        echo "  Windows: Download from https://github.com/cloudfoundry/cli/wiki/V8-CLI-Installation-Guide"
        echo ""
        return 1
    fi
    
    local cf_version
    cf_version=$(cf version | head -1)
    log_success "Cloud Foundry CLI installed: $cf_version"
    return 0
}

check_cf_auth() {
    log_info "Checking Cloud Foundry authentication..."
    
    if ! cf api >/dev/null 2>&1; then
        log_error "Not connected to Cloud Foundry API!"
        echo ""
        echo "Please run: cf login"
        echo "Or set the API endpoint: cf api <api-url>"
        echo ""
        return 1
    fi
    
    local api_url
    api_url=$(cf api | grep "API endpoint" | cut -d':' -f2 | xargs)
    log_success "Connected to Cloud Foundry API: $api_url"
    return 0
}

check_cf_target() {
    log_info "Checking Cloud Foundry target..."
    
    if ! cf target >/dev/null 2>&1; then
        log_error "No organization or space selected!"
        echo ""
        echo "Please select an org and space:"
        echo "  cf target -o <organization-name> -s <space-name>"
        echo ""
        echo "Or list available orgs/spaces:"
        echo "  cf orgs"
        echo "  cf spaces"
        echo ""
        return 1
    fi
    
    log_success "Target set successfully:"
    cf target
    return 0
}

check_app_exists() {
    local app_name="$1"
    
    log_info "Checking if app '$app_name' exists..."
    
    if cf app "$app_name" --guid >/dev/null 2>&1; then
        log_success "App '$app_name' found"
        
        # Get app status
        local app_status
        app_status=$(cf app "$app_name" | grep "requested state" | awk '{print $3}')
        log_info "App status: $app_status"
        
        # Get app routes
        local routes
        routes=$(cf app "$app_name" --guid 2>/dev/null | xargs -I {} cf curl "/v2/apps/{}/routes" 2>/dev/null | jq -r '.resources[].entity.host + "." + .resources[].entity.domain.name' 2>/dev/null || echo "")
        
        if [ -n "$routes" ]; then
            log_success "App routes:"
            echo "$routes" | while read -r route; do
                echo "  https://$route"
            done
        else
            log_warning "No routes found for app"
        fi
        
        return 0
    else
        log_error "App '$app_name' not found"
        return 1
    fi
}

deploy_app() {
    local app_name="$1"
    
    log_info "Deploying app '$app_name'..."
    
    if [ ! -f "manifest.yml" ]; then
        log_error "manifest.yml not found in current directory"
        return 1
    fi
    
    if [ ! -f "target/${app_name}-*.jar" ]; then
        log_error "JAR file not found. Please build the project first: mvn clean package"
        return 1
    fi
    
    log_info "Pushing app to Cloud Foundry..."
    if cf push "$app_name" -f manifest.yml; then
        log_success "App deployed successfully!"
        check_app_exists "$app_name"
    else
        log_error "App deployment failed"
        return 1
    fi
}

show_usage() {
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo ""
    echo "Commands:"
    echo "  check           Check CF environment (default)"
    echo "  deploy APP      Deploy app to Cloud Foundry"
    echo "  help            Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                    # Check CF environment"
    echo "  $0 check              # Check CF environment"
    echo "  $0 deploy myapp       # Deploy 'myapp' to CF"
    echo ""
}

main() {
    case "${1:-check}" in
        check)
            log_info "Cloud Foundry Environment Check"
            echo "=================================="
            echo ""
            
            if check_cf_cli; then
                if check_cf_auth; then
                    if check_cf_target; then
                        log_success "Cloud Foundry environment is ready!"
                        echo ""
                        log_info "You can now run:"
                        echo "  ./scripts/build-and-push.sh"
                        echo "  ./scripts/test-api.sh -c"
                    fi
                fi
            fi
            ;;
        deploy)
            if [ -z "$2" ]; then
                log_error "Please specify an app name"
                echo "Usage: $0 deploy <app-name>"
                exit 1
            fi
            deploy_app "$2"
            ;;
        help|--help|-h)
            show_usage
            ;;
        *)
            log_error "Unknown command: $1"
            show_usage
            exit 1
            ;;
    esac
}

main "$@"
