package com.caseyquinn.personal_website.exception.business;

import com.caseyquinn.personal_website.exception.BaseException;

public class BusinessException extends BaseException {
    public BusinessException(String errorCode, String message, Object... parameters) {
        super(errorCode, message, parameters);
    }
}