package com.caseyquinn.personal_website.entity.enums;

import lombok.Getter;

@Getter
public enum ProjectType {
    PERSONAL("Personal Project"),
    PROFESSIONAL("Professional Work"),
    OPEN_SOURCE("Open Source Contribution"),
    LEARNING("Learning Exercise"),
    FREELANCE("Freelance Project");
    
    private final String displayName;
    
    ProjectType(String displayName) {
        this.displayName = displayName;
    }

}