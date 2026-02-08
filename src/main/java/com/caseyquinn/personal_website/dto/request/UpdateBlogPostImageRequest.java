package com.caseyquinn.personal_website.dto.request;

import com.caseyquinn.personal_website.entity.enums.BlogImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request to update an existing blog post image.
 */
@Data
@Schema(description = "Request to update an existing blog post image")
public class UpdateBlogPostImageRequest {

    @Size(max = 255, message = "Alt text cannot exceed 255 characters")
    @Schema(description = "Alternative text for accessibility", example = "Spring Boot architecture diagram")
    private String altText;

    @Size(max = 500, message = "Caption cannot exceed 500 characters")
    @Schema(description = "Image caption", example = "Figure 1: Spring Boot application structure")
    private String caption;

    @Schema(description = "Type of image", example = "INLINE")
    private BlogImageType imageType;

    @Min(value = 0, message = "Display order cannot be negative")
    @Schema(description = "Display order for sorting", example = "0")
    private Integer displayOrder;
}
