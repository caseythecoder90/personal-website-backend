package com.caseyquinn.personal_website.service;

import com.caseyquinn.personal_website.dao.TechnologyDao;
import com.caseyquinn.personal_website.dto.request.CreateTechnologyRequest;
import com.caseyquinn.personal_website.dto.request.UpdateTechnologyRequest;
import com.caseyquinn.personal_website.dto.response.TechnologyResponse;
import com.caseyquinn.personal_website.entity.Technology;
import com.caseyquinn.personal_website.entity.enums.TechnologyCategory;
import com.caseyquinn.personal_website.entity.enums.ProficiencyLevel;
import com.caseyquinn.personal_website.exception.ErrorCode;
import com.caseyquinn.personal_website.exception.NotFoundException;
import com.caseyquinn.personal_website.exception.business.DuplicateResourceException;
import com.caseyquinn.personal_website.exception.business.ValidationException;
import com.caseyquinn.personal_website.mapper.TechnologyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TechnologyService {
    
    private final TechnologyDao technologyDao;
    private final TechnologyMapper technologyMapper;
    
    public List<TechnologyResponse> getAllTechnologies() {
        log.info("Service: Fetching all technologies");
        List<Technology> technologies = technologyDao.findAll();
        return technologyMapper.toResponseList(technologies);
    }
    
    public Page<TechnologyResponse> getTechnologiesPaginated(Pageable pageable) {
        log.info("Service: Fetching technologies with pagination: {}", pageable);
        Page<Technology> technologies = technologyDao.findAll(pageable);
        return technologies.map(technologyMapper::toResponse);
    }
    
    public TechnologyResponse getTechnologyById(Long id) {
        log.info("Service: Fetching technology with id: {}", id);
        Technology technology = technologyDao.findByIdOrThrow(id);
        return technologyMapper.toResponse(technology);
    }
    
    public TechnologyResponse getTechnologyByName(String name) {
        log.info("Service: Fetching technology with name: {}", name);
        Technology technology = technologyDao.findByName(name)
                .orElseThrow(() -> new NotFoundException("Technology", "name", name));
        return technologyMapper.toResponse(technology);
    }
    
    @Transactional
    public TechnologyResponse createTechnology(CreateTechnologyRequest request) {
        log.info("Service: Creating new technology: {}", request.getName());
        
        Technology technology = technologyMapper.toEntity(request);
        
        // Business validation
        validateTechnologyCreation(technology);
        
        Technology savedTechnology = technologyDao.save(technology);
        log.info("Service: Successfully created technology with id: {}", savedTechnology.getId());
        return technologyMapper.toResponse(savedTechnology);
    }
    
    @Transactional
    public TechnologyResponse updateTechnology(Long id, UpdateTechnologyRequest request) {
        log.info("Service: Updating technology with id: {}", id);
        
        Technology existingTechnology = technologyDao.findByIdOrThrow(id);
        
        // Create a temporary technology for validation
        Technology tempTechnology = new Technology();
        technologyMapper.updateEntityFromUpdateRequest(request, tempTechnology);
        
        // Business validation
        validateTechnologyUpdate(tempTechnology, existingTechnology);
        
        // Apply updates using mapper
        technologyMapper.updateEntityFromUpdateRequest(request, existingTechnology);
        
        Technology updatedTechnology = technologyDao.save(existingTechnology);
        log.info("Service: Successfully updated technology with id: {}", id);
        return technologyMapper.toResponse(updatedTechnology);
    }
    
    @Transactional
    public void deleteTechnology(Long id) {
        log.info("Service: Deleting technology with id: {}", id);
        
        Technology technology = technologyDao.findByIdOrThrow(id);
        
        // Business logic: Check if technology can be deleted
        validateTechnologyDeletion(technology);
        
        technologyDao.deleteById(id);
        log.info("Service: Successfully deleted technology with id: {}", id);
    }
    
    public List<TechnologyResponse> getTechnologiesByCategory(TechnologyCategory category) {
        log.info("Service: Fetching technologies by category: {}", category);
        List<Technology> technologies = technologyDao.findByCategory(category);
        return technologyMapper.toResponseList(technologies);
    }
    
    public List<TechnologyResponse> getTechnologiesByCategoryOrdered(TechnologyCategory category) {
        log.info("Service: Fetching technologies by category ordered: {}", category);
        List<Technology> technologies = technologyDao.findByCategoryOrderByName(category);
        return technologyMapper.toResponseList(technologies);
    }
    
    public List<TechnologyResponse> getTechnologiesByProficiencyLevel(ProficiencyLevel proficiencyLevel) {
        log.info("Service: Fetching technologies by proficiency level: {}", proficiencyLevel);
        List<Technology> technologies = technologyDao.findByProficiencyLevel(proficiencyLevel);
        return technologyMapper.toResponseList(technologies);
    }
    
    public List<TechnologyResponse> getFeaturedTechnologies() {
        log.info("Service: Fetching featured technologies");
        List<Technology> technologies = technologyDao.findFeaturedTechnologies();
        return technologyMapper.toResponseList(technologies);
    }
    
    public List<TechnologyResponse> getTechnologiesByProficiencyLevels(List<ProficiencyLevel> levels) {
        log.info("Service: Fetching technologies by proficiency levels: {}", levels);
        List<Technology> technologies = technologyDao.findByProficiencyLevelsOrderByName(levels);
        return technologyMapper.toResponseList(technologies);
    }
    
    public List<TechnologyResponse> getTechnologiesByProjectId(Long projectId) {
        log.info("Service: Fetching technologies by project id: {}", projectId);
        List<Technology> technologies = technologyDao.findByProjectId(projectId);
        return technologyMapper.toResponseList(technologies);
    }
    
    public List<TechnologyResponse> getMostUsedTechnologies() {
        log.info("Service: Fetching most used technologies");
        List<Technology> technologies = technologyDao.findMostUsedTechnologies();
        return technologyMapper.toResponseList(technologies);
    }
    
    public List<Object[]> getTechnologyStatsByCategory() {
        log.info("Service: Fetching technology statistics by category");
        return technologyDao.countByCategory();
    }
    
    // Business validation methods
    private void validateTechnologyCreation(Technology technology) {
        if (technologyDao.existsByName(technology.getName())) {
            throw new DuplicateResourceException("Technology", "name", technology.getName());
        }
        validateYearsExperience(technology.getYearsExperience());
    }

    private void validateTechnologyUpdate(Technology technologyUpdate, Technology existingTechnology) {
        if (technologyUpdate.getName() != null && !existingTechnology.getName().equals(technologyUpdate.getName()) &&
            technologyDao.existsByName(technologyUpdate.getName())) {
            throw new DuplicateResourceException("Technology", "name", technologyUpdate.getName());
        }
        validateYearsExperience(technologyUpdate.getYearsExperience());
    }

    private void validateYearsExperience(BigDecimal yearsExperience) {
        if (yearsExperience == null) return;
        if (yearsExperience.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED,
                    "Years of experience cannot be negative");
        }
        if (yearsExperience.compareTo(BigDecimal.valueOf(50)) > 0) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED,
                    "Years of experience seems unrealistic (max 50 years)");
        }
    }

    private void validateTechnologyDeletion(Technology technology) {
        if (!technology.getProjects().isEmpty()) {
            throw new ValidationException(ErrorCode.TECHNOLOGY_IN_USE,
                    "Cannot delete technology '" + technology.getName() +
                    "' because it is used in " + technology.getProjects().size() + " project(s)");
        }
    }
}