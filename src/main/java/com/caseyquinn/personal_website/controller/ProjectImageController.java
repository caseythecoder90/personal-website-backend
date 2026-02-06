package com.caseyquinn.personal_website.controller;

import com.caseyquinn.personal_website.annotations.ProjectImageApiResponses;
import com.caseyquinn.personal_website.dto.request.CreateProjectImageRequest;
import com.caseyquinn.personal_website.dto.request.UpdateProjectImageRequest;
import com.caseyquinn.personal_website.dto.response.ProjectImageResponse;
import com.caseyquinn.personal_website.dto.response.Response;
import com.caseyquinn.personal_website.service.ProjectImageService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for managing project images and their metadata.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Project Images", description = "Project image management APIs")
public class ProjectImageController {

    private final ProjectImageService projectImageService;

    /**
     * Uploads a new image for a project.
     *
     * @param projectId the project ID
     * @param file the image file to upload
     * @param request the image metadata
     * @return response entity containing the uploaded image details with HTTP 201 status
     */
    @ProjectImageApiResponses.Upload
    @PostMapping(value = "/projects/{projectId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<ProjectImageResponse>> uploadImage(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @Parameter(description = "Image file to upload") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Image metadata") @Valid @ModelAttribute CreateProjectImageRequest request) {

        log.info("Uploading image for projectId: {}", projectId);
        ProjectImageResponse response = projectImageService.uploadImage(projectId, file, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Response.success(response, "Image uploaded successfully"));
    }

    /**
     * Retrieves all images for a specific project.
     *
     * @param projectId the project ID
     * @return response entity containing list of project images
     */
    @ProjectImageApiResponses.GetAll
    @GetMapping("/projects/{projectId}/images")
    public ResponseEntity<Response<List<ProjectImageResponse>>> getProjectImages(
            @Parameter(description = "Project ID") @PathVariable Long projectId) {

        log.info("Fetching images for projectId: {}", projectId);
        List<ProjectImageResponse> images = projectImageService.getProjectImages(projectId);
        return ResponseEntity.ok(Response.success(images, "Images retrieved successfully"));
    }

    /**
     * Retrieves a specific image by project ID and image ID.
     *
     * @param projectId the project ID
     * @param imageId the image ID
     * @return response entity containing the image details
     */
    @ProjectImageApiResponses.GetById
    @GetMapping("/projects/{projectId}/images/{imageId}")
    public ResponseEntity<Response<ProjectImageResponse>> getImage(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @Parameter(description = "Image ID") @PathVariable Long imageId) {

        log.info("Fetching image: imageId={}, projectId={}", imageId, projectId);
        ProjectImageResponse image = projectImageService.getImageById(projectId, imageId);
        return ResponseEntity.ok(Response.success(image, "Image retrieved successfully"));
    }

    /**
     * Updates the metadata of an existing image.
     *
     * @param projectId the project ID
     * @param imageId the image ID
     * @param request the image metadata update request
     * @return response entity containing the updated image details
     */
    @ProjectImageApiResponses.Update
    @PutMapping("/projects/{projectId}/images/{imageId}")
    public ResponseEntity<Response<ProjectImageResponse>> updateImage(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @Parameter(description = "Image ID") @PathVariable Long imageId,
            @Valid @RequestBody UpdateProjectImageRequest request) {

        log.info("Updating image metadata: imageId={}, projectId={}", imageId, projectId);
        ProjectImageResponse image = projectImageService.updateImageMetadata(projectId, imageId, request);
        return ResponseEntity.ok(Response.success(image, "Image metadata updated successfully"));
    }

    /**
     * Deletes an image from a project.
     *
     * @param projectId the project ID
     * @param imageId the image ID
     * @return response entity with success message
     */
    @ProjectImageApiResponses.Delete
    @DeleteMapping("/projects/{projectId}/images/{imageId}")
    public ResponseEntity<Response<Void>> deleteImage(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @Parameter(description = "Image ID") @PathVariable Long imageId) {

        log.info("Deleting image: imageId={}, projectId={}", imageId, projectId);
        projectImageService.deleteImage(projectId, imageId);
        return ResponseEntity.ok(Response.success(null, "Image deleted successfully"));
    }

    /**
     * Sets an image as the primary image for a project.
     *
     * @param projectId the project ID
     * @param imageId the image ID
     * @return response entity containing the updated image details
     */
    @ProjectImageApiResponses.SetPrimary
    @PutMapping("/projects/{projectId}/images/{imageId}/set-primary")
    public ResponseEntity<Response<ProjectImageResponse>> setPrimaryImage(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @Parameter(description = "Image ID") @PathVariable Long imageId) {

        log.info("Setting primary image: imageId={}, projectId={}", imageId, projectId);
        ProjectImageResponse image = projectImageService.setPrimaryImage(projectId, imageId);
        return ResponseEntity.ok(Response.success(image, "Primary image set successfully"));
    }
}
