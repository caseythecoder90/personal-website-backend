package com.caseyquinn.personal_website.service;

import com.caseyquinn.personal_website.dao.ResumeDao;
import com.caseyquinn.personal_website.dto.response.CloudinaryUploadResult;
import com.caseyquinn.personal_website.dto.response.ResumeResponse;
import com.caseyquinn.personal_website.entity.Resume;
import com.caseyquinn.personal_website.exception.ErrorCode;
import com.caseyquinn.personal_website.exception.NotFoundException;
import com.caseyquinn.personal_website.mapper.ResumeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.caseyquinn.personal_website.exception.ErrorMessages.*;

/**
 * Service layer for managing resume uploads and downloads via Cloudinary.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ResumeService {

    private final ResumeDao resumeDao;
    private final ResumeMapper resumeMapper;
    private final CloudinaryService cloudinaryService;
    private final FileValidationService fileValidationService;

    @Value("${app.resume.max-file-size}")
    private long maxFileSize;

    /**
     * Uploads a new resume PDF, replacing the current active resume if one exists.
     *
     * @param file the PDF file to upload
     * @return the uploaded resume metadata
     */
    @Transactional
    public ResumeResponse uploadResume(MultipartFile file) {
        log.info("Service: Uploading new resume: {}", file.getOriginalFilename());

        fileValidationService.validatePdfFile(file, maxFileSize);

        resumeDao.findActive().ifPresent(existing -> {
            log.info("Service: Replacing existing resume: {}", existing.getFileName());
            cloudinaryService.deleteRawFile(existing.getCloudinaryPublicId());
            resumeDao.deleteById(existing.getId());
        });

        CloudinaryUploadResult uploadResult = cloudinaryService.uploadRawFile(file, "resumes");

        Resume resume = Resume.builder()
                .fileName(file.getOriginalFilename())
                .fileUrl(uploadResult.getSecureUrl())
                .cloudinaryPublicId(uploadResult.getPublicId())
                .fileSize(uploadResult.getBytes())
                .contentType("application/pdf")
                .active(true)
                .build();

        Resume saved = resumeDao.save(resume);
        log.info("Service: Resume uploaded successfully with id: {}", saved.getId());
        return resumeMapper.toResponse(saved);
    }

    /**
     * Retrieves metadata for the currently active resume.
     *
     * @return the active resume metadata
     */
    public ResumeResponse getActiveResume() {
        log.info("Service: Fetching active resume");
        Resume resume = resumeDao.findActive()
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND, NO_ACTIVE_RESUME));
        return resumeMapper.toResponse(resume);
    }

    /**
     * Gets the download URL for the currently active resume.
     *
     * @return the Cloudinary URL of the active resume
     */
    public String getResumeDownloadUrl() {
        log.info("Service: Fetching resume download URL");
        Resume resume = resumeDao.findActive()
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND, NO_ACTIVE_RESUME));
        return resume.getFileUrl();
    }

    /**
     * Deletes the currently active resume from Cloudinary and the database.
     */
    @Transactional
    public void deleteResume() {
        log.info("Service: Deleting active resume");
        Resume resume = resumeDao.findActive()
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND, NO_ACTIVE_RESUME));

        cloudinaryService.deleteRawFile(resume.getCloudinaryPublicId());
        resumeDao.deleteById(resume.getId());
        log.info("Service: Resume deleted successfully");
    }
}
