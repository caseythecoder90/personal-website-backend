package com.caseyquinn.personal_website.service;

/**
 * Constants for Cloudinary API parameter keys and response keys.
 * Scoped to Cloudinary integration only — used by {@link CloudinaryService}.
 */
final class CloudinaryConstants {

    private CloudinaryConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // Upload parameter keys
    static final String PARAM_FOLDER = "folder";
    static final String PARAM_RESOURCE_TYPE = "resource_type";
    static final String PARAM_USE_FILENAME = "use_filename";
    static final String PARAM_UNIQUE_FILENAME = "unique_filename";

    // Response keys
    static final String RESPONSE_URL = "url";
    static final String RESPONSE_SECURE_URL = "secure_url";
    static final String RESPONSE_PUBLIC_ID = "public_id";
    static final String RESPONSE_FORMAT = "format";
    static final String RESPONSE_BYTES = "bytes";
    static final String RESPONSE_WIDTH = "width";
    static final String RESPONSE_HEIGHT = "height";
    static final String RESPONSE_RESULT = "result";

    // Delete result values
    static final String DELETE_OK = "ok";
    static final String DELETE_NOT_FOUND = "not found";

    // Resource type values
    static final String RESOURCE_TYPE_IMAGE = "image";
    static final String RESOURCE_TYPE_RAW = "raw";
}