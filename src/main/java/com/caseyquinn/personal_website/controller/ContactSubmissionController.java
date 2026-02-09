package com.caseyquinn.personal_website.controller;

import com.caseyquinn.personal_website.annotations.ContactSubmissionApiResponses;
import com.caseyquinn.personal_website.dto.request.ContactSubmissionRequest;
import com.caseyquinn.personal_website.dto.request.UpdateContactStatusRequest;
import com.caseyquinn.personal_website.dto.response.ContactSubmissionResponse;
import com.caseyquinn.personal_website.dto.response.Response;
import com.caseyquinn.personal_website.entity.enums.InquiryType;
import com.caseyquinn.personal_website.entity.enums.SubmissionStatus;
import com.caseyquinn.personal_website.service.ContactSubmissionService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing contact form submissions.
 */
@RestController
@RequestMapping("/api/v1/contact")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Contact", description = "Contact form submission and management APIs")
public class ContactSubmissionController {

    private final ContactSubmissionService contactSubmissionService;

    /**
     * Submits a new contact form (public endpoint).
     *
     * @param request the contact form submission
     * @param httpRequest the HTTP request for IP and user agent extraction
     * @return response entity containing the saved submission with HTTP 201 status
     */
    @ContactSubmissionApiResponses.Submit
    @PostMapping
    public ResponseEntity<Response<ContactSubmissionResponse>> submitContactForm(
            @Valid @RequestBody ContactSubmissionRequest request,
            HttpServletRequest httpRequest) {
        log.info("Received contact form submission from: {}", request.getEmail());

        String ipAddress = extractIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        ContactSubmissionResponse submission = contactSubmissionService.submitContactForm(request, ipAddress, userAgent);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success(submission, "Contact form submitted successfully"));
    }

    /**
     * Retrieves all contact submissions (admin only).
     *
     * @return response entity containing list of all submissions
     */
    @ContactSubmissionApiResponses.GetAll
    @GetMapping
    public ResponseEntity<Response<List<ContactSubmissionResponse>>> getAllSubmissions() {
        log.info("Fetching all contact submissions");
        List<ContactSubmissionResponse> submissions = contactSubmissionService.getAllSubmissions();
        return ResponseEntity.ok(Response.success(submissions, "Submissions retrieved successfully"));
    }

    /**
     * Retrieves a specific contact submission by ID (admin only).
     *
     * @param id the submission ID
     * @return response entity containing the submission
     */
    @ContactSubmissionApiResponses.GetById
    @GetMapping("/{id}")
    public ResponseEntity<Response<ContactSubmissionResponse>> getSubmission(
            @Parameter(description = "Submission ID") @PathVariable Long id) {
        log.info("Fetching contact submission with id: {}", id);
        ContactSubmissionResponse submission = contactSubmissionService.getSubmissionById(id);
        return ResponseEntity.ok(Response.success(submission, "Submission retrieved successfully"));
    }

    /**
     * Retrieves contact submissions filtered by status (admin only).
     *
     * @param status the submission status
     * @return response entity containing list of matching submissions
     */
    @ContactSubmissionApiResponses.GetByStatus
    @GetMapping("/status/{status}")
    public ResponseEntity<Response<List<ContactSubmissionResponse>>> getSubmissionsByStatus(
            @Parameter(description = "Submission status") @PathVariable SubmissionStatus status) {
        log.info("Fetching contact submissions by status: {}", status);
        List<ContactSubmissionResponse> submissions = contactSubmissionService.getSubmissionsByStatus(status);
        return ResponseEntity.ok(Response.success(submissions, "Submissions retrieved successfully"));
    }

    /**
     * Retrieves contact submissions filtered by inquiry type (admin only).
     *
     * @param type the inquiry type
     * @return response entity containing list of matching submissions
     */
    @ContactSubmissionApiResponses.GetByInquiryType
    @GetMapping("/inquiry-type/{type}")
    public ResponseEntity<Response<List<ContactSubmissionResponse>>> getSubmissionsByInquiryType(
            @Parameter(description = "Inquiry type") @PathVariable InquiryType type) {
        log.info("Fetching contact submissions by inquiry type: {}", type);
        List<ContactSubmissionResponse> submissions = contactSubmissionService.getSubmissionsByInquiryType(type);
        return ResponseEntity.ok(Response.success(submissions, "Submissions retrieved successfully"));
    }

    /**
     * Updates the status of a contact submission (admin only).
     *
     * @param id the submission ID
     * @param request the status update request
     * @return response entity containing the updated submission
     */
    @ContactSubmissionApiResponses.UpdateStatus
    @PutMapping("/{id}/status")
    public ResponseEntity<Response<ContactSubmissionResponse>> updateSubmissionStatus(
            @Parameter(description = "Submission ID") @PathVariable Long id,
            @Valid @RequestBody UpdateContactStatusRequest request) {
        log.info("Updating contact submission {} status to: {}", id, request.getStatus());
        ContactSubmissionResponse submission = contactSubmissionService.updateSubmissionStatus(id, request);
        return ResponseEntity.ok(Response.success(submission, "Submission status updated successfully"));
    }

    /**
     * Deletes a contact submission (admin only).
     *
     * @param id the submission ID
     * @return response entity with success message
     */
    @ContactSubmissionApiResponses.Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteSubmission(
            @Parameter(description = "Submission ID") @PathVariable Long id) {
        log.info("Deleting contact submission with id: {}", id);
        contactSubmissionService.deleteSubmission(id);
        return ResponseEntity.ok(Response.success(null, "Submission deleted successfully"));
    }

    private String extractIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
