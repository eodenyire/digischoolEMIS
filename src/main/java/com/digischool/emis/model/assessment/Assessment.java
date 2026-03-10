package com.digischool.emis.model.assessment;

import com.digischool.emis.model.BaseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents an assessment (quiz, exam, assignment, project, etc.)
 */
public class Assessment extends BaseEntity {

    private Long classId;
    private Long subjectId;
    private Long teacherId;
    private Long termId;
    private Long assessmentTypeId;
    private String title;
    private String description;
    private double totalMarks;
    private LocalDate assessmentDate;
    private LocalDate dueDate;
    private LocalDateTime submissionDeadline;
    private boolean isPublished;

    public Assessment() {
        this.isPublished = false;
    }

    // Getters and Setters
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public Long getTermId() { return termId; }
    public void setTermId(Long termId) { this.termId = termId; }

    public Long getAssessmentTypeId() { return assessmentTypeId; }
    public void setAssessmentTypeId(Long assessmentTypeId) {
        this.assessmentTypeId = assessmentTypeId;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getTotalMarks() { return totalMarks; }
    public void setTotalMarks(double totalMarks) { this.totalMarks = totalMarks; }

    public LocalDate getAssessmentDate() { return assessmentDate; }
    public void setAssessmentDate(LocalDate assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getSubmissionDeadline() { return submissionDeadline; }
    public void setSubmissionDeadline(LocalDateTime submissionDeadline) {
        this.submissionDeadline = submissionDeadline;
    }

    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean published) { isPublished = published; }
}
