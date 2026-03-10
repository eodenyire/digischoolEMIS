package com.digischool.emis.dao.impl;

import com.digischool.emis.dao.TeacherDao;
import com.digischool.emis.model.teacher.Teacher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class TeacherDaoImpl implements TeacherDao {

    private static final Logger log = LoggerFactory.getLogger(TeacherDaoImpl.class);
    private final DataSource ds;

    public TeacherDaoImpl(DataSource ds) { this.ds = ds; }

    @Override
    public Teacher save(Teacher t) {
        String sql = """
            INSERT INTO teachers (user_id,school_id,teacher_number,tsc_number,
                first_name,last_name,middle_name,date_of_birth,gender,nationality,
                national_id,phone,email,physical_address,date_joined,
                employment_type,department,designation,is_active,is_class_teacher,
                created_at,updated_at)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),NOW())
            RETURNING id,created_at,updated_at""";
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            bindTeacher(s, t);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                t.setId(rs.getLong("id"));
                t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            return t;
        } catch (SQLException e) { throw new RuntimeException("save teacher failed", e); }
    }

    @Override
    public Teacher update(Teacher t) {
        String sql = """
            UPDATE teachers SET user_id=?,school_id=?,teacher_number=?,tsc_number=?,
                first_name=?,last_name=?,middle_name=?,date_of_birth=?,gender=?,
                nationality=?,national_id=?,phone=?,email=?,physical_address=?,
                date_joined=?,employment_type=?,department=?,designation=?,
                is_active=?,is_class_teacher=?,updated_at=NOW()
            WHERE id=?""";
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            bindTeacher(s, t);
            s.setLong(21, t.getId());
            s.executeUpdate();
            t.setUpdatedAt(LocalDateTime.now());
            return t;
        } catch (SQLException e) { throw new RuntimeException("update teacher failed", e); }
    }

    @Override public Optional<Teacher> findById(Long id) {
        return queryOne("SELECT * FROM teachers WHERE id=?", id);
    }
    @Override public List<Teacher> findAll() {
        return queryList("SELECT * FROM teachers ORDER BY last_name,first_name");
    }
    @Override public void deleteById(Long id) {
        exec("DELETE FROM teachers WHERE id=?", id);
    }
    @Override public boolean existsById(Long id) {
        return count("SELECT COUNT(*) FROM teachers WHERE id=?", id) > 0;
    }
    @Override public long count() {
        return count("SELECT COUNT(*) FROM teachers");
    }
    @Override public Optional<Teacher> findByTscNumber(String tsc) {
        return queryOne("SELECT * FROM teachers WHERE tsc_number=?", tsc);
    }
    @Override public Optional<Teacher> findByUserId(Long userId) {
        return queryOne("SELECT * FROM teachers WHERE user_id=?", userId);
    }
    @Override public List<Teacher> findActiveBySchool(Long schoolId) {
        return queryList("SELECT * FROM teachers WHERE school_id=? AND is_active=TRUE " +
                         "ORDER BY last_name,first_name", schoolId);
    }
    @Override public List<Teacher> searchByName(Long schoolId, String q) {
        String p = "%" + q + "%";
        return queryList("""
            SELECT * FROM teachers WHERE school_id=?
              AND (LOWER(first_name) LIKE LOWER(?) OR LOWER(last_name) LIKE LOWER(?))
            ORDER BY last_name,first_name LIMIT 50""", schoolId, p, p);
    }
    @Override public long countActiveTeachers(Long schoolId) {
        return count("SELECT COUNT(*) FROM teachers WHERE school_id=? AND is_active=TRUE", schoolId);
    }

    // ── helpers ──────────────────────────────────────────────────────────────
    private void bindTeacher(PreparedStatement s, Teacher t) throws SQLException {
        int i = 1;
        if (t.getUserId() != null) s.setLong(i++, t.getUserId()); else s.setNull(i++, Types.BIGINT);
        s.setLong(i++, t.getSchoolId());
        s.setString(i++, t.getTeacherNumber());
        s.setString(i++, t.getTscNumber());
        s.setString(i++, t.getFirstName());
        s.setString(i++, t.getLastName());
        s.setString(i++, t.getMiddleName());
        s.setDate(i++, t.getDateOfBirth() != null ? Date.valueOf(t.getDateOfBirth()) : null);
        s.setString(i++, t.getGender());
        s.setString(i++, t.getNationality());
        s.setString(i++, t.getNationalId());
        s.setString(i++, t.getPhone());
        s.setString(i++, t.getEmail());
        s.setString(i++, t.getPhysicalAddress());
        s.setDate(i++, t.getDateJoined() != null ? Date.valueOf(t.getDateJoined()) : null);
        s.setString(i++, t.getEmploymentType());
        s.setString(i++, t.getDepartment());
        s.setString(i++, t.getDesignation());
        s.setBoolean(i++, t.isActive());
        s.setBoolean(i++, t.isClassTeacher());
    }

    private Optional<Teacher> queryOne(String sql, Object... params) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) s.setObject(i + 1, params[i]);
            ResultSet rs = s.executeQuery();
            return rs.next() ? Optional.of(map(rs)) : Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private List<Teacher> queryList(String sql, Object... params) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) s.setObject(i + 1, params[i]);
            ResultSet rs = s.executeQuery();
            List<Teacher> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private void exec(String sql, Object... params) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) s.setObject(i + 1, params[i]);
            s.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private long count(String sql, Object... params) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) s.setObject(i + 1, params[i]);
            ResultSet rs = s.executeQuery();
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) { return 0; }
    }

    private Teacher map(ResultSet rs) throws SQLException {
        Teacher t = new Teacher();
        t.setId(rs.getLong("id"));
        long uid = rs.getLong("user_id"); if (!rs.wasNull()) t.setUserId(uid);
        t.setSchoolId(rs.getLong("school_id"));
        t.setTeacherNumber(rs.getString("teacher_number"));
        t.setTscNumber(rs.getString("tsc_number"));
        t.setFirstName(rs.getString("first_name"));
        t.setLastName(rs.getString("last_name"));
        t.setMiddleName(rs.getString("middle_name"));
        Date dob = rs.getDate("date_of_birth"); if (dob != null) t.setDateOfBirth(dob.toLocalDate());
        t.setGender(rs.getString("gender"));
        t.setNationality(rs.getString("nationality"));
        t.setNationalId(rs.getString("national_id"));
        t.setPhone(rs.getString("phone"));
        t.setEmail(rs.getString("email"));
        t.setPhysicalAddress(rs.getString("physical_address"));
        Date dj = rs.getDate("date_joined"); if (dj != null) t.setDateJoined(dj.toLocalDate());
        t.setEmploymentType(rs.getString("employment_type"));
        t.setDepartment(rs.getString("department"));
        t.setDesignation(rs.getString("designation"));
        t.setActive(rs.getBoolean("is_active"));
        t.setClassTeacher(rs.getBoolean("is_class_teacher"));
        Timestamp ca = rs.getTimestamp("created_at"); if (ca != null) t.setCreatedAt(ca.toLocalDateTime());
        Timestamp ua = rs.getTimestamp("updated_at"); if (ua != null) t.setUpdatedAt(ua.toLocalDateTime());
        return t;
    }
}
