package com.digischool.emis.dao;

import com.digischool.emis.model.transport.TransportRoute;
import com.digischool.emis.model.transport.TransportAssignment;
import java.util.List;

public interface TransportDao extends GenericDao<TransportRoute, Long> {

    List<TransportRoute> findActiveBySchool(Long schoolId);

    List<TransportAssignment> findAssignmentsByRoute(Long routeId);

    TransportAssignment saveAssignment(TransportAssignment assignment);
}
