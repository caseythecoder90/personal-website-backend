package com.caseyquinn.personal_website.dto.request;

import com.caseyquinn.personal_website.entity.enums.ImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Create project image metadata (file sent separately as multipart)")
public class CreateProjectImageRequest {

    @NotNull(message = "Image type is required")
    @Schema(description = "Type of image", required = true, example = "SCREENSHOT")
    private ImageType imageType;

    @Size(max = 255, message = "Alt text cannot exceed 255 characters")
    @Schema(description = "Alt text for accessibility", example = "Homepage screenshot showing main features")
    private String altText;

    @Size(max = 500, message = "Caption cannot exceed 500 characters")
    @Schema(description = "Image caption", example = "Main landing page with featured content")
    private String caption;

    @Min(value = 0, message = "Display order cannot be negative")
    @Schema(description = "Display order for sorting", example = "0")
    @Builder.Default
    private Integer displayOrder = 0;

    @Schema(description = "Set as primary image (unsets previous primary)", example = "false")
    @Builder.Default
    private Boolean isPrimary = false;
}
