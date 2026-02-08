package com.caseyquinn.personal_website.controller;

import com.caseyquinn.personal_website.annotations.ProjectLinkApiResponses;
import com.caseyquinn.personal_website.dto.request.CreateProjectLinkRequest;
import com.caseyquinn.personal_website.dto.request.UpdateProjectLinkRequest;
import com.caseyquinn.personal_website.dto.response.ProjectLinkResponse;
import com.caseyquinn.personal_website.dto.response.Response;
import com.caseyquinn.personal_website.service.ProjectLinkService;
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
 * REST controller for managing project links (GitHub, demo, docs, etc.).
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Project Links", description = "Project link management APIs")
public class ProjectLinkController {

    private final ProjectLinkService projectLinkService;

    /**
     * Creates a new link for a project.
     *
     * @param projectId the project ID
     * @param request the link creation request
     * @return response entity containing the created link details with HTTP 201 status
     */
    @ProjectLinkApiResponses.Create
    @PostMapping("/projects/{projectId}/links")
    public ResponseEntity<Response<ProjectLinkResponse>> createLink(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @Valid @RequestBody CreateProjectLinkRequest request) {

        log.info("Creating link for projectId: {}", projectId);
        ProjectLinkResponse response = projectLinkService.createLink(projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success(response, "Link created successfully"));
    }

    /**
     * Retrieves all links for a specific project.
     *
     * @param projectId the project ID
     * @return response entity containing list of project links
     */
    @ProjectLinkApiResponses.GetAll
    @GetMapping("/projects/{projectId}/links")
    public ResponseEntity<Response<List<ProjectLinkResponse>>> getProjectLinks(
            @Parameter(description = "Project ID") @PathVariable Long projectId) {

        log.info("Fetching links for projectId: {}", projectId);
        List<ProjectLinkResponse> links = projectLinkService.getProjectLinks(projectId);
        return ResponseEntity.ok(Response.success(links, "Links retrieved successfully"));
    }

    /**
     * Retrieves a specific link by project ID and link ID.
     *
     * @param projectId the project ID
     * @param linkId the link ID
     * @return response entity containing the link details
     */
    @ProjectLinkApiResponses.GetById
    @GetMapping("/projects/{projectId}/links/{linkId}")
    public ResponseEntity<Response<ProjectLinkResponse>> getLink(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @Parameter(description = "Link ID") @PathVariable Long linkId) {

        log.info("Fetching link: linkId={}, projectId={}", linkId, projectId);
        ProjectLinkResponse link = projectLinkService.getLinkById(projectId, linkId);
        return ResponseEntity.ok(Response.success(link, "Link retrieved successfully"));
    }

    /**
     * Updates an existing link.
     *
     * @param projectId the project ID
     * @param linkId the link ID
     * @param request the link update request
     * @return response entity containing the updated link details
     */
    @ProjectLinkApiResponses.Update
    @PutMapping("/projects/{projectId}/links/{linkId}")
    public ResponseEntity<Response<ProjectLinkResponse>> updateLink(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @Parameter(description = "Link ID") @PathVariable Long linkId,
            @Valid @RequestBody UpdateProjectLinkRequest request) {

        log.info("Updating link: linkId={}, projectId={}", linkId, projectId);
        ProjectLinkResponse link = projectLinkService.updateLink(projectId, linkId, request);
        return ResponseEntity.ok(Response.success(link, "Link updated successfully"));
    }

    /**
     * Deletes a link from a project.
     *
     * @param projectId the project ID
     * @param linkId the link ID
     * @return response entity with success message
     */
    @ProjectLinkApiResponses.Delete
    @DeleteMapping("/projects/{projectId}/links/{linkId}")
    public ResponseEntity<Response<Void>> deleteLink(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @Parameter(description = "Link ID") @PathVariable Long linkId) {

        log.info("Deleting link: linkId={}, projectId={}", linkId, projectId);
        projectLinkService.deleteLink(projectId, linkId);
        return ResponseEntity.ok(Response.success(null, "Link deleted successfully"));
    }
}
