package com.digischool.emis.dao.impl;

import com.digischool.emis.dao.CbcDao;
import com.digischool.emis.model.academic.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class CbcDaoImpl implements CbcDao {

    private static final Logger log = LoggerFactory.getLogger(CbcDaoImpl.class);
    private final DataSource ds;

    public CbcDaoImpl(DataSource ds) { this.ds = ds; }

    // ── GenericDao boilerplate (CbcStrand as root entity) ─────────────────────
    @Override public CbcStrand save(CbcStrand s) {
        String sql = "INSERT INTO cbc_strands (subject_id,grade_level_id,name,description,strand_code," +
                     "is_active,created_at,updated_at) VALUES (?,?,?,?,?,?,NOW(),NOW()) RETURNING id";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, s.getSubjectId()); ps.setLong(2, s.getGradeLevelId());
            ps.setString(3, s.getName()); ps.setString(4, s.getDescription());
            ps.setString(5, s.getStrandCode()); ps.setBoolean(6, s.isActive());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) s.setId(rs.getLong("id"));
            return s;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    @Override public CbcStrand update(CbcStrand s) {
        exec("UPDATE cbc_strands SET name=?,description=?,is_active=?,updated_at=NOW() WHERE id=?",
             s.getName(), s.getDescription(), s.isActive(), s.getId());
        return s;
    }
    @Override public Optional<CbcStrand> findById(Long id) {
        return queryOneStrand("SELECT * FROM cbc_strands WHERE id=?", id);
    }
    @Override public List<CbcStrand> findAll() {
        return queryListStrands("SELECT * FROM cbc_strands ORDER BY name");
    }
    @Override public void deleteById(Long id) { exec("DELETE FROM cbc_strands WHERE id=?", id); }
    @Override public boolean existsById(Long id) {
        return count("SELECT COUNT(*) FROM cbc_strands WHERE id=?", id) > 0;
    }
    @Override public long count() { return count("SELECT COUNT(*) FROM cbc_strands"); }

    // ── Business methods ──────────────────────────────────────────────────────
    @Override
    public List<CbcStrand> findStrandsBySubjectAndGrade(Long subjectId, Long gradeLevelId) {
        return queryListStrands("SELECT * FROM cbc_strands WHERE subject_id=? AND grade_level_id=? " +
                                "AND is_active=TRUE ORDER BY strand_code", subjectId, gradeLevelId);
    }

    @Override
    public List<CbcSubStrand> findSubStrandsByStrand(Long strandId) {
        return queryListSubStrands("SELECT * FROM cbc_sub_strands WHERE strand_id=? " +
                                   "AND is_active=TRUE ORDER BY sub_strand_code", strandId);
    }

    @Override
    public List<CbcCoreCompetency> findAllCompetencies() {
        try (Connection c = ds.getConnection();
             PreparedStatement s = c.prepareStatement("SELECT * FROM cbc_core_competencies WHERE is_active=TRUE ORDER BY name")) {
            ResultSet rs = s.executeQuery();
            List<CbcCoreCompetency> list = new ArrayList<>();
            while (rs.next()) list.add(mapCompetency(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void saveCompetencyAssessment(Long studentId, Long competencyId, Long termId,
                                          String masteryLevel, String remarks, Long assessedBy) {
        exec("""
            INSERT INTO student_competency_assessments
              (student_id,competency_id,term_id,mastery_level,remarks,assessed_by,
               assessment_date,created_at,updated_at)
            VALUES (?,?,?,?,?,?,CURRENT_DATE,NOW(),NOW())
            ON CONFLICT (student_id,competency_id,term_id) DO UPDATE
              SET mastery_level=EXCLUDED.mastery_level, remarks=EXCLUDED.remarks,
                  assessed_by=EXCLUDED.assessed_by, updated_at=NOW()""",
             studentId, competencyId, termId, masteryLevel, remarks, assessedBy);
    }

    @Override
    public void saveSubStrandAssessment(Long studentId, Long subStrandId, Long termId,
                                         String masteryLevel, double score, String remarks, Long assessedBy) {
        exec("""
            INSERT INTO student_substrand_assessments
              (student_id,sub_strand_id,term_id,mastery_level,score,remarks,assessed_by,
               assessment_date,created_at,updated_at)
            VALUES (?,?,?,?,?,?,?,CURRENT_DATE,NOW(),NOW())
            ON CONFLICT (student_id,sub_strand_id,term_id) DO UPDATE
              SET mastery_level=EXCLUDED.mastery_level, score=EXCLUDED.score,
                  remarks=EXCLUDED.remarks, assessed_by=EXCLUDED.assessed_by, updated_at=NOW()""",
             studentId, subStrandId, termId, masteryLevel, score, remarks, assessedBy);
    }

    @Override
    public List<CompetencyAssessment> findCompetencyAssessments(Long studentId, Long termId) {
        String sql = """
            SELECT cc.id,cc.name AS competency_name,sca.mastery_level,
                   0.0 AS score, sca.remarks
            FROM student_competency_assessments sca
            JOIN cbc_core_competencies cc ON sca.competency_id=cc.id
            WHERE sca.student_id=? AND sca.term_id=?
            ORDER BY cc.name""";
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, studentId); s.setLong(2, termId);
            ResultSet rs = s.executeQuery();
            List<CompetencyAssessment> list = new ArrayList<>();
            while (rs.next()) list.add(new CompetencyAssessment(
                    rs.getLong("id"), rs.getString("competency_name"),
                    rs.getString("mastery_level"), rs.getDouble("score"), rs.getString("remarks")));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<SubStrandAssessment> findSubStrandAssessments(Long studentId, Long termId) {
        String sql = """
            SELECT ss.id,str.name AS strand_name,ss.name AS sub_strand_name,
                   ssa.mastery_level,ssa.score,ssa.remarks
            FROM student_substrand_assessments ssa
            JOIN cbc_sub_strands ss ON ssa.sub_strand_id=ss.id
            JOIN cbc_strands str ON ss.strand_id=str.id
            WHERE ssa.student_id=? AND ssa.term_id=?
            ORDER BY str.name,ss.name""";
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, studentId); s.setLong(2, termId);
            ResultSet rs = s.executeQuery();
            List<SubStrandAssessment> list = new ArrayList<>();
            while (rs.next()) list.add(new SubStrandAssessment(
                    rs.getLong("id"), rs.getString("strand_name"), rs.getString("sub_strand_name"),
                    rs.getString("mastery_level"), rs.getDouble("score"), rs.getString("remarks")));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private Optional<CbcStrand> queryOneStrand(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            return rs.next() ? Optional.of(mapStrand(rs)) : Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private List<CbcStrand> queryListStrands(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            List<CbcStrand> list = new ArrayList<>();
            while (rs.next()) list.add(mapStrand(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private List<CbcSubStrand> queryListSubStrands(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            List<CbcSubStrand> list = new ArrayList<>();
            while (rs.next()) list.add(mapSubStrand(rs));
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

    private CbcStrand mapStrand(ResultSet rs) throws SQLException {
        CbcStrand s = new CbcStrand();
        s.setId(rs.getLong("id"));
        s.setSubjectId(rs.getLong("subject_id"));
        s.setGradeLevelId(rs.getLong("grade_level_id"));
        s.setName(rs.getString("name"));
        s.setDescription(rs.getString("description"));
        s.setStrandCode(rs.getString("strand_code"));
        s.setActive(rs.getBoolean("is_active"));
        return s;
    }

    private CbcSubStrand mapSubStrand(ResultSet rs) throws SQLException {
        CbcSubStrand ss = new CbcSubStrand();
        ss.setId(rs.getLong("id"));
        ss.setStrandId(rs.getLong("strand_id"));
        ss.setName(rs.getString("name"));
        ss.setDescription(rs.getString("description"));
        ss.setSubStrandCode(rs.getString("sub_strand_code"));
        ss.setActive(rs.getBoolean("is_active"));
        return ss;
    }

    private CbcCoreCompetency mapCompetency(ResultSet rs) throws SQLException {
        CbcCoreCompetency cc = new CbcCoreCompetency();
        cc.setId(rs.getLong("id"));
        cc.setName(rs.getString("name"));
        cc.setDescription(rs.getString("description"));
        cc.setCode(rs.getString("code"));
        cc.setActive(rs.getBoolean("is_active"));
        return cc;
    }
}
