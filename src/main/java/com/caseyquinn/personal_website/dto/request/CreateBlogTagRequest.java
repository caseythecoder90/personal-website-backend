package com.caseyquinn.personal_website.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request to create a new blog tag.
 */
@Data
@Schema(description = "Request to create a new blog tag")
public class CreateBlogTagRequest {

    @NotBlank(message = "Tag name is required")
    @Size(min = 2, max = 50, message = "Tag name must be between 2 and 50 characters")
    @Schema(description = "Tag name", example = "java", required = true)
    private String name;
}
