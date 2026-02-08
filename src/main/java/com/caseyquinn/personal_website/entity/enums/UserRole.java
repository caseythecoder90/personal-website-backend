package com.caseyquinn.personal_website.entity.enums;

import lombok.Getter;

/**
 * User roles for authorization.
 */
@Getter
public enum UserRole {
    USER("User"),
    ADMIN("Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }
}
