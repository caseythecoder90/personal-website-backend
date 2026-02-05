package com.caseyquinn.personal_website.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Encryption or decryption request")
public class EncryptionRequest {

    @NotBlank(message = "Text must not be blank")
    @Schema(description = "Text to encrypt or decrypt", example = "my-secret-value")
    private String text;
}
