package com.caseyquinn.personal_website.dao.impl;

import com.caseyquinn.personal_website.dao.TechnologyDao;
import com.caseyquinn.personal_website.entity.Technology;
import com.caseyquinn.personal_website.entity.enums.TechnologyCategory;
import com.caseyquinn.personal_website.entity.enums.ProficiencyLevel;
import com.caseyquinn.personal_website.exception.NotFoundException;
import com.caseyquinn.personal_website.exception.data.RetryableDataAccess;
import com.caseyquinn.personal_website.repository.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of TechnologyDao with automatic retry on transient data access failures.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@RetryableDataAccess
public class TechnologyDaoImpl implements TechnologyDao {

    private final TechnologyRepository technologyRepository;

    @Override
    public List<Technology> findAll() {
        log.info("DAO: Fetching all technologies");
        return technologyRepository.findAll();
    }

    @Override
    public Page<Technology> findAll(Pageable pageable) {
        log.info("DAO: Fetching technologies with pagination: {}", pageable);
        return technologyRepository.findAll(pageable);
    }

    @Override
    public Optional<Technology> findById(Long id) {
        log.info("DAO: Fetching technology with id: {}", id);
        return technologyRepository.findById(id);
    }

    @Override
    public Technology findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Technology", id));
    }

    @Override
    public Optional<Technology> findByName(String name) {
        log.info("DAO: Fetching technology with name: {}", name);
        return technologyRepository.findByName(name);
    }

    @Override
    public boolean existsByName(String name) {
        log.info("DAO: Checking if technology exists with name: {}", name);
        return technologyRepository.existsByName(name);
    }

    @Override
    public Technology save(Technology technology) {
        log.info("DAO: Saving technology: {}", technology.getName());
        Technology savedTechnology = technologyRepository.save(technology);
        log.info("DAO: Successfully saved technology with id: {}", savedTechnology.getId());
        return savedTechnology;
    }

    @Override
    public void deleteById(Long id) {
        log.info("DAO: Deleting technology with id: {}", id);
        if (!technologyRepository.existsById(id)) {
            throw new NotFoundException("Technology", id);
        }
        technologyRepository.deleteById(id);
        log.info("DAO: Successfully deleted technology with id: {}", id);
    }

    @Override
    public boolean existsById(Long id) {
        log.info("DAO: Checking if technology exists with id: {}", id);
        return technologyRepository.existsById(id);
    }

    @Override
    public List<Technology> findByCategory(TechnologyCategory category) {
        log.info("DAO: Fetching technologies by category: {}", category);
        return technologyRepository.findByCategory(category);
    }

    @Override
    public List<Technology> findByProficiencyLevel(ProficiencyLevel proficiencyLevel) {
        log.info("DAO: Fetching technologies by proficiency level: {}", proficiencyLevel);
        return technologyRepository.findByProficiencyLevel(proficiencyLevel);
    }

    @Override
    public List<Technology> findFeaturedTechnologies() {
        log.info("DAO: Fetching featured technologies");
        return technologyRepository.findByFeaturedTrue();
    }

    @Override
    public List<Technology> findByCategoryOrderByName(TechnologyCategory category) {
        log.info("DAO: Fetching technologies by category ordered by name: {}", category);
        return technologyRepository.findByCategoryOrderByNameAsc(category);
    }

    @Override
    public List<Technology> findByProficiencyLevelsOrderByName(List<ProficiencyLevel> levels) {
        log.info("DAO: Fetching technologies by proficiency levels: {}", levels);
        return technologyRepository.findByProficiencyLevelsOrderByName(levels);
    }

    @Override
    public List<Technology> findByProjectId(Long projectId) {
        log.info("DAO: Fetching technologies by project id: {}", projectId);
        return technologyRepository.findByProjectId(projectId);
    }

    @Override
    public List<Technology> findMostUsedTechnologies() {
        log.info("DAO: Fetching most used technologies");
        return technologyRepository.findMostUsedTechnologies();
    }

    @Override
    public List<Object[]> countByCategory() {
        log.info("DAO: Counting technologies by category");
        return technologyRepository.countByCategory();
    }

    @Override
    public long count() {
        log.info("DAO: Counting total technologies");
        return technologyRepository.count();
    }
}
