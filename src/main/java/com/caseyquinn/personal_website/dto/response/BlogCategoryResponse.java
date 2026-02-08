package com.caseyquinn.personal_website.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response DTO for blog category data.
 */
@Data
@Builder
@Schema(description = "Blog category information")
public class BlogCategoryResponse {

    @Schema(description = "Category ID", example = "1")
    private Long id;

    @Schema(description = "Category name", example = "Technology")
    private String name;

    @Schema(description = "URL-friendly slug", example = "technology")
    private String slug;

    @Schema(description = "Category description", example = "Posts about technology and software development")
    private String description;

    @Schema(description = "Category color in hex format", example = "#3B82F6")
    private String color;

    @Schema(description = "Number of posts in this category", example = "5")
    private Integer postCount;

    @Schema(description = "Category creation timestamp")
    private LocalDateTime createdAt;
}
