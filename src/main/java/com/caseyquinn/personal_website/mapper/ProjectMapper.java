package com.caseyquinn.personal_website.mapper;

import com.caseyquinn.personal_website.dto.request.CreateProjectRequest;
import com.caseyquinn.personal_website.dto.request.UpdateProjectRequest;
import com.caseyquinn.personal_website.dto.response.ProjectResponse;
import com.caseyquinn.personal_website.entity.Project;
import com.caseyquinn.personal_website.entity.Technology;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.AfterMapping;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Mapper(
    componentModel = "spring",
    uses = {TechnologyMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProjectMapper {
    
    @Mapping(target = "technologies", source = "technologies")
    ProjectResponse toResponse(Project project);
    
    List<ProjectResponse> toResponseList(List<Project> projects);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true) // Auto-generated via @PrePersist
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "technologies", ignore = true) // Handled manually
    Project toEntity(CreateProjectRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "technologies", ignore = true) // Handled manually
    void updateEntityFromRequest(CreateProjectRequest request, @MappingTarget Project project);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "technologies", ignore = true) // Handled manually
    void updateEntityFromUpdateRequest(UpdateProjectRequest request, @MappingTarget Project project);
    
    @Named("technologyIdsToTechnologies")
    default Set<Technology> technologyIdsToTechnologies(Set<Long> technologyIds) {
        if (technologyIds == null) {
            return new HashSet<>();
        }
        Set<Technology> technologies = new HashSet<>();
        for (Long id : technologyIds) {
            Technology tech = new Technology();
            tech.setId(id);
            technologies.add(tech);
        }
        return technologies;
    }
}