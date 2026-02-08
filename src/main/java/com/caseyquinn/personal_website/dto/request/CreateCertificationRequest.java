package com.caseyquinn.personal_website.dto.request;

import com.caseyquinn.personal_website.entity.enums.CertificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

/**
 * Request to create a new certification.
 */
@Data
@Schema(description = "Request to create a new certification")
public class CreateCertificationRequest {

    @NotBlank(message = "Certification name is required")
    @Size(min = 2, max = 255, message = "Certification name must be between 2 and 255 characters")
    @Schema(description = "Certification name", example = "Oracle Certified Professional, Java SE 17 Developer", required = true)
    private String name;

    @NotBlank(message = "Issuing organization is required")
    @Size(max = 255, message = "Issuing organization cannot exceed 255 characters")
    @Schema(description = "Organization that issued the certification", example = "Oracle", required = true)
    private String issuingOrganization;

    @Size(max = 255, message = "Credential ID cannot exceed 255 characters")
    @Schema(description = "Certification credential ID or number", example = "OC1234567")
    private String credentialId;

    @Size(max = 500, message = "Credential URL cannot exceed 500 characters")
    @Schema(description = "URL to verify the certification", example = "https://catalog-education.oracle.com/pls/certview/sharebadge?id=...")
    private String credentialUrl;

    @Schema(description = "Date the certification was issued")
    private LocalDate issueDate;

    @Schema(description = "Date the certification expires (null if no expiration)")
    private LocalDate expirationDate;

    @NotNull(message = "Certification status is required")
    @Schema(description = "Current status of the certification", required = true)
    private CertificationStatus status;

    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    @Schema(description = "Detailed description of the certification")
    private String description;

    @Size(max = 500, message = "Badge URL cannot exceed 500 characters")
    @Schema(description = "URL to the certification badge image")
    private String badgeUrl;

    @Schema(description = "Whether the certification should be published", example = "false")
    private Boolean published;

    @Schema(description = "Whether the certification should be featured", example = "false")
    private Boolean featured;

    @Min(value = 0, message = "Display order cannot be negative")
    @Schema(description = "Display order for sorting", example = "1")
    private Integer displayOrder;

    @Schema(description = "Set of technology IDs to associate with this certification")
    private Set<Long> technologyIds;
}
