package com.caseyquinn.personal_website.service;

import com.caseyquinn.personal_website.dao.ProjectDao;
import com.caseyquinn.personal_website.dao.ProjectLinkDao;
import com.caseyquinn.personal_website.dto.request.CreateProjectLinkRequest;
import com.caseyquinn.personal_website.dto.request.UpdateProjectLinkRequest;
import com.caseyquinn.personal_website.dto.response.ProjectLinkResponse;
import com.caseyquinn.personal_website.entity.Project;
import com.caseyquinn.personal_website.entity.ProjectLink;
import com.caseyquinn.personal_website.exception.ErrorCode;
import com.caseyquinn.personal_website.exception.business.ValidationException;
import com.caseyquinn.personal_website.mapper.ProjectLinkMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.caseyquinn.personal_website.exception.ErrorMessages.LINK_OWNERSHIP_MISMATCH;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

/**
 * Service layer for managing project links including creation, retrieval, and deletion.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ProjectLinkService {

    private static final int DEFAULT_DISPLAY_ORDER = 0;

    private final ProjectLinkDao projectLinkDao;
    private final ProjectDao projectDao;
    private final ProjectLinkMapper projectLinkMapper;

    /**
     * Retrieves all links for a specific project.
     *
     * @param projectId the project ID
     * @return list of project link responses
     */
    public List<ProjectLinkResponse> getProjectLinks(Long projectId) {
        log.info("Fetching links for projectId: {}", projectId);
        projectDao.findByIdOrThrow(projectId);
        List<ProjectLink> links = projectLinkDao.findByProjectId(projectId);
        return projectLinkMapper.toResponseList(links);
    }

    /**
     * Retrieves a specific link by ID with ownership validation.
     *
     * @param projectId the project ID
     * @param linkId the link ID
     * @return the link response
     */
    public ProjectLinkResponse getLinkById(Long projectId, Long linkId) {
        log.info("Fetching link: linkId={}, projectId={}", linkId, projectId);
        ProjectLink link = projectLinkDao.findByIdOrThrow(linkId);
        validateLinkOwnership(link, projectId);
        return projectLinkMapper.toResponse(link);
    }

    /**
     * Creates a new link for a project.
     *
     * @param projectId the project ID
     * @param request the create link request
     * @return the created link response
     */
    @Transactional
    public ProjectLinkResponse createLink(Long projectId, CreateProjectLinkRequest request) {
        log.info("Creating link for projectId: {}", projectId);
        Project project = projectDao.findByIdOrThrow(projectId);
        ProjectLink link = buildProjectLink(request, project);
        ProjectLink savedLink = projectLinkDao.save(link);
        log.info("Created link with id: {}", savedLink.getId());
        return projectLinkMapper.toResponse(savedLink);
    }

    /**
     * Creates multiple links for a project.
     * Used when creating a project with embedded links.
     *
     * @param project the project entity
     * @param requests the list of create link requests
     */
    @Transactional
    public void createLinks(Project project, List<CreateProjectLinkRequest> requests) {
        if (isEmpty(requests)) {
            return;
        }
        log.info("Creating {} links for projectId: {}", requests.size(), project.getId());
        for (CreateProjectLinkRequest request : requests) {
            ProjectLink link = buildProjectLink(request, project);
            projectLinkDao.save(link);
        }
    }

    /**
     * Updates an existing link with ownership validation.
     *
     * @param projectId the project ID
     * @param linkId the link ID
     * @param request the update request
     * @return the updated link response
     */
    @Transactional
    public ProjectLinkResponse updateLink(Long projectId, Long linkId, UpdateProjectLinkRequest request) {
        log.info("Updating link: linkId={}, projectId={}", linkId, projectId);
        ProjectLink link = projectLinkDao.findByIdOrThrow(linkId);
        validateLinkOwnership(link, projectId);
        applyUpdates(link, request);
        ProjectLink updatedLink = projectLinkDao.save(link);
        log.info("Updated link with id: {}", updatedLink.getId());
        return projectLinkMapper.toResponse(updatedLink);
    }

    /**
     * Deletes a link with ownership validation.
     *
     * @param projectId the project ID
     * @param linkId the link ID
     */
    @Transactional
    public void deleteLink(Long projectId, Long linkId) {
        log.info("Deleting link: linkId={}, projectId={}", linkId, projectId);
        ProjectLink link = projectLinkDao.findByIdOrThrow(linkId);
        validateLinkOwnership(link, projectId);
        projectLinkDao.deleteById(linkId);
        log.info("Deleted link with id: {}", linkId);
    }

    /**
     * Builds a ProjectLink entity from the request and project.
     *
     * @param request the creation request
     * @param project the project to associate with
     * @return the constructed ProjectLink entity
     */
    private ProjectLink buildProjectLink(CreateProjectLinkRequest request, Project project) {
        return ProjectLink.builder()
                .project(project)
                .linkType(request.getType())
                .url(request.getUrl())
                .label(request.getLabel())
                .displayOrder(nonNull(request.getDisplayOrder()) ? request.getDisplayOrder() : DEFAULT_DISPLAY_ORDER)
                .build();
    }

    /**
     * Applies update request fields to an existing link entity.
     *
     * @param link the link to update
     * @param request the update request
     */
    private void applyUpdates(ProjectLink link, UpdateProjectLinkRequest request) {
        if (nonNull(request.getType())) {
            link.setLinkType(request.getType());
        }
        if (nonNull(request.getUrl())) {
            link.setUrl(request.getUrl());
        }
        if (nonNull(request.getLabel())) {
            link.setLabel(request.getLabel());
        }
        if (nonNull(request.getDisplayOrder())) {
            link.setDisplayOrder(request.getDisplayOrder());
        }
    }

    /**
     * Validates that a link belongs to the specified project.
     *
     * @param link the link to validate
     * @param projectId the expected project ID
     * @throws ValidationException if the link does not belong to the project
     */
    private void validateLinkOwnership(ProjectLink link, Long projectId) {
        if (!link.getProject().getId().equals(projectId)) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, LINK_OWNERSHIP_MISMATCH);
        }
    }
}
