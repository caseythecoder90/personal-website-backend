package com.caseyquinn.personal_website.annotations;

import com.caseyquinn.personal_website.dto.response.ProjectImageResponse;
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
 * Reusable Swagger response annotations for ProjectImageController endpoints.
 * Each inner interface documents the expected HTTP status codes and response models.
 */
public class ProjectImageApiResponses {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Upload project image", description = "Upload a new image for a project with metadata")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Image uploaded successfully",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid file or request data",
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
    public @interface Upload {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Get project images", description = "Get all images for a specific project")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Images retrieved successfully",
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
    @Operation(summary = "Get image by ID", description = "Get a specific image by its ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Image retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project or image not found",
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
    @Operation(summary = "Update image metadata", description = "Update metadata for an existing image (does not replace the image file)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Image metadata updated successfully",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project or image not found",
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
    @Operation(summary = "Delete project image", description = "Delete an image from a project")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Image deleted successfully",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project or image not found",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = Response.class))
            )
    })
    public @interface Delete {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Set primary image", description = "Set an image as the primary image for a project (unsets previous primary)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Primary image set successfully",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project or image not found",
                    content = @Content(schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = Response.class))
            )
    })
    public @interface SetPrimary {}
}
