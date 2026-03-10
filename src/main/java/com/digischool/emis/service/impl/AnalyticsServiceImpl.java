package com.digischool.emis.service.impl;

import com.digischool.emis.dao.AnalyticsDao;
import com.digischool.emis.model.analytics.StudentPerformanceSnapshot;
import com.digischool.emis.model.student.Student;
import com.digischool.emis.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Analytics engine — combines assessment, attendance, and historical data
 * to compute dropout risk scores and performance predictions.
 *
 * Mirrors the "Early Warning System" in Infinite Campus and
 * the "Analytics & Reporting" engine in Alma SIS.
 */
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsServiceImpl.class);
    private final AnalyticsDao      analyticsDao;
    private final StudentService    studentService;
    private final AssessmentService assessmentService;
    private final AttendanceService attendanceService;

    public AnalyticsServiceImpl(AnalyticsDao analyticsDao, StudentService studentService,
                                 AssessmentService assessmentService, AttendanceService attendanceService) {
        this.analyticsDao      = analyticsDao;
        this.studentService    = studentService;
        this.assessmentService = assessmentService;
        this.attendanceService = attendanceService;
    }

    @Override
    public StudentPerformanceSnapshot computeSnapshot(Long studentId, Long termId, Long academicYearId) {
        // 1. Average score across all assessments this term
        var results = assessmentService.getResultsByStudent(studentId, termId);
        double overallScore = results.stream()
                .filter(r -> !r.isAbsent() && r.getPercentage() != null)
                .mapToDouble(r -> r.getPercentage().doubleValue())
                .average().orElse(0.0);

        // 2. Assignment completion rate
        double completionRate = results.isEmpty() ? 0.0
                : 100.0 * results.stream().filter(r -> !r.isAbsent()).count() / results.size();

        // 3. Attendance
        double attendanceRate = attendanceService.getStudentAttendanceRate(studentId, academicYearId);

        // 4. Trend (compare to previous stored snapshot)
        String trend = computeTrend(studentId, termId, overallScore);

        // 5. Dropout risk
        double dropoutRisk = computeDropoutRisk(overallScore, attendanceRate, completionRate);
        String riskLevel = riskBand(dropoutRisk);

        // 6. Predicted grade band
        String predicted = predictGradeBand(overallScore);

        // 7. Key insight summary
        String insights = buildInsights(overallScore, attendanceRate, completionRate, riskLevel);

        StudentPerformanceSnapshot snap = new StudentPerformanceSnapshot();
        snap.setStudentId(studentId);
        snap.setTermId(termId);
        snap.setAcademicYearId(academicYearId);
        snap.setOverallScore(Math.round(overallScore * 100.0) / 100.0);
        snap.setAttendanceRate(Math.round(attendanceRate * 100.0) / 100.0);
        snap.setAssignmentCompletion(Math.round(completionRate * 100.0) / 100.0);
        snap.setTrend(trend);
        snap.setRiskLevel(riskLevel);
        snap.setDropoutRiskScore(Math.round(dropoutRisk * 100.0) / 100.0);
        snap.setPredictedGrade(predicted);
        snap.setKeyInsights(insights);
        snap.setComputedAt(LocalDateTime.now());

        analyticsDao.upsert(snap);
        return snap;
    }

    @Override
    public void computeSchoolSnapshots(Long schoolId, Long termId, Long academicYearId) {
        List<Student> students = studentService.getActiveStudents(schoolId);
        log.info("Computing analytics snapshots for {} students in school {}", students.size(), schoolId);
        students.forEach(s -> {
            try { computeSnapshot(s.getId(), termId, academicYearId); }
            catch (Exception ex) { log.warn("Snapshot failed for student {}: {}", s.getId(), ex.getMessage()); }
        });
    }

    @Override public List<StudentPerformanceSnapshot> getAtRiskStudents(Long schoolId, Long termId) {
        return analyticsDao.findAtRiskStudents(schoolId, termId);
    }
    @Override public List<StudentPerformanceSnapshot> getClassAnalytics(Long classId, Long termId) {
        return analyticsDao.findByClass(classId, termId);
    }
    @Override public AnalyticsDao.DashboardStats getDashboardStats(Long schoolId, Long termId) {
        return analyticsDao.getDashboardStats(schoolId, termId);
    }

    @Override
    public String predictGradeBand(double overallScore) {
        if (overallScore >= 80) return "EE";
        if (overallScore >= 60) return "ME";
        if (overallScore >= 40) return "AE";
        return "BE";
    }

    /**
     * Weighted risk model:
     *  - Low academic score contributes 50%
     *  - Low attendance contributes 35%
     *  - Low assignment completion contributes 15%
     */
    @Override
    public double computeDropoutRisk(double scoreRate, double attendanceRate, double feeComplianceRate) {
        double scoreRisk      = Math.max(0, 100 - scoreRate)      * 0.50;
        double attendanceRisk = Math.max(0, 100 - attendanceRate) * 0.35;
        double completionRisk = Math.max(0, 100 - feeComplianceRate) * 0.15;
        return Math.min(100, scoreRisk + attendanceRisk + completionRisk);
    }

    // ── private helpers ───────────────────────────────────────────────────────
    private String riskBand(double risk) {
        if (risk >= 70) return "CRITICAL";
        if (risk >= 50) return "HIGH";
        if (risk >= 30) return "MEDIUM";
        return "LOW";
    }

    private String computeTrend(Long studentId, Long termId, double currentScore) {
        return analyticsDao.findLatest(studentId, termId)
                .map(prev -> {
                    if (currentScore > prev.getOverallScore() + 3) return "IMPROVING";
                    if (currentScore < prev.getOverallScore() - 3) return "DECLINING";
                    return "STABLE";
                }).orElse("STABLE");
    }

    private String buildInsights(double score, double attendance, double completion, String risk) {
        List<String> parts = new java.util.ArrayList<>();
        if (score < 40)        parts.add("Academic performance is critically low");
        else if (score < 60)   parts.add("Academic performance needs improvement");
        if (attendance < 75)   parts.add("Attendance is below the 75% threshold");
        if (completion < 50)   parts.add("Many assignments are incomplete");
        if ("CRITICAL".equals(risk) || "HIGH".equals(risk)) parts.add("Immediate intervention recommended");
        return parts.isEmpty() ? "Student is performing well across all metrics"
                : String.join(". ", parts) + ".";
    }
}
