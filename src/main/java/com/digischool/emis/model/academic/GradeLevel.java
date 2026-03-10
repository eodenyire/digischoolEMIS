package com.digischool.emis.model.academic;

import com.digischool.emis.model.BaseEntity;

/**
 * Represents a grade level (Grade 1 through Grade 13) in the Kenya CBC system.
 * Categories:
 * - LOWER_PRIMARY: Grade 1-3
 * - UPPER_PRIMARY: Grade 4-6
 * - JSS (Junior Secondary School): Grade 7-9
 * - SS (Senior Secondary School): Grade 10-13
 */
public class GradeLevel extends BaseEntity {

    private Long schoolId;
    private String name;
    private String gradeName;  // alias for name
    private String gradeCode;
    private int levelNumber;
    private int gradeOrder;
    private String category;
    private String cbcCategory; // alias for category
    private boolean isActive;

    public GradeLevel() { this.isActive = true; }

    public GradeLevel(Long schoolId, int levelNumber) {
        this();
        this.schoolId    = schoolId;
        this.levelNumber = levelNumber;
        this.gradeOrder  = levelNumber;
        this.name        = "Grade " + levelNumber;
        this.gradeName   = this.name;
        this.gradeCode   = "G" + levelNumber;
        this.category    = determineCbcCategory(levelNumber);
        this.cbcCategory = this.category;
    }

    private String determineCbcCategory(int level) {
        if (level <= 3) return "LOWER_PRIMARY";
        if (level <= 6) return "UPPER_PRIMARY";
        if (level <= 9) return "JSS";
        return "SS";
    }

    public Long getSchoolId()              { return schoolId; }
    public void setSchoolId(Long v)        { this.schoolId = v; }
    public String getName()                { return name; }
    public void setName(String name)       { this.name = name; this.gradeName = name; }
    public String getGradeName()           { return gradeName != null ? gradeName : name; }
    public void setGradeName(String v)     { this.gradeName = v; this.name = v; }
    public String getGradeCode()           { return gradeCode; }
    public void setGradeCode(String v)     { this.gradeCode = v; }
    public int getLevelNumber()            { return levelNumber; }
    public void setLevelNumber(int v)      { this.levelNumber = v; }
    public int getGradeOrder()             { return gradeOrder > 0 ? gradeOrder : levelNumber; }
    public void setGradeOrder(int v)       { this.gradeOrder = v; }
    public String getCategory()            { return category; }
    public void setCategory(String v)      { this.category = v; this.cbcCategory = v; }
    public String getCbcCategory()         { return cbcCategory != null ? cbcCategory : category; }
    public void setCbcCategory(String v)   { this.cbcCategory = v; this.category = v; }
    public boolean isActive()              { return isActive; }
    public void setActive(boolean v)       { this.isActive = v; }
}
