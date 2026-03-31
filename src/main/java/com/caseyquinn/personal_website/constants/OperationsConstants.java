package com.caseyquinn.personal_website.constants;

/**
 * Constants for operations service including encryption and health check values.
 */
public final class OperationsConstants {

    private OperationsConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ── Health ────────────────────────────────────────────────────────────

    public static final String HEALTH_STATUS_UP = "UP";

    // ── Jasypt Encryption ────────────────────────────────────────────────

    public static final String ENCRYPTION_PREFIX = "ENC(";
    public static final String ENCRYPTION_SUFFIX = ")";
}
