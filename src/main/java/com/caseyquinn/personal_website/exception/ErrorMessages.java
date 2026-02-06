package com.caseyquinn.personal_website.exception;

/**
 * Constants for error messages used throughout the application.
 * Centralizes error message strings for consistency and maintainability.
 */
public final class ErrorMessages {

    private ErrorMessages() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // File Upload Messages
    public static final String FILE_EMPTY = "File is empty";
    public static final String FILE_SIZE_EXCEEDED_FORMAT = "File size (%d bytes) exceeds maximum allowed (%d bytes)";
    public static final String INVALID_FILE_TYPE_FORMAT = "Invalid file type: %s. Allowed types: %s";
    public static final String FILE_CONTENT_MISMATCH = "File content does not match declared content type";
    public static final String FILE_READ_ERROR = "Failed to read file: %s";
    public static final String IMAGE_UPLOAD_FAILED = "Failed to upload image: %s";

    // Project Image Messages
    public static final String MAX_IMAGES_EXCEEDED_FORMAT = "Project already has maximum allowed images (%d)";
    public static final String IMAGE_OWNERSHIP_MISMATCH = "Image does not belong to specified project";

    // Project Messages
    public static final String MAX_PROJECTS_EXCEEDED = "Maximum number of projects (10) reached";
    public static final String CANNOT_DELETE_PUBLISHED = "Cannot delete published projects. Unpublish first.";
    public static final String TECHNOLOGY_ALREADY_ASSOCIATED = "Technology is already associated with this project";
    public static final String TECHNOLOGY_NOT_ASSOCIATED = "Technology is not associated with this project";

    // Technology Messages
    public static final String TECHNOLOGY_IN_USE_FORMAT = "Technology is currently associated with %d project(s) and cannot be deleted";
}
