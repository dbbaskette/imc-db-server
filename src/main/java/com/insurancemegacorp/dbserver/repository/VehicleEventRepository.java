package com.insurancemegacorp.dbserver.repository;

import com.insurancemegacorp.dbserver.model.VehicleEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VehicleEventRepository extends JpaRepository<VehicleEvent, Long> {

    @Query("""
        SELECT v FROM VehicleEvent v 
        WHERE (:driverId IS NULL OR v.driverId = :driverId)
        AND (:vehicleId IS NULL OR v.vehicleId = :vehicleId)
        AND (:eventType IS NULL OR v.eventType = :eventType)
        AND (:severity IS NULL OR v.severity = :severity)
        AND (:dateFrom IS NULL OR v.eventDate >= :dateFrom)
        AND (:dateTo IS NULL OR v.eventDate <= :dateTo)
        ORDER BY v.eventDate DESC
        """)
    Page<VehicleEvent> findEventsWithFilters(
        @Param("driverId") Long driverId,
        @Param("vehicleId") String vehicleId,
        @Param("eventType") String eventType,
        @Param("severity") String severity,
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo,
        Pageable pageable
    );

    @Query("SELECT COUNT(v) FROM VehicleEvent v WHERE v.eventDate >= :dateFrom")
    long countEventsSince(@Param("dateFrom") LocalDateTime dateFrom);

    @Query("SELECT COUNT(v) FROM VehicleEvent v WHERE v.eventType = 'CRASH'")
    long countCrashEvents();

    @Query("""
        SELECT v FROM VehicleEvent v 
        WHERE v.eventType = 'CRASH' 
        AND (:severity IS NULL OR v.severity = :severity)
        ORDER BY v.eventDate DESC
        """)
    Page<VehicleEvent> findCrashEvents(@Param("severity") String severity, Pageable pageable);

    @Query("""
        SELECT v FROM VehicleEvent v 
        WHERE v.driverId = :driverId 
        AND v.eventDate >= :dateFrom 
        AND v.eventDate <= :dateTo
        ORDER BY v.eventDate DESC
        """)
    List<VehicleEvent> findByDriverAndDateRange(
        @Param("driverId") Long driverId,
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo
    );
}