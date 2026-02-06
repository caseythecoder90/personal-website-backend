package com.caseyquinn.personal_website.service;

import com.caseyquinn.personal_website.dto.response.CloudinaryUploadResult;
import com.caseyquinn.personal_website.exception.business.ValidationException;
import com.caseyquinn.personal_website.exception.ErrorCode;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Service for managing image uploads and deletions with Cloudinary cloud storage.
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
     * @return upload result with URL and metadata
     */
    public CloudinaryUploadResult uploadImage(MultipartFile file, String subFolder) {
        log.info("Uploading image to Cloudinary: {} bytes", file.getSize());

        try {
            String uploadFolder = nonNull(subFolder) ? folder + "/" + subFolder : folder;

            Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", uploadFolder,
                "resource_type", "image",
                "use_filename", true,
                "unique_filename", true
            );

            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            CloudinaryUploadResult result = CloudinaryUploadResult.builder()
                .url((String) uploadResult.get("url"))
                .secureUrl((String) uploadResult.get("secure_url"))
                .publicId((String) uploadResult.get("public_id"))
                .format((String) uploadResult.get("format"))
                .bytes(((Number) uploadResult.get("bytes")).longValue())
                .width((Integer) uploadResult.get("width"))
                .height((Integer) uploadResult.get("height"))
                .build();

            log.info("Image uploaded successfully to Cloudinary: publicId={}", result.getPublicId());
            return result;

        } catch (IOException e) {
            log.error("Failed to upload image to Cloudinary", e);
            throw new ValidationException(ErrorCode.CLOUDINARY_ERROR, "Failed to upload image: " + e.getMessage());
        }
    }

    /**
     * Deletes an image from Cloudinary by its public ID.
     * This is a best-effort operation that does not throw exceptions on failure.
     *
     * @param publicId the Cloudinary public ID of the image to delete
     */
    public void deleteImage(String publicId) {
        if (isBlank(publicId)) {
            log.warn("Attempted to delete image with null or blank publicId");
            return;
        }

        log.info("Deleting image from Cloudinary: publicId={}", publicId);

        try {
            Map<?, ?> deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String result = (String) deleteResult.get("result");

            if ("ok".equals(result)) {
                log.info("Image deleted successfully from Cloudinary: publicId={}", publicId);
            } else if ("not found".equals(result)) {
                log.warn("Image not found in Cloudinary: publicId={}", publicId);
            } else {
                log.error("Unexpected result from Cloudinary delete: {}", result);
            }

        } catch (IOException e) {
            log.error("Failed to delete image from Cloudinary: publicId={}", publicId, e);
        }
    }
}
