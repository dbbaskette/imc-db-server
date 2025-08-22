# Implementation Details

This document contains technical implementation details for the IMC Database Server.

## ML Model Array Type Handling

### Problem
PostgreSQL arrays (specifically `double precision[]`) cannot be directly mapped to Java types using Hibernate's standard type system. This caused 500 errors when trying to retrieve ML model information.

### Solution
Implemented a hybrid approach using `JdbcTemplate` for complex data types while maintaining JPA for standard operations.

### Implementation Details
```java
@Service
public class MlService {
    private final JdbcTemplate jdbcTemplate;
    
    public MlModelInfoDto getModelInfo(String instance) {
        // Try entity-based approach first
        try {
            Optional<DriverAccidentModel> model = repository.findLatestModel();
            if (model.isPresent()) {
                return mapToDto(model.get());
            }
        } catch (Exception e) {
            logger.warn("Entity-based approach failed, falling back to JdbcTemplate");
        }
        
        // Fallback to JdbcTemplate for array handling
        String sql = "SELECT * FROM driver_accident_model ORDER BY created_at DESC LIMIT 1";
        return jdbcTemplate.query(sql, new ResultSetExtractor<MlModelInfoDto>() {
            @Override
            public MlModelInfoDto extractData(ResultSet rs) throws SQLException {
                if (rs.next()) {
                    return mapResultSetToDto(rs);
                }
                return null;
            }
        });
    }
}
```

### Benefits
- **Reliability**: Bypasses Hibernate type conversion issues
- **Performance**: Direct SQL execution for complex queries
- **Flexibility**: Can handle any PostgreSQL data type
- **Fallback Strategy**: Graceful degradation when entity mapping fails

### Alternatives Considered
1. **Custom Hibernate Types**: Complex and version-dependent
2. **Native Queries with @Query**: Still goes through Hibernate's result mapping
3. **EntityManager Native Queries**: Same limitations as @Query

## Service Registry Integration

### Overview
Successfully integrated with Cloud Foundry's service registry using Spring Cloud Services and Eureka client.

### Dependencies Added
```xml
<!-- Spring Cloud Service Registry -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>io.pivotal.cfenv</groupId>
    <artifactId>java-cfenv-boot</artifactId>
    <version>2.4.0</version>
</dependency>
<dependency>
    <groupId>io.pivotal.spring.cloud</groupId>
    <artifactId>spring-cloud-services-starter-service-registry</artifactId>
    <version>4.1.3</version>
</dependency>
```

### Configuration
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
    compatibility-verifier:
      enabled: false  # For Spring Boot 3.5.4 + Spring Cloud 2025.0.0
```

### Main Application Class
```java
@SpringBootApplication
@EnableDiscoveryClient
public class ImcDbServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImcDbServerApplication.class, args);
    }
}
```

### Service Binding
The application is bound to `imc-services` in Cloud Foundry:
```yaml
# manifest.yml
services:
  - imc-services  # Spring Cloud Service Registry
```

### Benefits Achieved
- **Automatic Registration**: App registers itself with service registry
- **Service Discovery**: Other services can discover this application
- **Load Balancing Ready**: Prepared for horizontal scaling
- **Health Monitoring**: Centralized health checks via registry
- **Fault Tolerance**: Automatic failover support

### Verification
- Service appears in Cloud Foundry service registry
- Health endpoint shows discovery client status
- All 30 API tests passing with service registry enabled
- Application successfully registers with Eureka

## Test Counting Logic

### Problem
The test script was incorrectly counting setup operations as API tests, leading to inaccurate success rates.

### Solution
Implemented separate counters for setup operations and API tests.

### Implementation
```bash
# Separate counters for different test types
SETUP_PASSED=0
SETUP_FAILED=0
PASSED_TESTS=0
FAILED_TESTS=0

log_success() {
    if [[ "$1" == "setup" ]]; then
        ((SETUP_PASSED++))
    else
        ((PASSED_TESTS++))
    fi
}

log_error() {
    if [[ "$1" == "setup" ]]; then
        ((SETUP_FAILED++))
    else
        ((FAILED_TESTS++))
    fi
}
```

### Result
Accurate test reporting with clear separation between setup operations and API tests.

## Vehicle Events Model Mismatches

### Problem
Test data contained string vehicle IDs that couldn't be parsed as Long values.

### Solution
Updated test data to use numeric strings that can be properly parsed.

### Before
```json
{
  "vehicleId": "TEST001",  // String - causes parsing error
  "eventType": "acceleration"
}
```

### After
```json
{
  "vehicleId": "999001",   // Numeric string - parses correctly
  "eventType": "acceleration"
}
```

## Parameter Validation Issues

### Problem
Invalid parameters were returning 500 errors instead of expected 400 Bad Request.

### Solution
Added specific exception handlers for parameter conversion errors.

### Implementation
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid parameter type for '" + ex.getName() + "'"));
    }
    
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiResponse<Object>> handleNumberFormatException(
            NumberFormatException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid numeric parameter"));
    }
}
```

### Result
Proper HTTP 400 responses for invalid parameters, improving API usability and error handling.

## Rate Limiting Implementation

### Configuration
```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilter() {
        FilterRegistrationBean<RateLimitingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitingFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
```

### Filter Logic
```java
public class RateLimitingFilter extends OncePerRequestFilter {
    private static final int MAX_REQUESTS_PER_MINUTE = 2;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response,
                                  FilterChain filterChain) {
        // Only apply to specific endpoints
        if (!requestURI.contains("/ml/recalculate") && 
            !requestURI.contains("/vehicle-events/batch")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Rate limiting logic
        if (info.isLimitExceeded()) {
            response.setStatus(429);
            return;
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### Result
Successfully limits requests to 2 per minute for expensive endpoints, with proper 429 responses.
