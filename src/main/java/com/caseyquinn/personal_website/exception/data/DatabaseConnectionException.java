package com.caseyquinn.personal_website.exception.data;

import com.caseyquinn.personal_website.exception.ErrorCode;

public class DatabaseConnectionException extends DataAccessException {

    public DatabaseConnectionException(Throwable cause) {
        super(ErrorCode.DB_CONNECTION_ERROR, "Database connection failed", cause);
    }
}