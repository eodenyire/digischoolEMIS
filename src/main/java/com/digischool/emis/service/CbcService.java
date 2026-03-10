package com.digischool.emis.service;

import com.digischool.emis.dao.CbcDao;
import com.digischool.emis.model.academic.CbcCoreCompetency;
import com.digischool.emis.model.academic.CbcStrand;
import com.digischool.emis.model.academic.CbcSubStrand;
import java.util.List;

public interface CbcService {
    List<CbcStrand> getStrandsForSubjectAndGrade(Long subjectId, Long gradeLevelId);
    List<CbcSubStrand> getSubStrands(Long strandId);
    List<CbcCoreCompetency> getAllCoreCompetencies();
    void assessCompetency(Long studentId, Long competencyId, Long termId,
                          String masteryLevel, String remarks, Long assessedBy);
    void assessSubStrand(Long studentId, Long subStrandId, Long termId,
                         String masteryLevel, double score, String remarks, Long assessedBy);
    List<CbcDao.CompetencyAssessment> getCompetencyAssessments(Long studentId, Long termId);
    List<CbcDao.SubStrandAssessment> getSubStrandAssessments(Long studentId, Long termId);
    /** Convert numeric score to CBC mastery descriptor. */
    String toMasteryLevel(double score);
    /** Mastery level display label (e.g. "EE – Exceeds Expectations"). */
    String masteryLabel(String code);
}
