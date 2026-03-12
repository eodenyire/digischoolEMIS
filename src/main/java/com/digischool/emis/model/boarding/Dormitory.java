package com.digischool.emis.model.boarding;

import com.digischool.emis.model.BaseEntity;

public class Dormitory extends BaseEntity {

    private Long schoolId;
    private String dormCode;
    private String dormName;
    private int capacity;
    private String gender;
    private boolean active;

    public Dormitory() {
        this.active = true;
    }

    public boolean isFull(int currentOccupancy) {
        return currentOccupancy >= capacity;
    }

    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public String getDormCode() { return dormCode; }
    public void setDormCode(String dormCode) { this.dormCode = dormCode; }

    public String getDormName() { return dormName; }
    public void setDormName(String dormName) { this.dormName = dormName; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "Dormitory{id=" + id + ", dormCode='" + dormCode + "', dormName='" + dormName + "', capacity=" + capacity + "}";
    }
}
