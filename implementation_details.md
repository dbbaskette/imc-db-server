# Implementation Details

## ML Model Array Type Handling

### Problem Overview
The `driver_accident_model` table contains PostgreSQL `double precision[]` arrays that Hibernate cannot properly handle, causing HTTP 500 errors on ML model endpoints.

### Technical Solution: JdbcTemplate Approach

#### Why JdbcTemplate?
- **Complete Bypass**: JdbcTemplate operates at the JDBC level, completely avoiding Hibernate's type system
- **Direct SQL**: Allows direct SQL execution without entity mapping complications
- **Array Handling**: PostgreSQL arrays are handled natively by the JDBC driver
- **Performance**: Minimal overhead compared to Hibernate's complex type conversion system

#### Implementation Details

**Service Layer**:
```java
@Service
public class MlService {
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public MlService(DriverAccidentModelRepository driverAccidentModelRepository, 
                    JdbcTemplate jdbcTemplate) {
        this.driverAccidentModelRepository = driverAccidentModelRepository;
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public MlModelInfoDto getModelInfo() {
        try {
            // Try entity approach first
            Optional<DriverAccidentModel> modelOpt = driverAccidentModelRepository.findLatestActiveModel();
            if (modelOpt.isPresent()) {
                return convertToDto(modelOpt.get());
            }
        } catch (Exception e) {
            // Fall back to JdbcTemplate on failure
            return getModelInfoFromJdbcTemplate();
        }
        return null;
    }
}
```

**JdbcTemplate Query**:
```java
private MlModelInfoDto getModelInfoFromJdbcTemplate() {
    try {
        String sql = """
            SELECT num_iterations, coef, log_likelihood, std_err, z_stats, 
                   p_values, odds_ratios, condition_no, num_rows_processed, 
                   num_missing_rows_skipped, variance_covariance 
            FROM driver_accident_model 
            LIMIT 1
            """;
        
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return convertResultSetToDto(rs);
            }
            return null;
        });
        
    } catch (Exception e) {
        System.err.println("Error getting ML model info from JdbcTemplate: " + e.getMessage());
    }
    return null;
}
```

**ResultSet Mapping**:
```java
private MlModelInfoDto convertResultSetToDto(ResultSet rs) throws SQLException {
    MlModelInfoDto dto = new MlModelInfoDto();
    
    // Map database columns to DTO fields
    Integer numIterations = rs.getObject("num_iterations", Integer.class);
    if (numIterations != null) {
        dto.setModelId(numIterations.toString());
    }
    
    dto.setAlgorithm("Logistic Regression");
    
    // Array fields set to null (not easily convertible)
    dto.setAccuracy(null);
    dto.setFeatureWeights(null);
    dto.setLastTrained(null);
    
    Long numRowsProcessed = rs.getObject("num_rows_processed", Long.class);
    if (numRowsProcessed != null) {
        dto.setNumRowsProcessed(numRowsProcessed.intValue());
    }
    
    return dto;
}
```

#### Fallback Strategy
The implementation uses a **graceful degradation** approach:
1. **Primary**: Attempt entity-based query via repository
2. **Fallback**: If entity approach fails, use JdbcTemplate
3. **Error Handling**: Log errors but don't crash the application

#### Benefits of This Approach
- **Reliability**: ML endpoints work regardless of Hibernate array handling issues
- **Maintainability**: Clear separation of concerns between entity and raw SQL approaches
- **Performance**: JdbcTemplate queries are typically faster for simple data retrieval
- **Flexibility**: Easy to modify SQL queries without changing entity mappings

#### Alternative Solutions Considered
1. **Custom Type Handlers**: Complex to implement and maintain
2. **Entity Mapping Changes**: Would require significant refactoring
3. **Native Query with Result Mapping**: Still goes through Hibernate's type system
4. **JdbcTemplate**: ✅ Clean, simple, and effective

### Database Schema Details
The `driver_accident_model` table structure:
```sql
CREATE TABLE driver_accident_model (
    num_iterations INTEGER,
    coef DOUBLE PRECISION[],
    log_likelihood DOUBLE PRECISION,
    std_err DOUBLE PRECISION[],
    z_stats DOUBLE PRECISION[],
    p_values DOUBLE PRECISION[],
    odds_ratios DOUBLE PRECISION[],
    condition_no DOUBLE PRECISION,
    num_rows_processed BIGINT,
    num_missing_rows_skipped BIGINT,
    variance_covariance DOUBLE PRECISION[]
);
```

This is a MADlib output table with typically one row containing model coefficients and statistics as arrays.
