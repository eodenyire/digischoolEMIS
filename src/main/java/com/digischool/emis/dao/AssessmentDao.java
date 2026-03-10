package com.digischool.emis.dao;

import com.digischool.emis.model.assessment.Assessment;
import com.digischool.emis.model.assessment.AssessmentResult;
import java.util.List;
import java.util.Optional;

public interface AssessmentDao extends GenericDao<Assessment, Long> {
    List<Assessment> findByClass(Long classId, Long termId);
    List<Assessment> findBySubjectAndClass(Long subjectId, Long classId, Long termId);
    List<AssessmentResult> findResultsByAssessment(Long assessmentId);
    List<AssessmentResult> findResultsByStudent(Long studentId, Long termId);
    Optional<AssessmentResult> findResult(Long assessmentId, Long studentId);
    AssessmentResult saveResult(AssessmentResult result);
    AssessmentResult updateResult(AssessmentResult result);
    /** Average score for a student in a subject over a term (0–100). */
    double getAverageScore(Long studentId, Long subjectId, Long termId);
    /** Class average for a subject in a term. */
    double getClassAverage(Long classId, Long subjectId, Long termId);
}
