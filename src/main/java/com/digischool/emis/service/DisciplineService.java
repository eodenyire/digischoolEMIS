package com.digischool.emis.service;

import com.digischool.emis.model.discipline.DisciplineIncident;
import com.digischool.emis.model.discipline.DisciplineAction;
import java.util.List;
import java.util.Optional;

public interface DisciplineService {

    DisciplineIncident reportIncident(DisciplineIncident incident);

    Optional<DisciplineIncident> getIncidentById(Long incidentId);

    List<DisciplineIncident> getStudentIncidents(Long studentId);

    DisciplineAction applyAction(DisciplineAction action);

    void resolveIncident(Long incidentId);
}
