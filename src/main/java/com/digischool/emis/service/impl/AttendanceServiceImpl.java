package com.digischool.emis.service.impl;

import com.digischool.emis.dao.AttendanceDao;
import com.digischool.emis.dao.StudentDao;
import com.digischool.emis.model.attendance.AttendanceSession;
import com.digischool.emis.model.attendance.StudentAttendance;
import com.digischool.emis.service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AttendanceServiceImpl implements AttendanceService {

    private static final Logger log = LoggerFactory.getLogger(AttendanceServiceImpl.class);
    private final AttendanceDao attendanceDao;
    private final StudentDao    studentDao;

    public AttendanceServiceImpl(AttendanceDao attendanceDao, StudentDao studentDao) {
        this.attendanceDao = attendanceDao;
        this.studentDao    = studentDao;
    }

    @Override
    public AttendanceSession openSession(Long classId, Long subjectId, Long teacherId,
                                          LocalDate date, int period) {
        AttendanceSession session = new AttendanceSession();
        session.setClassId(classId);
        session.setSubjectId(subjectId);
        session.setTeacherId(teacherId);
        session.setSessionDate(date != null ? date : LocalDate.now());
        session.setPeriodNumber(period);
        session.setSessionType("CLASS");
        session.setFinalized(false);
        return attendanceDao.saveSession(session);
    }

    @Override
    public StudentAttendance markAttendance(Long sessionId, Long studentId, String status,
                                             String excuseReason, Long recordedBy) {
        var existing = attendanceDao.findStudentRecord(sessionId, studentId);
        if (existing.isPresent()) {
            StudentAttendance r = existing.get();
            r.setStatus(status);
            r.setExcuseReason(excuseReason);
            return attendanceDao.updateRecord(r);
        }
        StudentAttendance r = new StudentAttendance();
        r.setSessionId(sessionId);
        r.setStudentId(studentId);
        r.setStatus(status);
        r.setExcuseReason(excuseReason);
        r.setRecordedBy(recordedBy);
        r.setParentNotified(false);
        return attendanceDao.saveRecord(r);
    }

    @Override
    public void bulkMarkAttendance(Long sessionId, Map<Long, String> statusMap, Long recordedBy) {
        statusMap.forEach((studentId, status) ->
                markAttendance(sessionId, studentId, status, null, recordedBy));
    }

    @Override
    public AttendanceSession finalizeSession(Long sessionId) {
        AttendanceSession session = attendanceDao.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
        session.setFinalized(true);
        return attendanceDao.saveSession(session);
    }

    @Override public List<AttendanceSession> getSessionsForClass(Long classId, LocalDate date) {
        return attendanceDao.findSessionsByClass(classId, date);
    }

    @Override public List<StudentAttendance> getAttendanceForSession(Long sessionId) {
        return attendanceDao.findBySession(sessionId);
    }

    @Override public double getStudentAttendanceRate(Long studentId, Long academicYearId) {
        return attendanceDao.getAttendanceRate(studentId, academicYearId);
    }

    @Override public int getAbsentDays(Long studentId, Long termId) {
        return attendanceDao.countAbsentDays(studentId, termId);
    }

    @Override public double getSchoolAttendanceRateToday(Long schoolId) {
        return attendanceDao.getSchoolAttendanceRate(schoolId, LocalDate.now());
    }
}
