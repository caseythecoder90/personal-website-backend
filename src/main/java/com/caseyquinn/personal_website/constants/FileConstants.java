package com.caseyquinn.personal_website.constants;

/**
 * Constants for file MIME types and Cloudinary subfolder paths.
 */
public final class FileConstants {

    private FileConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ── MIME Types ────────────────────────────────────────────────────────

    public static final String MIME_PDF = "application/pdf";
    public static final String MIME_JPEG = "image/jpeg";
    public static final String MIME_PNG = "image/png";
    public static final String MIME_GIF = "image/gif";
    public static final String MIME_WEBP = "image/webp";

    // ── Cloudinary Subfolders ────────────────────────────────────────────

    public static final String SUBFOLDER_BLOG = "blog/";
    public static final String SUBFOLDER_RESUMES = "resumes";
}
