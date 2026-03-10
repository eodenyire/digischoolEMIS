package com.digischool.emis.model.academic;

import com.digischool.emis.model.BaseEntity;

/**
 * Represents a class (stream) in a school for a specific academic year and grade level.
 */
public class SchoolClass extends BaseEntity {

    private Long schoolId;
    private Long gradeLevelId;
    private Long academicYearId;
    private String name;
    private String section;
    private int capacity;
    private Long classTeacherId;
    private String roomNumber;
    private boolean isActive;

    public SchoolClass() {
        this.capacity = 45;
        this.isActive = true;
    }

    // Getters and Setters
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public Long getGradeLevelId() { return gradeLevelId; }
    public void setGradeLevelId(Long gradeLevelId) { this.gradeLevelId = gradeLevelId; }

    public Long getAcademicYearId() { return academicYearId; }
    public void setAcademicYearId(Long academicYearId) { this.academicYearId = academicYearId; }

    public String getName()                { return name; }
    public void setName(String name)       { this.name = name; }
    public String getClassName()           { return name; }
    public void setClassName(String v)     { this.name = v; }
    public String getSection()             { return section; }
    public void setSection(String v)       { this.section = v; }
    public String getStream()              { return section; }
    public void setStream(String v)        { this.section = v; }
    public int getCapacity()               { return capacity; }
    public void setCapacity(int capacity)  { this.capacity = capacity; }

    public Long getClassTeacherId() { return classTeacherId; }
    public void setClassTeacherId(Long classTeacherId) { this.classTeacherId = classTeacherId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
