package com.caseyquinn.personal_website.exception.business;

public class ProjectValidationException extends BusinessException {
    public ProjectValidationException(String message, Object... parameters) {
        super("PROJECT_VALIDATION_ERROR", message, parameters);
    }
}