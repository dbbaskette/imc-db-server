package com.insurancemegacorp.dbserver.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicle_events")
@IdClass(VehicleEventId.class)
public class VehicleEvent {
    
    @Id
    @Column(name = "policy_id")
    private Long policyId;
    
    @Id
    @Column(name = "vehicle_id") 
    private Long vehicleId;
    
    @Id
    @Column(name = "driver_id")
    private Integer driverId;
    
    @Id
    @Column(name = "event_time")
    private Long eventTime;
    
    @Column(name = "vin")
    private String vin;
    
    @Column(name = "speed_mph")
    private Float speedMph;
    
    @Column(name = "speed_limit_mph")
    private Float speedLimitMph;
    
    @Column(name = "current_street")
    private String currentStreet;
    
    @Column(name = "g_force")
    private Float gForce;
    
    @Column(name = "gps_latitude")
    private Double gpsLatitude;
    
    @Column(name = "gps_longitude")
    private Double gpsLongitude;
    
    @Column(name = "gps_altitude")
    private Double gpsAltitude;
    
    @Column(name = "gps_speed")
    private Float gpsSpeed;
    
    @Column(name = "gps_bearing")
    private Float gpsBearing;
    
    @Column(name = "gps_accuracy")
    private Float gpsAccuracy;
    
    @Column(name = "gps_satellite_count")
    private Integer gpsSatelliteCount;
    
    @Column(name = "gps_fix_time")
    private Integer gpsFixTime;
    
    @Column(name = "accelerometer_x")
    private Float accelerometerX;
    
    @Column(name = "accelerometer_y")
    private Float accelerometerY;
    
    @Column(name = "accelerometer_z")
    private Float accelerometerZ;
    
    @Column(name = "gyroscope_x")
    private Float gyroscopeX;
    
    @Column(name = "gyroscope_y")
    private Float gyroscopeY;
    
    @Column(name = "gyroscope_z")
    private Float gyroscopeZ;
    
    @Column(name = "magnetometer_x")
    private Float magnetometerX;
    
    @Column(name = "magnetometer_y")
    private Float magnetometerY;
    
    @Column(name = "magnetometer_z")
    private Float magnetometerZ;
    
    @Column(name = "magnetometer_heading")
    private Float magnetometerHeading;
    
    @Column(name = "barometric_pressure")
    private Float barometricPressure;
    
    @Column(name = "device_battery_level")
    private Float deviceBatteryLevel;
    
    @Column(name = "device_signal_strength")
    private Integer deviceSignalStrength;
    
    @Column(name = "device_orientation")
    private String deviceOrientation;
    
    @Column(name = "device_screen_on")
    private Boolean deviceScreenOn;
    
    @Column(name = "device_charging")
    private Boolean deviceCharging;

    public VehicleEvent() {}

    // Getters and Setters
    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Float getSpeedMph() {
        return speedMph;
    }

    public void setSpeedMph(Float speedMph) {
        this.speedMph = speedMph;
    }

    public Float getSpeedLimitMph() {
        return speedLimitMph;
    }

    public void setSpeedLimitMph(Float speedLimitMph) {
        this.speedLimitMph = speedLimitMph;
    }

    public String getCurrentStreet() {
        return currentStreet;
    }

    public void setCurrentStreet(String currentStreet) {
        this.currentStreet = currentStreet;
    }

    public Float getGForce() {
        return gForce;
    }

    public void setGForce(Float gForce) {
        this.gForce = gForce;
    }

    public Double getGpsLatitude() {
        return gpsLatitude;
    }

