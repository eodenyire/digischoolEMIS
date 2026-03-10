package com.digischool.emis.model.assessment;

import com.digischool.emis.model.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents an assessment (quiz, exam, assignment, project, practical, portfolio, etc.)
 * Supports both traditional numeric scoring and CBC mastery-level evaluation.
 */
public class Assessment extends BaseEntity {

    private Long   schoolId;
    private Long   classId;
    private Long   subjectId;
    private Long   teacherId;
    private Long   termId;
    private String assessmentType;   // QUIZ, ASSIGNMENT, EXAM, PROJECT, PRACTICAL, PORTFOLIO
    private String title;
    private String description;
    private BigDecimal maxScore;
    private BigDecimal weightPercent; // contribution weight for overall score
    private LocalDate  assessmentDate;
    private LocalDate  dueDate;
    private LocalDateTime submissionDeadline;
    private boolean isCbcBased;
    private boolean isPublished;

    public Assessment() {
        this.assessmentType = "ASSIGNMENT";
        this.isCbcBased     = true;
        this.isPublished    = false;
        this.maxScore       = BigDecimal.valueOf(100);
        this.weightPercent  = BigDecimal.valueOf(100);
    }

    public Long getSchoolId()                      { return schoolId; }
    public void setSchoolId(Long v)                { this.schoolId = v; }
    public Long getClassId()                       { return classId; }
    public void setClassId(Long classId)           { this.classId = classId; }
    public Long getSubjectId()                     { return subjectId; }
    public void setSubjectId(Long subjectId)       { this.subjectId = subjectId; }
    public Long getTeacherId()                     { return teacherId; }
    public void setTeacherId(Long teacherId)       { this.teacherId = teacherId; }
    public Long getTermId()                        { return termId; }
    public void setTermId(Long termId)             { this.termId = termId; }
    public String getAssessmentType()              { return assessmentType; }
    public void setAssessmentType(String v)        { this.assessmentType = v; }
    public String getTitle()                       { return title; }
    public void setTitle(String title)             { this.title = title; }
    public String getDescription()                 { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getMaxScore()                { return maxScore; }
    public void setMaxScore(BigDecimal v)          { this.maxScore = v; }
    public BigDecimal getWeightPercent()           { return weightPercent; }
    public void setWeightPercent(BigDecimal v)     { this.weightPercent = v; }
    public LocalDate getAssessmentDate()           { return assessmentDate; }
    public void setAssessmentDate(LocalDate d)     { this.assessmentDate = d; }
    public LocalDate getDueDate()                  { return dueDate; }
    public void setDueDate(LocalDate dueDate)      { this.dueDate = dueDate; }
    public LocalDateTime getSubmissionDeadline()   { return submissionDeadline; }
    public void setSubmissionDeadline(LocalDateTime v) { this.submissionDeadline = v; }
    public boolean isCbcBased()                    { return isCbcBased; }
    public void setCbcBased(boolean v)             { this.isCbcBased = v; }
    public boolean isPublished()                   { return isPublished; }
    public void setPublished(boolean published)    { isPublished = published; }
}
