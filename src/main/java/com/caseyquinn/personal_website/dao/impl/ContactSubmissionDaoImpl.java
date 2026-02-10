package com.caseyquinn.personal_website.dao.impl;

import com.caseyquinn.personal_website.dao.ContactSubmissionDao;
import com.caseyquinn.personal_website.entity.ContactSubmission;
import com.caseyquinn.personal_website.entity.enums.InquiryType;
import com.caseyquinn.personal_website.entity.enums.SubmissionStatus;
import com.caseyquinn.personal_website.exception.NotFoundException;
import com.caseyquinn.personal_website.exception.data.RetryableDataAccess;
import com.caseyquinn.personal_website.repository.ContactSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of ContactSubmissionDao with automatic retry on transient data access failures.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@RetryableDataAccess
public class ContactSubmissionDaoImpl implements ContactSubmissionDao {

    private final ContactSubmissionRepository contactSubmissionRepository;

    @Override
    public List<ContactSubmission> findAll() {
        log.info("DAO: Fetching all contact submissions");
        return contactSubmissionRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public Optional<ContactSubmission> findById(Long id) {
        log.info("DAO: Fetching contact submission with id: {}", id);
        return contactSubmissionRepository.findById(id);
    }

    @Override
    public ContactSubmission findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("ContactSubmission", id));
    }

    @Override
    public List<ContactSubmission> findByStatus(SubmissionStatus status) {
        log.info("DAO: Fetching contact submissions by status: {}", status);
        return contactSubmissionRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Override
    public List<ContactSubmission> findByInquiryType(InquiryType inquiryType) {
        log.info("DAO: Fetching contact submissions by inquiry type: {}", inquiryType);
        return contactSubmissionRepository.findByInquiryTypeOrderByCreatedAtDesc(inquiryType);
    }

    @Override
    public List<ContactSubmission> findByEmail(String email) {
        log.info("DAO: Fetching contact submissions by email: {}", email);
        return contactSubmissionRepository.findByEmailOrderByCreatedAtDesc(email);
    }

    @Override
    public ContactSubmission save(ContactSubmission submission) {
        log.info("DAO: Saving contact submission from: {}", submission.getEmail());
        ContactSubmission saved = contactSubmissionRepository.save(submission);
        log.info("DAO: Successfully saved contact submission with id: {}", saved.getId());
        return saved;
    }

    @Override
    public void deleteById(Long id) {
        log.info("DAO: Deleting contact submission with id: {}", id);
        if (!contactSubmissionRepository.existsById(id)) {
            throw new NotFoundException("ContactSubmission", id);
        }
        contactSubmissionRepository.deleteById(id);
        log.info("DAO: Successfully deleted contact submission with id: {}", id);
    }
}
