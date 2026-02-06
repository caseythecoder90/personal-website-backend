package com.caseyquinn.personal_website.dao;

import com.caseyquinn.personal_website.entity.Technology;
import com.caseyquinn.personal_website.entity.enums.TechnologyCategory;
import com.caseyquinn.personal_website.entity.enums.ProficiencyLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Technology operations.
 * Provides abstraction layer over TechnologyRepository with exception translation.
 */
public interface TechnologyDao {

    /**
     * Retrieves all technologies.
     *
     * @return list of all technologies
     */
    List<Technology> findAll();

    /**
     * Retrieves technologies with pagination.
     *
     * @param pageable pagination parameters
     * @return paginated technologies
     */
    Page<Technology> findAll(Pageable pageable);

    /**
     * Finds a technology by ID.
     *
     * @param id the technology ID
     * @return optional containing the technology if found
     */
    Optional<Technology> findById(Long id);

    /**
     * Finds a technology by ID or throws NotFoundException.
     *
     * @param id the technology ID
     * @return the technology
     */
    Technology findByIdOrThrow(Long id);

    /**
     * Finds a technology by name.
     *
     * @param name the technology name
     * @return optional containing the technology if found
     */
    Optional<Technology> findByName(String name);

    /**
     * Checks if a technology exists with the given name.
     *
     * @param name the technology name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Saves or updates a technology.
     *
     * @param technology the technology to save
     * @return the saved technology
     */
    Technology save(Technology technology);

    /**
     * Deletes a technology by ID.
     *
     * @param id the technology ID
     */
    void deleteById(Long id);

    /**
     * Checks if a technology exists with the given ID.
     *
     * @param id the technology ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Finds technologies by category.
     *
     * @param category the technology category
     * @return list of matching technologies
     */
    List<Technology> findByCategory(TechnologyCategory category);

    /**
     * Finds technologies by proficiency level.
     *
     * @param proficiencyLevel the proficiency level
     * @return list of matching technologies
     */
    List<Technology> findByProficiencyLevel(ProficiencyLevel proficiencyLevel);

    /**
     * Finds featured technologies.
     *
     * @return list of featured technologies
     */
    List<Technology> findFeaturedTechnologies();

    /**
     * Finds technologies by category ordered by name.
     *
     * @param category the technology category
     * @return list of matching technologies ordered by name
     */
    List<Technology> findByCategoryOrderByName(TechnologyCategory category);

    /**
     * Finds technologies matching any of the given proficiency levels.
     *
     * @param levels list of proficiency levels
     * @return list of matching technologies ordered by name
     */
    List<Technology> findByProficiencyLevelsOrderByName(List<ProficiencyLevel> levels);

    /**
     * Finds technologies associated with a project.
     *
     * @param projectId the project ID
     * @return list of associated technologies
     */
    List<Technology> findByProjectId(Long projectId);

    /**
     * Finds technologies sorted by usage count in descending order.
     *
     * @return list of most used technologies
     */
    List<Technology> findMostUsedTechnologies();

    /**
     * Counts technologies grouped by category.
     *
     * @return list of category and count pairs
     */
    List<Object[]> countByCategory();

    /**
     * Counts total technologies.
     *
     * @return total technology count
     */
    long count();
}