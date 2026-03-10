package com.digischool.emis;

import com.digischool.emis.model.student.Student;
import com.digischool.emis.service.StudentService;
import com.digischool.emis.service.impl.StudentServiceImpl;
import com.digischool.emis.dao.StudentDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StudentService.
 * Tests business logic without requiring a database.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Student Service Tests")
class StudentServiceTest {

    @Mock
    private StudentDao studentDao;

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentService = new StudentServiceImpl(studentDao);
    }

    @Test
    @DisplayName("Should register a new student with generated admission number")
    void testRegisterStudentWithGeneratedAdmissionNumber() {
        // Arrange
        Student student = createValidStudent();
        student.setAdmissionNumber(null); // Let system generate it

        when(studentDao.countActiveStudents(1L)).thenReturn(100L);
        when(studentDao.save(any(Student.class))).thenAnswer(inv -> {
            Student s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        // Act
        Student result = studentService.registerStudent(student);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAdmissionNumber()).startsWith("ADM/");
        verify(studentDao).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw exception when registering duplicate admission number")
    void testRegisterStudentWithDuplicateAdmissionNumber() {
        // Arrange
        Student student = createValidStudent();
        student.setAdmissionNumber("ADM/2024/0001");

        when(studentDao.findByAdmissionNumber(1L, "ADM/2024/0001"))
                .thenReturn(Optional.of(student));

        // Act & Assert
        assertThatThrownBy(() -> studentService.registerStudent(student))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Should find student by ID")
    void testGetStudentById() {
        // Arrange
        Student student = createValidStudent();
        student.setId(1L);
        when(studentDao.findById(1L)).thenReturn(Optional.of(student));

        // Act
        Optional<Student> result = studentService.getStudentById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should return empty when student not found")
    void testGetStudentByIdNotFound() {
        // Arrange
        when(studentDao.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Student> result = studentService.getStudentById(999L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should get all active students in a school")
    void testGetActiveStudents() {
        // Arrange
        Student s1 = createValidStudent();
        s1.setId(1L);
        Student s2 = createValidStudent();
        s2.setId(2L);
        s2.setFirstName("Jane");

        when(studentDao.findActiveBySchool(1L)).thenReturn(Arrays.asList(s1, s2));

        // Act
        List<Student> results = studentService.getActiveStudents(1L);

        // Assert
        assertThat(results).hasSize(2);
        verify(studentDao).findActiveBySchool(1L);
    }

    @Test
    @DisplayName("Should search students by name")
    void testSearchStudents() {
        // Arrange
        Student student = createValidStudent();
        when(studentDao.searchByName(1L, "John")).thenReturn(List.of(student));

        // Act
        List<Student> results = studentService.searchStudents(1L, "John");

        // Assert
        assertThat(results).hasSize(1);
        verify(studentDao).searchByName(1L, "John");
    }

    @Test
    @DisplayName("Should return all active students when search query is empty")
    void testSearchStudentsWithEmptyQuery() {
        // Arrange
        when(studentDao.findActiveBySchool(1L)).thenReturn(List.of(createValidStudent()));

        // Act
        List<Student> results = studentService.searchStudents(1L, "");

        // Assert
        assertThat(results).hasSize(1);
        verify(studentDao).findActiveBySchool(1L);
        verify(studentDao, never()).searchByName(anyLong(), anyString());
    }

    @Test
    @DisplayName("Should withdraw (deactivate) a student")
    void testWithdrawStudent() {
        // Arrange
        Student student = createValidStudent();
        student.setId(1L);
        student.setActive(true);

        when(studentDao.findById(1L)).thenReturn(Optional.of(student));
        when(studentDao.update(any(Student.class))).thenReturn(student);

        // Act
        studentService.withdrawStudent(1L, "Family relocated");

        // Assert
        verify(studentDao).update(argThat(s -> !s.isActive()));
    }

    @Test
    @DisplayName("Should throw exception when withdrawing non-existent student")
    void testWithdrawStudentNotFound() {
        // Arrange
        when(studentDao.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> studentService.withdrawStudent(999L, "reason"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Should throw validation error for null first name")
    void testRegisterStudentWithNullFirstName() {
        // Arrange
        Student student = createValidStudent();
        student.setFirstName(null);

        // Act & Assert
        assertThatThrownBy(() -> studentService.registerStudent(student))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("first name");
    }

    @Test
    @DisplayName("Should throw validation error for null date of birth")
    void testRegisterStudentWithNullDateOfBirth() {
        // Arrange
        Student student = createValidStudent();
        student.setDateOfBirth(null);

        // Act & Assert
        assertThatThrownBy(() -> studentService.registerStudent(student))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("date of birth");
    }

    @Test
    @DisplayName("Should return correct student statistics")
    void testGetStudentStatistics() {
        // Arrange
        when(studentDao.countActiveStudents(1L)).thenReturn(500L);
        when(studentDao.countByGender(1L, "MALE")).thenReturn(260L);
        when(studentDao.countByGender(1L, "FEMALE")).thenReturn(240L);

        // Act
        StudentService.StudentStatistics stats = studentService.getStudentStatistics(1L);

        // Assert
        assertThat(stats.totalStudents()).isEqualTo(500L);
        assertThat(stats.maleStudents()).isEqualTo(260L);
        assertThat(stats.femaleStudents()).isEqualTo(240L);
    }

    @Test
    @DisplayName("Should generate correct admission number format")
    void testGenerateAdmissionNumber() {
        // Arrange
        when(studentDao.countActiveStudents(1L)).thenReturn(49L);

        // Act
        String admissionNumber = studentService.generateAdmissionNumber(1L);

        // Assert
        assertThat(admissionNumber).matches("ADM/\\d{4}/\\d{4}");
        assertThat(admissionNumber).contains("0050"); // 49 + 1
    }

    @Test
    @DisplayName("Student full name should combine first, middle, and last name")
    void testStudentFullName() {
        Student student = createValidStudent();
        student.setFirstName("John");
        student.setMiddleName("Kamau");
        student.setLastName("Njoroge");

        assertThat(student.getFullName()).isEqualTo("John Kamau Njoroge");
    }

    @Test
    @DisplayName("Student full name without middle name")
    void testStudentFullNameWithoutMiddleName() {
        Student student = createValidStudent();
        student.setFirstName("Jane");
        student.setMiddleName(null);
        student.setLastName("Wanjiku");

        assertThat(student.getFullName()).isEqualTo("Jane Wanjiku");
    }

    private Student createValidStudent() {
        Student student = new Student();
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setDateOfBirth(LocalDate.of(2010, 1, 15));
        student.setGender("MALE");
        student.setSchoolId(1L);
        student.setAdmissionNumber("ADM/2024/0001");
        student.setAdmissionDate(LocalDate.now());
        return student;
    }
}
