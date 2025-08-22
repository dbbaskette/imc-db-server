package com.insurancemegacorp.dbserver.service;

import com.insurancemegacorp.dbserver.dto.VehicleEventDto;
import com.insurancemegacorp.dbserver.model.VehicleEvent;
import com.insurancemegacorp.dbserver.repository.VehicleEventRepository;
import com.insurancemegacorp.dbserver.util.QueryFilterBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class VehicleEventService {

    private final VehicleEventRepository vehicleEventRepository;
    private final QueryFilterBuilder queryFilterBuilder;

    public VehicleEventService(VehicleEventRepository vehicleEventRepository,
                              QueryFilterBuilder queryFilterBuilder) {
        this.vehicleEventRepository = vehicleEventRepository;
        this.queryFilterBuilder = queryFilterBuilder;
    }

    public Page<VehicleEventDto> findEventsWithFilters(
            String driverIdStr, String vehicleId, String eventType, String severity,
            String dateFromStr, String dateToStr, Integer limit, Integer offset, String orderBy) {

        // Parse and validate filters
        Long driverId = queryFilterBuilder.parseDriverId(driverIdStr);
        vehicleId = queryFilterBuilder.sanitizeStringFilter(vehicleId);
        eventType = queryFilterBuilder.sanitizeStringFilter(eventType);
        severity = queryFilterBuilder.sanitizeStringFilter(severity);
        
        // Validate enum values
        if (!queryFilterBuilder.isValidEventType(eventType)) {
            throw new IllegalArgumentException("Invalid event type: " + eventType);
        }
        if (!queryFilterBuilder.isValidSeverity(severity)) {
            throw new IllegalArgumentException("Invalid severity: " + severity);
        }

        LocalDateTime dateFrom = queryFilterBuilder.parseDateTime(dateFromStr);
        LocalDateTime dateTo = queryFilterBuilder.parseDateTime(dateToStr);

        // Build pagination and sorting
        Pageable pageable = queryFilterBuilder.buildPageable(limit, offset, orderBy);

        // Execute query
        Page<VehicleEvent> events = vehicleEventRepository.findEventsWithFilters(
            driverId, vehicleId, eventType, severity, dateFrom, dateTo, pageable
        );

        // Convert to DTOs
        return events.map(this::convertToDto);
    }

    public long getTelemetryEventsCount(String dateFromStr) {
        LocalDateTime dateFrom = dateFromStr != null ? 
            queryFilterBuilder.parseDateTime(dateFromStr) : 
            LocalDateTime.now().minusDays(30);
            
        return vehicleEventRepository.countEventsSince(dateFrom);
    }

    public Map<String, Object> getDatabaseStats() {
        long totalEvents = vehicleEventRepository.count();
        long crashEvents = vehicleEventRepository.countCrashEvents();
        long recentEvents = vehicleEventRepository.countEventsSince(LocalDateTime.now().minusDays(7));

        return Map.of(
            "total_events", totalEvents,
            "crash_events", crashEvents,
            "events_last_7_days", recentEvents,
            "average_events_per_day", recentEvents / 7.0
        );
    }

    public Page<VehicleEventDto> findCrashEvents(String severity, Integer limit, Integer offset, String orderBy) {
        severity = queryFilterBuilder.sanitizeStringFilter(severity);
        
        if (!queryFilterBuilder.isValidSeverity(severity)) {
            throw new IllegalArgumentException("Invalid severity: " + severity);
        }

        Pageable pageable = queryFilterBuilder.buildPageable(limit, offset, orderBy);
        Page<VehicleEvent> crashes = vehicleEventRepository.findCrashEvents(severity, pageable);
        
        return crashes.map(this::convertToDto);
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
        VehicleEventDto dto = new VehicleEventDto();
        dto.setEventId(event.getEventId());
        dto.setDriverId(event.getDriverId());
        dto.setVehicleId(event.getVehicleId());
        dto.setEventType(event.getEventType());
        dto.setEventDate(event.getEventDate());
        dto.setLatitude(event.getLatitude());
        dto.setLongitude(event.getLongitude());
        dto.setSpeedMph(event.getSpeedMph());
        dto.setGforce(event.getGforce());
        dto.setSeverity(event.getSeverity());
        dto.setPhoneUsage(event.getPhoneUsage());
        dto.setWeatherConditions(event.getWeatherConditions());
        return dto;
    }

    private VehicleEvent convertToEntity(VehicleEventDto dto) {
        VehicleEvent event = new VehicleEvent();
        event.setEventId(dto.getEventId());
        event.setDriverId(dto.getDriverId());
        event.setVehicleId(dto.getVehicleId());
        event.setEventType(dto.getEventType());
        event.setEventDate(dto.getEventDate());
        event.setLatitude(dto.getLatitude());
        event.setLongitude(dto.getLongitude());
        event.setSpeedMph(dto.getSpeedMph());
        event.setGforce(dto.getGforce());
        event.setSeverity(dto.getSeverity());
        event.setPhoneUsage(dto.getPhoneUsage());
        event.setWeatherConditions(dto.getWeatherConditions());
        return event;
    }
}