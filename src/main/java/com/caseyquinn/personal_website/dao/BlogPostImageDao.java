package com.caseyquinn.personal_website.dao;

import com.caseyquinn.personal_website.entity.BlogPostImage;
import com.caseyquinn.personal_website.entity.enums.BlogImageType;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for BlogPostImage operations.
 * Provides abstraction layer over BlogPostImageRepository with exception translation.
 */
public interface BlogPostImageDao {

    /**
     * Retrieves all images for a blog post ordered by display order.
     *
     * @param blogPostId the blog post ID
     * @return list of images for the post
     */
    List<BlogPostImage> findByBlogPostId(Long blogPostId);

    /**
     * Finds an image by ID.
     *
     * @param id the image ID
     * @return optional containing the image if found
     */
    Optional<BlogPostImage> findById(Long id);

    /**
     * Finds an image by ID or throws NotFoundException.
     *
     * @param id the image ID
     * @return the blog post image
     */
    BlogPostImage findByIdOrThrow(Long id);

    /**
     * Finds the primary image for a blog post.
     *
     * @param blogPostId the blog post ID
     * @return optional containing the primary image if found
     */
    Optional<BlogPostImage> findPrimaryByBlogPostId(Long blogPostId);

    /**
     * Finds images by blog post ID and image type.
     *
     * @param blogPostId the blog post ID
     * @param imageType the image type
     * @return list of matching images
     */
    List<BlogPostImage> findByBlogPostIdAndImageType(Long blogPostId, BlogImageType imageType);

    /**
     * Saves or updates an image.
     *
     * @param image the image to save
     * @return the saved image
     */
    BlogPostImage save(BlogPostImage image);

    /**
     * Deletes an image by ID.
     *
     * @param id the image ID
     */
    void deleteById(Long id);

    /**
     * Counts images for a blog post.
     *
     * @param blogPostId the blog post ID
     * @return image count
     */
    long countByBlogPostId(Long blogPostId);

    /**
     * Clears the primary flag for all images in a blog post.
     *
     * @param blogPostId the blog post ID
     */
    void clearPrimaryFlag(Long blogPostId);

    /**
     * Clears the primary flag for all images except the specified one.
     *
     * @param blogPostId the blog post ID
     * @param imageId the image ID to exclude
     */
    void clearPrimaryFlagExcept(Long blogPostId, Long imageId);
}
