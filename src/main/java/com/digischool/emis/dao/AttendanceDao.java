package com.digischool.emis.dao;

import com.digischool.emis.model.attendance.AttendanceSession;
import com.digischool.emis.model.attendance.StudentAttendance;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceDao extends GenericDao<AttendanceSession, Long> {
    AttendanceSession saveSession(AttendanceSession session);
    List<AttendanceSession> findSessionsByClass(Long classId, LocalDate date);
    List<StudentAttendance> findBySession(Long sessionId);
    Optional<StudentAttendance> findStudentRecord(Long sessionId, Long studentId);
    StudentAttendance saveRecord(StudentAttendance record);
    StudentAttendance updateRecord(StudentAttendance record);
    /** Returns attendance percentage (0–100) for a student in an academic year. */
    double getAttendanceRate(Long studentId, Long academicYearId);
    /** Counts absent days for a student in a term. */
    int countAbsentDays(Long studentId, Long termId);
    /** Returns school-wide attendance rate for a given date. */
    double getSchoolAttendanceRate(Long schoolId, LocalDate date);
}
