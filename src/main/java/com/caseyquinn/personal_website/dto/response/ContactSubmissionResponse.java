package com.caseyquinn.personal_website.dto.response;

import com.caseyquinn.personal_website.entity.enums.InquiryType;
import com.caseyquinn.personal_website.entity.enums.SubmissionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response DTO for contact submission data.
 */
@Data
@Builder
@Schema(description = "Contact submission information")
public class ContactSubmissionResponse {

    @Schema(description = "Submission ID", example = "1")
    private Long id;

    @Schema(description = "Sender's name", example = "John Doe")
    private String name;

    @Schema(description = "Sender's email address", example = "john@example.com")
    private String email;

    @Schema(description = "Message subject", example = "Project Collaboration Inquiry")
    private String subject;

    @Schema(description = "Message body")
    private String message;

    @Schema(description = "Type of inquiry", example = "GENERAL")
    private InquiryType inquiryType;

    @Schema(description = "Submission status", example = "NEW")
    private SubmissionStatus status;

    @Schema(description = "Sender's IP address")
    private String ipAddress;

    @Schema(description = "Sender's user agent")
    private String userAgent;

    @Schema(description = "Submission timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Response timestamp")
    private LocalDateTime respondedAt;
}
