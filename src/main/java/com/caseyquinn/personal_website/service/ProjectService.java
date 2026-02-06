package com.caseyquinn.personal_website.service;

import com.caseyquinn.personal_website.dao.ProjectDao;
import com.caseyquinn.personal_website.dao.ProjectImageDao;
import com.caseyquinn.personal_website.dao.TechnologyDao;
import com.caseyquinn.personal_website.dto.request.CreateProjectRequest;
import com.caseyquinn.personal_website.dto.request.UpdateProjectRequest;
import com.caseyquinn.personal_website.dto.response.ProjectResponse;
import com.caseyquinn.personal_website.entity.Project;
import com.caseyquinn.personal_website.entity.ProjectImage;
import com.caseyquinn.personal_website.entity.Technology;
import com.caseyquinn.personal_website.mapper.ProjectImageMapper;
import com.caseyquinn.personal_website.entity.enums.ProjectType;
import com.caseyquinn.personal_website.entity.enums.ProjectStatus;
import com.caseyquinn.personal_website.entity.enums.DifficultyLevel;
import com.caseyquinn.personal_website.exception.ErrorCode;
import com.caseyquinn.personal_website.exception.NotFoundException;
import com.caseyquinn.personal_website.exception.business.DuplicateResourceException;
import com.caseyquinn.personal_website.exception.business.ValidationException;
import com.caseyquinn.personal_website.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.caseyquinn.personal_website.exception.ErrorMessages.*;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

