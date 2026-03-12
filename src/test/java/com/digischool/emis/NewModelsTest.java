package com.digischool.emis;

import com.digischool.emis.model.transport.*;
import com.digischool.emis.model.boarding.*;
import com.digischool.emis.model.discipline.*;
import com.digischool.emis.model.lms.*;
import com.digischool.emis.model.timetable.*;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.time.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Wave 2-4 model classes.
 */
@DisplayName("New Models Tests")
class NewModelsTest {

    // ============================================================
    // TransportRoute Tests
    // ============================================================

    @Test
    @DisplayName("TransportRoute should be active by default")
    void testTransportRouteDefaultActive() {
        TransportRoute route = new TransportRoute();
        assertThat(route.isActive()).isTrue();
    }

    @Test
    @DisplayName("TransportRoute routeCode and routeName fields should be settable")
    void testTransportRouteFields() {
        TransportRoute route = new TransportRoute();
        route.setRouteCode("RT-001");
        route.setRouteName("Westlands Express");
        assertThat(route.getRouteCode()).isEqualTo("RT-001");
        assertThat(route.getRouteName()).isEqualTo("Westlands Express");
    }

    // ============================================================
    // TransportAssignment Tests
    // ============================================================

    @Test
    @DisplayName("TransportAssignment should be active by default")
    void testTransportAssignmentDefaultActive() {
        TransportAssignment assignment = new TransportAssignment();
        assertThat(assignment.isActive()).isTrue();
    }

    // ============================================================
    // Dormitory Tests
    // ============================================================

    @Test
    @DisplayName("Dormitory should be active by default")
    void testDormitoryDefaultActive() {
        Dormitory dorm = new Dormitory();
        assertThat(dorm.isActive()).isTrue();
    }

    @Test
    @DisplayName("Dormitory isFull returns true when occupancy equals capacity")
    void testDormitoryIsFullWhenAtCapacity() {
        Dormitory dorm = new Dormitory();
        dorm.setCapacity(10);
        assertThat(dorm.isFull(10)).isTrue();
    }

    @Test
    @DisplayName("Dormitory isFull returns false when occupancy is below capacity")
    void testDormitoryIsNotFullWhenBelowCapacity() {
        Dormitory dorm = new Dormitory();
        dorm.setCapacity(10);
        assertThat(dorm.isFull(9)).isFalse();
    }

    // ============================================================
    // BedAllocation Tests
    // ============================================================

    @Test
    @DisplayName("BedAllocation should be active by default")
    void testBedAllocationDefaultActive() {
        BedAllocation allocation = new BedAllocation();
        assertThat(allocation.isActive()).isTrue();
    }

    // ============================================================
    // DisciplineIncident Tests
    // ============================================================

    @Test
    @DisplayName("DisciplineIncident should not be resolved by default")
    void testDisciplineIncidentDefaultResolved() {
        DisciplineIncident incident = new DisciplineIncident();
        assertThat(incident.isResolved()).isFalse();
    }

    // ============================================================
    // DisciplineAction Tests
    // ============================================================

    @Test
    @DisplayName("DisciplineAction actionType field should be settable")
    void testDisciplineActionFields() {
        DisciplineAction action = new DisciplineAction();
        action.setActionType("suspension");
        action.setActionDetails("Three-day suspension");
        assertThat(action.getActionType()).isEqualTo("suspension");
        assertThat(action.getActionDetails()).isEqualTo("Three-day suspension");
    }

    // ============================================================
    // Course Tests
    // ============================================================

    @Test
    @DisplayName("Course status should default to 'active'")
    void testCourseDefaultStatus() {
        Course course = new Course();
        assertThat(course.getStatus()).isEqualTo("active");
    }

    // ============================================================
    // CourseAssignment Tests
    // ============================================================

    @Test
    @DisplayName("CourseAssignment title field should be settable")
    void testCourseAssignmentFields() {
        CourseAssignment courseAssignment = new CourseAssignment();
        courseAssignment.setTitle("Essay on Water Cycle");
        courseAssignment.setMaxScore(new BigDecimal("100"));
        assertThat(courseAssignment.getTitle()).isEqualTo("Essay on Water Cycle");
        assertThat(courseAssignment.getMaxScore()).isEqualByComparingTo(new BigDecimal("100"));
    }

    // ============================================================
    // CourseSubmission Tests
    // ============================================================

    @Test
    @DisplayName("CourseSubmission isLate returns true when submitted after due date")
    void testCourseSubmissionIsLateAfterDue() {
        CourseSubmission submission = new CourseSubmission();
        LocalDateTime dueAt = LocalDateTime.of(2024, 3, 15, 23, 59);
        submission.setSubmittedAt(LocalDateTime.of(2024, 3, 16, 9, 0));
        assertThat(submission.isLate(dueAt)).isTrue();
    }

    @Test
    @DisplayName("CourseSubmission isLate returns false when submitted before due date")
    void testCourseSubmissionIsNotLateBeforeDue() {
        CourseSubmission submission = new CourseSubmission();
        LocalDateTime dueAt = LocalDateTime.of(2024, 3, 15, 23, 59);
        submission.setSubmittedAt(LocalDateTime.of(2024, 3, 14, 10, 0));
        assertThat(submission.isLate(dueAt)).isFalse();
    }

    @Test
    @DisplayName("CourseSubmission isLate returns false when submittedAt is null")
    void testCourseSubmissionIsLateNullSubmittedAt() {
        CourseSubmission submission = new CourseSubmission();
        LocalDateTime dueAt = LocalDateTime.of(2024, 3, 15, 23, 59);
        assertThat(submission.isLate(dueAt)).isFalse();
    }

    @Test
    @DisplayName("CourseSubmission isLate returns false when dueAt is null")
    void testCourseSubmissionIsLateNullDueAt() {
        CourseSubmission submission = new CourseSubmission();
        submission.setSubmittedAt(LocalDateTime.of(2024, 3, 16, 9, 0));
        assertThat(submission.isLate(null)).isFalse();
    }

    // ============================================================
    // Timetable Tests
    // ============================================================

    @Test
    @DisplayName("Timetable status should default to 'draft'")
    void testTimetableDefaultStatus() {
        Timetable timetable = new Timetable();
        assertThat(timetable.getStatus()).isEqualTo("draft");
    }

    @Test
    @DisplayName("Timetable isPublished returns false initially")
    void testTimetableIsNotPublishedInitially() {
        Timetable timetable = new Timetable();
        assertThat(timetable.isPublished()).isFalse();
    }

    @Test
    @DisplayName("Timetable isPublished returns true when status set to 'published'")
    void testTimetableIsPublishedWhenStatusPublished() {
        Timetable timetable = new Timetable();
        timetable.setStatus("published");
        assertThat(timetable.isPublished()).isTrue();
    }

    // ============================================================
    // TimetableSlot Tests
    // ============================================================

    @Test
    @DisplayName("TimetableSlot getDurationMinutes returns 60 for 09:00-10:00")
    void testTimetableSlotDurationMinutes() {
        TimetableSlot slot = new TimetableSlot();
        slot.setStartTime(LocalTime.of(9, 0));
        slot.setEndTime(LocalTime.of(10, 0));
        assertThat(slot.getDurationMinutes()).isEqualTo(60);
    }

    @Test
    @DisplayName("TimetableSlot getDurationMinutes returns 0 when times are null")
    void testTimetableSlotDurationMinutesNull() {
        TimetableSlot slot = new TimetableSlot();
        assertThat(slot.getDurationMinutes()).isEqualTo(0);
    }
}
