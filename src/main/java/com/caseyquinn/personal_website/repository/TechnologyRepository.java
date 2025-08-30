package com.caseyquinn.personal_website.repository;

import com.caseyquinn.personal_website.entity.Technology;
import com.caseyquinn.personal_website.entity.enums.TechnologyCategory;
import com.caseyquinn.personal_website.entity.enums.ProficiencyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnologyRepository extends JpaRepository<Technology, Long> {
    
    Optional<Technology> findByName(String name);
    
    boolean existsByName(String name);
    
    List<Technology> findByCategory(TechnologyCategory category);
    
    List<Technology> findByProficiencyLevel(ProficiencyLevel proficiencyLevel);
    
    List<Technology> findByFeaturedTrue();
    
    List<Technology> findByCategoryOrderByNameAsc(TechnologyCategory category);
    
    @Query("SELECT t FROM Technology t WHERE t.proficiencyLevel IN :levels ORDER BY t.name ASC")
    List<Technology> findByProficiencyLevelsOrderByName(@Param("levels") List<ProficiencyLevel> levels);
    
    @Query("SELECT t FROM Technology t JOIN t.projects p WHERE p.id = :projectId")
    List<Technology> findByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT t FROM Technology t WHERE SIZE(t.projects) > 0 ORDER BY SIZE(t.projects) DESC")
    List<Technology> findMostUsedTechnologies();
    
    @Query("SELECT t.category, COUNT(t) FROM Technology t GROUP BY t.category")
    List<Object[]> countByCategory();
}