package com.insurancemegacorp.dbserver.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_events")
public class VehicleEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;
    
    @Column(name = "driver_id")
    private Long driverId;
    
    @Column(name = "vehicle_id", length = 50)
    private String vehicleId;
    
    @Column(name = "event_type", length = 20)
    private String eventType;
    
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    
    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;
    
    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;
    
    @Column(name = "speed_mph", precision = 5, scale = 2)
    private BigDecimal speedMph;
    
    @Column(name = "gforce", precision = 5, scale = 3)
    private BigDecimal gforce;
    
    @Column(name = "severity", length = 10)
    private String severity;
    
    @Column(name = "phone_usage")
    private Boolean phoneUsage;
    
    @Column(name = "weather_conditions", length = 50)
    private String weatherConditions;

    public VehicleEvent() {}

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
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

    public BigDecimal getSpeedMph() {
        return speedMph;
    }

    public void setSpeedMph(BigDecimal speedMph) {
        this.speedMph = speedMph;
    }

    public BigDecimal getGforce() {
        return gforce;
    }

    public void setGforce(BigDecimal gforce) {
        this.gforce = gforce;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Boolean getPhoneUsage() {
        return phoneUsage;
    }

    public void setPhoneUsage(Boolean phoneUsage) {
        this.phoneUsage = phoneUsage;
    }

    public String getWeatherConditions() {
        return weatherConditions;
    }

    public void setWeatherConditions(String weatherConditions) {
        this.weatherConditions = weatherConditions;
    }
}