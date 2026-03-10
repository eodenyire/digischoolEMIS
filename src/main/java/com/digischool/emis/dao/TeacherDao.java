package com.digischool.emis.dao;

import com.digischool.emis.model.teacher.Teacher;
import java.util.List;
import java.util.Optional;

public interface TeacherDao extends GenericDao<Teacher, Long> {
    Optional<Teacher> findByTscNumber(String tscNumber);
    Optional<Teacher> findByUserId(Long userId);
    List<Teacher> findActiveBySchool(Long schoolId);
    List<Teacher> searchByName(Long schoolId, String query);
    long countActiveTeachers(Long schoolId);
}
