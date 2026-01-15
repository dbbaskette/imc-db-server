package com.insurancemegacorp.dbserver.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "driver_ml_training_data")
public class DriverMlTrainingData {

    @Id
    @Column(name = "driver_id")
    private Integer driverId;

    @Column(name = "total_events")
    private Long totalEvents;

    @Column(name = "speed_compliance_rate")
    private BigDecimal speedComplianceRate;

    @Column(name = "avg_g_force")
    private BigDecimal avgGForce;

    @Column(name = "harsh_driving_events")
    private Long harshDrivingEvents;

    @Column(name = "phone_usage_rate")
    private BigDecimal phoneUsageRate;

    @Column(name = "speed_variance")
    private BigDecimal speedVariance;

    @Column(name = "avg_speed")
    private BigDecimal avgSpeed;

    @Column(name = "max_speed")
    private BigDecimal maxSpeed;

    @Column(name = "excessive_speeding_count")
    private Long excessiveSpeedingCount;

    @Column(name = "last_updated")
    private OffsetDateTime lastUpdated;

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

    public BigDecimal getAvgGForce() {
        return avgGForce;
    }

    public void setAvgGForce(BigDecimal avgGForce) {
        this.avgGForce = avgGForce;
    }

    public Long getHarshDrivingEvents() {
        return harshDrivingEvents;
    }

    public void setHarshDrivingEvents(Long harshDrivingEvents) {
        this.harshDrivingEvents = harshDrivingEvents;
    }

    public BigDecimal getPhoneUsageRate() {
        return phoneUsageRate;
    }

    public void setPhoneUsageRate(BigDecimal phoneUsageRate) {
        this.phoneUsageRate = phoneUsageRate;
    }

    public BigDecimal getSpeedVariance() {
        return speedVariance;
    }

    public void setSpeedVariance(BigDecimal speedVariance) {
        this.speedVariance = speedVariance;
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

    public Long getExcessiveSpeedingCount() {
        return excessiveSpeedingCount;
    }

    public void setExcessiveSpeedingCount(Long excessiveSpeedingCount) {
        this.excessiveSpeedingCount = excessiveSpeedingCount;
    }

    public OffsetDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(OffsetDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
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