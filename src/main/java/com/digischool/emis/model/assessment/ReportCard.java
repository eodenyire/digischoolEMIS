package com.digischool.emis.model.assessment;

import com.digischool.emis.model.BaseEntity;
import java.time.LocalDateTime;

/**
 * A compiled end-of-term report card for one student.
 * Aggregates results from all assessments, attendance data,
 * and CBC competency levels — equivalent to what PowerSchool
 * calls a "GPA/transcript record" but extended for CBC.
 */
public class ReportCard extends BaseEntity {

    private Long   studentId;
    private Long   classId;
    private Long   termId;
    private Long   academicYearId;
    private String overallGrade;
    private Double overallPoints;
    private Integer classPosition;
    private Integer classSize;
    private String  overallRemarks;
    private String  classTeacherRemarks;
    private String  principalRemarks;
    private boolean isPublished;
    private LocalDateTime publishedAt;

    public ReportCard() { this.isPublished = false; }

    public Long   getStudentId()              { return studentId; }
    public void   setStudentId(Long v)        { this.studentId = v; }
    public Long   getClassId()                { return classId; }
    public void   setClassId(Long v)          { this.classId = v; }
    public Long   getTermId()                 { return termId; }
    public void   setTermId(Long v)           { this.termId = v; }
    public Long   getAcademicYearId()         { return academicYearId; }
    public void   setAcademicYearId(Long v)   { this.academicYearId = v; }
    public String getOverallGrade()           { return overallGrade; }
    public void   setOverallGrade(String v)   { this.overallGrade = v; }
    public Double getOverallPoints()          { return overallPoints; }
    public void   setOverallPoints(Double v)  { this.overallPoints = v; }
    public Integer getClassPosition()         { return classPosition; }
    public void    setClassPosition(Integer v){ this.classPosition = v; }
    public Integer getClassSize()             { return classSize; }
    public void    setClassSize(Integer v)    { this.classSize = v; }
    public String  getOverallRemarks()        { return overallRemarks; }
    public void    setOverallRemarks(String v){ this.overallRemarks = v; }
    public String  getClassTeacherRemarks()   { return classTeacherRemarks; }
    public void    setClassTeacherRemarks(String v) { this.classTeacherRemarks = v; }
    public String  getPrincipalRemarks()      { return principalRemarks; }
    public void    setPrincipalRemarks(String v) { this.principalRemarks = v; }
    public boolean isPublished()              { return isPublished; }
    public void    setPublished(boolean v)    { this.isPublished = v; }
    public LocalDateTime getPublishedAt()     { return publishedAt; }
    public void    setPublishedAt(LocalDateTime v) { this.publishedAt = v; }
}
