package com.digischool.emis.service;

import com.digischool.emis.dao.AnalyticsDao;
import com.digischool.emis.model.analytics.StudentPerformanceSnapshot;
import java.util.List;

/**
 * Analytics engine — equivalent to Infinite Campus's "Early Warning" and
 * Alma SIS's real-time analytics module.
 *
 * Computes dropout risk, academic trends, and predictive grades by
 * combining live data from AssessmentService, AttendanceService, and FinanceService.
 */
public interface AnalyticsService {
    /**
     * Compute and persist a fresh snapshot for one student in a term.
     */
    StudentPerformanceSnapshot computeSnapshot(Long studentId, Long termId, Long academicYearId);
    /**
     * Compute snapshots for all active students in a school for the current term.
     */
    void computeSchoolSnapshots(Long schoolId, Long termId, Long academicYearId);
    List<StudentPerformanceSnapshot> getAtRiskStudents(Long schoolId, Long termId);
    List<StudentPerformanceSnapshot> getClassAnalytics(Long classId, Long termId);
    AnalyticsDao.DashboardStats getDashboardStats(Long schoolId, Long termId);
    /** Predict CBC grade band from an overall score (EE / ME / AE / BE). */
    String predictGradeBand(double overallScore);
    /** Compute a dropout risk score (0–100) from score, attendance, and fee compliance. */
    double computeDropoutRisk(double scoreRate, double attendanceRate, double feeComplianceRate);
}
