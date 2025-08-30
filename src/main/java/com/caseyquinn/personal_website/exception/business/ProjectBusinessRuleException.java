package com.caseyquinn.personal_website.exception.business;

public class ProjectBusinessRuleException extends BusinessException {
    public ProjectBusinessRuleException(String message, Object... parameters) {
        super("PROJECT_BUSINESS_RULE", message, parameters);
    }
}