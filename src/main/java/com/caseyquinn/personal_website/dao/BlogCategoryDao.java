package com.caseyquinn.personal_website.dao;

import com.caseyquinn.personal_website.entity.BlogCategory;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for BlogCategory operations.
 * Provides abstraction layer over BlogCategoryRepository with exception translation.
 */
public interface BlogCategoryDao {

    /**
     * Retrieves all blog categories ordered by name.
     *
     * @return list of all blog categories
     */
    List<BlogCategory> findAll();

    /**
     * Finds a blog category by ID.
     *
     * @param id the category ID
     * @return optional containing the category if found
     */
    Optional<BlogCategory> findById(Long id);

    /**
     * Finds a blog category by ID or throws NotFoundException.
     *
     * @param id the category ID
     * @return the blog category
     */
    BlogCategory findByIdOrThrow(Long id);

    /**
     * Finds a blog category by name.
     *
     * @param name the category name
     * @return optional containing the category if found
     */
    Optional<BlogCategory> findByName(String name);

    /**
     * Finds a blog category by slug.
     *
     * @param slug the category slug
     * @return optional containing the category if found
     */
    Optional<BlogCategory> findBySlug(String slug);

    /**
     * Checks if a blog category exists with the given name.
     *
     * @param name the category name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Checks if a blog category exists with the given slug.
     *
     * @param slug the category slug
     * @return true if exists, false otherwise
     */
    boolean existsBySlug(String slug);

    /**
     * Saves or updates a blog category.
     *
     * @param category the category to save
     * @return the saved category
     */
    BlogCategory save(BlogCategory category);

    /**
     * Deletes a blog category by ID.
     *
     * @param id the category ID
     */
    void deleteById(Long id);

    /**
     * Counts total blog categories.
     *
     * @return total category count
     */
    long count();
}
