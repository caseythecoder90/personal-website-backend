package com.caseyquinn.personal_website.exception.data;

public class DatabaseConnectionException extends DataAccessException {
    public DatabaseConnectionException(Throwable cause) {
        super("DATABASE_CONNECTION_ERROR", "Database connection failed", cause);
    }
}