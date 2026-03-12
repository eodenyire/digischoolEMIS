package com.digischool.emis.model.lms;

import com.digischool.emis.model.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CourseAssignment extends BaseEntity {

    private Long courseId;
    private String title;
    private String instructions;
    private LocalDateTime dueAt;
    private BigDecimal maxScore;
    private Long createdBy;

    public CourseAssignment() {}

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public LocalDateTime getDueAt() { return dueAt; }
    public void setDueAt(LocalDateTime dueAt) { this.dueAt = dueAt; }

    public BigDecimal getMaxScore() { return maxScore; }
    public void setMaxScore(BigDecimal maxScore) { this.maxScore = maxScore; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    @Override
    public String toString() {
        return "CourseAssignment{id=" + id + ", courseId=" + courseId + ", title='" + title + "'}";
    }
}
