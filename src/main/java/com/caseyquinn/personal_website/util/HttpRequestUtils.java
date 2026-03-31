package com.caseyquinn.personal_website.util;

import jakarta.servlet.http.HttpServletRequest;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Utility methods for extracting information from HTTP requests.
 */
public final class HttpRequestUtils {

    private HttpRequestUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_RETRY_AFTER = "Retry-After";
    public static final String HEADER_X_RATE_LIMIT_REMAINING = "X-Rate-Limit-Remaining";

    /**
     * Extracts the client IP address from the request, checking X-Forwarded-For
     * for reverse proxy support before falling back to the remote address.
     *
     * @param request the HTTP servlet request
     * @return the client IP address
     */
    public static String extractIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader(HEADER_X_FORWARDED_FOR);
        if (isNotBlank(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
