package com.digischool.emis.service;

import com.digischool.emis.model.timetable.Timetable;
import com.digischool.emis.model.timetable.TimetableSlot;
import java.util.List;
import java.util.Optional;

public interface TimetableService {

    Timetable createTimetable(Timetable timetable);

    Optional<Timetable> getTimetableById(Long timetableId);

    TimetableSlot addSlot(TimetableSlot slot);

    List<TimetableSlot> getSlotsForClassStream(Long timetableId, Long classStreamId);

    Timetable publishTimetable(Long timetableId);
}
