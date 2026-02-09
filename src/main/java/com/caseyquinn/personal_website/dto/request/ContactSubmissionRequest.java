package com.caseyquinn.personal_website.dto.request;

import com.caseyquinn.personal_website.entity.enums.InquiryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for submitting a contact form.
 */
@Data
@Schema(description = "Request to submit a contact form")
public class ContactSubmissionRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    @Schema(description = "Sender's name", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    @Schema(description = "Sender's email address", example = "john@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Size(max = 255, message = "Subject cannot exceed 255 characters")
    @Schema(description = "Message subject", example = "Project Collaboration Inquiry")
    private String subject;

    @NotBlank(message = "Message is required")
    @Size(min = 20, max = 2000, message = "Message must be between 20 and 2000 characters")
    @Schema(description = "Message body", example = "I would like to discuss a potential collaboration opportunity.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    @Schema(description = "Type of inquiry", example = "GENERAL")
    private InquiryType inquiryType;
}
