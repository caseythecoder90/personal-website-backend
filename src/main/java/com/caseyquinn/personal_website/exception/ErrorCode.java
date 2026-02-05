package com.caseyquinn.personal_website.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // Not Found
    NOT_FOUND("NOT_FOUND", "Resource not found"),

    // Business / Validation
    DUPLICATE_RESOURCE("DUPLICATE_RESOURCE", "A resource with that value already exists"),
    VALIDATION_FAILED("VALIDATION_FAILED", "Validation failed"),
    MAX_PROJECTS_EXCEEDED("MAX_PROJECTS", "Maximum number of projects reached"),
    CANNOT_DELETE_PUBLISHED("DELETE_PUBLISHED", "Cannot delete a published resource"),
    TECHNOLOGY_IN_USE("TECH_IN_USE", "Technology is in use by one or more projects"),
    DUPLICATE_TECH_ASSOCIATION("DUPLICATE_TECH_ASSOC", "Technology is already associated with this project"),

    // Data Access
    DB_CONNECTION_ERROR("DB_CONNECTION", "Database connection failed"),
    DB_INTEGRITY_ERROR("DB_INTEGRITY", "Data integrity violation"),
    DB_ACCESS_ERROR("DB_ACCESS", "A data access error occurred"),

    // Security
    FORBIDDEN("FORBIDDEN", "Access denied"),

    // Rate Limiting
    RATE_LIMIT_EXCEEDED("RATE_LIMIT", "Too many requests"),

    // Generic
    INTERNAL_ERROR("INTERNAL_ERROR", "An unexpected error occurred");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}
