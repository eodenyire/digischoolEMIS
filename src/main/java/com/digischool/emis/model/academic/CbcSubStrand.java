package com.digischool.emis.model.academic;

import com.digischool.emis.model.BaseEntity;

/**
 * Represents a CBC Sub-Strand (specific topic within a strand).
 * Example: Under strand "Numbers", a sub-strand might be "Whole Numbers"
 */
public class CbcSubStrand extends BaseEntity {

    private Long strandId;
    private String name;
    private String code;
    private String description;

    public CbcSubStrand() {}

    // Getters and Setters
    public Long getStrandId() { return strandId; }
    public void setStrandId(Long strandId) { this.strandId = strandId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
