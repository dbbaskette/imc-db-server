package com.insurancemegacorp.dbserver.dto;

public class MlRecalculationResultDto {
    
    private String status;
    private String message;
    private Integer updatedDrivers;
    private Integer executedStatements;
    private Long executionTimeMs;
    private Long timestamp;
    private String jobId;

    public MlRecalculationResultDto() {}

    public MlRecalculationResultDto(String status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getUpdatedDrivers() {
        return updatedDrivers;
    }

    public void setUpdatedDrivers(Integer updatedDrivers) {
        this.updatedDrivers = updatedDrivers;
    }

    public Integer getExecutedStatements() {
        return executedStatements;
    }

    public void setExecutedStatements(Integer executedStatements) {
        this.executedStatements = executedStatements;
    }

    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}