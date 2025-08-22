package com.insurancemegacorp.dbserver.controller;

import com.insurancemegacorp.dbserver.config.DatabaseInstanceManager;
import com.insurancemegacorp.dbserver.dto.ApiResponse;
import com.insurancemegacorp.dbserver.dto.JobStatusDto;
import com.insurancemegacorp.dbserver.dto.MlModelInfoDto;
import com.insurancemegacorp.dbserver.exception.DatabaseInstanceNotFoundException;
import com.insurancemegacorp.dbserver.service.JobTrackingService;
import com.insurancemegacorp.dbserver.service.MlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/{instance}/ml")
public class MlController {

    private final MlService mlService;
    private final JobTrackingService jobTrackingService;
    private final DatabaseInstanceManager databaseInstanceManager;

    public MlController(MlService mlService, JobTrackingService jobTrackingService, 
                       DatabaseInstanceManager databaseInstanceManager) {
        this.mlService = mlService;
        this.jobTrackingService = jobTrackingService;
        this.databaseInstanceManager = databaseInstanceManager;
    }

    @GetMapping("/model-info")
    public ResponseEntity<ApiResponse<MlModelInfoDto>> getModelInfo(@PathVariable String instance) {
        long startTime = System.currentTimeMillis();
        validateInstance(instance);
        
        MlModelInfoDto modelInfo = mlService.getModelInfo();
        
        if (modelInfo == null) {
            long executionTime = System.currentTimeMillis() - startTime;
            return ResponseEntity.notFound()
                    .header("X-Execution-Time", String.valueOf(executionTime))
                    .build();
        }
        
        long executionTime = System.currentTimeMillis() - startTime;
        return ResponseEntity.ok(ApiResponse.success(modelInfo).withExecutionTime(executionTime));
    }

    @PostMapping("/recalculate")
    public ResponseEntity<ApiResponse<Map<String, String>>> startMlRecalculation(@PathVariable String instance) {
        long startTime = System.currentTimeMillis();
        validateInstance(instance);
        
        String jobId = mlService.startMlRecalculation();
        
        Map<String, String> response = new HashMap<>();
        response.put("jobId", jobId);
        response.put("status", "started");
        response.put("message", "ML recalculation started successfully");
        
        long executionTime = System.currentTimeMillis() - startTime;
        return ResponseEntity.accepted()
                .body(ApiResponse.success(response).withExecutionTime(executionTime));
    }

    @GetMapping("/job-status/{jobId}")
    public ResponseEntity<ApiResponse<JobStatusDto>> getJobStatus(
            @PathVariable String instance,
            @PathVariable String jobId) {
        
        long startTime = System.currentTimeMillis();
        validateInstance(instance);
        
        JobStatusDto jobStatus = jobTrackingService.getJobStatus(jobId);
        
        if (jobStatus == null) {
            long executionTime = System.currentTimeMillis() - startTime;
            return ResponseEntity.notFound()
                    .header("X-Execution-Time", String.valueOf(executionTime))
                    .build();
        }
        
        long executionTime = System.currentTimeMillis() - startTime;
        return ResponseEntity.ok(ApiResponse.success(jobStatus).withExecutionTime(executionTime));
    }

    @DeleteMapping("/job-status/{jobId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> cancelJob(
            @PathVariable String instance,
            @PathVariable String jobId) {
        
        long startTime = System.currentTimeMillis();
        validateInstance(instance);
        
        boolean cancelled = jobTrackingService.cancelJob(jobId);
        
        Map<String, String> response = new HashMap<>();
        if (cancelled) {
            response.put("status", "cancelled");
            response.put("message", "Job cancelled successfully");
        } else {
            response.put("status", "not_found_or_completed");
            response.put("message", "Job not found or already completed");
        }
        
        long executionTime = System.currentTimeMillis() - startTime;
        return ResponseEntity.ok(ApiResponse.success(response).withExecutionTime(executionTime));
    }

    private void validateInstance(String instance) {
        if (!databaseInstanceManager.isInstanceAvailable(instance)) {
            throw new DatabaseInstanceNotFoundException(instance);
        }
    }
}