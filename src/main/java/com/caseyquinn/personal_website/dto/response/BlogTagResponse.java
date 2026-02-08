package com.caseyquinn.personal_website.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response DTO for blog tag data.
 */
@Data
@Builder
@Schema(description = "Blog tag information")
public class BlogTagResponse {

    @Schema(description = "Tag ID", example = "1")
    private Long id;

    @Schema(description = "Tag name", example = "java")
    private String name;

    @Schema(description = "URL-friendly slug", example = "java")
    private String slug;

    @Schema(description = "Number of posts using this tag", example = "12")
    private Integer usageCount;

    @Schema(description = "Tag creation timestamp")
    private LocalDateTime createdAt;
}
