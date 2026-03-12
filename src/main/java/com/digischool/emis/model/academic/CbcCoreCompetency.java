package com.digischool.emis.model.academic;

import com.digischool.emis.model.BaseEntity;

/**
 * Represents a CBC Core Competency.
 * Kenya CBC tracks 7 core competencies for every student.
 */
public class CbcCoreCompetency extends BaseEntity {

    private String name;
    private String code;
    private String description;
    private String category; // COGNITIVE, SOCIAL, CREATIVE, DIGITAL, PERSONAL
    private boolean active;

    public CbcCoreCompetency() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
