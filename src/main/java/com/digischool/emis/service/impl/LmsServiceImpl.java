package com.digischool.emis.service.impl;

import com.digischool.emis.dao.LmsDao;
import com.digischool.emis.model.lms.Course;
import com.digischool.emis.model.lms.CourseAssignment;
import com.digischool.emis.model.lms.CourseSubmission;
import com.digischool.emis.service.LmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class LmsServiceImpl implements LmsService {

    private static final Logger logger = LoggerFactory.getLogger(LmsServiceImpl.class);

    private final LmsDao lmsDao;

    public LmsServiceImpl(LmsDao lmsDao) {
        this.lmsDao = lmsDao;
    }

    @Override
    public Course createCourse(Course course) {
        if (course == null) throw new IllegalArgumentException("Course cannot be null");
        if (course.getTitle() == null || course.getTitle().isBlank())
            throw new IllegalArgumentException("Course title is required");
        if (course.getSchoolId() == null)
            throw new IllegalArgumentException("School ID is required");
        Course saved = lmsDao.save(course);
        logger.info("Created course: {}", saved.getTitle());
        return saved;
    }

    @Override
    public Optional<Course> getCourseById(Long courseId) {
        return lmsDao.findById(courseId);
    }

    @Override
    public List<Course> getCoursesByClassStream(Long classStreamId) {
        return lmsDao.findByClassStream(classStreamId);
    }

    @Override
    public CourseAssignment createAssignment(CourseAssignment assignment) {
        if (assignment == null) throw new IllegalArgumentException("Assignment cannot be null");
        if (assignment.getCourseId() == null)
            throw new IllegalArgumentException("Course ID is required");
        if (assignment.getTitle() == null || assignment.getTitle().isBlank())
            throw new IllegalArgumentException("Assignment title is required");
        CourseAssignment saved = lmsDao.saveAssignment(assignment);
        logger.info("Created assignment '{}' for course {}", saved.getTitle(), saved.getCourseId());
        return saved;
    }

    @Override
    public CourseSubmission submitAssignment(CourseSubmission submission) {
        if (submission == null) throw new IllegalArgumentException("Submission cannot be null");
        if (submission.getAssignmentId() == null)
            throw new IllegalArgumentException("Assignment ID is required");
        if (submission.getStudentId() == null)
            throw new IllegalArgumentException("Student ID is required");
        if (submission.getSubmittedAt() == null)
            submission.setSubmittedAt(LocalDateTime.now());
        CourseSubmission saved = lmsDao.saveSubmission(submission);
        logger.info("Student {} submitted assignment {}", saved.getStudentId(), saved.getAssignmentId());
        return saved;
    }

    @Override
    public List<CourseSubmission> getAssignmentSubmissions(Long assignmentId) {
        return lmsDao.findSubmissionsByAssignment(assignmentId);
    }
}
