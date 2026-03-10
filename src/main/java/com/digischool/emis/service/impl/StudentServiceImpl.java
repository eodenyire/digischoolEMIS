package com.digischool.emis.service.impl;

import com.digischool.emis.dao.StudentDao;
import com.digischool.emis.model.student.Student;
import com.digischool.emis.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of StudentService with business logic for student management.
 */
public class StudentServiceImpl implements StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentDao studentDao;

    public StudentServiceImpl(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    @Override
    public Student registerStudent(Student student) {
        validateStudent(student);

        // Check for duplicate admission number
        if (student.getAdmissionNumber() != null) {
            Optional<Student> existing = studentDao.findByAdmissionNumber(
                    student.getSchoolId(), student.getAdmissionNumber());
            if (existing.isPresent()) {
                throw new IllegalArgumentException(
                        "Student with admission number " + student.getAdmissionNumber()
                                + " already exists");
            }
        } else {
            student.setAdmissionNumber(generateAdmissionNumber(student.getSchoolId()));
        }

        // Set admission date if not set
        if (student.getAdmissionDate() == null) {
            student.setAdmissionDate(LocalDate.now());
        }

        Student saved = studentDao.save(student);
        logger.info("Registered new student: {}", saved.getAdmissionNumber());
        return saved;
    }

    @Override
    public Student updateStudent(Student student) {
        validateStudent(student);

        if (student.getId() == null) {
            throw new IllegalArgumentException("Student ID is required for update");
        }

        if (!studentDao.existsById(student.getId())) {
            throw new IllegalArgumentException("Student not found with id: " + student.getId());
        }

        Student updated = studentDao.update(student);
        logger.info("Updated student: {}", updated.getAdmissionNumber());
        return updated;
    }

    @Override
    public Optional<Student> getStudentById(Long id) {
        return studentDao.findById(id);
    }

    @Override
    public Optional<Student> getStudentByAdmissionNumber(Long schoolId, String admissionNumber) {
        return studentDao.findByAdmissionNumber(schoolId, admissionNumber);
    }

    @Override
    public List<Student> getActiveStudents(Long schoolId) {
        return studentDao.findActiveBySchool(schoolId);
    }

    @Override
    public List<Student> getStudentsByClass(Long classId) {
        return studentDao.findByClass(classId);
    }

    @Override
    public List<Student> searchStudents(Long schoolId, String query) {
        if (query == null || query.trim().isEmpty()) {
            return studentDao.findActiveBySchool(schoolId);
        }
        return studentDao.searchByName(schoolId, query.trim());
    }

    @Override
    public void withdrawStudent(Long studentId, String reason) {
        Optional<Student> studentOpt = studentDao.findById(studentId);
        if (studentOpt.isEmpty()) {
            throw new IllegalArgumentException("Student not found with id: " + studentId);
        }

        Student student = studentOpt.get();
        student.setActive(false);
        studentDao.update(student);
        logger.info("Withdrew student id={}, reason: {}", studentId, reason);
    }

    @Override
    public String generateAdmissionNumber(Long schoolId) {
        String year = String.valueOf(LocalDate.now().getYear());
        long count = studentDao.countActiveStudents(schoolId);
        return String.format("ADM/%s/%04d", year, count + 1);
    }

    @Override
    public StudentStatistics getStudentStatistics(Long schoolId) {
        long total = studentDao.countActiveStudents(schoolId);
        long male = studentDao.countByGender(schoolId, "MALE");
        long female = studentDao.countByGender(schoolId, "FEMALE");

        // New admissions this year would require a more complex query
        // For now, use total as a placeholder
        return new StudentStatistics(total, male, female, total);
    }

    private void validateStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        if (student.getFirstName() == null || student.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Student first name is required");
        }
        if (student.getLastName() == null || student.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Student last name is required");
        }
        if (student.getDateOfBirth() == null) {
            throw new IllegalArgumentException("Student date of birth is required");
        }
        if (student.getGender() == null || student.getGender().trim().isEmpty()) {
            throw new IllegalArgumentException("Student gender is required");
        }
        if (student.getSchoolId() == null) {
            throw new IllegalArgumentException("School ID is required");
        }
    }
}
