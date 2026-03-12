package com.digischool.emis.model.discipline;

import com.digischool.emis.model.BaseEntity;
import java.time.LocalDate;

public class DisciplineIncident extends BaseEntity {

    private Long schoolId;
    private Long studentId;
    private LocalDate incidentDate;
    private String incidentType;
    private String severity;
    private String description;
    private Long reportedBy;
    private boolean resolved;

    public DisciplineIncident() {
        this.resolved = false;
    }

    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public LocalDate getIncidentDate() { return incidentDate; }
    public void setIncidentDate(LocalDate incidentDate) { this.incidentDate = incidentDate; }

    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String incidentType) { this.incidentType = incidentType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getReportedBy() { return reportedBy; }
    public void setReportedBy(Long reportedBy) { this.reportedBy = reportedBy; }

    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }

    @Override
    public String toString() {
        return "DisciplineIncident{id=" + id + ", studentId=" + studentId + ", incidentType='" + incidentType + "', resolved=" + resolved + "}";
    }
}
