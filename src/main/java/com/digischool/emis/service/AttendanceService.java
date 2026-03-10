package com.digischool.emis.service;

import com.digischool.emis.model.attendance.AttendanceSession;
import com.digischool.emis.model.attendance.StudentAttendance;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AttendanceService {
    /** Open a new attendance session for a class period. */
    AttendanceSession openSession(Long classId, Long subjectId, Long teacherId, LocalDate date, int period);
    /** Mark a student present/absent/late in a session. */
    StudentAttendance markAttendance(Long sessionId, Long studentId, String status, String excuseReason, Long recordedBy);
    /** Bulk mark attendance for a whole class from a status map (studentId → status). */
    void bulkMarkAttendance(Long sessionId, Map<Long, String> statusMap, Long recordedBy);
    /** Finalize a session — no more edits. */
    AttendanceSession finalizeSession(Long sessionId);
    List<AttendanceSession> getSessionsForClass(Long classId, LocalDate date);
    List<StudentAttendance> getAttendanceForSession(Long sessionId);
    double getStudentAttendanceRate(Long studentId, Long academicYearId);
    int getAbsentDays(Long studentId, Long termId);
    double getSchoolAttendanceRateToday(Long schoolId);
}
