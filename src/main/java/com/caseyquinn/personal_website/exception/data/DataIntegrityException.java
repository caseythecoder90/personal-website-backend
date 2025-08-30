package com.caseyquinn.personal_website.exception.data;

public class DataIntegrityException extends DataAccessException {
    public DataIntegrityException(String message, Throwable cause) {
        super("DATA_INTEGRITY_ERROR", message, cause);
    }
}