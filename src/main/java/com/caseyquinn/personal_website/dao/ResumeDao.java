package com.caseyquinn.personal_website.dao;

import com.caseyquinn.personal_website.entity.Resume;

import java.util.Optional;

/**
 * Data Access Object for Resume operations.
 * Provides abstraction layer over ResumeRepository with exception translation.
 */
public interface ResumeDao {

    /**
     * Finds the currently active resume.
     *
     * @return optional containing the active resume if one exists
     */
    Optional<Resume> findActive();

    /**
     * Saves or updates a resume.
     *
     * @param resume the resume to save
     * @return the saved resume
     */
    Resume save(Resume resume);

    /**
     * Deletes a resume by ID.
     *
     * @param id the resume ID
     */
    void deleteById(Long id);
}
