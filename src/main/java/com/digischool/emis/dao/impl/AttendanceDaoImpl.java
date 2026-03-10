package com.digischool.emis.dao.impl;

import com.digischool.emis.dao.AttendanceDao;
import com.digischool.emis.model.attendance.AttendanceSession;
import com.digischool.emis.model.attendance.StudentAttendance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class AttendanceDaoImpl implements AttendanceDao {

    private static final Logger log = LoggerFactory.getLogger(AttendanceDaoImpl.class);
    private final DataSource ds;

    public AttendanceDaoImpl(DataSource ds) { this.ds = ds; }

    @Override public AttendanceSession save(AttendanceSession s) { return saveSession(s); }
    @Override public AttendanceSession update(AttendanceSession s) { return saveSession(s); }
    @Override public Optional<AttendanceSession> findById(Long id) {
        return queryOneSession("SELECT * FROM attendance_sessions WHERE id=?", id);
    }
    @Override public List<AttendanceSession> findAll() {
        return queryListSessions("SELECT * FROM attendance_sessions ORDER BY session_date DESC");
    }
    @Override public void deleteById(Long id) { exec("DELETE FROM attendance_sessions WHERE id=?", id); }
    @Override public boolean existsById(Long id) {
        return count("SELECT COUNT(*) FROM attendance_sessions WHERE id=?", id) > 0;
    }
    @Override public long count() { return count("SELECT COUNT(*) FROM attendance_sessions"); }

    @Override
    public AttendanceSession saveSession(AttendanceSession ss) {
        if (ss.getId() == null) {
            String sql = """
                INSERT INTO attendance_sessions
                  (class_id,subject_id,teacher_id,session_date,period_number,
                   start_time,end_time,session_type,is_finalized,created_at,updated_at)
                VALUES (?,?,?,?,?,?,?,?,?,NOW(),NOW()) RETURNING id""";
            try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
                bindSession(s, ss);
                ResultSet rs = s.executeQuery();
                if (rs.next()) ss.setId(rs.getLong("id"));
                return ss;
            } catch (SQLException e) { throw new RuntimeException(e); }
        } else {
            exec("UPDATE attendance_sessions SET is_finalized=?,updated_at=NOW() WHERE id=?",
                 ss.isFinalized(), ss.getId());
            return ss;
        }
    }

    @Override
    public List<AttendanceSession> findSessionsByClass(Long classId, LocalDate date) {
        return queryListSessions("SELECT * FROM attendance_sessions WHERE class_id=? AND session_date=? " +
                                 "ORDER BY period_number", classId, Date.valueOf(date));
    }

    @Override
    public List<StudentAttendance> findBySession(Long sessionId) {
        return queryListAttendance("SELECT * FROM student_attendance WHERE session_id=?", sessionId);
    }

    @Override
    public Optional<StudentAttendance> findStudentRecord(Long sessionId, Long studentId) {
        return queryOneAttendance("SELECT * FROM student_attendance WHERE session_id=? AND student_id=?",
                sessionId, studentId);
    }

    @Override
    public StudentAttendance saveRecord(StudentAttendance r) {
        String sql = """
            INSERT INTO student_attendance
              (session_id,student_id,status,arrival_time,departure_time,excuse_reason,
               recorded_by,parent_notified,created_at,updated_at)
            VALUES (?,?,?,?,?,?,?,?,NOW(),NOW()) RETURNING id""";
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            bindAttendance(s, r);
            ResultSet rs = s.executeQuery();
            if (rs.next()) r.setId(rs.getLong("id"));
            return r;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public StudentAttendance updateRecord(StudentAttendance r) {
        exec("UPDATE student_attendance SET status=?,arrival_time=?,departure_time=?," +
             "excuse_reason=?,parent_notified=?,updated_at=NOW() WHERE id=?",
             r.getStatus(), r.getArrivalTime(), r.getDepartureTime(),
             r.getExcuseReason(), r.isParentNotified(), r.getId());
        return r;
    }

    @Override
    public double getAttendanceRate(Long studentId, Long academicYearId) {
        return queryDouble("""
            SELECT COALESCE(
              100.0 * SUM(CASE WHEN sa.status IN ('PRESENT','LATE') THEN 1 ELSE 0 END)
                   / NULLIF(COUNT(*),0), 0)
            FROM student_attendance sa
            JOIN attendance_sessions sess ON sa.session_id = sess.id
            JOIN terms t ON sess.session_date BETWEEN t.start_date AND t.end_date
            WHERE sa.student_id=? AND t.academic_year_id=?""", studentId, academicYearId);
    }

    @Override
    public int countAbsentDays(Long studentId, Long termId) {
        return (int) count("""
            SELECT COUNT(DISTINCT DATE(sess.session_date))
            FROM student_attendance sa
            JOIN attendance_sessions sess ON sa.session_id = sess.id
            JOIN terms t ON sess.session_date BETWEEN t.start_date AND t.end_date
            WHERE sa.student_id=? AND t.id=? AND sa.status='ABSENT'""", studentId, termId);
    }

    @Override
    public double getSchoolAttendanceRate(Long schoolId, LocalDate date) {
        return queryDouble("""
            SELECT COALESCE(
              100.0 * SUM(CASE WHEN sa.status IN ('PRESENT','LATE') THEN 1 ELSE 0 END)
                   / NULLIF(COUNT(*),0), 0)
            FROM student_attendance sa
            JOIN attendance_sessions sess ON sa.session_id = sess.id
            JOIN school_classes sc ON sess.class_id = sc.id
            WHERE sc.school_id=? AND sess.session_date=?""", schoolId, Date.valueOf(date));
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private void bindSession(PreparedStatement s, AttendanceSession ss) throws SQLException {
        s.setLong(1, ss.getClassId());
        if (ss.getSubjectId() != null) s.setLong(2, ss.getSubjectId()); else s.setNull(2, Types.BIGINT);
        if (ss.getTeacherId() != null) s.setLong(3, ss.getTeacherId()); else s.setNull(3, Types.BIGINT);
        s.setDate(4, Date.valueOf(ss.getSessionDate()));
        s.setInt(5, ss.getPeriodNumber() != null ? ss.getPeriodNumber() : 1);
        s.setTime(6, ss.getStartTime() != null ? Time.valueOf(ss.getStartTime()) : null);
        s.setTime(7, ss.getEndTime()   != null ? Time.valueOf(ss.getEndTime())   : null);
        s.setString(8, ss.getSessionType());
        s.setBoolean(9, ss.isFinalized());
    }

    private void bindAttendance(PreparedStatement s, StudentAttendance r) throws SQLException {
        s.setLong(1, r.getSessionId());
        s.setLong(2, r.getStudentId());
        s.setString(3, r.getStatus());
        s.setTime(4, r.getArrivalTime() != null ? Time.valueOf(r.getArrivalTime()) : null);
        s.setTime(5, r.getDepartureTime() != null ? Time.valueOf(r.getDepartureTime()) : null);
        s.setString(6, r.getExcuseReason());
        if (r.getRecordedBy() != null) s.setLong(7, r.getRecordedBy()); else s.setNull(7, Types.BIGINT);
        s.setBoolean(8, r.isParentNotified());
    }

    private Optional<AttendanceSession> queryOneSession(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            return rs.next() ? Optional.of(mapSession(rs)) : Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private List<AttendanceSession> queryListSessions(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            List<AttendanceSession> list = new ArrayList<>();
            while (rs.next()) list.add(mapSession(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Optional<StudentAttendance> queryOneAttendance(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            return rs.next() ? Optional.of(mapAttendance(rs)) : Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private List<StudentAttendance> queryListAttendance(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            List<StudentAttendance> list = new ArrayList<>();
            while (rs.next()) list.add(mapAttendance(rs));
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

    private AttendanceSession mapSession(ResultSet rs) throws SQLException {
        AttendanceSession ss = new AttendanceSession();
        ss.setId(rs.getLong("id"));
        ss.setClassId(rs.getLong("class_id"));
        long sid = rs.getLong("subject_id"); if (!rs.wasNull()) ss.setSubjectId(sid);
        long tid = rs.getLong("teacher_id"); if (!rs.wasNull()) ss.setTeacherId(tid);
        Date d = rs.getDate("session_date"); if (d != null) ss.setSessionDate(d.toLocalDate());
        ss.setPeriodNumber(rs.getInt("period_number"));
        Time st = rs.getTime("start_time"); if (st != null) ss.setStartTime(st.toLocalTime());
        Time et = rs.getTime("end_time");   if (et != null) ss.setEndTime(et.toLocalTime());
        ss.setSessionType(rs.getString("session_type"));
        ss.setFinalized(rs.getBoolean("is_finalized"));
        Timestamp ca = rs.getTimestamp("created_at"); if (ca != null) ss.setCreatedAt(ca.toLocalDateTime());
        return ss;
    }

    private StudentAttendance mapAttendance(ResultSet rs) throws SQLException {
        StudentAttendance a = new StudentAttendance();
        a.setId(rs.getLong("id"));
        a.setSessionId(rs.getLong("session_id"));
        a.setStudentId(rs.getLong("student_id"));
        a.setStatus(rs.getString("status"));
        Time art = rs.getTime("arrival_time");   if (art != null) a.setArrivalTime(art.toLocalTime());
        Time det = rs.getTime("departure_time"); if (det != null) a.setDepartureTime(det.toLocalTime());
        a.setExcuseReason(rs.getString("excuse_reason"));
        long rb = rs.getLong("recorded_by"); if (!rs.wasNull()) a.setRecordedBy(rb);
        a.setParentNotified(rs.getBoolean("parent_notified"));
        Timestamp ca = rs.getTimestamp("created_at"); if (ca != null) a.setCreatedAt(ca.toLocalDateTime());
        return a;
    }
}
