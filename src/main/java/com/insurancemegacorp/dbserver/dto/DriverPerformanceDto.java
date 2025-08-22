package com.insurancemegacorp.dbserver.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DriverPerformanceDto {
    
    private Long driverId;
    private BigDecimal safetyScore;
    private String riskCategory;
    private BigDecimal speedCompliance;
    private Integer harshEvents;
    private BigDecimal phoneUsage;
    private Integer accidents;
    private Integer totalEvents;
    private LocalDateTime calculationDate;

    public DriverPerformanceDto() {}

    public DriverPerformanceDto(Long driverId, BigDecimal safetyScore, String riskCategory, 
                               BigDecimal speedCompliance, Integer harshEvents, BigDecimal phoneUsage,
                               Integer accidents, Integer totalEvents, LocalDateTime calculationDate) {
        this.driverId = driverId;
        this.safetyScore = safetyScore;
        this.riskCategory = riskCategory;
        this.speedCompliance = speedCompliance;
        this.harshEvents = harshEvents;
        this.phoneUsage = phoneUsage;
        this.accidents = accidents;
        this.totalEvents = totalEvents;
        this.calculationDate = calculationDate;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public BigDecimal getSafetyScore() {
        return safetyScore;
    }

    public void setSafetyScore(BigDecimal safetyScore) {
        this.safetyScore = safetyScore;
    }

    public String getRiskCategory() {
        return riskCategory;
    }

    public void setRiskCategory(String riskCategory) {
        this.riskCategory = riskCategory;
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

    public LocalDateTime getCalculationDate() {
        return calculationDate;
    }

    public void setCalculationDate(LocalDateTime calculationDate) {
        this.calculationDate = calculationDate;
    }
}