package com.digischool.emis.model.student;

import com.digischool.emis.model.BaseEntity;

/**
 * Guardian/Parent entity for the Student Information System.
 */
public class Guardian extends BaseEntity {

    private Long userId;
    private String firstName;
    private String lastName;
    private String relationshipType; // FATHER, MOTHER, GUARDIAN, SIBLING
    private String nationalId;
    private String phonePrimary;
    private String phoneSecondary;
    private String email;
    private String occupation;
    private String employer;
    private String physicalAddress;
    private String postalAddress;
    private boolean isEmergencyContact;
    private boolean isFeePayer;

    public Guardian() {
        this.isEmergencyContact = false;
        this.isFeePayer = false;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getRelationshipType() { return relationshipType; }
    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public String getPhonePrimary() { return phonePrimary; }
    public void setPhonePrimary(String phonePrimary) { this.phonePrimary = phonePrimary; }

    public String getPhoneSecondary() { return phoneSecondary; }
    public void setPhoneSecondary(String phoneSecondary) { this.phoneSecondary = phoneSecondary; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getEmployer() { return employer; }
    public void setEmployer(String employer) { this.employer = employer; }

    public String getPhysicalAddress() { return physicalAddress; }
    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public String getPostalAddress() { return postalAddress; }
    public void setPostalAddress(String postalAddress) { this.postalAddress = postalAddress; }

    public boolean isEmergencyContact() { return isEmergencyContact; }
    public void setEmergencyContact(boolean emergencyContact) {
        isEmergencyContact = emergencyContact;
    }

    public boolean isFeePayer() { return isFeePayer; }
    public void setFeePayer(boolean feePayer) { isFeePayer = feePayer; }
}
