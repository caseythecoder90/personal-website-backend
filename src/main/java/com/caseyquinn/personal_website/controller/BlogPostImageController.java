package com.caseyquinn.personal_website.controller;

import com.caseyquinn.personal_website.annotations.BlogPostImageApiResponses;
import com.caseyquinn.personal_website.dto.request.CreateBlogPostImageRequest;
import com.caseyquinn.personal_website.dto.request.UpdateBlogPostImageRequest;
import com.caseyquinn.personal_website.dto.response.BlogPostImageResponse;
import com.caseyquinn.personal_website.dto.response.Response;
import com.caseyquinn.personal_website.service.BlogPostImageService;
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
 * REST controller for managing blog post images.
 */
@RestController
@RequestMapping("/api/v1/blog/posts/{postId}/images")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Blog Post Images", description = "Blog post image management APIs")
public class BlogPostImageController {

    private final BlogPostImageService blogPostImageService;

    /**
     * Retrieves all images for a blog post.
     *
     * @param postId the blog post ID
     * @return response entity containing list of images
     */
    @BlogPostImageApiResponses.GetAll
    @GetMapping
    public ResponseEntity<Response<List<BlogPostImageResponse>>> getImagesByPostId(
            @Parameter(description = "Blog post ID") @PathVariable Long postId) {
        log.info("Fetching images for blog post id: {}", postId);
        List<BlogPostImageResponse> images = blogPostImageService.getImagesByPostId(postId);
        return ResponseEntity.ok(Response.success(images, "Blog post images retrieved successfully"));
    }

    /**
     * Retrieves a specific image by ID.
     *
     * @param postId the blog post ID
     * @param imageId the image ID
     * @return response entity containing the image
     */
    @BlogPostImageApiResponses.GetById
    @GetMapping("/{imageId}")
    public ResponseEntity<Response<BlogPostImageResponse>> getImageById(
            @Parameter(description = "Blog post ID") @PathVariable Long postId,
            @Parameter(description = "Image ID") @PathVariable Long imageId) {
        log.info("Fetching image with id: {} for blog post id: {}", imageId, postId);
        BlogPostImageResponse image = blogPostImageService.getImageById(postId, imageId);
        return ResponseEntity.ok(Response.success(image, "Blog post image retrieved successfully"));
    }

    /**
     * Creates a new image for a blog post.
     *
     * @param postId the blog post ID
     * @param request the image creation request
     * @return response entity containing the created image with HTTP 201 status
     */
    @BlogPostImageApiResponses.Create
    @PostMapping
    public ResponseEntity<Response<BlogPostImageResponse>> createImage(
            @Parameter(description = "Blog post ID") @PathVariable Long postId,
            @Valid @RequestBody CreateBlogPostImageRequest request) {
        log.info("Creating image for blog post id: {}", postId);
        BlogPostImageResponse image = blogPostImageService.createImage(postId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success(image, "Blog post image created successfully"));
    }

    /**
     * Updates an existing image metadata.
     *
     * @param postId the blog post ID
     * @param imageId the image ID
     * @param request the image update request
     * @return response entity containing the updated image
     */
    @BlogPostImageApiResponses.Update
    @PutMapping("/{imageId}")
    public ResponseEntity<Response<BlogPostImageResponse>> updateImage(
            @Parameter(description = "Blog post ID") @PathVariable Long postId,
            @Parameter(description = "Image ID") @PathVariable Long imageId,
            @Valid @RequestBody UpdateBlogPostImageRequest request) {
        log.info("Updating image with id: {} for blog post id: {}", imageId, postId);
        BlogPostImageResponse image = blogPostImageService.updateImage(postId, imageId, request);
        return ResponseEntity.ok(Response.success(image, "Blog post image updated successfully"));
    }

    /**
     * Deletes an image from a blog post.
     *
     * @param postId the blog post ID
     * @param imageId the image ID
     * @return response entity with success message
     */
    @BlogPostImageApiResponses.Delete
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Response<Void>> deleteImage(
            @Parameter(description = "Blog post ID") @PathVariable Long postId,
            @Parameter(description = "Image ID") @PathVariable Long imageId) {
        log.info("Deleting image with id: {} from blog post id: {}", imageId, postId);
        blogPostImageService.deleteImage(postId, imageId);
        return ResponseEntity.ok(Response.success(null, "Blog post image deleted successfully"));
    }

    /**
     * Sets an image as the primary image for a blog post.
     *
     * @param postId the blog post ID
     * @param imageId the image ID
     * @return response entity containing the updated image
     */
    @BlogPostImageApiResponses.SetPrimary
    @PutMapping("/{imageId}/primary")
    public ResponseEntity<Response<BlogPostImageResponse>> setPrimaryImage(
            @Parameter(description = "Blog post ID") @PathVariable Long postId,
            @Parameter(description = "Image ID") @PathVariable Long imageId) {
        log.info("Setting image {} as primary for blog post id: {}", imageId, postId);
        BlogPostImageResponse image = blogPostImageService.setPrimaryImage(postId, imageId);
        return ResponseEntity.ok(Response.success(image, "Image set as primary successfully"));
    }
}
