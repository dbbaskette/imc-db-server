# Insurance MegaCorp Database Server API Documentation

## OpenAPI 3.0 Specification

```yaml
openapi: 3.0.3
info:
  title: Insurance MegaCorp Database Server API
  description: |
    API service for Insurance MegaCorp's database server providing access to fleet management,
    vehicle events, machine learning operations, and health monitoring capabilities.
  version: 3.0.0
  contact:
    name: Insurance MegaCorp Development Team
    email: dev@insurancemegacorp.com
servers:
  - url: http://localhost:8080
    description: Local development server
  - url: https://api.insurancemegacorp.com
    description: Production server

paths:
  /api/{instance}/health:
    get:
      tags:
        - Health
      summary: Get service health status
      description: Returns health status and database connection information for the specified instance
      parameters:
        - name: instance
          in: path
          required: true
          description: Database instance identifier
          schema:
            type: string
            example: "prod"
      responses:
        '200':
          description: Health check successful
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/HealthResponse'
        '404':
          description: Database instance not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /api/{instance}/database/info:
    get:
      tags:
        - Health
      summary: Get database information
      description: Returns detailed database connection and instance information
      parameters:
        - name: instance
          in: path
          required: true
          description: Database instance identifier
          schema:
            type: string
            example: "prod"
      responses:
        '200':
          description: Database info retrieved successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/DatabaseInfoResponse'
        '404':
          description: Database instance not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /api/{instance}/fleet/summary:
    get:
      tags:
        - Fleet Management
      summary: Get fleet summary statistics
      description: Returns comprehensive fleet statistics including driver counts, safety scores, and trends
      parameters:
        - name: instance
          in: path
          required: true
          description: Database instance identifier
          schema:
            type: string
            example: "prod"
      responses:
        '200':
          description: Fleet summary retrieved successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/FleetSummaryResponse'
        '404':
          description: Database instance not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /api/{instance}/drivers/active-count:
    get:
      tags:
        - Fleet Management
      summary: Get active drivers count
      description: Returns the number of currently active drivers in the fleet
      parameters:
        - name: instance
          in: path
          required: true
          description: Database instance identifier
          schema:
            type: string
            example: "prod"
      responses:
        '200':
          description: Active drivers count retrieved successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ActiveDriversCountResponse'
        '404':
          description: Database instance not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /api/{instance}/drivers/high-risk-count:
    get:
      tags:
        - Fleet Management
      summary: Get high-risk drivers count
      description: Returns the number of drivers classified as high-risk
      parameters:
        - name: instance
          in: path
          required: true
          description: Database instance identifier
          schema:
            type: string
            example: "prod"
      responses:
        '200':
          description: High-risk drivers count retrieved successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/HighRiskDriversCountResponse'
        '404':
          description: Database instance not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /api/{instance}/drivers/top-performers:
    get:
      tags:
        - Fleet Management
      summary: Get top performing drivers
      description: Returns a list of top performing drivers based on safety scores
      parameters:
        - name: instance
          in: path
          required: true
          description: Database instance identifier
          schema:
            type: string
            example: "prod"
        - name: limit
          in: query
          required: false
          description: Maximum number of drivers to return
          schema:
            type: integer
            default: 10
            minimum: 1
            maximum: 100
      responses:
        '200':
          description: Top performers retrieved successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/DriverPerformanceListResponse'
        '404':
          description: Database instance not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /api/{instance}/drivers/high-risk:
    get:
      tags:
        - Fleet Management
      summary: Get high-risk drivers
      description: Returns a list of drivers classified as high-risk
      parameters:
        - name: instance
          in: path
          required: true
          description: Database instance identifier
          schema:
            type: string
            example: "prod"
        - name: limit
          in: query
          required: false
          description: Maximum number of drivers to return
          schema:
            type: integer
            default: 10
            minimum: 1
            maximum: 100
      responses:
        '200':
          description: High-risk drivers retrieved successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/DriverPerformanceListResponse'
        '404':
          description: Database instance not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /api/{instance}/vehicle-events:
    get:
      tags:
        - Vehicle Events
      summary: Get vehicle events with filters
      description: Retrieve vehicle events with various filtering options
      parameters:
        - name: instance
          in: path
          required: true
          description: Database instance identifier
          schema:
            type: string
            example: "prod"
        - name: driver_id
          in: query
          required: false
          description: Filter by driver ID
          schema:
            type: string
        - name: vehicle_id
          in: query
          required: false
          description: Filter by vehicle ID
          schema:
            type: string
        - name: event_type
          in: query
          required: false
          description: Filter by event type
          schema:
            type: string
            enum: [telematics_event, acceleration, braking, cornering, speed_violation, harsh_driving, phone_usage, weather_event, CRASH, crash]
        - name: severity
          in: query
          required: false
          description: Filter by severity level
          schema:
            type: string
            enum: [LOW, MEDIUM, HIGH, CRITICAL, unknown]
        - name: date_from
          in: query
          required: false
          description: Start date filter (ISO 8601 format)
          schema:
            type: string
            format: date-time
        - name: date_to
          in: query
          required: false
          description: End date filter (ISO 8601 format)
          schema:
            type: string
            format: date-time
        - name: limit
          in: query
          required: false
          description: Maximum number of events to return
          schema:
            type: integer
            minimum: 1
            maximum: 1000
        - name: offset
          in: query
          required: false
          description: Number of events to skip
          schema:
            type: integer
            minimum: 0
        - name: order_by
          in: query
          required: false
          description: Field to order by
          schema:
            type: string
      responses:
        '200':
          description: Vehicle events retrieved successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/VehicleEventsPageResponse'
        '400':
          description: Invalid parameters provided
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
        '404':
          description: Database instance not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /api/{instance}/vehicle-events/batch:
    post:
      tags:
        - Vehicle Events
      summary: Batch insert vehicle events
      description: Insert multiple vehicle events in a single operation
      parameters:
        - name: instance
          in: path
          required: true
          description: Database instance identifier
          schema:
            type: string
            example: "prod"
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: array
              items:
                $ref: '#/components/schemas/VehicleEventDto'
      responses:
        '200':
          description: Events inserted successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/BatchInsertResponse'
        '400':
          description: Invalid request body
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
        '404':
          description: Database instance not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /api/{instance}/vehicle-events/high-gforce:
    get:
      tags:
        - Vehicle Events
      summary: Get high G-force events
      description: Retrieve vehicle events with high G-force measurements
      parameters:
        - name: instance
          in: path
          required: true
          description: Database instance identifier
          schema:
            type: string
            example: "prod"
        - name: limit
          in: query
          required: false
          description: Maximum number of events to return
          schema:
            type: integer
            default: 50
            minimum: 1
            maximum: 1000
        - name: offset
          in: query
          required: false
          description: Number of events to skip
          schema:
            type: integer
            minimum: 0
        - name: order_by
          in: query
          required: false
          description: Field to order by
          schema:
            type: string
      responses:
        '200':
          description: High G-force events retrieved successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/VehicleEventsPageResponse'
        '404':
          description: Database instance not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /api/{instance}/telemetry/events-count:
    get:
      tags:
        - Telemetry
      summary: Get telemetry events count
      description: Returns the total count of telemetry events, optionally filtered by date
      parameters:
        - name: instance
          in: path
          required: true
          description: Database instance identifier
          schema:
            type: string
            example: "prod"
        - name: date_from
          in: query
          required: false
          description: Start date filter (ISO 8601 format)
          schema:
            type: string
            format: date-time
      responses:
        '200':
          description: Telemetry events count retrieved successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/TelemetryEventsCountResponse'
        '404':
          description: Database instance not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /api/{instance}/telemetry/table-counts:
    get:
      tags:
        - Telemetry
      summary: Get telemetry table counts
      description: Returns count of records in various telemetry tables
      parameters:
        - name: instance
          in: path
          required: true
          description: Database instance identifier
          schema:
            type: string
            example: "prod"
      responses:
        '200':
          description: Telemetry table counts retrieved successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/TelemetryTableCountsResponse'
        '404':
          description: Database instance not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /api/{instance}/database/stats:
    get:
      tags:
        - Database
      summary: Get database statistics
      description: Returns comprehensive database statistics and metrics
      parameters:
        - name: instance
          in: path
          required: true
          description: Database instance identifier
          schema:
            type: string
            example: "prod"
      responses:
        '200':
          description: Database statistics retrieved successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/DatabaseStatsResponse'
        '404':
          description: Database instance not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /api/{instance}/ml/model-info:
    get:
      tags:
        - Machine Learning
      summary: Get ML model information
      description: Returns information about the current machine learning model
      parameters:
        - name: instance
          in: path
          required: true
          description: Database instance identifier
          schema:
            type: string
            example: "prod"
      responses:
        '200':
          description: ML model info retrieved successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/MlModelInfoResponse'
        '404':
          description: Model not found or database instance not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /api/{instance}/ml/recalculate:
    post:
      tags:
        - Machine Learning
      summary: Start ML recalculation job
      description: Initiates a machine learning model recalculation process
      parameters:
        - name: instance
          in: path
          required: true
          description: Database instance identifier
          schema:
            type: string
            example: "prod"
      responses:
        '202':
          description: ML recalculation job started successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/JobStartResponse'
        '404':
          description: Database instance not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /api/{instance}/ml/job-status/{jobId}:
    get:
      tags:
        - Machine Learning
      summary: Get ML job status
      description: Returns the status and progress of a machine learning job
      parameters:
        - name: instance
          in: path
          required: true
          description: Database instance identifier
          schema:
            type: string
            example: "prod"
        - name: jobId
          in: path
          required: true
          description: Job identifier
          schema:
            type: string
            example: "ml-job-12345"
      responses:
        '200':
          description: Job status retrieved successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/JobStatusResponse'
        '404':
          description: Job not found or database instance not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

    delete:
      tags:
        - Machine Learning
      summary: Cancel ML job
      description: Cancels a running machine learning job
      parameters:
        - name: instance
          in: path
          required: true
          description: Database instance identifier
          schema:
            type: string
            example: "prod"
        - name: jobId
          in: path
          required: true
          description: Job identifier
          schema:
            type: string
            example: "ml-job-12345"
      responses:
        '200':
          description: Job cancellation processed
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/JobCancelResponse'
        '404':
          description: Job not found or database instance not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

components:
  schemas:
    ApiResponse:
      type: object
      required:
        - success
        - timestamp
      properties:
        success:
          type: boolean
          description: Indicates if the request was successful
        data:
          type: object
          description: Response data (present on success)
        error:
          type: string
          description: Error message (present on failure)
        timestamp:
          type: integer
          format: int64
          description: Unix timestamp of the response
        executionTimeMs:
          type: integer
          format: int64
          description: Execution time in milliseconds

    HealthResponse:
      allOf:
        - $ref: '#/components/schemas/ApiResponse'
        - type: object
          properties:
            data:
              type: object
              properties:
                instance:
                  type: string
                  example: "prod"
                status:
                  type: string
                  example: "UP"
                database_connected:
                  type: boolean
                  example: true

    DatabaseInfoResponse:
      allOf:
        - $ref: '#/components/schemas/ApiResponse'
        - type: object
          properties:
            data:
              type: object
              properties:
                instance:
                  type: string
                  example: "prod"
                available_instances:
                  type: array
                  items:
                    type: string
                  example: ["prod", "dev", "test"]
                connection_test:
                  type: boolean
                  example: true

    FleetSummaryDto:
      type: object
      properties:
        totalDrivers:
          type: integer
          format: int64
          example: 1250
        averageSafetyScore:
          type: number
          format: decimal
          example: 85.5
        highRiskCount:
          type: integer
          format: int64
          example: 45
        accidentsThisMonth:
          type: integer
          format: int64
          example: 8
        improvementTrend:
          type: number
          format: decimal
          example: 2.3

    FleetSummaryResponse:
      allOf:
        - $ref: '#/components/schemas/ApiResponse'
        - type: object
          properties:
            data:
              $ref: '#/components/schemas/FleetSummaryDto'

    ActiveDriversCountResponse:
      allOf:
        - $ref: '#/components/schemas/ApiResponse'
        - type: object
          properties:
            data:
              type: object
              properties:
                active_drivers:
                  type: integer
                  format: int64
                  example: 950

    HighRiskDriversCountResponse:
      allOf:
        - $ref: '#/components/schemas/ApiResponse'
        - type: object
          properties:
            data:
              type: object
              properties:
                high_risk_drivers:
                  type: integer
                  format: int64
                  example: 45

    DriverPerformanceDto:
      type: object
      properties:
        driverId:
          type: integer
          format: int64
          example: 12345
        safetyScore:
          type: number
          format: decimal
          example: 92.5
        riskCategory:
          type: string
          example: "LOW"
          enum: [LOW, MEDIUM, HIGH, CRITICAL]
        speedCompliance:
          type: number
          format: decimal
          example: 95.2
        harshEvents:
          type: integer
          example: 3
        phoneUsage:
          type: number
          format: decimal
          example: 2.1
        accidents:
          type: integer
          example: 0
        totalEvents:
          type: integer
          example: 127
        calculationDate:
          type: string
          format: date-time
          example: "2024-09-24T10:30:00"

    DriverPerformanceListResponse:
      allOf:
        - $ref: '#/components/schemas/ApiResponse'
        - type: object
          properties:
            data:
              type: array
              items:
                $ref: '#/components/schemas/DriverPerformanceDto'

    VehicleEventDto:
      type: object
      properties:
        eventId:
          type: integer
          format: int64
          example: 67890
        driverId:
          type: integer
          format: int64
          example: 12345
        vehicleId:
          type: string
          example: "VH001234"
        eventType:
          type: string
          example: "harsh_braking"
          enum: [telematics_event, acceleration, braking, cornering, speed_violation, harsh_driving, phone_usage, weather_event, CRASH, crash]
        eventDate:
          type: string
          format: date-time
          example: "2024-09-24T14:30:00"
        latitude:
          type: number
          format: decimal
          example: 40.7128
        longitude:
          type: number
          format: decimal
          example: -74.0060
        speedMph:
          type: number
          format: decimal
          example: 45.2
        gforce:
          type: number
          format: decimal
          example: 0.8
        severity:
          type: string
          example: "MEDIUM"
          enum: [LOW, MEDIUM, HIGH, CRITICAL, unknown]
        phoneUsage:
          type: boolean
          example: false
        weatherConditions:
          type: string
          example: "clear"

    PageInfo:
      type: object
      properties:
        totalElements:
          type: integer
          format: int64
          example: 1500
        totalPages:
          type: integer
          example: 150
        size:
          type: integer
          example: 10
        number:
          type: integer
          example: 0
        numberOfElements:
          type: integer
          example: 10
        first:
          type: boolean
          example: true
        last:
          type: boolean
          example: false

    VehicleEventsPage:
      type: object
      properties:
        content:
          type: array
          items:
            $ref: '#/components/schemas/VehicleEventDto'
        pageable:
          $ref: '#/components/schemas/PageInfo'

    VehicleEventsPageResponse:
      allOf:
        - $ref: '#/components/schemas/ApiResponse'
        - type: object
          properties:
            data:
              $ref: '#/components/schemas/VehicleEventsPage'

    BatchInsertResponse:
      allOf:
        - $ref: '#/components/schemas/ApiResponse'
        - type: object
          properties:
            data:
              type: object
              properties:
                inserted_count:
                  type: integer
                  example: 25
                status:
                  type: string
                  example: "success"
                message:
                  type: string
                  example: "Events inserted successfully"

    TelemetryEventsCountResponse:
      allOf:
        - $ref: '#/components/schemas/ApiResponse'
        - type: object
          properties:
            data:
              type: object
              properties:
                total_events:
                  type: integer
                  format: int64
                  example: 125000

    TelemetryTableCountsResponse:
      allOf:
        - $ref: '#/components/schemas/ApiResponse'
        - type: object
          properties:
            data:
              type: object
              additionalProperties:
                type: integer
                format: int64
              example:
                vehicle_events: 125000
                driver_performance: 1250
                ml_results: 850

    DatabaseStatsResponse:
      allOf:
        - $ref: '#/components/schemas/ApiResponse'
        - type: object
          properties:
            data:
              type: object
              properties:
                database_name:
                  type: string
                  example: "insurance_megacorp"
                instance:
                  type: string
                  example: "prod"
                last_updated:
                  type: integer
                  format: int64
                  example: 1695557400000
              additionalProperties:
                type: object
                description: Additional database statistics

    MlModelInfoDto:
      type: object
      properties:
        modelId:
          type: string
          example: "risk-model-v2.1"
        algorithm:
          type: string
          example: "RandomForest"
        accuracy:
          type: number
          format: decimal
          example: 0.892
        numIterations:
          type: integer
          example: 100
        numRowsProcessed:
          type: integer
          example: 50000
        featureWeights:
          type: object
          additionalProperties:
            type: number
            format: decimal
          example:
            speed_violations: 0.23
            harsh_braking: 0.18
            phone_usage: 0.15
        lastTrained:
          type: string
          format: date-time
          example: "2024-09-20T08:00:00"
        status:
          type: string
          example: "ACTIVE"

    MlModelInfoResponse:
      allOf:
        - $ref: '#/components/schemas/ApiResponse'
        - type: object
          properties:
            data:
              $ref: '#/components/schemas/MlModelInfoDto'

    JobStartResponse:
      allOf:
        - $ref: '#/components/schemas/ApiResponse'
        - type: object
          properties:
            data:
              type: object
              properties:
                jobId:
                  type: string
                  example: "ml-job-12345"
                status:
                  type: string
                  example: "started"
                message:
                  type: string
                  example: "ML recalculation started successfully"

    JobStatusDto:
      type: object
      properties:
        jobId:
          type: string
          example: "ml-job-12345"
        status:
          type: string
          example: "RUNNING"
          enum: [PENDING, RUNNING, COMPLETED, FAILED, CANCELLED]
        message:
          type: string
          example: "Processing driver risk calculations..."
        progress:
          type: integer
          minimum: 0
          maximum: 100
          example: 65
        startTime:
          type: integer
          format: int64
          example: 1695557400000
        endTime:
          type: integer
          format: int64
          example: null
        result:
          type: object
          description: Job result data (present when completed)

    JobStatusResponse:
      allOf:
        - $ref: '#/components/schemas/ApiResponse'
        - type: object
          properties:
            data:
              $ref: '#/components/schemas/JobStatusDto'

    JobCancelResponse:
      allOf:
        - $ref: '#/components/schemas/ApiResponse'
        - type: object
          properties:
            data:
              type: object
              properties:
                status:
                  type: string
                  example: "cancelled"
                  enum: [cancelled, not_found_or_completed]
                message:
                  type: string
                  example: "Job cancelled successfully"

    ErrorResponse:
      allOf:
        - $ref: '#/components/schemas/ApiResponse'
        - type: object
          properties:
            success:
              type: boolean
              example: false
            error:
              type: string
              example: "Database instance 'invalid' not found"

  securitySchemes:
    ApiKeyAuth:
      type: apiKey
      in: header
      name: X-API-Key
    BearerAuth:
      type: http
      scheme: bearer

security:
  - ApiKeyAuth: []
  - BearerAuth: []

tags:
  - name: Health
    description: Service health and status endpoints
  - name: Fleet Management
    description: Fleet and driver management operations
  - name: Vehicle Events
    description: Vehicle telemetry and event data
  - name: Telemetry
    description: Telemetry data and statistics
  - name: Database
    description: Database operations and statistics
  - name: Machine Learning
    description: ML model operations and job management
```

