package com.caseyquinn.personal_website.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request to update an existing blog category.
 */
@Data
@Schema(description = "Request to update an existing blog category")
public class UpdateBlogCategoryRequest {

    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    @Schema(description = "Category name", example = "Technology")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Schema(description = "Category description", example = "Posts about technology and software development")
    private String description;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color (e.g., #FF5733)")
    @Schema(description = "Category color in hex format", example = "#3B82F6")
    private String color;
}
