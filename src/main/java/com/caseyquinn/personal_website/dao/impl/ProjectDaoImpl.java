package com.caseyquinn.personal_website.dao.impl;

import com.caseyquinn.personal_website.dao.ProjectDao;
import com.caseyquinn.personal_website.entity.Project;
import com.caseyquinn.personal_website.entity.enums.ProjectType;
import com.caseyquinn.personal_website.entity.enums.ProjectStatus;
import com.caseyquinn.personal_website.entity.enums.DifficultyLevel;
import com.caseyquinn.personal_website.exception.NotFoundException;
import com.caseyquinn.personal_website.exception.data.RetryableDataAccess;
import com.caseyquinn.personal_website.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of ProjectDao with automatic retry on transient data access failures.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@RetryableDataAccess
public class ProjectDaoImpl implements ProjectDao {

    private final ProjectRepository projectRepository;

    @Override
    public List<Project> findAll() {
        log.info("DAO: Fetching all projects");
        return projectRepository.findAll();
    }

    @Override
    public Page<Project> findAll(Pageable pageable) {
        log.info("DAO: Fetching projects with pagination: {}", pageable);
        return projectRepository.findAll(pageable);
    }

    @Override
    public Optional<Project> findById(Long id) {
        log.info("DAO: Fetching project with id: {}", id);
        return projectRepository.findById(id);
    }

    @Override
    public Project findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Project", id));
    }

    @Override
    public Optional<Project> findByName(String name) {
        log.info("DAO: Fetching project with name: {}", name);
        return projectRepository.findByName(name);
    }

    @Override
    public boolean existsByName(String name) {
        log.info("DAO: Checking if project exists with name: {}", name);
        return projectRepository.existsByName(name);
    }

    @Override
    public Project save(Project project) {
        log.info("DAO: Saving project: {}", project.getName());
        Project savedProject = projectRepository.save(project);
        log.info("DAO: Successfully saved project with id: {}", savedProject.getId());
        return savedProject;
    }

    @Override
    public void deleteById(Long id) {
        log.info("DAO: Deleting project with id: {}", id);
        if (!projectRepository.existsById(id)) {
            throw new NotFoundException("Project", id);
        }
        projectRepository.deleteById(id);
        log.info("DAO: Successfully deleted project with id: {}", id);
    }

    @Override
    public boolean existsById(Long id) {
        log.info("DAO: Checking if project exists with id: {}", id);
        return projectRepository.existsById(id);
    }

    @Override
    public List<Project> findByTechStackContaining(String technology) {
        log.info("DAO: Fetching projects containing technology: {}", technology);
        return projectRepository.findByTechStackContainingIgnoreCase(technology);
    }

    @Override
    public Optional<Project> findBySlug(String slug) {
        log.info("DAO: Fetching project with slug: {}", slug);
        return projectRepository.findBySlug(slug);
    }

    @Override
    public boolean existsBySlug(String slug) {
        log.info("DAO: Checking if project exists with slug: {}", slug);
        return projectRepository.existsBySlug(slug);
    }

    @Override
    public List<Project> findByTechnologyName(String technologyName) {
        log.info("DAO: Fetching projects by technology name: {}", technologyName);
        return projectRepository.findByTechnologyName(technologyName);
    }

    @Override
    public List<Project> findByTechnologyId(Long technologyId) {
        log.info("DAO: Fetching projects by technology id: {}", technologyId);
        return projectRepository.findByTechnologyId(technologyId);
    }

    @Override
    public List<Project> findPublishedProjectsOrderedByDisplay() {
        log.info("DAO: Fetching published projects ordered by display");
        return projectRepository.findByPublishedTrueOrderByDisplayOrderAscCreatedAtDesc();
    }

    @Override
    public List<Project> findFeaturedPublishedProjects() {
        log.info("DAO: Fetching featured published projects");
        return projectRepository.findByFeaturedTrueAndPublishedTrueOrderByDisplayOrderAsc();
    }

    @Override
    public List<Project> findByProjectType(ProjectType projectType) {
        log.info("DAO: Fetching projects by type: {}", projectType);
        return projectRepository.findByProjectType(projectType);
    }

    @Override
    public List<Project> findByStatus(ProjectStatus status) {
        log.info("DAO: Fetching projects by status: {}", status);
        return projectRepository.findByStatus(status);
    }

    @Override
    public List<Project> findByDifficultyLevel(DifficultyLevel difficultyLevel) {
        log.info("DAO: Fetching projects by difficulty level: {}", difficultyLevel);
        return projectRepository.findByDifficultyLevel(difficultyLevel);
    }

    @Override
    public List<Project> findMostViewedProjects() {
        log.info("DAO: Fetching most viewed projects");
        return projectRepository.findMostViewedProjects();
    }

    @Override
    public List<Project> findPublishedByTypeOrderedByDisplay(ProjectType type) {
        log.info("DAO: Fetching published projects by type ordered by display: {}", type);
        return projectRepository.findPublishedByProjectTypeOrderByDisplayOrder(type);
    }

    @Override
    public long countPublishedProjects() {
        log.info("DAO: Counting published projects");
        return projectRepository.countPublishedProjects();
    }

    @Override
    public List<Object[]> countByStatus() {
        log.info("DAO: Counting projects by status");
        return projectRepository.countByStatus();
    }

    @Override
    public List<Object[]> countByType() {
        log.info("DAO: Counting projects by type");
        return projectRepository.countByProjectType();
    }

    @Override
    public void incrementViewCount(Long id) {
        log.info("DAO: Incrementing view count for project id: {}", id);
        projectRepository.incrementViewCount(id);
    }

    @Override
    public long count() {
        log.info("DAO: Counting total projects");
        return projectRepository.count();
    }
}
