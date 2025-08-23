<div align="center">
  <img src="assets/logo.png" alt="IMC Database Server Logo" width="600">
</div>

<div align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen?style=for-the-badge&logo=spring-boot" alt="Spring Boot 3.5.4">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java" alt="Java 21">
  <img src="https://img.shields.io/badge/PostgreSQL-12.22-blue?style=for-the-badge&logo=postgresql" alt="PostgreSQL 12.22">
  <img src="https://img.shields.io/badge/Cloud%20Foundry-Platform-lightblue?style=for-the-badge&logo=cloudfoundry" alt="Cloud Foundry">
  <img src="https://img.shields.io/badge/Service%20Registry-Eureka%20Client-green?style=for-the-badge&logo=spring" alt="Service Registry">
</div>

<div align="center">
  <h1>🏢 Insurance MegaCorp Database Server</h1>
  <p><strong>Enterprise-grade Spring Boot service providing REST APIs for fleet management, driver analytics, and ML model operations</strong></p>
</div>

---

## 📊 **Current Status**

| Metric | Value | Status |
|--------|-------|--------|
| **Test Success Rate** | 30/30 | 🎉 **100%** |
| **API Endpoints** | 20+ | ✅ **All Operational** |
| **Service Registry** | Eureka Client | ✅ **Integrated** |
| **Database Connection** | PostgreSQL 12.22 | ✅ **Connected** |
| **Deployment** | Cloud Foundry | ✅ **Deployed** |

---

## 🎯 **Mission Statement**

The IMC Database Server provides a robust, scalable REST API layer for Insurance MegaCorp's telematics and fleet management systems. We enable real-time access to driver performance data, vehicle events, and machine learning models while maintaining enterprise-grade security and performance standards.

---

## 🚀 **Available API Endpoints**

All endpoints follow the pattern `/api/{instance}/...` where `{instance}` is the database instance (e.g., `db01`).

### **🔍 Health & Monitoring**

#### **GET** `/api/{instance}/health`
**Description**: Check database instance health and connectivity  
**Parameters**: `{instance}` - Database instance name (e.g., `db01`)  
**Response**: Database health status and connection information

<details>
<summary>📋 Sample Response</summary>

```json
{
  "success": true,
  "data": {
    "instance": "db01",
    "status": "UP",
    "database_connected": true
  },
  "timestamp": 1755891600000,
  "executionTimeMs": 45
}
```
</details>

#### **GET** `/api/{instance}/database/info`
**Description**: Get database instance information and available instances  
**Parameters**: `{instance}` - Database instance name  
**Response**: Database configuration and available instances

<details>
<summary>📋 Sample Response</summary>

```json
{
  "success": true,
  "data": {
    "instance": "db01",
    "available_instances": ["db01"],
    "connection_test": true
  },
  "timestamp": 1755891600000,
  "executionTimeMs": 32
}
```
</details>

### **🚛 Fleet Management**

#### **GET** `/api/{instance}/fleet/summary`
**Description**: Get comprehensive fleet overview and statistics  
**Parameters**: `{instance}` - Database instance name  
**Response**: Fleet summary with driver counts, safety scores, and trends

<details>
<summary>📋 Sample Response</summary>

```json
{
  "success": true,
  "data": {
    "totalDrivers": 1250,
    "averageSafetyScore": 87.5,
    "highRiskCount": 45,
    "accidentsThisMonth": 3,
    "improvementTrend": 2.3
  },
  "timestamp": 1755891600000,
  "executionTimeMs": 156
}
```
</details>

#### **GET** `/api/{instance}/drivers/active-count`
**Description**: Get count of currently active drivers  
**Parameters**: `{instance}` - Database instance name  
**Response**: Active driver count

<details>
<summary>📋 Sample Response</summary>

```json
{
  "success": true,
  "data": {
    "active_drivers": 1187
  },
  "timestamp": 1755891600000,
  "executionTimeMs": 28
}
```
</details>

#### **GET** `/api/{instance}/drivers/high-risk-count`
**Description**: Get count of high-risk drivers  
**Parameters**: `{instance}` - Database instance name  
**Response**: High-risk driver count

<details>
<summary>📋 Sample Response</summary>

```json
{
  "success": true,
  "data": {
    "high_risk_drivers": 45
  },
  "timestamp": 1755891600000,
  "executionTimeMs": 31
}
```
</details>