## API Usage Examples

### Authentication
All API endpoints require authentication using either API Key or Bearer token:

```bash
# Using API Key
curl -H "X-API-Key: your-api-key" "http://localhost:8080/api/prod/health"

# Using Bearer token
curl -H "Authorization: Bearer your-jwt-token" "http://localhost:8080/api/prod/health"
```

### Common Request Examples

#### Get Health Status
```bash
curl -X GET "http://localhost:8080/api/prod/health" \
  -H "X-API-Key: your-api-key"
```

#### Get Fleet Summary
```bash
curl -X GET "http://localhost:8080/api/prod/fleet/summary" \
  -H "X-API-Key: your-api-key"
```

#### Get Vehicle Events with Filters
```bash
curl -X GET "http://localhost:8080/api/prod/vehicle-events?event_type=harsh_braking&severity=HIGH&limit=50" \
  -H "X-API-Key: your-api-key"
```

#### Batch Insert Vehicle Events
```bash
curl -X POST "http://localhost:8080/api/prod/vehicle-events/batch" \
  -H "Content-Type: application/json" \
  -H "X-API-Key: your-api-key" \
  -d '[
    {
      "driverId": 12345,
      "vehicleId": "VH001234",
      "eventType": "harsh_braking",
      "eventDate": "2024-09-24T14:30:00",
      "latitude": 40.7128,
      "longitude": -74.0060,
      "speedMph": 45.2,
      "gforce": 0.8,
      "severity": "MEDIUM",
      "phoneUsage": false,
      "weatherConditions": "clear"
    }
  ]'
```

