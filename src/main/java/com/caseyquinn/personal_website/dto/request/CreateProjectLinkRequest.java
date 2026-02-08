package com.caseyquinn.personal_website.dto.request;

import com.caseyquinn.personal_website.entity.enums.LinkType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

/**
 * Request DTO for creating a project link.
 */
@Data
@Schema(description = "Request to create a project link")
public class CreateProjectLinkRequest {

    @NotNull(message = "Link type is required")
    @Schema(description = "Type of link", example = "GITHUB", required = true)
    private LinkType type;

    @NotNull(message = "URL is required")
    @URL(message = "Must be a valid URL")
    @Size(max = 1000, message = "URL cannot exceed 1000 characters")
    @Schema(description = "Link URL", example = "https://github.com/user/repo", required = true)
    private String url;

    @Size(max = 100, message = "Label cannot exceed 100 characters")
    @Schema(description = "Optional label for the link", example = "Backend")
    private String label;

    @Min(value = 0, message = "Display order cannot be negative")
    @Schema(description = "Display order for sorting", example = "0")
    private Integer displayOrder = 0;
}
