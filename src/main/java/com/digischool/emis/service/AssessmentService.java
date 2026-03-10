package com.digischool.emis.service;

import com.digischool.emis.model.assessment.Assessment;
import com.digischool.emis.model.assessment.AssessmentResult;
import java.util.List;
import java.util.Optional;

public interface AssessmentService {
    Assessment createAssessment(Assessment assessment);
    Assessment updateAssessment(Assessment assessment);
    Optional<Assessment> getById(Long id);
    List<Assessment> getAssessmentsForClass(Long classId, Long termId);
    AssessmentResult recordResult(Long assessmentId, Long studentId, double score, String masteryLevel, String remarks, Long recordedBy);
    AssessmentResult updateResult(AssessmentResult result);
    List<AssessmentResult> getResultsByStudent(Long studentId, Long termId);
    List<AssessmentResult> getResultsByAssessment(Long assessmentId);
    /** Average score (0–100) for a student across a subject for a term. */
    double getStudentAverage(Long studentId, Long subjectId, Long termId);
    /** CBC mastery level from a numeric score (EE, ME, AE, BE). */
    String scoreToMasteryLevel(double percentage);
    void publishAssessment(Long assessmentId);
}