#### Start ML Recalculation
```bash
curl -X POST "http://localhost:8080/api/prod/ml/recalculate" \
  -H "X-API-Key: your-api-key"
```

#### Check Job Status
```bash
curl -X GET "http://localhost:8080/api/prod/ml/job-status/ml-job-12345" \
  -H "X-API-Key: your-api-key"
```

## Response Format

All API responses follow a consistent format:

### Success Response
```json
{
  "success": true,
  "data": {
    // Response data here
  },
  "timestamp": 1695557400000,
  "executionTimeMs": 25
}
```

### Error Response
```json
{
  "success": false,
  "error": "Error message describing what went wrong",
  "timestamp": 1695557400000,
  "executionTimeMs": 12
}
```

## Error Codes

| HTTP Status | Description |
|------------|-------------|
| 200 | Success |
| 202 | Accepted (for async operations) |
| 400 | Bad Request - Invalid parameters |
| 404 | Not Found - Resource or instance not found |
| 500 | Internal Server Error |

## Rate Limiting

The API implements rate limiting to ensure fair usage:
- 1000 requests per minute per API key
- Burst capacity: 100 requests
- Rate limit headers are included in responses

## Data Types and Constraints

### Instance Parameter
The `{instance}` path parameter accepts database instance identifiers such as:
- `prod` - Production database
- `dev` - Development database
- `test` - Testing database

### Event Types
Valid event types for vehicle events:
- `telematics_event`
- `acceleration`
- `braking`
- `cornering`
- `speed_violation`
- `harsh_driving`
- `phone_usage`
- `weather_event`
- `CRASH` / `crash`

### Severity Levels
Valid severity levels:
- `LOW`
- `MEDIUM`
- `HIGH`
- `CRITICAL`
- `unknown`

### Date Formats
All dates should be in ISO 8601 format: `YYYY-MM-DDTHH:mm:ss`

Example: `2024-09-24T14:30:00`