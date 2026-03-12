package com.digischool.emis.service.impl;

import com.digischool.emis.dao.TransportDao;
import com.digischool.emis.model.transport.TransportAssignment;
import com.digischool.emis.model.transport.TransportRoute;
import com.digischool.emis.service.TransportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class TransportServiceImpl implements TransportService {

    private static final Logger logger = LoggerFactory.getLogger(TransportServiceImpl.class);

    private final TransportDao transportDao;

    public TransportServiceImpl(TransportDao transportDao) {
        this.transportDao = transportDao;
    }

    @Override
    public TransportRoute createRoute(TransportRoute route) {
        if (route == null) throw new IllegalArgumentException("Route cannot be null");
        if (route.getRouteName() == null || route.getRouteName().isBlank())
            throw new IllegalArgumentException("Route name is required");
        if (route.getSchoolId() == null)
            throw new IllegalArgumentException("School ID is required");
        TransportRoute saved = transportDao.save(route);
        logger.info("Created transport route: {}", saved.getRouteCode());
        return saved;
    }

    @Override
    public Optional<TransportRoute> getRouteById(Long routeId) {
        return transportDao.findById(routeId);
    }

    @Override
    public List<TransportRoute> getActiveRoutes(Long schoolId) {
        return transportDao.findActiveBySchool(schoolId);
    }

    @Override
    public TransportAssignment assignStudentToRoute(TransportAssignment assignment) {
        if (assignment == null) throw new IllegalArgumentException("Assignment cannot be null");
        if (assignment.getStudentId() == null)
            throw new IllegalArgumentException("Student ID is required");
        if (assignment.getRouteId() == null)
            throw new IllegalArgumentException("Route ID is required");
        TransportAssignment saved = transportDao.saveAssignment(assignment);
        logger.info("Assigned student {} to route {}", saved.getStudentId(), saved.getRouteId());
        return saved;
    }

    @Override
    public List<TransportAssignment> getStudentsByRoute(Long routeId) {
        return transportDao.findAssignmentsByRoute(routeId);
    }
}
