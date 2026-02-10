package com.caseyquinn.personal_website.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class NotFoundException extends BaseException {

    /**
     * Constructs a new NotFoundException with a custom message.
     *
     * @param errorCode the error code
     * @param message the detail message
     */
    public NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * Constructs a new NotFoundException for entity not found by ID.
     *
     * @param entityType the type of entity
     * @param id the entity ID
     */
    public NotFoundException(String entityType, Object id) {
        super(ErrorCode.NOT_FOUND,
                String.format("%s not found with id: %s", entityType, id));
    }

    /**
     * Constructs a new NotFoundException for entity not found by field value.
     *
     * @param entityType the type of entity
     * @param fieldName the field name
     * @param value the field value
     */
    public NotFoundException(String entityType, String fieldName, Object value) {
        super(ErrorCode.NOT_FOUND,
                String.format("%s not found with %s: %s", entityType, fieldName, value));
    }
}
