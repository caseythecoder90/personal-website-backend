package com.caseyquinn.personal_website.mapper;

import com.caseyquinn.personal_website.dto.request.CreateBlogCategoryRequest;
import com.caseyquinn.personal_website.dto.request.UpdateBlogCategoryRequest;
import com.caseyquinn.personal_website.dto.response.BlogCategoryResponse;
import com.caseyquinn.personal_website.entity.BlogCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for converting between BlogCategory entities and DTOs.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BlogCategoryMapper {

    @Mapping(target = "postCount", expression = "java(category.getBlogPosts() != null ? category.getBlogPosts().size() : 0)")
    BlogCategoryResponse toResponse(BlogCategory category);

    List<BlogCategoryResponse> toResponseList(List<BlogCategory> categories);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "blogPosts", ignore = true)
    BlogCategory toEntity(CreateBlogCategoryRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "blogPosts", ignore = true)
    void updateEntityFromRequest(UpdateBlogCategoryRequest request, @MappingTarget BlogCategory category);
}
