package com.caseyquinn.personal_website.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for blog post data.
 */
@Data
@Builder
@Schema(description = "Blog post information")
public class BlogPostResponse {

    @Schema(description = "Post ID", example = "1")
    private Long id;

    @Schema(description = "Post title", example = "Getting Started with Spring Boot")
    private String title;

    @Schema(description = "URL-friendly slug", example = "getting-started-with-spring-boot")
    private String slug;

    @Schema(description = "Post content in markdown or HTML format")
    private String content;

    @Schema(description = "Short excerpt or summary of the post")
    private String excerpt;

    @Schema(description = "Whether the post is published", example = "true")
    private Boolean published;

    @Schema(description = "Date and time when the post was published")
    private LocalDateTime publishedAt;

    @Schema(description = "Number of times the post has been viewed", example = "150")
    private Integer viewCount;

    @Schema(description = "Estimated read time in minutes", example = "5")
    private Integer readTimeMinutes;

    @Schema(description = "Categories associated with this post")
    private List<BlogCategoryResponse> categories;

    @Schema(description = "Tags associated with this post")
    private List<BlogTagResponse> tags;

    @Schema(description = "Images associated with this post")
    private List<BlogPostImageResponse> images;

    @Schema(description = "Post creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Post last update timestamp")
    private LocalDateTime updatedAt;
}
