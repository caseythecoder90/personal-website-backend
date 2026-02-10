package com.caseyquinn.personal_website.dto.request;

import com.caseyquinn.personal_website.entity.enums.SubmissionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for updating a contact submission status.
 */
@Data
@Schema(description = "Request to update a contact submission status")
public class UpdateContactStatusRequest {

    @NotNull(message = "Status is required")
    @Schema(description = "New submission status", example = "READ", requiredMode = Schema.RequiredMode.REQUIRED)
    private SubmissionStatus status;
}
