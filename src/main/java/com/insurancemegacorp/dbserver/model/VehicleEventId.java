package com.insurancemegacorp.dbserver.model;

import java.io.Serializable;
import java.util.Objects;

public class VehicleEventId implements Serializable {
    private Long policyId;
    private Long vehicleId;
    private Integer driverId;
    private Long eventTime;

    public VehicleEventId() {}

    public VehicleEventId(Long policyId, Long vehicleId, Integer driverId, Long eventTime) {
        this.policyId = policyId;
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.eventTime = eventTime;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VehicleEventId that = (VehicleEventId) o;
        return Objects.equals(policyId, that.policyId) &&
               Objects.equals(vehicleId, that.vehicleId) &&
               Objects.equals(driverId, that.driverId) &&
               Objects.equals(eventTime, that.eventTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(policyId, vehicleId, driverId, eventTime);
    }
}
