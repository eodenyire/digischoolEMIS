package com.digischool.emis.model.attendance;

import com.digischool.emis.model.BaseEntity;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Records student attendance for a session.
 */
public class StudentAttendance extends BaseEntity {

    private Long      sessionId;
    private Long      studentId;
    private String    status;         // PRESENT, ABSENT, LATE, EXCUSED, SICK
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private String    excuseReason;
    private Long      recordedBy;
    private boolean   parentNotified;

    public StudentAttendance() { this.parentNotified = false; }

    public Long getSessionId()                  { return sessionId; }
    public void setSessionId(Long sessionId)    { this.sessionId = sessionId; }
    public Long getStudentId()                  { return studentId; }
    public void setStudentId(Long studentId)    { this.studentId = studentId; }
    public String getStatus()                   { return status; }
    public void setStatus(String status)        { this.status = status; }
    public LocalTime getArrivalTime()           { return arrivalTime; }
    public void setArrivalTime(LocalTime v)     { this.arrivalTime = v; }
    public LocalTime getDepartureTime()         { return departureTime; }
    public void setDepartureTime(LocalTime v)   { this.departureTime = v; }
    public String getExcuseReason()             { return excuseReason; }
    public void setExcuseReason(String v)       { this.excuseReason = v; }
    public Long getRecordedBy()                 { return recordedBy; }
    public void setRecordedBy(Long recordedBy)  { this.recordedBy = recordedBy; }
    public boolean isParentNotified()           { return parentNotified; }
    public void setParentNotified(boolean v)    { this.parentNotified = v; }

    public boolean isPresent() { return "PRESENT".equals(status); }
    public boolean isAbsent()  { return "ABSENT".equals(status); }
    public boolean isLate()    { return "LATE".equals(status); }
}
