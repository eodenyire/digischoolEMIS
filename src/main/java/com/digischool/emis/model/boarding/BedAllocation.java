package com.digischool.emis.model.boarding;

import com.digischool.emis.model.BaseEntity;
import java.time.LocalDate;

public class BedAllocation extends BaseEntity {

    private Long dormitoryId;
    private Long studentId;
    private Long academicYearId;
    private String bedNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private boolean active;

    public BedAllocation() {
        this.active = true;
    }

    public Long getDormitoryId() { return dormitoryId; }
    public void setDormitoryId(Long dormitoryId) { this.dormitoryId = dormitoryId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getAcademicYearId() { return academicYearId; }
    public void setAcademicYearId(Long academicYearId) { this.academicYearId = academicYearId; }

    public String getBedNumber() { return bedNumber; }
    public void setBedNumber(String bedNumber) { this.bedNumber = bedNumber; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "BedAllocation{id=" + id + ", dormitoryId=" + dormitoryId + ", studentId=" + studentId + ", bedNumber='" + bedNumber + "'}";
    }
}
