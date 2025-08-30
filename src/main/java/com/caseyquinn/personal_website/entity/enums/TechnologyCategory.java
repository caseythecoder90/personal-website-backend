package com.caseyquinn.personal_website.entity.enums;

import lombok.Getter;

@Getter
public enum TechnologyCategory {
    LANGUAGE("Programming Language"),
    FRAMEWORK("Framework"),
    LIBRARY("Library"),
    DATABASE("Database"),
    TOOL("Development Tool"),
    CLOUD("Cloud Service"),
    DEPLOYMENT("Deployment"),
    TESTING("Testing");
    
    private final String displayName;
    
    TechnologyCategory(String displayName) {
        this.displayName = displayName;
    }

}