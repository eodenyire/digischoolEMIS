package com.digischool.emis.model.academic;

import com.digischool.emis.model.BaseEntity;
import java.time.LocalDate;

/**
 * Represents a school entity in DigiSchool EMIS.
 */
public class School extends BaseEntity {

    private String name;
    private String knecCode;
    private String nemisCode;
    private String registrationNumber;
    private String schoolType;
    private String ownership;   // PUBLIC, PRIVATE, MISSION, INTERNATIONAL
    private String schoolLevel;
    private String curriculumType;
    private String email;
    private String phone;
    private String website;
    private String physicalAddress;
    private String postalAddress;
    private String county;
    private String subCounty;
    private String ward;
    private String logoUrl;
    private int establishedYear;
    private String motto;
    private String vision;
    private String mission;
    private String principalName;
    private boolean isBoarding;
    private boolean isDaySchool;
    private boolean isActive;

    public School() {
        this.curriculumType = "CBC";
        this.isDaySchool = true;
        this.isBoarding = false;
        this.isActive = true;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getKnecCode() { return knecCode; }
    public void setKnecCode(String knecCode) { this.knecCode = knecCode; }

    public String getNemisCode() { return nemisCode; }
    public void setNemisCode(String nemisCode) { this.nemisCode = nemisCode; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getSchoolType() { return schoolType; }
    public void setSchoolType(String schoolType) { this.schoolType = schoolType; }

    public String getOwnership() { return ownership != null ? ownership : schoolType; }
    public void setOwnership(String v) { this.ownership = v; }

    public String getSchoolLevel() { return schoolLevel; }
    public void setSchoolLevel(String schoolLevel) { this.schoolLevel = schoolLevel; }

    public String getCurriculumType() { return curriculumType; }
    public void setCurriculumType(String curriculumType) { this.curriculumType = curriculumType; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getPhysicalAddress() { return physicalAddress; }
    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public String getPostalAddress() { return postalAddress; }
    public void setPostalAddress(String postalAddress) { this.postalAddress = postalAddress; }

    public String getCounty() { return county; }
    public void setCounty(String county) { this.county = county; }

    public String getSubCounty() { return subCounty; }
    public void setSubCounty(String subCounty) { this.subCounty = subCounty; }

    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public int getEstablishedYear() { return establishedYear; }
    public void setEstablishedYear(int establishedYear) { this.establishedYear = establishedYear; }
    public void setEstablishedYear(Integer v) { if (v != null) this.establishedYear = v; }

    public String getMotto() { return motto; }
    public void setMotto(String motto) { this.motto = motto; }

    public String getVision() { return vision; }
    public void setVision(String vision) { this.vision = vision; }

    public String getMission() { return mission; }
    public void setMission(String mission) { this.mission = mission; }

    public String getPrincipalName() { return principalName; }
    public void setPrincipalName(String principalName) { this.principalName = principalName; }

    public boolean isBoarding() { return isBoarding; }
    public void setBoarding(boolean boarding) { isBoarding = boarding; }

    public boolean isDaySchool() { return isDaySchool; }
    public void setDaySchool(boolean daySchool) { isDaySchool = daySchool; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return "School{id=" + id + ", name='" + name + "', knecCode='" + knecCode + "'}";
    }
}
