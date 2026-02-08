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

    // Project Link Messages
    public static final String LINK_OWNERSHIP_MISMATCH = "Link does not belong to specified project";

    // Project Messages
    public static final String MAX_PROJECTS_EXCEEDED = "Maximum number of projects (10) reached";
    public static final String CANNOT_DELETE_PUBLISHED = "Cannot delete published projects. Unpublish first.";
    public static final String TECHNOLOGY_ALREADY_ASSOCIATED = "Technology is already associated with this project";
    public static final String TECHNOLOGY_NOT_ASSOCIATED = "Technology is not associated with this project";

    // Certification Messages
    public static final String CERT_TECHNOLOGY_ALREADY_ASSOCIATED = "Technology is already associated with this certification";
    public static final String CERT_TECHNOLOGY_NOT_ASSOCIATED = "Technology is not associated with this certification";
    public static final String CANNOT_DELETE_PUBLISHED_CERTIFICATION = "Cannot delete published certifications. Unpublish first.";

    // Technology Messages
    public static final String TECHNOLOGY_IN_USE_FORMAT = "Technology is currently associated with %d project(s) and cannot be deleted";

    // Blog Messages
    public static final String BLOG_POST_TITLE_EXISTS = "A blog post with this title already exists";
    public static final String BLOG_CATEGORY_NAME_EXISTS = "A category with this name already exists";
    public static final String BLOG_TAG_NAME_EXISTS = "A tag with this name already exists";
    public static final String BLOG_CATEGORY_IN_USE = "Category is associated with blog posts and cannot be deleted";
    public static final String BLOG_TAG_IN_USE = "Tag is associated with blog posts and cannot be deleted";
    public static final String BLOG_CATEGORY_ALREADY_ASSOCIATED = "Category is already associated with this post";
    public static final String BLOG_CATEGORY_NOT_ASSOCIATED = "Category is not associated with this post";
    public static final String BLOG_TAG_ALREADY_ASSOCIATED = "Tag is already associated with this post";
    public static final String BLOG_TAG_NOT_ASSOCIATED = "Tag is not associated with this post";
    public static final String CANNOT_DELETE_PUBLISHED_POST = "Cannot delete published blog posts. Unpublish first.";

    // Blog Image Messages
    public static final String BLOG_IMAGE_OWNERSHIP_MISMATCH = "Image does not belong to specified blog post";
    public static final String MAX_BLOG_IMAGES_EXCEEDED_FORMAT = "Blog post already has maximum allowed images (%d)";
}
