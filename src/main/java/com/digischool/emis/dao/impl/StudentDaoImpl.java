package com.digischool.emis.dao.impl;

import com.digischool.emis.config.DatabaseConfig;
import com.digischool.emis.dao.StudentDao;
import com.digischool.emis.model.student.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of StudentDao.
 * Uses parameterized queries to prevent SQL injection.
 */
public class StudentDaoImpl implements StudentDao {

    private static final Logger logger = LoggerFactory.getLogger(StudentDaoImpl.class);

    private final DataSource dataSource;

    public StudentDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Student save(Student student) {
        String sql = """
                INSERT INTO students (user_id, school_id, admission_number, nemis_id,
                    first_name, last_name, middle_name, date_of_birth, gender,
                    nationality, national_id, birth_certificate_number, religion,
                    ethnicity, blood_group, home_language, profile_picture_url,
                    admission_date, is_active, house_id, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
                RETURNING id, created_at, updated_at
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setStudentParameters(stmt, student);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                student.setId(rs.getLong("id"));
                student.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                student.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }
            logger.info("Saved student: {}", student.getAdmissionNumber());
            return student;

        } catch (SQLException e) {
            logger.error("Failed to save student: {}", student.getAdmissionNumber(), e);
            throw new RuntimeException("Failed to save student", e);
        }
    }

    @Override
    public Student update(Student student) {
        String sql = """
                UPDATE students SET user_id=?, school_id=?, admission_number=?,
                    nemis_id=?, first_name=?, last_name=?, middle_name=?,
                    date_of_birth=?, gender=?, nationality=?, national_id=?,
                    birth_certificate_number=?, religion=?, ethnicity=?,
                    blood_group=?, home_language=?, profile_picture_url=?,
                    admission_date=?, is_active=?, house_id=?, updated_at=NOW()
                WHERE id=?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setStudentParameters(stmt, student);
            stmt.setLong(21, student.getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Student not found with id: " + student.getId());
            }
            student.setUpdatedAt(LocalDateTime.now());
            return student;

        } catch (SQLException e) {
            logger.error("Failed to update student id={}", student.getId(), e);
            throw new RuntimeException("Failed to update student", e);
        }
    }

    @Override
    public Optional<Student> findById(Long id) {
        String sql = "SELECT * FROM students WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToStudent(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Failed to find student by id={}", id, e);
            throw new RuntimeException("Failed to find student", e);
        }
    }

    @Override
    public List<Student> findAll() {
        String sql = "SELECT * FROM students ORDER BY last_name, first_name";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<Student> students = new ArrayList<>();
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            return students;

        } catch (SQLException e) {
            logger.error("Failed to find all students", e);
            throw new RuntimeException("Database query failed", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM students WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Failed to delete student id={}", id, e);
            throw new RuntimeException("Failed to delete student", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM students WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getLong(1) > 0;

        } catch (SQLException e) {
            logger.error("Failed to check existence for student id={}", id, e);
            return false;
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM students";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next() ? rs.getLong(1) : 0;

        } catch (SQLException e) {
            logger.error("Failed to count students", e);
            return 0;
        }
    }

    @Override
    public Optional<Student> findByAdmissionNumber(Long schoolId, String admissionNumber) {
        String sql = "SELECT * FROM students WHERE school_id = ? AND admission_number = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, schoolId);
            stmt.setString(2, admissionNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToStudent(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Failed to find student by admission number", e);
            throw new RuntimeException("Failed to find student", e);
        }
    }

    @Override
    public Optional<Student> findByNemisId(String nemisId) {
        String sql = "SELECT * FROM students WHERE nemis_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nemisId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToStudent(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Failed to find student by NEMIS id", e);
            throw new RuntimeException("Failed to find student", e);
        }
    }

    @Override
    public List<Student> findActiveBySchool(Long schoolId) {
        String sql = "SELECT * FROM students WHERE school_id = ? AND is_active = TRUE "
                + "ORDER BY last_name, first_name";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, schoolId);
            return executeQueryWithStatement(stmt);

        } catch (SQLException e) {
            logger.error("Failed to find active students for school={}", schoolId, e);
            throw new RuntimeException("Failed to find students", e);
        }
    }

    @Override
    public List<Student> findByClass(Long classId) {
        String sql = """
                SELECT s.* FROM students s
                JOIN student_enrollments e ON s.id = e.student_id
                WHERE e.class_id = ? AND e.is_current = TRUE
                ORDER BY s.last_name, s.first_name
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, classId);
            return executeQueryWithStatement(stmt);

        } catch (SQLException e) {
            logger.error("Failed to find students for class={}", classId, e);
            throw new RuntimeException("Failed to find students", e);
        }
    }

    @Override
    public List<Student> findByGradeLevel(Long schoolId, Long gradeLevelId, Long academicYearId) {
        String sql = """
                SELECT s.* FROM students s
                JOIN student_enrollments e ON s.id = e.student_id
                JOIN classes c ON e.class_id = c.id
                WHERE s.school_id = ? AND c.grade_level_id = ?
                    AND e.academic_year_id = ? AND e.is_current = TRUE
                ORDER BY s.last_name, s.first_name
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, schoolId);
            stmt.setLong(2, gradeLevelId);
            stmt.setLong(3, academicYearId);
            return executeQueryWithStatement(stmt);

        } catch (SQLException e) {
            logger.error("Failed to find students by grade level", e);
            throw new RuntimeException("Failed to find students", e);
        }
    }

    @Override
    public List<Student> searchByName(Long schoolId, String nameQuery) {
        String sql = """
                SELECT * FROM students
                WHERE school_id = ?
                    AND (LOWER(first_name) LIKE LOWER(?) OR LOWER(last_name) LIKE LOWER(?)
                         OR LOWER(admission_number) LIKE LOWER(?))
                ORDER BY last_name, first_name
                LIMIT 50
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + nameQuery + "%";
            stmt.setLong(1, schoolId);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);
            return executeQueryWithStatement(stmt);

        } catch (SQLException e) {
            logger.error("Failed to search students by name", e);
            throw new RuntimeException("Failed to search students", e);
        }
    }

    @Override
    public long countActiveStudents(Long schoolId) {
        String sql = "SELECT COUNT(*) FROM students WHERE school_id = ? AND is_active = TRUE";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, schoolId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getLong(1) : 0;

        } catch (SQLException e) {
            logger.error("Failed to count active students for school={}", schoolId, e);
            return 0;
        }
    }

    @Override
    public long countByGender(Long schoolId, String gender) {
        String sql = "SELECT COUNT(*) FROM students WHERE school_id = ? AND gender = ? AND is_active = TRUE";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, schoolId);
            stmt.setString(2, gender);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getLong(1) : 0;

        } catch (SQLException e) {
            logger.error("Failed to count students by gender", e);
            return 0;
        }
    }

    private void setStudentParameters(PreparedStatement stmt, Student s) throws SQLException {
        int i = 1;
        if (s.getUserId() != null) stmt.setLong(i++, s.getUserId());
        else stmt.setNull(i++, Types.BIGINT);

        stmt.setLong(i++, s.getSchoolId());
        stmt.setString(i++, s.getAdmissionNumber());
        stmt.setString(i++, s.getNemisId());
        stmt.setString(i++, s.getFirstName());
        stmt.setString(i++, s.getLastName());
        stmt.setString(i++, s.getMiddleName());
        stmt.setDate(i++, s.getDateOfBirth() != null ? Date.valueOf(s.getDateOfBirth()) : null);
        stmt.setString(i++, s.getGender());
        stmt.setString(i++, s.getNationality());
        stmt.setString(i++, s.getNationalId());
        stmt.setString(i++, s.getBirthCertificateNumber());
        stmt.setString(i++, s.getReligion());
        stmt.setString(i++, s.getEthnicity());
        stmt.setString(i++, s.getBloodGroup());
        stmt.setString(i++, s.getHomeLanguage());
        stmt.setString(i++, s.getProfilePictureUrl());
        stmt.setDate(i++, s.getAdmissionDate() != null ? Date.valueOf(s.getAdmissionDate()) : null);
        stmt.setBoolean(i++, s.isActive());
        if (s.getHouseId() != null) stmt.setLong(i++, s.getHouseId());
        else stmt.setNull(i++, Types.BIGINT);
    }

    private List<Student> executeQueryWithStatement(PreparedStatement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery();
        List<Student> students = new ArrayList<>();
        while (rs.next()) {
            students.add(mapResultSetToStudent(rs));
        }
        return students;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getLong("id"));

        long userId = rs.getLong("user_id");
        if (!rs.wasNull()) student.setUserId(userId);

        student.setSchoolId(rs.getLong("school_id"));
        student.setAdmissionNumber(rs.getString("admission_number"));
        student.setNemisId(rs.getString("nemis_id"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setMiddleName(rs.getString("middle_name"));

        Date dob = rs.getDate("date_of_birth");
        if (dob != null) student.setDateOfBirth(dob.toLocalDate());

        student.setGender(rs.getString("gender"));
        student.setNationality(rs.getString("nationality"));
        student.setNationalId(rs.getString("national_id"));
        student.setBirthCertificateNumber(rs.getString("birth_certificate_number"));
        student.setReligion(rs.getString("religion"));
        student.setEthnicity(rs.getString("ethnicity"));
        student.setBloodGroup(rs.getString("blood_group"));
        student.setHomeLanguage(rs.getString("home_language"));
        student.setProfilePictureUrl(rs.getString("profile_picture_url"));

        Date admissionDate = rs.getDate("admission_date");
        if (admissionDate != null) student.setAdmissionDate(admissionDate.toLocalDate());

        student.setActive(rs.getBoolean("is_active"));

        long houseId = rs.getLong("house_id");
        if (!rs.wasNull()) student.setHouseId(houseId);

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) student.setCreatedAt(createdAt.toLocalDateTime());

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) student.setUpdatedAt(updatedAt.toLocalDateTime());

        return student;
    }
}
