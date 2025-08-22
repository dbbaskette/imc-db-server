# Cloud Foundry Deployment Scripts

This directory contains scripts for building, deploying, and testing the IMC Database Server on Cloud Foundry.

## Scripts Overview

### 1. `build-and-push.sh` - Build and Deploy
Builds the Maven project and deploys it to Cloud Foundry.

```bash
./scripts/build-and-push.sh
```

**What it does:**
- Builds the Maven project (`mvn clean package`)
- Sets Cloud Foundry environment variables
- Pushes the app to Cloud Foundry
- Displays the deployed app URL

**Prerequisites:**
- Cloud Foundry CLI installed (`cf`)
- Logged into Cloud Foundry (`cf login`)
- Target org/space set (`cf target -o <org> -s <space>`)
- `scripts/config.env` file configured

### 2. `test-api.sh` - API Testing
Tests the deployed API endpoints.

```bash
# Test local server (default)
./scripts/test-api.sh

# Test Cloud Foundry deployed app
./scripts/test-api.sh -c

# Test specific URL
./scripts/test-api.sh -u https://myapp.apps.cf.com

# Test different database instance
./scripts/test-api.sh -i db02
```

**Options:**
- `-c, --cf` - Use Cloud Foundry route (requires cf CLI)
- `-u, --url URL` - Set custom base URL
- `-i, --instance NAME` - Set database instance name
- `-v, --verbose` - Enable verbose output
- `-h, --help` - Show help message

### 3. `cf-setup.sh` - Cloud Foundry Environment Check
Helps troubleshoot Cloud Foundry setup issues.

```bash
# Check CF environment
./scripts/cf-setup.sh

# Deploy app directly
./scripts/cf-setup.sh deploy imc-db-server

# Show help
./scripts/cf-setup.sh help
```

## Common Issues and Solutions

### Issue: "cf CLI is not installed"
**Solution:** Install Cloud Foundry CLI
```bash
# macOS
brew install cloudfoundry/tap/cf-cli

# Linux/Windows
# Download from: https://github.com/cloudfoundry/cli/wiki/V8-CLI-Installation-Guide
```

### Issue: "Not logged into Cloud Foundry"
**Solution:** Login to Cloud Foundry
```bash
cf login
# Follow the prompts to enter your credentials
```

### Issue: "No organization or space selected"
**Solution:** Set target org and space
```bash
# List available orgs
cf orgs

# List available spaces
cf spaces

# Set target
cf target -o <organization-name> -s <space-name>
```

### Issue: "App deployment fails"
**Solutions:**
1. Check if app already exists: `cf app imc-db-server`
2. Delete existing app: `cf delete imc-db-server`
3. Check app logs: `cf logs imc-db-server --recent`
4. Verify manifest.yml configuration

### Issue: "Environment variables not set"
**Solution:** Check your `scripts/config.env` file
```bash
# Copy template if it doesn't exist
cp scripts/config.env.template scripts/config.env

# Edit the file with your database credentials
nano scripts/config.env
```

## Configuration

### Environment Variables
Create `scripts/config.env` with your database credentials:

```bash
# Database Configuration
DB01_HOST=your-db-host
DB01_PORT=5432
DB01_DATABASE=your-db-name
DB01_USER=your-db-user
DB01_PASSWORD=your-db-password
```

### Manifest Configuration
The `manifest.yml` file configures the Cloud Foundry deployment:

```yaml
applications:
  - name: imc-db-server
    path: ./target/imc-db-server-1.1.0.jar
    memory: 1G
    instances: 2
    buildpacks:
      - java_buildpack
    env:
      JAVA_OPTS: "-XX:+UseG1GC -XX:MaxMetaspaceSize=256m"
      JBP_CONFIG_OPEN_JDK_JRE: '{ version: 21.+ }'
      SPRING_PROFILES_ACTIVE: cloud
    health-check-type: http
    health-check-http-endpoint: /actuator/health
    timeout: 180
```

## Workflow

### 1. Initial Setup
```bash
# Check Cloud Foundry environment
./scripts/cf-setup.sh

# Configure environment variables
cp scripts/config.env.template scripts/config.env
# Edit scripts/config.env with your credentials
```

### 2. Deploy
```bash
# Build and deploy
./scripts/build-and-push.sh
```

### 3. Test
```bash
# Test the deployed API
./scripts/test-api.sh -c
```

### 4. Monitor
```bash
# Check app status
cf app imc-db-server

# View logs
cf logs imc-db-server --recent

# Scale app if needed
cf scale imc-db-server -i 3
```

## Troubleshooting

### Check App Status
```bash
cf app imc-db-server
```

### View Recent Logs
```bash
cf logs imc-db-server --recent
```

### Check Environment Variables
```bash
cf env imc-db-server
```

### Restart App
```bash
cf restart imc-db-server
```

### Delete and Redeploy
```bash
cf delete imc-db-server
./scripts/build-and-push.sh
```

## Support

If you encounter issues:

1. Run `./scripts/cf-setup.sh` to check your environment
2. Check the Cloud Foundry logs: `cf logs imc-db-server --recent`
3. Verify your configuration in `scripts/config.env`
4. Ensure you have the necessary permissions in your Cloud Foundry org/space
