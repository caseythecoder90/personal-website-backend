package com.caseyquinn.personal_website.exception;

import lombok.Getter;

/**
 * Enumeration of all error codes used across the application.
 * Each error code has a unique code string and default message.
 */
@Getter
public enum ErrorCode {

    // Authentication errors
    AUTHENTICATION_FAILED("AUTHENTICATION_FAILED", "Invalid username or password"),

    // Resource errors
    NOT_FOUND("NOT_FOUND", "Resource not found"),
    IMAGE_NOT_FOUND("IMAGE_NOT_FOUND", "Image not found"),

    FILE_UPLOAD_ERROR("FILE_UPLOAD", "File upload failed"),
    FILE_SIZE_EXCEEDED("FILE_TOO_LARGE", "File size exceeds maximum allowed"),
    INVALID_FILE_TYPE("INVALID_FILE", "Invalid file type"),
    MAX_IMAGES_EXCEEDED("MAX_IMAGES", "Maximum number of images per project reached"),
    CLOUDINARY_ERROR("CLOUDINARY_ERROR", "Cloud storage service error"),

    DUPLICATE_RESOURCE("DUPLICATE_RESOURCE", "A resource with that value already exists"),
    VALIDATION_FAILED("VALIDATION_FAILED", "Validation failed"),
    MAX_PROJECTS_EXCEEDED("MAX_PROJECTS", "Maximum number of projects reached"),
    CANNOT_DELETE_PUBLISHED("DELETE_PUBLISHED", "Cannot delete a published resource"),
    TECHNOLOGY_IN_USE("TECH_IN_USE", "Technology is in use by one or more projects"),
    DUPLICATE_TECH_ASSOCIATION("DUPLICATE_TECH_ASSOC", "Technology is already associated with this project"),
    DUPLICATE_CERT_TECH_ASSOCIATION("DUPLICATE_CERT_TECH_ASSOC", "Technology is already associated with this certification"),
    DUPLICATE_BLOG_CATEGORY_ASSOCIATION("DUPLICATE_BLOG_CAT_ASSOC", "Category is already associated with this blog post"),
    DUPLICATE_BLOG_TAG_ASSOCIATION("DUPLICATE_BLOG_TAG_ASSOC", "Tag is already associated with this blog post"),
    MAX_BLOG_IMAGES_EXCEEDED("MAX_BLOG_IMAGES", "Maximum number of images per blog post reached"),

    DB_CONNECTION_ERROR("DB_CONNECTION", "Database connection failed"),
    DB_INTEGRITY_ERROR("DB_INTEGRITY", "Data integrity violation"),
    DB_ACCESS_ERROR("DB_ACCESS", "A data access error occurred"),

    FORBIDDEN("FORBIDDEN", "Access denied"),

    RATE_LIMIT_EXCEEDED("RATE_LIMIT", "Too many requests"),

    INTERNAL_ERROR("INTERNAL_ERROR", "An unexpected error occurred");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}
