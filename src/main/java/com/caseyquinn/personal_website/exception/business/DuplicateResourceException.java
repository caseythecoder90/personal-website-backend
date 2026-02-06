package com.caseyquinn.personal_website.exception.business;

import com.caseyquinn.personal_website.exception.ErrorCode;

/**
 * Exception thrown when attempting to create a resource that already exists.
 */
public class DuplicateResourceException extends BusinessException {

    /**
     * Constructs a new DuplicateResourceException with entity details.
     *
     * @param entityType the type of entity
     * @param fieldName the field name that must be unique
     * @param value the duplicate value
     */
    public DuplicateResourceException(String entityType, String fieldName, String value) {
        super(ErrorCode.DUPLICATE_RESOURCE,
                String.format("%s with %s '%s' already exists", entityType, fieldName, value));
    }
}
