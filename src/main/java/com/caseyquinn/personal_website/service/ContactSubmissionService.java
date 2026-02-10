package com.caseyquinn.personal_website.service;

import com.caseyquinn.personal_website.dao.ContactSubmissionDao;
import com.caseyquinn.personal_website.dto.request.ContactSubmissionRequest;
import com.caseyquinn.personal_website.dto.request.UpdateContactStatusRequest;
import com.caseyquinn.personal_website.dto.response.ContactSubmissionResponse;
import com.caseyquinn.personal_website.entity.ContactSubmission;
import com.caseyquinn.personal_website.entity.enums.InquiryType;
import com.caseyquinn.personal_website.entity.enums.SubmissionStatus;
import com.caseyquinn.personal_website.mapper.ContactSubmissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * Service layer for managing contact form submissions and their business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ContactSubmissionService {

    private final ContactSubmissionDao contactSubmissionDao;
    private final ContactSubmissionMapper contactSubmissionMapper;
    private final EmailService emailService;

    /**
     * Submits a new contact form from a visitor.
     *
     * @param request the submission request
     * @param ipAddress the sender's IP address
     * @param userAgent the sender's user agent
     * @return the saved submission response
     */
    @Transactional
    public ContactSubmissionResponse submitContactForm(ContactSubmissionRequest request, String ipAddress, String userAgent) {
        log.info("Service: Processing contact form submission from: {}", request.getEmail());

        ContactSubmission submission = contactSubmissionMapper.toEntity(request);
        submission.setIpAddress(ipAddress);
        submission.setUserAgent(userAgent);

        if (isNull(submission.getInquiryType())) {
            submission.setInquiryType(InquiryType.GENERAL);
        }

        ContactSubmission saved = contactSubmissionDao.save(submission);
        log.info("Service: Contact submission saved with id: {}", saved.getId());

        emailService.sendContactConfirmation(saved);
        emailService.sendNewContactNotification(saved);

        return contactSubmissionMapper.toResponse(saved);
    }

    /**
     * Retrieves all contact submissions for admin review.
     *
     * @return list of all submissions ordered by creation date descending
     */
    public List<ContactSubmissionResponse> getAllSubmissions() {
        log.info("Service: Fetching all contact submissions");
        List<ContactSubmission> submissions = contactSubmissionDao.findAll();
        return contactSubmissionMapper.toResponseList(submissions);
    }

    /**
     * Retrieves a specific contact submission by ID.
     *
     * @param id the submission ID
     * @return the submission response
     */
    public ContactSubmissionResponse getSubmissionById(Long id) {
        log.info("Service: Fetching contact submission with id: {}", id);
        ContactSubmission submission = contactSubmissionDao.findByIdOrThrow(id);
        return contactSubmissionMapper.toResponse(submission);
    }

    /**
     * Retrieves contact submissions filtered by status.
     *
     * @param status the submission status
     * @return list of matching submissions
     */
    public List<ContactSubmissionResponse> getSubmissionsByStatus(SubmissionStatus status) {
        log.info("Service: Fetching contact submissions by status: {}", status);
        List<ContactSubmission> submissions = contactSubmissionDao.findByStatus(status);
        return contactSubmissionMapper.toResponseList(submissions);
    }

    /**
     * Retrieves contact submissions filtered by inquiry type.
     *
     * @param inquiryType the inquiry type
     * @return list of matching submissions
     */
    public List<ContactSubmissionResponse> getSubmissionsByInquiryType(InquiryType inquiryType) {
        log.info("Service: Fetching contact submissions by inquiry type: {}", inquiryType);
        List<ContactSubmission> submissions = contactSubmissionDao.findByInquiryType(inquiryType);
        return contactSubmissionMapper.toResponseList(submissions);
    }

    /**
     * Updates the status of a contact submission.
     * Sets respondedAt timestamp when status changes to REPLIED.
     *
     * @param id the submission ID
     * @param request the status update request
     * @return the updated submission response
     */
    @Transactional
    public ContactSubmissionResponse updateSubmissionStatus(Long id, UpdateContactStatusRequest request) {
        log.info("Service: Updating contact submission {} status to: {}", id, request.getStatus());

        ContactSubmission submission = contactSubmissionDao.findByIdOrThrow(id);
        submission.setStatus(request.getStatus());

        if (request.getStatus() == SubmissionStatus.REPLIED && isNull(submission.getRespondedAt())) {
            submission.setRespondedAt(LocalDateTime.now());
        }

        ContactSubmission updated = contactSubmissionDao.save(submission);
        log.info("Service: Successfully updated contact submission {} status", id);
        return contactSubmissionMapper.toResponse(updated);
    }

    /**
     * Deletes a contact submission.
     *
     * @param id the submission ID
     */
    @Transactional
    public void deleteSubmission(Long id) {
        log.info("Service: Deleting contact submission with id: {}", id);
        contactSubmissionDao.findByIdOrThrow(id);
        contactSubmissionDao.deleteById(id);
        log.info("Service: Successfully deleted contact submission with id: {}", id);
    }
}
