package com.caseyquinn.personal_website.exception;

import lombok.Getter;

/**
 * Base exception class for all custom application exceptions.
 * Associates each exception with an error code for consistent error handling.
 */
@Getter
public abstract class BaseException extends RuntimeException {
    private final com.caseyquinn.personal_website.exception.ErrorCode errorCode;

    /**
     * Constructs a new base exception with error code and message.
     *
     * @param errorCode the error code
     * @param message the detail message
     */
    protected BaseException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new base exception with error code, message, and cause.
     *
     * @param errorCode the error code
     * @param message the detail message
     * @param cause the cause
     */
    protected BaseException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}