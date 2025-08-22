package com.insurancemegacorp.dbserver.repository;

import com.insurancemegacorp.dbserver.model.VehicleEvent;
import com.insurancemegacorp.dbserver.model.VehicleEventId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleEventRepository extends JpaRepository<VehicleEvent, VehicleEventId> {

    @Query("""
        SELECT v FROM VehicleEvent v 
        WHERE (:driverId IS NULL OR v.driverId = :driverId)
        AND (:vehicleId IS NULL OR v.vehicleId = :vehicleId)
        AND (:dateFrom IS NULL OR v.eventTime >= :dateFrom)
        AND (:dateTo IS NULL OR v.eventTime <= :dateTo)
        ORDER BY v.eventTime DESC
        """)
    Page<VehicleEvent> findEventsWithFilters(
        @Param("driverId") Integer driverId,
        @Param("vehicleId") Long vehicleId,
        @Param("dateFrom") Long dateFrom,
        @Param("dateTo") Long dateTo,
        Pageable pageable
    );

    @Query("SELECT COUNT(v) FROM VehicleEvent v WHERE v.eventTime >= :dateFrom")
    long countEventsSince(@Param("dateFrom") Long dateFrom);

    @Query("SELECT COUNT(v) FROM VehicleEvent v WHERE v.gForce > 2.0")
    long countHighGForceEvents();

    @Query("""
        SELECT v FROM VehicleEvent v 
        WHERE v.gForce > 2.0 
        ORDER BY v.eventTime DESC
        """)
    Page<VehicleEvent> findHighGForceEvents(Pageable pageable);

    @Query("""
        SELECT v FROM VehicleEvent v 
        WHERE v.driverId = :driverId 
        AND v.eventTime >= :dateFrom 
        AND v.eventTime <= :dateTo
        ORDER BY v.eventTime DESC
        """)
    List<VehicleEvent> findByDriverAndDateRange(
        @Param("driverId") Integer driverId,
        @Param("dateFrom") Long dateFrom,
        @Param("dateTo") Long dateTo
    );
}