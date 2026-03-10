package com.digischool.emis.model.assessment;

import com.digischool.emis.model.BaseEntity;
import java.time.LocalDateTime;

/**
 * Stores assessment results (marks) for each student per assessment.
 */
public class AssessmentResult extends BaseEntity {

    private Long assessmentId;
    private Long studentId;
    private Double marksObtained;
    private String grade;
    private Double gradePoints;
    private Double percentage;
    private String teacherRemarks;
    private boolean isAbsent;
    private boolean isExcused;
    private LocalDateTime submittedAt;
    private LocalDateTime markedAt;
    private Long markedBy;

    public AssessmentResult() {
        this.isAbsent = false;
        this.isExcused = false;
    }

    // Getters and Setters
    public Long getAssessmentId() { return assessmentId; }
    public void setAssessmentId(Long assessmentId) { this.assessmentId = assessmentId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Double getMarksObtained() { return marksObtained; }
    public void setMarksObtained(Double marksObtained) { this.marksObtained = marksObtained; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public Double getGradePoints() { return gradePoints; }
    public void setGradePoints(Double gradePoints) { this.gradePoints = gradePoints; }

    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }

    public String getTeacherRemarks() { return teacherRemarks; }
    public void setTeacherRemarks(String teacherRemarks) { this.teacherRemarks = teacherRemarks; }

    public boolean isAbsent() { return isAbsent; }
    public void setAbsent(boolean absent) { isAbsent = absent; }

    public boolean isExcused() { return isExcused; }
    public void setExcused(boolean excused) { isExcused = excused; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getMarkedAt() { return markedAt; }
    public void setMarkedAt(LocalDateTime markedAt) { this.markedAt = markedAt; }

    public Long getMarkedBy() { return markedBy; }
    public void setMarkedBy(Long markedBy) { this.markedBy = markedBy; }
}
