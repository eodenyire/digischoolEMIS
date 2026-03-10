package com.digischool.emis.dao;

import com.digischool.emis.model.academic.*;
import java.util.List;
import java.util.Optional;

public interface CbcDao extends GenericDao<CbcStrand, Long> {
    List<CbcStrand> findStrandsBySubjectAndGrade(Long subjectId, Long gradeLevelId);
    List<CbcSubStrand> findSubStrandsByStrand(Long strandId);
    List<CbcCoreCompetency> findAllCompetencies();
    /** Save or update a student competency assessment. */
    void saveCompetencyAssessment(Long studentId, Long competencyId, Long termId,
                                  String masteryLevel, String remarks, Long assessedBy);
    /** Save or update a student sub-strand mastery record. */
    void saveSubStrandAssessment(Long studentId, Long subStrandId, Long termId,
                                 String masteryLevel, double score, String remarks, Long assessedBy);
    /** Get all competency assessments for a student in a term. */
    List<CompetencyAssessment> findCompetencyAssessments(Long studentId, Long termId);
    /** Get all sub-strand assessments for a student in a term. */
    List<SubStrandAssessment> findSubStrandAssessments(Long studentId, Long termId);

    record CompetencyAssessment(
            Long competencyId, String competencyName, String masteryLevel,
            double score, String remarks) {}

    record SubStrandAssessment(
            Long subStrandId, String strandName, String subStrandName,
            String masteryLevel, double score, String remarks) {}
}
