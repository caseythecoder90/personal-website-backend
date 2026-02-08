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
 * Reusable Swagger response annotations for ProjectLinkController endpoints.
 * Each inner interface documents the expected HTTP status codes and response models.
 */
public class ProjectLinkApiResponses {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Create project link", description = "Create a new link for a project (GitHub, demo, docs, etc.)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Link created successfully",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = Response.class))
            )
    })
    public @interface Create {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Get project links", description = "Get all links for a specific project")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Links retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = Response.class))
            )
    })
    public @interface GetAll {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Get link by ID", description = "Get a specific link by its ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Link retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project or link not found",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = Response.class))
            )
    })
    public @interface GetById {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Update project link", description = "Update an existing link")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Link updated successfully",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project or link not found",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = Response.class))
            )
    })
    public @interface Update {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Delete project link", description = "Delete a link from a project")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Link deleted successfully",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project or link not found",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = Response.class))
            )
    })
    public @interface Delete {}
}
