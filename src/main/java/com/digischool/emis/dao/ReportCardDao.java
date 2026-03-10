package com.digischool.emis.dao;

import com.digischool.emis.model.assessment.ReportCard;
import java.util.List;
import java.util.Optional;

public interface ReportCardDao extends GenericDao<ReportCard, Long> {
    Optional<ReportCard> findByStudentAndTerm(Long studentId, Long termId);
    List<ReportCard> findByClass(Long classId, Long termId);
    List<ReportCard> findByStudent(Long studentId);
    ReportCard saveOrUpdate(ReportCard reportCard);
    void publish(Long reportCardId, Long publishedBy);
}
