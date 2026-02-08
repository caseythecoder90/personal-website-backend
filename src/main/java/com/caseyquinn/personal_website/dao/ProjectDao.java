package com.caseyquinn.personal_website.dao;

import com.caseyquinn.personal_website.entity.Project;
import com.caseyquinn.personal_website.entity.enums.ProjectType;
import com.caseyquinn.personal_website.entity.enums.ProjectStatus;
import com.caseyquinn.personal_website.entity.enums.DifficultyLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Project operations.
 * Provides abstraction layer over ProjectRepository with exception translation.
 */
public interface ProjectDao {

    /**
     * Retrieves all projects.
     *
     * @return list of all projects
     */
    List<Project> findAll();

    /**
     * Retrieves projects with pagination.
     *
     * @param pageable pagination parameters
     * @return paginated projects
     */
    Page<Project> findAll(Pageable pageable);

    /**
     * Finds a project by ID.
     *
     * @param id the project ID
     * @return optional containing the project if found
     */
    Optional<Project> findById(Long id);

    /**
     * Finds a project by ID or throws NotFoundException.
     *
     * @param id the project ID
     * @return the project
     */
    Project findByIdOrThrow(Long id);

    /**
     * Finds a project by name.
     *
     * @param name the project name
     * @return optional containing the project if found
     */
    Optional<Project> findByName(String name);

    /**
     * Finds a project by slug.
     *
     * @param slug the project slug
     * @return optional containing the project if found
     */
    Optional<Project> findBySlug(String slug);

    /**
     * Checks if a project exists with the given name.
     *
     * @param name the project name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Checks if a project exists with the given slug.
     *
     * @param slug the project slug
     * @return true if exists, false otherwise
     */
    boolean existsBySlug(String slug);

    /**
     * Saves or updates a project.
     *
     * @param project the project to save
     * @return the saved project
     */
    Project save(Project project);

    /**
     * Deletes a project by ID.
     *
     * @param id the project ID
     */
    void deleteById(Long id);

    /**
     * Checks if a project exists with the given ID.
     *
     * @param id the project ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Finds projects associated with a technology by name.
     *
     * @param technologyName the technology name
     * @return list of matching projects
     */
    List<Project> findByTechnologyName(String technologyName);

    /**
     * Finds projects associated with a technology by ID.
     *
     * @param technologyId the technology ID
     * @return list of matching projects
     */
    List<Project> findByTechnologyId(Long technologyId);

    /**
     * Finds published projects ordered by display order.
     *
     * @return list of published projects
     */
    List<Project> findPublishedProjectsOrderedByDisplay();

    /**
     * Finds featured and published projects.
     *
     * @return list of featured published projects
     */
    List<Project> findFeaturedPublishedProjects();

    /**
     * Finds projects by type.
     *
     * @param projectType the project type
     * @return list of matching projects
     */
    List<Project> findByProjectType(ProjectType projectType);

    /**
     * Finds projects by status.
     *
     * @param status the project status
     * @return list of matching projects
     */
    List<Project> findByStatus(ProjectStatus status);

    /**
     * Finds projects by difficulty level.
     *
     * @param difficultyLevel the difficulty level
     * @return list of matching projects
     */
    List<Project> findByDifficultyLevel(DifficultyLevel difficultyLevel);

    /**
     * Finds projects sorted by view count in descending order.
     *
     * @return list of most viewed projects
     */
    List<Project> findMostViewedProjects();

    /**
     * Finds published projects by type ordered by display order.
     *
     * @param projectType the project type
     * @return list of matching projects
     */
    List<Project> findPublishedByTypeOrderedByDisplay(ProjectType projectType);

    /**
     * Counts published projects.
     *
     * @return count of published projects
     */
    long countPublishedProjects();

    /**
     * Counts projects grouped by status.
     *
     * @return list of status and count pairs
     */
    List<Object[]> countByStatus();

    /**
     * Counts projects grouped by type.
     *
     * @return list of type and count pairs
     */
    List<Object[]> countByType();

    /**
     * Increments the view count for a project.
     *
     * @param id the project ID
     */
    void incrementViewCount(Long id);

    /**
     * Counts total projects.
     *
     * @return total project count
     */
    long count();
}