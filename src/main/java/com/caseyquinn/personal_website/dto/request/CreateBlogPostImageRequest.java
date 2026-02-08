package com.caseyquinn.personal_website.dto.request;

import com.caseyquinn.personal_website.entity.enums.BlogImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * Request containing metadata for a blog post image upload.
 * The actual image file is sent separately as multipart form data.
 */
@Data
@Builder
@Schema(description = "Blog post image metadata (file sent separately as multipart)")
public class CreateBlogPostImageRequest {

    @NotNull(message = "Image type is required")
    @Schema(description = "Type of image", required = true, example = "FEATURED")
    private BlogImageType imageType;

    @Size(max = 255, message = "Alt text cannot exceed 255 characters")
    @Schema(description = "Alternative text for accessibility", example = "Spring Boot architecture diagram")
    private String altText;

    @Size(max = 500, message = "Caption cannot exceed 500 characters")
    @Schema(description = "Image caption", example = "Figure 1: Spring Boot application structure")
    private String caption;

    @Min(value = 0, message = "Display order cannot be negative")
    @Schema(description = "Display order for sorting", example = "0")
    @Builder.Default
    private Integer displayOrder = 0;

    @Schema(description = "Set as primary image (unsets previous primary)", example = "false")
    @Builder.Default
    private Boolean isPrimary = false;
}
