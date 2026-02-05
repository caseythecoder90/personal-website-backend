package com.caseyquinn.personal_website.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Generic API response wrapper")
public class Response<T> {
    
    @Schema(description = "Response status", example = "success")
    private String status;

    @Schema(description = "Machine-readable error code, present on error responses", example = "NOT_FOUND")
    private String errorCode;

    @Schema(description = "Response message", example = "Operation completed successfully")
    private String message;
    
    @Schema(description = "Response data")
    private T data;
    
    @Schema(description = "Response timestamp")
    private LocalDateTime timestamp;
    
    @Schema(description = "Request ID for tracing")
    private String requestId;
    
    public static <T> Response<T> success(T data) {
        return Response.<T>builder()
                .status("success")
                .message("Operation completed successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> Response<T> success(T data, String message) {
        return Response.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> Response<T> error(String message) {
        return Response.<T>builder()
                .status("error")
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> Response<T> error(String errorCode, String message) {
        return Response.<T>builder()
                .status("error")
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}