#### **GET** `/api/{instance}/drivers/top-performers`
**Description**: Get list of top-performing drivers  
**Parameters**: 
- `{instance}` - Database instance name
- `limit` (optional) - Number of drivers to return (default: 10)  
**Response**: List of top-performing drivers with safety metrics

<details>
<summary>📋 Sample Response</summary>

```json
{
  "success": true,
  "data": [
    {
      "driverId": 1001,
      "safetyScore": 95.8,
      "riskCategory": "LOW",
      "speedCompliance": 98.2,
      "harshEvents": 2,
      "phoneUsage": 0.5,
      "accidents": 0,
      "totalEvents": 156,
      "calculationDate": "2025-01-22T10:30:00"
    }
  ],
  "timestamp": 1755891600000,
  "executionTimeMs": 89
}
```
</details>

#### **GET** `/api/{instance}/drivers/high-risk`
**Description**: Get list of high-risk drivers  
**Parameters**: 
- `{instance}` - Database instance name
- `limit` (optional) - Number of drivers to return (default: 10)  
**Response**: List of high-risk drivers requiring attention

<details>
<summary>📋 Sample Response</summary>

```json
{
  "success": true,
  "data": [
    {
      "driverId": 2047,
      "safetyScore": 62.3,
      "riskCategory": "HIGH",
      "speedCompliance": 78.9,
      "harshEvents": 15,
      "phoneUsage": 12.4,
      "accidents": 2,
      "totalEvents": 89,
      "calculationDate": "2025-01-22T10:30:00"
    }
  ],
  "timestamp": 1755891600000,
  "executionTimeMs": 67
}
```
</details>

### **🚗 Vehicle Events & Telemetry**

#### **GET** `/api/{instance}/vehicle-events`
**Description**: Get vehicle events with comprehensive filtering  
**Parameters**: 
- `{instance}` - Database instance name
- `driver_id` (optional) - Filter by driver ID
- `vehicle_id` (optional) - Filter by vehicle ID
- `event_type` (optional) - Filter by event type
- `severity` (optional) - Filter by severity level
- `date_from` (optional) - Start date filter
- `date_to` (optional) - End date filter
- `limit` (optional) - Number of events to return
- `offset` (optional) - Pagination offset
- `order_by` (optional) - Sort order  
**Response**: Paginated list of vehicle events

<details>
<summary>📋 Sample Response</summary>

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "eventId": 1001,
        "driverId": 1001,
        "vehicleId": "VH001",
        "eventType": "harsh_driving",
        "eventDate": "2025-01-22T10:30:00",
        "latitude": 40.7128,
        "longitude": -74.0060,
        "speedMph": 65.0,
        "gforce": 0.8,
        "severity": "MEDIUM",
        "phoneUsage": false,
        "weatherConditions": "CLEAR"
      }
    ],
    "totalElements": 1250,
    "totalPages": 13,
    "size": 100,
    "number": 0
  },
  "timestamp": 1755891600000,
  "executionTimeMs": 234
}
```
</details>

#### **GET** `/api/{instance}/vehicle-events/high-gforce`
**Description**: Get high G-force events (harsh driving incidents)  
**Parameters**: 
- `{instance}` - Database instance name
- `limit` (optional) - Number of events to return (default: 50)
- `offset` (optional) - Pagination offset
- `order_by` (optional) - Sort order  
**Response**: Paginated list of high G-force events

<details>
<summary>📋 Sample Response</summary>

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "eventId": 2047,
        "driverId": 2047,
        "vehicleId": "VH089",
        "eventType": "harsh_driving",
        "eventDate": "2025-01-22T09:15:00",
        "latitude": 40.7589,
        "longitude": -73.9851,
        "speedMph": 72.0,
        "gforce": 1.2,
        "severity": "HIGH",
        "phoneUsage": true,
        "weatherConditions": "RAIN"
      }
    ],
    "totalElements": 89,
    "totalPages": 2,
    "size": 50,
    "number": 0
  },
  "timestamp": 1755891600000,
  "executionTimeMs": 156
}
```
</details>

#### **GET** `/api/{instance}/telemetry/events-count`
**Description**: Get total count of telemetry events  
**Parameters**: 
- `{instance}` - Database instance name
- `date_from` (optional) - Filter from specific date  
**Response**: Total event count

<details>
<summary>📋 Sample Response</summary>

```json
{
  "success": true,
  "data": {
    "total_events": 125000
  },
  "timestamp": 1755891600000,
  "executionTimeMs": 23
}
```
</details>