    public void setGpsLatitude(Double gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    public Double getGpsLongitude() {
        return gpsLongitude;
    }

    public void setGpsLongitude(Double gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    public Double getGpsAltitude() {
        return gpsAltitude;
    }

    public void setGpsAltitude(Double gpsAltitude) {
        this.gpsAltitude = gpsAltitude;
    }

    public Float getGpsSpeed() {
        return gpsSpeed;
    }

    public void setGpsSpeed(Float gpsSpeed) {
        this.gpsSpeed = gpsSpeed;
    }

    public Float getGpsBearing() {
        return gpsBearing;
    }

    public void setGpsBearing(Float gpsBearing) {
        this.gpsBearing = gpsBearing;
    }

    public Float getGpsAccuracy() {
        return gpsAccuracy;
    }

    public void setGpsAccuracy(Float gpsAccuracy) {
        this.gpsAccuracy = gpsAccuracy;
    }

    public Integer getGpsSatelliteCount() {
        return gpsSatelliteCount;
    }

    public void setGpsSatelliteCount(Integer gpsSatelliteCount) {
        this.gpsSatelliteCount = gpsSatelliteCount;
    }

    public Integer getGpsFixTime() {
        return gpsFixTime;
    }

    public void setGpsFixTime(Integer gpsFixTime) {
        this.gpsFixTime = gpsFixTime;
    }

    public Float getAccelerometerX() {
        return accelerometerX;
    }

    public void setAccelerometerX(Float accelerometerX) {
        this.accelerometerX = accelerometerX;
    }

    public Float getAccelerometerY() {
        return accelerometerY;
    }

    public void setAccelerometerY(Float accelerometerY) {
        this.accelerometerY = accelerometerY;
    }

    public Float getAccelerometerZ() {
        return accelerometerZ;
    }

    public void setAccelerometerZ(Float accelerometerZ) {
        this.accelerometerZ = accelerometerZ;
    }

    public Float getGyroscopeX() {
        return gyroscopeX;
    }

    public void setGyroscopeX(Float gyroscopeX) {
        this.gyroscopeX = gyroscopeX;
    }

    public Float getGyroscopeY() {
        return gyroscopeY;
    }

    public void setGyroscopeY(Float gyroscopeY) {
        this.gyroscopeY = gyroscopeY;
    }

    public Float getGyroscopeZ() {
        return gyroscopeZ;
    }

    public void setGyroscopeZ(Float gyroscopeZ) {
        this.gyroscopeZ = gyroscopeZ;
    }

    public Float getMagnetometerX() {
        return magnetometerX;
    }

    public void setMagnetometerX(Float magnetometerX) {
        this.magnetometerX = magnetometerX;
    }

    public Float getMagnetometerY() {
        return magnetometerY;
    }

    public void setMagnetometerY(Float magnetometerY) {
        this.magnetometerY = magnetometerY;
    }

    public Float getMagnetometerZ() {
        return magnetometerZ;
    }

    public void setMagnetometerZ(Float magnetometerZ) {
        this.magnetometerZ = magnetometerZ;
    }

    public Float getMagnetometerHeading() {
        return magnetometerHeading;
    }

    public void setMagnetometerHeading(Float magnetometerHeading) {
        this.magnetometerHeading = magnetometerHeading;
    }

    public Float getBarometricPressure() {
        return barometricPressure;
    }

    public void setBarometricPressure(Float barometricPressure) {
        this.barometricPressure = barometricPressure;
    }

    public Float getDeviceBatteryLevel() {
        return deviceBatteryLevel;
    }

    public void setDeviceBatteryLevel(Float deviceBatteryLevel) {
        this.deviceBatteryLevel = deviceBatteryLevel;
    }

    public Integer getDeviceSignalStrength() {
        return deviceSignalStrength;
    }

    public void setDeviceSignalStrength(Integer deviceSignalStrength) {
        this.deviceSignalStrength = deviceSignalStrength;
    }

    public String getDeviceOrientation() {
        return deviceOrientation;
    }

    public void setDeviceOrientation(String deviceOrientation) {
        this.deviceOrientation = deviceOrientation;
    }

    public Boolean getDeviceScreenOn() {
        return deviceScreenOn;
    }

    public void setDeviceScreenOn(Boolean deviceScreenOn) {
        this.deviceScreenOn = deviceScreenOn;
    }

    public Boolean getDeviceCharging() {
        return deviceCharging;
    }

    public void setDeviceCharging(Boolean deviceCharging) {
        this.deviceCharging = deviceCharging;
    }
}