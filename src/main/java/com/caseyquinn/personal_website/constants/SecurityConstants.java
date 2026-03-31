package com.caseyquinn.personal_website.constants;

/**
 * Constants for security-related values including JWT, authentication, and rate limiting.
 */
public final class SecurityConstants {

    private SecurityConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ── JWT ──────────────────────────────────────────────────────────────

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final int BEARER_PREFIX_LENGTH = 7;

    // ── Rate Limit Tiers ─────────────────────────────────────────────────

    public static final String TIER_LOGIN = "login";
    public static final String TIER_ADMIN = "admin";
    public static final String TIER_PUBLIC = "public";

    // ── User Errors ──────────────────────────────────────────────────────

    public static final String USER_NOT_FOUND_BY_USERNAME = "User not found with username: ";
    public static final String USER_NOT_FOUND_BY_ID = "User not found with id: ";
}
