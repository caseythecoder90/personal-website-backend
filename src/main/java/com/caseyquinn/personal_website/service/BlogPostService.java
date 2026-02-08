package com.caseyquinn.personal_website.service;

import com.caseyquinn.personal_website.dao.BlogCategoryDao;
import com.caseyquinn.personal_website.dao.BlogPostDao;
import com.caseyquinn.personal_website.dao.BlogTagDao;
import com.caseyquinn.personal_website.dto.request.CreateBlogPostRequest;
import com.caseyquinn.personal_website.dto.request.UpdateBlogPostRequest;
import com.caseyquinn.personal_website.dto.response.BlogPostResponse;
import com.caseyquinn.personal_website.entity.BlogCategory;
import com.caseyquinn.personal_website.entity.BlogPost;
import com.caseyquinn.personal_website.entity.BlogTag;
import com.caseyquinn.personal_website.exception.ErrorCode;
import com.caseyquinn.personal_website.exception.NotFoundException;
import com.caseyquinn.personal_website.exception.business.DuplicateResourceException;
import com.caseyquinn.personal_website.exception.business.ValidationException;
import com.caseyquinn.personal_website.mapper.BlogPostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.caseyquinn.personal_website.exception.ErrorMessages.BLOG_CATEGORY_ALREADY_ASSOCIATED;
import static com.caseyquinn.personal_website.exception.ErrorMessages.BLOG_CATEGORY_NOT_ASSOCIATED;
import static com.caseyquinn.personal_website.exception.ErrorMessages.BLOG_TAG_ALREADY_ASSOCIATED;
import static com.caseyquinn.personal_website.exception.ErrorMessages.BLOG_TAG_NOT_ASSOCIATED;
import static com.caseyquinn.personal_website.exception.ErrorMessages.CANNOT_DELETE_PUBLISHED_POST;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Service layer for managing blog posts and their business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BlogPostService {

    private final BlogPostDao blogPostDao;
    private final BlogCategoryDao blogCategoryDao;
    private final BlogTagDao blogTagDao;
    private final BlogPostMapper blogPostMapper;

    /**
     * Retrieves all blog posts ordered by creation date.
     *
     * @return list of all blog post responses
     */
    public List<BlogPostResponse> getAllPosts() {
        log.info("Service: Fetching all blog posts");
        List<BlogPost> posts = blogPostDao.findAll();
        return blogPostMapper.toResponseList(posts);
    }

    /**
     * Retrieves all published blog posts ordered by published date.
     *
     * @return list of published blog post responses
     */
    public List<BlogPostResponse> getPublishedPosts() {
        log.info("Service: Fetching published blog posts");
        List<BlogPost> posts = blogPostDao.findPublished();
        return blogPostMapper.toResponseList(posts);
    }

    /**
     * Retrieves a specific blog post by its ID.
     *
     * @param id the post ID
     * @return blog post response
     */
    public BlogPostResponse getPostById(Long id) {
        log.info("Service: Fetching blog post with id: {}", id);
        BlogPost post = blogPostDao.findByIdOrThrow(id);
        return blogPostMapper.toResponse(post);
    }

    /**
     * Retrieves a blog post by its URL slug and increments view count.
     *
     * @param slug the post slug
     * @return blog post response
     */
    @Transactional
    public BlogPostResponse getPostBySlug(String slug) {
        log.info("Service: Fetching blog post with slug: {}", slug);
        BlogPost post = blogPostDao.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("BlogPost", "slug", slug));
        blogPostDao.incrementViewCount(post.getId());
        return blogPostMapper.toResponse(post);
    }

    /**
     * Creates a new blog post with validation and optional category/tag associations.
     *
     * @param request the post creation request
     * @return the created blog post response
     */
    @Transactional
    public BlogPostResponse createPost(CreateBlogPostRequest request) {
        log.info("Service: Creating new blog post: {}", request.getTitle());

        validatePostCreation(request);

        BlogPost post = blogPostMapper.toEntity(request);
        applyDefaults(post, request);
        associateCategories(post, request.getCategoryIds());
        associateTags(post, request.getTagIds());

        BlogPost saved = blogPostDao.save(post);
        log.info("Service: Successfully created blog post with id: {}", saved.getId());
        return blogPostMapper.toResponse(saved);
    }

    /**
     * Updates an existing blog post with validation.
     *
     * @param id the post ID
     * @param request the post update request
     * @return the updated blog post response
     */
    @Transactional
    public BlogPostResponse updatePost(Long id, UpdateBlogPostRequest request) {
        log.info("Service: Updating blog post with id: {}", id);

        BlogPost existing = blogPostDao.findByIdOrThrow(id);
        validatePostUpdate(request, existing);
        blogPostMapper.updateEntityFromRequest(request, existing);
        updateCategoryAssociations(existing, request.getCategoryIds());
        updateTagAssociations(existing, request.getTagIds());

        BlogPost updated = blogPostDao.save(existing);
        log.info("Service: Successfully updated blog post with id: {}", id);
        return blogPostMapper.toResponse(updated);
    }

    /**
     * Deletes a blog post after validating it can be deleted.
     *
     * @param id the post ID
     */
    @Transactional
    public void deletePost(Long id) {
        log.info("Service: Deleting blog post with id: {}", id);

        BlogPost post = blogPostDao.findByIdOrThrow(id);
        validatePostDeletion(post);

        blogPostDao.deleteById(id);
        log.info("Service: Successfully deleted blog post with id: {}", id);
    }

    /**
     * Publishes a blog post, setting the publishedAt timestamp.
     *
     * @param id the post ID
     * @return the updated blog post response
     */
    @Transactional
    public BlogPostResponse publishPost(Long id) {
        log.info("Service: Publishing blog post with id: {}", id);

        BlogPost post = blogPostDao.findByIdOrThrow(id);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        BlogPost updated = blogPostDao.save(post);
        log.info("Service: Successfully published blog post with id: {}", id);
        return blogPostMapper.toResponse(updated);
    }

    /**
     * Unpublishes a blog post, clearing the publishedAt timestamp.
     *
     * @param id the post ID
     * @return the updated blog post response
     */
    @Transactional
    public BlogPostResponse unpublishPost(Long id) {
        log.info("Service: Unpublishing blog post with id: {}", id);

        BlogPost post = blogPostDao.findByIdOrThrow(id);
        post.setPublished(false);
        post.setPublishedAt(null);

        BlogPost updated = blogPostDao.save(post);
        log.info("Service: Successfully unpublished blog post with id: {}", id);
        return blogPostMapper.toResponse(updated);
    }

    /**
     * Retrieves published blog posts by category slug.
     *
     * @param slug the category slug
     * @return list of matching blog post responses
     */
    public List<BlogPostResponse> getPostsByCategorySlug(String slug) {
        log.info("Service: Fetching blog posts by category slug: {}", slug);
        List<BlogPost> posts = blogPostDao.findPublishedByCategorySlug(slug);
        return blogPostMapper.toResponseList(posts);
    }

    /**
     * Retrieves published blog posts by tag slug.
     *
     * @param slug the tag slug
     * @return list of matching blog post responses
     */
    public List<BlogPostResponse> getPostsByTagSlug(String slug) {
        log.info("Service: Fetching blog posts by tag slug: {}", slug);
        List<BlogPost> posts = blogPostDao.findPublishedByTagSlug(slug);
        return blogPostMapper.toResponseList(posts);
    }

    /**
     * Searches published blog posts by title or content.
     *
     * @param query the search query
     * @return list of matching blog post responses
     */
    public List<BlogPostResponse> searchPosts(String query) {
        log.info("Service: Searching blog posts with query: {}", query);
        List<BlogPost> posts = blogPostDao.searchPublished(query);
        return blogPostMapper.toResponseList(posts);
    }

    /**
     * Associates a category with a blog post.
     *
     * @param postId the post ID
     * @param categoryId the category ID
     * @return the updated blog post response
     */
    @Transactional
    public BlogPostResponse addCategoryToPost(Long postId, Long categoryId) {
        log.info("Service: Adding category {} to blog post {}", categoryId, postId);

        BlogPost post = blogPostDao.findByIdOrThrow(postId);
        BlogCategory category = blogCategoryDao.findByIdOrThrow(categoryId);

        if (post.getCategories().contains(category)) {
            throw new ValidationException(ErrorCode.DUPLICATE_BLOG_CATEGORY_ASSOCIATION, BLOG_CATEGORY_ALREADY_ASSOCIATED);
        }

        post.addCategory(category);
        BlogPost updated = blogPostDao.save(post);

        log.info("Service: Successfully added category to blog post");
        return blogPostMapper.toResponse(updated);
    }

    /**
     * Removes a category association from a blog post.
     *
     * @param postId the post ID
     * @param categoryId the category ID
     * @return the updated blog post response
     */
    @Transactional
    public BlogPostResponse removeCategoryFromPost(Long postId, Long categoryId) {
        log.info("Service: Removing category {} from blog post {}", categoryId, postId);

        BlogPost post = blogPostDao.findByIdOrThrow(postId);
        BlogCategory category = blogCategoryDao.findByIdOrThrow(categoryId);

        if (!post.getCategories().contains(category)) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, BLOG_CATEGORY_NOT_ASSOCIATED);
        }

        post.removeCategory(category);
        BlogPost updated = blogPostDao.save(post);

        log.info("Service: Successfully removed category from blog post");
        return blogPostMapper.toResponse(updated);
    }

    /**
     * Associates a tag with a blog post.
     *
     * @param postId the post ID
     * @param tagId the tag ID
     * @return the updated blog post response
     */
    @Transactional
    public BlogPostResponse addTagToPost(Long postId, Long tagId) {
        log.info("Service: Adding tag {} to blog post {}", tagId, postId);

        BlogPost post = blogPostDao.findByIdOrThrow(postId);
        BlogTag tag = blogTagDao.findByIdOrThrow(tagId);

        if (post.getTags().contains(tag)) {
            throw new ValidationException(ErrorCode.DUPLICATE_BLOG_TAG_ASSOCIATION, BLOG_TAG_ALREADY_ASSOCIATED);
        }

        post.addTag(tag);
        blogTagDao.incrementUsageCount(tagId);
        BlogPost updated = blogPostDao.save(post);

        log.info("Service: Successfully added tag to blog post");
        return blogPostMapper.toResponse(updated);
    }

    /**
     * Removes a tag association from a blog post.
     *
     * @param postId the post ID
     * @param tagId the tag ID
     * @return the updated blog post response
     */
    @Transactional
    public BlogPostResponse removeTagFromPost(Long postId, Long tagId) {
        log.info("Service: Removing tag {} from blog post {}", tagId, postId);

        BlogPost post = blogPostDao.findByIdOrThrow(postId);
        BlogTag tag = blogTagDao.findByIdOrThrow(tagId);

        if (!post.getTags().contains(tag)) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, BLOG_TAG_NOT_ASSOCIATED);
        }

        post.removeTag(tag);
        blogTagDao.decrementUsageCount(tagId);
        BlogPost updated = blogPostDao.save(post);

        log.info("Service: Successfully removed tag from blog post");
        return blogPostMapper.toResponse(updated);
    }

    private void validatePostCreation(CreateBlogPostRequest request) {
        if (blogPostDao.existsByTitle(request.getTitle())) {
            throw new DuplicateResourceException("BlogPost", "title", request.getTitle());
        }
    }

    private void validatePostUpdate(UpdateBlogPostRequest request, BlogPost existing) {
        if (isNotBlank(request.getTitle()) && !existing.getTitle().equals(request.getTitle())
                && blogPostDao.existsByTitle(request.getTitle())) {
            throw new DuplicateResourceException("BlogPost", "title", request.getTitle());
        }
    }

    private void validatePostDeletion(BlogPost post) {
        if (isTrue(post.getPublished())) {
            throw new ValidationException(ErrorCode.CANNOT_DELETE_PUBLISHED, CANNOT_DELETE_PUBLISHED_POST);
        }
    }

    private void applyDefaults(BlogPost post, CreateBlogPostRequest request) {
        if (isNull(post.getPublished())) {
            post.setPublished(false);
        }
        if (isTrue(request.getPublished())) {
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
        }
        if (isNull(post.getViewCount())) {
            post.setViewCount(0);
        }
    }

    private void associateCategories(BlogPost post, Set<Long> categoryIds) {
        if (isNotEmpty(categoryIds)) {
            for (Long categoryId : categoryIds) {
                BlogCategory category = blogCategoryDao.findByIdOrThrow(categoryId);
                post.addCategory(category);
            }
        }
    }

    private void associateTags(BlogPost post, Set<Long> tagIds) {
        if (isNotEmpty(tagIds)) {
            for (Long tagId : tagIds) {
                BlogTag tag = blogTagDao.findByIdOrThrow(tagId);
                post.addTag(tag);
                blogTagDao.incrementUsageCount(tagId);
            }
        }
    }

    private void updateCategoryAssociations(BlogPost post, Set<Long> categoryIds) {
        if (nonNull(categoryIds)) {
            post.getCategories().clear();
            associateCategories(post, categoryIds);
        }
    }

    private void updateTagAssociations(BlogPost post, Set<Long> tagIds) {
        if (nonNull(tagIds)) {
            for (BlogTag oldTag : post.getTags()) {
                blogTagDao.decrementUsageCount(oldTag.getId());
            }
            post.getTags().clear();
            associateTags(post, tagIds);
        }
    }
}
