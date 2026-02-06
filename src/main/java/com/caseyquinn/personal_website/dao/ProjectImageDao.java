package com.caseyquinn.personal_website.dao;

import com.caseyquinn.personal_website.entity.ProjectImage;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for ProjectImage operations
 * Provides abstraction layer over ProjectImageRepository with exception translation
 */
public interface ProjectImageDao {

    /**
     * Save or update a project image
     */
    ProjectImage save(ProjectImage image);

    /**
     * Find a project image by ID
     */
    Optional<ProjectImage> findById(Long id);

    /**
     * Find a project image by ID or throw NotFoundException
     */
    ProjectImage findByIdOrThrow(Long id);

    /**
     * Find all images for a project, ordered by display order
     */
    List<ProjectImage> findByProjectId(Long projectId);

    /**
     * Delete a project image by ID
     */
    void deleteById(Long id);

    /**
     * Count images for a project
     */
    long countByProjectId(Long projectId);

    /**
     * Unset the primary flag for all images in a project
     */
    void unsetPrimaryForProject(Long projectId);
}
