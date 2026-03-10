package com.digischool.emis.dao.impl;

import com.digischool.emis.dao.SchoolDao;
import com.digischool.emis.model.academic.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class SchoolDaoImpl implements SchoolDao {

    private static final Logger log = LoggerFactory.getLogger(SchoolDaoImpl.class);
    private final DataSource ds;

    public SchoolDaoImpl(DataSource ds) { this.ds = ds; }

    @Override public School save(School s) {
        String sql = """
            INSERT INTO schools (name,knec_code,nemis_code,school_type,ownership,
              county,sub_county,ward,physical_address,postal_address,email,phone,
              principal_name,established_year,is_boarding,motto,created_at,updated_at)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),NOW()) RETURNING id""";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, s.getName()); ps.setString(i++, s.getKnecCode());
            ps.setString(i++, s.getNemisCode()); ps.setString(i++, s.getSchoolType());
            ps.setString(i++, s.getOwnership()); ps.setString(i++, s.getCounty());
            ps.setString(i++, s.getSubCounty()); ps.setString(i++, s.getWard());
            ps.setString(i++, s.getPhysicalAddress()); ps.setString(i++, s.getPostalAddress());
            ps.setString(i++, s.getEmail()); ps.setString(i++, s.getPhone());
            ps.setString(i++, s.getPrincipalName());
            if (s.getEstablishedYear() != null) ps.setInt(i++, s.getEstablishedYear()); else ps.setNull(i++, Types.INTEGER);
            ps.setBoolean(i++, s.isBoarding()); ps.setString(i++, s.getMotto());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) s.setId(rs.getLong("id"));
            return s;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    @Override public School update(School s) {
        exec("UPDATE schools SET name=?,principal_name=?,phone=?,email=?,updated_at=NOW() WHERE id=?",
             s.getName(), s.getPrincipalName(), s.getPhone(), s.getEmail(), s.getId());
        return s;
    }
    @Override public Optional<School> findById(Long id) {
        return queryOneSchool("SELECT * FROM schools WHERE id=?", id);
    }
    @Override public List<School> findAll() {
        return queryListSchools("SELECT * FROM schools ORDER BY name");
    }
    @Override public void deleteById(Long id) { exec("DELETE FROM schools WHERE id=?", id); }
    @Override public boolean existsById(Long id) {
        return count("SELECT COUNT(*) FROM schools WHERE id=?", id) > 0;
    }
    @Override public long count() { return count("SELECT COUNT(*) FROM schools"); }

    @Override public Optional<School> findByKnecCode(String code) {
        return queryOneSchool("SELECT * FROM schools WHERE knec_code=?", code);
    }

    @Override public Optional<AcademicYear> findCurrentAcademicYear(Long schoolId) {
        return queryOneAY("SELECT * FROM academic_years WHERE school_id=? AND is_current=TRUE LIMIT 1",
                schoolId);
    }

    @Override public Optional<Term> findCurrentTerm(Long schoolId) {
        return queryOneTerm("""
            SELECT t.* FROM terms t
            JOIN academic_years ay ON t.academic_year_id=ay.id
            WHERE ay.school_id=? AND t.is_current=TRUE LIMIT 1""", schoolId);
    }

    @Override public List<Term> findTermsByAcademicYear(Long academicYearId) {
        return queryListTerms("SELECT * FROM terms WHERE academic_year_id=? ORDER BY term_number",
                academicYearId);
    }

    @Override public List<GradeLevel> findGradeLevelsBySchool(Long schoolId) {
        return queryListGrades("SELECT * FROM grade_levels WHERE school_id=? AND is_active=TRUE " +
                               "ORDER BY grade_order", schoolId);
    }

    @Override public List<SchoolClass> findActiveClasses(Long schoolId, Long academicYearId) {
        return queryListClasses("""
            SELECT sc.* FROM school_classes sc
            WHERE sc.school_id=? AND sc.academic_year_id=? AND sc.is_active=TRUE
            ORDER BY sc.class_name""", schoolId, academicYearId);
    }

    @Override public Optional<SchoolClass> findClassById(Long classId) {
        return queryOneClass("SELECT * FROM school_classes WHERE id=?", classId);
    }

    @Override public long countActiveClasses(Long schoolId, Long academicYearId) {
        return count("SELECT COUNT(*) FROM school_classes WHERE school_id=? AND academic_year_id=? AND is_active=TRUE",
                schoolId, academicYearId);
    }

    @Override public AcademicYear saveAcademicYear(AcademicYear ay) {
        String sql = """
            INSERT INTO academic_years (school_id,year_name,start_date,end_date,is_current,created_at,updated_at)
            VALUES (?,?,?,?,?,NOW(),NOW()) RETURNING id""";
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, ay.getSchoolId()); s.setString(2, ay.getYearName());
            s.setDate(3, ay.getStartDate() != null ? Date.valueOf(ay.getStartDate()) : null);
            s.setDate(4, ay.getEndDate()   != null ? Date.valueOf(ay.getEndDate())   : null);
            s.setBoolean(5, ay.isCurrent());
            ResultSet rs = s.executeQuery();
            if (rs.next()) ay.setId(rs.getLong("id"));
            return ay;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public Term saveTerm(Term t) {
        String sql = """
            INSERT INTO terms (academic_year_id,term_number,term_name,start_date,end_date,is_current,created_at,updated_at)
            VALUES (?,?,?,?,?,?,NOW(),NOW()) RETURNING id""";
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, t.getAcademicYearId()); s.setInt(2, t.getTermNumber());
            s.setString(3, t.getTermName());
            s.setDate(4, t.getStartDate() != null ? Date.valueOf(t.getStartDate()) : null);
            s.setDate(5, t.getEndDate()   != null ? Date.valueOf(t.getEndDate())   : null);
            s.setBoolean(6, t.isCurrent());
            ResultSet rs = s.executeQuery();
            if (rs.next()) t.setId(rs.getLong("id"));
            return t;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public SchoolClass saveClass(SchoolClass sc) {
        String sql = """
            INSERT INTO school_classes
              (school_id,grade_level_id,academic_year_id,class_teacher_id,
               class_name,stream,capacity,is_active,created_at,updated_at)
            VALUES (?,?,?,?,?,?,?,?,NOW(),NOW()) RETURNING id""";
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, sc.getSchoolId()); s.setLong(2, sc.getGradeLevelId());
            s.setLong(3, sc.getAcademicYearId());
            if (sc.getClassTeacherId() != null) s.setLong(4, sc.getClassTeacherId()); else s.setNull(4, Types.BIGINT);
            s.setString(5, sc.getClassName()); s.setString(6, sc.getStream());
            if (sc.getCapacity() != null) s.setInt(7, sc.getCapacity()); else s.setNull(7, Types.INTEGER);
            s.setBoolean(8, sc.isActive());
            ResultSet rs = s.executeQuery();
            if (rs.next()) sc.setId(rs.getLong("id"));
            return sc;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public GradeLevel saveGradeLevel(GradeLevel gl) {
        String sql = """
            INSERT INTO grade_levels
              (school_id,grade_name,grade_code,grade_order,cbc_category,is_active,created_at,updated_at)
            VALUES (?,?,?,?,?,?,NOW(),NOW()) RETURNING id""";
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, gl.getSchoolId()); s.setString(2, gl.getGradeName());
            s.setString(3, gl.getGradeCode()); s.setInt(4, gl.getGradeOrder());
            s.setString(5, gl.getCbcCategory()); s.setBoolean(6, gl.isActive());
            ResultSet rs = s.executeQuery();
            if (rs.next()) gl.setId(rs.getLong("id"));
            return gl;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private Optional<School> queryOneSchool(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            return rs.next() ? Optional.of(mapSchool(rs)) : Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    private List<School> queryListSchools(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            List<School> list = new ArrayList<>();
            while (rs.next()) list.add(mapSchool(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    private Optional<AcademicYear> queryOneAY(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            if (!rs.next()) return Optional.empty();
            AcademicYear ay = new AcademicYear();
            ay.setId(rs.getLong("id")); ay.setSchoolId(rs.getLong("school_id"));
            ay.setYearName(rs.getString("year_name")); ay.setCurrent(rs.getBoolean("is_current"));
            Date sd = rs.getDate("start_date"); if (sd != null) ay.setStartDate(sd.toLocalDate());
            Date ed = rs.getDate("end_date");   if (ed != null) ay.setEndDate(ed.toLocalDate());
            return Optional.of(ay);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    private Optional<Term> queryOneTerm(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            return rs.next() ? Optional.of(mapTerm(rs)) : Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    private List<Term> queryListTerms(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery(); List<Term> list = new ArrayList<>();
            while (rs.next()) list.add(mapTerm(rs)); return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    private List<GradeLevel> queryListGrades(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery(); List<GradeLevel> list = new ArrayList<>();
            while (rs.next()) { GradeLevel g = new GradeLevel();
                g.setId(rs.getLong("id")); g.setSchoolId(rs.getLong("school_id"));
                g.setGradeName(rs.getString("grade_name")); g.setGradeCode(rs.getString("grade_code"));
                g.setGradeOrder(rs.getInt("grade_order")); g.setCbcCategory(rs.getString("cbc_category"));
                g.setActive(rs.getBoolean("is_active")); list.add(g); }
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    private Optional<SchoolClass> queryOneClass(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            return rs.next() ? Optional.of(mapClass(rs)) : Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    private List<SchoolClass> queryListClasses(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery(); List<SchoolClass> list = new ArrayList<>();
            while (rs.next()) list.add(mapClass(rs)); return list;
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
    private School mapSchool(ResultSet rs) throws SQLException {
        School s = new School();
        s.setId(rs.getLong("id")); s.setName(rs.getString("name"));
        s.setKnecCode(rs.getString("knec_code")); s.setNemisCode(rs.getString("nemis_code"));
        s.setSchoolType(rs.getString("school_type")); s.setOwnership(rs.getString("ownership"));
        s.setCounty(rs.getString("county")); s.setSubCounty(rs.getString("sub_county"));
        s.setWard(rs.getString("ward")); s.setPhysicalAddress(rs.getString("physical_address"));
        s.setPostalAddress(rs.getString("postal_address")); s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone")); s.setPrincipalName(rs.getString("principal_name"));
        int ey = rs.getInt("established_year"); if (!rs.wasNull()) s.setEstablishedYear(ey);
        s.setBoarding(rs.getBoolean("is_boarding")); s.setMotto(rs.getString("motto"));
        return s;
    }
    private Term mapTerm(ResultSet rs) throws SQLException {
        Term t = new Term();
        t.setId(rs.getLong("id")); t.setAcademicYearId(rs.getLong("academic_year_id"));
        t.setTermNumber(rs.getInt("term_number")); t.setTermName(rs.getString("term_name"));
        Date sd = rs.getDate("start_date"); if (sd != null) t.setStartDate(sd.toLocalDate());
        Date ed = rs.getDate("end_date");   if (ed != null) t.setEndDate(ed.toLocalDate());
        t.setCurrent(rs.getBoolean("is_current")); return t;
    }
    private SchoolClass mapClass(ResultSet rs) throws SQLException {
        SchoolClass sc = new SchoolClass();
        sc.setId(rs.getLong("id")); sc.setSchoolId(rs.getLong("school_id"));
        sc.setGradeLevelId(rs.getLong("grade_level_id")); sc.setAcademicYearId(rs.getLong("academic_year_id"));
        long ct = rs.getLong("class_teacher_id"); if (!rs.wasNull()) sc.setClassTeacherId(ct);
        sc.setClassName(rs.getString("class_name")); sc.setStream(rs.getString("stream"));
        int cap = rs.getInt("capacity"); if (!rs.wasNull()) sc.setCapacity(cap);
        sc.setActive(rs.getBoolean("is_active")); return sc;
    }
}
