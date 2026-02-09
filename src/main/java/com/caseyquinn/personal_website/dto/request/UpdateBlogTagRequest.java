package com.caseyquinn.personal_website.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request to update an existing blog tag.
 */
@Data
@Schema(description = "Request to update an existing blog tag")
public class UpdateBlogTagRequest {

    @Size(min = 2, max = 50, message = "Tag name must be between 2 and 50 characters")
    @Schema(description = "Tag name", example = "java")
    private String name;
}
