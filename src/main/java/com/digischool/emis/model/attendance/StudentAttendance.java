package com.digischool.emis.model.attendance;

import com.digischool.emis.model.BaseEntity;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Records student attendance for a session.
 */
public class StudentAttendance extends BaseEntity {

    private Long sessionId;
    private Long studentId;
    private String status; // PRESENT, ABSENT, LATE, EXCUSED, SICK
    private LocalTime checkInTime;
    private Integer minutesLate;
    private String remarks;
    private Long recordedBy;

    public StudentAttendance() {}

    // Getters and Setters
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalTime checkInTime) { this.checkInTime = checkInTime; }

    public Integer getMinutesLate() { return minutesLate; }
    public void setMinutesLate(Integer minutesLate) { this.minutesLate = minutesLate; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Long getRecordedBy() { return recordedBy; }
    public void setRecordedBy(Long recordedBy) { this.recordedBy = recordedBy; }

    public boolean isPresent() {
        return "PRESENT".equals(status);
    }

    public boolean isAbsent() {
        return "ABSENT".equals(status);
    }
}
