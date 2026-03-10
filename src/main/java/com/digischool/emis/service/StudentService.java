package com.digischool.emis.service;

import com.digischool.emis.model.student.Student;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Student Information System operations.
 */
public interface StudentService {

    /**
     * Registers a new student in the system.
     */
    Student registerStudent(Student student);

    /**
     * Updates student personal information.
     */
    Student updateStudent(Student student);

    /**
     * Gets student by ID.
     */
    Optional<Student> getStudentById(Long id);

    /**
     * Gets student by admission number.
     */
    Optional<Student> getStudentByAdmissionNumber(Long schoolId, String admissionNumber);

    /**
     * Gets all active students in a school.
     */
    List<Student> getActiveStudents(Long schoolId);

    /**
     * Gets students in a specific class.
     */
    List<Student> getStudentsByClass(Long classId);

    /**
     * Searches students by name.
     */
    List<Student> searchStudents(Long schoolId, String query);

    /**
     * Deactivates (withdraws) a student.
     */
    void withdrawStudent(Long studentId, String reason);

    /**
     * Generates next admission number for a school.
     */
    String generateAdmissionNumber(Long schoolId);

    /**
     * Gets dashboard statistics for a school.
     */
    StudentStatistics getStudentStatistics(Long schoolId);

    /**
     * Statistics data class for dashboard.
     */
    record StudentStatistics(
            long totalStudents,
            long maleStudents,
            long femaleStudents,
            long newAdmissionsThisYear
    ) {}
}
