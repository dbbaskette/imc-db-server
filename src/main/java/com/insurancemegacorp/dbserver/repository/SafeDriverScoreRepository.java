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
public interface SafeDriverScoreRepository extends JpaRepository<SafeDriverScore, Long> {

    @Query("""
        SELECT new com.insurancemegacorp.dbserver.dto.FleetSummaryDto(
            COUNT(s), 
            ROUND(AVG(s.score), 1),
            COUNT(s) FILTER (WHERE s.riskCategory IN ('HIGH_RISK', 'POOR')),
            COUNT(d) FILTER (WHERE d.accidents > 0),
            COALESCE(AVG(s.score) - LAG(AVG(s.score)) OVER (ORDER BY MAX(s.calculationDate)), 0)
        )
        FROM SafeDriverScore s 
        JOIN DriverMlTrainingData d ON s.driverId = d.driverId
        """)
    FleetSummaryDto getFleetSummary();

    @Query("SELECT COUNT(s) FROM SafeDriverScore s JOIN DriverMlTrainingData d ON s.driverId = d.driverId")
    long countActiveDrivers();

    @Query("SELECT COUNT(s) FROM SafeDriverScore s WHERE s.riskCategory IN ('HIGH_RISK', 'POOR')")
    long countHighRiskDrivers();

    @Query("""
        SELECT new com.insurancemegacorp.dbserver.dto.DriverPerformanceDto(
            s.driverId, s.score, s.riskCategory,
            d.speedCompliance, d.harshEvents, d.phoneUsage,
            d.accidents, d.totalEvents, s.calculationDate
        )
        FROM SafeDriverScore s 
        JOIN DriverMlTrainingData d ON s.driverId = d.driverId
        WHERE s.riskCategory IN ('EXCELLENT', 'GOOD')
        ORDER BY s.score DESC
        """)
    List<DriverPerformanceDto> findTopPerformers(Pageable pageable);

    @Query("""
        SELECT new com.insurancemegacorp.dbserver.dto.DriverPerformanceDto(
            s.driverId, s.score, s.riskCategory,
            d.speedCompliance, d.harshEvents, d.phoneUsage,
            d.accidents, d.totalEvents, s.calculationDate
        )
        FROM SafeDriverScore s 
        JOIN DriverMlTrainingData d ON s.driverId = d.driverId
        WHERE s.riskCategory IN ('HIGH_RISK', 'POOR')
        ORDER BY s.score ASC
        """)
    List<DriverPerformanceDto> findHighRiskDrivers(Pageable pageable);
}