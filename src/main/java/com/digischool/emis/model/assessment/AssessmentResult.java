package com.digischool.emis.model.assessment;

import com.digischool.emis.model.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Stores assessment results (marks) for each student per assessment.
 * Supports both numeric scores and CBC mastery-level descriptors (EE/ME/AE/BE).
 */
public class AssessmentResult extends BaseEntity {

    private Long       assessmentId;
    private Long       studentId;
    private BigDecimal score;           // raw marks obtained
    private BigDecimal percentage;      // score as percentage of maxScore
    private String     cbcMasteryLevel; // EE, ME, AE, BE
    private String     teacherRemarks;
    private boolean    isAbsent;
    private boolean    isExcused;
    private Long       recordedBy;
    private LocalDateTime submittedAt;
    private LocalDateTime markedAt;

    public AssessmentResult() {
        this.isAbsent  = false;
        this.isExcused = false;
    }

    public Long getAssessmentId()                  { return assessmentId; }
    public void setAssessmentId(Long assessmentId) { this.assessmentId = assessmentId; }
    public Long getStudentId()                     { return studentId; }
    public void setStudentId(Long studentId)       { this.studentId = studentId; }
    public BigDecimal getScore()                   { return score; }
    public void setScore(BigDecimal v)             { this.score = v; }
    public BigDecimal getPercentage()              { return percentage; }
    public void setPercentage(BigDecimal v)        { this.percentage = v; }
    public String getCbcMasteryLevel()             { return cbcMasteryLevel; }
    public void setCbcMasteryLevel(String v)       { this.cbcMasteryLevel = v; }
    public String getTeacherRemarks()              { return teacherRemarks; }
    public void setTeacherRemarks(String v)        { this.teacherRemarks = v; }
    public boolean isAbsent()                      { return isAbsent; }
    public void setAbsent(boolean absent)          { isAbsent = absent; }
    public boolean isExcused()                     { return isExcused; }
    public void setExcused(boolean excused)        { isExcused = excused; }
    public Long getRecordedBy()                    { return recordedBy; }
    public void setRecordedBy(Long v)              { this.recordedBy = v; }
    public LocalDateTime getSubmittedAt()          { return submittedAt; }
    public void setSubmittedAt(LocalDateTime v)    { this.submittedAt = v; }
    public LocalDateTime getMarkedAt()             { return markedAt; }
    public void setMarkedAt(LocalDateTime v)       { this.markedAt = v; }
}
