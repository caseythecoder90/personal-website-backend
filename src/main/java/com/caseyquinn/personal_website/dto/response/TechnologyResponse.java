package com.caseyquinn.personal_website.dto.response;

import com.caseyquinn.personal_website.entity.enums.TechnologyCategory;
import com.caseyquinn.personal_website.entity.enums.ProficiencyLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Technology information")
public class TechnologyResponse {
    
    @Schema(description = "Technology ID", example = "1")
    private Long id;
    
    @Schema(description = "Technology name", example = "Java")
    private String name;
    
    @Schema(description = "Technology version", example = "21")
    private String version;
    
    @Schema(description = "Technology category")
    private TechnologyCategory category;
    
    @Schema(description = "Icon URL for the technology")
    private String iconUrl;
    
    @Schema(description = "Brand color for the technology", example = "#f89820")
    private String color;
    
    @Schema(description = "Documentation URL")
    private String documentationUrl;
    
    @Schema(description = "Proficiency level with this technology")
    private ProficiencyLevel proficiencyLevel;
    
    @Schema(description = "Years of experience with this technology", example = "5")
    private Integer yearsExperience;
    
    @Schema(description = "Whether this technology is featured", example = "true")
    private Boolean featured;
    
    @Schema(description = "Number of projects using this technology", example = "3")
    private Integer projectCount;
    
    @Schema(description = "Technology creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Technology last update timestamp")
    private LocalDateTime updatedAt;
}