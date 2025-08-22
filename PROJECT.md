# IMC Database Server

> **Project Overview**: Dedicated Spring Boot service providing standardized REST APIs for Insurance MegaCorp database interactions, supporting Safe Driver Scoring, ML model management, and vehicle events data across multiple database instances.

---

## 1. Project Goals

**Primary Goal**: Build a multi-database Spring Boot service (`imc-db-server`) that provides REST APIs for:
- Safe Driver Scoring and ML model management
- Vehicle events data and telemetry analytics
- Database health monitoring and statistics
- Support for multiple named database instances

**End Users**: 
- Internal web dashboards and UIs
- Other IMC ecosystem microservices  
- Data analysts and ML engineers
- Future accident-mcp-server integration

## 2. Tech Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.5.3
- **Database**: Greenplum with MADlib extensions (PostgreSQL driver)
- **Build Tool**: Maven
- **Deployment**: Cloud Foundry
- **Local Port**: 8084

**Key Dependencies**:
- Spring Data JPA (database access)
- Spring Web (REST APIs)
- Spring Boot Actuator (health checks)
- Spring Validation (input validation)
- HikariCP (connection pooling)

## 3. Multi-Database Architecture

### Database Instance Configuration
The service supports multiple named database instances via `config.env`:

```bash
# Database Instance: db01
export DB01_HOST="big-data-001.kuhn-labs.com" 
export DB01_PORT="5432"
export DB01_DATABASE="insurance_megacorp"
export DB01_USER="gpadmin"
export DB01_PASSWORD="VMware1!"

# Database Instance: db02 (future)
export DB02_HOST="..." 
# etc.
```

### API Pattern with Database Instances
APIs include the database instance name in the URL:
```
GET /api/{instance}/health
GET /api/{instance}/fleet/summary
GET /api/{instance}/vehicle-events?where=driver_id=123&limit=10
```

Examples:
- `GET /api/db01/fleet/summary`
- `GET /api/db01/vehicle-events?limit=50&where=event_type='CRASH'`

## 4. API Endpoints

### Health & Configuration
```
GET /api/{instance}/health                 # Database health check
GET /api/{instance}/database/info          # Connection status
GET /api/{instance}/database/stats         # Database statistics
```

### Fleet Management 
```
GET /api/{instance}/fleet/summary          # Fleet safety overview
GET /api/{instance}/drivers/active-count   # Active drivers count
GET /api/{instance}/drivers/high-risk-count # High-risk drivers
```

### Driver Analytics
```
GET /api/{instance}/drivers/top-performers?limit=10    # Best drivers
GET /api/{instance}/drivers/high-risk?limit=10        # High-risk drivers
```

### ML Model Management
```
GET /api/{instance}/ml/model-info          # Current model information
POST /api/{instance}/ml/recalculate        # Async ML pipeline execution
GET /api/{instance}/ml/job-status/{jobId}  # Async job status tracking
```

### Vehicle Events & Telemetry
```
GET /api/{instance}/vehicle-events?where=driver_id=123&limit=10
GET /api/{instance}/telemetry/events-count
POST /api/{instance}/vehicle-events/batch  # Batch ingestion
```

### Query Parameters
- `limit` - Limit results (default: 100, max: 1000)
- `offset` - Pagination offset
- `where` - SQL-like filtering (sanitized)
- `orderBy` - Sort field and direction

## 5. Project Structure

```
src/main/java/com/insurancemegacorp/dbserver/
├── controller/          # REST API endpoints  
├── service/            # Business logic & ML pipeline
├── repository/         # Database access layer
├── model/              # JPA entities
├── dto/                # Data Transfer Objects
├── config/             # Multi-database configuration
├── exception/          # Error handling
└── util/               # Utilities & query builders

src/main/resources/
├── application.yml         # Base configuration
├── application-local.yml   # Local development  
├── application-cloud.yml   # Cloud Foundry
└── sql/                   # MADlib ML scripts

scripts/
├── build-and-push.sh      # Build & CF deployment
├── config.env.template    # Configuration template
└── cf-manifest.yml        # Cloud Foundry manifest
```

## 6. Configuration & Deployment

### Local Development
- Uses `config.env` (git-ignored) sourced for database credentials
- Port 8084 to avoid conflicts
- H2 database option for offline development

### Cloud Foundry Production  
- Database credentials via service bindings or manifest environment
- Auto-scaling configuration
- Health check endpoints for monitoring

### Multi-Database Support
- Named database instances (db01, db02, etc.)
- Instance-specific connection pools
- Failover and circuit breaker patterns

## 7. Development Phases

### Phase 1: Core Infrastructure
- [x] Project structure and configuration
- [x] Multi-database connection management  
- [x] Health check endpoints
- [x] Basic fleet summary APIs

### Phase 2: Database Integration
- [x] JPA entities and repositories
- [x] Driver analytics endpoints
- [x] Query parameter filtering
- [x] Error handling and validation

### Phase 3: ML Pipeline Integration  
- [ ] MADlib model information retrieval
- [ ] Async ML recalculation with job tracking
- [ ] Feature importance and model metrics

### Phase 4: Advanced Features
- [ ] Vehicle events APIs with complex filtering
- [ ] Performance optimization and caching
- [ ] Batch data ingestion endpoints

### Phase 5: Production Ready
- [ ] Comprehensive monitoring and logging
- [ ] Security hardening  
- [ ] Load testing and optimization

## 8. Requirements Summary

**Resolved Questions**:
- **Authentication**: Trusted network, database auth via config.env/bindings
- **Caching**: Stub out initially, implement later
- **Async Processing**: Yes, with job status tracking for ML operations
- **Error Handling**: Simple error messages, detailed logging  
- **Multi-DB**: Named instances with instance-aware API endpoints
- **Query Filtering**: Support `where`, `limit`, `orderBy` parameters

**Database Schema**: Uses existing Insurance MegaCorp tables:
- `safe_driver_scores` - Driver safety scores and risk categories
- `driver_ml_training_data` - ML training dataset  
- `driver_accident_model` - MADlib model metadata
- `vehicle_events` - Vehicle telemetry data

## 9. Standards & Best Practices

**Code Standards**:
- Google Java Style Guide
- Constructor injection only
- Comprehensive unit and integration tests
- Proper transaction management for ML operations

**API Standards**:
- RESTful design with standard HTTP verbs
- Consistent JSON response format
- Input validation and SQL injection protection
- Meaningful error messages and proper HTTP status codes

**Security**:  
- No credentials in repository
- Environment-based configuration
- Input sanitization for query parameters
- Database connection encryption