package com.caseyquinn.personal_website.dao;

import com.caseyquinn.personal_website.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for BlogPost operations.
 * Provides abstraction layer over BlogPostRepository with exception translation.
 */
public interface BlogPostDao {

    /**
     * Retrieves all blog posts ordered by creation date.
     *
     * @return list of all blog posts
     */
    List<BlogPost> findAll();

    /**
     * Retrieves published blog posts ordered by published date.
     *
     * @return list of published blog posts
     */
    List<BlogPost> findPublished();

    /**
     * Retrieves published blog posts with pagination.
     *
     * @param pageable the pagination information
     * @return page of published blog posts
     */
    Page<BlogPost> findPublished(Pageable pageable);

    /**
     * Finds a blog post by ID.
     *
     * @param id the post ID
     * @return optional containing the post if found
     */
    Optional<BlogPost> findById(Long id);

    /**
     * Finds a blog post by ID or throws NotFoundException.
     *
     * @param id the post ID
     * @return the blog post
     */
    BlogPost findByIdOrThrow(Long id);

    /**
     * Finds a blog post by title.
     *
     * @param title the post title
     * @return optional containing the post if found
     */
    Optional<BlogPost> findByTitle(String title);

    /**
     * Finds a blog post by slug.
     *
     * @param slug the post slug
     * @return optional containing the post if found
     */
    Optional<BlogPost> findBySlug(String slug);

    /**
     * Checks if a blog post exists with the given title.
     *
     * @param title the post title
     * @return true if exists, false otherwise
     */
    boolean existsByTitle(String title);

    /**
     * Checks if a blog post exists with the given slug.
     *
     * @param slug the post slug
     * @return true if exists, false otherwise
     */
    boolean existsBySlug(String slug);

    /**
     * Saves or updates a blog post.
     *
     * @param post the post to save
     * @return the saved post
     */
    BlogPost save(BlogPost post);

    /**
     * Deletes a blog post by ID.
     *
     * @param id the post ID
     */
    void deleteById(Long id);

    /**
     * Finds published blog posts by category ID.
     *
     * @param categoryId the category ID
     * @return list of matching posts
     */
    List<BlogPost> findByCategoryId(Long categoryId);

    /**
     * Finds published blog posts by category slug.
     *
     * @param slug the category slug
     * @return list of matching published posts
     */
    List<BlogPost> findPublishedByCategorySlug(String slug);

    /**
     * Finds blog posts by tag ID.
     *
     * @param tagId the tag ID
     * @return list of matching posts
     */
    List<BlogPost> findByTagId(Long tagId);

    /**
     * Finds published blog posts by tag slug.
     *
     * @param slug the tag slug
     * @return list of matching published posts
     */
    List<BlogPost> findPublishedByTagSlug(String slug);

    /**
     * Searches published blog posts by title or content.
     *
     * @param query the search query
     * @return list of matching published posts
     */
    List<BlogPost> searchPublished(String query);

    /**
     * Increments the view count for a blog post.
     *
     * @param id the post ID
     */
    void incrementViewCount(Long id);

    /**
     * Counts total blog posts.
     *
     * @return total post count
     */
    long count();

    /**
     * Counts published blog posts.
     *
     * @return published post count
     */
    long countPublished();
}
