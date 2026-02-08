package com.caseyquinn.personal_website.controller;

import com.caseyquinn.personal_website.annotations.BlogTagApiResponses;
import com.caseyquinn.personal_website.dto.request.CreateBlogTagRequest;
import com.caseyquinn.personal_website.dto.request.UpdateBlogTagRequest;
import com.caseyquinn.personal_website.dto.response.BlogTagResponse;
import com.caseyquinn.personal_website.dto.response.Response;
import com.caseyquinn.personal_website.service.BlogTagService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing blog tags.
 */
@RestController
@RequestMapping("/api/v1/blog/tags")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Blog Tags", description = "Blog tag management APIs")
public class BlogTagController {

    private final BlogTagService blogTagService;

    /**
     * Retrieves all blog tags.
     *
     * @return response entity containing list of all tags
     */
    @BlogTagApiResponses.GetAll
    @GetMapping
    public ResponseEntity<Response<List<BlogTagResponse>>> getAllTags() {
        log.info("Fetching all blog tags");
        List<BlogTagResponse> tags = blogTagService.getAllTags();
        return ResponseEntity.ok(Response.success(tags, "Blog tags retrieved successfully"));
    }

    /**
     * Retrieves top 10 most popular tags by usage count.
     *
     * @return response entity containing list of popular tags
     */
    @BlogTagApiResponses.GetPopular
    @GetMapping("/popular")
    public ResponseEntity<Response<List<BlogTagResponse>>> getPopularTags() {
        log.info("Fetching popular blog tags");
        List<BlogTagResponse> tags = blogTagService.getPopularTags();
        return ResponseEntity.ok(Response.success(tags, "Popular blog tags retrieved successfully"));
    }

    /**
     * Retrieves a specific blog tag by its ID.
     *
     * @param id the tag ID
     * @return response entity containing the tag
     */
    @BlogTagApiResponses.GetById
    @GetMapping("/{id}")
    public ResponseEntity<Response<BlogTagResponse>> getTagById(
            @Parameter(description = "Tag ID") @PathVariable Long id) {
        log.info("Fetching blog tag with id: {}", id);
        BlogTagResponse tag = blogTagService.getTagById(id);
        return ResponseEntity.ok(Response.success(tag, "Blog tag retrieved successfully"));
    }

    /**
     * Retrieves a blog tag by its URL slug.
     *
     * @param slug the tag slug
     * @return response entity containing the tag
     */
    @BlogTagApiResponses.GetBySlug
    @GetMapping("/slug/{slug}")
    public ResponseEntity<Response<BlogTagResponse>> getTagBySlug(
            @Parameter(description = "Tag slug") @PathVariable String slug) {
        log.info("Fetching blog tag with slug: {}", slug);
        BlogTagResponse tag = blogTagService.getTagBySlug(slug);
        return ResponseEntity.ok(Response.success(tag, "Blog tag retrieved successfully"));
    }

    /**
     * Creates a new blog tag.
     *
     * @param request the tag creation request
     * @return response entity containing the created tag with HTTP 201 status
     */
    @BlogTagApiResponses.Create
    @PostMapping
    public ResponseEntity<Response<BlogTagResponse>> createTag(
            @Valid @RequestBody CreateBlogTagRequest request) {
        log.info("Creating new blog tag: {}", request.getName());
        BlogTagResponse tag = blogTagService.createTag(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success(tag, "Blog tag created successfully"));
    }

    /**
     * Updates an existing blog tag.
     *
     * @param id the tag ID
     * @param request the tag update request
     * @return response entity containing the updated tag
     */
    @BlogTagApiResponses.Update
    @PutMapping("/{id}")
    public ResponseEntity<Response<BlogTagResponse>> updateTag(
            @Parameter(description = "Tag ID") @PathVariable Long id,
            @Valid @RequestBody UpdateBlogTagRequest request) {
        log.info("Updating blog tag with id: {}", id);
        BlogTagResponse tag = blogTagService.updateTag(id, request);
        return ResponseEntity.ok(Response.success(tag, "Blog tag updated successfully"));
    }

    /**
     * Deletes a blog tag by its ID.
     *
     * @param id the tag ID
     * @return response entity with success message
     */
    @BlogTagApiResponses.Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteTag(
            @Parameter(description = "Tag ID") @PathVariable Long id) {
        log.info("Deleting blog tag with id: {}", id);
        blogTagService.deleteTag(id);
        return ResponseEntity.ok(Response.success(null, "Blog tag deleted successfully"));
    }
}
