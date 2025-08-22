# IMC Database Server

![IMC Logo](./assets/logo.png)

> A multi-database Spring Boot service providing REST APIs for Insurance MegaCorp's Safe Driver Scoring, ML model management, and vehicle events data.

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.3-green.svg)](https://spring.io/projects/spring-boot)
[![Build](https://img.shields.io/badge/Build-Maven-orange.svg)](https://maven.apache.org/)

## 🚀 Features

- **Multi-Database Support**: Named database instances (db01, db02, etc.) with instance-aware APIs
- **Fleet Management**: Driver safety scoring, risk analysis, and performance metrics
- **ML Pipeline**: MADlib integration for machine learning model management
- **Query Flexibility**: Support for filtering, pagination, and sorting
- **Cloud Ready**: Built for Cloud Foundry deployment with health monitoring

## 📊 API Endpoints

### Health & Monitoring
```http
GET /api/{instance}/health                 # Database health check
GET /api/{instance}/database/info          # Connection status
GET /api/{instance}/database/stats         # Database statistics
```

### Fleet Management
```http
GET /api/{instance}/fleet/summary          # Fleet safety overview
GET /api/{instance}/drivers/active-count   # Active drivers count
GET /api/{instance}/drivers/high-risk-count # High-risk drivers count
```

### Driver Analytics
```http
GET /api/{instance}/drivers/top-performers?limit=10    # Best performing drivers
GET /api/{instance}/drivers/high-risk?limit=10         # High-risk drivers
```

### ML Pipeline Management
```http
GET /api/{instance}/ml/model-info          # Current ML model information
POST /api/{instance}/ml/recalculate        # Start async ML recalculation
GET /api/{instance}/ml/job-status/{jobId}  # Check job status
```

### Vehicle Events & Telemetry
```http
GET /api/{instance}/vehicle-events?driver_id=123&limit=10  # Query events with filtering
GET /api/{instance}/vehicle-events/crashes?severity=high   # Crash events only
GET /api/{instance}/telemetry/events-count                 # Event count statistics  
POST /api/{instance}/vehicle-events/batch                  # Batch event ingestion
```

### Example API Calls
```bash
# Get fleet summary for database instance db01
curl http://localhost:8084/api/db01/fleet/summary

# Get top 5 performing drivers
curl http://localhost:8084/api/db01/drivers/top-performers?limit=5

# Check database health
curl http://localhost:8084/api/db01/health
```

## 🛠️ Quick Start

### Prerequisites
- Java 21+
- Maven 3.6+
- PostgreSQL/Greenplum database access

### Local Development

1. **Clone and configure**
   ```bash
   git clone https://github.com/dbbaskette/imc-db-server.git
   cd imc-db-server
   
   # Copy and configure database settings
   cp scripts/config.env.template scripts/config.env
   # Edit scripts/config.env with your actual database credentials
   # IMPORTANT: Never commit config.env - it contains sensitive data
   ```

2. **Source configuration and run**
   ```bash
   source scripts/config.env
   mvn spring-boot:run
   ```

3. **Test the API**
   ```bash
   curl http://localhost:8084/api/db01/health
   ```

### Cloud Foundry Deployment

```bash
# Configure your database credentials in scripts/config.env
source scripts/config.env

# Build and deploy
./scripts/build-and-push.sh
```

#### Enhanced Cloud Foundry Scripts

We've enhanced the Cloud Foundry deployment with intelligent scripts that handle common deployment issues:

**🔧 Build and Push Script (`build-and-push.sh`)**
- Automatically detects if app exists and creates it if needed
- Sets environment variables after app creation
- Handles both new deployments and updates gracefully
- Displays actual deployed app URL after deployment
- Keeps `cf-manifest.yml` for debugging (ignored by git)

**🧪 Smart Testing Script (`test-api.sh`)**
- **Local Testing**: `./scripts/test-api.sh` (default)
- **Cloud Foundry Testing**: `./scripts/test-api.sh -c` (automatically detects deployed route)
- **Custom URL Testing**: `./scripts/test-api.sh -u https://myapp.com`
- **Instance Testing**: `./scripts/test-api.sh -i db02`

**🛠️ CF Setup Helper (`cf-setup.sh`)**
- Environment validation and troubleshooting
- Direct app deployment: `./scripts/cf-setup.sh deploy imc-db-server`
- Comprehensive error checking and guidance

**📚 Complete Documentation**: See [scripts/README.md](scripts/README.md) for detailed usage examples.

## ⚙️ Configuration

### Database Instances
Configure multiple database instances in `scripts/config.env`:

```bash
# Database Instance: db01
export DB01_HOST="your-greenplum-host"
export DB01_PORT="5432"
export DB01_DATABASE="insurance_megacorp"
export DB01_USER="gpadmin"
export DB01_PASSWORD="your-password"

# Database Instance: db02 (optional)
export DB02_HOST="backup-db-host"
# ... additional instances
```

### Application Profiles
- **local**: Development with detailed logging
- **cloud**: Production optimized for Cloud Foundry

## 🏗️ Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   REST APIs     │────│  Service Layer   │────│  Database Layer │
│  (Multi-tenant) │    │ (Business Logic) │    │ (Multi-instance)│
└─────────────────┘    └──────────────────┘    └─────────────────┘
         │                        │                        │
    ┌────────────┐         ┌──────────────┐        ┌───────────────┐
    │ Instance   │         │ Fleet        │        │ db01: Primary │
    │ Routing    │         │ Service      │        │ db02: Backup  │
    │ /api/db01  │         │ ML Service   │        │ ...           │
    └────────────┘         └──────────────┘        └───────────────┘
```

### Key Components
- **DatabaseInstanceManager**: Routes requests to appropriate database instances
- **FleetService**: Business logic for driver analytics and fleet management
- **Global Exception Handler**: Standardized error responses
- **Multi-Database Configuration**: HikariCP connection pooling per instance

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run with integration tests (requires PostgreSQL/Testcontainers)
mvn verify

# Build without tests
mvn clean package -DskipTests
```

## 📋 Development Status

**✅ All Phases Completed (1-5)**
- ✅ **Phase 1-2**: Core infrastructure and database integration
- ✅ **Phase 3**: ML Pipeline integration with MADlib and async processing  
- ✅ **Phase 4**: Vehicle events APIs with advanced filtering and analytics
- ✅ **Phase 5**: Production readiness with testing, security, and monitoring

### Key Features Implemented
- **Multi-database support** with instance routing
- **Complete ML pipeline** with MADlib integration and job tracking
- **Advanced filtering** for vehicle events with pagination and sorting
- **Comprehensive testing** including unit tests and Testcontainers integration
- **Production security** with input sanitization and rate limiting
- **Performance monitoring** with correlation IDs and execution tracking
- **Health monitoring** with database connection testing

### 🆕 Recent Improvements & Fixes

**Spring Boot 3.x Compatibility**
- ✅ Fixed deprecated `@AutoConfigureTestDatabase` annotation
- ✅ Updated test configuration for Spring Boot 3.x
- ✅ Converted integration tests to use `@WebMvcTest` with proper mocking
- ✅ All tests now passing (32/32)

**Cloud Foundry Deployment**
- ✅ Enhanced build script with intelligent app creation/update logic
- ✅ Fixed environment variable setting for new apps
- ✅ Added automatic route detection and display
- ✅ Created comprehensive CF environment troubleshooting tools

**JPA Entity Mapping**
- ✅ Fixed `Map<String, BigDecimal>` to `jsonb` mapping issues
- ✅ Implemented proper `JsonMapConverter` for complex data types
- ✅ Resolved Hibernate type resolution warnings

**Maven & Java Compatibility**
- ✅ Created Maven wrapper for consistent Java 21 usage
- ✅ Fixed Java version compatibility issues
- ✅ Added `.mvn/jvm.config` for explicit Java 21 targeting

See [DEVPLAN.md](DEVPLAN.md) for detailed development roadmap.

## 🔧 Development

### Troubleshooting Common Issues

**Cloud Foundry Deployment Issues**
```bash
# Check your CF environment
./scripts/cf-setup.sh

# Common fixes:
cf login                    # Login to Cloud Foundry
cf target -o <org> -s <space>  # Set target org/space
cf delete imc-db-server    # Remove existing app if needed
./scripts/build-and-push.sh    # Redeploy
```

**Test Failures**
```bash
# Run tests with verbose output
mvn test -X

# Check Java version compatibility
java -version              # Should be Java 21
mvn --version             # Maven should use Java 21

# Clean and rebuild
mvn clean package
```

**Database Connection Issues**
```bash
# Verify database credentials in scripts/config.env
# Check database accessibility
# Ensure proper network access to database hosts
```

### Project Structure
```
src/main/java/com/insurancemegacorp/dbserver/
├── controller/     # REST API endpoints
├── service/        # Business logic
├── repository/     # Data access layer
├── model/          # JPA entities
├── dto/            # Data Transfer Objects
├── config/         # Configuration classes
├── exception/      # Error handling
└── util/           # Utility classes
```

### Database Schema
The service works with existing Insurance MegaCorp database tables:
- `safe_driver_scores`: Driver safety scores and risk categories
- `driver_ml_training_data`: ML training dataset with driver metrics
- `driver_accident_model`: MADlib model metadata and feature weights
- `vehicle_events`: Vehicle telemetry data (future)

## 📝 Contributing

1. Follow the [CLAUDE.md](CLAUDE.md) development guidelines
2. Use Google Java Style Guide conventions
3. Write tests for all new business logic
4. Ensure all endpoints include proper error handling

## 📜 License

Internal Insurance MegaCorp project - Proprietary

---

**Quick Links:**
- 📋 [Project Requirements](PROJECT.md)
- 🗺️ [Development Plan](DEVPLAN.md)
- 🔧 [Configuration Guide](scripts/config.env.template)
- 🚀 [Deployment Script](scripts/build-and-push.sh)