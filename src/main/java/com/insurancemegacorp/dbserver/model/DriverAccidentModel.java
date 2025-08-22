package com.insurancemegacorp.dbserver.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import com.insurancemegacorp.dbserver.util.JsonMapConverter;

@Entity
@Table(name = "driver_accident_model")
public class DriverAccidentModel {
    
    @Id
    @Column(name = "model_id")
    private String modelId;
    
    @Column(name = "algorithm", length = 50)
    private String algorithm;
    
    @Column(name = "accuracy", precision = 5, scale = 3)
    private BigDecimal accuracy;
    
    @Column(name = "num_iterations")
    private Integer numIterations;
    
    @Column(name = "num_rows_processed")
    private Integer numRowsProcessed;
    
    @Column(name = "feature_weights", columnDefinition = "jsonb")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, BigDecimal> featureWeights;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "status", length = 20)
    private String status;

    public DriverAccidentModel() {}

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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}