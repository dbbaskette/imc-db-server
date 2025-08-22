package com.insurancemegacorp.dbserver.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "accidents")
public class Accident {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accident_id")
    private Integer accidentId;
    
    @Column(name = "policy_id", nullable = false)
    private Integer policyId;
    
    @Column(name = "vehicle_id", nullable = false)
    private Integer vehicleId;
    
    @Column(name = "driver_id", nullable = false)
    private Integer driverId;
    
    @Column(name = "accident_timestamp", nullable = false)
    private ZonedDateTime accidentTimestamp;
    
    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;
    
    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;
    
    @Column(name = "g_force", precision = 4, scale = 2)
    private BigDecimal gForce;
    
    @Column(name = "description", columnDefinition = "text")
    private String description;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Accident() {}

    public Accident(Integer policyId, Integer vehicleId, Integer driverId, ZonedDateTime accidentTimestamp) {
        this.policyId = policyId;
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.accidentTimestamp = accidentTimestamp;
    }

    // Getters and Setters
    public Integer getAccidentId() {
        return accidentId;
    }

    public void setAccidentId(Integer accidentId) {
        this.accidentId = accidentId;
    }

    public Integer getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Integer policyId) {
        this.policyId = policyId;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public ZonedDateTime getAccidentTimestamp() {
        return accidentTimestamp;
    }

    public void setAccidentTimestamp(ZonedDateTime accidentTimestamp) {
        this.accidentTimestamp = accidentTimestamp;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getGForce() {
        return gForce;
    }

    public void setGForce(BigDecimal gForce) {
        this.gForce = gForce;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods
    public boolean hasLocation() {
        return latitude != null && longitude != null;
    }

    public boolean isHighImpact() {
        return gForce != null && gForce.compareTo(new BigDecimal("2.0")) > 0;
    }

    public String getLocationString() {
        if (hasLocation()) {
            return latitude + ", " + longitude;
        }
        return "Location not available";
    }
}
