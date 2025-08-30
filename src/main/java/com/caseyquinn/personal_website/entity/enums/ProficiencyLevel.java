package com.caseyquinn.personal_website.entity.enums;

import lombok.Getter;

@Getter
public enum ProficiencyLevel {
    LEARNING("Learning"),
    FAMILIAR("Familiar"),
    PROFICIENT("Proficient"),
    EXPERT("Expert");
    
    private final String displayName;
    
    ProficiencyLevel(String displayName) {
        this.displayName = displayName;
    }

}