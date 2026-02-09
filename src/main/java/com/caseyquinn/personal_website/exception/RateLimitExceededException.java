package com.caseyquinn.personal_website.exception;

/**
 * Exception thrown when a client exceeds the configured rate limit.
 */
public class RateLimitExceededException extends BaseException {

    /**
     * Constructs a rate limit exceeded exception with a detail message.
     *
     * @param message the detail message
     */
    public RateLimitExceededException(String message) {
        super(ErrorCode.RATE_LIMIT_EXCEEDED, message);
    }
}
