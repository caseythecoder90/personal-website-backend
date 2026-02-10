package com.caseyquinn.personal_website.dao;

import com.caseyquinn.personal_website.entity.ContactSubmission;
import com.caseyquinn.personal_website.entity.enums.InquiryType;
import com.caseyquinn.personal_website.entity.enums.SubmissionStatus;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for ContactSubmission operations.
 * Provides abstraction layer over ContactSubmissionRepository with exception translation.
 */
public interface ContactSubmissionDao {

    /**
     * Retrieves all contact submissions ordered by creation date descending.
     *
     * @return list of all submissions
     */
    List<ContactSubmission> findAll();

    /**
     * Finds a contact submission by ID.
     *
     * @param id the submission ID
     * @return optional containing the submission if found
     */
    Optional<ContactSubmission> findById(Long id);

    /**
     * Finds a contact submission by ID or throws NotFoundException.
     *
     * @param id the submission ID
     * @return the submission
     */
    ContactSubmission findByIdOrThrow(Long id);

    /**
     * Finds submissions filtered by status.
     *
     * @param status the submission status
     * @return list of matching submissions
     */
    List<ContactSubmission> findByStatus(SubmissionStatus status);

    /**
     * Finds submissions filtered by inquiry type.
     *
     * @param inquiryType the inquiry type
     * @return list of matching submissions
     */
    List<ContactSubmission> findByInquiryType(InquiryType inquiryType);

    /**
     * Finds submissions by email address.
     *
     * @param email the email address
     * @return list of matching submissions
     */
    List<ContactSubmission> findByEmail(String email);

    /**
     * Saves or updates a contact submission.
     *
     * @param submission the submission to save
     * @return the saved submission
     */
    ContactSubmission save(ContactSubmission submission);

    /**
     * Deletes a contact submission by ID.
     *
     * @param id the submission ID
     */
    void deleteById(Long id);
}
