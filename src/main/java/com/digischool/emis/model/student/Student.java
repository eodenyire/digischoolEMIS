package com.digischool.emis.model.student;

import com.digischool.emis.model.BaseEntity;
import java.time.LocalDate;

/**
 * Student entity - core of the Student Information System (SIS).
 * Stores all student personal information, enrollment and academic history.
 */
public class Student extends BaseEntity {

    private Long userId;
    private Long schoolId;
    private String admissionNumber;
    private String nemisId;
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate dateOfBirth;
    private String gender;  // MALE, FEMALE, OTHER
    private String nationality;
    private String nationalId;
    private String birthCertificateNumber;
    private String religion;
    private String ethnicity;
    private String bloodGroup;
    private String homeLanguage;
    private String profilePictureUrl;
    private LocalDate admissionDate;
    private boolean isActive;
    private Long houseId;

    public Student() {
        this.nationality = "Kenyan";
        this.isActive = true;
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

    public String getAdmissionNumber() { return admissionNumber; }
    public void setAdmissionNumber(String admissionNumber) { this.admissionNumber = admissionNumber; }

    public String getNemisId() { return nemisId; }
    public void setNemisId(String nemisId) { this.nemisId = nemisId; }

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

    public String getBirthCertificateNumber() { return birthCertificateNumber; }
    public void setBirthCertificateNumber(String birthCertificateNumber) {
        this.birthCertificateNumber = birthCertificateNumber;
    }

    public String getReligion() { return religion; }
    public void setReligion(String religion) { this.religion = religion; }

    public String getEthnicity() { return ethnicity; }
    public void setEthnicity(String ethnicity) { this.ethnicity = ethnicity; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getHomeLanguage() { return homeLanguage; }
    public void setHomeLanguage(String homeLanguage) { this.homeLanguage = homeLanguage; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public LocalDate getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(LocalDate admissionDate) { this.admissionDate = admissionDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Long getHouseId() { return houseId; }
    public void setHouseId(Long houseId) { this.houseId = houseId; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", admissionNumber='" + admissionNumber
                + "', name='" + getFullName() + "'}";
    }
}
