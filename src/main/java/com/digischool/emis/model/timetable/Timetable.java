package com.digischool.emis.model.timetable;

import com.digischool.emis.model.BaseEntity;
import java.time.LocalDateTime;

public class Timetable extends BaseEntity {

    private Long schoolId;
    private Long academicYearId;
    private Long termId;
    private String name;
    private String status;
    private LocalDateTime publishedAt;

    public Timetable() {
        this.status = "draft";
    }

    public boolean isPublished() {
        return "published".equals(status);
    }

    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public Long getAcademicYearId() { return academicYearId; }
    public void setAcademicYearId(Long academicYearId) { this.academicYearId = academicYearId; }

    public Long getTermId() { return termId; }
    public void setTermId(Long termId) { this.termId = termId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    @Override
    public String toString() {
        return "Timetable{id=" + id + ", name='" + name + "', status='" + status + "'}";
    }
}
