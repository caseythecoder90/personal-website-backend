package com.caseyquinn.personal_website.exception.data;

import com.caseyquinn.personal_website.exception.BaseException;

public class DataAccessException extends BaseException {
    public DataAccessException(String errorCode, String message, Throwable cause, Object... parameters) {
        super(errorCode, message, cause, parameters);
    }
}