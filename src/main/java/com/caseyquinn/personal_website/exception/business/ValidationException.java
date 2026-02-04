package com.caseyquinn.personal_website.exception.business;

import com.caseyquinn.personal_website.exception.ErrorCode;

public class ValidationException extends BusinessException {

    public ValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
