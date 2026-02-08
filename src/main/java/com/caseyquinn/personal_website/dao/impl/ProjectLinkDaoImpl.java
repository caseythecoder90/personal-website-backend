package com.caseyquinn.personal_website.dao.impl;

import com.caseyquinn.personal_website.dao.ProjectLinkDao;
import com.caseyquinn.personal_website.entity.ProjectLink;
import com.caseyquinn.personal_website.entity.enums.LinkType;
import com.caseyquinn.personal_website.exception.NotFoundException;
import com.caseyquinn.personal_website.exception.data.RetryableDataAccess;
import com.caseyquinn.personal_website.repository.ProjectLinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of ProjectLinkDao with automatic retry on transient data access failures.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@RetryableDataAccess
public class ProjectLinkDaoImpl implements ProjectLinkDao {

    private final ProjectLinkRepository projectLinkRepository;

    @Override
    public ProjectLink save(ProjectLink link) {
        log.info("Saving project link for projectId: {}", link.getProject().getId());
        return projectLinkRepository.save(link);
    }

    @Override
    public Optional<ProjectLink> findById(Long id) {
        log.info("Finding project link by id: {}", id);
        return projectLinkRepository.findById(id);
    }

    @Override
    public ProjectLink findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("ProjectLink", id));
    }

    @Override
    public List<ProjectLink> findByProjectId(Long projectId) {
        log.info("Finding all links for projectId: {}", projectId);
        return projectLinkRepository.findByProjectIdOrderByLinkTypeAscDisplayOrderAsc(projectId);
    }

    @Override
    public List<ProjectLink> findByProjectIdAndType(Long projectId, LinkType linkType) {
        log.info("Finding links of type {} for projectId: {}", linkType, projectId);
        return projectLinkRepository.findByProjectIdAndLinkTypeOrderByDisplayOrderAsc(projectId, linkType);
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting project link with id: {}", id);
        projectLinkRepository.deleteById(id);
    }

    @Override
    public long countByProjectId(Long projectId) {
        log.info("Counting links for projectId: {}", projectId);
        return projectLinkRepository.countByProjectId(projectId);
    }
}
