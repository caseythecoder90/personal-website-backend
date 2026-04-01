package com.caseyquinn.personal_website.constants;

/**
 * Constants for Cloudinary API parameter keys and response keys.
 * Scoped to Cloudinary integration — used by CloudinaryService.
 */
public final class CloudinaryConstants {

    private CloudinaryConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // Upload parameter keys
    public static final String PARAM_FOLDER = "folder";
    public static final String PARAM_RESOURCE_TYPE = "resource_type";
    public static final String PARAM_USE_FILENAME = "use_filename";
    public static final String PARAM_UNIQUE_FILENAME = "unique_filename";

    // Response keys
    public static final String RESPONSE_URL = "url";
    public static final String RESPONSE_SECURE_URL = "secure_url";
    public static final String RESPONSE_PUBLIC_ID = "public_id";
    public static final String RESPONSE_FORMAT = "format";
    public static final String RESPONSE_BYTES = "bytes";
    public static final String RESPONSE_WIDTH = "width";
    public static final String RESPONSE_HEIGHT = "height";
    public static final String RESPONSE_RESULT = "result";

    // Delete result values
    public static final String DELETE_OK = "ok";
    public static final String DELETE_NOT_FOUND = "not found";

    // Resource type values
    public static final String RESOURCE_TYPE_IMAGE = "image";
    public static final String RESOURCE_TYPE_RAW = "raw";
}
