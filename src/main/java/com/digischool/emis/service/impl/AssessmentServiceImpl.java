package com.digischool.emis.service.impl;

import com.digischool.emis.dao.AssessmentDao;
import com.digischool.emis.dao.StudentDao;
import com.digischool.emis.model.assessment.Assessment;
import com.digischool.emis.model.assessment.AssessmentResult;
import com.digischool.emis.service.AssessmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

public class AssessmentServiceImpl implements AssessmentService {

    private static final Logger log = LoggerFactory.getLogger(AssessmentServiceImpl.class);
    private final AssessmentDao assessmentDao;
    private final StudentDao    studentDao;

    public AssessmentServiceImpl(AssessmentDao assessmentDao, StudentDao studentDao) {
        this.assessmentDao = assessmentDao;
        this.studentDao    = studentDao;
    }

    @Override
    public Assessment createAssessment(Assessment a) {
        if (a.getTitle() == null || a.getTitle().isBlank())
            throw new IllegalArgumentException("Assessment title is required");
        if (a.getMaxScore() == null || a.getMaxScore().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Max score must be positive");
        return assessmentDao.save(a);
    }

    @Override public Assessment updateAssessment(Assessment a) { return assessmentDao.update(a); }
    @Override public Optional<Assessment> getById(Long id) { return assessmentDao.findById(id); }

    @Override
    public List<Assessment> getAssessmentsForClass(Long classId, Long termId) {
        return assessmentDao.findByClass(classId, termId);
    }

    @Override
    public AssessmentResult recordResult(Long assessmentId, Long studentId, double score,
                                          String masteryLevel, String remarks, Long recordedBy) {
        Assessment assessment = assessmentDao.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found: " + assessmentId));

        double max = assessment.getMaxScore().doubleValue();
        double pct = max > 0 ? (score / max) * 100.0 : 0.0;
        String computed = masteryLevel != null ? masteryLevel : scoreToMasteryLevel(pct);

        Optional<AssessmentResult> existing = assessmentDao.findResult(assessmentId, studentId);
        if (existing.isPresent()) {
            AssessmentResult r = existing.get();
            r.setScore(BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP));
            r.setPercentage(BigDecimal.valueOf(pct).setScale(2, RoundingMode.HALF_UP));
            r.setCbcMasteryLevel(computed);
            r.setTeacherRemarks(remarks);
            r.setAbsent(false);
            return assessmentDao.updateResult(r);
        }

        AssessmentResult r = new AssessmentResult();
        r.setAssessmentId(assessmentId);
        r.setStudentId(studentId);
        r.setScore(BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP));
        r.setPercentage(BigDecimal.valueOf(pct).setScale(2, RoundingMode.HALF_UP));
        r.setCbcMasteryLevel(computed);
        r.setTeacherRemarks(remarks);
        r.setRecordedBy(recordedBy);
        return assessmentDao.saveResult(r);
    }

    @Override public AssessmentResult updateResult(AssessmentResult r) {
        return assessmentDao.updateResult(r);
    }

    @Override public List<AssessmentResult> getResultsByStudent(Long studentId, Long termId) {
        return assessmentDao.findResultsByStudent(studentId, termId);
    }

    @Override public List<AssessmentResult> getResultsByAssessment(Long assessmentId) {
        return assessmentDao.findResultsByAssessment(assessmentId);
    }

    @Override public double getStudentAverage(Long studentId, Long subjectId, Long termId) {
        return assessmentDao.getAverageScore(studentId, subjectId, termId);
    }

    @Override
    public String scoreToMasteryLevel(double percentage) {
        if (percentage >= 80) return "EE";   // Exceeds Expectations
        if (percentage >= 60) return "ME";   // Meets Expectations
        if (percentage >= 40) return "AE";   // Approaching Expectations
        return "BE";                          // Below Expectations
    }

    @Override
    public void publishAssessment(Long assessmentId) {
        assessmentDao.findById(assessmentId).ifPresent(a -> {
            a.setPublished(true);
            assessmentDao.update(a);
        });
    }
}
