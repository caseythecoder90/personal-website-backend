package com.caseyquinn.personal_website.mapper;

import com.caseyquinn.personal_website.dto.request.CreateTechnologyRequest;
import com.caseyquinn.personal_website.dto.request.UpdateTechnologyRequest;
import com.caseyquinn.personal_website.dto.response.TechnologyResponse;
import com.caseyquinn.personal_website.entity.Technology;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.AfterMapping;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TechnologyMapper {
    
    @Mapping(target = "projectCount", expression = "java(technology.getProjects().size())")
    TechnologyResponse toResponse(Technology technology);
    
    List<TechnologyResponse> toResponseList(List<Technology> technologies);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "projects", ignore = true)
    Technology toEntity(CreateTechnologyRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "projects", ignore = true)
    void updateEntityFromRequest(CreateTechnologyRequest request, @MappingTarget Technology technology);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "projects", ignore = true)
    void updateEntityFromUpdateRequest(UpdateTechnologyRequest request, @MappingTarget Technology technology);
    
    @AfterMapping
    default void setDefaultValues(@MappingTarget Technology technology) {
        if (technology.getFeatured() == null) {
            technology.setFeatured(false);
        }
        if (technology.getYearsExperience() == null) {
            technology.setYearsExperience(java.math.BigDecimal.ZERO);
        }
    }
}