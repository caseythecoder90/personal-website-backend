package com.caseyquinn.personal_website.mapper;

import com.caseyquinn.personal_website.dto.request.CreateBlogPostRequest;
import com.caseyquinn.personal_website.dto.request.UpdateBlogPostRequest;
import com.caseyquinn.personal_website.dto.response.BlogPostResponse;
import com.caseyquinn.personal_website.entity.BlogPost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for converting between BlogPost entities and DTOs.
 */
@Mapper(
    componentModel = "spring",
    uses = {BlogCategoryMapper.class, BlogTagMapper.class, BlogPostImageMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BlogPostMapper {

    @Mapping(target = "categories", source = "categories")
    @Mapping(target = "tags", source = "tags")
    @Mapping(target = "images", source = "images")
    BlogPostResponse toResponse(BlogPost post);

    List<BlogPostResponse> toResponseList(List<BlogPost> posts);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "images", ignore = true)
    BlogPost toEntity(CreateBlogPostRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "published", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "images", ignore = true)
    void updateEntityFromRequest(UpdateBlogPostRequest request, @MappingTarget BlogPost post);
}
