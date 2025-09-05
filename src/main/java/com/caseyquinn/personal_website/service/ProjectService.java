package com.caseyquinn.personal_website.service;

import com.caseyquinn.personal_website.dao.ProjectDao;
import com.caseyquinn.personal_website.dao.TechnologyDao;
import com.caseyquinn.personal_website.dto.request.CreateProjectRequest;
import com.caseyquinn.personal_website.dto.request.UpdateProjectRequest;
import com.caseyquinn.personal_website.dto.response.ProjectResponse;
import com.caseyquinn.personal_website.entity.Project;
import com.caseyquinn.personal_website.entity.Technology;
import com.caseyquinn.personal_website.entity.enums.ProjectType;
import com.caseyquinn.personal_website.entity.enums.ProjectStatus;
import com.caseyquinn.personal_website.entity.enums.DifficultyLevel;
import com.caseyquinn.personal_website.exception.business.DuplicateProjectException;
import com.caseyquinn.personal_website.exception.business.ProjectValidationException;
import com.caseyquinn.personal_website.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProjectService {
    
    private final ProjectDao projectDao;
    private final TechnologyDao technologyDao;
    private final ProjectMapper projectMapper;
    
    public List<ProjectResponse> getAllProjects() {
        log.info("Service: Fetching all projects");
        List<Project> projects = projectDao.findAll();
        return projectMapper.toResponseList(projects);
    }
    
    public Page<ProjectResponse> getProjectsPaginated(Pageable pageable) {
        log.info("Service: Fetching projects with pagination: {}", pageable);
        Page<Project> projects = projectDao.findAll(pageable);
        return projects.map(projectMapper::toResponse);
    }
    
    public ProjectResponse getProjectById(Long id) {
        log.info("Service: Fetching project with id: {}", id);
        Project project = projectDao.findByIdOrThrow(id);
        return projectMapper.toResponse(project);
    }
    
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        log.info("Service: Creating new project: {}", request.getName());
        
        // Business validation
        validateProjectCreation(request);
        
        Project project = projectMapper.toEntity(request);
        
        // Business logic: Set defaults
        if (project.getPublished() == null) {
            project.setPublished(false);
        }
        if (project.getFeatured() == null) {
            project.setFeatured(false);
        }
        if (project.getStatus() == null) {
            project.setStatus(ProjectStatus.PLANNING);
        }
        
        // Handle technology associations if provided
        if (request.getTechnologyIds() != null) {
            for (Long techId : request.getTechnologyIds()) {
                Technology technology = technologyDao.findByIdOrThrow(techId);
                project.addTechnology(technology);
            }
        }
        
        Project savedProject = projectDao.save(project);
        log.info("Service: Successfully created project with id: {}", savedProject.getId());
        return projectMapper.toResponse(savedProject);
    }
    
    @Transactional
    public ProjectResponse updateProject(Long id, UpdateProjectRequest request) {
        log.info("Service: Updating project with id: {}", id);
        
        Project existingProject = projectDao.findByIdOrThrow(id);
        
        // Business validation
        validateProjectUpdate(request, existingProject);
        
        // Handle technology associations if provided
        if (request.getTechnologyIds() != null) {
            // Clear existing technologies and add new ones
            existingProject.getTechnologies().clear();
            for (Long techId : request.getTechnologyIds()) {
                Technology technology = technologyDao.findByIdOrThrow(techId);
                existingProject.addTechnology(technology);
            }
        }
        
        projectMapper.updateEntityFromUpdateRequest(request, existingProject);
        Project updatedProject = projectDao.save(existingProject);
        
        log.info("Service: Successfully updated project with id: {}", id);
        return projectMapper.toResponse(updatedProject);
    }
    
    @Transactional
    public void deleteProject(Long id) {
        log.info("Service: Deleting project with id: {}", id);
        
        Project project = projectDao.findByIdOrThrow(id);
        
        // Business logic: Check if project can be deleted
        validateProjectDeletion(project);
        
        projectDao.deleteById(id);
        log.info("Service: Successfully deleted project with id: {}", id);
    }
    
    public List<ProjectResponse> getProjectsByTechnology(String technology) {
        log.info("Service: Fetching projects by technology: {}", technology);
        List<Project> projects = projectDao.findByTechStackContaining(technology);
        return projectMapper.toResponseList(projects);
    }
    
    // New methods for enhanced functionality
    public ProjectResponse getProjectBySlug(String slug) {
        log.info("Service: Fetching project with slug: {}", slug);
        Project project = projectDao.findBySlug(slug)
                .orElseThrow(() -> new ProjectValidationException("Project not found with slug: " + slug));
        return projectMapper.toResponse(project);
    }
    
    public List<ProjectResponse> getProjectsByTechnologyName(String technologyName) {
        log.info("Service: Fetching projects by technology name: {}", technologyName);
        List<Project> projects = projectDao.findByTechnologyName(technologyName);
        return projectMapper.toResponseList(projects);
    }
    
    public List<ProjectResponse> getProjectsByTechnologyId(Long technologyId) {
        log.info("Service: Fetching projects by technology id: {}", technologyId);
        
        // Validate technology exists
        technologyDao.findByIdOrThrow(technologyId);
        
        List<Project> projects = projectDao.findByTechnologyId(technologyId);
        return projectMapper.toResponseList(projects);
    }
    
    public List<ProjectResponse> getPublishedProjectsOrderedByDisplay() {
        log.info("Service: Fetching published projects ordered by display");
        List<Project> projects = projectDao.findPublishedProjectsOrderedByDisplay();
        return projectMapper.toResponseList(projects);
    }
    
    public List<ProjectResponse> getFeaturedPublishedProjects() {
        log.info("Service: Fetching featured published projects");
        List<Project> projects = projectDao.findFeaturedPublishedProjects();
        return projectMapper.toResponseList(projects);
    }
    
    public List<ProjectResponse> getProjectsByType(ProjectType type) {
        log.info("Service: Fetching projects by type: {}", type);
        List<Project> projects = projectDao.findByProjectType(type);
        return projectMapper.toResponseList(projects);
    }
    
    public List<ProjectResponse> getProjectsByStatus(ProjectStatus status) {
        log.info("Service: Fetching projects by status: {}", status);
        List<Project> projects = projectDao.findByStatus(status);
        return projectMapper.toResponseList(projects);
    }
    
    public List<ProjectResponse> getProjectsByDifficultyLevel(DifficultyLevel difficultyLevel) {
        log.info("Service: Fetching projects by difficulty level: {}", difficultyLevel);
        List<Project> projects = projectDao.findByDifficultyLevel(difficultyLevel);
        return projectMapper.toResponseList(projects);
    }
    
    public List<ProjectResponse> getMostViewedProjects() {
        log.info("Service: Fetching most viewed projects");
        List<Project> projects = projectDao.findMostViewedProjects();
        return projectMapper.toResponseList(projects);
    }
    
    public List<ProjectResponse> getPublishedProjectsByTypeOrderedByDisplay(ProjectType type) {
        log.info("Service: Fetching published projects by type ordered by display: {}", type);
        List<Project> projects = projectDao.findPublishedByTypeOrderedByDisplay(type);
        return projectMapper.toResponseList(projects);
    }
    
    @Transactional
    public ProjectResponse addTechnologyToProject(Long projectId, Long technologyId) {
        log.info("Service: Adding technology {} to project {}", technologyId, projectId);
        
        Project project = projectDao.findByIdOrThrow(projectId);
        Technology technology = technologyDao.findByIdOrThrow(technologyId);
        
        // Business validation
        if (project.getTechnologies().contains(technology)) {
            throw new ProjectValidationException("Technology is already associated with this project");
        }
        
        project.addTechnology(technology);
        Project updatedProject = projectDao.save(project);
        
        log.info("Service: Successfully added technology to project");
        return projectMapper.toResponse(updatedProject);
    }
    
    @Transactional
    public ProjectResponse removeTechnologyFromProject(Long projectId, Long technologyId) {
        log.info("Service: Removing technology {} from project {}", technologyId, projectId);
        
        Project project = projectDao.findByIdOrThrow(projectId);
        Technology technology = technologyDao.findByIdOrThrow(technologyId);
        
        // Business validation
        if (!project.getTechnologies().contains(technology)) {
            throw new ProjectValidationException("Technology is not associated with this project");
        }
        
        project.removeTechnology(technology);
        Project updatedProject = projectDao.save(project);
        
        log.info("Service: Successfully removed technology from project");
        return projectMapper.toResponse(updatedProject);
    }
    
    @Transactional
    public void incrementProjectViewCount(Long id) {
        log.info("Service: Incrementing view count for project: {}", id);
        projectDao.incrementViewCount(id);
    }
    
    // Analytics methods
    public long getPublishedProjectCount() {
        log.info("Service: Getting published project count");
        return projectDao.countPublishedProjects();
    }
    
    public List<Object[]> getProjectStatsByStatus() {
        log.info("Service: Getting project statistics by status");
        return projectDao.countByStatus();
    }
    
    public List<Object[]> getProjectStatsByType() {
        log.info("Service: Getting project statistics by type");
        return projectDao.countByType();
    }
    
    // Business validation methods
    private void validateProjectCreation(CreateProjectRequest request) {
        // Business rule: No duplicate project names
        if (projectDao.existsByName(request.getName())) {
            throw new DuplicateProjectException(request.getName());
        }
        
        // Business rule: GitHub URL must be unique if provided
        if (request.getGithubUrl() != null) {
            // Additional business validation logic can be added here
        }
        
        // Business rule: Maximum 10 active projects
        long activeProjects = projectDao.count();
        if (activeProjects >= 10) {
            throw new ProjectValidationException("Maximum number of projects (10) reached");
        }
    }
    
    private void validateProjectUpdate(UpdateProjectRequest request, Project existingProject) {
        // Business rule: Can't change name to existing name
        if (!existingProject.getName().equals(request.getName()) && 
            projectDao.existsByName(request.getName())) {
            throw new DuplicateProjectException(request.getName());
        }
        
        // Business rule: Can't change slug to existing slug if provided
        if (request.getSlug() != null && !existingProject.getSlug().equals(request.getSlug()) && 
            projectDao.existsBySlug(request.getSlug())) {
            throw new ProjectValidationException("Project with slug '" + request.getSlug() + "' already exists");
        }
    }
    
    private void validateProjectDeletion(Project project) {
        // Business rule: Can't delete published projects
        if (Boolean.TRUE.equals(project.getPublished())) {
            throw new ProjectValidationException("Cannot delete published projects. Unpublish first.");
        }
    }
}