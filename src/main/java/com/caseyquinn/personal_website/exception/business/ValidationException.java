package com.caseyquinn.personal_website.exception.business;

import com.caseyquinn.personal_website.exception.ErrorCode;

/**
 * Exception thrown when business validation rules are violated.
 */
public class ValidationException extends BusinessException {

    /**
     * Constructs a new ValidationException with error code and message.
     *
     * @param errorCode the error code
     * @param message the detail message
     */
    public ValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
