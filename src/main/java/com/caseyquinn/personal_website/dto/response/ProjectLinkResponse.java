package com.caseyquinn.personal_website.dto.response;

import com.caseyquinn.personal_website.entity.enums.LinkType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response DTO for project link information.
 */
@Data
@Builder
@Schema(description = "Project link response")
public class ProjectLinkResponse {

    @Schema(description = "Link ID", example = "1")
    private Long id;

    @Schema(description = "Link type", example = "GITHUB")
    private LinkType type;

    @Schema(description = "Link URL", example = "https://github.com/user/repo")
    private String url;

    @Schema(description = "Optional label", example = "Backend")
    private String label;

    @Schema(description = "Display order", example = "0")
    private Integer displayOrder;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
}
