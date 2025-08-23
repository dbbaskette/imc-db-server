package com.insurancemegacorp.dbserver.repository;

import com.insurancemegacorp.dbserver.dto.DriverPerformanceDto;
import com.insurancemegacorp.dbserver.dto.FleetSummaryDto;
import com.insurancemegacorp.dbserver.model.SafeDriverScore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SafeDriverScoreRepository extends JpaRepository<SafeDriverScore, Integer> {

    @Query(value = """
        SELECT 
            COUNT(s.score_id), 
            AVG(s.score),
            SUM(CASE WHEN s.score < 70.0 THEN 1 ELSE 0 END),
            COALESCE(SUM(CASE WHEN d.accident_count > 0 THEN 1 ELSE 0 END), 0),
            0.0
        FROM safe_driver_scores s 
        LEFT JOIN driver_ml_training_data d ON s.driver_id = d.driver_id
        """, nativeQuery = true)
    List<Object[]> getFleetSummaryRaw();

    default FleetSummaryDto getFleetSummary() {
        List<Object[]> results = getFleetSummaryRaw();
        try {
            if (results == null || results.isEmpty()) {
                System.out.println("Fleet Summary: No results returned");
                return new FleetSummaryDto(0L, new java.math.BigDecimal("0.0"), 0L, 0L, new java.math.BigDecimal("0.0"));
            }
            
            Object[] result = results.get(0); // Get the first (and only) row
            
            // Debug logging
            System.out.println("Fleet Summary Raw Result: " + java.util.Arrays.toString(result));
            System.out.println("Result length: " + result.length);
            System.out.println("Result[0] type: " + (result[0] != null ? result[0].getClass().getName() : "null"));
            System.out.println("Result[1] type: " + (result[1] != null ? result[1].getClass().getName() : "null"));
            System.out.println("Result[2] type: " + (result[2] != null ? result[2].getClass().getName() : "null"));
            System.out.println("Result[3] type: " + (result[3] != null ? result[3].getClass().getName() : "null"));
            
            return new FleetSummaryDto(
                result[0] instanceof Number ? ((Number) result[0]).longValue() : 0L,
                result[1] != null ? new java.math.BigDecimal(result[1].toString()) : new java.math.BigDecimal("0.0"),
                result[2] instanceof Number ? ((Number) result[2]).longValue() : 0L,
                result[3] instanceof Number ? ((Number) result[3]).longValue() : 0L,
                new java.math.BigDecimal("0.0")
            );
        } catch (Exception e) {
            // Fallback to safe defaults if casting fails
            System.out.println("Exception in getFleetSummary: " + e.getMessage());
            e.printStackTrace();
            return new FleetSummaryDto(0L, new java.math.BigDecimal("0.0"), 0L, 0L, new java.math.BigDecimal("0.0"));
        }
    }

    @Query("SELECT COUNT(s) FROM SafeDriverScore s")
    long countActiveDrivers();

    @Query("SELECT COUNT(s) FROM SafeDriverScore s WHERE s.score < 70.0")
    long countHighRiskDrivers();

    @Query("""
        SELECT s.driverId, s.score, 
            CASE WHEN s.score >= 90.0 THEN 'EXCELLENT' 
                 WHEN s.score >= 80.0 THEN 'GOOD' 
                 WHEN s.score >= 70.0 THEN 'AVERAGE' 
                 ELSE 'HIGH_RISK' END,
            COALESCE(d.speedComplianceRate, 0), COALESCE(d.harshDrivingEvents, 0), COALESCE(d.phoneUsageRate, 0),
            COALESCE(d.accidentCount, 0), COALESCE(d.totalEvents, 0), s.calculationDate
        FROM SafeDriverScore s 
        LEFT JOIN DriverMlTrainingData d ON s.driverId = d.driverId
        WHERE s.score >= 80.0
        ORDER BY s.score DESC
        """)
    List<Object[]> findTopPerformersRaw(Pageable pageable);

    @Query("""
        SELECT s.driverId, s.score, 
            CASE WHEN s.score >= 90.0 THEN 'EXCELLENT' 
                 WHEN s.score >= 80.0 THEN 'GOOD' 
                 WHEN s.score >= 70.0 THEN 'AVERAGE' 
                 ELSE 'HIGH_RISK' END,
            COALESCE(d.speedComplianceRate, 0), COALESCE(d.harshDrivingEvents, 0), COALESCE(d.phoneUsageRate, 0),
            COALESCE(d.accidentCount, 0), COALESCE(d.totalEvents, 0), s.calculationDate
        FROM SafeDriverScore s 
        LEFT JOIN DriverMlTrainingData d ON s.driverId = d.driverId
        WHERE s.score < 70.0
        ORDER BY s.score ASC
        """)
    List<Object[]> findHighRiskDriversRaw(Pageable pageable);

    // Keep the original method names but use the raw queries and convert in the service layer
    default List<DriverPerformanceDto> findTopPerformers(Pageable pageable) {
        return findTopPerformersRaw(pageable).stream()
            .map(row -> new DriverPerformanceDto(
                ((Integer) row[0]).longValue(), // driverId
                (java.math.BigDecimal) row[1], // score
                (String) row[2], // riskCategory
                row[3] instanceof Number ? new java.math.BigDecimal(row[3].toString()) : java.math.BigDecimal.ZERO, // speedComplianceRate
                row[4] instanceof Number ? ((Number) row[4]).intValue() : 0, // harshDrivingEvents
                row[5] instanceof Number ? new java.math.BigDecimal(row[5].toString()) : java.math.BigDecimal.ZERO, // phoneUsageRate
                row[6] instanceof Number ? ((Number) row[6]).intValue() : 0, // accidentCount
                row[7] instanceof Number ? ((Number) row[7]).intValue() : 0, // totalEvents
                ((java.time.ZonedDateTime) row[8]).toLocalDateTime() // calculationDate
            ))
            .toList();
    }

    default List<DriverPerformanceDto> findHighRiskDrivers(Pageable pageable) {
        return findHighRiskDriversRaw(pageable).stream()
            .map(row -> new DriverPerformanceDto(
                ((Integer) row[0]).longValue(), // driverId
                (java.math.BigDecimal) row[1], // score
                (String) row[2], // riskCategory
                row[3] instanceof Number ? new java.math.BigDecimal(row[3].toString()) : java.math.BigDecimal.ZERO, // speedComplianceRate
                row[4] instanceof Number ? ((Number) row[4]).intValue() : 0, // harshDrivingEvents
                row[5] instanceof Number ? new java.math.BigDecimal(row[5].toString()) : java.math.BigDecimal.ZERO, // phoneUsageRate
                row[6] instanceof Number ? ((Number) row[6]).intValue() : 0, // accidentCount
                row[7] instanceof Number ? ((Number) row[7]).intValue() : 0, // totalEvents
                ((java.time.ZonedDateTime) row[8]).toLocalDateTime() // calculationDate
            ))
            .toList();
    }
}