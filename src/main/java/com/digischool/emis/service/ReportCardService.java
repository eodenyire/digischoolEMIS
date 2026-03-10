package com.digischool.emis.service;

import com.digischool.emis.model.assessment.ReportCard;
import java.util.List;
import java.util.Optional;

/**
 * Cross-module service that compiles a CBC-compliant term report card
 * by drawing on AssessmentService, AttendanceService, and CbcService.
 *
 * Equivalent to PowerSchool's "Report Card" + Skyward's "Grade Reporting" module.
 */
public interface ReportCardService {
    /**
     * Generate (or regenerate) a report card for a student in a term.
     * Pulls scores, CBC competencies, attendance, and computes overall grade.
     */
    ReportCard generateReportCard(Long studentId, Long termId, Long classId, Long academicYearId);
    ReportCard updateRemarks(Long reportCardId, String classTeacherRemarks, String principalRemarks);
    void publishReportCard(Long reportCardId, Long publishedBy);
    void publishClassReportCards(Long classId, Long termId, Long publishedBy);
    Optional<ReportCard> getReportCard(Long studentId, Long termId);
    List<ReportCard> getClassReportCards(Long classId, Long termId);
    List<ReportCard> getStudentHistory(Long studentId);
    /** Class position ranking — call after all report cards for a class are generated. */
    void rankClass(Long classId, Long termId);
}
