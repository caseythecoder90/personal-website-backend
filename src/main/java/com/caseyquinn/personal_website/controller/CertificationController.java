package com.caseyquinn.personal_website.controller;

import com.caseyquinn.personal_website.annotations.CertificationApiResponses;
import com.caseyquinn.personal_website.dto.request.CreateCertificationRequest;
import com.caseyquinn.personal_website.dto.request.UpdateCertificationRequest;
import com.caseyquinn.personal_website.dto.response.CertificationResponse;
import com.caseyquinn.personal_website.dto.response.Response;
import com.caseyquinn.personal_website.entity.enums.CertificationStatus;
import com.caseyquinn.personal_website.service.CertificationService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * REST controller for managing professional certifications.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Certifications", description = "Certification management APIs")
public class CertificationController {

    private final CertificationService certificationService;

    /**
     * Retrieves all certifications.
     *
     * @return response entity containing list of all certifications
     */
    @CertificationApiResponses.GetAll
    @GetMapping("/certifications")
    public ResponseEntity<Response<List<CertificationResponse>>> getAllCertifications() {
        log.info("Fetching all certifications");
        List<CertificationResponse> certifications = certificationService.getAllCertifications();
        return ResponseEntity.ok(Response.success(certifications, "Certifications retrieved successfully"));
    }

    /**
     * Retrieves a specific certification by its ID.
     *
     * @param id the certification ID
     * @return response entity containing the certification
     */
    @CertificationApiResponses.GetById
    @GetMapping("/certifications/{id}")
    public ResponseEntity<Response<CertificationResponse>> getCertification(
            @Parameter(description = "Certification ID") @PathVariable Long id) {
        log.info("Fetching certification with id: {}", id);
        CertificationResponse certification = certificationService.getCertificationById(id);
        return ResponseEntity.ok(Response.success(certification, "Certification retrieved successfully"));
    }

    /**
     * Retrieves a certification by its URL slug.
     *
     * @param slug the certification slug
     * @return response entity containing the certification
     */
    @CertificationApiResponses.GetBySlug
    @GetMapping("/certifications/slug/{slug}")
    public ResponseEntity<Response<CertificationResponse>> getCertificationBySlug(
            @Parameter(description = "Certification slug") @PathVariable String slug) {
        log.info("Fetching certification with slug: {}", slug);
        CertificationResponse certification = certificationService.getCertificationBySlug(slug);
        return ResponseEntity.ok(Response.success(certification, "Certification retrieved successfully"));
    }

