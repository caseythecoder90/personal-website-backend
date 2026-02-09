package com.caseyquinn.personal_website.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

/**
 * Request to update an existing blog post.
 */
@Data
@Schema(description = "Request to update an existing blog post")
public class UpdateBlogPostRequest {

    @Size(min = 2, max = 200, message = "Post title must be between 2 and 200 characters")
    @Schema(description = "Post title", example = "Getting Started with Spring Boot")
    private String title;

    @Schema(description = "Post content in markdown or HTML format")
    private String content;

    @Size(max = 500, message = "Excerpt cannot exceed 500 characters")
    @Schema(description = "Short excerpt or summary of the post", example = "Learn how to build your first Spring Boot application with this comprehensive guide.")
    private String excerpt;

    @Min(value = 1, message = "Read time must be at least 1 minute")
    @Schema(description = "Estimated read time in minutes", example = "5")
    private Integer readTimeMinutes;

    @Schema(description = "Set of category IDs to replace existing categories")
    private Set<Long> categoryIds;

    @Schema(description = "Set of tag IDs to replace existing tags")
    private Set<Long> tagIds;
}
