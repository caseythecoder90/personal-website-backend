package com.caseyquinn.personal_website.controller;

import com.caseyquinn.personal_website.annotations.BlogPostApiResponses;
import com.caseyquinn.personal_website.dto.request.CreateBlogPostRequest;
import com.caseyquinn.personal_website.dto.request.UpdateBlogPostRequest;
import com.caseyquinn.personal_website.dto.response.BlogPostResponse;
import com.caseyquinn.personal_website.dto.response.Response;
import com.caseyquinn.personal_website.service.BlogPostService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing blog posts.
 */
@RestController
@RequestMapping("/api/v1/blog/posts")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Blog Posts", description = "Blog post management APIs")
public class BlogPostController {

    private final BlogPostService blogPostService;

    /**
     * Retrieves all blog posts (admin view).
     *
     * @return response entity containing list of all posts
     */
    @BlogPostApiResponses.GetAll
    @GetMapping
    public ResponseEntity<Response<List<BlogPostResponse>>> getAllPosts() {
        log.info("Fetching all blog posts");
        List<BlogPostResponse> posts = blogPostService.getAllPosts();
        return ResponseEntity.ok(Response.success(posts, "Blog posts retrieved successfully"));
    }

    /**
     * Retrieves all published blog posts (public view).
     *
     * @return response entity containing list of published posts
     */
    @BlogPostApiResponses.GetPublished
    @GetMapping("/published")
    public ResponseEntity<Response<List<BlogPostResponse>>> getPublishedPosts() {
        log.info("Fetching published blog posts");
        List<BlogPostResponse> posts = blogPostService.getPublishedPosts();
        return ResponseEntity.ok(Response.success(posts, "Published blog posts retrieved successfully"));
    }

    /**
     * Retrieves a specific blog post by its ID.
     *
     * @param id the post ID
     * @return response entity containing the post
     */
    @BlogPostApiResponses.GetById
    @GetMapping("/{id}")
    public ResponseEntity<Response<BlogPostResponse>> getPostById(
            @Parameter(description = "Post ID") @PathVariable Long id) {
        log.info("Fetching blog post with id: {}", id);
        BlogPostResponse post = blogPostService.getPostById(id);
        return ResponseEntity.ok(Response.success(post, "Blog post retrieved successfully"));
    }

    /**
     * Retrieves a blog post by its URL slug (public view, increments view count).
     *
     * @param slug the post slug
     * @return response entity containing the post
     */
    @BlogPostApiResponses.GetBySlug
    @GetMapping("/slug/{slug}")
    public ResponseEntity<Response<BlogPostResponse>> getPostBySlug(
            @Parameter(description = "Post slug") @PathVariable String slug) {
        log.info("Fetching blog post with slug: {}", slug);
        BlogPostResponse post = blogPostService.getPostBySlug(slug);
        return ResponseEntity.ok(Response.success(post, "Blog post retrieved successfully"));
    }

