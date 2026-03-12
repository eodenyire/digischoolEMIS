package com.digischool.emis.service.impl;

import com.digischool.emis.dao.TimetableDao;
import com.digischool.emis.model.timetable.Timetable;
import com.digischool.emis.model.timetable.TimetableSlot;
import com.digischool.emis.service.TimetableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TimetableServiceImpl implements TimetableService {

    private static final Logger logger = LoggerFactory.getLogger(TimetableServiceImpl.class);

    private final TimetableDao timetableDao;

    public TimetableServiceImpl(TimetableDao timetableDao) {
        this.timetableDao = timetableDao;
    }

    @Override
    public Timetable createTimetable(Timetable timetable) {
        if (timetable == null) throw new IllegalArgumentException("Timetable cannot be null");
        if (timetable.getName() == null || timetable.getName().isBlank())
            throw new IllegalArgumentException("Timetable name is required");
        if (timetable.getSchoolId() == null)
            throw new IllegalArgumentException("School ID is required");
        Timetable saved = timetableDao.save(timetable);
        logger.info("Created timetable: {}", saved.getName());
        return saved;
    }

    @Override
    public Optional<Timetable> getTimetableById(Long timetableId) {
        return timetableDao.findById(timetableId);
    }

    @Override
    public TimetableSlot addSlot(TimetableSlot slot) {
        if (slot == null) throw new IllegalArgumentException("Slot cannot be null");
        if (slot.getTimetableId() == null)
            throw new IllegalArgumentException("Timetable ID is required");
        if (slot.getDayOfWeek() < 1 || slot.getDayOfWeek() > 7)
            throw new IllegalArgumentException("Day of week must be between 1 and 7");
        TimetableSlot saved = timetableDao.saveSlot(slot);
        logger.info("Added slot to timetable {} on day {}", saved.getTimetableId(), saved.getDayOfWeek());
        return saved;
    }

    @Override
    public List<TimetableSlot> getSlotsForClassStream(Long timetableId, Long classStreamId) {
        return timetableDao.findSlotsByTimetableAndClassStream(timetableId, classStreamId);
    }

    @Override
    public Timetable publishTimetable(Long timetableId) {
        Optional<Timetable> timetableOpt = timetableDao.findById(timetableId);
        if (timetableOpt.isEmpty())
            throw new IllegalArgumentException("Timetable not found with id: " + timetableId);

        Timetable timetable = timetableOpt.get();
        if (timetable.isPublished())
            throw new IllegalStateException("Timetable is already published");

        timetable.setStatus("published");
        timetable.setPublishedAt(LocalDateTime.now());
        Timetable updated = timetableDao.update(timetable);
        logger.info("Published timetable id={}", timetableId);
        return updated;
    }
}
