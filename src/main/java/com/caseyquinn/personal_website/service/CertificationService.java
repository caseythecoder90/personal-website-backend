package com.caseyquinn.personal_website.service;

import com.caseyquinn.personal_website.dao.CertificationDao;
import com.caseyquinn.personal_website.dao.TechnologyDao;
import com.caseyquinn.personal_website.dto.request.CreateCertificationRequest;
import com.caseyquinn.personal_website.dto.request.UpdateCertificationRequest;
import com.caseyquinn.personal_website.dto.response.CertificationResponse;
import com.caseyquinn.personal_website.entity.Certification;
import com.caseyquinn.personal_website.entity.Technology;
import com.caseyquinn.personal_website.entity.enums.CertificationStatus;
import com.caseyquinn.personal_website.exception.ErrorCode;
import com.caseyquinn.personal_website.exception.NotFoundException;
import com.caseyquinn.personal_website.exception.business.DuplicateResourceException;
import com.caseyquinn.personal_website.exception.business.ValidationException;
import com.caseyquinn.personal_website.mapper.CertificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.caseyquinn.personal_website.exception.ErrorMessages.*;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Service layer for managing professional certifications and their business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CertificationService {

    private final CertificationDao certificationDao;
    private final TechnologyDao technologyDao;
    private final CertificationMapper certificationMapper;

    /**
     * Retrieves all certifications.
     *
     * @return list of all certification responses
     */
    @Cacheable(value = "certifications", key = "'all'")
    public List<CertificationResponse> getAllCertifications() {
        log.info("Service: Fetching all certifications");
        List<Certification> certifications = certificationDao.findAll();
        return certificationMapper.toResponseList(certifications);
    }

    /**
     * Retrieves a specific certification by its ID.
     *
     * @param id the certification ID
     * @return certification response
     */
    @Cacheable(value = "certifications", key = "'id:' + #id")
    public CertificationResponse getCertificationById(Long id) {
        log.info("Service: Fetching certification with id: {}", id);
        Certification certification = certificationDao.findByIdOrThrow(id);
        return certificationMapper.toResponse(certification);
    }

    /**
     * Retrieves a certification by its URL slug.
     *
     * @param slug the certification slug
     * @return certification response
     */
    @Cacheable(value = "certifications", key = "'slug:' + #slug")
    public CertificationResponse getCertificationBySlug(String slug) {
        log.info("Service: Fetching certification with slug: {}", slug);
        Certification certification = certificationDao.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Certification", "slug", slug));
        return certificationMapper.toResponse(certification);
    }

    /**
     * Creates a new certification with validation and default value handling.
     *
     * @param request the certification creation request
     * @return the created certification response
     */
    @CacheEvict(value = "certifications", allEntries = true)
    @Transactional
    public CertificationResponse createCertification(CreateCertificationRequest request) {
        log.info("Service: Creating new certification: {}", request.getName());

        validateCertificationCreation(request);

        Certification certification = certificationMapper.toEntity(request);
        applyDefaults(certification);
        associateTechnologies(certification, request.getTechnologyIds());

        Certification saved = certificationDao.save(certification);
        log.info("Service: Successfully created certification with id: {}", saved.getId());
        return certificationMapper.toResponse(saved);
    }

    /**
     * Updates an existing certification with validation.
     *
     * @param id the certification ID
     * @param request the certification update request
     * @return the updated certification response
     */
    @CacheEvict(value = "certifications", allEntries = true)
    @Transactional
    public CertificationResponse updateCertification(Long id, UpdateCertificationRequest request) {
        log.info("Service: Updating certification with id: {}", id);

        Certification existing = certificationDao.findByIdOrThrow(id);
        validateCertificationUpdate(request, existing);
        updateTechnologyAssociations(existing, request.getTechnologyIds());
        certificationMapper.updateEntityFromRequest(request, existing);

        Certification updated = certificationDao.save(existing);
        log.info("Service: Successfully updated certification with id: {}", id);
        return certificationMapper.toResponse(updated);
    }

    /**
     * Deletes a certification after validating it can be deleted.
     *
     * @param id the certification ID
     */
    @CacheEvict(value = "certifications", allEntries = true)
    @Transactional
    public void deleteCertification(Long id) {
        log.info("Service: Deleting certification with id: {}", id);

        Certification certification = certificationDao.findByIdOrThrow(id);
        validateCertificationDeletion(certification);

        certificationDao.deleteById(id);
        log.info("Service: Successfully deleted certification with id: {}", id);
    }

    /**
     * Retrieves certifications filtered by status.
     *
     * @param status the certification status
     * @return list of matching certification responses
     */
    public List<CertificationResponse> getCertificationsByStatus(CertificationStatus status) {
        log.info("Service: Fetching certifications by status: {}", status);
        List<Certification> certifications = certificationDao.findByStatus(status);
        return certificationMapper.toResponseList(certifications);
    }

    /**
     * Retrieves certifications filtered by issuing organization.
     *
     * @param organization the organization name
     * @return list of matching certification responses
     */
    public List<CertificationResponse> getCertificationsByOrganization(String organization) {
        log.info("Service: Fetching certifications by organization: {}", organization);
        List<Certification> certifications = certificationDao.findByIssuingOrganization(organization);
        return certificationMapper.toResponseList(certifications);
    }

    /**
     * Retrieves published certifications ordered for public display.
     *
     * @return list of published certification responses
     */
    @Cacheable(value = "certifications", key = "'published'")
    public List<CertificationResponse> getPublishedCertifications() {
        log.info("Service: Fetching published certifications");
        List<Certification> certifications = certificationDao.findPublishedOrdered();
        return certificationMapper.toResponseList(certifications);
    }

    /**
     * Retrieves featured certifications that are published.
     *
     * @return list of featured published certification responses
     */
    @Cacheable(value = "certifications", key = "'featured'")
    public List<CertificationResponse> getFeaturedCertifications() {
        log.info("Service: Fetching featured certifications");
        List<Certification> certifications = certificationDao.findFeaturedPublished();
        return certificationMapper.toResponseList(certifications);
    }

    /**
     * Associates a technology with a certification.
     *
     * @param certificationId the certification ID
     * @param technologyId the technology ID
     * @return the updated certification response
     */
    @CacheEvict(value = "certifications", allEntries = true)
    @Transactional
    public CertificationResponse addTechnologyToCertification(Long certificationId, Long technologyId) {
        log.info("Service: Adding technology {} to certification {}", technologyId, certificationId);

        Certification certification = certificationDao.findByIdOrThrow(certificationId);
        Technology technology = technologyDao.findByIdOrThrow(technologyId);

        if (certification.getTechnologies().contains(technology)) {
            throw new ValidationException(ErrorCode.DUPLICATE_CERT_TECH_ASSOCIATION, CERT_TECHNOLOGY_ALREADY_ASSOCIATED);
        }

        certification.addTechnology(technology);
        Certification updated = certificationDao.save(certification);

        log.info("Service: Successfully added technology to certification");
        return certificationMapper.toResponse(updated);
    }

    /**
     * Removes a technology association from a certification.
     *
     * @param certificationId the certification ID
     * @param technologyId the technology ID
     * @return the updated certification response
     */
    @CacheEvict(value = "certifications", allEntries = true)
    @Transactional
    public CertificationResponse removeTechnologyFromCertification(Long certificationId, Long technologyId) {
        log.info("Service: Removing technology {} from certification {}", technologyId, certificationId);

        Certification certification = certificationDao.findByIdOrThrow(certificationId);
        Technology technology = technologyDao.findByIdOrThrow(technologyId);

        if (!certification.getTechnologies().contains(technology)) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, CERT_TECHNOLOGY_NOT_ASSOCIATED);
        }

        certification.removeTechnology(technology);
        Certification updated = certificationDao.save(certification);

        log.info("Service: Successfully removed technology from certification");
        return certificationMapper.toResponse(updated);
    }

    private void validateCertificationCreation(CreateCertificationRequest request) {
        if (certificationDao.existsByName(request.getName())) {
            throw new DuplicateResourceException("Certification", "name", request.getName());
        }
    }

    private void validateCertificationUpdate(UpdateCertificationRequest request, Certification existing) {
        if (isNotBlank(request.getName()) && !existing.getName().equals(request.getName())
                && certificationDao.existsByName(request.getName())) {
            throw new DuplicateResourceException("Certification", "name", request.getName());
        }
    }

    private void validateCertificationDeletion(Certification certification) {
        if (isTrue(certification.getPublished())) {
            throw new ValidationException(ErrorCode.CANNOT_DELETE_PUBLISHED, CANNOT_DELETE_PUBLISHED_CERTIFICATION);
        }
    }

    /**
     * Applies default values to a new certification if not explicitly set.
     *
     * @param certification the certification to apply defaults to
     */
    private void applyDefaults(Certification certification) {
        if (isNull(certification.getPublished())) {
            certification.setPublished(false);
        }
        if (isNull(certification.getFeatured())) {
            certification.setFeatured(false);
        }
        if (isNull(certification.getStatus())) {
            certification.setStatus(CertificationStatus.EARNED);
        }
    }

    /**
     * Associates technologies with a certification by loading and linking each technology entity.
     *
     * @param certification the certification to associate technologies with
     * @param technologyIds the set of technology IDs to associate
     */
    private void associateTechnologies(Certification certification, Set<Long> technologyIds) {
        if (technologyIds != null) {
            for (Long techId : technologyIds) {
                Technology technology = technologyDao.findByIdOrThrow(techId);
                certification.addTechnology(technology);
            }
        }
    }

    /**
     * Updates technology associations for an existing certification.
     *
     * @param certification the certification to update
     * @param technologyIds the new set of technology IDs to associate
     */
    private void updateTechnologyAssociations(Certification certification, Set<Long> technologyIds) {
        if (technologyIds != null) {
            certification.getTechnologies().clear();
            associateTechnologies(certification, technologyIds);
        }
    }
}