    /**
     * Creates a new blog post.
     *
     * @param request the post creation request
     * @return response entity containing the created post with HTTP 201 status
     */
    @BlogPostApiResponses.Create
    @PostMapping
    public ResponseEntity<Response<BlogPostResponse>> createPost(
            @Valid @RequestBody CreateBlogPostRequest request) {
        log.info("Creating new blog post: {}", request.getTitle());
        BlogPostResponse post = blogPostService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success(post, "Blog post created successfully"));
    }

    /**
     * Updates an existing blog post.
     *
     * @param id the post ID
     * @param request the post update request
     * @return response entity containing the updated post
     */
    @BlogPostApiResponses.Update
    @PutMapping("/{id}")
    public ResponseEntity<Response<BlogPostResponse>> updatePost(
            @Parameter(description = "Post ID") @PathVariable Long id,
            @Valid @RequestBody UpdateBlogPostRequest request) {
        log.info("Updating blog post with id: {}", id);
        BlogPostResponse post = blogPostService.updatePost(id, request);
        return ResponseEntity.ok(Response.success(post, "Blog post updated successfully"));
    }

    /**
     * Deletes a blog post by its ID.
     *
     * @param id the post ID
     * @return response entity with success message
     */
    @BlogPostApiResponses.Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deletePost(
            @Parameter(description = "Post ID") @PathVariable Long id) {
        log.info("Deleting blog post with id: {}", id);
        blogPostService.deletePost(id);
        return ResponseEntity.ok(Response.success(null, "Blog post deleted successfully"));
    }

    /**
     * Publishes a blog post.
     *
     * @param id the post ID
     * @return response entity containing the updated post
     */
    @BlogPostApiResponses.Publish
    @PutMapping("/{id}/publish")
    public ResponseEntity<Response<BlogPostResponse>> publishPost(
            @Parameter(description = "Post ID") @PathVariable Long id) {
        log.info("Publishing blog post with id: {}", id);
        BlogPostResponse post = blogPostService.publishPost(id);
        return ResponseEntity.ok(Response.success(post, "Blog post published successfully"));
    }

    /**
     * Unpublishes a blog post.
     *
     * @param id the post ID
     * @return response entity containing the updated post
     */
    @BlogPostApiResponses.Unpublish
    @PutMapping("/{id}/unpublish")
    public ResponseEntity<Response<BlogPostResponse>> unpublishPost(
            @Parameter(description = "Post ID") @PathVariable Long id) {
        log.info("Unpublishing blog post with id: {}", id);
        BlogPostResponse post = blogPostService.unpublishPost(id);
        return ResponseEntity.ok(Response.success(post, "Blog post unpublished successfully"));
    }

    /**
     * Retrieves published blog posts by category slug.
     *
     * @param slug the category slug
     * @return response entity containing list of matching posts
     */
    @BlogPostApiResponses.GetByCategory
    @GetMapping("/category/{slug}")
    public ResponseEntity<Response<List<BlogPostResponse>>> getPostsByCategory(
            @Parameter(description = "Category slug") @PathVariable String slug) {
        log.info("Fetching blog posts by category slug: {}", slug);
        List<BlogPostResponse> posts = blogPostService.getPostsByCategorySlug(slug);
        return ResponseEntity.ok(Response.success(posts, "Blog posts retrieved successfully"));
    }

    /**
     * Retrieves published blog posts by tag slug.
     *
     * @param slug the tag slug
     * @return response entity containing list of matching posts
     */
    @BlogPostApiResponses.GetByTag
    @GetMapping("/tag/{slug}")
    public ResponseEntity<Response<List<BlogPostResponse>>> getPostsByTag(
            @Parameter(description = "Tag slug") @PathVariable String slug) {
        log.info("Fetching blog posts by tag slug: {}", slug);
        List<BlogPostResponse> posts = blogPostService.getPostsByTagSlug(slug);
        return ResponseEntity.ok(Response.success(posts, "Blog posts retrieved successfully"));
    }

    /**
     * Searches published blog posts by title or content.
     *
     * @param q the search query
     * @return response entity containing list of matching posts
     */
    @BlogPostApiResponses.Search
    @GetMapping("/search")
    public ResponseEntity<Response<List<BlogPostResponse>>> searchPosts(
            @Parameter(description = "Search query") @RequestParam String q) {
        log.info("Searching blog posts with query: {}", q);
        List<BlogPostResponse> posts = blogPostService.searchPosts(q);
        return ResponseEntity.ok(Response.success(posts, "Search results retrieved successfully"));
    }

    /**
     * Associates a category with a blog post.
     *
     * @param id the post ID
     * @param categoryId the category ID
     * @return response entity containing the updated post
     */
    @BlogPostApiResponses.AddCategory
    @PostMapping("/{id}/categories/{categoryId}")
    public ResponseEntity<Response<BlogPostResponse>> addCategoryToPost(
            @Parameter(description = "Post ID") @PathVariable Long id,
            @Parameter(description = "Category ID") @PathVariable Long categoryId) {
        log.info("Adding category {} to blog post {}", categoryId, id);
        BlogPostResponse post = blogPostService.addCategoryToPost(id, categoryId);
        return ResponseEntity.ok(Response.success(post, "Category added to blog post successfully"));
    }

    /**
     * Removes a category association from a blog post.
     *
     * @param id the post ID
     * @param categoryId the category ID
     * @return response entity containing the updated post
     */
    @BlogPostApiResponses.RemoveCategory
    @DeleteMapping("/{id}/categories/{categoryId}")
    public ResponseEntity<Response<BlogPostResponse>> removeCategoryFromPost(
            @Parameter(description = "Post ID") @PathVariable Long id,
            @Parameter(description = "Category ID") @PathVariable Long categoryId) {
        log.info("Removing category {} from blog post {}", categoryId, id);
        BlogPostResponse post = blogPostService.removeCategoryFromPost(id, categoryId);
        return ResponseEntity.ok(Response.success(post, "Category removed from blog post successfully"));
    }

    /**
     * Associates a tag with a blog post.
     *
     * @param id the post ID
     * @param tagId the tag ID
     * @return response entity containing the updated post
     */
    @BlogPostApiResponses.AddTag
    @PostMapping("/{id}/tags/{tagId}")
    public ResponseEntity<Response<BlogPostResponse>> addTagToPost(
            @Parameter(description = "Post ID") @PathVariable Long id,
            @Parameter(description = "Tag ID") @PathVariable Long tagId) {
        log.info("Adding tag {} to blog post {}", tagId, id);
        BlogPostResponse post = blogPostService.addTagToPost(id, tagId);
        return ResponseEntity.ok(Response.success(post, "Tag added to blog post successfully"));
    }

    /**
     * Removes a tag association from a blog post.
     *
     * @param id the post ID
     * @param tagId the tag ID
     * @return response entity containing the updated post
     */
    @BlogPostApiResponses.RemoveTag
    @DeleteMapping("/{id}/tags/{tagId}")
    public ResponseEntity<Response<BlogPostResponse>> removeTagFromPost(
            @Parameter(description = "Post ID") @PathVariable Long id,
            @Parameter(description = "Tag ID") @PathVariable Long tagId) {
        log.info("Removing tag {} from blog post {}", tagId, id);
        BlogPostResponse post = blogPostService.removeTagFromPost(id, tagId);
        return ResponseEntity.ok(Response.success(post, "Tag removed from blog post successfully"));
    }
}
