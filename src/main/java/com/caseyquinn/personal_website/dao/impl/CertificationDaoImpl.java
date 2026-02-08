package com.caseyquinn.personal_website.dao.impl;

import com.caseyquinn.personal_website.dao.CertificationDao;
import com.caseyquinn.personal_website.entity.Certification;
import com.caseyquinn.personal_website.entity.enums.CertificationStatus;
import com.caseyquinn.personal_website.exception.NotFoundException;
import com.caseyquinn.personal_website.exception.data.RetryableDataAccess;
import com.caseyquinn.personal_website.repository.CertificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of CertificationDao with automatic retry on transient data access failures.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@RetryableDataAccess
public class CertificationDaoImpl implements CertificationDao {

    private final CertificationRepository certificationRepository;

    @Override
    public List<Certification> findAll() {
        log.info("DAO: Fetching all certifications");
        return certificationRepository.findAll();
    }

    @Override
    public Optional<Certification> findById(Long id) {
        log.info("DAO: Fetching certification with id: {}", id);
        return certificationRepository.findById(id);
    }

    @Override
    public Certification findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Certification", id));
    }

    @Override
    public Optional<Certification> findByName(String name) {
        log.info("DAO: Fetching certification with name: {}", name);
        return certificationRepository.findByName(name);
    }

    @Override
    public Optional<Certification> findBySlug(String slug) {
        log.info("DAO: Fetching certification with slug: {}", slug);
        return certificationRepository.findBySlug(slug);
    }

    @Override
    public boolean existsByName(String name) {
        log.info("DAO: Checking if certification exists with name: {}", name);
        return certificationRepository.existsByName(name);
    }

    @Override
    public boolean existsBySlug(String slug) {
        log.info("DAO: Checking if certification exists with slug: {}", slug);
        return certificationRepository.existsBySlug(slug);
    }

    @Override
    public Certification save(Certification certification) {
        log.info("DAO: Saving certification: {}", certification.getName());
        Certification saved = certificationRepository.save(certification);
        log.info("DAO: Successfully saved certification with id: {}", saved.getId());
        return saved;
    }

    @Override
    public void deleteById(Long id) {
        log.info("DAO: Deleting certification with id: {}", id);
        if (!certificationRepository.existsById(id)) {
            throw new NotFoundException("Certification", id);
        }
        certificationRepository.deleteById(id);
        log.info("DAO: Successfully deleted certification with id: {}", id);
    }

    @Override
    public List<Certification> findByStatus(CertificationStatus status) {
        log.info("DAO: Fetching certifications by status: {}", status);
        return certificationRepository.findByStatus(status);
    }

    @Override
    public List<Certification> findByIssuingOrganization(String issuingOrganization) {
        log.info("DAO: Fetching certifications by issuing organization: {}", issuingOrganization);
        return certificationRepository.findByIssuingOrganization(issuingOrganization);
    }

    @Override
    public List<Certification> findPublishedOrdered() {
        log.info("DAO: Fetching published certifications ordered by display");
        return certificationRepository.findByPublishedTrueOrderByDisplayOrderAscIssueDateDesc();
    }

    @Override
    public List<Certification> findFeaturedPublished() {
        log.info("DAO: Fetching featured published certifications");
        return certificationRepository.findByFeaturedTrueAndPublishedTrueOrderByDisplayOrderAsc();
    }

    @Override
    public List<Certification> findByTechnologyId(Long technologyId) {
        log.info("DAO: Fetching certifications by technology id: {}", technologyId);
        return certificationRepository.findByTechnologyId(technologyId);
    }

    @Override
    public long count() {
        log.info("DAO: Counting total certifications");
        return certificationRepository.count();
    }
}
