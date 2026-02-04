package com.caseyquinn.personal_website.exception;

import com.caseyquinn.personal_website.dto.response.Response;
import com.caseyquinn.personal_website.exception.business.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response<Void>> handleNotFoundException(NotFoundException ex) {
        log.warn("[{}] {}", ex.getErrorCode().getCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Response.error(ex.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response<Void>> handleBusinessException(BusinessException ex) {
        log.warn("[{}] {}", ex.getErrorCode().getCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.error(ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("[DB_INTEGRITY] {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("A data integrity error occurred"));
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<Response<Void>> handleDataAccessResourceFailure(DataAccessResourceFailureException ex) {
        log.error("[DB_CONNECTION] Database connection failed after retries", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("Database connection failed. Please try again later."));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Response<Void>> handleDataAccessException(DataAccessException ex) {
        log.error("[DB_ACCESS] {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("A data access error occurred. Please try again later."));
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
    public ResponseEntity<Response<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.<Map<String, String>>builder()
                        .status("error")
                        .message("Validation failed")
                        .data(errors)
                        .build());
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("An unexpected error occurred"));
    }
}