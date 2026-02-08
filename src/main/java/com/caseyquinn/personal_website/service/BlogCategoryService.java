package com.caseyquinn.personal_website.service;

import com.caseyquinn.personal_website.dao.BlogCategoryDao;
import com.caseyquinn.personal_website.dto.request.CreateBlogCategoryRequest;
import com.caseyquinn.personal_website.dto.request.UpdateBlogCategoryRequest;
import com.caseyquinn.personal_website.dto.response.BlogCategoryResponse;
import com.caseyquinn.personal_website.entity.BlogCategory;
import com.caseyquinn.personal_website.exception.ErrorCode;
import com.caseyquinn.personal_website.exception.NotFoundException;
import com.caseyquinn.personal_website.exception.business.DuplicateResourceException;
import com.caseyquinn.personal_website.exception.business.ValidationException;
import com.caseyquinn.personal_website.mapper.BlogCategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.caseyquinn.personal_website.exception.ErrorMessages.BLOG_CATEGORY_IN_USE;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Service layer for managing blog categories and their business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BlogCategoryService {

    private final BlogCategoryDao blogCategoryDao;
    private final BlogCategoryMapper blogCategoryMapper;

    /**
     * Retrieves all blog categories ordered by name.
     *
     * @return list of all blog category responses
     */
    public List<BlogCategoryResponse> getAllCategories() {
        log.info("Service: Fetching all blog categories");
        List<BlogCategory> categories = blogCategoryDao.findAll();
        return blogCategoryMapper.toResponseList(categories);
    }

    /**
     * Retrieves a specific blog category by its ID.
     *
     * @param id the category ID
     * @return blog category response
     */
    public BlogCategoryResponse getCategoryById(Long id) {
        log.info("Service: Fetching blog category with id: {}", id);
        BlogCategory category = blogCategoryDao.findByIdOrThrow(id);
        return blogCategoryMapper.toResponse(category);
    }

    /**
     * Retrieves a blog category by its URL slug.
     *
     * @param slug the category slug
     * @return blog category response
     */
    public BlogCategoryResponse getCategoryBySlug(String slug) {
        log.info("Service: Fetching blog category with slug: {}", slug);
        BlogCategory category = blogCategoryDao.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("BlogCategory", "slug", slug));
        return blogCategoryMapper.toResponse(category);
    }

    /**
     * Creates a new blog category with validation.
     *
     * @param request the category creation request
     * @return the created blog category response
     */
    @Transactional
    public BlogCategoryResponse createCategory(CreateBlogCategoryRequest request) {
        log.info("Service: Creating new blog category: {}", request.getName());

        validateCategoryCreation(request);

        BlogCategory category = blogCategoryMapper.toEntity(request);
        BlogCategory saved = blogCategoryDao.save(category);

        log.info("Service: Successfully created blog category with id: {}", saved.getId());
        return blogCategoryMapper.toResponse(saved);
    }

    /**
     * Updates an existing blog category with validation.
     *
     * @param id the category ID
     * @param request the category update request
     * @return the updated blog category response
     */
    @Transactional
    public BlogCategoryResponse updateCategory(Long id, UpdateBlogCategoryRequest request) {
        log.info("Service: Updating blog category with id: {}", id);

        BlogCategory existing = blogCategoryDao.findByIdOrThrow(id);
        validateCategoryUpdate(request, existing);
        blogCategoryMapper.updateEntityFromRequest(request, existing);

        BlogCategory updated = blogCategoryDao.save(existing);
        log.info("Service: Successfully updated blog category with id: {}", id);
        return blogCategoryMapper.toResponse(updated);
    }

    /**
     * Deletes a blog category after validating it can be deleted.
     *
     * @param id the category ID
     */
    @Transactional
    public void deleteCategory(Long id) {
        log.info("Service: Deleting blog category with id: {}", id);

        BlogCategory category = blogCategoryDao.findByIdOrThrow(id);
        validateCategoryDeletion(category);

        blogCategoryDao.deleteById(id);
        log.info("Service: Successfully deleted blog category with id: {}", id);
    }

    private void validateCategoryCreation(CreateBlogCategoryRequest request) {
        if (blogCategoryDao.existsByName(request.getName())) {
            throw new DuplicateResourceException("BlogCategory", "name", request.getName());
        }
    }

    private void validateCategoryUpdate(UpdateBlogCategoryRequest request, BlogCategory existing) {
        if (isNotBlank(request.getName()) && !existing.getName().equals(request.getName())
                && blogCategoryDao.existsByName(request.getName())) {
            throw new DuplicateResourceException("BlogCategory", "name", request.getName());
        }
    }

    private void validateCategoryDeletion(BlogCategory category) {
        if (isNotEmpty(category.getBlogPosts())) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, BLOG_CATEGORY_IN_USE);
        }
    }
}
