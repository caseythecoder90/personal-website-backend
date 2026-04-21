package com.caseyquinn.personal_website.service;

import com.caseyquinn.personal_website.dto.response.CloudinaryUploadResult;
import com.caseyquinn.personal_website.exception.ErrorCode;
import com.caseyquinn.personal_website.exception.business.ValidationException;
import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.caseyquinn.personal_website.exception.ErrorMessages.*;
import static com.caseyquinn.personal_website.constants.CloudinaryConstants.*;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Service for managing file uploads and deletions with Cloudinary cloud storage.
 * Supports both image uploads (with transformation metadata) and raw file uploads (e.g., PDFs).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder}")
    private String folder;

    /**
     * Uploads an image to Cloudinary with optional subfolder organization.
     *
     * @param file the image file to upload
     * @param subFolder optional subfolder within the main folder
     * @return upload result with URL, dimensions, and metadata
     */
    public CloudinaryUploadResult uploadImage(MultipartFile file, String subFolder) {
        return upload(file, subFolder, null, RESOURCE_TYPE_IMAGE, IMAGE_UPLOAD_FAILED);
    }

    /**
     * Uploads a raw file (e.g., PDF) to Cloudinary with an explicit public ID.
     * The public ID becomes the final URL segment, including extension, so the
     * browser saves the file with a human-readable name on direct download.
     *
     * @param file the file to upload
     * @param subFolder optional subfolder within the main folder
     * @param publicId the desired Cloudinary public ID (e.g. "casey_quinn_resume.pdf")
     * @return upload result with URL and metadata (no dimensions for raw files)
     */
    public CloudinaryUploadResult uploadRawFile(MultipartFile file, String subFolder, String publicId) {
        return upload(file, subFolder, publicId, RESOURCE_TYPE_RAW, RESUME_UPLOAD_FAILED);
    }

    /**
     * Deletes an image from Cloudinary by its public ID.
     * Best-effort operation that does not throw exceptions on failure.
     *
     * @param publicId the Cloudinary public ID of the image to delete
     */
    public void deleteImage(String publicId) {
        delete(publicId, RESOURCE_TYPE_IMAGE);
    }

    /**
     * Deletes a raw file from Cloudinary by its public ID.
     * Best-effort operation that does not throw exceptions on failure.
     *
     * @param publicId the Cloudinary public ID of the file to delete
     */
    public void deleteRawFile(String publicId) {
        delete(publicId, RESOURCE_TYPE_RAW);
    }

    /**
     * Uploads a file to Cloudinary with the specified resource type.
     *
     * @param file the file to upload
     * @param subFolder optional subfolder within the main folder
     * @param resourceType the Cloudinary resource type (image or raw)
     * @param errorMessageFormat the error message format for upload failures
     * @return upload result with URL and metadata
     */
    private CloudinaryUploadResult upload(MultipartFile file, String subFolder, String publicId,
                                          String resourceType, String errorMessageFormat) {
        log.info("Uploading {} to Cloudinary: {} bytes", resourceType, file.getSize());

        try {
            String uploadFolder = nonNull(subFolder) ? folder + "/" + subFolder : folder;

            Map<String, Object> uploadParams = new HashMap<>();
            uploadParams.put(PARAM_FOLDER, uploadFolder);
            uploadParams.put(PARAM_RESOURCE_TYPE, resourceType);
            if (isNotBlank(publicId)) {
                uploadParams.put(PARAM_PUBLIC_ID, publicId);
                uploadParams.put(PARAM_OVERWRITE, true);
            } else {
                uploadParams.put(PARAM_USE_FILENAME, true);
                uploadParams.put(PARAM_UNIQUE_FILENAME, true);
            }

            Map<?, ?> response = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            CloudinaryUploadResult result = buildUploadResult(response, resourceType);

            log.info("{} uploaded successfully to Cloudinary: publicId={}",
                resourceType, result.getPublicId());
            return result;

        } catch (IOException e) {
            log.error("Failed to upload {} to Cloudinary", resourceType, e);
            throw new ValidationException(ErrorCode.CLOUDINARY_ERROR,
                String.format(errorMessageFormat, e.getMessage()));
        }
    }

    /**
     * Builds a CloudinaryUploadResult from the Cloudinary API response map.
     * Image uploads include width and height; raw uploads do not.
     *
     * @param response the raw response map from Cloudinary
     * @param resourceType the resource type to determine which fields to extract
     * @return the structured upload result
     */
    private CloudinaryUploadResult buildUploadResult(Map<?, ?> response, String resourceType) {
        CloudinaryUploadResult.CloudinaryUploadResultBuilder builder = CloudinaryUploadResult.builder()
            .url(extractString(response, RESPONSE_URL))
            .secureUrl(extractString(response, RESPONSE_SECURE_URL))
            .publicId(extractString(response, RESPONSE_PUBLIC_ID))
            .format(extractString(response, RESPONSE_FORMAT))
            .bytes(extractLong(response, RESPONSE_BYTES));

        if (RESOURCE_TYPE_IMAGE.equals(resourceType)) {
            builder.width(extractInteger(response, RESPONSE_WIDTH))
                .height(extractInteger(response, RESPONSE_HEIGHT));
        }

        return builder.build();
    }

    /**
     * Deletes a resource from Cloudinary by public ID and resource type.
     * Best-effort operation — logs failures but does not throw.
     *
     * @param publicId the Cloudinary public ID
     * @param resourceType the Cloudinary resource type (image or raw)
     */
    private void delete(String publicId, String resourceType) {
        if (isBlank(publicId)) {
            log.warn("Attempted to delete {} with null or blank publicId", resourceType);
            return;
        }

        log.info("Deleting {} from Cloudinary: publicId={}", resourceType, publicId);

        try {
            Map<String, Object> params = RESOURCE_TYPE_IMAGE.equals(resourceType)
                ? Collections.emptyMap()
                : Map.of(PARAM_RESOURCE_TYPE, resourceType);

            Map<?, ?> deleteResponse = cloudinary.uploader().destroy(publicId, params);
            String result = extractString(deleteResponse, RESPONSE_RESULT);

            if (DELETE_OK.equals(result)) {
                log.info("{} deleted successfully from Cloudinary: publicId={}", resourceType, publicId);
            } else if (DELETE_NOT_FOUND.equals(result)) {
                log.warn("{} not found in Cloudinary: publicId={}", resourceType, publicId);
            } else {
                log.error("Unexpected result from Cloudinary delete: {}", result);
            }

        } catch (IOException e) {
            log.error("Failed to delete {} from Cloudinary: publicId={}", resourceType, publicId, e);
        }
    }

    /**
     * Safely extracts a String value from the Cloudinary response map.
     *
     * @param response the response map
     * @param key the key to extract
     * @return the string value, or null if not present
     */
    private String extractString(Map<?, ?> response, String key) {
        Object value = response.get(key);
        return value instanceof String s ? s : null;
    }

    /**
     * Safely extracts a Long value from the Cloudinary response map.
     *
     * @param response the response map
     * @param key the key to extract
     * @return the long value, or null if not present or not a number
     */
    private Long extractLong(Map<?, ?> response, String key) {
        Object value = response.get(key);
        return value instanceof Number n ? n.longValue() : null;
    }

    /**
     * Safely extracts an Integer value from the Cloudinary response map.
     *
     * @param response the response map
     * @param key the key to extract
     * @return the integer value, or null if not present or not a number
     */
    private Integer extractInteger(Map<?, ?> response, String key) {
        Object value = response.get(key);
        return value instanceof Number n ? n.intValue() : null;
    }
}