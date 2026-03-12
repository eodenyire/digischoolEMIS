package com.digischool.emis.service;

import com.digischool.emis.model.lms.Course;
import com.digischool.emis.model.lms.CourseAssignment;
import com.digischool.emis.model.lms.CourseSubmission;
import java.util.List;
import java.util.Optional;

public interface LmsService {

    Course createCourse(Course course);

    Optional<Course> getCourseById(Long courseId);

    List<Course> getCoursesByClassStream(Long classStreamId);

    CourseAssignment createAssignment(CourseAssignment assignment);

    CourseSubmission submitAssignment(CourseSubmission submission);

    List<CourseSubmission> getAssignmentSubmissions(Long assignmentId);
}
