package com.digischool.emis.service.impl;

import com.digischool.emis.dao.ReportCardDao;
import com.digischool.emis.model.assessment.ReportCard;
import com.digischool.emis.model.assessment.AssessmentResult;
import com.digischool.emis.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Compiles end-of-term CBC report cards by aggregating:
 *   1. Academic scores from AssessmentService
 *   2. Attendance data from AttendanceService
 *   3. CBC competency levels from CbcService
 *
 * Equivalent to PowerSchool's Report Card Publisher + Skyward's Grade Reporting.
 */
public class ReportCardServiceImpl implements ReportCardService {

    private static final Logger log = LoggerFactory.getLogger(ReportCardServiceImpl.class);
    private final ReportCardDao    reportCardDao;
    private final AssessmentService assessmentService;
    private final AttendanceService attendanceService;
    private final CbcService        cbcService;
    private final StudentService    studentService;

    public ReportCardServiceImpl(ReportCardDao reportCardDao,
                                  AssessmentService assessmentService,
                                  AttendanceService attendanceService,
                                  CbcService cbcService,
                                  StudentService studentService) {
        this.reportCardDao     = reportCardDao;
        this.assessmentService = assessmentService;
        this.attendanceService = attendanceService;
        this.cbcService        = cbcService;
        this.studentService    = studentService;
    }

    @Override
    public ReportCard generateReportCard(Long studentId, Long termId, Long classId, Long academicYearId) {
        // 1. Collect all assessment results for the student in this term
        List<AssessmentResult> results = assessmentService.getResultsByStudent(studentId, termId);

        // 2. Compute overall percentage average
        double overallScore = results.stream()
                .filter(r -> !r.isAbsent() && r.getPercentage() != null)
                .mapToDouble(r -> r.getPercentage().doubleValue())
                .average()
                .orElse(0.0);

        // 3. Map overall score to CBC grade band
        String gradeBand = assessmentService.scoreToMasteryLevel(overallScore);

        // 4. Attendance for this academic year
        double attendanceRate = attendanceService.getStudentAttendanceRate(studentId, academicYearId);
        int    absentDays     = attendanceService.getAbsentDays(studentId, termId);

        // 5. Build overall remarks
        String remarks = buildRemarks(overallScore, attendanceRate, absentDays, gradeBand);

        // 6. Persist (upsert) the report card
        ReportCard rc = reportCardDao.findByStudentAndTerm(studentId, termId)
                .orElse(new ReportCard());
        rc.setStudentId(studentId);
        rc.setClassId(classId);
        rc.setTermId(termId);
        rc.setAcademicYearId(academicYearId);
        rc.setOverallGrade(gradeBand);
        rc.setOverallPoints(Math.round(overallScore * 100.0) / 100.0);
        rc.setOverallRemarks(remarks);
        rc.setPublished(false);
        return reportCardDao.saveOrUpdate(rc);
    }

    @Override
    public ReportCard updateRemarks(Long reportCardId, String classTeacherRemarks, String principalRemarks) {
        ReportCard rc = reportCardDao.findById(reportCardId)
                .orElseThrow(() -> new IllegalArgumentException("Report card not found: " + reportCardId));
        rc.setClassTeacherRemarks(classTeacherRemarks);
        rc.setPrincipalRemarks(principalRemarks);
        return reportCardDao.saveOrUpdate(rc);
    }

    @Override
    public void publishReportCard(Long reportCardId, Long publishedBy) {
        reportCardDao.publish(reportCardId, publishedBy);
    }

    @Override
    public void publishClassReportCards(Long classId, Long termId, Long publishedBy) {
        reportCardDao.findByClass(classId, termId)
                .forEach(rc -> reportCardDao.publish(rc.getId(), publishedBy));
    }

    @Override public Optional<ReportCard> getReportCard(Long studentId, Long termId) {
        return reportCardDao.findByStudentAndTerm(studentId, termId);
    }
    @Override public List<ReportCard> getClassReportCards(Long classId, Long termId) {
        return reportCardDao.findByClass(classId, termId);
    }
    @Override public List<ReportCard> getStudentHistory(Long studentId) {
        return reportCardDao.findByStudent(studentId);
    }

    @Override
    public void rankClass(Long classId, Long termId) {
        List<ReportCard> cards = reportCardDao.findByClass(classId, termId);
        // Sort descending by overall points
        cards.sort(Comparator.comparingDouble(
                (ReportCard rc) -> rc.getOverallPoints() != null ? rc.getOverallPoints() : 0.0).reversed());
        int classSize = cards.size();
        for (int i = 0; i < cards.size(); i++) {
            ReportCard rc = cards.get(i);
            rc.setClassPosition(i + 1);
            rc.setClassSize(classSize);
            reportCardDao.saveOrUpdate(rc);
        }
    }

    private String buildRemarks(double score, double attendance, int absentDays, String grade) {
        StringBuilder sb = new StringBuilder();
        if ("EE".equals(grade))      sb.append("Excellent performance. Keep it up! ");
        else if ("ME".equals(grade)) sb.append("Good performance. Continue working hard. ");
        else if ("AE".equals(grade)) sb.append("Fair performance. More effort needed. ");
        else                          sb.append("Performance needs significant improvement. ");

        if (attendance >= 90)        sb.append("Excellent attendance record.");
        else if (attendance >= 75)   sb.append("Good attendance. Aim for above 90%.");
        else                         sb.append("Attendance is below acceptable levels. Total absences: ").append(absentDays).append(" days.");
        return sb.toString();
    }
}