#### **GET** `/api/{instance}/database/stats`
**Description**: Get database statistics and metadata  
**Parameters**: `{instance}` - Database instance name  
**Response**: Database statistics and instance information

<details>
<summary>📋 Sample Response</summary>

```json
{
  "success": true,
  "data": {
    "total_events": 125000,
    "total_drivers": 1250,
    "total_vehicles": 890,
    "database_name": "insurance_megacorp",
    "instance": "db01",
    "last_updated": 1755891600000
  },
  "timestamp": 1755891600000,
  "executionTimeMs": 45
}
```
</details>

#### **POST** `/api/{instance}/vehicle-events/batch`
**Description**: Insert multiple vehicle events in batch  
**Parameters**: 
- `{instance}` - Database instance name
- `events` - Array of vehicle event objects in request body  
**Response**: Batch insertion results

<details>
<summary>📋 Sample Request</summary>

```json
[
  {
    "driverId": 1001,
    "vehicleId": "VH001",
    "eventType": "telematics_event",
    "eventDate": "2025-01-22T10:30:00",
    "latitude": 40.7128,
    "longitude": -74.0060,
    "speedMph": 45.0,
    "gforce": 0.3,
    "severity": "LOW",
    "phoneUsage": false,
    "weatherConditions": "CLEAR"
  }
]
```

**Sample Response:**
```json
{
  "success": true,
  "data": {
    "inserted_count": 1,
    "status": "success",
    "message": "Events inserted successfully"
  },
  "timestamp": 1755891600000,
  "executionTimeMs": 89
}
```
</details>

### **🤖 Machine Learning Operations**

#### **GET** `/api/{instance}/ml/model-info`
**Description**: Get current ML model information and performance metrics  
**Parameters**: `{instance}` - Database instance name  
**Response**: ML model details including accuracy and feature weights

<details>
<summary>📋 Sample Response</summary>

```json
{
  "success": true,
  "data": {
    "modelId": "safe_driver_v2.1",
    "algorithm": "Random Forest",
    "accuracy": 89.7,
    "numIterations": 1000,
    "numRowsProcessed": 50000,
    "featureWeights": {
      "speed_compliance": 0.25,
      "harsh_events": 0.30,
      "phone_usage": 0.20,
      "weather_conditions": 0.15,
      "time_of_day": 0.10
    },
    "lastTrained": "2025-01-20T14:30:00",
    "status": "ACTIVE"
  },
  "timestamp": 1755891600000,
  "executionTimeMs": 123
}
```
</details>

#### **POST** `/api/{instance}/ml/recalculate`
**Description**: Start ML model recalculation process  
**Parameters**: `{instance}` - Database instance name  
**Response**: Job ID and status for the recalculation process

<details>
<summary>📋 Sample Response</summary>

```json
{
  "success": true,
  "data": {
    "jobId": "ml_recalc_20250122_103000",
    "status": "started",
    "message": "ML recalculation started successfully"
  },
  "timestamp": 1755891600000,
  "executionTimeMs": 67
}
```
</details>

#### **GET** `/api/{instance}/ml/job-status/{jobId}`
**Description**: Get status of ML job by ID  
**Parameters**: 
- `{instance}` - Database instance name
- `{jobId}` - ML job identifier  
**Response**: Current job status and progress information

<details>
<summary>📋 Sample Response</summary>

```json
{
  "success": true,
  "data": {
    "jobId": "ml_recalc_20250122_103000",
    "status": "IN_PROGRESS",
    "message": "Training model",
    "progress": 75,
    "startTime": 1755891600000,
    "endTime": null,
    "result": null
  },
  "timestamp": 1755891600000,
  "executionTimeMs": 23
}
```
</details>

#### **DELETE** `/api/{instance}/ml/job-status/{jobId}`
**Description**: Cancel an ML job by ID  
**Parameters**: 
- `{instance}` - Database instance name
- `{jobId}` - ML job identifier  
**Response**: Job cancellation status

<details>
<summary>📋 Sample Response</summary>

```json
{
  "success": true,
  "data": {
    "status": "cancelled",
    "message": "Job cancelled successfully"
  },
  "timestamp": 1755891600000,
  "executionTimeMs": 34
}
```
</details>

---

## 📈 **Performance Metrics**

