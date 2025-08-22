<div align="center">
  <img src="./assets/logo.png" alt="IMC Database Server Logo" width="600"/>
  
  # 🚀 **IMC Database Server** 🚀
  
  *Multi-database Spring Boot service with Service Registry integration for Insurance MegaCorp*
  
  [![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/insurance-megacorp/imc-db-server)
  [![Test Coverage](https://img.shields.io/badge/tests-100%25-brightgreen)](https://github.com/insurance-megacorp/imc-db-server)
  [![Java Version](https://img.shields.io/badge/java-21-orange)](https://openjdk.java.net/)
  [![Spring Boot](https://img.shields.io/badge/spring--boot-3.3.3-brightgreen)](https://spring.io/projects/spring-boot)
  [![Cloud Foundry](https://img.shields.io/badge/cloud--foundry-ready-blue)](https://www.cloudfoundry.org/)
  
  ---
  
  **🎯 Mission**: Provide a robust, scalable database server for insurance mega-corporation data management, featuring advanced ML capabilities, real-time analytics, and enterprise-grade security. **Now enhanced with Cloud Foundry service registry integration for dynamic service discovery and load balancing.**
  
  **🌟 Status**: **PRODUCTION READY** with **100% Test Success Rate** ✅
  
  ---
</div>

## 📊 **Current Status** 🎉

| Component | Status | Details |
|-----------|--------|---------|
| **Overall Health** | 🟢 **PRODUCTION READY** | All systems operational |
| **API Endpoints** | 🟢 **30/30 tests passing** | 100% success rate |
| **Service Registry** | 🟢 **INTEGRATED** | Successfully connected to imc-services |
| **Database** | 🟢 **Connected** | PostgreSQL 12.22 operational |
| **Cloud Foundry** | 🟢 **Deployed** | Running with Spring Boot 3.5.4 |

## 🎯 **Mission**

The IMC Database Server provides a robust, scalable REST API for insurance telematics data management, featuring advanced ML pipeline capabilities, real-time vehicle event processing, and comprehensive driver analytics. **Now enhanced with Cloud Foundry service registry integration for dynamic service discovery and load balancing.**

## 🏗️ **Architecture**

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Cloud Foundry │    │   imc-services   │    │   Load Balancer │
│   Router        │◄──►│   (Eureka)       │◄──►│   & Discovery   │
└─────────────────┘    └──────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   imc-db-server │    │   Other Services │    │   Client Apps   │
│   (This App)    │    │   (Auto-discover)│    │   (Auto-discover)│
└─────────────────┘    └──────────────────┘    └─────────────────┘
         │
         ▼
┌─────────────────┐
│   PostgreSQL    │
│   Database      │
└─────────────────┘
```

## 🔧 **Available API Endpoints**

### 🏥 **Health & Monitoring**
| Endpoint | Method | Returns | Usage |
|----------|--------|---------|-------|
| `/api/{instance}/health` | GET | Database connection status | Health checks, monitoring |
| `/api/{instance}/database/info` | GET | Database metadata | System information |
| `/api/{instance}/database/stats` | GET | Performance metrics | Performance monitoring |

### 🚛 **Fleet Management**
| Endpoint | Method | Returns | Usage |
|----------|--------|---------|-------|
| `/api/{instance}/fleet/summary` | GET | Fleet overview | Dashboard data |
| `/api/{instance}/drivers/active-count` | GET | Active driver count | Real-time metrics |
| `/api/{instance}/drivers/high-risk-count` | GET | High-risk driver count | Risk assessment |

### 👨‍💼 **Driver Analytics**
| Endpoint | Method | Returns | Usage |
|----------|--------|---------|-------|
| `/api/{instance}/drivers/top-performers?limit=N` | GET | Top performing drivers | Performance reviews |
| `/api/{instance}/drivers/high-risk?limit=N` | GET | High-risk drivers | Risk management |

### 🤖 **ML Pipeline**
| Endpoint | Method | Returns | Usage |
|----------|--------|---------|-------|
| `/api/{instance}/ml/model-info` | GET | ML model details | Model monitoring |
| `/api/{instance}/ml/recalculate` | POST | Job ID | Trigger model updates |
| `/api/{instance}/ml/job-status/{jobId}` | GET | Job progress | Track ML jobs |
| `/api/{instance}/ml/job-status/{jobId}` | DELETE | Success status | Cancel ML jobs |

### 🚗 **Vehicle Events**
| Endpoint | Method | Returns | Usage |
|----------|--------|---------|-------|
| `/api/{instance}/vehicle-events` | GET | Vehicle events | Event monitoring |
| `/api/{instance}/vehicle-events/high-gforce` | GET | High G-force events | Safety analysis |
| `/api/{instance}/vehicle-events/batch` | POST | Insert results | Bulk data import |
| `/api/{instance}/telemetry/events-count` | GET | Event counts | Metrics collection |

## 🚀 **Service Registry Integration**

### **What's New**
- ✅ **Eureka Client Integration**: Connected to Cloud Foundry `imc-services`
- ✅ **Auto-registration**: Automatically registers with service registry
- ✅ **Service Discovery**: Other services can discover this application
- ✅ **Load Balancing Ready**: Prepared for horizontal scaling

### **Configuration**
```yaml
# application-cloud.yml
spring:
  cloud:
    service-registry:
      auto-registration:
        enabled: true
        register-management: true
        fail-fast: false
    discovery:
      enabled: true
      client:
        enabled: true
        service-id: ${spring.application.name}
```

### **Benefits**
- **Dynamic Scaling**: Easy to add/remove instances
- **Service Discovery**: Automatic service location
- **Load Balancing**: Built-in traffic distribution
- **Health Monitoring**: Centralized health checks
- **Fault Tolerance**: Automatic failover support

## 📈 **Performance Metrics**

| Metric | Value | Status |
|--------|-------|--------|
| **Response Time** | < 300ms | 🟢 Excellent |
| **Database Connection** | PostgreSQL 12.22 | 🟢 Connected |
| **Memory Usage** | 1GB allocated | 🟢 Optimal |
| **Health Check** | /actuator/health | 🟢 Operational |
| **Service Registry** | imc-services bound | 🟢 Integrated |

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

## 🛠️ **Technical Specifications**

- **Framework**: Spring Boot 3.5.4
- **Java Version**: 21
- **Database**: PostgreSQL 12.22
- **Service Registry**: Spring Cloud Services (Eureka)
- **Cloud Platform**: Cloud Foundry
- **Memory**: 1GB allocated
- **Health Check**: HTTP endpoint at `/actuator/health`

## 🚀 **Quick Start**

### **Local Development**
```bash
# Clone and setup
git clone <repository>
cd imc-db-server
mvn spring-boot:run

# Test locally
scripts/test-api.sh
```

### **Cloud Foundry Deployment**
```bash
# Deploy with service registry
scripts/build-and-push.sh

# Check status
cf apps
cf services
```

### **Service Registry Usage**
```bash
# View registered services
cf env imc-db-server | grep VCAP_SERVICES

# Check health with discovery
curl https://imc-db-server.apps.tas-ndc.kuhn-labs.com/actuator/health
```

## ✨ **Key Features**

- 🔍 **Service Discovery**: Automatic registration with Cloud Foundry service registry
- 🚀 **ML Pipeline**: Advanced machine learning model management
- 📊 **Real-time Analytics**: Live driver performance monitoring
- 🚗 **Vehicle Events**: Comprehensive telematics data processing
- 🛡️ **Security**: Input validation and rate limiting
- 📈 **Monitoring**: Health checks and performance metrics
- 🔄 **Auto-scaling**: Ready for horizontal scaling via service registry

## 🤝 **Contributing**

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests: `scripts/test-api.sh`
5. Submit a pull request

## 📄 **License**

This project is proprietary to Insurance MegaCorp.

## 🆘 **Support**

- **Documentation**: See `docs/` directory
- **Issues**: Check `gotchas.md` for common problems
- **Testing**: Use `scripts/test-api.sh` for validation

---

## 🎉 **Celebration**

**🚀 Service Registry Integration Complete! 🚀**

The IMC Database Server is now fully integrated with Cloud Foundry's service registry, enabling:
- ✅ Automatic service discovery
- ✅ Dynamic load balancing
- ✅ Seamless horizontal scaling
- ✅ Centralized health monitoring
- ✅ Fault tolerance and failover

**All 30 tests passing with 100% success rate!** 🎯

---

*Built with ❤️ using Spring Boot, Cloud Foundry, and PostgreSQL*

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-6DB33F?style=for-the-badge&logo=spring-boot)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=java)
![Cloud Foundry](https://img.shields.io/badge/Cloud%20Foundry-Platform-FF6600?style=for-the-badge&logo=cloud-foundry)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12.22-336791?style=for-the-badge&logo=postgresql)
![Service Registry](https://img.shields.io/badge/Service%20Registry-Eureka-00C4B3?style=for-the-badge&logo=eureka)
