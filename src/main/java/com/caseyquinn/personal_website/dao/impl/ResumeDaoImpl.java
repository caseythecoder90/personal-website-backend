package com.caseyquinn.personal_website.dao.impl;

import com.caseyquinn.personal_website.dao.ResumeDao;
import com.caseyquinn.personal_website.entity.Resume;
import com.caseyquinn.personal_website.exception.data.RetryableDataAccess;
import com.caseyquinn.personal_website.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of ResumeDao with automatic retry on transient data access failures.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@RetryableDataAccess
public class ResumeDaoImpl implements ResumeDao {

    private final ResumeRepository resumeRepository;

    @Override
    public Optional<Resume> findActive() {
        log.info("DAO: Fetching active resume");
        return resumeRepository.findByActiveTrue();
    }

    @Override
    public Resume save(Resume resume) {
        log.info("DAO: Saving resume: {}", resume.getFileName());
        Resume saved = resumeRepository.save(resume);
        log.info("DAO: Successfully saved resume with id: {}", saved.getId());
        return saved;
    }

    @Override
    public void deleteById(Long id) {
        log.info("DAO: Deleting resume with id: {}", id);
        resumeRepository.deleteById(id);
        log.info("DAO: Successfully deleted resume with id: {}", id);
    }
}
