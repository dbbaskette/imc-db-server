# IMC Database Server - Gotchas & Common Issues

This document captures common issues encountered during development and deployment, along with their solutions and prevention strategies.

## Database Configuration Property Binding Issues

### Problem
After adding Spring Cloud Services integration, the application fails to start with:
```
HikariPool-1 - jdbcUrl is required with driverClassName
```

### Root Cause
Spring Boot 3.5.4 + Spring Cloud 2025.0.0 has property binding issues where `@ConfigurationProperties` doesn't properly map `url` to HikariCP's expected `jdbcUrl` property.

### Solution
Use `jdbc-url:` instead of `url:` in YAML configuration:

```yaml
# ❌ Wrong - causes binding issues
spring:
  datasource:
    db01:
      url: jdbc:postgresql://${DB01_HOST}:${DB01_PORT:5432}/${DB01_DATABASE}

# ✅ Correct - works with HikariCP
spring:
  datasource:
    db01:
      jdbc-url: jdbc:postgresql://${DB01_HOST}:${DB01_PORT:5432}/${DB01_DATABASE}
```

### Prevention
- Always use `jdbc-url:` for HikariCP datasource configuration
- Test property binding after adding Spring Cloud dependencies
- Verify configuration works in both local and cloud profiles

### Files Affected
- `src/main/resources/application-cloud.yml`
- `src/main/resources/application-local.yml`

## ML Model Array Type Conversion Issues

### Problem
PostgreSQL arrays (e.g., `double precision[]`) cannot be mapped to Java types using Hibernate's standard entity mapping, causing 500 errors.

### Root Cause
Hibernate 6 doesn't have built-in support for PostgreSQL array types, and `@JdbcTypeCode(SqlTypes.ARRAY)` doesn't work reliably with complex array structures.

### Solution
Use `JdbcTemplate` for endpoints requiring array handling:

```java
@Service
public class MlService {
    public MlModelInfoDto getModelInfo() {
        String sql = "SELECT model_id, algorithm, accuracy, feature_weights FROM driver_accident_models WHERE status = 'ACTIVE'";
        return jdbcTemplate.query(sql, new ResultSetExtractor<MlModelInfoDto>() {
            @Override
            public MlModelInfoDto extractData(ResultSet rs) throws SQLException {
                if (rs.next()) {
                    MlModelInfoDto dto = new MlModelInfoDto();
                    // Handle PostgreSQL array type
                    Array featureWeightsArray = rs.getArray("feature_weights");
                    if (featureWeightsArray != null) {
                        Double[] weights = (Double[]) featureWeightsArray.getArray();
                        // Map array to DTO structure
                    }
                    return dto;
                }
                return null;
            }
        });
    }
}
```

### Prevention
- Test array handling endpoints thoroughly
- Have fallback strategies for complex data types
- Document which endpoints use JdbcTemplate vs JPA

## Test Counting Logic Issues

### Problem
Test script incorrectly counts setup operations as API tests, leading to inaccurate success rate reporting.

### Root Cause
Single counter used for both setup operations and actual API tests.

### Solution
Implement separate counters for different test types:

```bash
# Separate counters
SETUP_PASSED=0
SETUP_FAILED=0
PASSED_TESTS=0
FAILED_TESTS=0

log_success() {
    local message="$1"
    local test_type="${2:-api}"  # Default to API test
    
    if [ "$test_type" = "setup" ]; then
        ((SETUP_PASSED++))
    else
        ((PASSED_TESTS++))
    fi
}
```

### Prevention
- Always use separate counters for different test phases
- Validate test counts before reporting results
- Use descriptive variable names

## Vehicle Events Model Mismatches

### Problem
Test data uses string vehicle IDs that can't be parsed as Long values, causing type conversion errors.

### Root Cause
JSON test data contains `"vehicleId": "TEST001"` instead of numeric values.

### Solution
Use numeric strings that can be parsed as Long:

```bash
# ❌ Wrong - causes parsing errors
sample_events='[{"vehicleId": "TEST001", ...}]'

# ✅ Correct - parses correctly
sample_events='[{"vehicleId": "999001", ...}]'
```

### Prevention
- Validate test data types match expected Java types
- Use realistic but valid test data
- Test data parsing in isolation

## Parameter Validation Issues

### Problem
Invalid parameters return 500 errors instead of expected 400 Bad Request responses.

### Root Cause
Missing exception handlers for parameter conversion errors.

