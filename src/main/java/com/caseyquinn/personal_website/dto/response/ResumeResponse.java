package com.caseyquinn.personal_website.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response DTO for resume metadata.
 */
@Data
@Builder
@Schema(description = "Resume information")
public class ResumeResponse {

    @Schema(description = "Resume file name", example = "Casey_Quinn_Resume.pdf")
    private String fileName;

    @Schema(description = "Resume download URL")
    private String fileUrl;

    @Schema(description = "File size in bytes", example = "245760")
    private Long fileSize;

    @Schema(description = "Content type", example = "application/pdf")
    private String contentType;

    @Schema(description = "Upload timestamp")
    private LocalDateTime uploadedAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}
