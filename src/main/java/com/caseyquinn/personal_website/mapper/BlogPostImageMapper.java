package com.caseyquinn.personal_website.mapper;

import com.caseyquinn.personal_website.dto.request.CreateBlogPostImageRequest;
import com.caseyquinn.personal_website.dto.request.UpdateBlogPostImageRequest;
import com.caseyquinn.personal_website.dto.response.BlogPostImageResponse;
import com.caseyquinn.personal_website.entity.BlogPostImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for converting between BlogPostImage entities and DTOs.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BlogPostImageMapper {

    BlogPostImageResponse toResponse(BlogPostImage image);

    List<BlogPostImageResponse> toResponseList(List<BlogPostImage> images);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "blogPost", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    BlogPostImage toEntity(CreateBlogPostImageRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "blogPost", ignore = true)
    @Mapping(target = "url", ignore = true)
    @Mapping(target = "cloudinaryPublicId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromRequest(UpdateBlogPostImageRequest request, @MappingTarget BlogPostImage image);
}
