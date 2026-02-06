package com.caseyquinn.personal_website.mapper;

import com.caseyquinn.personal_website.dto.request.CreateProjectImageRequest;
import com.caseyquinn.personal_website.dto.request.UpdateProjectImageRequest;
import com.caseyquinn.personal_website.dto.response.ProjectImageResponse;
import com.caseyquinn.personal_website.entity.ProjectImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProjectImageMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", source = "project.name")
    ProjectImageResponse toResponse(ProjectImage image);

    List<ProjectImageResponse> toResponseList(List<ProjectImage> images);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "url", ignore = true)
    @Mapping(target = "cloudinaryPublicId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ProjectImage toEntity(CreateProjectImageRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "url", ignore = true)
    @Mapping(target = "cloudinaryPublicId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromRequest(UpdateProjectImageRequest request, @MappingTarget ProjectImage image);
}
