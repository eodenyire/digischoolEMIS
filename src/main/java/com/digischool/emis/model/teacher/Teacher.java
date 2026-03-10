package com.digischool.emis.model.teacher;

import com.digischool.emis.model.BaseEntity;
import java.time.LocalDate;

/**
 * Teacher entity with full professional profile.
 */
public class Teacher extends BaseEntity {

    private Long userId;
    private Long schoolId;
    private String teacherNumber;
    private String tscNumber; // Teachers Service Commission number
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate dateOfBirth;
    private String gender;
    private String nationality;
    private String nationalId;
    private String phone;
    private String email;
    private String physicalAddress;
    private String profilePictureUrl;
    private LocalDate dateJoined;
    private String employmentType; // PERMANENT, CONTRACT, VOLUNTEER
    private String department;
    private String designation;
    private boolean isActive;
    private boolean isClassTeacher;

    public Teacher() {
        this.nationality = "Kenyan";
        this.isActive = true;
        this.isClassTeacher = false;
    }

    public String getFullName() {
        if (middleName != null && !middleName.isEmpty()) {
            return firstName + " " + middleName + " " + lastName;
        }
        return firstName + " " + lastName;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public String getTeacherNumber() { return teacherNumber; }
    public void setTeacherNumber(String teacherNumber) { this.teacherNumber = teacherNumber; }

    public String getTscNumber() { return tscNumber; }
    public void setTscNumber(String tscNumber) { this.tscNumber = tscNumber; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhysicalAddress() { return physicalAddress; }
    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public LocalDate getDateJoined() { return dateJoined; }
    public void setDateJoined(LocalDate dateJoined) { this.dateJoined = dateJoined; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public boolean isClassTeacher() { return isClassTeacher; }
    public void setClassTeacher(boolean classTeacher) { isClassTeacher = classTeacher; }

    @Override
    public String toString() {
        return "Teacher{id=" + id + ", tscNumber='" + tscNumber
                + "', name='" + getFullName() + "'}";
    }
}
