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
echo "📊 App URL: https://${APP_NAME}.apps.your-cf-domain.com"
echo "🔍 Logs: cf logs $APP_NAME --recent"