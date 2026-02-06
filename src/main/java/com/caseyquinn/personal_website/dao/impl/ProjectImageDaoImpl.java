package com.caseyquinn.personal_website.dao.impl;

import com.caseyquinn.personal_website.dao.ProjectImageDao;
import com.caseyquinn.personal_website.entity.ProjectImage;
import com.caseyquinn.personal_website.exception.NotFoundException;
import com.caseyquinn.personal_website.exception.data.RetryableDataAccess;
import com.caseyquinn.personal_website.repository.ProjectImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of ProjectImageDao with automatic retry on transient data access failures.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@RetryableDataAccess
public class ProjectImageDaoImpl implements ProjectImageDao {

    private final ProjectImageRepository projectImageRepository;

    @Override
    public ProjectImage save(ProjectImage image) {
        log.info("DAO: Saving project image for projectId: {}", image.getProject().getId());
        return projectImageRepository.save(image);
    }

    @Override
    public Optional<ProjectImage> findById(Long id) {
        log.info("DAO: Finding project image by id: {}", id);
        return projectImageRepository.findById(id);
    }

    @Override
    public ProjectImage findByIdOrThrow(Long id) {
        return findById(id)
            .orElseThrow(() -> new NotFoundException("ProjectImage", id));
    }

    @Override
    public List<ProjectImage> findByProjectId(Long projectId) {
        log.info("DAO: Finding all images for projectId: {}", projectId);
        return projectImageRepository.findByProjectIdOrderByDisplayOrderAsc(projectId);
    }

    @Override
    public void deleteById(Long id) {
        log.info("DAO: Deleting project image with id: {}", id);
        projectImageRepository.deleteById(id);
    }

    @Override
    public long countByProjectId(Long projectId) {
        log.info("DAO: Counting images for projectId: {}", projectId);
        return projectImageRepository.countByProjectId(projectId);
    }

    @Override
    public void unsetPrimaryForProject(Long projectId) {
        log.info("DAO: Unsetting primary image for projectId: {}", projectId);
        Optional<ProjectImage> currentPrimary = projectImageRepository.findByProjectIdAndIsPrimaryTrue(projectId);

        if (currentPrimary.isPresent()) {
            ProjectImage image = currentPrimary.get();
            image.setIsPrimary(false);
            projectImageRepository.save(image);
            log.debug("DAO: Unset primary flag for imageId: {}", image.getId());
        } else {
            log.debug("DAO: No primary image found for projectId: {}", projectId);
        }
    }
}
