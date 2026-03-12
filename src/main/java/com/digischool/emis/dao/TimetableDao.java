package com.digischool.emis.dao;

import com.digischool.emis.model.timetable.Timetable;
import com.digischool.emis.model.timetable.TimetableSlot;
import java.util.List;

public interface TimetableDao extends GenericDao<Timetable, Long> {

    TimetableSlot saveSlot(TimetableSlot slot);

    List<TimetableSlot> findSlotsByTimetableAndClassStream(Long timetableId, Long classStreamId);
}
