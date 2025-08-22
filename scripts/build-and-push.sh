#!/bin/bash

# =============================================================================
# IMC Database Server - Build and Push to Cloud Foundry
# =============================================================================

set -e

APP_NAME="imc-db-server"
CONFIG_FILE="scripts/config.env"

echo "🏗️  Building IMC Database Server..."

# Check if config.env exists
if [ ! -f "$CONFIG_FILE" ]; then
    echo "❌ Error: $CONFIG_FILE not found"
    echo "📋 Please copy scripts/config.env.template to scripts/config.env and configure it"
    exit 1
fi

# Source configuration
echo "📋 Loading configuration from $CONFIG_FILE"
source "$CONFIG_FILE"

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

# Set environment variables in Cloud Foundry
echo "⚙️  Setting environment variables..."
cf set-env "$APP_NAME" DB01_HOST "$DB01_HOST"
cf set-env "$APP_NAME" DB01_PORT "$DB01_PORT"
cf set-env "$APP_NAME" DB01_DATABASE "$DB01_DATABASE"
cf set-env "$APP_NAME" DB01_USER "$DB01_USER"
cf set-env "$APP_NAME" DB01_PASSWORD "$DB01_PASSWORD"
cf set-env "$APP_NAME" SPRING_PROFILES_ACTIVE "cloud"

# Push to Cloud Foundry
echo "🚀 Pushing to Cloud Foundry..."
cf push "$APP_NAME" -f manifest.yml

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