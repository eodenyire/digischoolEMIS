package com.digischool.emis.model.lms;

import com.digischool.emis.model.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CourseSubmission extends BaseEntity {

    private Long assignmentId;
    private Long studentId;
    private String content;
    private String fileUrl;
    private LocalDateTime submittedAt;
    private BigDecimal score;
    private boolean late;

    public CourseSubmission() {}

    public boolean isLate(LocalDateTime dueAt) {
        return submittedAt != null && dueAt != null && submittedAt.isAfter(dueAt);
    }

    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }

    public boolean isLate() { return late; }
    public void setLate(boolean late) { this.late = late; }

    @Override
    public String toString() {
        return "CourseSubmission{id=" + id + ", assignmentId=" + assignmentId + ", studentId=" + studentId + ", late=" + late + "}";
    }
}
