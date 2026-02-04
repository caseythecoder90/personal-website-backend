package com.caseyquinn.personal_website.exception.business;

import com.caseyquinn.personal_website.exception.ErrorCode;

public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String entityType, String fieldName, String value) {
        super(ErrorCode.DUPLICATE_RESOURCE,
                String.format("%s with %s '%s' already exists", entityType, fieldName, value));
    }
}