    /**
     * Creates a new certification.
     *
     * @param request the certification creation request
     * @return response entity containing the created certification with HTTP 201 status
     */
    @CertificationApiResponses.Create
    @PostMapping("/certifications")
    public ResponseEntity<Response<CertificationResponse>> createCertification(
            @Valid @RequestBody CreateCertificationRequest request) {
        log.info("Creating new certification: {}", request.getName());
        CertificationResponse certification = certificationService.createCertification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success(certification, "Certification created successfully"));
    }

    /**
     * Updates an existing certification.
     *
     * @param id the certification ID
     * @param request the certification update request
     * @return response entity containing the updated certification
     */
    @CertificationApiResponses.Update
    @PutMapping("/certifications/{id}")
    public ResponseEntity<Response<CertificationResponse>> updateCertification(
            @Parameter(description = "Certification ID") @PathVariable Long id,
            @Valid @RequestBody UpdateCertificationRequest request) {
        log.info("Updating certification with id: {}", id);
        CertificationResponse certification = certificationService.updateCertification(id, request);
        return ResponseEntity.ok(Response.success(certification, "Certification updated successfully"));
    }

    /**
     * Deletes a certification by its ID.
     *
     * @param id the certification ID
     * @return response entity with success message
     */
    @CertificationApiResponses.Delete
    @DeleteMapping("/certifications/{id}")
    public ResponseEntity<Response<Void>> deleteCertification(
            @Parameter(description = "Certification ID") @PathVariable Long id) {
        log.info("Deleting certification with id: {}", id);
        certificationService.deleteCertification(id);
        return ResponseEntity.ok(Response.success(null, "Certification deleted successfully"));
    }

    /**
     * Retrieves certifications filtered by status.
     *
     * @param status the certification status
     * @return response entity containing list of matching certifications
     */
    @CertificationApiResponses.GetByStatus
    @GetMapping("/certifications/status/{status}")
    public ResponseEntity<Response<List<CertificationResponse>>> getCertificationsByStatus(
            @Parameter(description = "Certification status") @PathVariable CertificationStatus status) {
        log.info("Fetching certifications by status: {}", status);
        List<CertificationResponse> certifications = certificationService.getCertificationsByStatus(status);
        return ResponseEntity.ok(Response.success(certifications, "Certifications retrieved successfully"));
    }

    /**
     * Retrieves certifications filtered by issuing organization.
     *
     * @param organization the organization name
     * @return response entity containing list of matching certifications
     */
    @CertificationApiResponses.GetByOrganization
    @GetMapping("/certifications/organization/{organization}")
    public ResponseEntity<Response<List<CertificationResponse>>> getCertificationsByOrganization(
            @Parameter(description = "Issuing organization name") @PathVariable String organization) {
        log.info("Fetching certifications by organization: {}", organization);
        List<CertificationResponse> certifications = certificationService.getCertificationsByOrganization(organization);
        return ResponseEntity.ok(Response.success(certifications, "Certifications retrieved successfully"));
    }

    /**
     * Retrieves published certifications for public display.
     *
     * @return response entity containing list of published certifications
     */
    @CertificationApiResponses.GetPublished
    @GetMapping("/certifications/published")
    public ResponseEntity<Response<List<CertificationResponse>>> getPublishedCertifications() {
        log.info("Fetching published certifications");
        List<CertificationResponse> certifications = certificationService.getPublishedCertifications();
        return ResponseEntity.ok(Response.success(certifications, "Published certifications retrieved successfully"));
    }

    /**
     * Retrieves featured certifications that are published.
     *
     * @return response entity containing list of featured certifications
     */
    @CertificationApiResponses.GetFeatured
    @GetMapping("/certifications/featured")
    public ResponseEntity<Response<List<CertificationResponse>>> getFeaturedCertifications() {
        log.info("Fetching featured certifications");
        List<CertificationResponse> certifications = certificationService.getFeaturedCertifications();
        return ResponseEntity.ok(Response.success(certifications, "Featured certifications retrieved successfully"));
    }

    /**
     * Associates a technology with a certification.
     *
     * @param id the certification ID
     * @param technologyId the technology ID
     * @return response entity containing the updated certification
     */
    @CertificationApiResponses.AddTechnology
    @PostMapping("/certifications/{id}/technologies/{technologyId}")
    public ResponseEntity<Response<CertificationResponse>> addTechnologyToCertification(
            @Parameter(description = "Certification ID") @PathVariable Long id,
            @Parameter(description = "Technology ID") @PathVariable Long technologyId) {
        log.info("Adding technology {} to certification {}", technologyId, id);
        CertificationResponse certification = certificationService.addTechnologyToCertification(id, technologyId);
        return ResponseEntity.ok(Response.success(certification, "Technology added to certification successfully"));
    }

    /**
     * Removes a technology association from a certification.
     *
     * @param id the certification ID
     * @param technologyId the technology ID
     * @return response entity containing the updated certification
     */
    @CertificationApiResponses.RemoveTechnology
    @DeleteMapping("/certifications/{id}/technologies/{technologyId}")
    public ResponseEntity<Response<CertificationResponse>> removeTechnologyFromCertification(
            @Parameter(description = "Certification ID") @PathVariable Long id,
            @Parameter(description = "Technology ID") @PathVariable Long technologyId) {
        log.info("Removing technology {} from certification {}", technologyId, id);
        CertificationResponse certification = certificationService.removeTechnologyFromCertification(id, technologyId);
        return ResponseEntity.ok(Response.success(certification, "Technology removed from certification successfully"));
    }
}
