package com.digischool.emis.model.academic;

import com.digischool.emis.model.BaseEntity;
import java.time.LocalDate;

/**
 * Represents an academic year in a school.
 */
public class AcademicYear extends BaseEntity {

    private Long schoolId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isCurrent;
    private String status; // ACTIVE, COMPLETED, UPCOMING

    public AcademicYear() {
        this.status = "ACTIVE";
        this.isCurrent = false;
    }

    // Getters and Setters
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public boolean isCurrent() { return isCurrent; }
    public void setCurrent(boolean current) { isCurrent = current; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
