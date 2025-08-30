package com.caseyquinn.personal_website.dao;

import com.caseyquinn.personal_website.entity.Technology;
import com.caseyquinn.personal_website.entity.enums.TechnologyCategory;
import com.caseyquinn.personal_website.entity.enums.ProficiencyLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TechnologyDao {
    
    List<Technology> findAll();
    
    Page<Technology> findAll(Pageable pageable);
    
    Optional<Technology> findById(Long id);
    
    Technology findByIdOrThrow(Long id);
    
    Optional<Technology> findByName(String name);
    
    boolean existsByName(String name);
    
    Technology save(Technology technology);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    List<Technology> findByCategory(TechnologyCategory category);
    
    List<Technology> findByProficiencyLevel(ProficiencyLevel proficiencyLevel);
    
    List<Technology> findFeaturedTechnologies();
    
    List<Technology> findByCategoryOrderByName(TechnologyCategory category);
    
    List<Technology> findByProficiencyLevelsOrderByName(List<ProficiencyLevel> levels);
    
    List<Technology> findByProjectId(Long projectId);
    
    List<Technology> findMostUsedTechnologies();
    
    List<Object[]> countByCategory();
    
    long count();
}