package com.insurancemegacorp.dbserver.controller;

import com.insurancemegacorp.dbserver.config.DatabaseInstanceManager;
import com.insurancemegacorp.dbserver.dto.ApiResponse;
import com.insurancemegacorp.dbserver.exception.DatabaseInstanceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/{instance}")
public class HealthController {

    private final DatabaseInstanceManager databaseInstanceManager;

    public HealthController(DatabaseInstanceManager databaseInstanceManager) {
        this.databaseInstanceManager = databaseInstanceManager;
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health(@PathVariable String instance) {
        long startTime = System.currentTimeMillis();
        
        if (!databaseInstanceManager.isInstanceAvailable(instance)) {
            throw new DatabaseInstanceNotFoundException(instance);
        }

        Map<String, Object> healthData = new HashMap<>();
        healthData.put("instance", instance);
        healthData.put("status", "UP");
        healthData.put("database_connected", databaseInstanceManager.testConnection(instance));

        long executionTime = System.currentTimeMillis() - startTime;
        return ResponseEntity.ok(ApiResponse.success(healthData).withExecutionTime(executionTime));
    }

    @GetMapping("/database/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> databaseInfo(@PathVariable String instance) {
        long startTime = System.currentTimeMillis();
        
        if (!databaseInstanceManager.isInstanceAvailable(instance)) {
            throw new DatabaseInstanceNotFoundException(instance);
        }

        Map<String, Object> dbInfo = new HashMap<>();
        dbInfo.put("instance", instance);
        dbInfo.put("available_instances", databaseInstanceManager.getAvailableInstances());
        dbInfo.put("connection_test", databaseInstanceManager.testConnection(instance));

        long executionTime = System.currentTimeMillis() - startTime;
        return ResponseEntity.ok(ApiResponse.success(dbInfo).withExecutionTime(executionTime));
    }
}