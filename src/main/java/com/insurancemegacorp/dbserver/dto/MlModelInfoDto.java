package com.insurancemegacorp.dbserver.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class MlModelInfoDto {
    
    private String modelId;
    private String algorithm;
    private BigDecimal accuracy;
    private Integer numIterations;
    private Integer numRowsProcessed;
    private Map<String, BigDecimal> featureWeights;
    private LocalDateTime lastTrained;
    private String status;

    public MlModelInfoDto() {}

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public BigDecimal getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(BigDecimal accuracy) {
        this.accuracy = accuracy;
    }

    public Integer getNumIterations() {
        return numIterations;
    }

    public void setNumIterations(Integer numIterations) {
        this.numIterations = numIterations;
    }

    public Integer getNumRowsProcessed() {
        return numRowsProcessed;
    }

    public void setNumRowsProcessed(Integer numRowsProcessed) {
        this.numRowsProcessed = numRowsProcessed;
    }

    public Map<String, BigDecimal> getFeatureWeights() {
        return featureWeights;
    }

    public void setFeatureWeights(Map<String, BigDecimal> featureWeights) {
        this.featureWeights = featureWeights;
    }

    public LocalDateTime getLastTrained() {
        return lastTrained;
    }

    public void setLastTrained(LocalDateTime lastTrained) {
        this.lastTrained = lastTrained;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}