package com.caseyquinn.personal_website.exception;

public class NotFoundException extends BaseException {

    public NotFoundException(String entityType, Object id) {
        super(ErrorCode.NOT_FOUND,
                String.format("%s not found with id: %s", entityType, id));
    }

    public NotFoundException(String entityType, String fieldName, Object value) {
        super(ErrorCode.NOT_FOUND,
                String.format("%s not found with %s: %s", entityType, fieldName, value));
    }
}
