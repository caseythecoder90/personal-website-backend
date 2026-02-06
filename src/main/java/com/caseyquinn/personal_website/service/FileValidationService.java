package com.caseyquinn.personal_website.service;

import com.caseyquinn.personal_website.exception.ErrorCode;
import com.caseyquinn.personal_website.exception.business.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import static java.util.Objects.isNull;

/**
 * Service for validating uploaded files including size, type, and content verification.
 */
@Service
@Slf4j
public class FileValidationService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/webp"
    );

    private static final byte[] JPEG_MAGIC = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_MAGIC = {(byte) 0x89, 0x50, 0x4E, 0x47};
    private static final byte[] GIF_MAGIC = {0x47, 0x49, 0x46};
    private static final byte[] WEBP_MAGIC = {0x52, 0x49, 0x46, 0x46};

    /**
     * Validates an image file for size, content type, and magic bytes.
     *
     * @param file the file to validate
     * @throws ValidationException if validation fails
     */
    public void validateImageFile(MultipartFile file) {
        log.debug("Validating image file: name={}, size={}, contentType={}",
            file.getOriginalFilename(), file.getSize(), file.getContentType());

        validateFileNotEmpty(file);
        validateFileSize(file);
        String contentType = validateContentType(file);
        validateMagicBytes(file, contentType);

        log.debug("Image file validation successful");
    }

    private boolean isValidImageMagicBytes(byte[] fileBytes, String contentType) {
        if (isNull(fileBytes) || fileBytes.length < 4) {
            return false;
        }

        return switch (contentType.toLowerCase()) {
            case "image/jpeg" -> startsWith(fileBytes, JPEG_MAGIC);
            case "image/png" -> startsWith(fileBytes, PNG_MAGIC);
            case "image/gif" -> startsWith(fileBytes, GIF_MAGIC);
            case "image/webp" -> startsWith(fileBytes, WEBP_MAGIC);
            default -> false;
        };
    }

    private boolean startsWith(byte[] data, byte[] magic) {
        if (data.length < magic.length) {
            return false;
        }
        return Arrays.equals(Arrays.copyOf(data, magic.length), magic);
    }

    /**
     * Validates that the file is not empty.
     *
     * @param file the file to validate
     * @throws ValidationException if the file is empty
     */
    private void validateFileNotEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ValidationException(ErrorCode.FILE_UPLOAD_ERROR, "File is empty");
        }
    }

    /**
     * Validates that the file size does not exceed the maximum allowed.
     *
     * @param file the file to validate
     * @throws ValidationException if the file size exceeds the limit
     */
    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ValidationException(
                ErrorCode.FILE_SIZE_EXCEEDED,
                String.format("File size (%d bytes) exceeds maximum allowed (%d bytes)",
                    file.getSize(), MAX_FILE_SIZE)
            );
        }
    }

    /**
     * Validates that the file content type is allowed.
     *
     * @param file the file to validate
     * @return the validated content type
     * @throws ValidationException if the content type is invalid or not allowed
     */
    private String validateContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (isNull(contentType) || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new ValidationException(
                ErrorCode.INVALID_FILE_TYPE,
                String.format("Invalid file type: %s. Allowed types: %s", contentType, ALLOWED_CONTENT_TYPES)
            );
        }
        return contentType;
    }

    /**
     * Validates that the file content matches its declared content type using magic bytes.
     *
     * @param file the file to validate
     * @param contentType the declared content type
     * @throws ValidationException if magic bytes do not match the content type
     */
    private void validateMagicBytes(MultipartFile file, String contentType) {
        try {
            byte[] fileBytes = file.getBytes();
            if (!isValidImageMagicBytes(fileBytes, contentType)) {
                throw new ValidationException(
                    ErrorCode.INVALID_FILE_TYPE,
                    "File content does not match declared content type"
                );
            }
        } catch (IOException e) {
            log.error("Failed to read file bytes for validation", e);
            throw new ValidationException(ErrorCode.FILE_UPLOAD_ERROR, "Failed to read file: " + e.getMessage());
        }
    }
}
