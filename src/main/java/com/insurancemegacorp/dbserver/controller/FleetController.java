package com.insurancemegacorp.dbserver.controller;

import com.insurancemegacorp.dbserver.config.DatabaseInstanceManager;
import com.insurancemegacorp.dbserver.dto.ApiResponse;
import com.insurancemegacorp.dbserver.dto.DriverPerformanceDto;
import com.insurancemegacorp.dbserver.dto.FleetSummaryDto;
import com.insurancemegacorp.dbserver.exception.DatabaseInstanceNotFoundException;
import com.insurancemegacorp.dbserver.service.FleetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/{instance}")
public class FleetController {

    private final FleetService fleetService;
    private final DatabaseInstanceManager databaseInstanceManager;

    public FleetController(FleetService fleetService, DatabaseInstanceManager databaseInstanceManager) {
        this.fleetService = fleetService;
        this.databaseInstanceManager = databaseInstanceManager;
    }

    @GetMapping("/fleet/summary")
    public ResponseEntity<ApiResponse<FleetSummaryDto>> getFleetSummary(@PathVariable String instance) {
        long startTime = System.currentTimeMillis();
        validateInstance(instance);
        
        FleetSummaryDto summary = fleetService.getFleetSummary();
        
        long executionTime = System.currentTimeMillis() - startTime;
        return ResponseEntity.ok(ApiResponse.success(summary).withExecutionTime(executionTime));
    }

    @GetMapping("/drivers/active-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getActiveDriversCount(@PathVariable String instance) {
        long startTime = System.currentTimeMillis();
        validateInstance(instance);
        
        long count = fleetService.getActiveDriversCount();
        Map<String, Long> response = new HashMap<>();
        response.put("active_drivers", count);
        
        long executionTime = System.currentTimeMillis() - startTime;
        return ResponseEntity.ok(ApiResponse.success(response).withExecutionTime(executionTime));
    }

    @GetMapping("/drivers/high-risk-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getHighRiskDriversCount(@PathVariable String instance) {
        long startTime = System.currentTimeMillis();
        validateInstance(instance);
        
        long count = fleetService.getHighRiskDriversCount();
        Map<String, Long> response = new HashMap<>();
        response.put("high_risk_drivers", count);
        
        long executionTime = System.currentTimeMillis() - startTime;
        return ResponseEntity.ok(ApiResponse.success(response).withExecutionTime(executionTime));
    }

    @GetMapping("/drivers/top-performers")
    public ResponseEntity<ApiResponse<List<DriverPerformanceDto>>> getTopPerformers(
            @PathVariable String instance,
            @RequestParam(defaultValue = "10") int limit) {
        
        long startTime = System.currentTimeMillis();
        validateInstance(instance);
        
        List<DriverPerformanceDto> performers = fleetService.getTopPerformers(limit);
        
        long executionTime = System.currentTimeMillis() - startTime;
        return ResponseEntity.ok(ApiResponse.success(performers).withExecutionTime(executionTime));
    }

    @GetMapping("/drivers/high-risk")
    public ResponseEntity<ApiResponse<List<DriverPerformanceDto>>> getHighRiskDrivers(
            @PathVariable String instance,
            @RequestParam(defaultValue = "10") int limit) {
        
        long startTime = System.currentTimeMillis();
        validateInstance(instance);
        
        List<DriverPerformanceDto> drivers = fleetService.getHighRiskDrivers(limit);
        
        long executionTime = System.currentTimeMillis() - startTime;
        return ResponseEntity.ok(ApiResponse.success(drivers).withExecutionTime(executionTime));
    }

    private void validateInstance(String instance) {
        if (!databaseInstanceManager.isInstanceAvailable(instance)) {
            throw new DatabaseInstanceNotFoundException(instance);
        }
    }
}