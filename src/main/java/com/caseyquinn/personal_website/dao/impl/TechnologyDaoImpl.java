package com.caseyquinn.personal_website.dao.impl;

import com.caseyquinn.personal_website.dao.TechnologyDao;
import com.caseyquinn.personal_website.entity.Technology;
import com.caseyquinn.personal_website.entity.enums.TechnologyCategory;
import com.caseyquinn.personal_website.entity.enums.ProficiencyLevel;
import com.caseyquinn.personal_website.exception.data.DatabaseConnectionException;
import com.caseyquinn.personal_website.exception.data.DataIntegrityException;
import com.caseyquinn.personal_website.exception.NotFoundException;
import com.caseyquinn.personal_website.repository.TechnologyRepository;
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
public class TechnologyDaoImpl implements TechnologyDao {
    
    private final TechnologyRepository technologyRepository;
    
    @Override
    public List<Technology> findAll() {
        try {
            log.debug("DAO: Fetching all technologies");
            return technologyRepository.findAll();
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching all technologies", ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching all technologies", ex);
            throw new DataIntegrityException("Failed to fetch technologies", ex);
        }
    }
    
    @Override
    public Page<Technology> findAll(Pageable pageable) {
        try {
            log.debug("DAO: Fetching technologies with pagination: {}", pageable);
            return technologyRepository.findAll(pageable);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching paginated technologies", ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching paginated technologies", ex);
            throw new DataIntegrityException("Failed to fetch paginated technologies", ex);
        }
    }
    
    @Override
    public Optional<Technology> findById(Long id) {
        try {
            log.debug("DAO: Fetching technology with id: {}", id);
            return technologyRepository.findById(id);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching technology with id: {}", id, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching technology with id: {}", id, ex);
            throw new DataIntegrityException("Failed to fetch technology", ex);
        }
    }
    
    @Override
    public Technology findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Technology", id));
    }
    
    @Override
    public Optional<Technology> findByName(String name) {
        try {
            log.debug("DAO: Fetching technology with name: {}", name);
            return technologyRepository.findByName(name);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching technology with name: {}", name, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching technology with name: {}", name, ex);
            throw new DataIntegrityException("Failed to fetch technology by name", ex);
        }
    }
    
    @Override
    public boolean existsByName(String name) {
        try {
            log.debug("DAO: Checking if technology exists with name: {}", name);
            return technologyRepository.existsByName(name);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while checking technology existence with name: {}", name, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while checking technology existence with name: {}", name, ex);
            throw new DataIntegrityException("Failed to check technology existence", ex);
        }
    }
    
    @Override
    public Technology save(Technology technology) {
        try {
            log.debug("DAO: Saving technology: {}", technology.getName());
            Technology savedTechnology = technologyRepository.save(technology);
            log.debug("DAO: Successfully saved technology with id: {}", savedTechnology.getId());
            return savedTechnology;
        } catch (DataIntegrityViolationException ex) {
            log.error("Data integrity violation while saving technology: {}", technology.getName(), ex);
            throw new DataIntegrityException("Data integrity violation while saving technology", ex);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while saving technology: {}", technology.getName(), ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while saving technology: {}", technology.getName(), ex);
            throw new DataIntegrityException("Failed to save technology", ex);
        }
    }
    
    @Override
    public void deleteById(Long id) {
        try {
            log.debug("DAO: Deleting technology with id: {}", id);
            if (!technologyRepository.existsById(id)) {
                throw new NotFoundException("Technology", id);
            }
            technologyRepository.deleteById(id);
            log.debug("DAO: Successfully deleted technology with id: {}", id);
        } catch (NotFoundException ex) {
            throw ex; // Re-throw our custom exception
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while deleting technology with id: {}", id, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while deleting technology with id: {}", id, ex);
            throw new DataIntegrityException("Failed to delete technology", ex);
        }
    }
    
    @Override
    public boolean existsById(Long id) {
        try {
            log.debug("DAO: Checking if technology exists with id: {}", id);
            return technologyRepository.existsById(id);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while checking technology existence with id: {}", id, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while checking technology existence with id: {}", id, ex);
            throw new DataIntegrityException("Failed to check technology existence", ex);
        }
    }
    
    @Override
    public List<Technology> findByCategory(TechnologyCategory category) {
        try {
            log.debug("DAO: Fetching technologies by category: {}", category);
            return technologyRepository.findByCategory(category);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching technologies by category: {}", category, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching technologies by category: {}", category, ex);
            throw new DataIntegrityException("Failed to fetch technologies by category", ex);
        }
    }
    
    @Override
    public List<Technology> findByProficiencyLevel(ProficiencyLevel proficiencyLevel) {
        try {
            log.debug("DAO: Fetching technologies by proficiency level: {}", proficiencyLevel);
            return technologyRepository.findByProficiencyLevel(proficiencyLevel);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching technologies by proficiency: {}", proficiencyLevel, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching technologies by proficiency: {}", proficiencyLevel, ex);
            throw new DataIntegrityException("Failed to fetch technologies by proficiency level", ex);
        }
    }
    
    @Override
    public List<Technology> findFeaturedTechnologies() {
        try {
            log.debug("DAO: Fetching featured technologies");
            return technologyRepository.findByFeaturedTrue();
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching featured technologies", ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching featured technologies", ex);
            throw new DataIntegrityException("Failed to fetch featured technologies", ex);
        }
    }
    
    @Override
    public List<Technology> findByCategoryOrderByName(TechnologyCategory category) {
        try {
            log.debug("DAO: Fetching technologies by category ordered by name: {}", category);
            return technologyRepository.findByCategoryOrderByNameAsc(category);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching technologies by category ordered: {}", category, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching technologies by category ordered: {}", category, ex);
            throw new DataIntegrityException("Failed to fetch technologies by category ordered", ex);
        }
    }
    
    @Override
    public List<Technology> findByProficiencyLevelsOrderByName(List<ProficiencyLevel> levels) {
        try {
            log.debug("DAO: Fetching technologies by proficiency levels: {}", levels);
            return technologyRepository.findByProficiencyLevelsOrderByName(levels);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching technologies by proficiency levels: {}", levels, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching technologies by proficiency levels: {}", levels, ex);
            throw new DataIntegrityException("Failed to fetch technologies by proficiency levels", ex);
        }
    }
    
    @Override
    public List<Technology> findByProjectId(Long projectId) {
        try {
            log.debug("DAO: Fetching technologies by project id: {}", projectId);
            return technologyRepository.findByProjectId(projectId);
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching technologies by project id: {}", projectId, ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching technologies by project id: {}", projectId, ex);
            throw new DataIntegrityException("Failed to fetch technologies by project id", ex);
        }
    }
    
    @Override
    public List<Technology> findMostUsedTechnologies() {
        try {
            log.debug("DAO: Fetching most used technologies");
            return technologyRepository.findMostUsedTechnologies();
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while fetching most used technologies", ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching most used technologies", ex);
            throw new DataIntegrityException("Failed to fetch most used technologies", ex);
        }
    }
    
    @Override
    public List<Object[]> countByCategory() {
        try {
            log.debug("DAO: Counting technologies by category");
            return technologyRepository.countByCategory();
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while counting technologies by category", ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while counting technologies by category", ex);
            throw new DataIntegrityException("Failed to count technologies by category", ex);
        }
    }
    
    @Override
    public long count() {
        try {
            log.debug("DAO: Counting total technologies");
            return technologyRepository.count();
        } catch (DataAccessResourceFailureException ex) {
            log.error("Database connection failed while counting technologies", ex);
            throw new DatabaseConnectionException(ex);
        } catch (Exception ex) {
            log.error("Unexpected error while counting technologies", ex);
            throw new DataIntegrityException("Failed to count technologies", ex);
        }
    }
}