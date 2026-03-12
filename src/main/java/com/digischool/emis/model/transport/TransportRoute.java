package com.digischool.emis.model.transport;

import com.digischool.emis.model.BaseEntity;

public class TransportRoute extends BaseEntity {

    private Long schoolId;
    private String routeCode;
    private String routeName;
    private String description;
    private String vehicleReg;
    private String driverName;
    private String driverPhone;
    private boolean active;

    public TransportRoute() {
        this.active = true;
    }

    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public String getRouteCode() { return routeCode; }
    public void setRouteCode(String routeCode) { this.routeCode = routeCode; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVehicleReg() { return vehicleReg; }
    public void setVehicleReg(String vehicleReg) { this.vehicleReg = vehicleReg; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "TransportRoute{id=" + id + ", routeCode='" + routeCode + "', routeName='" + routeName + "'}";
    }
}
