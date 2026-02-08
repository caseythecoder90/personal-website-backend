package com.caseyquinn.personal_website.service;

import com.caseyquinn.personal_website.dao.BlogTagDao;
import com.caseyquinn.personal_website.dto.request.CreateBlogTagRequest;
import com.caseyquinn.personal_website.dto.request.UpdateBlogTagRequest;
import com.caseyquinn.personal_website.dto.response.BlogTagResponse;
import com.caseyquinn.personal_website.entity.BlogTag;
import com.caseyquinn.personal_website.exception.ErrorCode;
import com.caseyquinn.personal_website.exception.NotFoundException;
import com.caseyquinn.personal_website.exception.business.DuplicateResourceException;
import com.caseyquinn.personal_website.exception.business.ValidationException;
import com.caseyquinn.personal_website.mapper.BlogTagMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.caseyquinn.personal_website.exception.ErrorMessages.BLOG_TAG_IN_USE;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Service layer for managing blog tags and their business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BlogTagService {

    private final BlogTagDao blogTagDao;
    private final BlogTagMapper blogTagMapper;

    /**
     * Retrieves all blog tags ordered by name.
     *
     * @return list of all blog tag responses
     */
    public List<BlogTagResponse> getAllTags() {
        log.info("Service: Fetching all blog tags");
        List<BlogTag> tags = blogTagDao.findAll();
        return blogTagMapper.toResponseList(tags);
    }

    /**
     * Retrieves top 10 most popular tags by usage count.
     *
     * @return list of popular blog tag responses
     */
    public List<BlogTagResponse> getPopularTags() {
        log.info("Service: Fetching popular blog tags");
        List<BlogTag> tags = blogTagDao.findPopular();
        return blogTagMapper.toResponseList(tags);
    }

    /**
     * Retrieves a specific blog tag by its ID.
     *
     * @param id the tag ID
     * @return blog tag response
     */
    public BlogTagResponse getTagById(Long id) {
        log.info("Service: Fetching blog tag with id: {}", id);
        BlogTag tag = blogTagDao.findByIdOrThrow(id);
        return blogTagMapper.toResponse(tag);
    }

    /**
     * Retrieves a blog tag by its URL slug.
     *
     * @param slug the tag slug
     * @return blog tag response
     */
    public BlogTagResponse getTagBySlug(String slug) {
        log.info("Service: Fetching blog tag with slug: {}", slug);
        BlogTag tag = blogTagDao.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("BlogTag", "slug", slug));
        return blogTagMapper.toResponse(tag);
    }

    /**
     * Creates a new blog tag with validation.
     *
     * @param request the tag creation request
     * @return the created blog tag response
     */
    @Transactional
    public BlogTagResponse createTag(CreateBlogTagRequest request) {
        log.info("Service: Creating new blog tag: {}", request.getName());

        validateTagCreation(request);

        BlogTag tag = blogTagMapper.toEntity(request);
        BlogTag saved = blogTagDao.save(tag);

        log.info("Service: Successfully created blog tag with id: {}", saved.getId());
        return blogTagMapper.toResponse(saved);
    }

    /**
     * Updates an existing blog tag with validation.
     *
     * @param id the tag ID
     * @param request the tag update request
     * @return the updated blog tag response
     */
    @Transactional
    public BlogTagResponse updateTag(Long id, UpdateBlogTagRequest request) {
        log.info("Service: Updating blog tag with id: {}", id);

        BlogTag existing = blogTagDao.findByIdOrThrow(id);
        validateTagUpdate(request, existing);
        blogTagMapper.updateEntityFromRequest(request, existing);

        BlogTag updated = blogTagDao.save(existing);
        log.info("Service: Successfully updated blog tag with id: {}", id);
        return blogTagMapper.toResponse(updated);
    }

    /**
     * Deletes a blog tag after validating it can be deleted.
     *
     * @param id the tag ID
     */
    @Transactional
    public void deleteTag(Long id) {
        log.info("Service: Deleting blog tag with id: {}", id);

        BlogTag tag = blogTagDao.findByIdOrThrow(id);
        validateTagDeletion(tag);

        blogTagDao.deleteById(id);
        log.info("Service: Successfully deleted blog tag with id: {}", id);
    }

    /**
     * Increments the usage count for a tag.
     *
     * @param id the tag ID
     */
    @Transactional
    public void incrementUsageCount(Long id) {
        log.info("Service: Incrementing usage count for tag with id: {}", id);
        blogTagDao.incrementUsageCount(id);
    }

    /**
     * Decrements the usage count for a tag.
     *
     * @param id the tag ID
     */
    @Transactional
    public void decrementUsageCount(Long id) {
        log.info("Service: Decrementing usage count for tag with id: {}", id);
        blogTagDao.decrementUsageCount(id);
    }

    private void validateTagCreation(CreateBlogTagRequest request) {
        if (blogTagDao.existsByName(request.getName())) {
            throw new DuplicateResourceException("BlogTag", "name", request.getName());
        }
    }

    private void validateTagUpdate(UpdateBlogTagRequest request, BlogTag existing) {
        if (isNotBlank(request.getName()) && !existing.getName().equals(request.getName())
                && blogTagDao.existsByName(request.getName())) {
            throw new DuplicateResourceException("BlogTag", "name", request.getName());
        }
    }

    private void validateTagDeletion(BlogTag tag) {
        if (isNotEmpty(tag.getBlogPosts())) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, BLOG_TAG_IN_USE);
        }
    }
}
