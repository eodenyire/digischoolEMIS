package com.digischool.emis.model.lms;

import com.digischool.emis.model.BaseEntity;

public class Course extends BaseEntity {

    private Long schoolId;
    private Long classStreamId;
    private Long subjectId;
    private Long teacherId;
    private String title;
    private String description;
    private String status;

    public Course() {
        this.status = "active";
    }

    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public Long getClassStreamId() { return classStreamId; }
    public void setClassStreamId(Long classStreamId) { this.classStreamId = classStreamId; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Course{id=" + id + ", title='" + title + "', status='" + status + "'}";
    }
}
