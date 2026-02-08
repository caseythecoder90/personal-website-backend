package com.caseyquinn.personal_website.annotations;

import com.caseyquinn.personal_website.dto.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Reusable Swagger response annotations for OperationsController endpoints.
 * Each inner interface documents the expected HTTP status codes and response models.
 */
public class OperationsApiResponses {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Health check", description = "Check service health and status")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Service is healthy",
                    content = @Content(schema = @Schema(implementation = Response.class))
            )
    })
    public @interface Health {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Encrypt text", description = "Encrypt text using Jasypt encryption (non-production only)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Text encrypted successfully",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or blank input",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not available in production",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = Response.class))
            )
    })
    public @interface Encrypt {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Decrypt text", description = "Decrypt text using Jasypt encryption (non-production only)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Text decrypted successfully",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or blank input",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not available in production",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = Response.class))
            )
    })
    public @interface Decrypt {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Hash password", description = "Generate BCrypt hash for a password (non-production only)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password hashed successfully",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or blank input",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not available in production",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = Response.class))
            )
    })
    public @interface HashPassword {}
}
