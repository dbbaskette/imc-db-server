# IMC Database Server

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

**✅ Completed (Phases 1-2)**
- Core infrastructure and project setup
- Multi-database configuration and connection management
- JPA entities and repository pattern
- Fleet management APIs with query parameters
- Health check and monitoring endpoints
- Standardized JSON response format

**🔄 In Progress (Phase 3)**
- ML Pipeline integration with MADlib
- Async job processing for ML model recalculation

**📅 Planned (Phases 4-5)**
- Vehicle events APIs with advanced filtering
- Performance optimization and caching
- Production monitoring and security hardening

See [DEVPLAN.md](DEVPLAN.md) for detailed development roadmap.

## 🔧 Development

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