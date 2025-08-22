package com.insurancemegacorp.dbserver.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "driver_ml_training_data")
public class DriverMlTrainingData {
    
    @Id
    @Column(name = "driver_id")
    private Integer driverId; // Change to Integer to match database int4
    
    @Column(name = "total_events")
    private Long totalEvents;
    
    @Column(name = "speed_compliance_rate", precision = 10, scale = 6)
    private BigDecimal speedComplianceRate;
    
    @Column(name = "avg_speed_violation", precision = 10, scale = 6)
    private BigDecimal avgSpeedViolation;
    
    @Column(name = "excessive_speeding_count")
    private Long excessiveSpeedingCount;
    
    @Column(name = "avg_g_force", precision = 10, scale = 6)
    private BigDecimal avgGForce;
    
    @Column(name = "max_g_force", precision = 10, scale = 6)
    private BigDecimal maxGForce;
    
    @Column(name = "harsh_driving_events")
    private Long harshDrivingEvents;
    
    @Column(name = "extreme_events")
    private Long extremeEvents;
    
    @Column(name = "speed_variance", precision = 10, scale = 6)
    private BigDecimal speedVariance;
    
    @Column(name = "gforce_variance", precision = 10, scale = 6)
    private BigDecimal gforceVariance;
    
    @Column(name = "phone_usage_rate", precision = 10, scale = 6)
    private BigDecimal phoneUsageRate;
    
    @Column(name = "low_battery_driving_events")
    private Long lowBatteryDrivingEvents;
    
    @Column(name = "avg_speed", precision = 10, scale = 6)
    private BigDecimal avgSpeed;
    
    @Column(name = "max_speed", precision = 10, scale = 6)
    private BigDecimal maxSpeed;
    
    @Column(name = "accident_count")
    private Long accidentCount;
    
    @Column(name = "has_accident")
    private Integer hasAccident;

    public DriverMlTrainingData() {}

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(Long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public BigDecimal getSpeedComplianceRate() {
        return speedComplianceRate;
    }

    public void setSpeedComplianceRate(BigDecimal speedComplianceRate) {
        this.speedComplianceRate = speedComplianceRate;
    }

    public BigDecimal getAvgSpeedViolation() {
        return avgSpeedViolation;
    }

    public void setAvgSpeedViolation(BigDecimal avgSpeedViolation) {
        this.avgSpeedViolation = avgSpeedViolation;
    }

    public Long getExcessiveSpeedingCount() {
        return excessiveSpeedingCount;
    }

    public void setExcessiveSpeedingCount(Long excessiveSpeedingCount) {
        this.excessiveSpeedingCount = excessiveSpeedingCount;
    }

    public BigDecimal getAvgGForce() {
        return avgGForce;
    }

    public void setAvgGForce(BigDecimal avgGForce) {
        this.avgGForce = avgGForce;
    }

    public BigDecimal getMaxGForce() {
        return maxGForce;
    }

    public void setMaxGForce(BigDecimal maxGForce) {
        this.maxGForce = maxGForce;
    }

    public Long getHarshDrivingEvents() {
        return harshDrivingEvents;
    }

    public void setHarshDrivingEvents(Long harshDrivingEvents) {
        this.harshDrivingEvents = harshDrivingEvents;
    }

    public Long getExtremeEvents() {
        return extremeEvents;
    }

    public void setExtremeEvents(Long extremeEvents) {
        this.extremeEvents = extremeEvents;
    }

    public BigDecimal getSpeedVariance() {
        return speedVariance;
    }

    public void setSpeedVariance(BigDecimal speedVariance) {
        this.speedVariance = speedVariance;
    }

    public BigDecimal getGforceVariance() {
        return gforceVariance;
    }

    public void setGforceVariance(BigDecimal gforceVariance) {
        this.gforceVariance = gforceVariance;
    }

    public BigDecimal getPhoneUsageRate() {
        return phoneUsageRate;
    }

    public void setPhoneUsageRate(BigDecimal phoneUsageRate) {
        this.phoneUsageRate = phoneUsageRate;
    }

    public Long getLowBatteryDrivingEvents() {
        return lowBatteryDrivingEvents;
    }

    public void setLowBatteryDrivingEvents(Long lowBatteryDrivingEvents) {
        this.lowBatteryDrivingEvents = lowBatteryDrivingEvents;
    }

    public BigDecimal getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(BigDecimal avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public BigDecimal getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(BigDecimal maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Long getAccidentCount() {
        return accidentCount;
    }

    public void setAccidentCount(Long accidentCount) {
        this.accidentCount = accidentCount;
    }

    public Integer getHasAccident() {
        return hasAccident;
    }

    public void setHasAccident(Integer hasAccident) {
        this.hasAccident = hasAccident;
    }
}