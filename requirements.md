# IMC Database Server Requirements

## Overview
A dedicated Spring Boot service to handle all Greenplum database interactions for the Insurance MegaCorp ecosystem. This service will provide REST APIs for Safe Driver Scoring, ML model management, and vehicle events data.

## Architecture
- **Service Name**: `imc-db-server`
- **Technology Stack**: Spring Boot 3.5+, Java 21, PostgreSQL Driver, MADlib integration
- **Database**: Greenplum with MADlib extensions
- **Port**: 8084 (to avoid conflicts with existing services)

## Database Schema Dependencies
- `safe_driver_scores` table
- `driver_ml_training_data` table  
- `driver_accident_model` table
- `vehicle_events` table (for future accident-mcp-server)

## Required API Endpoints

### 1. Health & Configuration
```http
GET /api/health
GET /api/database/info
GET /api/database/connection-test
```

**Purpose**: Service health, database connectivity status, and configuration info.

### 2. Fleet Safety Summary
```http
GET /api/fleet/summary
```

**Response**:
```json
{
  "total_drivers": 1250,
  "average_safety_score": 78.5,
  "high_risk_count": 125,
  "accidents_this_month": 8,
  "improvement_trend": 2.3
}
```

**SQL Query**:
```sql
SELECT 
    COUNT(*) as total_drivers,
    ROUND(AVG(score)::numeric, 1) as average_safety_score,
    COUNT(*) FILTER (WHERE risk_category IN ('HIGH_RISK', 'POOR')) as high_risk_count,
    COUNT(*) FILTER (WHERE accidents > 0) as drivers_with_accidents
FROM safe_driver_scores s
JOIN driver_ml_training_data d ON s.driver_id = d.driver_id;
```

### 3. Active Drivers Count
```http
GET /api/drivers/active-count
```

**Response**: `{ "active_drivers": 1180 }`

### 4. High Risk Drivers Count  
```http
GET /api/drivers/high-risk-count
```

**Response**: `{ "high_risk_drivers": 125 }`

### 5. Telemetry Events Count
```http
GET /api/telemetry/events-count
```

**Response**: `{ "total_events": 45230 }`

**SQL Query**: `SELECT COUNT(*) FROM vehicle_events WHERE event_date >= CURRENT_DATE - INTERVAL '30 days'`

### 6. Top Performing Drivers
```http
GET /api/drivers/top-performers
GET /api/drivers/top-performers?limit=10
```

**Response**:
```json
[
  {
    "driver_id": 100001,
    "safety_score": 94.7,
    "risk_category": "EXCELLENT",
    "speed_compliance": 98.5,
    "harsh_events": 0,
    "phone_usage": 2.1,
    "accidents": 0,
    "total_events": 1250,
    "calculation_date": 1724284800000
  }
]
```

**SQL Query**:
```sql
SELECT 
    s.driver_id,
    s.score as safety_score,
    s.risk_category,
    d.speed_compliance,
    d.harsh_events,
    d.phone_usage,
    d.accidents,
    d.total_events,
    s.calculation_date
FROM safe_driver_scores s
JOIN driver_ml_training_data d ON s.driver_id = d.driver_id
WHERE s.risk_category IN ('EXCELLENT', 'GOOD')
ORDER BY s.score DESC
LIMIT ?;
```

### 7. High Risk Drivers
```http
GET /api/drivers/high-risk
GET /api/drivers/high-risk?limit=10
```

**Response**: Same structure as top performers, filtered for high-risk categories.

**SQL Query**:
```sql
SELECT 
    s.driver_id,
    s.score as safety_score,
    s.risk_category,
    d.speed_compliance,
    d.harsh_events,
    d.phone_usage,
    d.accidents,
    d.total_events,
    s.calculation_date
FROM safe_driver_scores s
JOIN driver_ml_training_data d ON s.driver_id = d.driver_id
WHERE s.risk_category IN ('HIGH_RISK', 'POOR')
ORDER BY s.score ASC
LIMIT ?;
```