/**
 * Service layer for managing portfolio projects and their business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectDao projectDao;
    private final TechnologyDao technologyDao;
    private final ProjectImageDao projectImageDao;
    private final ProjectMapper projectMapper;
    private final ProjectImageMapper projectImageMapper;
    
    /**
     * Retrieves all projects without pagination.
     *
     * @return list of all project responses
     */
    public List<ProjectResponse> getAllProjects() {
        log.info("Service: Fetching all projects");
        List<Project> projects = projectDao.findAll();
        return projectMapper.toResponseList(projects);
    }

    /**
     * Retrieves projects with pagination support.
     *
     * @param pageable pagination parameters
     * @return paginated project responses
     */
    public Page<ProjectResponse> getProjectsPaginated(Pageable pageable) {
        log.info("Service: Fetching projects with pagination: {}", pageable);
        Page<Project> projects = projectDao.findAll(pageable);
        return projects.map(projectMapper::toResponse);
    }

    /**
     * Retrieves a specific project by its ID including associated images.
     *
     * @param id the project ID
     * @return project response with images
     */
    public ProjectResponse getProjectById(Long id) {
        log.info("Service: Fetching project with id: {}", id);
        Project project = projectDao.findByIdOrThrow(id);
        List<ProjectImage> images = projectImageDao.findByProjectId(id);

        ProjectResponse response = projectMapper.toResponse(project);
        response.setImages(projectImageMapper.toResponseList(images));
        return response;
    }
    
    /**
     * Creates a new project with validation and default value handling.
     *
     * @param request the project creation request
     * @return the created project response
     */
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        log.info("Service: Creating new project: {}", request.getName());

        validateProjectCreation(request);

        Project project = projectMapper.toEntity(request);
        applyProjectDefaults(project);
        associateTechnologies(project, request.getTechnologyIds());

        Project savedProject = projectDao.save(project);
        log.info("Service: Successfully created project with id: {}", savedProject.getId());
        return projectMapper.toResponse(savedProject);
    }
    
    /**
     * Updates an existing project with validation.
     *
     * @param id the project ID
     * @param request the project update request
     * @return the updated project response
     */
    @Transactional
    public ProjectResponse updateProject(Long id, UpdateProjectRequest request) {
        log.info("Service: Updating project with id: {}", id);

        Project existingProject = projectDao.findByIdOrThrow(id);
        validateProjectUpdate(request, existingProject);
        updateTechnologyAssociations(existingProject, request.getTechnologyIds());
        projectMapper.updateEntityFromUpdateRequest(request, existingProject);

        Project updatedProject = projectDao.save(existingProject);
        log.info("Service: Successfully updated project with id: {}", id);
        return projectMapper.toResponse(updatedProject);
    }
    
    /**
     * Deletes a project after validating it can be deleted.
     *
     * @param id the project ID
     */
    @Transactional
    public void deleteProject(Long id) {
        log.info("Service: Deleting project with id: {}", id);

        Project project = projectDao.findByIdOrThrow(id);

        validateProjectDeletion(project);

        projectDao.deleteById(id);
        log.info("Service: Successfully deleted project with id: {}", id);
    }

    /**
     * Retrieves projects that use a specific technology in their tech stack.
     *
     * @param technology the technology name to search for
     * @return list of matching project responses
     */
    public List<ProjectResponse> getProjectsByTechnology(String technology) {
        log.info("Service: Fetching projects by technology: {}", technology);
        List<Project> projects = projectDao.findByTechStackContaining(technology);
        return projectMapper.toResponseList(projects);
    }
    
    /**
     * Retrieves a project by its URL slug including associated images.
     *
     * @param slug the project slug
     * @return project response with images
     */
    public ProjectResponse getProjectBySlug(String slug) {
        log.info("Service: Fetching project with slug: {}", slug);
        Project project = projectDao.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Project", "slug", slug));
        List<ProjectImage> images = projectImageDao.findByProjectId(project.getId());

        ProjectResponse response = projectMapper.toResponse(project);
        response.setImages(projectImageMapper.toResponseList(images));
        return response;
    }
    
    /**
     * Retrieves projects associated with a specific technology by name.
     *
     * @param technologyName the technology name
     * @return list of matching project responses
     */
    public List<ProjectResponse> getProjectsByTechnologyName(String technologyName) {
        log.info("Service: Fetching projects by technology name: {}", technologyName);
        List<Project> projects = projectDao.findByTechnologyName(technologyName);
        return projectMapper.toResponseList(projects);
    }

    /**
     * Retrieves projects associated with a specific technology by ID.
     *
     * @param technologyId the technology ID
     * @return list of matching project responses
     */
    public List<ProjectResponse> getProjectsByTechnologyId(Long technologyId) {
        log.info("Service: Fetching projects by technology id: {}", technologyId);

        technologyDao.findByIdOrThrow(technologyId);

        List<Project> projects = projectDao.findByTechnologyId(technologyId);
        return projectMapper.toResponseList(projects);
    }

    /**
     * Retrieves published projects ordered by display order.
     *
     * @return list of published project responses in display order
     */
    public List<ProjectResponse> getPublishedProjectsOrderedByDisplay() {
        log.info("Service: Fetching published projects ordered by display");
        List<Project> projects = projectDao.findPublishedProjectsOrderedByDisplay();
        return projectMapper.toResponseList(projects);
    }

    /**
     * Retrieves featured projects that are published.
     *
     * @return list of featured published project responses
     */
    public List<ProjectResponse> getFeaturedPublishedProjects() {
        log.info("Service: Fetching featured published projects");
        List<Project> projects = projectDao.findFeaturedPublishedProjects();
        return projectMapper.toResponseList(projects);
    }

    /**
     * Retrieves projects filtered by project type.
     *
     * @param type the project type
     * @return list of matching project responses
     */
    public List<ProjectResponse> getProjectsByType(ProjectType type) {
        log.info("Service: Fetching projects by type: {}", type);
        List<Project> projects = projectDao.findByProjectType(type);
        return projectMapper.toResponseList(projects);
    }

    /**
     * Retrieves projects filtered by project status.
     *
     * @param status the project status
     * @return list of matching project responses
     */
    public List<ProjectResponse> getProjectsByStatus(ProjectStatus status) {
        log.info("Service: Fetching projects by status: {}", status);
        List<Project> projects = projectDao.findByStatus(status);
        return projectMapper.toResponseList(projects);
    }

    /**
     * Retrieves projects filtered by difficulty level.
     *
     * @param difficultyLevel the difficulty level
     * @return list of matching project responses
     */
    public List<ProjectResponse> getProjectsByDifficultyLevel(DifficultyLevel difficultyLevel) {
        log.info("Service: Fetching projects by difficulty level: {}", difficultyLevel);
        List<Project> projects = projectDao.findByDifficultyLevel(difficultyLevel);
        return projectMapper.toResponseList(projects);
    }

    /**
     * Retrieves projects sorted by view count in descending order.
     *
     * @return list of most viewed project responses
     */
    public List<ProjectResponse> getMostViewedProjects() {
        log.info("Service: Fetching most viewed projects");
        List<Project> projects = projectDao.findMostViewedProjects();
        return projectMapper.toResponseList(projects);
    }

    /**
     * Retrieves published projects of a specific type ordered by display order.
     *
     * @param type the project type
     * @return list of matching project responses in display order
     */
    public List<ProjectResponse> getPublishedProjectsByTypeOrderedByDisplay(ProjectType type) {
        log.info("Service: Fetching published projects by type ordered by display: {}", type);
        List<Project> projects = projectDao.findPublishedByTypeOrderedByDisplay(type);
        return projectMapper.toResponseList(projects);
    }
    
    /**
     * Associates a technology with a project.
     *
     * @param projectId the project ID
     * @param technologyId the technology ID
     * @return the updated project response
     */
    @Transactional
    public ProjectResponse addTechnologyToProject(Long projectId, Long technologyId) {
        log.info("Service: Adding technology {} to project {}", technologyId, projectId);

        Project project = projectDao.findByIdOrThrow(projectId);
        Technology technology = technologyDao.findByIdOrThrow(technologyId);

        if (project.getTechnologies().contains(technology)) {
            throw new ValidationException(ErrorCode.DUPLICATE_TECH_ASSOCIATION, TECHNOLOGY_ALREADY_ASSOCIATED);
        }

        project.addTechnology(technology);
        Project updatedProject = projectDao.save(project);

        log.info("Service: Successfully added technology to project");
        return projectMapper.toResponse(updatedProject);
    }

    /**
     * Removes a technology association from a project.
     *
     * @param projectId the project ID
     * @param technologyId the technology ID
     * @return the updated project response
     */
    @Transactional
    public ProjectResponse removeTechnologyFromProject(Long projectId, Long technologyId) {
        log.info("Service: Removing technology {} from project {}", technologyId, projectId);

        Project project = projectDao.findByIdOrThrow(projectId);
        Technology technology = technologyDao.findByIdOrThrow(technologyId);

        if (!project.getTechnologies().contains(technology)) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, TECHNOLOGY_NOT_ASSOCIATED);
        }

        project.removeTechnology(technology);
        Project updatedProject = projectDao.save(project);

        log.info("Service: Successfully removed technology from project");
        return projectMapper.toResponse(updatedProject);
    }

    /**
     * Increments the view count for a project.
     *
     * @param id the project ID
     */
    @Transactional
    public void incrementProjectViewCount(Long id) {
        log.info("Service: Incrementing view count for project: {}", id);
        projectDao.incrementViewCount(id);
    }

    /**
     * Retrieves the count of published projects.
     *
     * @return the number of published projects
     */
    public long getPublishedProjectCount() {
        log.info("Service: Getting published project count");
        return projectDao.countPublishedProjects();
    }

    /**
     * Retrieves project statistics grouped by status.
     *
     * @return list of status and count pairs
     */
    public List<Object[]> getProjectStatsByStatus() {
        log.info("Service: Getting project statistics by status");
        return projectDao.countByStatus();
    }

    /**
     * Retrieves project statistics grouped by type.
     *
     * @return list of type and count pairs
     */
    public List<Object[]> getProjectStatsByType() {
        log.info("Service: Getting project statistics by type");
        return projectDao.countByType();
    }
    
    private void validateProjectCreation(CreateProjectRequest request) {
        if (projectDao.existsByName(request.getName())) {
            throw new DuplicateResourceException("Project", "name", request.getName());
        }

        long activeProjects = projectDao.count();
        if (activeProjects >= 10) {
            throw new ValidationException(ErrorCode.MAX_PROJECTS_EXCEEDED, MAX_PROJECTS_EXCEEDED);
        }
    }

    private void validateProjectUpdate(UpdateProjectRequest request, Project existingProject) {
        if (!existingProject.getName().equals(request.getName()) &&
            projectDao.existsByName(request.getName())) {
            throw new DuplicateResourceException("Project", "name", request.getName());
        }

        if (request.getSlug() != null && !existingProject.getSlug().equals(request.getSlug()) &&
            projectDao.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("Project", "slug", request.getSlug());
        }
    }

    private void validateProjectDeletion(Project project) {
        if (isTrue(project.getPublished())) {
            throw new ValidationException(ErrorCode.CANNOT_DELETE_PUBLISHED, CANNOT_DELETE_PUBLISHED);
        }
    }

    /**
     * Applies default values to a new project if not explicitly set.
     *
     * @param project the project to apply defaults to
     */
    private void applyProjectDefaults(Project project) {
        if (isNull(project.getPublished())) {
            project.setPublished(false);
        }
        if (isNull(project.getFeatured())) {
            project.setFeatured(false);
        }
        if (isNull(project.getStatus())) {
            project.setStatus(ProjectStatus.PLANNING);
        }
    }

    /**
     * Associates technologies with a project by loading and linking each technology entity.
     *
     * @param project the project to associate technologies with
     * @param technologyIds the set of technology IDs to associate
     */
    private void associateTechnologies(Project project, Set<Long> technologyIds) {
        if (technologyIds != null) {
            for (Long techId : technologyIds) {
                Technology technology = technologyDao.findByIdOrThrow(techId);
                project.addTechnology(technology);
            }
        }
    }

    /**
     * Updates technology associations for an existing project by clearing old associations
     * and creating new ones.
     *
     * @param project the project to update
     * @param technologyIds the new set of technology IDs to associate
     */
    private void updateTechnologyAssociations(Project project, Set<Long> technologyIds) {
        if (technologyIds != null) {
            project.getTechnologies().clear();
            associateTechnologies(project, technologyIds);
        }
    }
}