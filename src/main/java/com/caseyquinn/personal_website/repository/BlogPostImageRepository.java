package com.caseyquinn.personal_website.repository;

import com.caseyquinn.personal_website.entity.BlogPostImage;
import com.caseyquinn.personal_website.entity.enums.BlogImageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BlogPostImage persistence operations.
 */
@Repository
public interface BlogPostImageRepository extends JpaRepository<BlogPostImage, Long> {

    List<BlogPostImage> findByBlogPostIdOrderByDisplayOrderAsc(Long blogPostId);

    Optional<BlogPostImage> findByBlogPostIdAndIsPrimaryTrue(Long blogPostId);

    List<BlogPostImage> findByBlogPostIdAndImageType(Long blogPostId, BlogImageType imageType);

    long countByBlogPostId(Long blogPostId);

    @Modifying
    @Query("UPDATE BlogPostImage i SET i.isPrimary = false WHERE i.blogPost.id = :blogPostId")
    void clearPrimaryFlag(@Param("blogPostId") Long blogPostId);

    @Modifying
    @Query("UPDATE BlogPostImage i SET i.isPrimary = false WHERE i.blogPost.id = :blogPostId AND i.id != :imageId")
    void clearPrimaryFlagExcept(@Param("blogPostId") Long blogPostId, @Param("imageId") Long imageId);
}
