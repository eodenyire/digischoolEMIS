package com.digischool.emis.dao;

import com.digischool.emis.model.lms.Course;
import com.digischool.emis.model.lms.CourseAssignment;
import com.digischool.emis.model.lms.CourseSubmission;
import java.util.List;

public interface LmsDao extends GenericDao<Course, Long> {

    List<Course> findByClassStream(Long classStreamId);

    CourseAssignment saveAssignment(CourseAssignment assignment);

    CourseSubmission saveSubmission(CourseSubmission submission);

    List<CourseSubmission> findSubmissionsByAssignment(Long assignmentId);
}
