package com.caseyquinn.personal_website.service;

import com.caseyquinn.personal_website.dao.BlogPostDao;
import com.caseyquinn.personal_website.dao.BlogPostImageDao;
import com.caseyquinn.personal_website.dto.request.CreateBlogPostImageRequest;
import com.caseyquinn.personal_website.dto.request.UpdateBlogPostImageRequest;
import com.caseyquinn.personal_website.dto.response.BlogPostImageResponse;
import com.caseyquinn.personal_website.entity.BlogPost;
import com.caseyquinn.personal_website.entity.BlogPostImage;
import com.caseyquinn.personal_website.entity.enums.BlogImageType;
import com.caseyquinn.personal_website.exception.ErrorCode;
import com.caseyquinn.personal_website.exception.business.ValidationException;
import com.caseyquinn.personal_website.mapper.BlogPostImageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.caseyquinn.personal_website.exception.ErrorMessages.BLOG_IMAGE_OWNERSHIP_MISMATCH;
import static com.caseyquinn.personal_website.exception.ErrorMessages.MAX_BLOG_IMAGES_EXCEEDED_FORMAT;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

/**
 * Service layer for managing blog post images and their business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BlogPostImageService {

    private static final int MAX_IMAGES_PER_POST = 20;

    private final BlogPostImageDao blogPostImageDao;
    private final BlogPostDao blogPostDao;
    private final BlogPostImageMapper blogPostImageMapper;

    /**
     * Retrieves all images for a blog post ordered by display order.
     *
     * @param postId the blog post ID
     * @return list of image responses
     */
    public List<BlogPostImageResponse> getImagesByPostId(Long postId) {
        log.info("Service: Fetching images for blog post id: {}", postId);
        blogPostDao.findByIdOrThrow(postId);
        List<BlogPostImage> images = blogPostImageDao.findByBlogPostId(postId);
        return blogPostImageMapper.toResponseList(images);
    }

    /**
     * Retrieves a specific image by ID after validating ownership.
     *
     * @param postId the blog post ID
     * @param imageId the image ID
     * @return image response
     */
    public BlogPostImageResponse getImageById(Long postId, Long imageId) {
        log.info("Service: Fetching image with id: {} for post id: {}", imageId, postId);
        BlogPostImage image = blogPostImageDao.findByIdOrThrow(imageId);
        validateImageOwnership(image, postId);
        return blogPostImageMapper.toResponse(image);
    }

    /**
     * Creates a new image for a blog post.
     *
     * @param postId the blog post ID
     * @param request the image creation request
     * @return the created image response
     */
    @Transactional
    public BlogPostImageResponse createImage(Long postId, CreateBlogPostImageRequest request) {
        log.info("Service: Creating image for blog post id: {}", postId);

        BlogPost post = blogPostDao.findByIdOrThrow(postId);
        validateImageLimit(postId);

        BlogPostImage image = blogPostImageMapper.toEntity(request);
        image.setBlogPost(post);
        applyDefaults(image, request);

        if (isTrue(request.getIsPrimary())) {
            blogPostImageDao.clearPrimaryFlag(postId);
            image.setIsPrimary(true);
        }

        BlogPostImage saved = blogPostImageDao.save(image);
        log.info("Service: Successfully created image with id: {}", saved.getId());
        return blogPostImageMapper.toResponse(saved);
    }

    /**
     * Updates an existing image metadata.
     *
     * @param postId the blog post ID
     * @param imageId the image ID
     * @param request the image update request
     * @return the updated image response
     */
    @Transactional
    public BlogPostImageResponse updateImage(Long postId, Long imageId, UpdateBlogPostImageRequest request) {
        log.info("Service: Updating image with id: {} for post id: {}", imageId, postId);

        BlogPostImage image = blogPostImageDao.findByIdOrThrow(imageId);
        validateImageOwnership(image, postId);
        blogPostImageMapper.updateEntityFromRequest(request, image);

        BlogPostImage updated = blogPostImageDao.save(image);
        log.info("Service: Successfully updated image with id: {}", imageId);
        return blogPostImageMapper.toResponse(updated);
    }

    /**
     * Deletes an image from a blog post.
     *
     * @param postId the blog post ID
     * @param imageId the image ID
     */
    @Transactional
    public void deleteImage(Long postId, Long imageId) {
        log.info("Service: Deleting image with id: {} from post id: {}", imageId, postId);

        BlogPostImage image = blogPostImageDao.findByIdOrThrow(imageId);
        validateImageOwnership(image, postId);

        blogPostImageDao.deleteById(imageId);
        log.info("Service: Successfully deleted image with id: {}", imageId);
    }

    /**
     * Sets an image as the primary image for a blog post.
     *
     * @param postId the blog post ID
     * @param imageId the image ID
     * @return the updated image response
     */
    @Transactional
    public BlogPostImageResponse setPrimaryImage(Long postId, Long imageId) {
        log.info("Service: Setting image {} as primary for post id: {}", imageId, postId);

        BlogPostImage image = blogPostImageDao.findByIdOrThrow(imageId);
        validateImageOwnership(image, postId);

        blogPostImageDao.clearPrimaryFlagExcept(postId, imageId);
        image.setIsPrimary(true);

        BlogPostImage updated = blogPostImageDao.save(image);
        log.info("Service: Successfully set image {} as primary", imageId);
        return blogPostImageMapper.toResponse(updated);
    }

    /**
     * Retrieves images of a specific type for a blog post.
     *
     * @param postId the blog post ID
     * @param imageType the image type
     * @return list of matching image responses
     */
    public List<BlogPostImageResponse> getImagesByType(Long postId, BlogImageType imageType) {
        log.info("Service: Fetching {} images for blog post id: {}", imageType, postId);
        blogPostDao.findByIdOrThrow(postId);
        List<BlogPostImage> images = blogPostImageDao.findByBlogPostIdAndImageType(postId, imageType);
        return blogPostImageMapper.toResponseList(images);
    }

    private void validateImageOwnership(BlogPostImage image, Long postId) {
        if (!image.getBlogPost().getId().equals(postId)) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, BLOG_IMAGE_OWNERSHIP_MISMATCH);
        }
    }

    private void validateImageLimit(Long postId) {
        long currentCount = blogPostImageDao.countByBlogPostId(postId);
        if (currentCount >= MAX_IMAGES_PER_POST) {
            throw new ValidationException(ErrorCode.MAX_BLOG_IMAGES_EXCEEDED,
                    String.format(MAX_BLOG_IMAGES_EXCEEDED_FORMAT, MAX_IMAGES_PER_POST));
        }
    }

    private void applyDefaults(BlogPostImage image, CreateBlogPostImageRequest request) {
        if (isNull(image.getImageType())) {
            image.setImageType(BlogImageType.INLINE);
        }
        if (isNull(image.getDisplayOrder())) {
            image.setDisplayOrder(0);
        }
        if (isNull(image.getIsPrimary())) {
            image.setIsPrimary(false);
        }
    }
}
