package com.digischool.emis.model.timetable;

import com.digischool.emis.model.BaseEntity;
import java.time.Duration;
import java.time.LocalTime;

public class TimetableSlot extends BaseEntity {

    private Long timetableId;
    private Long classStreamId;
    private Long subjectId;
    private Long teacherId;
    private int dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;

    public TimetableSlot() {}

    public int getDurationMinutes() {
        if (startTime == null || endTime == null) return 0;
        return (int) Duration.between(startTime, endTime).toMinutes();
    }

    public Long getTimetableId() { return timetableId; }
    public void setTimetableId(Long timetableId) { this.timetableId = timetableId; }

    public Long getClassStreamId() { return classStreamId; }
    public void setClassStreamId(Long classStreamId) { this.classStreamId = classStreamId; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public int getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    @Override
    public String toString() {
        return "TimetableSlot{id=" + id + ", timetableId=" + timetableId + ", dayOfWeek=" + dayOfWeek + ", startTime=" + startTime + "}";
    }
}
