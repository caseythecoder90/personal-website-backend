package com.caseyquinn.personal_website.dao.impl;

import com.caseyquinn.personal_website.dao.BlogPostImageDao;
import com.caseyquinn.personal_website.entity.BlogPostImage;
import com.caseyquinn.personal_website.entity.enums.BlogImageType;
import com.caseyquinn.personal_website.exception.NotFoundException;
import com.caseyquinn.personal_website.exception.data.RetryableDataAccess;
import com.caseyquinn.personal_website.repository.BlogPostImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of BlogPostImageDao with automatic retry on transient data access failures.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@RetryableDataAccess
public class BlogPostImageDaoImpl implements BlogPostImageDao {

    private final BlogPostImageRepository blogPostImageRepository;

    @Override
    public List<BlogPostImage> findByBlogPostId(Long blogPostId) {
        log.info("DAO: Fetching images for blog post id: {}", blogPostId);
        return blogPostImageRepository.findByBlogPostIdOrderByDisplayOrderAsc(blogPostId);
    }

    @Override
    public Optional<BlogPostImage> findById(Long id) {
        log.info("DAO: Fetching blog post image with id: {}", id);
        return blogPostImageRepository.findById(id);
    }

    @Override
    public BlogPostImage findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("BlogPostImage", id));
    }

    @Override
    public Optional<BlogPostImage> findPrimaryByBlogPostId(Long blogPostId) {
        log.info("DAO: Fetching primary image for blog post id: {}", blogPostId);
        return blogPostImageRepository.findByBlogPostIdAndIsPrimaryTrue(blogPostId);
    }

    @Override
    public List<BlogPostImage> findByBlogPostIdAndImageType(Long blogPostId, BlogImageType imageType) {
        log.info("DAO: Fetching images for blog post id: {} with type: {}", blogPostId, imageType);
        return blogPostImageRepository.findByBlogPostIdAndImageType(blogPostId, imageType);
    }

    @Override
    public BlogPostImage save(BlogPostImage image) {
        log.info("DAO: Saving blog post image");
        BlogPostImage saved = blogPostImageRepository.save(image);
        log.info("DAO: Successfully saved blog post image with id: {}", saved.getId());
        return saved;
    }

    @Override
    public void deleteById(Long id) {
        log.info("DAO: Deleting blog post image with id: {}", id);
        if (!blogPostImageRepository.existsById(id)) {
            throw new NotFoundException("BlogPostImage", id);
        }
        blogPostImageRepository.deleteById(id);
        log.info("DAO: Successfully deleted blog post image with id: {}", id);
    }

    @Override
    public long countByBlogPostId(Long blogPostId) {
        log.info("DAO: Counting images for blog post id: {}", blogPostId);
        return blogPostImageRepository.countByBlogPostId(blogPostId);
    }

    @Override
    public void clearPrimaryFlag(Long blogPostId) {
        log.info("DAO: Clearing primary flag for blog post id: {}", blogPostId);
        blogPostImageRepository.clearPrimaryFlag(blogPostId);
    }

    @Override
    public void clearPrimaryFlagExcept(Long blogPostId, Long imageId) {
        log.info("DAO: Clearing primary flag for blog post id: {} except image id: {}", blogPostId, imageId);
        blogPostImageRepository.clearPrimaryFlagExcept(blogPostId, imageId);
    }
}
