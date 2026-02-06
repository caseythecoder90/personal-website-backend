package com.caseyquinn.personal_website.service;

import com.caseyquinn.personal_website.dao.ProjectDao;
import com.caseyquinn.personal_website.dao.ProjectImageDao;
import com.caseyquinn.personal_website.dto.request.CreateProjectImageRequest;
import com.caseyquinn.personal_website.dto.request.UpdateProjectImageRequest;
import com.caseyquinn.personal_website.dto.response.CloudinaryUploadResult;
import com.caseyquinn.personal_website.dto.response.ProjectImageResponse;
import com.caseyquinn.personal_website.entity.Project;
import com.caseyquinn.personal_website.entity.ProjectImage;
import com.caseyquinn.personal_website.exception.ErrorCode;
import com.caseyquinn.personal_website.exception.business.ValidationException;
import com.caseyquinn.personal_website.mapper.ProjectImageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.caseyquinn.personal_website.exception.ErrorMessages.*;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

/**
 * Service layer for managing project images including upload, retrieval, and deletion.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ProjectImageService {

    private static final int MAX_IMAGES_PER_PROJECT = 20;

    private final ProjectDao projectDao;
    private final ProjectImageDao projectImageDao;
    private final CloudinaryService cloudinaryService;
    private final FileValidationService fileValidationService;
    private final ProjectImageMapper projectImageMapper;

    /**
     * Uploads a new image for a project with validation and compensating transaction handling.
     *
     * @param projectId the project ID
     * @param file the image file to upload
     * @param request the image creation request containing metadata
     * @return the uploaded image response
     */
    @Transactional
    public ProjectImageResponse uploadImage(Long projectId, MultipartFile file, CreateProjectImageRequest request) {
        log.info("Uploading image for projectId: {}", projectId);

        Project project = projectDao.findByIdOrThrow(projectId);
        fileValidationService.validateImageFile(file);
        validateImageCount(projectId);

        CloudinaryUploadResult uploadResult = uploadToCloudinary(file, project.getSlug(), projectId);
        ProjectImage image = buildProjectImage(request, project, uploadResult);

        handlePrimaryImageFlag(projectId, request.getIsPrimary());

        return saveImageWithCompensation(image, uploadResult.getPublicId(), projectId);
    }

    /**
     * Retrieves all images for a specific project.
     *
     * @param projectId the project ID
     * @return list of project image responses
     */
    public List<ProjectImageResponse> getProjectImages(Long projectId) {
        log.info("Fetching images for projectId: {}", projectId);

        projectDao.findByIdOrThrow(projectId);

        List<ProjectImage> images = projectImageDao.findByProjectId(projectId);
        return projectImageMapper.toResponseList(images);
    }

    /**
     * Retrieves a specific image by ID with ownership validation.
     *
     * @param projectId the project ID
     * @param imageId the image ID
     * @return the image response
     */
    public ProjectImageResponse getImageById(Long projectId, Long imageId) {
        log.info("Fetching image: imageId={}, projectId={}", imageId, projectId);

        ProjectImage image = projectImageDao.findByIdOrThrow(imageId);
        validateImageOwnership(image, projectId);

        return projectImageMapper.toResponse(image);
    }

    /**
     * Updates image metadata without replacing the file.
     *
     * @param projectId the project ID
     * @param imageId the image ID
     * @param request the update request containing new metadata
     * @return the updated image response
     */
    @Transactional
    public ProjectImageResponse updateImageMetadata(Long projectId, Long imageId, UpdateProjectImageRequest request) {
        log.info("Updating image metadata: imageId={}, projectId={}", imageId, projectId);

        ProjectImage image = projectImageDao.findByIdOrThrow(imageId);
        validateImageOwnership(image, projectId);

        if (isTrue(request.getIsPrimary()) && isNotTrue(image.getIsPrimary())) {
            projectImageDao.unsetPrimaryForProject(projectId);
        }

        projectImageMapper.updateEntityFromRequest(request, image);
        ProjectImage updatedImage = projectImageDao.save(image);

        log.info("Image metadata updated successfully: imageId={}", imageId);
        return projectImageMapper.toResponse(updatedImage);
    }

    /**
     * Deletes an image from the project with ownership validation.
     * Performs best-effort deletion from Cloudinary.
     *
     * @param projectId the project ID
     * @param imageId the image ID
     */
    @Transactional
    public void deleteImage(Long projectId, Long imageId) {
        log.info("Deleting image: imageId={}, projectId={}", imageId, projectId);

        ProjectImage image = projectImageDao.findByIdOrThrow(imageId);
        validateImageOwnership(image, projectId);

        String cloudinaryPublicId = image.getCloudinaryPublicId();

        projectImageDao.deleteById(imageId);
        log.info("Image deleted from database: imageId={}", imageId);

        deleteFromCloudinaryBestEffort(cloudinaryPublicId);
    }

    /**
     * Sets an image as the primary image for a project.
     * Unsets any existing primary image first.
     *
     * @param projectId the project ID
     * @param imageId the image ID
     * @return the updated image response
     */
    @Transactional
    public ProjectImageResponse setPrimaryImage(Long projectId, Long imageId) {
        log.info("Setting primary image: imageId={}, projectId={}", imageId, projectId);

        ProjectImage image = projectImageDao.findByIdOrThrow(imageId);
        validateImageOwnership(image, projectId);

        projectImageDao.unsetPrimaryForProject(projectId);
        image.setIsPrimary(true);
        ProjectImage updatedImage = projectImageDao.save(image);

        log.info("Image set as primary: imageId={}", imageId);
        return projectImageMapper.toResponse(updatedImage);
    }

    /**
     * Validates that an image count does not exceed the maximum allowed per project.
     *
     * @param projectId the project ID to check
     * @throws ValidationException if the maximum image count is exceeded
     */
    private void validateImageCount(Long projectId) {
        long imageCount = projectImageDao.countByProjectId(projectId);
        if (imageCount >= MAX_IMAGES_PER_PROJECT) {
            throw new ValidationException(
                ErrorCode.MAX_IMAGES_EXCEEDED,
                String.format(MAX_IMAGES_EXCEEDED_FORMAT, MAX_IMAGES_PER_PROJECT)
            );
        }
    }

    /**
     * Validates that an image belongs to the specified project.
     *
     * @param image the image to validate
     * @param projectId the expected project ID
     * @throws ValidationException if the image does not belong to the project
     */
    private void validateImageOwnership(ProjectImage image, Long projectId) {
        if (!image.getProject().getId().equals(projectId)) {
            throw new ValidationException(
                ErrorCode.VALIDATION_FAILED,
                IMAGE_OWNERSHIP_MISMATCH
            );
        }
    }

    /**
     * Uploads an image file to Cloudinary with error handling.
     *
     * @param file the file to upload
     * @param subFolder the subfolder path in Cloudinary
     * @param projectId the project ID for logging
     * @return the upload result containing URL and public ID
     * @throws ValidationException if upload fails
     */
    private CloudinaryUploadResult uploadToCloudinary(MultipartFile file, String subFolder, Long projectId) {
        try {
            return cloudinaryService.uploadImage(file, subFolder);
        } catch (ValidationException e) {
            log.error("Failed to upload image to Cloudinary for projectId: {}", projectId, e);
            throw e;
        }
    }

    /**
     * Builds a ProjectImage entity from the request and upload result.
     *
     * @param request the creation request containing metadata
     * @param project the project to associate with
     * @param uploadResult the Cloudinary upload result
     * @return the constructed ProjectImage entity
     */
    private ProjectImage buildProjectImage(CreateProjectImageRequest request, Project project,
                                           CloudinaryUploadResult uploadResult) {
        ProjectImage image = projectImageMapper.toEntity(request);
        image.setProject(project);
        image.setUrl(uploadResult.getSecureUrl());
        image.setCloudinaryPublicId(uploadResult.getPublicId());
        return image;
    }

    /**
     * Handles the primary image flag by unsetting any existing primary image if needed.
     *
     * @param projectId the project ID
     * @param isPrimary whether the new image should be primary
     */
    private void handlePrimaryImageFlag(Long projectId, Boolean isPrimary) {
        if (isTrue(isPrimary)) {
            projectImageDao.unsetPrimaryForProject(projectId);
        }
    }

    /**
     * Saves an image with compensating transaction support.
     * If database save fails, deletes the uploaded image from Cloudinary.
     *
     * @param image the image to save
     * @param cloudinaryPublicId the Cloudinary public ID for rollback
     * @param projectId the project ID for logging
     * @return the saved image response
     */
    private ProjectImageResponse saveImageWithCompensation(ProjectImage image, String cloudinaryPublicId,
                                                           Long projectId) {
        try {
            ProjectImage savedImage = projectImageDao.save(image);
            log.info("Image saved successfully: imageId={}, projectId={}", savedImage.getId(), projectId);
            return projectImageMapper.toResponse(savedImage);

        } catch (Exception e) {
            log.error("Failed to save image to database, rolling back Cloudinary upload", e);
            cloudinaryService.deleteImage(cloudinaryPublicId);
            throw e;
        }
    }

    /**
     * Attempts to delete an image from Cloudinary with best-effort error handling.
     * Logs errors but does not throw exceptions.
     *
     * @param cloudinaryPublicId the Cloudinary public ID to delete
     */
    private void deleteFromCloudinaryBestEffort(String cloudinaryPublicId) {
        try {
            cloudinaryService.deleteImage(cloudinaryPublicId);
        } catch (Exception e) {
            log.error("Failed to delete image from Cloudinary (continuing): publicId={}", cloudinaryPublicId, e);
        }
    }
}
