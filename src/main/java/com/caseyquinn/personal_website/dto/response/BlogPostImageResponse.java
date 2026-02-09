package com.caseyquinn.personal_website.dto.response;

import com.caseyquinn.personal_website.entity.enums.BlogImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response DTO for blog post image data.
 */
@Data
@Builder
@Schema(description = "Blog post image information")
public class BlogPostImageResponse {

    @Schema(description = "Image ID", example = "1")
    private Long id;

    @Schema(description = "Image URL", example = "https://res.cloudinary.com/xxx/image/upload/v123/blog/image.jpg")
    private String url;

    @Schema(description = "Cloudinary public ID for image management")
    private String cloudinaryPublicId;

    @Schema(description = "Alt text for accessibility", example = "Spring Boot architecture diagram")
    private String altText;

    @Schema(description = "Image caption", example = "Figure 1: Spring Boot application structure")
    private String caption;

    @Schema(description = "Type of image")
    private BlogImageType imageType;

    @Schema(description = "Display order for sorting", example = "1")
    private Integer displayOrder;

    @Schema(description = "Whether this is the primary/featured image", example = "true")
    private Boolean isPrimary;

    @Schema(description = "Image creation timestamp")
    private LocalDateTime createdAt;
}
