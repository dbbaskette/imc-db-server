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
            SUM(CASE WHEN s.score < 3.0 THEN 1 ELSE 0 END),
            SUM(CASE WHEN d.accident_count > 0 THEN 1 ELSE 0 END),
            0.0
        FROM safe_driver_scores s 
        JOIN driver_ml_training_data d ON s.driver_id = d.driver_id
        """, nativeQuery = true)
    Object[] getFleetSummaryRaw();

    default FleetSummaryDto getFleetSummary() {
        Object[] result = getFleetSummaryRaw();
        try {
            return new FleetSummaryDto(
                result[0] instanceof Number ? ((Number) result[0]).longValue() : 0L,
                result[1] != null ? new java.math.BigDecimal(result[1].toString()) : new java.math.BigDecimal("0.0"),
                result[2] instanceof Number ? ((Number) result[2]).longValue() : 0L,
                result[3] instanceof Number ? ((Number) result[3]).longValue() : 0L,
                new java.math.BigDecimal("0.0")
            );
        } catch (Exception e) {
            // Fallback to safe defaults if casting fails
            return new FleetSummaryDto(0L, new java.math.BigDecimal("0.0"), 0L, 0L, new java.math.BigDecimal("0.0"));
        }
    }

    @Query("SELECT COUNT(s) FROM SafeDriverScore s JOIN DriverMlTrainingData d ON s.driverId = d.driverId")
    long countActiveDrivers();

    @Query("SELECT COUNT(s) FROM SafeDriverScore s WHERE s.score < 3.0")
    long countHighRiskDrivers();

    @Query("""
        SELECT s.driverId, s.score, 
            CASE WHEN s.score >= 4.0 THEN 'EXCELLENT' 
                 WHEN s.score >= 3.5 THEN 'GOOD' 
                 WHEN s.score >= 2.5 THEN 'AVERAGE' 
                 ELSE 'HIGH_RISK' END,
            d.speedComplianceRate, d.harshDrivingEvents, d.phoneUsageRate,
            d.accidentCount, d.totalEvents, s.calculationDate
        FROM SafeDriverScore s 
        JOIN DriverMlTrainingData d ON s.driverId = d.driverId
        WHERE s.score >= 3.5
        ORDER BY s.score DESC
        """)
    List<Object[]> findTopPerformersRaw(Pageable pageable);

    @Query("""
        SELECT s.driverId, s.score, 
            CASE WHEN s.score >= 4.0 THEN 'EXCELLENT' 
                 WHEN s.score >= 3.5 THEN 'GOOD' 
                 WHEN s.score >= 2.5 THEN 'AVERAGE' 
                 ELSE 'HIGH_RISK' END,
            d.speedComplianceRate, d.harshDrivingEvents, d.phoneUsageRate,
            d.accidentCount, d.totalEvents, s.calculationDate
        FROM SafeDriverScore s 
        JOIN DriverMlTrainingData d ON s.driverId = d.driverId
        WHERE s.score < 3.0
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
                (java.math.BigDecimal) row[3], // speedComplianceRate
                ((Long) row[4]).intValue(), // harshDrivingEvents
                (java.math.BigDecimal) row[5], // phoneUsageRate
                ((Long) row[6]).intValue(), // accidentCount
                ((Long) row[7]).intValue(), // totalEvents
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
                (java.math.BigDecimal) row[3], // speedComplianceRate
                ((Long) row[4]).intValue(), // harshDrivingEvents
                (java.math.BigDecimal) row[5], // phoneUsageRate
                ((Long) row[6]).intValue(), // accidentCount
                ((Long) row[7]).intValue(), // totalEvents
                ((java.time.ZonedDateTime) row[8]).toLocalDateTime() // calculationDate
            ))
            .toList();
    }
}