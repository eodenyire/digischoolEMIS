package com.digischool.emis.service.impl;

import com.digischool.emis.dao.BoardingDao;
import com.digischool.emis.model.boarding.BedAllocation;
import com.digischool.emis.model.boarding.Dormitory;
import com.digischool.emis.service.BoardingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class BoardingServiceImpl implements BoardingService {

    private static final Logger logger = LoggerFactory.getLogger(BoardingServiceImpl.class);

    private final BoardingDao boardingDao;

    public BoardingServiceImpl(BoardingDao boardingDao) {
        this.boardingDao = boardingDao;
    }

    @Override
    public Dormitory createDormitory(Dormitory dormitory) {
        if (dormitory == null) throw new IllegalArgumentException("Dormitory cannot be null");
        if (dormitory.getDormName() == null || dormitory.getDormName().isBlank())
            throw new IllegalArgumentException("Dormitory name is required");
        if (dormitory.getCapacity() <= 0)
            throw new IllegalArgumentException("Dormitory capacity must be greater than zero");
        if (dormitory.getSchoolId() == null)
            throw new IllegalArgumentException("School ID is required");
        Dormitory saved = boardingDao.save(dormitory);
        logger.info("Created dormitory: {}", saved.getDormName());
        return saved;
    }

    @Override
    public Optional<Dormitory> getDormitoryById(Long dormitoryId) {
        return boardingDao.findById(dormitoryId);
    }

    @Override
    public BedAllocation allocateStudent(BedAllocation allocation) {
        if (allocation == null) throw new IllegalArgumentException("Allocation cannot be null");
        if (allocation.getStudentId() == null)
            throw new IllegalArgumentException("Student ID is required");
        if (allocation.getDormitoryId() == null)
            throw new IllegalArgumentException("Dormitory ID is required");
        if (allocation.getBedNumber() == null || allocation.getBedNumber().isBlank())
            throw new IllegalArgumentException("Bed number is required");

        Optional<Dormitory> dormOpt = boardingDao.findById(allocation.getDormitoryId());
        if (dormOpt.isEmpty())
            throw new IllegalArgumentException("Dormitory not found: " + allocation.getDormitoryId());

        Dormitory dorm = dormOpt.get();
        long currentOccupancy = boardingDao.countActiveOccupants(allocation.getDormitoryId());
        if (dorm.isFull((int) currentOccupancy))
            throw new IllegalStateException("Dormitory " + dorm.getDormName() + " is at full capacity");

        BedAllocation saved = boardingDao.saveAllocation(allocation);
        logger.info("Allocated student {} to bed {} in dormitory {}", saved.getStudentId(), saved.getBedNumber(), saved.getDormitoryId());
        return saved;
    }

    @Override
    public List<BedAllocation> getDormitoryOccupants(Long dormitoryId) {
        return boardingDao.findAllocationsByDormitory(dormitoryId);
    }

    @Override
    public int getAvailableBeds(Long dormitoryId) {
        Optional<Dormitory> dormOpt = boardingDao.findById(dormitoryId);
        if (dormOpt.isEmpty()) return 0;
        Dormitory dorm = dormOpt.get();
        long occupied = boardingDao.countActiveOccupants(dormitoryId);
        return Math.max(0, dorm.getCapacity() - (int) occupied);
    }
}
