package com.digischool.emis.model.student;

import com.digischool.emis.model.BaseEntity;
import java.time.LocalDate;

/**
 * Tracks student enrollment in a class for an academic year.
 */
public class StudentEnrollment extends BaseEntity {

    private Long studentId;
    private Long classId;
    private Long academicYearId;
    private LocalDate enrollmentDate;
    private LocalDate exitDate;
    private String status; // ACTIVE, TRANSFERRED, COMPLETED, WITHDRAWN
    private boolean isCurrent;

    public StudentEnrollment() {
        this.status = "ACTIVE";
        this.isCurrent = true;
    }

    // Getters and Setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public Long getAcademicYearId() { return academicYearId; }
    public void setAcademicYearId(Long academicYearId) { this.academicYearId = academicYearId; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public LocalDate getExitDate() { return exitDate; }
    public void setExitDate(LocalDate exitDate) { this.exitDate = exitDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isCurrent() { return isCurrent; }
    public void setCurrent(boolean current) { isCurrent = current; }
}
