package com.digischool.emis.service;

import com.digischool.emis.model.transport.TransportRoute;
import com.digischool.emis.model.transport.TransportAssignment;
import java.util.List;
import java.util.Optional;

public interface TransportService {

    TransportRoute createRoute(TransportRoute route);

    Optional<TransportRoute> getRouteById(Long routeId);

    List<TransportRoute> getActiveRoutes(Long schoolId);

    TransportAssignment assignStudentToRoute(TransportAssignment assignment);

    List<TransportAssignment> getStudentsByRoute(Long routeId);
}
