package com.caseyquinn.personal_website.exception.business;

public class DuplicateProjectException extends BusinessException {
    public DuplicateProjectException(String projectName) {
        super("PROJECT_DUPLICATE", "Project with name '%s' already exists", projectName);
    }
}