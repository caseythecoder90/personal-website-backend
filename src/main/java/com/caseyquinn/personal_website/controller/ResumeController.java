package com.caseyquinn.personal_website.controller;

import com.caseyquinn.personal_website.annotations.ResumeApiResponses;
import com.caseyquinn.personal_website.dto.response.ResumeResponse;
import com.caseyquinn.personal_website.dto.response.Response;
import com.caseyquinn.personal_website.service.ResumeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for managing resume uploads and downloads.
 */
@RestController
@RequestMapping("/api/v1/resume")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Resume", description = "Resume upload and download APIs")
public class ResumeController {

    private final ResumeService resumeService;

    /**
     * Retrieves metadata for the currently active resume.
     *
     * @return response entity containing the resume metadata
     */
    @ResumeApiResponses.GetActive
    @GetMapping
    public ResponseEntity<Response<ResumeResponse>> getActiveResume() {
        log.info("Fetching active resume metadata");
        ResumeResponse resume = resumeService.getActiveResume();
        return ResponseEntity.ok(Response.success(resume, "Resume retrieved successfully"));
    }

    /**
     * Redirects to the resume PDF download URL on Cloudinary.
     *
     * @return redirect response to the resume URL
     */
    @ResumeApiResponses.Download
    @GetMapping("/download")
    public ResponseEntity<Void> downloadResume() {
        log.info("Redirecting to resume download");
        String downloadUrl = resumeService.getResumeDownloadUrl();
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, downloadUrl)
                .build();
    }

    /**
     * Uploads a new resume PDF, replacing the current active resume.
     *
     * @param file the PDF file to upload
     * @return response entity containing the uploaded resume metadata with HTTP 201 status
     */
    @ResumeApiResponses.Upload
    @PostMapping
    public ResponseEntity<Response<ResumeResponse>> uploadResume(
            @RequestParam("file") MultipartFile file) {
        log.info("Uploading new resume: {}", file.getOriginalFilename());
        ResumeResponse resume = resumeService.uploadResume(file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success(resume, "Resume uploaded successfully"));
    }

    /**
     * Deletes the currently active resume.
     *
     * @return response entity with success message
     */
    @ResumeApiResponses.Delete
    @DeleteMapping
    public ResponseEntity<Response<Void>> deleteResume() {
        log.info("Deleting active resume");
        resumeService.deleteResume();
        return ResponseEntity.ok(Response.success(null, "Resume deleted successfully"));
    }
}
