package com.caseyquinn.personal_website.dao.impl;

import com.caseyquinn.personal_website.dao.ProjectDao;
import com.caseyquinn.personal_website.entity.Project;
import com.caseyquinn.personal_website.entity.enums.ProjectType;
import com.caseyquinn.personal_website.entity.enums.ProjectStatus;
import com.caseyquinn.personal_website.entity.enums.DifficultyLevel;
import com.caseyquinn.personal_website.exception.data.DatabaseConnectionException;
import com.caseyquinn.personal_website.exception.data.DataIntegrityException;
import com.caseyquinn.personal_website.exception.data.EntityNotFoundException;
import com.caseyquinn.personal_website.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectDaoImpl implements ProjectDao {
    
    private final ProjectRepository projectRepository;
    
    @Override
    public List<Project> findAll() {
        try {
            log.debug("DAO: Fetching all projects");
            return projectRepository.findAll();
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching all projects", ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching all projects", ex);
            throw new DataIntegrityException("Failed to fetch projects", ex);
        }
    }
    
    @Override
    public Page<Project> findAll(Pageable pageable) {
        try {
            log.debug("DAO: Fetching projects with pagination: {}", pageable);
            return projectRepository.findAll(pageable);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching paginated projects", ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching paginated projects", ex);
            throw new DataIntegrityException("Failed to fetch paginated projects", ex);
        }
    }
    
    @Override
    public Optional<Project> findById(Long id) {
        try {
            log.debug("DAO: Fetching project with id: {}", id);
            return projectRepository.findById(id);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching project with id: {}", id, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching project with id: {}", id, ex);
            throw new DataIntegrityException("Failed to fetch project", ex);
        }
    }
    
    @Override
    public Project findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project", id));
    }
    
    @Override
    public Optional<Project> findByName(String name) {
        try {
            log.debug("DAO: Fetching project with name: {}", name);
            return projectRepository.findByName(name);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching project with name: {}", name, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching project with name: {}", name, ex);
            throw new DataIntegrityException("Failed to fetch project by name", ex);
        }
    }
    
    @Override
    public boolean existsByName(String name) {
        try {
            log.debug("DAO: Checking if project exists with name: {}", name);
            return projectRepository.existsByName(name);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while checking project existence with name: {}", name, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while checking project existence with name: {}", name, ex);
            throw new DataIntegrityException("Failed to check project existence", ex);
        }
    }
    
    @Override
    public Project save(Project project) {
        try {
            log.debug("DAO: Saving project: {}", project.getName());
            Project savedProject = projectRepository.save(project);
            log.debug("DAO: Successfully saved project with id: {}", savedProject.getId());
            return savedProject;
        } catch (DataIntegrityViolationException ex) {
            log.error("Data integrity violation while saving project: {}", project.getName(), ex);
            throw new DataIntegrityException("Data integrity violation while saving project", ex);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while saving project: {}", project.getName(), ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while saving project: {}", project.getName(), ex);
            throw new DataIntegrityException("Failed to save project", ex);
        }
    }
    
    @Override
    public void deleteById(Long id) {
        try {
            log.debug("DAO: Deleting project with id: {}", id);
            if (!projectRepository.existsById(id)) {
                throw new EntityNotFoundException("Project", id);
            }
            projectRepository.deleteById(id);
            log.debug("DAO: Successfully deleted project with id: {}", id);
        } catch (EntityNotFoundException ex) {
            throw ex; // Re-throw our custom exception
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while deleting project with id: {}", id, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while deleting project with id: {}", id, ex);
            throw new DataIntegrityException("Failed to delete project", ex);
        }
    }
    
    @Override
    public boolean existsById(Long id) {
        try {
            log.debug("DAO: Checking if project exists with id: {}", id);
            return projectRepository.existsById(id);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while checking project existence with id: {}", id, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while checking project existence with id: {}", id, ex);
            throw new DataIntegrityException("Failed to check project existence", ex);
        }
    }
    
    @Override
    public List<Project> findByTechStackContaining(String technology) {
        try {
            log.debug("DAO: Fetching projects containing technology: {}", technology);
            return projectRepository.findByTechStackContainingIgnoreCase(technology);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching projects by technology: {}", technology, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching projects by technology: {}", technology, ex);
            throw new DataIntegrityException("Failed to fetch projects by technology", ex);
        }
    }
    
    @Override
    public Optional<Project> findBySlug(String slug) {
        try {
            log.debug("DAO: Fetching project with slug: {}", slug);
            return projectRepository.findBySlug(slug);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching project with slug: {}", slug, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching project with slug: {}", slug, ex);
            throw new DataIntegrityException("Failed to fetch project by slug", ex);
        }
    }
    
    @Override
    public boolean existsBySlug(String slug) {
        try {
            log.debug("DAO: Checking if project exists with slug: {}", slug);
            return projectRepository.existsBySlug(slug);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while checking project existence with slug: {}", slug, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while checking project existence with slug: {}", slug, ex);
            throw new DataIntegrityException("Failed to check project existence by slug", ex);
        }
    }
    
    @Override
    public List<Project> findByTechnologyName(String technologyName) {
        try {
            log.debug("DAO: Fetching projects by technology name: {}", technologyName);
            return projectRepository.findByTechnologyName(technologyName);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching projects by technology name: {}", technologyName, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching projects by technology name: {}", technologyName, ex);
            throw new DataIntegrityException("Failed to fetch projects by technology name", ex);
        }
    }
    
    @Override
    public List<Project> findByTechnologyId(Long technologyId) {
        try {
            log.debug("DAO: Fetching projects by technology id: {}", technologyId);
            return projectRepository.findByTechnologyId(technologyId);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching projects by technology id: {}", technologyId, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching projects by technology id: {}", technologyId, ex);
            throw new DataIntegrityException("Failed to fetch projects by technology id", ex);
        }
    }
    
    @Override
    public List<Project> findPublishedProjectsOrderedByDisplay() {
        try {
            log.debug("DAO: Fetching published projects ordered by display");
            return projectRepository.findByPublishedTrueOrderByDisplayOrderAscCreatedAtDesc();
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching published projects ordered", ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching published projects ordered", ex);
            throw new DataIntegrityException("Failed to fetch published projects ordered", ex);
        }
    }
    
    @Override
    public List<Project> findFeaturedPublishedProjects() {
        try {
            log.debug("DAO: Fetching featured published projects");
            return projectRepository.findByFeaturedTrueAndPublishedTrueOrderByDisplayOrderAsc();
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching featured published projects", ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching featured published projects", ex);
            throw new DataIntegrityException("Failed to fetch featured published projects", ex);
        }
    }
    
    @Override
    public List<Project> findByProjectType(ProjectType projectType) {
        try {
            log.debug("DAO: Fetching projects by type: {}", projectType);
            return projectRepository.findByProjectType(projectType);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching projects by type: {}", projectType, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching projects by type: {}", projectType, ex);
            throw new DataIntegrityException("Failed to fetch projects by type", ex);
        }
    }
    
    @Override
    public List<Project> findByStatus(ProjectStatus status) {
        try {
            log.debug("DAO: Fetching projects by status: {}", status);
            return projectRepository.findByStatus(status);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching projects by status: {}", status, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching projects by status: {}", status, ex);
            throw new DataIntegrityException("Failed to fetch projects by status", ex);
        }
    }
    
    @Override
    public List<Project> findByDifficultyLevel(DifficultyLevel difficultyLevel) {
        try {
            log.debug("DAO: Fetching projects by difficulty level: {}", difficultyLevel);
            return projectRepository.findByDifficultyLevel(difficultyLevel);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching projects by difficulty: {}", difficultyLevel, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching projects by difficulty: {}", difficultyLevel, ex);
            throw new DataIntegrityException("Failed to fetch projects by difficulty level", ex);
        }
    }
    
    @Override
    public List<Project> findMostViewedProjects() {
        try {
            log.debug("DAO: Fetching most viewed projects");
            return projectRepository.findMostViewedProjects();
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching most viewed projects", ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching most viewed projects", ex);
            throw new DataIntegrityException("Failed to fetch most viewed projects", ex);
        }
    }
    
    @Override
    public List<Project> findPublishedByTypeOrderedByDisplay(ProjectType type) {
        try {
            log.debug("DAO: Fetching published projects by type ordered by display: {}", type);
            return projectRepository.findPublishedByTypeOrderByDisplayOrder(type);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching published projects by type ordered: {}", type, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching published projects by type ordered: {}", type, ex);
            throw new DataIntegrityException("Failed to fetch published projects by type ordered", ex);
        }
    }
    
    @Override
    public long countPublishedProjects() {
        try {
            log.debug("DAO: Counting published projects");
            return projectRepository.countPublishedProjects();
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while counting published projects", ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while counting published projects", ex);
            throw new DataIntegrityException("Failed to count published projects", ex);
        }
    }
    
    @Override
    public List<Object[]> countByStatus() {
        try {
            log.debug("DAO: Counting projects by status");
            return projectRepository.countByStatus();
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while counting projects by status", ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while counting projects by status", ex);
            throw new DataIntegrityException("Failed to count projects by status", ex);
        }
    }
    
    @Override
    public List<Object[]> countByType() {
        try {
            log.debug("DAO: Counting projects by type");
            return projectRepository.countByType();
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while counting projects by type", ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while counting projects by type", ex);
            throw new DataIntegrityException("Failed to count projects by type", ex);
        }
    }
    
    @Override
    public void incrementViewCount(Long id) {
        try {
            log.debug("DAO: Incrementing view count for project id: {}", id);
            projectRepository.incrementViewCount(id);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while incrementing view count for id: {}", id, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while incrementing view count for id: {}", id, ex);
            throw new DataIntegrityException("Failed to increment view count", ex);
        }
    }

    @Override
    public long count() {
        try {
            log.debug("DAO: Counting total projects");
            return projectRepository.count();
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while counting projects", ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while counting projects", ex);
            throw new DataIntegrityException("Failed to count projects", ex);
        }
    }
}