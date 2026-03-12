package com.digischool.emis.model.transport;

import com.digischool.emis.model.BaseEntity;
import java.time.LocalTime;

public class TransportAssignment extends BaseEntity {

    private Long routeId;
    private Long studentId;
    private String pickupPoint;
    private LocalTime pickupTime;
    private LocalTime dropoffTime;
    private boolean active;

    public TransportAssignment() {
        this.active = true;
    }

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getPickupPoint() { return pickupPoint; }
    public void setPickupPoint(String pickupPoint) { this.pickupPoint = pickupPoint; }

    public LocalTime getPickupTime() { return pickupTime; }
    public void setPickupTime(LocalTime pickupTime) { this.pickupTime = pickupTime; }

    public LocalTime getDropoffTime() { return dropoffTime; }
    public void setDropoffTime(LocalTime dropoffTime) { this.dropoffTime = dropoffTime; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "TransportAssignment{id=" + id + ", routeId=" + routeId + ", studentId=" + studentId + "}";
    }
}
