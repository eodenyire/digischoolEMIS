package com.digischool.emis.model.analytics;

import com.digischool.emis.model.BaseEntity;
import java.time.LocalDateTime;

/**
 * A computed analytics snapshot for one student per term.
 * The AnalyticsService populates these from live assessment,
 * attendance, and finance data — enabling AI-style risk flagging
 * similar to Infinite Campus's "Early Warning" system.
 */
public class StudentPerformanceSnapshot extends BaseEntity {

    private Long   studentId;
    private Long   academicYearId;
    private Long   termId;
    private Double overallScore;        // 0–100 weighted average
    private Double attendanceRate;      // 0–100 %
    private Double assignmentCompletion;// 0–100 %
    private String trend;               // IMPROVING | STABLE | DECLINING
    private String riskLevel;           // LOW | MEDIUM | HIGH | CRITICAL
    private Double dropoutRiskScore;    // 0–100 (higher = more at risk)
    private String predictedGrade;      // e.g. "B+" or "EE" (CBC)
    private String keyInsights;         // Short text summary
    private LocalDateTime computedAt;

    public StudentPerformanceSnapshot() {}

    public Long   getStudentId()              { return studentId; }
    public void   setStudentId(Long v)        { this.studentId = v; }
    public Long   getAcademicYearId()         { return academicYearId; }
    public void   setAcademicYearId(Long v)   { this.academicYearId = v; }
    public Long   getTermId()                 { return termId; }
    public void   setTermId(Long v)           { this.termId = v; }
    public Double getOverallScore()           { return overallScore; }
    public void   setOverallScore(Double v)   { this.overallScore = v; }
    public Double getAttendanceRate()         { return attendanceRate; }
    public void   setAttendanceRate(Double v) { this.attendanceRate = v; }
    public Double getAssignmentCompletion()   { return assignmentCompletion; }
    public void   setAssignmentCompletion(Double v) { this.assignmentCompletion = v; }
    public String getTrend()                  { return trend; }
    public void   setTrend(String v)          { this.trend = v; }
    public String getRiskLevel()              { return riskLevel; }
    public void   setRiskLevel(String v)      { this.riskLevel = v; }
    public Double getDropoutRiskScore()       { return dropoutRiskScore; }
    public void   setDropoutRiskScore(Double v){ this.dropoutRiskScore = v; }
    public String getPredictedGrade()         { return predictedGrade; }
    public void   setPredictedGrade(String v) { this.predictedGrade = v; }
    public String getKeyInsights()            { return keyInsights; }
    public void   setKeyInsights(String v)    { this.keyInsights = v; }
    public LocalDateTime getComputedAt()      { return computedAt; }
    public void   setComputedAt(LocalDateTime v){ this.computedAt = v; }
}
