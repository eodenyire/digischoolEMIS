package com.digischool.emis.service.impl;

import com.digischool.emis.dao.DisciplineDao;
import com.digischool.emis.model.discipline.DisciplineAction;
import com.digischool.emis.model.discipline.DisciplineIncident;
import com.digischool.emis.service.DisciplineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class DisciplineServiceImpl implements DisciplineService {

    private static final Logger logger = LoggerFactory.getLogger(DisciplineServiceImpl.class);

    private final DisciplineDao disciplineDao;

    public DisciplineServiceImpl(DisciplineDao disciplineDao) {
        this.disciplineDao = disciplineDao;
    }

    @Override
    public DisciplineIncident reportIncident(DisciplineIncident incident) {
        if (incident == null) throw new IllegalArgumentException("Incident cannot be null");
        if (incident.getStudentId() == null)
            throw new IllegalArgumentException("Student ID is required");
        if (incident.getIncidentType() == null || incident.getIncidentType().isBlank())
            throw new IllegalArgumentException("Incident type is required");
        if (incident.getIncidentDate() == null)
            incident.setIncidentDate(LocalDate.now());

        DisciplineIncident saved = disciplineDao.save(incident);
        logger.info("Reported discipline incident for student {}: {}", saved.getStudentId(), saved.getIncidentType());
        return saved;
    }

    @Override
    public Optional<DisciplineIncident> getIncidentById(Long incidentId) {
        return disciplineDao.findById(incidentId);
    }

    @Override
    public List<DisciplineIncident> getStudentIncidents(Long studentId) {
        return disciplineDao.findByStudent(studentId);
    }

    @Override
    public DisciplineAction applyAction(DisciplineAction action) {
        if (action == null) throw new IllegalArgumentException("Action cannot be null");
        if (action.getIncidentId() == null)
            throw new IllegalArgumentException("Incident ID is required");
        if (action.getActionType() == null || action.getActionType().isBlank())
            throw new IllegalArgumentException("Action type is required");
        if (action.getActionDate() == null)
            action.setActionDate(LocalDate.now());

        DisciplineAction saved = disciplineDao.saveAction(action);
        logger.info("Applied action '{}' for incident {}", saved.getActionType(), saved.getIncidentId());
        return saved;
    }

    @Override
    public void resolveIncident(Long incidentId) {
        Optional<DisciplineIncident> incidentOpt = disciplineDao.findById(incidentId);
        if (incidentOpt.isEmpty())
            throw new IllegalArgumentException("Incident not found with id: " + incidentId);

        DisciplineIncident incident = incidentOpt.get();
        incident.setResolved(true);
        disciplineDao.update(incident);
        logger.info("Resolved discipline incident id={}", incidentId);
    }
}
