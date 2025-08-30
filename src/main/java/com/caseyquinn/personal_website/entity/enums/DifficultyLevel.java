package com.caseyquinn.personal_website.entity.enums;

import lombok.Getter;

@Getter
public enum DifficultyLevel {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced"),
    EXPERT("Expert");
    
    private final String displayName;
    
    DifficultyLevel(String displayName) {
        this.displayName = displayName;
    }

}