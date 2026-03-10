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
    private int levelNumber;
    private String category; // LOWER_PRIMARY, UPPER_PRIMARY, JSS, SS

    public GradeLevel() {}

    public GradeLevel(Long schoolId, int levelNumber) {
        this.schoolId = schoolId;
        this.levelNumber = levelNumber;
        this.name = "Grade " + levelNumber;
        this.category = determineCbcCategory(levelNumber);
    }

    private String determineCbcCategory(int level) {
        if (level <= 3) return "LOWER_PRIMARY";
        if (level <= 6) return "UPPER_PRIMARY";
        if (level <= 9) return "JSS";
        return "SS";
    }

    // Getters and Setters
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getLevelNumber() { return levelNumber; }
    public void setLevelNumber(int levelNumber) { this.levelNumber = levelNumber; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
