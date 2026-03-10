package com.digischool.emis;

import com.digischool.emis.model.academic.GradeLevel;
import com.digischool.emis.model.academic.School;
import com.digischool.emis.model.assessment.Assessment;
import com.digischool.emis.model.assessment.AssessmentResult;
import com.digischool.emis.model.finance.FeePayment;
import com.digischool.emis.model.finance.StudentInvoice;
import com.digischool.emis.model.student.Guardian;
import com.digischool.emis.model.student.Student;
import com.digischool.emis.model.teacher.Teacher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for model classes.
 * Validates model behavior, defaults, and business rules.
 */
@DisplayName("Model Entity Tests")
class ModelEntityTest {

    // ============================================================
    // Student Tests
    // ============================================================

    @Test
    @DisplayName("Student default values should be set correctly")
    void testStudentDefaults() {
        Student student = new Student();
        assertThat(student.getNationality()).isEqualTo("Kenyan");
        assertThat(student.isActive()).isTrue();
    }

    @Test
    @DisplayName("Student full name without middle name")
    void testStudentFullNameWithoutMiddleName() {
        Student student = new Student();
        student.setFirstName("Alice");
        student.setLastName("Wangari");
        assertThat(student.getFullName()).isEqualTo("Alice Wangari");
    }

    @Test
    @DisplayName("Student full name with middle name")
    void testStudentFullNameWithMiddleName() {
        Student student = new Student();
        student.setFirstName("Alice");
        student.setMiddleName("Njeri");
        student.setLastName("Wangari");
        assertThat(student.getFullName()).isEqualTo("Alice Njeri Wangari");
    }

    @Test
    @DisplayName("Student toString should include admission number and name")
    void testStudentToString() {
        Student student = new Student();
        student.setId(1L);
        student.setAdmissionNumber("ADM/2024/001");
        student.setFirstName("John");
        student.setLastName("Doe");
        assertThat(student.toString()).contains("ADM/2024/001");
        assertThat(student.toString()).contains("John Doe");
    }

    // ============================================================
    // GradeLevel Tests (CBC Kenya Specific)
    // ============================================================

    @Test
    @DisplayName("Grade 1-3 should be LOWER_PRIMARY in CBC system")
    void testGradeLevelLowerPrimary() {
        for (int level = 1; level <= 3; level++) {
            GradeLevel grade = new GradeLevel(1L, level);
            assertThat(grade.getCategory())
                    .as("Grade %d should be LOWER_PRIMARY", level)
                    .isEqualTo("LOWER_PRIMARY");
        }
    }

    @Test
    @DisplayName("Grade 4-6 should be UPPER_PRIMARY in CBC system")
    void testGradeLevelUpperPrimary() {
        for (int level = 4; level <= 6; level++) {
            GradeLevel grade = new GradeLevel(1L, level);
            assertThat(grade.getCategory())
                    .as("Grade %d should be UPPER_PRIMARY", level)
                    .isEqualTo("UPPER_PRIMARY");
        }
    }

    @Test
    @DisplayName("Grade 7-9 should be JSS (Junior Secondary School) in CBC system")
    void testGradeLevelJSS() {
        for (int level = 7; level <= 9; level++) {
            GradeLevel grade = new GradeLevel(1L, level);
            assertThat(grade.getCategory())
                    .as("Grade %d should be JSS", level)
                    .isEqualTo("JSS");
        }
    }

    @Test
    @DisplayName("Grade 10-13 should be SS (Senior Secondary) in CBC system")
    void testGradeLevelSS() {
        for (int level = 10; level <= 13; level++) {
            GradeLevel grade = new GradeLevel(1L, level);
            assertThat(grade.getCategory())
                    .as("Grade %d should be SS", level)
                    .isEqualTo("SS");
        }
    }

    @Test
    @DisplayName("GradeLevel name should be auto-set as 'Grade N'")
    void testGradeLevelName() {
        GradeLevel grade = new GradeLevel(1L, 5);
        assertThat(grade.getName()).isEqualTo("Grade 5");
    }

    // ============================================================
    // School Tests
    // ============================================================

    @Test
    @DisplayName("School default curriculum should be CBC")
    void testSchoolDefaultCurriculum() {
        School school = new School();
        assertThat(school.getCurriculumType()).isEqualTo("CBC");
        assertThat(school.isDaySchool()).isTrue();
        assertThat(school.isBoarding()).isFalse();
        assertThat(school.isActive()).isTrue();
    }

    // ============================================================
    // Guardian Tests
    // ============================================================

    @Test
    @DisplayName("Guardian full name should combine first and last name")
    void testGuardianFullName() {
        Guardian guardian = new Guardian();
        guardian.setFirstName("James");
        guardian.setLastName("Mwangi");
        assertThat(guardian.getFullName()).isEqualTo("James Mwangi");
    }

    @Test
    @DisplayName("Guardian default values")
    void testGuardianDefaults() {
        Guardian guardian = new Guardian();
        assertThat(guardian.isEmergencyContact()).isFalse();
        assertThat(guardian.isFeePayer()).isFalse();
    }

    // ============================================================
    // Teacher Tests
    // ============================================================

    @Test
    @DisplayName("Teacher full name should include middle name when present")
    void testTeacherFullName() {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Samuel");
        teacher.setMiddleName("Kiptoo");
        teacher.setLastName("Mutai");
        assertThat(teacher.getFullName()).isEqualTo("Samuel Kiptoo Mutai");
    }

    @Test
    @DisplayName("Teacher default nationality should be Kenyan")
    void testTeacherDefaults() {
        Teacher teacher = new Teacher();
        assertThat(teacher.getNationality()).isEqualTo("Kenyan");
        assertThat(teacher.isActive()).isTrue();
        assertThat(teacher.isClassTeacher()).isFalse();
    }

    // ============================================================
    // Assessment Tests
    // ============================================================

    @Test
    @DisplayName("New assessment should not be published by default")
    void testAssessmentDefaults() {
        Assessment assessment = new Assessment();
        assertThat(assessment.isPublished()).isFalse();
    }

    @Test
    @DisplayName("Assessment result should not be absent by default")
    void testAssessmentResultDefaults() {
        AssessmentResult result = new AssessmentResult();
        assertThat(result.isAbsent()).isFalse();
        assertThat(result.isExcused()).isFalse();
    }

    // ============================================================
    // Finance Tests
    // ============================================================

    @Test
    @DisplayName("New invoice should have UNPAID status with zero amounts")
    void testStudentInvoiceDefaults() {
        StudentInvoice invoice = new StudentInvoice();
        assertThat(invoice.getStatus()).isEqualTo("UNPAID");
        assertThat(invoice.getDiscountAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(invoice.getScholarshipAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(invoice.getAmountPaid()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Fee payment should not be reversed by default")
    void testFeePaymentDefaults() {
        FeePayment payment = new FeePayment();
        assertThat(payment.isReversed()).isFalse();
    }
}
