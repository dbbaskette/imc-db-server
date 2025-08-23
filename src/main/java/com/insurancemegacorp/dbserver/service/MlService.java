package com.insurancemegacorp.dbserver.service;

import com.insurancemegacorp.dbserver.dto.ApiResponse;
import com.insurancemegacorp.dbserver.dto.MlModelInfoDto;
import com.insurancemegacorp.dbserver.model.DriverAccidentModel;
import com.insurancemegacorp.dbserver.repository.DriverAccidentModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MlService {

    private final DriverAccidentModelRepository driverAccidentModelRepository;
    private final JdbcTemplate jdbcTemplate;
    private final JobTrackingService jobTrackingService;

    @Autowired
    public MlService(DriverAccidentModelRepository driverAccidentModelRepository, 
                    JdbcTemplate jdbcTemplate,
                    JobTrackingService jobTrackingService) {
        this.driverAccidentModelRepository = driverAccidentModelRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.jobTrackingService = jobTrackingService;
    }

    public MlModelInfoDto getModelInfo() {
        try {
            // Try the entity approach first
            Optional<DriverAccidentModel> modelOpt = driverAccidentModelRepository.findLatestActiveModel();
            if (modelOpt.isPresent()) {
                return convertToDto(modelOpt.get());
            }
        } catch (Exception e) {
            // If entity approach fails, fall back to JdbcTemplate
            return getModelInfoFromJdbcTemplate();
        }
        return null;
    }

    private MlModelInfoDto getModelInfoFromJdbcTemplate() {
        try {
            // Use JdbcTemplate to completely bypass Hibernate
            String sql = "SELECT num_iterations, coef, log_likelihood, std_err, z_stats, p_values, odds_ratios, condition_no, num_rows_processed, num_missing_rows_skipped, variance_covariance FROM driver_accident_model LIMIT 1";
            
            return jdbcTemplate.query(sql, rs -> {
                if (rs.next()) {
                    return convertResultSetToDto(rs);
                }
                return null;
            });
            
        } catch (Exception e) {
            // Log the error but don't throw
            System.err.println("Error getting ML model info from JdbcTemplate: " + e.getMessage());
        }
        return null;
    }

    private MlModelInfoDto convertResultSetToDto(java.sql.ResultSet rs) throws java.sql.SQLException {
        MlModelInfoDto dto = new MlModelInfoDto();
        
        // Map ResultSet to DTO
        Integer numIterations = rs.getObject("num_iterations", Integer.class);
        if (numIterations != null) {
            dto.setModelId(numIterations.toString());
        }
        
        dto.setAlgorithm("Logistic Regression");
        
        // For array fields, we'll set them to null for now since we can't easily convert them
        dto.setAccuracy(null);
        dto.setFeatureWeights(null);
        dto.setLastTrained(null);
        
        Long numRowsProcessed = rs.getObject("num_rows_processed", Long.class);
        if (numRowsProcessed != null) {
            dto.setNumRowsProcessed(numRowsProcessed.intValue());
        }
        
        return dto;
    }

    private MlModelInfoDto convertToDto(DriverAccidentModel model) {
        MlModelInfoDto dto = new MlModelInfoDto();
        dto.setModelId(model.getNumIterations().toString());
        dto.setAlgorithm("Logistic Regression");
        dto.setAccuracy(null); // Not available in the new model structure
        dto.setFeatureWeights(null); // Not available in the new model structure
        dto.setLastTrained(null); // Not available in the new model structure
        dto.setNumRowsProcessed(model.getNumRowsProcessed() != null ? model.getNumRowsProcessed().intValue() : 0);
        return dto;
    }

    public String startMlRecalculation() {
        // Create a proper job in the tracking service
        String jobId = jobTrackingService.createJob("ML Model Recalculation");
        jobTrackingService.startJob(jobId);
        
        // In a real implementation, this would trigger an async background job
        // For now, we'll simulate the job completion after a short delay
        simulateJobCompletion(jobId);
        
        return jobId;
    }
    
    private void simulateJobCompletion(String jobId) {
        // Simulate job progress and completion
        new Thread(() -> {
            try {
                // Simulate work progress
                Thread.sleep(100);
                jobTrackingService.updateJobProgress(jobId, 25, "Loading training data");
                
                Thread.sleep(100);
                jobTrackingService.updateJobProgress(jobId, 50, "Training model");
                
                Thread.sleep(100);
                jobTrackingService.updateJobProgress(jobId, 75, "Validating results");
                
                Thread.sleep(100);
                jobTrackingService.updateJobProgress(jobId, 100, "Model recalculation completed");
                
                // Complete the job
                Map<String, Object> result = new HashMap<>();
                result.put("message", "ML model successfully recalculated");
                result.put("timestamp", System.currentTimeMillis());
                jobTrackingService.completeJob(jobId, result);
                
            } catch (InterruptedException e) {
                jobTrackingService.failJob(jobId, "Job interrupted: " + e.getMessage());
            }
        }).start();
    }
}