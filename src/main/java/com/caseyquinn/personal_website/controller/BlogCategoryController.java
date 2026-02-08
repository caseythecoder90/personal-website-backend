package com.caseyquinn.personal_website.controller;

import com.caseyquinn.personal_website.annotations.BlogCategoryApiResponses;
import com.caseyquinn.personal_website.dto.request.CreateBlogCategoryRequest;
import com.caseyquinn.personal_website.dto.request.UpdateBlogCategoryRequest;
import com.caseyquinn.personal_website.dto.response.BlogCategoryResponse;
import com.caseyquinn.personal_website.dto.response.Response;
import com.caseyquinn.personal_website.service.BlogCategoryService;
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
 * REST controller for managing blog categories.
 */
@RestController
@RequestMapping("/api/v1/blog/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Blog Categories", description = "Blog category management APIs")
public class BlogCategoryController {

    private final BlogCategoryService blogCategoryService;

    /**
     * Retrieves all blog categories.
     *
     * @return response entity containing list of all categories
     */
    @BlogCategoryApiResponses.GetAll
    @GetMapping
    public ResponseEntity<Response<List<BlogCategoryResponse>>> getAllCategories() {
        log.info("Fetching all blog categories");
        List<BlogCategoryResponse> categories = blogCategoryService.getAllCategories();
        return ResponseEntity.ok(Response.success(categories, "Blog categories retrieved successfully"));
    }

    /**
     * Retrieves a specific blog category by its ID.
     *
     * @param id the category ID
     * @return response entity containing the category
     */
    @BlogCategoryApiResponses.GetById
    @GetMapping("/{id}")
    public ResponseEntity<Response<BlogCategoryResponse>> getCategoryById(
            @Parameter(description = "Category ID") @PathVariable Long id) {
        log.info("Fetching blog category with id: {}", id);
        BlogCategoryResponse category = blogCategoryService.getCategoryById(id);
        return ResponseEntity.ok(Response.success(category, "Blog category retrieved successfully"));
    }

    /**
     * Retrieves a blog category by its URL slug.
     *
     * @param slug the category slug
     * @return response entity containing the category
     */
    @BlogCategoryApiResponses.GetBySlug
    @GetMapping("/slug/{slug}")
    public ResponseEntity<Response<BlogCategoryResponse>> getCategoryBySlug(
            @Parameter(description = "Category slug") @PathVariable String slug) {
        log.info("Fetching blog category with slug: {}", slug);
        BlogCategoryResponse category = blogCategoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(Response.success(category, "Blog category retrieved successfully"));
    }

    /**
     * Creates a new blog category.
     *
     * @param request the category creation request
     * @return response entity containing the created category with HTTP 201 status
     */
    @BlogCategoryApiResponses.Create
    @PostMapping
    public ResponseEntity<Response<BlogCategoryResponse>> createCategory(
            @Valid @RequestBody CreateBlogCategoryRequest request) {
        log.info("Creating new blog category: {}", request.getName());
        BlogCategoryResponse category = blogCategoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success(category, "Blog category created successfully"));
    }

    /**
     * Updates an existing blog category.
     *
     * @param id the category ID
     * @param request the category update request
     * @return response entity containing the updated category
     */
    @BlogCategoryApiResponses.Update
    @PutMapping("/{id}")
    public ResponseEntity<Response<BlogCategoryResponse>> updateCategory(
            @Parameter(description = "Category ID") @PathVariable Long id,
            @Valid @RequestBody UpdateBlogCategoryRequest request) {
        log.info("Updating blog category with id: {}", id);
        BlogCategoryResponse category = blogCategoryService.updateCategory(id, request);
        return ResponseEntity.ok(Response.success(category, "Blog category updated successfully"));
    }

    /**
     * Deletes a blog category by its ID.
     *
     * @param id the category ID
     * @return response entity with success message
     */
    @BlogCategoryApiResponses.Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteCategory(
            @Parameter(description = "Category ID") @PathVariable Long id) {
        log.info("Deleting blog category with id: {}", id);
        blogCategoryService.deleteCategory(id);
        return ResponseEntity.ok(Response.success(null, "Blog category deleted successfully"));
    }
}
