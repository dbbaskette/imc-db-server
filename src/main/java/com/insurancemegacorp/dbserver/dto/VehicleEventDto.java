package com.insurancemegacorp.dbserver.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VehicleEventDto {
    
    private Long eventId;
    private Long driverId;
    private String vehicleId;
    private String eventType;
    private LocalDateTime eventDate;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal speedMph;
    private BigDecimal gforce;
    private String severity;
    private Boolean phoneUsage;
    private String weatherConditions;

    public VehicleEventDto() {}

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