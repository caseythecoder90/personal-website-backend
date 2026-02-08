package com.caseyquinn.personal_website.dto.request;

import com.caseyquinn.personal_website.entity.enums.ProjectType;
import com.caseyquinn.personal_website.entity.enums.ProjectStatus;
import com.caseyquinn.personal_website.entity.enums.DifficultyLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Schema(description = "Request to update an existing project")
public class UpdateProjectRequest {
    
    @NotBlank(message = "Project name is required")
    @Size(min = 2, max = 100, message = "Project name must be between 2 and 100 characters")
    @Schema(description = "Project name", example = "Personal Website", required = true)
    private String name;
    
    @Size(min = 2, max = 100, message = "Slug must be between 2 and 100 characters")
    @Schema(description = "URL-friendly slug", example = "personal-website")
    private String slug;
    
    @NotBlank(message = "Short description is required")
    @Size(max = 200, message = "Short description cannot exceed 200 characters")
    @Schema(description = "Short project description for listings", required = true)
    private String shortDescription;
    
    @Size(max = 5000, message = "Full description cannot exceed 5000 characters")
    @Schema(description = "Full detailed project description")
    private String fullDescription;
    
    @NotNull(message = "Project type is required")
    @Schema(description = "Project type", required = true)
    private ProjectType type;
    
    @Schema(description = "Project status")
    private ProjectStatus status;
    
    @Schema(description = "Project difficulty level")
    private DifficultyLevel difficultyLevel;

    @Schema(description = "Project start date")
    private LocalDateTime startDate;

    @Schema(description = "Project completion date")
    private LocalDateTime completionDate;

    @Min(value = 0, message = "Estimated hours cannot be negative")
    @Schema(description = "Estimated hours to complete", example = "100")
    private Integer estimatedHours;

    @Schema(description = "Whether the project should be published", example = "false")
    private Boolean published;
    
    @Schema(description = "Whether the project should be featured", example = "false")
    private Boolean featured;
    
    @Min(value = 0, message = "Display order cannot be negative")
    @Schema(description = "Display order for sorting", example = "1")
    private Integer displayOrder;
    
    @Schema(description = "Set of technology IDs to associate with this project")
    private Set<Long> technologyIds;
}