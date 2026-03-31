package com.caseyquinn.personal_website.exception;

import com.caseyquinn.personal_website.dto.response.Response;
import com.caseyquinn.personal_website.dto.response.ValidationErrorResponse;
import com.caseyquinn.personal_website.exception.business.BusinessException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.caseyquinn.personal_website.exception.ErrorMessages.*;
import static com.caseyquinn.personal_website.util.HttpRequestUtils.HEADER_RETRY_AFTER;

/**
 * Global exception handler for all REST controllers.
 * Translates exceptions to standardized error responses with appropriate HTTP status codes.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String RETRY_AFTER_SECONDS = "60";

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
                .header(HEADER_RETRY_AFTER, RETRY_AFTER_SECONDS)
                .body(Response.error(ex.getErrorCode().getCode(), ex.getMessage()));
    }

    /**
     * Handles malformed request bodies including invalid JSON, unrecognized enum values,
     * and type mismatches during deserialization.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("[{}] Malformed request body: {}", ErrorCode.VALIDATION_FAILED.getCode(), ex.getMessage());
        String message = extractReadableMessage(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.error(ErrorCode.VALIDATION_FAILED.getCode(), message));
    }

    /**
     * Handles uploads that exceed the servlet container's maximum size limit.
     * Acts as a safety net when files bypass application-level validation.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Response<Void>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        log.warn("[{}] Upload size exceeded: {}", ErrorCode.FILE_SIZE_EXCEEDED.getCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.error(ErrorCode.FILE_SIZE_EXCEEDED.getCode(), MAX_UPLOAD_SIZE_EXCEEDED));
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
                .body(Response.error(ErrorCode.DB_INTEGRITY_ERROR.getCode(), DATA_INTEGRITY_ERROR));
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<Response<Void>> handleDataAccessResourceFailure(DataAccessResourceFailureException ex) {
        log.error("[DB_CONNECTION] Database connection failed after retries", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(ErrorCode.DB_CONNECTION_ERROR.getCode(), DB_CONNECTION_FAILED));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Response<Void>> handleDataAccessException(DataAccessException ex) {
        log.error("[DB_ACCESS] {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(ErrorCode.DB_ACCESS_ERROR.getCode(), DATA_ACCESS_ERROR));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFound(NoResourceFoundException ex) {
        String resourcePath = ex.getResourcePath();
        if (!resourcePath.contains(".well-known") && !resourcePath.contains("favicon")) {
            log.debug("Static resource not found: {}", resourcePath);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Handles missing required request parameters (e.g., ?q= not provided on search endpoints).
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Response<Void>> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex) {
        log.warn("[{}] Missing parameter: {}", ErrorCode.VALIDATION_FAILED.getCode(), ex.getParameterName());
        String message = String.format(MISSING_REQUEST_PARAMETER_FORMAT, ex.getParameterName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.error(ErrorCode.VALIDATION_FAILED.getCode(), message));
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
                        .message(VALIDATION_FAILED)
                        .data(validationErrors)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(ErrorCode.INTERNAL_ERROR.getCode(), UNEXPECTED_ERROR));
    }

    /**
     * Extracts a user-friendly message from Jackson deserialization exceptions.
     * Returns the specific error for known cases (invalid enum, type mismatch)
     * or a generic message for unparseable JSON.
     *
     * @param ex the HTTP message not readable exception
     * @return a user-friendly error message
     */
    private String extractReadableMessage(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException ife) {
            String fieldPath = ife.getPath().stream()
                .map(JsonMappingException.Reference::getFieldName)
                .reduce((a, b) -> a + "." + b)
                .orElse("unknown");

            if (ife.getTargetType().isEnum()) {
                String acceptedValues = Arrays.toString(ife.getTargetType().getEnumConstants());
                return String.format(INVALID_ENUM_VALUE_FORMAT, ife.getValue(), fieldPath, acceptedValues);
            }

            return String.format(INVALID_FIELD_TYPE_FORMAT, fieldPath, ife.getTargetType().getSimpleName());
        }

        return MALFORMED_REQUEST_BODY;
    }
}
