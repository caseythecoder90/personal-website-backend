package com.caseyquinn.personal_website.service;

import com.caseyquinn.personal_website.dao.BlogPostDao;
import com.caseyquinn.personal_website.dao.BlogPostImageDao;
import com.caseyquinn.personal_website.dto.request.CreateBlogPostImageRequest;
import com.caseyquinn.personal_website.dto.request.UpdateBlogPostImageRequest;
import com.caseyquinn.personal_website.dto.response.BlogPostImageResponse;
import com.caseyquinn.personal_website.dto.response.CloudinaryUploadResult;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.caseyquinn.personal_website.exception.ErrorMessages.BLOG_IMAGE_OWNERSHIP_MISMATCH;
import static com.caseyquinn.personal_website.exception.ErrorMessages.MAX_BLOG_IMAGES_EXCEEDED_FORMAT;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

/**
 * Service layer for managing blog post images including upload, retrieval, and deletion.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BlogPostImageService {

    private static final int MAX_IMAGES_PER_POST = 20;

    private final BlogPostImageDao blogPostImageDao;
    private final BlogPostDao blogPostDao;
    private final CloudinaryService cloudinaryService;
    private final FileValidationService fileValidationService;
    private final BlogPostImageMapper blogPostImageMapper;

    /**
     * Uploads a new image for a blog post with validation and compensating transaction handling.
     *
     * @param postId the blog post ID
     * @param file the image file to upload
     * @param request the image creation request containing metadata
     * @return the uploaded image response
     */
    @Transactional
    public BlogPostImageResponse uploadImage(Long postId, MultipartFile file, CreateBlogPostImageRequest request) {
        log.info("Uploading image for blog post id: {}", postId);

        BlogPost post = blogPostDao.findByIdOrThrow(postId);
        fileValidationService.validateImageFile(file);
        validateImageLimit(postId);

        CloudinaryUploadResult uploadResult = uploadToCloudinary(file, post.getSlug(), postId);
        BlogPostImage image = buildBlogPostImage(request, post, uploadResult);

        handlePrimaryImageFlag(postId, request.getIsPrimary());

        return saveImageWithCompensation(image, uploadResult.getPublicId(), postId);
    }

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

        if (isTrue(request.getIsPrimary()) && isNotTrue(image.getIsPrimary())) {
            blogPostImageDao.clearPrimaryFlag(postId);
        }

        blogPostImageMapper.updateEntityFromRequest(request, image);

        BlogPostImage updated = blogPostImageDao.save(image);
        log.info("Service: Successfully updated image with id: {}", imageId);
        return blogPostImageMapper.toResponse(updated);
    }

    /**
     * Deletes an image from a blog post with ownership validation.
     * Performs best-effort deletion from Cloudinary.
     *
     * @param postId the blog post ID
     * @param imageId the image ID
     */
    @Transactional
    public void deleteImage(Long postId, Long imageId) {
        log.info("Service: Deleting image with id: {} from post id: {}", imageId, postId);

        BlogPostImage image = blogPostImageDao.findByIdOrThrow(imageId);
        validateImageOwnership(image, postId);

        String cloudinaryPublicId = image.getCloudinaryPublicId();

        blogPostImageDao.deleteById(imageId);
        log.info("Service: Image deleted from database: imageId={}", imageId);

        deleteFromCloudinaryBestEffort(cloudinaryPublicId);
    }

    /**
     * Sets an image as the primary image for a blog post.
     * Unsets any existing primary image first.
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

    /**
     * Validates that an image count does not exceed the maximum allowed per post.
     *
     * @param postId the blog post ID to check
     * @throws ValidationException if the maximum image count is exceeded
     */
    private void validateImageLimit(Long postId) {
        long currentCount = blogPostImageDao.countByBlogPostId(postId);
        if (currentCount >= MAX_IMAGES_PER_POST) {
            throw new ValidationException(ErrorCode.MAX_BLOG_IMAGES_EXCEEDED,
                    String.format(MAX_BLOG_IMAGES_EXCEEDED_FORMAT, MAX_IMAGES_PER_POST));
        }
    }

    /**
     * Validates that an image belongs to the specified blog post.
     *
     * @param image the image to validate
     * @param postId the expected blog post ID
     * @throws ValidationException if the image does not belong to the post
     */
    private void validateImageOwnership(BlogPostImage image, Long postId) {
        if (!image.getBlogPost().getId().equals(postId)) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, BLOG_IMAGE_OWNERSHIP_MISMATCH);
        }
    }

    /**
     * Uploads an image file to Cloudinary with error handling.
     *
     * @param file the file to upload
     * @param subFolder the subfolder path in Cloudinary (blog post slug)
     * @param postId the blog post ID for logging
     * @return the upload result containing URL and public ID
     * @throws ValidationException if upload fails
     */
    private CloudinaryUploadResult uploadToCloudinary(MultipartFile file, String subFolder, Long postId) {
        try {
            return cloudinaryService.uploadImage(file, "blog/" + subFolder);
        } catch (ValidationException e) {
            log.error("Failed to upload image to Cloudinary for blog post id: {}", postId, e);
            throw e;
        }
    }

    /**
     * Builds a BlogPostImage entity from the request and upload result.
     *
     * @param request the creation request containing metadata
     * @param post the blog post to associate with
     * @param uploadResult the Cloudinary upload result
     * @return the constructed BlogPostImage entity
     */
    private BlogPostImage buildBlogPostImage(CreateBlogPostImageRequest request, BlogPost post,
                                              CloudinaryUploadResult uploadResult) {
        return BlogPostImage.builder()
                .blogPost(post)
                .url(uploadResult.getSecureUrl())
                .cloudinaryPublicId(uploadResult.getPublicId())
                .altText(request.getAltText())
                .caption(request.getCaption())
                .imageType(isNull(request.getImageType()) ? BlogImageType.INLINE : request.getImageType())
                .displayOrder(isNull(request.getDisplayOrder()) ? 0 : request.getDisplayOrder())
                .isPrimary(isTrue(request.getIsPrimary()))
                .build();
    }

    /**
     * Handles the primary image flag by unsetting any existing primary image if needed.
     *
     * @param postId the blog post ID
     * @param isPrimary whether the new image should be primary
     */
    private void handlePrimaryImageFlag(Long postId, Boolean isPrimary) {
        if (isTrue(isPrimary)) {
            blogPostImageDao.clearPrimaryFlag(postId);
        }
    }

    /**
     * Saves an image with compensating transaction support.
     * If database save fails, deletes the uploaded image from Cloudinary.
     *
     * @param image the image to save
     * @param cloudinaryPublicId the Cloudinary public ID for rollback
     * @param postId the blog post ID for logging
     * @return the saved image response
     */
    private BlogPostImageResponse saveImageWithCompensation(BlogPostImage image, String cloudinaryPublicId,
                                                             Long postId) {
        try {
            BlogPostImage savedImage = blogPostImageDao.save(image);
            log.info("Image saved successfully: imageId={}, postId={}", savedImage.getId(), postId);
            return blogPostImageMapper.toResponse(savedImage);

        } catch (Exception e) {
            log.error("Failed to save image to database, rolling back Cloudinary upload", e);
            cloudinaryService.deleteImage(cloudinaryPublicId);
            throw e;
        }
    }

    /**
     * Attempts to delete an image from Cloudinary with best-effort error handling.
     * Logs errors but does not throw exceptions.
     *
     * @param cloudinaryPublicId the Cloudinary public ID to delete
     */
    private void deleteFromCloudinaryBestEffort(String cloudinaryPublicId) {
        try {
            cloudinaryService.deleteImage(cloudinaryPublicId);
        } catch (Exception e) {
            log.error("Failed to delete image from Cloudinary (continuing): publicId={}", cloudinaryPublicId, e);
        }
    }
}
