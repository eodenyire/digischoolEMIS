package com.digischool.emis.dao;

import com.digischool.emis.model.analytics.StudentPerformanceSnapshot;
import java.util.List;
import java.util.Optional;

public interface AnalyticsDao extends GenericDao<StudentPerformanceSnapshot, Long> {
    Optional<StudentPerformanceSnapshot> findLatest(Long studentId, Long termId);
    List<StudentPerformanceSnapshot> findAtRiskStudents(Long schoolId, Long termId);
    List<StudentPerformanceSnapshot> findByClass(Long classId, Long termId);
    void upsert(StudentPerformanceSnapshot snapshot);
    /** Dashboard summary numbers. */
    DashboardStats getDashboardStats(Long schoolId, Long termId);

    record DashboardStats(
            long totalStudents,
            long totalTeachers,
            long activeClasses,
            long unpaidInvoices,
            double attendanceRateToday,
            long booksIssued,
            long announcementsToday,
            long atRiskStudents) {}
}
