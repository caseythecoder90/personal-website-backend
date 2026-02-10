package com.caseyquinn.personal_website.repository;

import com.caseyquinn.personal_website.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for Resume entities.
 */
@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Optional<Resume> findByActiveTrue();
}
