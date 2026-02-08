package com.caseyquinn.personal_website.dao;

import com.caseyquinn.personal_website.entity.Certification;
import com.caseyquinn.personal_website.entity.enums.CertificationStatus;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Certification operations.
 * Provides abstraction layer over CertificationRepository with exception translation.
 */
public interface CertificationDao {

    /**
     * Retrieves all certifications.
     *
     * @return list of all certifications
     */
    List<Certification> findAll();

    /**
     * Finds a certification by ID.
     *
     * @param id the certification ID
     * @return optional containing the certification if found
     */
    Optional<Certification> findById(Long id);

    /**
     * Finds a certification by ID or throws NotFoundException.
     *
     * @param id the certification ID
     * @return the certification
     */
    Certification findByIdOrThrow(Long id);

    /**
     * Finds a certification by name.
     *
     * @param name the certification name
     * @return optional containing the certification if found
     */
    Optional<Certification> findByName(String name);

    /**
     * Finds a certification by slug.
     *
     * @param slug the certification slug
     * @return optional containing the certification if found
     */
    Optional<Certification> findBySlug(String slug);

    /**
     * Checks if a certification exists with the given name.
     *
     * @param name the certification name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Checks if a certification exists with the given slug.
     *
     * @param slug the certification slug
     * @return true if exists, false otherwise
     */
    boolean existsBySlug(String slug);

    /**
     * Saves or updates a certification.
     *
     * @param certification the certification to save
     * @return the saved certification
     */
    Certification save(Certification certification);

    /**
     * Deletes a certification by ID.
     *
     * @param id the certification ID
     */
    void deleteById(Long id);

    /**
     * Finds certifications by status.
     *
     * @param status the certification status
     * @return list of matching certifications
     */
    List<Certification> findByStatus(CertificationStatus status);

    /**
     * Finds certifications by issuing organization.
     *
     * @param issuingOrganization the organization name
     * @return list of matching certifications
     */
    List<Certification> findByIssuingOrganization(String issuingOrganization);

    /**
     * Finds published certifications ordered by display order then issue date.
     *
     * @return list of published certifications
     */
    List<Certification> findPublishedOrdered();

    /**
     * Finds featured and published certifications.
     *
     * @return list of featured published certifications
     */
    List<Certification> findFeaturedPublished();

    /**
     * Finds certifications associated with a technology by ID.
     *
     * @param technologyId the technology ID
     * @return list of matching certifications
     */
    List<Certification> findByTechnologyId(Long technologyId);

    /**
     * Counts total certifications.
     *
     * @return total certification count
     */
    long count();
}
