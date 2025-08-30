package com.caseyquinn.personal_website.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Health check response")
public class HealthResponse {
    
    @Schema(description = "Service status", example = "UP")
    private String status;
    
    @Schema(description = "Service name", example = "personal-website-backend")
    private String service;
    
    @Schema(description = "Current timestamp")
    private LocalDateTime timestamp;
    
    @Schema(description = "Service version", example = "1.0.0")
    private String version;
    
    @Schema(description = "Environment", example = "development")
    private String environment;
}