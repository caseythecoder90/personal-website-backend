package com.caseyquinn.personal_website.exception.data;

import com.caseyquinn.personal_website.exception.ErrorCode;

public class DataIntegrityException extends DataAccessException {

    public DataIntegrityException(String message, Throwable cause) {
        super(ErrorCode.DB_INTEGRITY_ERROR, message, cause);
    }
}