| Metric | Value | Status |
|--------|-------|--------|
| **Response Time** | < 300ms | 🟢 Excellent |
| **Database Connection** | PostgreSQL 12.22 | 🟢 Connected |
| **Memory Usage** | 1GB allocated | 🟢 Optimal |
| **Health Check** | /actuator/health | 🟢 Operational |
| **Service Registry** | imc-services bound | 🟢 Integrated |

---

## 🧪 **Testing & Quality**

| Test Category | Count | Status |
|---------------|-------|--------|
| **Health & Monitoring** | 3/3 | ✅ Passed |
| **Fleet Management** | 3/3 | ✅ Passed |
| **Driver Analytics** | 4/4 | ✅ Passed |
| **ML Pipeline** | 4/4 | ✅ Passed |
| **Vehicle Events** | 8/8 | ✅ Passed |
| **Edge Cases** | 4/4 | ✅ Passed |
| **Performance** | 4/4 | ✅ Passed |
| **Total** | **30/30** | **🎉 100%** |

---

## 🏗️ **Architecture Overview**

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Web Clients   │    │   Load Balancer  │    │  Service        │
│   (Dashboards)  │◄──►│   (Cloud Foundry)│◄──►│  Registry      │
└─────────────────┘    └──────────────────┘    │  (Eureka)       │
                                               └─────────────────┘
                                                        │
                                               ┌──────────────────┐
                                               │   IMC DB Server  │
                                               │  (Spring Boot)   │
                                               └──────────────────┘
                                                        │
                                               ┌──────────────────┐
                                               │   PostgreSQL     │
                                               │   (Greenplum)    │
                                               └──────────────────┘
```

---

## 🚀 **Quick Start**

### **Local Development**
   ```bash
# Clone and setup
git clone <repository-url>
   cd imc-db-server
   
# Configure database connection
   cp scripts/config.env.template scripts/config.env
# Edit scripts/config.env with your database credentials

# Run locally
mvn spring-boot:run -Dspring.profiles.active=local
```

### **Cloud Foundry Deployment**
```bash
# Deploy to Cloud Foundry
./scripts/build-and-push.sh

# Test the deployed API
./scripts/test-api.sh -c
```

---

## 🛠️ **Key Features**

- **🔒 Multi-Database Support**: Connect to multiple database instances
- **📊 Real-time Analytics**: Fleet performance and driver safety metrics
- **🤖 ML Integration**: Machine learning model management and recalculation
- **📱 Telematics Processing**: Vehicle event data and harsh driving detection
- **🌐 Service Discovery**: Automatic registration with Cloud Foundry service registry
- **📈 Performance Monitoring**: Built-in metrics and health checks
- **🔐 Security**: Input validation and rate limiting
- **🌍 CORS Support**: Cross-origin requests enabled for UI integration
- **📝 Comprehensive Logging**: Request/response logging with execution times

---

## 🔧 **Technical Specifications**

- **Framework**: Spring Boot 3.5.4
- **Java Version**: 21
- **Database**: PostgreSQL 12.22 (Greenplum)
- **Service Registry**: Spring Cloud Services (Eureka)
- **Cloud Platform**: Cloud Foundry
- **Memory**: 1GB allocated
- **Health Check**: HTTP endpoint at `/actuator/health`
- **Build Tool**: Maven
- **Testing**: JUnit 5 with Spring Boot Test
- **CORS Support**: Enabled for UI integration

---

## 📚 **Usage Examples**

### **Check Database Health**
```bash
curl -X GET "https://imc-db-server.apps.tas-ndc.kuhn-labs.com/api/db01/health"
```

### **Get Fleet Summary**
```bash
curl -X GET "https://imc-db-server.apps.tas-ndc.kuhn-labs.com/api/db01/fleet/summary"
```

### **Start ML Recalculation**
```bash
curl -X POST "https://imc-db-server.apps.tas-ndc.kuhn-labs.com/api/db01/ml/recalculate"
```

### **Get Vehicle Events with Filters**
```bash
curl -X GET "https://imc-db-server.apps.tas-ndc.kuhn-labs.com/api/db01/vehicle-events?event_type=harsh_driving&severity=HIGH&limit=10"
```

---

## 🤝 **Contributing**

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 **License**

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🆘 **Support**

- **Documentation**: Check this README and the `docs/` folder
- **Issues**: Report bugs via GitHub Issues
- **Questions**: Contact the development team

---

<div align="center">
  <p><strong>🎉 Built with ❤️ by the Insurance MegaCorp Development Team</strong></p>
  <p><em>Empowering safer roads through data-driven insights</em></p>
</div>
