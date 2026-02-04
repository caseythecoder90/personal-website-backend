package com.caseyquinn.personal_website.exception;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
    private final ErrorCode errorCode;

    protected BaseException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected BaseException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}