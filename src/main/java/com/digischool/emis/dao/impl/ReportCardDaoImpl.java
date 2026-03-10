package com.digischool.emis.dao.impl;

import com.digischool.emis.dao.ReportCardDao;
import com.digischool.emis.model.assessment.ReportCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class ReportCardDaoImpl implements ReportCardDao {

    private static final Logger log = LoggerFactory.getLogger(ReportCardDaoImpl.class);
    private final DataSource ds;

    public ReportCardDaoImpl(DataSource ds) { this.ds = ds; }

    @Override public ReportCard save(ReportCard r) { return saveOrUpdate(r); }
    @Override public ReportCard update(ReportCard r) { return saveOrUpdate(r); }

    @Override public Optional<ReportCard> findById(Long id) {
        return queryOne("SELECT * FROM report_cards WHERE id=?", id);
    }
    @Override public List<ReportCard> findAll() {
        return queryList("SELECT * FROM report_cards ORDER BY created_at DESC");
    }
    @Override public void deleteById(Long id) { exec("DELETE FROM report_cards WHERE id=?", id); }
    @Override public boolean existsById(Long id) {
        return count("SELECT COUNT(*) FROM report_cards WHERE id=?", id) > 0;
    }
    @Override public long count() { return count("SELECT COUNT(*) FROM report_cards"); }

    @Override public Optional<ReportCard> findByStudentAndTerm(Long studentId, Long termId) {
        return queryOne("SELECT * FROM report_cards WHERE student_id=? AND term_id=?", studentId, termId);
    }
    @Override public List<ReportCard> findByClass(Long classId, Long termId) {
        return queryList("SELECT * FROM report_cards WHERE class_id=? AND term_id=? " +
                         "ORDER BY class_position", classId, termId);
    }
    @Override public List<ReportCard> findByStudent(Long studentId) {
        return queryList("SELECT * FROM report_cards WHERE student_id=? ORDER BY created_at DESC", studentId);
    }

    @Override
    public ReportCard saveOrUpdate(ReportCard r) {
        if (r.getId() == null) {
            String sql = """
                INSERT INTO report_cards
                  (student_id,class_id,term_id,academic_year_id,overall_grade,overall_points,
                   class_position,class_size,overall_remarks,class_teacher_remarks,
                   principal_remarks,is_published,published_at,created_at,updated_at)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),NOW()) RETURNING id""";
            try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
                bindCard(s, r);
                ResultSet rs = s.executeQuery();
                if (rs.next()) r.setId(rs.getLong("id"));
                return r;
            } catch (SQLException e) { throw new RuntimeException(e); }
        } else {
            exec("""
                UPDATE report_cards SET overall_grade=?,overall_points=?,class_position=?,
                  class_size=?,overall_remarks=?,class_teacher_remarks=?,principal_remarks=?,
                  is_published=?,published_at=?,updated_at=NOW()
                WHERE id=?""",
                r.getOverallGrade(), r.getOverallPoints(), r.getClassPosition(), r.getClassSize(),
                r.getOverallRemarks(), r.getClassTeacherRemarks(), r.getPrincipalRemarks(),
                r.isPublished(),
                r.getPublishedAt() != null ? Timestamp.valueOf(r.getPublishedAt()) : null,
                r.getId());
            return r;
        }
    }

    @Override
    public void publish(Long reportCardId, Long publishedBy) {
        exec("UPDATE report_cards SET is_published=TRUE, published_at=NOW(), updated_at=NOW() WHERE id=?",
             reportCardId);
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private void bindCard(PreparedStatement s, ReportCard r) throws SQLException {
        int i = 1;
        s.setLong(i++, r.getStudentId()); s.setLong(i++, r.getClassId());
        s.setLong(i++, r.getTermId()); s.setLong(i++, r.getAcademicYearId());
        s.setString(i++, r.getOverallGrade());
        if (r.getOverallPoints() != null) s.setDouble(i++, r.getOverallPoints()); else s.setNull(i++, Types.DOUBLE);
        if (r.getClassPosition() != null) s.setInt(i++, r.getClassPosition()); else s.setNull(i++, Types.INTEGER);
        if (r.getClassSize() != null) s.setInt(i++, r.getClassSize()); else s.setNull(i++, Types.INTEGER);
        s.setString(i++, r.getOverallRemarks()); s.setString(i++, r.getClassTeacherRemarks());
        s.setString(i++, r.getPrincipalRemarks()); s.setBoolean(i++, r.isPublished());
        s.setTimestamp(i++, r.getPublishedAt() != null ? Timestamp.valueOf(r.getPublishedAt()) : null);
    }

    private Optional<ReportCard> queryOne(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            return rs.next() ? Optional.of(map(rs)) : Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private List<ReportCard> queryList(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            List<ReportCard> list = new ArrayList<>();
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

    private ReportCard map(ResultSet rs) throws SQLException {
        ReportCard r = new ReportCard();
        r.setId(rs.getLong("id")); r.setStudentId(rs.getLong("student_id"));
        r.setClassId(rs.getLong("class_id")); r.setTermId(rs.getLong("term_id"));
        r.setAcademicYearId(rs.getLong("academic_year_id"));
        r.setOverallGrade(rs.getString("overall_grade"));
        double op = rs.getDouble("overall_points"); if (!rs.wasNull()) r.setOverallPoints(op);
        int cp = rs.getInt("class_position"); if (!rs.wasNull()) r.setClassPosition(cp);
        int cs = rs.getInt("class_size"); if (!rs.wasNull()) r.setClassSize(cs);
        r.setOverallRemarks(rs.getString("overall_remarks"));
        r.setClassTeacherRemarks(rs.getString("class_teacher_remarks"));
        r.setPrincipalRemarks(rs.getString("principal_remarks"));
        r.setPublished(rs.getBoolean("is_published"));
        Timestamp pa = rs.getTimestamp("published_at"); if (pa != null) r.setPublishedAt(pa.toLocalDateTime());
        Timestamp ca = rs.getTimestamp("created_at"); if (ca != null) r.setCreatedAt(ca.toLocalDateTime());
        return r;
    }
}
