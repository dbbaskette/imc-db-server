# Mental Model

## Architecture Patterns

### Hybrid Data Access Strategy
The application uses a **hybrid approach** to data access, combining JPA/Hibernate for standard entity operations with JdbcTemplate for complex data types.

#### When to Use Each Approach

**JPA/Hibernate (Primary)**:
- Standard CRUD operations
- Simple data types (String, Integer, Date, etc.)
- Entity relationships and associations
- Automatic transaction management
- Schema validation and migration

**JdbcTemplate (Fallback)**:
- Complex PostgreSQL data types (arrays, JSON, etc.)
- Performance-critical queries
- Raw SQL operations
- Bypassing Hibernate type system limitations
- Legacy database compatibility

#### Implementation Pattern
```java
@Service
public class HybridService {
    
    public DataDto getData() {
        try {
            // Primary: JPA approach
            return repository.findData();
        } catch (Exception e) {
            // Fallback: JdbcTemplate approach
            return getDataFromJdbcTemplate();
        }
    }
}
```

### PostgreSQL Array Handling Strategy
**Problem**: PostgreSQL arrays (`double precision[]`, `text[]`, etc.) are not well-supported by Hibernate 6.

**Solution**: Use JdbcTemplate for array-containing tables while maintaining JPA for standard tables.

**Benefits**:
- Reliable array data access
- No Hibernate type conversion issues
- Maintains JPA benefits for other operations
- Clean separation of concerns

**Trade-offs**:
- Some code duplication between entity and raw SQL approaches
- Need to maintain both data access patterns
- Potential for inconsistency if schemas change

## Data Flow Architecture

### ML Model Data Pipeline
```
PostgreSQL (MADlib Output) → JdbcTemplate → Service → Controller → API Response
     ↓
driver_accident_model table with double precision[] arrays
     ↓
Bypass Hibernate type system
     ↓
Direct JDBC access for reliable array handling
     ↓
DTO mapping and business logic
     ↓
REST API response
```

### Fallback Strategy Pattern
The application implements a **graceful degradation** pattern:
1. **Primary Path**: Standard JPA repository operations
2. **Fallback Path**: JdbcTemplate for problematic data types
3. **Error Handling**: Log failures but maintain service availability

This ensures the application remains functional even when encountering Hibernate compatibility issues.

## Testing Strategy

### Test Counting Architecture
**Problem**: Mixed test types (setup, API, performance) were inflating success rates.

**Solution**: Separate counters for different test categories:
- **Setup Operations**: Prerequisites, configuration, server checks
- **API Tests**: Actual endpoint functionality testing
- **Performance Tests**: Concurrency and rate limiting validation

**Benefits**:
- Accurate success rate reporting
- Clear identification of failing areas
- Better debugging and issue tracking
- Professional test reporting

### Test Data Management
**Challenge**: Test data must match actual database schema constraints.

**Solution**: 
- Verify test data against actual database schema
- Use realistic data types (numeric IDs vs. string IDs)
- Test both valid and invalid input scenarios
- Maintain test data consistency across test runs

## Error Handling Philosophy

### Graceful Degradation
The application prioritizes **availability over perfection**:
- ML endpoints work via JdbcTemplate even when JPA fails
- Batch operations succeed with proper data formatting
- Input validation provides meaningful error messages
- Service continues operating despite individual endpoint issues

### Error Classification
**Recoverable Errors**: 
- Database connection issues (retry logic)
- Invalid input parameters (validation errors)
- Missing data (fallback to alternative sources)

**Non-Recoverable Errors**:
- Schema validation failures (startup blocking)
- Critical configuration missing (service unavailable)
- Database corruption (manual intervention required)

## Performance Considerations

### Memory Management
- **JVM Heap**: Optimized for Cloud Foundry deployment (65% of available memory)
- **Connection Pooling**: HikariCP for efficient database connections
- **Array Handling**: JdbcTemplate avoids Hibernate's memory overhead for complex types

### Query Optimization
- **Repository Queries**: Use JPA for simple, standard operations
- **Raw SQL**: JdbcTemplate for complex queries or problematic data types
- **Pagination**: Implemented across all list endpoints
- **Indexing**: Leverage database indexes for performance

## Deployment Strategy

### Cloud Foundry Optimization
- **Buildpack**: `java_buildpack_offline` for consistent deployments
- **Memory**: Optimized for specific application needs (1GB)
- **Health Checks**: HTTP endpoint monitoring with appropriate timeouts
- **Environment Variables**: Dynamic configuration via manifest generation

### Configuration Management
- **Profiles**: `cloud` profile for Cloud Foundry deployment
- **Auto-reconfiguration**: Disabled to prevent conflicts with custom datasource setup
- **Database Connection**: Dynamic JDBC URL construction from environment variables
- **Logging**: Performance monitoring and request/response logging