### 8. ML Model Information
```http
GET /api/ml/model-info
```

**Response**:
```json
{
  "model_id": "safe_driver_v1",
  "algorithm": "logistic_regression", 
  "accuracy": 0.943,
  "num_iterations": 12,
  "num_rows_processed": 15000,
  "feature_weights": {
    "speed_compliance": 0.402,
    "avg_gforce": 0.248,
    "harsh_events": 0.153,
    "phone_usage": 0.149,
    "speed_variance": 0.048
  },
  "last_trained": "2024-08-22",
  "status": "active"
}
```

**SQL Query**:
```sql
SELECT 
    model_id,
    algorithm,
    accuracy,
    num_iterations,
    num_rows_processed,
    feature_weights,
    created_date
FROM driver_accident_model
ORDER BY created_date DESC
LIMIT 1;
```

### 9. ML Model Recalculation
```http
POST /api/ml/recalculate
```

**Purpose**: Execute the full MADlib ML pipeline to retrain the model.

**Response**:
```json
{
  "status": "success",
  "message": "Safe driver scores recalculated successfully",
  "updated_drivers": 1250,
  "executed_statements": 7,
  "execution_time_ms": 2840,
  "timestamp": 1724284800000
}
```

**Implementation**: Execute the 7-step MADlib SQL script with proper transaction management.

### 10. Database Statistics
```http
GET /api/database/stats
```

**Response**:
```json
{
  "database_name": "insurance_megacorp",
  "total_tables": 12,
  "total_size_mb": 2048,
  "safe_driver_scores_count": 1250,
  "vehicle_events_count": 450000,
  "last_backup": "2024-08-21T10:30:00Z"
}
```

## Future Vehicle Events APIs (for accident-mcp-server)

### 11. Vehicle Events Query
```http
GET /api/vehicle-events?driver_id=123&date_from=2024-08-01&date_to=2024-08-22
GET /api/vehicle-events/accidents?severity=high
GET /api/vehicle-events/crashes?limit=50
```

### 12. Real-Time Event Ingestion
```http
POST /api/vehicle-events/batch
PUT /api/vehicle-events/{event_id}
```

## Configuration Requirements

### Database Connection
- Support for `GREENPLUM_HOST`, `GREENPLUM_PORT`, `GREENPLUM_DATABASE`
- Connection pooling (HikariCP)
- Health check endpoints for monitoring
- Retry logic and circuit breaker patterns

### Security
- Input validation and SQL injection protection
- Rate limiting on expensive queries
- Database connection encryption

### Performance
- Query result caching for frequently accessed data
- Async processing for long-running ML operations
- Database connection pooling
- Query optimization for large datasets

### Monitoring
- Detailed logging of database operations
- Performance metrics (query execution times)
- Error tracking and alerting

## Development Phases

### Phase 1: Core Infrastructure
- Database connectivity and health checks
- Basic CRUD operations for safe_driver_scores
- Fleet summary and driver count APIs

### Phase 2: ML Integration
- ML model information retrieval
- MADlib recalculation pipeline
- Feature importance and model metrics

### Phase 3: Advanced Queries
- Complex driver analytics
- Performance optimization
- Caching strategies

### Phase 4: Vehicle Events Support
- Vehicle events table integration
- Real-time event processing APIs
- Support for future accident-mcp-server

## Questions for Clarification

1. **Authentication**: Do we need API authentication (JWT, API keys) or will this run in a trusted network?

2. **Caching Strategy**: Should we implement Redis caching for frequently accessed data like fleet summaries?

3. **Async Processing**: For the ML recalculation (which can take several seconds), do you want async processing with job status tracking?

4. **Error Handling**: What level of error detail should be exposed in API responses?

5. **Data Refresh**: How often should cached data be refreshed? Should we support webhook notifications when data changes?

6. **Vehicle Events Schema**: Can you provide the schema for the `vehicle_events` table for the future accident-mcp-server integration?

Does this capture all the requirements? Should I proceed with creating the initial Spring Boot project structure?