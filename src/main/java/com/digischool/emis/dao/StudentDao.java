package com.digischool.emis.dao;

import com.digischool.emis.model.student.Student;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Student operations.
 */
public interface StudentDao extends GenericDao<Student, Long> {

    /**
     * Finds a student by admission number within a school.
     */
    Optional<Student> findByAdmissionNumber(Long schoolId, String admissionNumber);

    /**
     * Finds a student by NEMIS ID.
     */
    Optional<Student> findByNemisId(String nemisId);

    /**
     * Returns all active students in a school.
     */
    List<Student> findActiveBySchool(Long schoolId);

    /**
     * Returns all students currently enrolled in a specific class.
     */
    List<Student> findByClass(Long classId);

    /**
     * Returns all students in a specific grade level for the current academic year.
     */
    List<Student> findByGradeLevel(Long schoolId, Long gradeLevelId, Long academicYearId);

    /**
     * Searches students by name within a school.
     */
    List<Student> searchByName(Long schoolId, String nameQuery);

    /**
     * Counts total active students in a school.
     */
    long countActiveStudents(Long schoolId);

    /**
     * Counts students by gender in a school.
     */
    long countByGender(Long schoolId, String gender);
}
