package com.insurancemegacorp.dbserver.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Integer vehicleId;
    
    @Column(name = "policy_id", nullable = false)
    private Integer policyId;
    
    @Column(name = "vin", length = 17, nullable = false, unique = true)
    private String vin;
    
    @Column(name = "make", length = 50, nullable = false)
    private String make;
    
    @Column(name = "model", length = 50, nullable = false)
    private String model;
    
    @Column(name = "year", nullable = false)
    private Integer year;
    
    @Column(name = "color", length = 30)
    private String color;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Vehicle() {}

    public Vehicle(Integer policyId, String vin, String make, String model, Integer year, String color) {
        this.policyId = policyId;
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
    }

    // Getters and Setters
    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Integer policyId) {
        this.policyId = policyId;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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
    public String getFullDescription() {
        return year + " " + make + " " + model + (color != null ? " " + color : "");
    }

    public int getAge() {
        if (year != null) {
            return LocalDateTime.now().getYear() - year;
        }
        return 0;
    }
}
