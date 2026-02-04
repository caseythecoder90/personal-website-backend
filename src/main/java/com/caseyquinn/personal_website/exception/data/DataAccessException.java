package com.caseyquinn.personal_website.exception.data;

import com.caseyquinn.personal_website.exception.BaseException;
import com.caseyquinn.personal_website.exception.ErrorCode;

public abstract class DataAccessException extends BaseException {

    protected DataAccessException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}