package com.caseyquinn.personal_website.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Encryption or decryption response")
public class EncryptionResponse {

    @Schema(description = "Resulting text after encryption or decryption")
    private String text;
}
