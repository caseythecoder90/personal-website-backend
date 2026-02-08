package com.caseyquinn.personal_website.repository;

import com.caseyquinn.personal_website.entity.Project;
import com.caseyquinn.personal_website.entity.enums.ProjectType;
import com.caseyquinn.personal_website.entity.enums.ProjectStatus;
import com.caseyquinn.personal_website.entity.enums.DifficultyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    Optional<Project> findByName(String name);
    
    Optional<Project> findBySlug(String slug);
    
    boolean existsByName(String name);
    
    boolean existsBySlug(String slug);

    // Technology relationship queries
    @Query("SELECT p FROM Project p JOIN p.technologies t WHERE t.name = :technologyName")
    List<Project> findByTechnologyName(@Param("technologyName") String technologyName);
    
    @Query("SELECT p FROM Project p JOIN p.technologies t WHERE t.id = :technologyId")
    List<Project> findByTechnologyId(@Param("technologyId") Long technologyId);
    
    // Project filtering and sorting
    List<Project> findByPublishedTrueOrderByDisplayOrderAscCreatedAtDesc();
    
    List<Project> findByFeaturedTrueAndPublishedTrueOrderByDisplayOrderAsc();
    
    List<Project> findByProjectType(ProjectType projectType);
    
    List<Project> findByStatus(ProjectStatus status);
    
    List<Project> findByDifficultyLevel(DifficultyLevel difficultyLevel);
    
    @Query("SELECT p FROM Project p WHERE p.published = true ORDER BY p.viewCount DESC")
    List<Project> findMostViewedProjects();
    
    @Query("SELECT p FROM Project p ORDER BY p.createdAt DESC")
    List<Project> findAllOrderByCreatedAtDesc();
    
    @Query("SELECT p FROM Project p WHERE p.projectType = :projectType AND p.published = true ORDER BY p.displayOrder ASC, p.createdAt DESC")
    List<Project> findPublishedByProjectTypeOrderByDisplayOrder(@Param("projectType") ProjectType projectType);
    
    // Analytics queries
    @Query("SELECT COUNT(p) FROM Project p WHERE p.published = true")
    long countPublishedProjects();
    
    @Query("SELECT p.status, COUNT(p) FROM Project p GROUP BY p.status")
    List<Object[]> countByStatus();
    
    @Query("SELECT p.projectType, COUNT(p) FROM Project p GROUP BY p.projectType")
    List<Object[]> countByProjectType();
    
    // Update view count
    @Modifying
    @Query("UPDATE Project p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);
}