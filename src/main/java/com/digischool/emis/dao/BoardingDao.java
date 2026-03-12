package com.digischool.emis.dao;

import com.digischool.emis.model.boarding.Dormitory;
import com.digischool.emis.model.boarding.BedAllocation;
import java.util.List;

public interface BoardingDao extends GenericDao<Dormitory, Long> {

    List<BedAllocation> findAllocationsByDormitory(Long dormitoryId);

    BedAllocation saveAllocation(BedAllocation allocation);

    long countActiveOccupants(Long dormitoryId);
}
