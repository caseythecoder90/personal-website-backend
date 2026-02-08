package com.caseyquinn.personal_website.mapper;

import com.caseyquinn.personal_website.dto.response.ProjectLinkResponse;
import com.caseyquinn.personal_website.entity.ProjectLink;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for ProjectLink entity to DTO conversions.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProjectLinkMapper {

    @Mapping(target = "type", source = "linkType")
    ProjectLinkResponse toResponse(ProjectLink link);

    List<ProjectLinkResponse> toResponseList(List<ProjectLink> links);
}
