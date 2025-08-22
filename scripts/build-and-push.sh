#!/bin/bash

# =============================================================================
# IMC Database Server - Build and Push to Cloud Foundry
# =============================================================================

set -e

APP_NAME="imc-db-server"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "🏗️  Building IMC Database Server..."

# Build the application
echo "🔨 Building Maven project..."
mvn clean package -DskipTests

# Check if jar was built
JAR_FILE="target/${APP_NAME}-*.jar"
if [ ! -f $JAR_FILE ]; then
    echo "❌ Error: JAR file not found in target/"
    exit 1
fi

# Login to Cloud Foundry (if not already logged in)
echo "🔐 Checking Cloud Foundry authentication..."
if ! cf api >/dev/null 2>&1; then
    echo "❌ Please login to Cloud Foundry first: cf login"
    exit 1
fi

# Check if we're logged in to a specific org/space
if ! cf target >/dev/null 2>&1; then
    echo "❌ Please select an org and space: cf target -o <org> -s <space>"
    exit 1
fi

# Display current target
echo "📍 Current target:"
cf target
echo ""

# Prepare the manifest with environment variables and correct domain
echo "📋 Preparing Cloud Foundry manifest..."
MANIFEST_TEMP=$(cd "$SCRIPT_DIR" && CALLED_FROM_BUILD=true ./prepare-manifest.sh)

if [ $? -ne 0 ]; then
    echo "❌ Failed to prepare manifest"
    exit 1
fi

# Deploy the application
echo "🚀 Deploying to Cloud Foundry..."
if cf push "$APP_NAME" -f "$MANIFEST_TEMP"; then
    echo "✅ Deployment complete!"
    
    # Get the actual deployed URL
    echo "📊 Getting deployed app URL..."
    if cf app "$APP_NAME" --guid >/dev/null 2>&1; then
        local routes
        routes=$(cf app "$APP_NAME" --guid 2>/dev/null | xargs -I {} cf curl "/v2/apps/{}/routes" 2>/dev/null | jq -r '.resources[].entity.host + "." + .resources[].entity.domain.name' 2>/dev/null || echo "")
        
        if [ -n "$routes" ]; then
            local first_route=$(echo "$routes" | head -1)
            echo "🌐 App URL: https://$first_route"
        else
            echo "⚠️  Could not retrieve app URL, check manually with: cf app $APP_NAME"
        fi
    else
        echo "⚠️  App not found, check deployment status with: cf app $APP_NAME"
    fi
    
    echo "🔍 Logs: cf logs $APP_NAME --recent"
    echo "🧪 Test the API: ./scripts/test-api.sh -c"
    echo "🐛 Debug manifest: cf-manifest.yml (kept for troubleshooting)"
else
    echo "❌ Deployment failed"
    exit 1
fi

# Clean up backup and temporary files (keep main manifest for debugging)
cleanup_backup_files() {
    local files_cleaned=0
    
    # Only clean up backup and temporary files, keep the main generated manifest
    for file in manifest.temp.yml cf-manifest.yml.bak cf-manifest.yml.tmp manifest.temp.yml.bak manifest.temp.yml.tmp; do
        if [ -f "$file" ]; then
            rm -f "$file"
            ((files_cleaned++))
        fi
    done
    
    if [ $files_cleaned -gt 0 ]; then
        echo "🧹 Cleaned up $files_cleaned backup/temp file(s) (kept cf-manifest.yml for debugging)"
    fi
}

# Ensure cleanup happens on script exit (success or failure)
trap cleanup_backup_files EXIT

# Clean up at the end as well
cleanup_backup_files