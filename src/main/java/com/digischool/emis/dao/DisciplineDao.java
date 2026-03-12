package com.digischool.emis.dao;

import com.digischool.emis.model.discipline.DisciplineIncident;
import com.digischool.emis.model.discipline.DisciplineAction;
import java.util.List;

public interface DisciplineDao extends GenericDao<DisciplineIncident, Long> {

    List<DisciplineIncident> findByStudent(Long studentId);

    DisciplineAction saveAction(DisciplineAction action);
}
