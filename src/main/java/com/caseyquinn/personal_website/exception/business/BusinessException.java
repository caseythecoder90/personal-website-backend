package com.caseyquinn.personal_website.exception.business;

import com.caseyquinn.personal_website.exception.BaseException;
import com.caseyquinn.personal_website.exception.ErrorCode;

/**
 * Base exception class for business logic violations.
 * Mapped to HTTP 400 Bad Request responses.
 */
public abstract class BusinessException extends BaseException {

    /**
     * Constructs a new BusinessException with error code and message.
     *
     * @param errorCode the error code
     * @param message the detail message
     */
    protected BusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}