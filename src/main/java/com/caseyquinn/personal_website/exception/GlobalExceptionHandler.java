package com.caseyquinn.personal_website.exception;

import com.caseyquinn.personal_website.dto.response.Response;
import com.caseyquinn.personal_website.dto.response.ValidationErrorResponse;
import com.caseyquinn.personal_website.exception.business.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Global exception handler for all REST controllers.
 * Translates exceptions to standardized error responses with appropriate HTTP status codes.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response<Void>> handleNotFoundException(NotFoundException ex) {
        log.warn("[{}] {}", ex.getErrorCode().getCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Response.error(ex.getErrorCode().getCode(), ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Response<Void>> handleForbiddenException(ForbiddenException ex) {
        log.warn("[{}] {}", ex.getErrorCode().getCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Response.error(ex.getErrorCode().getCode(), ex.getMessage()));
    }

    /**
     * Handles authentication failures (bad credentials, user not found, etc.).
     * Returns a generic message to prevent username enumeration attacks.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Response<Void>> handleAuthenticationException(AuthenticationException ex) {
        log.warn("[AUTHENTICATION_FAILED] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Response.error(ErrorCode.AUTHENTICATION_FAILED.getCode(),
                        ErrorCode.AUTHENTICATION_FAILED.getDefaultMessage()));
    }

    /**
     * Handles rate limit exceeded responses with Retry-After header.
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Response<Void>> handleRateLimitExceeded(RateLimitExceededException ex) {
        log.warn("[{}] {}", ex.getErrorCode().getCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", "60")
                .body(Response.error(ex.getErrorCode().getCode(), ex.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response<Void>> handleBusinessException(BusinessException ex) {
        log.warn("[{}] {}", ex.getErrorCode().getCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.error(ex.getErrorCode().getCode(), ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("[DB_INTEGRITY] {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(ErrorCode.DB_INTEGRITY_ERROR.getCode(), "A data integrity error occurred"));
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<Response<Void>> handleDataAccessResourceFailure(DataAccessResourceFailureException ex) {
        log.error("[DB_CONNECTION] Database connection failed after retries", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(ErrorCode.DB_CONNECTION_ERROR.getCode(), "Database connection failed. Please try again later."));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Response<Void>> handleDataAccessException(DataAccessException ex) {
        log.error("[DB_ACCESS] {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(ErrorCode.DB_ACCESS_ERROR.getCode(), "A data access error occurred. Please try again later."));
    }
    
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFound(NoResourceFoundException ex) {
        // Don't log Chrome DevTools or other browser static resource requests
        String resourcePath = ex.getResourcePath();
        if (!resourcePath.contains(".well-known") && !resourcePath.contains("favicon")) {
            log.debug("Static resource not found: {}", resourcePath);
        }
        return ResponseEntity.notFound().build();
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<ValidationErrorResponse>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());

        List<ValidationErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> ValidationErrorResponse.FieldError.builder()
                        .field(((FieldError) error).getField())
                        .message(error.getDefaultMessage())
                        .build())
                .toList();

        ValidationErrorResponse validationErrors = ValidationErrorResponse.builder()
                .errors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.<ValidationErrorResponse>builder()
                        .status("error")
                        .errorCode(ErrorCode.VALIDATION_FAILED.getCode())
                        .message("Validation failed")
                        .data(validationErrors)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(ErrorCode.INTERNAL_ERROR.getCode(), "An unexpected error occurred"));
    }
}