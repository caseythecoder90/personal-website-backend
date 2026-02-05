package com.caseyquinn.personal_website.annotations;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Reusable Swagger response annotations for ProjectController endpoints.
 * Each inner interface documents the expected HTTP status codes for one endpoint.
 */
public class ProjectApiResponses {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projects retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public @interface GetAll {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated projects retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public @interface GetPaginated {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public @interface GetById {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Project created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Duplicate project name"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public @interface Create {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public @interface Update {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public @interface Delete {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projects filtered by technology"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public @interface GetByTechnology {}
}
