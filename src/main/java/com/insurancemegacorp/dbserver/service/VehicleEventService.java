package com.insurancemegacorp.dbserver.service;

import com.insurancemegacorp.dbserver.dto.VehicleEventDto;
import com.insurancemegacorp.dbserver.model.VehicleEvent;
import com.insurancemegacorp.dbserver.repository.VehicleEventRepository;
import com.insurancemegacorp.dbserver.util.QueryFilterBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class VehicleEventService {

    private final VehicleEventRepository vehicleEventRepository;
    private final QueryFilterBuilder queryFilterBuilder;
    private final JdbcTemplate jdbcTemplate;

    public VehicleEventService(VehicleEventRepository vehicleEventRepository,
                              QueryFilterBuilder queryFilterBuilder,
                              JdbcTemplate jdbcTemplate) {
        this.vehicleEventRepository = vehicleEventRepository;
        this.queryFilterBuilder = queryFilterBuilder;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Page<VehicleEventDto> findEventsWithFilters(
            String driverIdStr, String vehicleId, String eventType, String severity,
            String dateFromStr, String dateToStr, Integer limit, Integer offset, String orderBy) {

        // Parse and validate filters - only use fields that exist in the database
        Long driverId = queryFilterBuilder.parseDriverId(driverIdStr);
        vehicleId = queryFilterBuilder.sanitizeStringFilter(vehicleId);
        
        // Note: eventType and severity are not stored in the database, so we ignore these filters
        
        LocalDateTime dateFrom = queryFilterBuilder.parseDateTime(dateFromStr);
        LocalDateTime dateTo = queryFilterBuilder.parseDateTime(dateToStr);

        // Build pagination and sorting
        Pageable pageable = queryFilterBuilder.buildPageable(limit, offset, orderBy);

        // Execute query - convert parameters to match new repository method
        Integer driverIdInt = driverId != null ? driverId.intValue() : null;
        Long vehicleIdLong = vehicleId != null ? Long.valueOf(vehicleId) : null;
        Long dateFromLong = dateFrom != null ? dateFrom.toEpochSecond(java.time.ZoneOffset.UTC) * 1000 : null;
        Long dateToLong = dateTo != null ? dateTo.toEpochSecond(java.time.ZoneOffset.UTC) * 1000 : null;
        
        Page<VehicleEvent> events = vehicleEventRepository.findEventsWithFilters(
            driverIdInt, vehicleIdLong, dateFromLong, dateToLong, pageable
        );

        // Convert to DTOs and filter out nulls
        return events.map(event -> {
            VehicleEventDto dto = convertToDto(event);
            return dto != null ? dto : new VehicleEventDto(); // Return empty DTO instead of null
        });
    }

    public long getTelemetryEventsCount(String dateFromStr) {
        LocalDateTime dateFrom = dateFromStr != null ? 
            queryFilterBuilder.parseDateTime(dateFromStr) : 
            LocalDateTime.now().minusDays(30);
            
        Long dateFromLong = dateFrom.toEpochSecond(java.time.ZoneOffset.UTC) * 1000;
        return vehicleEventRepository.countEventsSince(dateFromLong);
    }

    public Map<String, Object> getDatabaseStats() {
        long totalEvents = vehicleEventRepository.count();
        long highGForceEvents = vehicleEventRepository.countHighGForceEvents();
        long recentEvents = vehicleEventRepository.countEventsSince(
            LocalDateTime.now().minusDays(7).toEpochSecond(java.time.ZoneOffset.UTC) * 1000
        );

        Map<String, Object> stats = new HashMap<>();
        stats.put("total_events", totalEvents);
        stats.put("high_gforce_events", highGForceEvents);
        stats.put("events_last_7_days", recentEvents);
        stats.put("average_events_per_day", recentEvents / 7.0);
        
        return stats;
    }

    public Map<String, Object> getTelemetryTableCounts() {
        Map<String, Object> counts = new HashMap<>();
        
        try {
            // Count vehicle_events table
            long vehicleEventsCount = vehicleEventRepository.count();
            counts.put("vehicle_events_count", vehicleEventsCount);
            
            // Count vehicle_telemetry_data_v2 table using JdbcTemplate
            String sql = "SELECT COUNT(*) FROM vehicle_telemetry_data_v2";
            Long telemetryDataCount = jdbcTemplate.queryForObject(sql, Long.class);
            counts.put("vehicle_telemetry_data_v2_count", telemetryDataCount != null ? telemetryDataCount : 0L);
            
            // Add total count
            long totalCount = vehicleEventsCount + (telemetryDataCount != null ? telemetryDataCount : 0L);
            counts.put("total_telemetry_records", totalCount);
            
        } catch (Exception e) {
            // If vehicle_telemetry_data_v2 table doesn't exist, just return vehicle_events count
            long vehicleEventsCount = vehicleEventRepository.count();
            counts.put("vehicle_events_count", vehicleEventsCount);
            counts.put("vehicle_telemetry_data_v2_count", 0L);
            counts.put("total_telemetry_records", vehicleEventsCount);
            counts.put("note", "vehicle_telemetry_data_v2 table not accessible");
        }
        
        return counts;
    }

    public Page<VehicleEventDto> findHighGForceEvents(Integer limit, Integer offset, String orderBy) {
        Pageable pageable = queryFilterBuilder.buildPageable(limit, offset, orderBy);
        Page<VehicleEvent> highGForceEvents = vehicleEventRepository.findHighGForceEvents(pageable);
        
        return highGForceEvents.map(this::convertToDto);
    }

    @Transactional
    public List<VehicleEventDto> batchInsertEvents(List<VehicleEventDto> eventDtos) {
        List<VehicleEvent> events = eventDtos.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());

        List<VehicleEvent> savedEvents = vehicleEventRepository.saveAll(events);
        
        return savedEvents.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private VehicleEventDto convertToDto(VehicleEvent event) {
        if (event == null) {
            return null; // Skip null events
        }
        
        VehicleEventDto dto = new VehicleEventDto();
        // Create a composite event ID from the primary key components
        dto.setEventId(event.getEventTime()); // Use event_time as event ID
        dto.setDriverId(event.getDriverId() != null ? event.getDriverId().longValue() : 0L); // Convert Integer to Long
        dto.setVehicleId(event.getVehicleId() != null ? event.getVehicleId().toString() : "0"); // Convert Long to String
        dto.setEventType("telematics_event"); // Fixed event type since it's not in the DB
        dto.setEventDate(null); // event_time is a timestamp, would need conversion
        dto.setLatitude(event.getGpsLatitude() != null ? java.math.BigDecimal.valueOf(event.getGpsLatitude()) : null);
        dto.setLongitude(event.getGpsLongitude() != null ? java.math.BigDecimal.valueOf(event.getGpsLongitude()) : null);
        dto.setSpeedMph(event.getSpeedMph() != null ? java.math.BigDecimal.valueOf(event.getSpeedMph()) : null);
        dto.setGforce(event.getGForce() != null ? java.math.BigDecimal.valueOf(event.getGForce()) : null);
        dto.setSeverity("unknown"); // Not available in the DB
        dto.setPhoneUsage(false); // Not directly available, could derive from device_screen_on
        dto.setWeatherConditions("unknown"); // Not available in the DB
        return dto;
    }

    private VehicleEvent convertToEntity(VehicleEventDto dto) {
        VehicleEvent event = new VehicleEvent();
        // Map DTO fields to actual database fields
        event.setEventTime(dto.getEventId()); // Use event ID as event_time
        event.setDriverId(dto.getDriverId().intValue()); // Convert Long to Integer
        event.setVehicleId(Long.valueOf(dto.getVehicleId())); // Convert String to Long
        // Note: eventType, eventDate, severity, phoneUsage, weatherConditions don't exist in DB
        event.setGpsLatitude(dto.getLatitude() != null ? dto.getLatitude().doubleValue() : null);
        event.setGpsLongitude(dto.getLongitude() != null ? dto.getLongitude().doubleValue() : null);
        event.setSpeedMph(dto.getSpeedMph() != null ? dto.getSpeedMph().floatValue() : null);
        event.setGForce(dto.getGforce() != null ? dto.getGforce().floatValue() : null);
        // Set some reasonable defaults for required fields that aren't in the DTO
        event.setPolicyId(1L); // Default policy ID - should be provided by the API
        return event;
    }
}