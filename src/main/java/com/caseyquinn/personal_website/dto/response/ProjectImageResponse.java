package com.caseyquinn.personal_website.dto.response;

import com.caseyquinn.personal_website.entity.enums.ImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Project image response")
public class ProjectImageResponse {

    @Schema(description = "Image ID", example = "1")
    private Long id;

    @Schema(description = "Project ID", example = "1")
    private Long projectId;

    @Schema(description = "Project name for reference", example = "E-Commerce Platform")
    private String projectName;

    @Schema(description = "Image URL (Cloudinary CDN)", example = "https://res.cloudinary.com/...")
    private String url;

    @Schema(description = "Cloudinary public ID", example = "portfolio-images/project-slug/image123")
    private String cloudinaryPublicId;

    @Schema(description = "Alt text for accessibility", example = "Homepage screenshot showing product listings")
    private String altText;

    @Schema(description = "Image caption", example = "Main landing page with featured products")
    private String caption;

    @Schema(description = "Image type", example = "SCREENSHOT")
    private ImageType imageType;

    @Schema(description = "Display order for sorting", example = "0")
    private Integer displayOrder;

    @Schema(description = "Whether this is the primary image", example = "true")
    private Boolean isPrimary;

    @Schema(description = "Creation timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}
