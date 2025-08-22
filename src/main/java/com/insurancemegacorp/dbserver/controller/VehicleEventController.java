package com.insurancemegacorp.dbserver.controller;

import com.insurancemegacorp.dbserver.config.DatabaseInstanceManager;
import com.insurancemegacorp.dbserver.dto.ApiResponse;
import com.insurancemegacorp.dbserver.dto.VehicleEventDto;
import com.insurancemegacorp.dbserver.exception.DatabaseInstanceNotFoundException;
import com.insurancemegacorp.dbserver.service.VehicleEventService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/{instance}")
public class VehicleEventController {

    private final VehicleEventService vehicleEventService;
    private final DatabaseInstanceManager databaseInstanceManager;

    public VehicleEventController(VehicleEventService vehicleEventService,
                                 DatabaseInstanceManager databaseInstanceManager) {
        this.vehicleEventService = vehicleEventService;
        this.databaseInstanceManager = databaseInstanceManager;
    }

    @GetMapping("/vehicle-events")
    public ResponseEntity<ApiResponse<Page<VehicleEventDto>>> getVehicleEvents(
            @PathVariable String instance,
            @RequestParam(required = false) String driver_id,
            @RequestParam(required = false) String vehicle_id,
            @RequestParam(required = false) String event_type,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String date_from,
            @RequestParam(required = false) String date_to,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) String order_by) {

        long startTime = System.currentTimeMillis();
        validateInstance(instance);

        Page<VehicleEventDto> events = vehicleEventService.findEventsWithFilters(
                driver_id, vehicle_id, event_type, severity, 
                date_from, date_to, limit, offset, order_by
        );

        long executionTime = System.currentTimeMillis() - startTime;
        return ResponseEntity.ok(ApiResponse.success(events).withExecutionTime(executionTime));
    }

    @GetMapping("/vehicle-events/crashes")
    public ResponseEntity<ApiResponse<Page<VehicleEventDto>>> getCrashEvents(
            @PathVariable String instance,
            @RequestParam(required = false) String severity,
            @RequestParam(defaultValue = "50") Integer limit,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) String order_by) {

        long startTime = System.currentTimeMillis();
        validateInstance(instance);

        Page<VehicleEventDto> crashes = vehicleEventService.findCrashEvents(
                severity, limit, offset, order_by
        );

        long executionTime = System.currentTimeMillis() - startTime;
        return ResponseEntity.ok(ApiResponse.success(crashes).withExecutionTime(executionTime));
    }

    @GetMapping("/telemetry/events-count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTelemetryEventsCount(
            @PathVariable String instance,
            @RequestParam(required = false) String date_from) {

        long startTime = System.currentTimeMillis();
        validateInstance(instance);

        long count = vehicleEventService.getTelemetryEventsCount(date_from);
        Map<String, Object> response = new HashMap<>();
        response.put("total_events", count);

        long executionTime = System.currentTimeMillis() - startTime;
        return ResponseEntity.ok(ApiResponse.success(response).withExecutionTime(executionTime));
    }

    @GetMapping("/database/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDatabaseStats(
            @PathVariable String instance) {

        long startTime = System.currentTimeMillis();
        validateInstance(instance);

        Map<String, Object> stats = vehicleEventService.getDatabaseStats();
        
        // Add database info
        stats.put("database_name", "insurance_megacorp");
        stats.put("instance", instance);
        stats.put("last_updated", System.currentTimeMillis());

        long executionTime = System.currentTimeMillis() - startTime;
        return ResponseEntity.ok(ApiResponse.success(stats).withExecutionTime(executionTime));
    }

    @PostMapping("/vehicle-events/batch")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchInsertEvents(
            @PathVariable String instance,
            @RequestBody List<VehicleEventDto> events) {

        long startTime = System.currentTimeMillis();
        validateInstance(instance);

        List<VehicleEventDto> savedEvents = vehicleEventService.batchInsertEvents(events);

        Map<String, Object> response = new HashMap<>();
        response.put("inserted_count", savedEvents.size());
        response.put("status", "success");
        response.put("message", "Events inserted successfully");

        long executionTime = System.currentTimeMillis() - startTime;
        return ResponseEntity.ok(ApiResponse.success(response).withExecutionTime(executionTime));
    }

    private void validateInstance(String instance) {
        if (!databaseInstanceManager.isInstanceAvailable(instance)) {
            throw new DatabaseInstanceNotFoundException(instance);
        }
    }
}