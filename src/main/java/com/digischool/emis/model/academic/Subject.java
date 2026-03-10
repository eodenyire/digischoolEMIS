package com.digischool.emis.model.academic;

import com.digischool.emis.model.BaseEntity;

/**
 * Represents a subject in the Kenya CBC curriculum.
 */
public class Subject extends BaseEntity {

    private Long learningAreaId;
    private Long schoolId;
    private String name;
    private String code;
    private String description;
    private boolean isElective;

    public Subject() {
        this.isElective = false;
    }

    // Getters and Setters
    public Long getLearningAreaId() { return learningAreaId; }
    public void setLearningAreaId(Long learningAreaId) { this.learningAreaId = learningAreaId; }

    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isElective() { return isElective; }
    public void setElective(boolean elective) { isElective = elective; }

    @Override
    public String toString() {
        return "Subject{id=" + id + ", name='" + name + "', code='" + code + "'}";
    }
}
