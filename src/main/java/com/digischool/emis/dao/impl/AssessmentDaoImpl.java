package com.digischool.emis.dao.impl;

import com.digischool.emis.dao.AssessmentDao;
import com.digischool.emis.model.assessment.Assessment;
import com.digischool.emis.model.assessment.AssessmentResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AssessmentDaoImpl implements AssessmentDao {

    private static final Logger log = LoggerFactory.getLogger(AssessmentDaoImpl.class);
    private final DataSource ds;

    public AssessmentDaoImpl(DataSource ds) { this.ds = ds; }

    @Override
    public Assessment save(Assessment a) {
        String sql = """
            INSERT INTO assessments (school_id,class_id,subject_id,teacher_id,term_id,
                assessment_type,title,description,max_score,weight_percent,
                assessment_date,is_cbc_based,is_published,created_at,updated_at)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),NOW()) RETURNING id,created_at""";
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            int i = 1;
            s.setLong(i++, a.getSchoolId());
            s.setLong(i++, a.getClassId());
            s.setLong(i++, a.getSubjectId());
            s.setLong(i++, a.getTeacherId());
            s.setLong(i++, a.getTermId());
            s.setString(i++, a.getAssessmentType());
            s.setString(i++, a.getTitle());
            s.setString(i++, a.getDescription());
            s.setBigDecimal(i++, a.getMaxScore());
            s.setBigDecimal(i++, a.getWeightPercent());
            s.setDate(i++, a.getAssessmentDate() != null ? Date.valueOf(a.getAssessmentDate()) : null);
            s.setBoolean(i++, a.isCbcBased());
            s.setBoolean(i++, a.isPublished());
            ResultSet rs = s.executeQuery();
            if (rs.next()) a.setId(rs.getLong("id"));
            return a;
        } catch (SQLException e) { throw new RuntimeException("save assessment failed", e); }
    }

    @Override public Assessment update(Assessment a) {
        exec("UPDATE assessments SET title=?,description=?,max_score=?,weight_percent=?," +
             "assessment_date=?,is_published=?,updated_at=NOW() WHERE id=?",
             a.getTitle(), a.getDescription(), a.getMaxScore(), a.getWeightPercent(),
             a.getAssessmentDate() != null ? Date.valueOf(a.getAssessmentDate()) : null,
             a.isPublished(), a.getId());
        return a;
    }
    @Override public Optional<Assessment> findById(Long id) {
        return queryOne("SELECT * FROM assessments WHERE id=?", id);
    }
    @Override public List<Assessment> findAll() {
        return queryList("SELECT * FROM assessments ORDER BY assessment_date DESC");
    }
    @Override public void deleteById(Long id) { exec("DELETE FROM assessments WHERE id=?", id); }
    @Override public boolean existsById(Long id) { return count("SELECT COUNT(*) FROM assessments WHERE id=?", id) > 0; }
    @Override public long count() { return count("SELECT COUNT(*) FROM assessments"); }

    @Override
    public List<Assessment> findByClass(Long classId, Long termId) {
        return queryList("SELECT * FROM assessments WHERE class_id=? AND term_id=? " +
                         "ORDER BY assessment_date", classId, termId);
    }
    @Override
    public List<Assessment> findBySubjectAndClass(Long subjectId, Long classId, Long termId) {
        return queryList("SELECT * FROM assessments WHERE subject_id=? AND class_id=? AND term_id=? " +
                         "ORDER BY assessment_date", subjectId, classId, termId);
    }

    @Override
    public List<AssessmentResult> findResultsByAssessment(Long assessmentId) {
        return queryResults("SELECT * FROM assessment_results WHERE assessment_id=?", assessmentId);
    }
    @Override
    public List<AssessmentResult> findResultsByStudent(Long studentId, Long termId) {
        return queryResults("""
            SELECT ar.* FROM assessment_results ar
            JOIN assessments a ON ar.assessment_id = a.id
            WHERE ar.student_id=? AND a.term_id=?
            ORDER BY a.assessment_date""", studentId, termId);
    }
    @Override
    public Optional<AssessmentResult> findResult(Long assessmentId, Long studentId) {
        return queryOneResult("SELECT * FROM assessment_results WHERE assessment_id=? AND student_id=?",
                assessmentId, studentId);
    }

    @Override
    public AssessmentResult saveResult(AssessmentResult r) {
        String sql = """
            INSERT INTO assessment_results (assessment_id,student_id,score,percentage,
                cbc_mastery_level,teacher_remarks,is_absent,recorded_by,created_at,updated_at)
            VALUES (?,?,?,?,?,?,?,?,NOW(),NOW()) RETURNING id""";
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, r.getAssessmentId());
            s.setLong(2, r.getStudentId());
            s.setBigDecimal(3, r.getScore());
            s.setBigDecimal(4, r.getPercentage());
            s.setString(5, r.getCbcMasteryLevel());
            s.setString(6, r.getTeacherRemarks());
            s.setBoolean(7, r.isAbsent());
            if (r.getRecordedBy() != null) s.setLong(8, r.getRecordedBy()); else s.setNull(8, Types.BIGINT);
            ResultSet rs = s.executeQuery();
            if (rs.next()) r.setId(rs.getLong("id"));
            return r;
        } catch (SQLException e) { throw new RuntimeException("saveResult failed", e); }
    }

    @Override
    public AssessmentResult updateResult(AssessmentResult r) {
        exec("UPDATE assessment_results SET score=?,percentage=?,cbc_mastery_level=?," +
             "teacher_remarks=?,is_absent=?,updated_at=NOW() WHERE id=?",
             r.getScore(), r.getPercentage(), r.getCbcMasteryLevel(),
             r.getTeacherRemarks(), r.isAbsent(), r.getId());
        return r;
    }

    @Override
    public double getAverageScore(Long studentId, Long subjectId, Long termId) {
        return queryDouble("""
            SELECT COALESCE(AVG(ar.percentage),0)
            FROM assessment_results ar JOIN assessments a ON ar.assessment_id=a.id
            WHERE ar.student_id=? AND a.subject_id=? AND a.term_id=?
              AND ar.is_absent=FALSE""", studentId, subjectId, termId);
    }

    @Override
    public double getClassAverage(Long classId, Long subjectId, Long termId) {
        return queryDouble("""
            SELECT COALESCE(AVG(ar.percentage),0)
            FROM assessment_results ar JOIN assessments a ON ar.assessment_id=a.id
            WHERE a.class_id=? AND a.subject_id=? AND a.term_id=?
              AND ar.is_absent=FALSE""", classId, subjectId, termId);
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private Optional<Assessment> queryOne(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            return rs.next() ? Optional.of(mapA(rs)) : Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private List<Assessment> queryList(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            List<Assessment> list = new ArrayList<>();
            while (rs.next()) list.add(mapA(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private List<AssessmentResult> queryResults(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            List<AssessmentResult> list = new ArrayList<>();
            while (rs.next()) list.add(mapR(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Optional<AssessmentResult> queryOneResult(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            return rs.next() ? Optional.of(mapR(rs)) : Optional.empty();
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

    private Assessment mapA(ResultSet rs) throws SQLException {
        Assessment a = new Assessment();
        a.setId(rs.getLong("id"));
        a.setSchoolId(rs.getLong("school_id"));
        a.setClassId(rs.getLong("class_id"));
        a.setSubjectId(rs.getLong("subject_id"));
        a.setTeacherId(rs.getLong("teacher_id"));
        a.setTermId(rs.getLong("term_id"));
        a.setAssessmentType(rs.getString("assessment_type"));
        a.setTitle(rs.getString("title"));
        a.setDescription(rs.getString("description"));
        a.setMaxScore(rs.getBigDecimal("max_score"));
        a.setWeightPercent(rs.getBigDecimal("weight_percent"));
        Date d = rs.getDate("assessment_date"); if (d != null) a.setAssessmentDate(d.toLocalDate());
        a.setCbcBased(rs.getBoolean("is_cbc_based"));
        a.setPublished(rs.getBoolean("is_published"));
        Timestamp ca = rs.getTimestamp("created_at"); if (ca != null) a.setCreatedAt(ca.toLocalDateTime());
        return a;
    }

    private AssessmentResult mapR(ResultSet rs) throws SQLException {
        AssessmentResult r = new AssessmentResult();
        r.setId(rs.getLong("id"));
        r.setAssessmentId(rs.getLong("assessment_id"));
        r.setStudentId(rs.getLong("student_id"));
        r.setScore(rs.getBigDecimal("score"));
        r.setPercentage(rs.getBigDecimal("percentage"));
        r.setCbcMasteryLevel(rs.getString("cbc_mastery_level"));
        r.setTeacherRemarks(rs.getString("teacher_remarks"));
        r.setAbsent(rs.getBoolean("is_absent"));
        long rb = rs.getLong("recorded_by"); if (!rs.wasNull()) r.setRecordedBy(rb);
        Timestamp ca = rs.getTimestamp("created_at"); if (ca != null) r.setCreatedAt(ca.toLocalDateTime());
        return r;
    }
}
