package com.caseyquinn.personal_website.dto.request;

import com.caseyquinn.personal_website.entity.enums.TechnologyCategory;
import com.caseyquinn.personal_website.entity.enums.ProficiencyLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import org.hibernate.validator.constraints.URL;

@Data
@Schema(description = "Request to create a new technology")
public class CreateTechnologyRequest {
    
    @NotBlank(message = "Technology name is required")
    @Size(min = 2, max = 50, message = "Technology name must be between 2 and 50 characters")
    @Schema(description = "Technology name", example = "Java", required = true)
    private String name;
    
    @Size(max = 20, message = "Version cannot exceed 20 characters")
    @Schema(description = "Technology version", example = "21")
    private String version;
    
    @NotNull(message = "Technology category is required")
    @Schema(description = "Technology category", required = true)
    private TechnologyCategory category;
    
    @URL(message = "Must be a valid URL")
    @Schema(description = "Icon URL for the technology")
    private String iconUrl;
    
    @Size(max = 7, message = "Color must be a valid hex color")
    @Schema(description = "Brand color for the technology", example = "#f89820")
    private String color;
    
    @URL(message = "Must be a valid URL")
    @Schema(description = "Documentation URL")
    private String documentationUrl;
    
    @Schema(description = "Proficiency level with this technology")
    private ProficiencyLevel proficiencyLevel;
    
    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 50, message = "Years of experience cannot exceed 50")
    @Schema(description = "Years of experience with this technology", example = "5")
    private Integer yearsExperience;
    
    @Schema(description = "Whether this technology should be featured", example = "true")
    private Boolean featured;
}