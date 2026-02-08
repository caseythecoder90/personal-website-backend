package com.caseyquinn.personal_website.dao;

import com.caseyquinn.personal_website.entity.BlogTag;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for BlogTag operations.
 * Provides abstraction layer over BlogTagRepository with exception translation.
 */
public interface BlogTagDao {

    /**
     * Retrieves all blog tags ordered by name.
     *
     * @return list of all blog tags
     */
    List<BlogTag> findAll();

    /**
     * Retrieves all blog tags ordered by usage count (most used first).
     *
     * @return list of all blog tags ordered by usage
     */
    List<BlogTag> findAllByUsage();

    /**
     * Retrieves top 10 most popular tags.
     *
     * @return list of top 10 tags by usage count
     */
    List<BlogTag> findPopular();

    /**
     * Finds a blog tag by ID.
     *
     * @param id the tag ID
     * @return optional containing the tag if found
     */
    Optional<BlogTag> findById(Long id);

    /**
     * Finds a blog tag by ID or throws NotFoundException.
     *
     * @param id the tag ID
     * @return the blog tag
     */
    BlogTag findByIdOrThrow(Long id);

    /**
     * Finds a blog tag by name.
     *
     * @param name the tag name
     * @return optional containing the tag if found
     */
    Optional<BlogTag> findByName(String name);

    /**
     * Finds a blog tag by slug.
     *
     * @param slug the tag slug
     * @return optional containing the tag if found
     */
    Optional<BlogTag> findBySlug(String slug);

    /**
     * Checks if a blog tag exists with the given name.
     *
     * @param name the tag name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Checks if a blog tag exists with the given slug.
     *
     * @param slug the tag slug
     * @return true if exists, false otherwise
     */
    boolean existsBySlug(String slug);

    /**
     * Saves or updates a blog tag.
     *
     * @param tag the tag to save
     * @return the saved tag
     */
    BlogTag save(BlogTag tag);

    /**
     * Deletes a blog tag by ID.
     *
     * @param id the tag ID
     */
    void deleteById(Long id);

    /**
     * Increments the usage count for a tag.
     *
     * @param id the tag ID
     */
    void incrementUsageCount(Long id);

    /**
     * Decrements the usage count for a tag.
     *
     * @param id the tag ID
     */
    void decrementUsageCount(Long id);

    /**
     * Counts total blog tags.
     *
     * @return total tag count
     */
    long count();
}
