package com.digischool.emis.model.finance;

import com.digischool.emis.model.BaseEntity;
import java.math.BigDecimal;

/**
 * A fee structure groups the fee items applicable to a grade for a year.
 */
public class FeeStructure extends BaseEntity {

    private Long schoolId;
    private Long academicYearId;
    private Long gradeLevelId;
    private String name;
    private String description;
    private boolean isBoarding;
    private boolean isActive;

    public FeeStructure() {
        this.isBoarding = false;
        this.isActive   = true;
    }

    public Long getSchoolId()                 { return schoolId; }
    public void setSchoolId(Long v)           { this.schoolId = v; }
    public Long getAcademicYearId()           { return academicYearId; }
    public void setAcademicYearId(Long v)     { this.academicYearId = v; }
    public Long getGradeLevelId()             { return gradeLevelId; }
    public void setGradeLevelId(Long v)       { this.gradeLevelId = v; }
    public String getName()                   { return name; }
    public void setName(String v)             { this.name = v; }
    public String getDescription()            { return description; }
    public void setDescription(String v)      { this.description = v; }
    public boolean isBoarding()               { return isBoarding; }
    public void setBoarding(boolean v)        { this.isBoarding = v; }
    public boolean isActive()                 { return isActive; }
    public void setActive(boolean v)          { this.isActive = v; }
}
