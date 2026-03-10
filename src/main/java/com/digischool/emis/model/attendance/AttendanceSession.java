package com.digischool.emis.model.attendance;

import com.digischool.emis.model.BaseEntity;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * An attendance session represents a single class period for which
 * attendance is being recorded (mirrors the attendance_sessions table).
 */
public class AttendanceSession extends BaseEntity {

    private Long classId;
    private Long subjectId;
    private Long teacherId;
    private LocalDate sessionDate;
    private Integer periodNumber;
    private LocalTime startTime;
    private LocalTime endTime;
    private String sessionType; // CLASS, EXAM, ACTIVITY
    private boolean isFinalized;

    public AttendanceSession() {
        this.sessionType = "CLASS";
        this.isFinalized = false;
    }

    public Long getClassId()                 { return classId; }
    public void setClassId(Long v)           { this.classId = v; }
    public Long getSubjectId()               { return subjectId; }
    public void setSubjectId(Long v)         { this.subjectId = v; }
    public Long getTeacherId()               { return teacherId; }
    public void setTeacherId(Long v)         { this.teacherId = v; }
    public LocalDate getSessionDate()        { return sessionDate; }
    public void setSessionDate(LocalDate v)  { this.sessionDate = v; }
    public Integer getPeriodNumber()         { return periodNumber; }
    public void setPeriodNumber(Integer v)   { this.periodNumber = v; }
    public LocalTime getStartTime()          { return startTime; }
    public void setStartTime(LocalTime v)    { this.startTime = v; }
    public LocalTime getEndTime()            { return endTime; }
    public void setEndTime(LocalTime v)      { this.endTime = v; }
    public String getSessionType()           { return sessionType; }
    public void setSessionType(String v)     { this.sessionType = v; }
    public boolean isFinalized()             { return isFinalized; }
    public void setFinalized(boolean v)      { this.isFinalized = v; }
}
