package com.digischool.emis.model.admin;

import com.digischool.emis.model.BaseEntity;

/**
 * Represents a user role in the system (e.g., ADMIN, TEACHER, STUDENT, PARENT).
 */
public class Role extends BaseEntity {

    private String name;
    private String description;
    private boolean isSystemRole;

    public Role() {}

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
        this.isSystemRole = false;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isSystemRole() { return isSystemRole; }
    public void setSystemRole(boolean systemRole) { isSystemRole = systemRole; }

    @Override
    public String toString() {
        return "Role{id=" + id + ", name='" + name + "'}";
    }
}
