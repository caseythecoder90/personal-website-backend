package com.caseyquinn.personal_website.exception.business;

import com.caseyquinn.personal_website.exception.BaseException;
import com.caseyquinn.personal_website.exception.ErrorCode;

public abstract class BusinessException extends BaseException {

    protected BusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}