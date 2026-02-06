package com.caseyquinn.personal_website.dto.request;

import com.caseyquinn.personal_website.entity.enums.ImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Update project image metadata (does not replace image file)")
public class UpdateProjectImageRequest {

    @Schema(description = "Type of image", example = "SCREENSHOT")
    private ImageType imageType;

    @Size(max = 255, message = "Alt text cannot exceed 255 characters")
    @Schema(description = "Alt text for accessibility", example = "Updated alt text")
    private String altText;

    @Size(max = 500, message = "Caption cannot exceed 500 characters")
    @Schema(description = "Image caption", example = "Updated caption")
    private String caption;

    @Min(value = 0, message = "Display order cannot be negative")
    @Schema(description = "Display order for sorting", example = "1")
    private Integer displayOrder;

    @Schema(description = "Set as primary image (unsets previous primary)", example = "true")
    private Boolean isPrimary;
}
