package com.insurancemegacorp.dbserver.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "driver_ml_training_data")
public class DriverMlTrainingData {
    
    @Id
    @Column(name = "driver_id")
    private Long driverId;
    
    @Column(name = "speed_compliance", precision = 5, scale = 2)
    private BigDecimal speedCompliance;
    
    @Column(name = "harsh_events")
    private Integer harshEvents;
    
    @Column(name = "phone_usage", precision = 5, scale = 2)
    private BigDecimal phoneUsage;
    
    @Column(name = "accidents")
    private Integer accidents;
    
    @Column(name = "total_events")
    private Integer totalEvents;
    
    @Column(name = "avg_gforce", precision = 5, scale = 2)
    private BigDecimal avgGforce;
    
    @Column(name = "speed_variance", precision = 5, scale = 2)
    private BigDecimal speedVariance;

    public DriverMlTrainingData() {}

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public BigDecimal getSpeedCompliance() {
        return speedCompliance;
    }

    public void setSpeedCompliance(BigDecimal speedCompliance) {
        this.speedCompliance = speedCompliance;
    }

    public Integer getHarshEvents() {
        return harshEvents;
    }

    public void setHarshEvents(Integer harshEvents) {
        this.harshEvents = harshEvents;
    }

    public BigDecimal getPhoneUsage() {
        return phoneUsage;
    }

    public void setPhoneUsage(BigDecimal phoneUsage) {
        this.phoneUsage = phoneUsage;
    }

    public Integer getAccidents() {
        return accidents;
    }

    public void setAccidents(Integer accidents) {
        this.accidents = accidents;
    }

    public Integer getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(Integer totalEvents) {
        this.totalEvents = totalEvents;
    }

    public BigDecimal getAvgGforce() {
        return avgGforce;
    }

    public void setAvgGforce(BigDecimal avgGforce) {
        this.avgGforce = avgGforce;
    }

    public BigDecimal getSpeedVariance() {
        return speedVariance;
    }

    public void setSpeedVariance(BigDecimal speedVariance) {
        this.speedVariance = speedVariance;
    }
}