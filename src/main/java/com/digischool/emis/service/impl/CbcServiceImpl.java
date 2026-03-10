package com.digischool.emis.service.impl;

import com.digischool.emis.dao.CbcDao;
import com.digischool.emis.model.academic.CbcCoreCompetency;
import com.digischool.emis.model.academic.CbcStrand;
import com.digischool.emis.model.academic.CbcSubStrand;
import com.digischool.emis.service.CbcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class CbcServiceImpl implements CbcService {

    private static final Logger log = LoggerFactory.getLogger(CbcServiceImpl.class);
    private final CbcDao cbcDao;

    /** CBC mastery level descriptors used by Kenya's KICD framework. */
    private static final Map<String, String> MASTERY_LABELS = Map.of(
            "EE", "EE – Exceeds Expectations",
            "ME", "ME – Meets Expectations",
            "AE", "AE – Approaching Expectations",
            "BE", "BE – Below Expectations"
    );

    public CbcServiceImpl(CbcDao cbcDao) { this.cbcDao = cbcDao; }

    @Override public List<CbcStrand> getStrandsForSubjectAndGrade(Long subjectId, Long gradeLevelId) {
        return cbcDao.findStrandsBySubjectAndGrade(subjectId, gradeLevelId);
    }
    @Override public List<CbcSubStrand> getSubStrands(Long strandId) {
        return cbcDao.findSubStrandsByStrand(strandId);
    }
    @Override public List<CbcCoreCompetency> getAllCoreCompetencies() {
        return cbcDao.findAllCompetencies();
    }
    @Override
    public void assessCompetency(Long studentId, Long competencyId, Long termId,
                                  String masteryLevel, String remarks, Long assessedBy) {
        validateMasteryLevel(masteryLevel);
        cbcDao.saveCompetencyAssessment(studentId, competencyId, termId, masteryLevel, remarks, assessedBy);
    }
    @Override
    public void assessSubStrand(Long studentId, Long subStrandId, Long termId,
                                 String masteryLevel, double score, String remarks, Long assessedBy) {
        validateMasteryLevel(masteryLevel);
        cbcDao.saveSubStrandAssessment(studentId, subStrandId, termId, masteryLevel, score, remarks, assessedBy);
    }
    @Override public List<CbcDao.CompetencyAssessment> getCompetencyAssessments(Long studentId, Long termId) {
        return cbcDao.findCompetencyAssessments(studentId, termId);
    }
    @Override public List<CbcDao.SubStrandAssessment> getSubStrandAssessments(Long studentId, Long termId) {
        return cbcDao.findSubStrandAssessments(studentId, termId);
    }

    @Override
    public String toMasteryLevel(double score) {
        if (score >= 80) return "EE";
        if (score >= 60) return "ME";
        if (score >= 40) return "AE";
        return "BE";
    }

    @Override
    public String masteryLabel(String code) {
        return MASTERY_LABELS.getOrDefault(code, code);
    }

    private void validateMasteryLevel(String level) {
        if (!MASTERY_LABELS.containsKey(level))
            throw new IllegalArgumentException("Invalid CBC mastery level: " + level +
                    ". Must be one of: EE, ME, AE, BE");
    }
}
