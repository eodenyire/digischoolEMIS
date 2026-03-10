package com.digischool.emis.service;

import com.digischool.emis.model.teacher.Teacher;
import java.util.List;
import java.util.Optional;

public interface TeacherService {
    Teacher registerTeacher(Teacher teacher);
    Teacher updateTeacher(Teacher teacher);
    Optional<Teacher> getById(Long id);
    Optional<Teacher> getByTscNumber(String tscNumber);
    List<Teacher> getActiveTeachersBySchool(Long schoolId);
    List<Teacher> searchTeachers(Long schoolId, String query);
    Teacher deactivateTeacher(Long teacherId);
    long countActiveTeachers(Long schoolId);
    /** Validate required fields before registration. */
    void validateTeacher(Teacher teacher);
}
