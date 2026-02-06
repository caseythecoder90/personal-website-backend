package com.caseyquinn.personal_website.exception;

/**
 * Exception thrown when an operation is forbidden or access is denied.
 */
public class ForbiddenException extends BaseException {

    /**
     * Constructs a new ForbiddenException with the specified message.
     *
     * @param message the detail message
     */
    public ForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }
}
