package com.caseyquinn.personal_website.mapper;

import com.caseyquinn.personal_website.dto.request.CreateBlogTagRequest;
import com.caseyquinn.personal_website.dto.request.UpdateBlogTagRequest;
import com.caseyquinn.personal_website.dto.response.BlogTagResponse;
import com.caseyquinn.personal_website.entity.BlogTag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for converting between BlogTag entities and DTOs.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BlogTagMapper {

    BlogTagResponse toResponse(BlogTag tag);

    List<BlogTagResponse> toResponseList(List<BlogTag> tags);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "usageCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "blogPosts", ignore = true)
    BlogTag toEntity(CreateBlogTagRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "usageCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "blogPosts", ignore = true)
    void updateEntityFromRequest(UpdateBlogTagRequest request, @MappingTarget BlogTag tag);
}
