package com.caseyquinn.personal_website.dto.response;

import com.caseyquinn.personal_website.entity.enums.ProjectType;
import com.caseyquinn.personal_website.entity.enums.ProjectStatus;
import com.caseyquinn.personal_website.entity.enums.DifficultyLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "Project information")
public class ProjectResponse {
    
    @Schema(description = "Project ID", example = "1")
    private Long id;
    
    @Schema(description = "Project name", example = "Personal Website")
    private String name;
    
    @Schema(description = "URL-friendly slug", example = "personal-website")
    private String slug;
    
    @Schema(description = "Short project description for listings")
    private String shortDescription;
    
    @Schema(description = "Full detailed project description")
    private String fullDescription;
    
    @Schema(description = "Project type")
    private ProjectType type;
    
    @Schema(description = "Project status")
    private ProjectStatus status;
    
    @Schema(description = "Project difficulty level")
    private DifficultyLevel difficultyLevel;
    
    @Schema(description = "GitHub repository URL")
    private String githubUrl;
    
    @Schema(description = "Live demo URL")
    private String demoUrl;
    
    @Schema(description = "Documentation URL")
    private String documentationUrl;
    
    @Schema(description = "Project start date")
    private LocalDateTime startDate;
    
    @Schema(description = "Project end date")
    private LocalDateTime endDate;
    
    @Schema(description = "Estimated completion date")
    private LocalDateTime estimatedCompletion;
    
    @Schema(description = "Whether the project is published", example = "true")
    private Boolean published;
    
    @Schema(description = "Whether the project is featured", example = "false")
    private Boolean featured;
    
    @Schema(description = "Display order for sorting", example = "1")
    private Integer displayOrder;
    
    @Schema(description = "Number of views", example = "150")
    private Long viewCount;
    
    @Schema(description = "Technologies used in this project")
    private List<TechnologyResponse> technologies;
    
    @Schema(description = "Legacy tech stack field (deprecated)")
    @Deprecated
    private String techStack;
    
    @Schema(description = "Project creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Project last update timestamp")
    private LocalDateTime updatedAt;
}