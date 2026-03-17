package com.caseyquinn.personal_website.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Validation error details")
public class ValidationErrorResponse {

    @Schema(description = "List of field-level validation errors")
    private List<FieldError> errors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Single field validation error")
    public static class FieldError {

        @Schema(description = "Field name that failed validation", example = "name")
        private String field;

        @Schema(description = "Validation error message", example = "must not be blank")
        private String message;
    }
}
