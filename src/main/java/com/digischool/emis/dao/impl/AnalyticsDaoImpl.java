package com.digischool.emis.dao.impl;

import com.digischool.emis.dao.AnalyticsDao;
import com.digischool.emis.model.analytics.StudentPerformanceSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AnalyticsDaoImpl implements AnalyticsDao {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsDaoImpl.class);
    private final DataSource ds;

    public AnalyticsDaoImpl(DataSource ds) { this.ds = ds; }

    @Override public StudentPerformanceSnapshot save(StudentPerformanceSnapshot s) { upsert(s); return s; }
    @Override public StudentPerformanceSnapshot update(StudentPerformanceSnapshot s) { upsert(s); return s; }
    @Override public Optional<StudentPerformanceSnapshot> findById(Long id) {
        return queryOne("SELECT * FROM student_performance_snapshots WHERE id=?", id);
    }
    @Override public List<StudentPerformanceSnapshot> findAll() {
        return queryList("SELECT * FROM student_performance_snapshots ORDER BY computed_at DESC");
    }
    @Override public void deleteById(Long id) {
        exec("DELETE FROM student_performance_snapshots WHERE id=?", id);
    }
    @Override public boolean existsById(Long id) {
        return count("SELECT COUNT(*) FROM student_performance_snapshots WHERE id=?", id) > 0;
    }
    @Override public long count() { return count("SELECT COUNT(*) FROM student_performance_snapshots"); }

    @Override public Optional<StudentPerformanceSnapshot> findLatest(Long studentId, Long termId) {
        return queryOne("""
            SELECT * FROM student_performance_snapshots
            WHERE student_id=? AND term_id=?
            ORDER BY computed_at DESC LIMIT 1""", studentId, termId);
    }

    @Override public List<StudentPerformanceSnapshot> findAtRiskStudents(Long schoolId, Long termId) {
        return queryList("""
            SELECT sps.* FROM student_performance_snapshots sps
            JOIN students st ON sps.student_id=st.id
            WHERE st.school_id=? AND sps.term_id=? AND sps.risk_level IN ('HIGH','CRITICAL')
            ORDER BY sps.dropout_risk_score DESC""", schoolId, termId);
    }

    @Override public List<StudentPerformanceSnapshot> findByClass(Long classId, Long termId) {
        return queryList("""
            SELECT sps.* FROM student_performance_snapshots sps
            JOIN student_enrollments se ON sps.student_id=se.student_id
            WHERE se.class_id=? AND sps.term_id=?
            ORDER BY sps.overall_score DESC""", classId, termId);
    }

    @Override
    public void upsert(StudentPerformanceSnapshot s) {
        String sql = """
            INSERT INTO student_performance_snapshots
              (student_id,academic_year_id,term_id,overall_score,attendance_rate,
               assignment_completion,trend,risk_level,dropout_risk_score,
               predicted_grade,key_insights,computed_at,created_at,updated_at)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,NOW(),NOW(),NOW())
            ON CONFLICT (student_id,term_id) DO UPDATE
              SET overall_score=EXCLUDED.overall_score,
                  attendance_rate=EXCLUDED.attendance_rate,
                  assignment_completion=EXCLUDED.assignment_completion,
                  trend=EXCLUDED.trend, risk_level=EXCLUDED.risk_level,
                  dropout_risk_score=EXCLUDED.dropout_risk_score,
                  predicted_grade=EXCLUDED.predicted_grade,
                  key_insights=EXCLUDED.key_insights,
                  computed_at=NOW(), updated_at=NOW()""";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            int i = 1;
            ps.setLong(i++, s.getStudentId());
            if (s.getAcademicYearId() != null) ps.setLong(i++, s.getAcademicYearId()); else ps.setNull(i++, Types.BIGINT);
            ps.setLong(i++, s.getTermId());
            ps.setDouble(i++, s.getOverallScore() != null ? s.getOverallScore() : 0.0);
            ps.setDouble(i++, s.getAttendanceRate() != null ? s.getAttendanceRate() : 0.0);
            ps.setDouble(i++, s.getAssignmentCompletion() != null ? s.getAssignmentCompletion() : 0.0);
            ps.setString(i++, s.getTrend());
            ps.setString(i++, s.getRiskLevel());
            ps.setDouble(i++, s.getDropoutRiskScore() != null ? s.getDropoutRiskScore() : 0.0);
            ps.setString(i++, s.getPredictedGrade());
            ps.setString(i++, s.getKeyInsights());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("upsert snapshot failed", e); }
    }

    @Override
    public DashboardStats getDashboardStats(Long schoolId, Long termId) {
        // Run separate lightweight queries so any single failure doesn't break the dashboard
        long students  = count("SELECT COUNT(*) FROM students WHERE school_id=? AND enrollment_status='ACTIVE'", schoolId);
        long teachers  = count("SELECT COUNT(*) FROM teachers WHERE school_id=? AND is_active=TRUE", schoolId);
        long classes   = count("SELECT COUNT(*) FROM school_classes sc " +
                               "JOIN academic_years ay ON sc.academic_year_id=ay.id " +
                               "WHERE sc.school_id=? AND ay.is_current=TRUE AND sc.is_active=TRUE", schoolId);
        long unpaid    = count("SELECT COUNT(*) FROM student_invoices WHERE school_id=? AND term_id=? " +
                               "AND status IN ('UNPAID','PARTIAL')", schoolId, termId);
        double attRate = queryDouble("SELECT COALESCE(100.0*SUM(CASE WHEN sa.status IN " +
                               "('PRESENT','LATE') THEN 1 ELSE 0 END)/NULLIF(COUNT(*),0),0) " +
                               "FROM student_attendance sa " +
                               "JOIN attendance_sessions sess ON sa.session_id=sess.id " +
                               "JOIN school_classes sc ON sess.class_id=sc.id " +
                               "WHERE sc.school_id=? AND sess.session_date=CURRENT_DATE", schoolId);
        long books     = count("SELECT COUNT(*) FROM library_issues WHERE school_id=? AND return_date IS NULL", schoolId);
        long news      = count("SELECT COUNT(*) FROM announcements WHERE school_id=? AND DATE(created_at)=CURRENT_DATE", schoolId);
        long atRisk    = count("SELECT COUNT(*) FROM student_performance_snapshots sps " +
                               "JOIN students st ON sps.student_id=st.id " +
                               "WHERE st.school_id=? AND sps.term_id=? AND sps.risk_level IN ('HIGH','CRITICAL')",
                               schoolId, termId);
        return new DashboardStats(students, teachers, classes, unpaid, attRate, books, news, atRisk);
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private Optional<StudentPerformanceSnapshot> queryOne(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            return rs.next() ? Optional.of(map(rs)) : Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private List<StudentPerformanceSnapshot> queryList(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            List<StudentPerformanceSnapshot> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private void exec(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            s.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private long count(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) { return 0; }
    }

    private double queryDouble(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) { return 0.0; }
    }

    private StudentPerformanceSnapshot map(ResultSet rs) throws SQLException {
        StudentPerformanceSnapshot s = new StudentPerformanceSnapshot();
        s.setId(rs.getLong("id")); s.setStudentId(rs.getLong("student_id"));
        long ay = rs.getLong("academic_year_id"); if (!rs.wasNull()) s.setAcademicYearId(ay);
        s.setTermId(rs.getLong("term_id")); s.setOverallScore(rs.getDouble("overall_score"));
        s.setAttendanceRate(rs.getDouble("attendance_rate"));
        s.setAssignmentCompletion(rs.getDouble("assignment_completion"));
        s.setTrend(rs.getString("trend")); s.setRiskLevel(rs.getString("risk_level"));
        s.setDropoutRiskScore(rs.getDouble("dropout_risk_score"));
        s.setPredictedGrade(rs.getString("predicted_grade"));
        s.setKeyInsights(rs.getString("key_insights"));
        Timestamp ct = rs.getTimestamp("computed_at"); if (ct != null) s.setComputedAt(ct.toLocalDateTime());
        Timestamp ca = rs.getTimestamp("created_at"); if (ca != null) s.setCreatedAt(ca.toLocalDateTime());
        return s;
    }
}
