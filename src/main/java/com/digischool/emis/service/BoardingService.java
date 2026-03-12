package com.digischool.emis.service;

import com.digischool.emis.model.boarding.Dormitory;
import com.digischool.emis.model.boarding.BedAllocation;
import java.util.List;
import java.util.Optional;

public interface BoardingService {

    Dormitory createDormitory(Dormitory dormitory);

    Optional<Dormitory> getDormitoryById(Long dormitoryId);

    BedAllocation allocateStudent(BedAllocation allocation);

    List<BedAllocation> getDormitoryOccupants(Long dormitoryId);

    int getAvailableBeds(Long dormitoryId);
}
