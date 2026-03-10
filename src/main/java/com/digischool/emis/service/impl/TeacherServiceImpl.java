package com.digischool.emis.service.impl;

import com.digischool.emis.dao.TeacherDao;
import com.digischool.emis.model.teacher.Teacher;
import com.digischool.emis.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class TeacherServiceImpl implements TeacherService {

    private static final Logger log = LoggerFactory.getLogger(TeacherServiceImpl.class);
    private final TeacherDao teacherDao;

    public TeacherServiceImpl(TeacherDao teacherDao) { this.teacherDao = teacherDao; }

    @Override
    public Teacher registerTeacher(Teacher teacher) {
        validateTeacher(teacher);
        if (teacher.getTscNumber() != null &&
                teacherDao.findByTscNumber(teacher.getTscNumber()).isPresent()) {
            throw new IllegalArgumentException("TSC number already registered: " + teacher.getTscNumber());
        }
        return teacherDao.save(teacher);
    }

    @Override
    public Teacher updateTeacher(Teacher teacher) {
        validateTeacher(teacher);
        return teacherDao.update(teacher);
    }

    @Override public Optional<Teacher> getById(Long id) { return teacherDao.findById(id); }

    @Override public Optional<Teacher> getByTscNumber(String tsc) {
        return teacherDao.findByTscNumber(tsc);
    }

    @Override public List<Teacher> getActiveTeachersBySchool(Long schoolId) {
        return teacherDao.findActiveBySchool(schoolId);
    }

    @Override public List<Teacher> searchTeachers(Long schoolId, String query) {
        return teacherDao.searchByName(schoolId, query);
    }

    @Override
    public Teacher deactivateTeacher(Long teacherId) {
        Teacher t = teacherDao.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + teacherId));
        t.setActive(false);
        return teacherDao.update(t);
    }

    @Override public long countActiveTeachers(Long schoolId) {
        return teacherDao.countActiveTeachers(schoolId);
    }

    @Override
    public void validateTeacher(Teacher t) {
        if (t.getFirstName() == null || t.getFirstName().isBlank())
            throw new IllegalArgumentException("Teacher first name is required");
        if (t.getLastName()  == null || t.getLastName().isBlank())
            throw new IllegalArgumentException("Teacher last name is required");
        if (t.getSchoolId()  == null)
            throw new IllegalArgumentException("Teacher must belong to a school");
    }
}