### Solution
Add comprehensive exception handling:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid parameter type for '" + ex.getName() + "'"));
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiResponse<Object>> handleNumberFormatException(NumberFormatException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid numeric parameter"));
    }
}
```

### Prevention
- Always handle parameter conversion exceptions
- Return appropriate HTTP status codes
- Provide clear error messages

## Rate Limiting Configuration Issues

### Problem
Rate limiting not working - all requests return 202 instead of 429 for rate-limited endpoints.

### Root Cause
Filter registration and endpoint matching issues.

### Solution
Proper filter configuration with endpoint-specific logic:

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilter() {
        FilterRegistrationBean<RateLimitingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitingFilter());
        registrationBean.addUrlPatterns("/api/*"); // Apply to all API endpoints
        registrationBean.setOrder(1);
        return registrationBean;
    }
}

public class RateLimitingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        
        // Only apply rate limiting to specific endpoints
        if (!requestURI.contains("/ml/recalculate") && !requestURI.contains("/vehicle-events/batch")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Rate limiting logic for target endpoints
        // ... implementation
    }
}
```

### Prevention
- Test rate limiting with actual HTTP requests
- Verify filter registration and URL patterns
- Use appropriate rate limits for testing

## Service Registry Integration Issues

### Problem
Application not appearing in Cloud Foundry service registry despite proper configuration.

### Root Cause
Conflicting Eureka configurations and Spring Cloud Services auto-configuration interference.

### Solution
Simplify configuration to rely on Spring Cloud Services auto-configuration:

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

### Prevention
- Don't override Spring Cloud Services auto-configuration
- Use `@EnableDiscoveryClient` only when necessary
- Test service registry integration after deployment

## Environment Variable Resolution Issues

### Problem
Environment variables not being resolved in Cloud Foundry deployment.

### Root Cause
Using wrong property names or conflicting configurations.

### Solution
Use the build script and proper property naming:

```bash
# Use the build script for deployment
./scripts/build-and-push.sh

# Ensure proper property names in YAML
spring:
  datasource:
    db01:
      jdbc-url: jdbc:postgresql://${DB01_HOST}:${DB01_PORT:5432}/${DB01_DATABASE}
      username: ${DB01_USER}
      password: ${DB01_PASSWORD}
```

### Prevention
- Always use `./scripts/build-and-push.sh` for deployment
- Verify environment variables in `cf env`
- Test property resolution locally

## Spring Boot Version Compatibility Issues

### Problem
Spring Boot 3.3.3 not compatible with Spring Cloud 2025.0.0.

### Root Cause
Version mismatch between Spring Boot and Spring Cloud releases.

### Solution
Use compatible versions:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.4</version>
</parent>

<properties>
    <spring-cloud.version>2025.0.0</spring-cloud.version>
</properties>
```

### Prevention
- Check Spring Boot and Spring Cloud compatibility matrix
- Use latest stable versions
- Test compatibility before deployment

## Cloud Foundry Buildpack Issues

### Problem
Application crashes during startup in Cloud Foundry.

### Root Cause
Memory configuration or JVM settings not optimized for Cloud Foundry.

### Solution
Optimize memory settings in manifest:

```yaml
env:
  JVM_HEAP_RATIO: 0.65
  JAVA_OPTS: '-Xmx650m -Xss1M -XX:ReservedCodeCacheSize=240M -XX:MaxMetaspaceSize=200M -XX:+UseG1GC'
  JBP_CONFIG_OPEN_JDK_JRE: '{ jre: { version: 21.+ } }'
```

### Prevention
- Monitor memory usage during startup
- Use appropriate heap ratios for Cloud Foundry
- Test with realistic memory constraints

## General Prevention Strategies

### Code Quality
- **Unit Tests**: Cover all edge cases and error conditions
- **Integration Tests**: Test complete request/response cycles
- **Error Handling**: Implement comprehensive exception handling
- **Validation**: Validate all inputs and parameters

### Configuration Management
- **Profile-based**: Use different configurations for different environments
- **Environment Variables**: Use environment variables for sensitive data
- **Validation**: Validate configuration at startup
- **Documentation**: Document all configuration options

### Deployment
- **Build Scripts**: Use automated build and deployment scripts
- **Environment Checks**: Verify environment before deployment
- **Health Checks**: Implement comprehensive health monitoring
- **Rollback Strategy**: Have rollback procedures ready

### Monitoring
- **Logging**: Implement structured logging with appropriate levels
- **Metrics**: Track performance and error rates
- **Alerts**: Set up alerts for critical failures
- **Dashboards**: Monitor application health in real-time
