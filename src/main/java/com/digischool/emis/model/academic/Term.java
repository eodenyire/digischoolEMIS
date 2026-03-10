package com.digischool.emis.model.academic;

import com.digischool.emis.model.BaseEntity;
import java.time.LocalDate;

/**
 * Represents an academic Term (school term within an academic year).
 * Kenya schools typically have 3 terms per academic year.
 */
public class Term extends BaseEntity {

    private Long academicYearId;
    private String name;
    private int termNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isCurrent;
    private String status; // ACTIVE, COMPLETED, UPCOMING

    public Term() {
        this.status = "ACTIVE";
        this.isCurrent = false;
    }

    // Getters and Setters
    public Long getAcademicYearId() { return academicYearId; }
    public void setAcademicYearId(Long academicYearId) { this.academicYearId = academicYearId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getTermNumber() { return termNumber; }
    public void setTermNumber(int termNumber) { this.termNumber = termNumber; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public boolean isCurrent() { return isCurrent; }
    public void setCurrent(boolean current) { isCurrent = current; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
