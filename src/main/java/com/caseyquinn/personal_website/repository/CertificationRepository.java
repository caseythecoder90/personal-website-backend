package com.caseyquinn.personal_website.repository;

import com.caseyquinn.personal_website.entity.Certification;
import com.caseyquinn.personal_website.entity.enums.CertificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {

    Optional<Certification> findByName(String name);

    Optional<Certification> findBySlug(String slug);

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    List<Certification> findByStatus(CertificationStatus status);

    List<Certification> findByIssuingOrganization(String issuingOrganization);

    List<Certification> findByPublishedTrueOrderByDisplayOrderAscIssueDateDesc();

    List<Certification> findByFeaturedTrueAndPublishedTrueOrderByDisplayOrderAsc();

    @Query("SELECT c FROM Certification c JOIN c.technologies t WHERE t.id = :technologyId")
    List<Certification> findByTechnologyId(@Param("technologyId") Long technologyId);

    @Query("SELECT COUNT(c) FROM Certification c WHERE c.published = true")
    long countPublishedCertifications();
}
