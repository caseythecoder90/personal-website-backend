package com.caseyquinn.personal_website.entity.enums;

import lombok.Getter;

@Getter
public enum ProjectStatus {
    PLANNING("Planning"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    MAINTAINED("Actively Maintained"),
    ARCHIVED("Archived");
    
    private final String displayName;
    
    ProjectStatus(String displayName) {
        this.displayName = displayName;
    }

}