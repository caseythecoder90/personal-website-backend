package com.caseyquinn.personal_website.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

/**
 * Request to create a new blog post.
 */
@Data
@Schema(description = "Request to create a new blog post")
public class CreateBlogPostRequest {

    @NotBlank(message = "Post title is required")
    @Size(min = 2, max = 200, message = "Post title must be between 2 and 200 characters")
    @Schema(description = "Post title", example = "Getting Started with Spring Boot", required = true)
    private String title;

    @Size(max = 200, message = "Slug cannot exceed 200 characters")
    @Schema(description = "Custom URL slug (auto-generated from title if not provided)", example = "getting-started-with-spring-boot")
    private String slug;

    @NotBlank(message = "Post content is required")
    @Schema(description = "Post content in markdown or HTML format", required = true)
    private String content;

    @Size(max = 500, message = "Excerpt cannot exceed 500 characters")
    @Schema(description = "Short excerpt or summary of the post", example = "Learn how to build your first Spring Boot application with this comprehensive guide.")
    private String excerpt;

    @Schema(description = "Whether the post should be published immediately", example = "false")
    private Boolean published;

    @Min(value = 1, message = "Read time must be at least 1 minute")
    @Schema(description = "Estimated read time in minutes", example = "5")
    private Integer readTimeMinutes;

    @Schema(description = "Set of category IDs to associate with this post")
    private Set<Long> categoryIds;

    @Schema(description = "Set of tag IDs to associate with this post")
    private Set<Long> tagIds;
}
