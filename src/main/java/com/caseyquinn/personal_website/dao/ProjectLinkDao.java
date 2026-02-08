package com.caseyquinn.personal_website.dao;

import com.caseyquinn.personal_website.entity.ProjectLink;
import com.caseyquinn.personal_website.entity.enums.LinkType;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for ProjectLink operations.
 * Provides abstraction layer over ProjectLinkRepository with exception translation.
 */
public interface ProjectLinkDao {

    /**
     * Save or update a project link.
     */
    ProjectLink save(ProjectLink link);

    /**
     * Find a project link by ID.
     */
    Optional<ProjectLink> findById(Long id);

    /**
     * Find a project link by ID or throw NotFoundException.
     */
    ProjectLink findByIdOrThrow(Long id);

    /**
     * Find all links for a project, ordered by type and display order.
     */
    List<ProjectLink> findByProjectId(Long projectId);

    /**
     * Find all links of a specific type for a project.
     */
    List<ProjectLink> findByProjectIdAndType(Long projectId, LinkType linkType);

    /**
     * Delete a project link by ID.
     */
    void deleteById(Long id);

    /**
     * Count links for a project.
     */
    long countByProjectId(Long projectId);
}
