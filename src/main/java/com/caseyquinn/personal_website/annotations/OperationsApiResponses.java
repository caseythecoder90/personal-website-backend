package com.caseyquinn.personal_website.annotations;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Reusable Swagger response annotations for OperationsController endpoints.
 * Each inner interface documents the expected HTTP status codes for one endpoint.
 */
public class OperationsApiResponses {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service is healthy")
    })
    public @interface Health {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Text encrypted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or blank input"),
            @ApiResponse(responseCode = "403", description = "Not available in production"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public @interface Encrypt {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Text decrypted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or blank input"),
            @ApiResponse(responseCode = "403", description = "Not available in production"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public @interface Decrypt {}
}
