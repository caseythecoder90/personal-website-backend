package com.caseyquinn.personal_website.dto.response;

import com.caseyquinn.personal_website.entity.enums.CertificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for certification data.
 */
@Data
@Builder
@Schema(description = "Certification information")
public class CertificationResponse {

    @Schema(description = "Certification ID", example = "1")
    private Long id;

    @Schema(description = "Certification name", example = "Oracle Certified Professional, Java SE 17 Developer")
    private String name;

    @Schema(description = "URL-friendly slug", example = "oracle-certified-professional-java-se-17-developer")
    private String slug;

    @Schema(description = "Organization that issued the certification", example = "Oracle")
    private String issuingOrganization;

    @Schema(description = "Certification credential ID or number", example = "OC1234567")
    private String credentialId;

    @Schema(description = "URL to verify the certification")
    private String credentialUrl;

    @Schema(description = "Date the certification was issued")
    private LocalDate issueDate;

    @Schema(description = "Date the certification expires")
    private LocalDate expirationDate;

    @Schema(description = "Current status of the certification")
    private CertificationStatus status;

    @Schema(description = "Detailed description of the certification")
    private String description;

    @Schema(description = "URL to the certification badge image")
    private String badgeUrl;

    @Schema(description = "Whether the certification is published", example = "true")
    private Boolean published;

    @Schema(description = "Whether the certification is featured", example = "false")
    private Boolean featured;

    @Schema(description = "Display order for sorting", example = "1")
    private Integer displayOrder;

    @Schema(description = "Technologies related to this certification")
    private List<TechnologyResponse> technologies;

    @Schema(description = "Certification creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Certification last update timestamp")
    private LocalDateTime updatedAt;
}
