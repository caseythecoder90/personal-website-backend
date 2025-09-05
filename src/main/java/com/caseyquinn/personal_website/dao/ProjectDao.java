package com.caseyquinn.personal_website.dao;

import com.caseyquinn.personal_website.entity.Project;
import com.caseyquinn.personal_website.entity.enums.ProjectType;
import com.caseyquinn.personal_website.entity.enums.ProjectStatus;
import com.caseyquinn.personal_website.entity.enums.DifficultyLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProjectDao {
    
    List<Project> findAll();
    
    Page<Project> findAll(Pageable pageable);
    
    Optional<Project> findById(Long id);
    
    Project findByIdOrThrow(Long id);
    
    Optional<Project> findByName(String name);
    
    Optional<Project> findBySlug(String slug);
    
    boolean existsByName(String name);
    
    boolean existsBySlug(String slug);
    
    Project save(Project project);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    // Legacy method for tech stack string - will be deprecated
    List<Project> findByTechStackContaining(String technology);
    
    // New methods for technology relationships
    List<Project> findByTechnologyName(String technologyName);
    
    List<Project> findByTechnologyId(Long technologyId);
    
    // Project filtering and sorting
    List<Project> findPublishedProjectsOrderedByDisplay();
    
    List<Project> findFeaturedPublishedProjects();
    
    List<Project> findByProjectType(ProjectType projectType);
    
    List<Project> findByStatus(ProjectStatus status);
    
    List<Project> findByDifficultyLevel(DifficultyLevel difficultyLevel);
    
    List<Project> findMostViewedProjects();
    
    List<Project> findPublishedByTypeOrderedByDisplay(ProjectType projectType);
    
    // Analytics methods
    long countPublishedProjects();
    
    List<Object[]> countByStatus();
    
    List<Object[]> countByType();
    
    void incrementViewCount(Long id);
    
    long count();
}