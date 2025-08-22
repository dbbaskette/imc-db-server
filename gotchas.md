# Gotchas & Edge Cases

## ML Model Array Type Conversion Issues

### Problem: PostgreSQL Array Types in Hibernate
**Issue**: Hibernate cannot properly handle PostgreSQL `double precision[]` arrays when using `@JdbcTypeCode(SqlTypes.ARRAY)` with `List<Double>` or `double[]` fields.

**Symptoms**:
- HTTP 500 errors on ML model endpoints
- Error: `Could not convert '[Ljava.lang.Double;' to 'java.lang.Double' using 'org.hibernate.type.descriptor.java.DoubleJavaType' to wrap`
- Both entity-based queries and native queries fail with the same error

**Root Cause**: 
- PostgreSQL returns Java array types (`[Ljava.lang.Double;`) that Hibernate's type system cannot convert to the expected field types
- This is a fundamental compatibility issue between Hibernate 6 and PostgreSQL array handling
- Even `@Query(nativeQuery = true)` still goes through Hibernate's result mapping

**Solutions Attempted**:
1. **Entity Mapping**: `@JdbcTypeCode(SqlTypes.ARRAY)` with `List<Double>` ❌
2. **Primitive Arrays**: `@JdbcTypeCode(SqlTypes.ARRAY)` with `double[]` ❌  
3. **Native Queries**: `@Query(nativeQuery = true)` with EntityManager ❌
4. **JdbcTemplate**: Direct SQL with Spring's JdbcTemplate ✅

**Final Solution**: Use `JdbcTemplate` to completely bypass Hibernate
```java
@Autowired
private JdbcTemplate jdbcTemplate;

private MlModelInfoDto getModelInfoFromJdbcTemplate() {
    String sql = "SELECT num_iterations, coef, log_likelihood, std_err, z_stats, p_values, odds_ratios, condition_no, num_rows_processed, num_missing_rows_skipped, variance_covariance FROM driver_accident_model LIMIT 1";
    
    return jdbcTemplate.query(sql, rs -> {
        if (rs.next()) {
            return convertResultSetToDto(rs);
        }
        return null;
    });
}
```

**Key Learning**: When dealing with PostgreSQL arrays in Spring Boot, JdbcTemplate provides a clean, reliable alternative to Hibernate's problematic array handling.

## Test Counting Logic Issues

### Problem: Inflated Success Rates
**Issue**: Test scripts were counting setup operations as "passed tests", leading to misleading success rates.

**Symptoms**:
- Success rate showing 100% with failed tests
- Setup operations (prerequisites, config loading) counted as API tests
- Performance tests and other non-API operations inflating counts

**Solution**: Separate counters for setup vs. actual API tests
```bash
# Test counters
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0
SETUP_PASSED=0
SETUP_FAILED=0
```

**Key Learning**: Always separate infrastructure/setup operations from actual API endpoint testing for accurate success rate reporting.

## Vehicle Events Model Mismatches

### Problem: Database Schema vs. JPA Entity Mismatches
**Issue**: JPA entities didn't match actual database table structures.

**Symptoms**:
- Schema validation errors during startup
- Missing columns or wrong data types
- Repository queries failing with field reference errors

**Solution**: Audit all entities against actual database schema and update accordingly.

**Key Learning**: Always verify JPA entities against actual database schema, especially when working with existing databases or MADlib output tables.

## Parameter Validation Issues

### Problem: Type Conversion Errors Returning 500 Instead of 400
**Issue**: When Spring tries to convert string parameters to numeric types (e.g., `limit=abc`), it throws exceptions that result in HTTP 500 errors instead of the expected 400 Bad Request.

**Symptoms**:
- Invalid numeric parameters return 500 Internal Server Error
- Error logs show `NumberFormatException` or similar type conversion errors
- Tests expecting 400 get 500 instead

**Root Cause**: 
- Spring's parameter binding happens before controller method execution
- Type conversion failures throw exceptions that aren't caught by standard validation handlers
- Need specific exception handlers for parameter type mismatches

**Solutions**:
1. **Add `MethodArgumentTypeMismatchException` handler**:
```java
@ExceptionHandler(MethodArgumentTypeMismatchException.class)
public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    logger.warn("Type mismatch error: {} for parameter '{}' with value '{}'", 
               ex.getMessage(), ex.getName(), ex.getValue());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("Invalid parameter type for '" + ex.getName() + "'"));
}
```

2. **Add `NumberFormatException` handler** for additional coverage:
```java
@ExceptionHandler(NumberFormatException.class)
public ResponseEntity<ApiResponse<Object>> handleNumberFormatException(NumberFormatException ex) {
    logger.warn("Number format error: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("Invalid numeric parameter"));
}
```

**Key Learning**: Always handle `MethodArgumentTypeMismatchException` in Spring Boot applications to provide proper 400 responses for parameter type conversion failures.

### Problem: Missing Parameter Validation
**Issue**: After removing validation for non-existent database fields, endpoints accepted invalid parameters and returned 200 with data instead of 400 errors.

**Symptoms**:
- Invalid event types returned 200 instead of expected 400
- Tests expecting validation errors got successful responses
- Inconsistent behavior between different endpoints

**Solution**: Re-implement parameter validation with explicit validation methods:
```java
private boolean isValidEventType(String eventType) {
    if (eventType == null) return true;
    
    String[] validTypes = {
        "telematics_event", "acceleration", "braking", "cornering", 
        "speed_violation", "harsh_driving", "phone_usage", "weather_event"
    };
    
    for (String validType : validTypes) {
        if (validType.equalsIgnoreCase(eventType)) {
            return true;
        }
    }
    return false;
}
```

**Key Learning**: When removing validation, ensure the new behavior aligns with test expectations, or re-implement appropriate validation logic.
