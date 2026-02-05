package com.caseyquinn.personal_website.controller;

import com.caseyquinn.personal_website.annotations.OperationsApiResponses;
import com.caseyquinn.personal_website.dto.request.EncryptionRequest;
import com.caseyquinn.personal_website.dto.response.EncryptionResponse;
import com.caseyquinn.personal_website.dto.response.HealthResponse;
import com.caseyquinn.personal_website.dto.response.Response;
import com.caseyquinn.personal_website.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.text.TextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Operations", description = "Service health and utility operations")
public class OperationsController {

    private final Environment environment;
    private final TextEncryptor textEncryptor;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${app.version:1.0.0}")
    private String version;

    @OperationsApiResponses.Health
    @Operation(summary = "Health check", description = "Get service health status")
    @GetMapping("/health")
    public ResponseEntity<Response<HealthResponse>> health() {
        log.debug("Health check requested");

        HealthResponse health = HealthResponse.builder()
                .status("UP")
                .service(applicationName)
                .version(version)
                .environment(String.join(", ", environment.getActiveProfiles()))
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(Response.success(health, "Service is healthy"));
    }

    @OperationsApiResponses.Encrypt
    @Operation(summary = "Encrypt text",
            description = "Encrypt plaintext for use in property files. Returns an ENC(...) wrapped value. Not available in production.")
    @PostMapping("/operations/encrypt")
    public ResponseEntity<Response<EncryptionResponse>> encrypt(@Valid @RequestBody EncryptionRequest request) {
        if (isProductionProfile()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Response.error(ErrorCode.FORBIDDEN.getCode(), "This endpoint is not available in production"));
        }
        log.info("Encrypting text");
        String encrypted = textEncryptor.encrypt(request.getText());
        return ResponseEntity.ok(Response.success(
                EncryptionResponse.builder().text("ENC(" + encrypted + ")").build(),
                "Text encrypted successfully"));
    }

    @OperationsApiResponses.Decrypt
    @Operation(summary = "Decrypt text",
            description = "Decrypt an ENC(...) wrapped ciphertext back to plaintext. Not available in production.")
    @PostMapping("/operations/decrypt")
    public ResponseEntity<Response<EncryptionResponse>> decrypt(@Valid @RequestBody EncryptionRequest request) {
        if (isProductionProfile()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Response.error(ErrorCode.FORBIDDEN.getCode(), "This endpoint is not available in production"));
        }
        log.info("Decrypting text");
        String input = request.getText();
        if (input.startsWith("ENC(") && input.endsWith(")")) {
            input = input.substring(4, input.length() - 1);
        }
        String decrypted = textEncryptor.decrypt(input);
        return ResponseEntity.ok(Response.success(
                EncryptionResponse.builder().text(decrypted).build(),
                "Text decrypted successfully"));
    }

    private boolean isProductionProfile() {
        return Arrays.stream(environment.getActiveProfiles())
                .anyMatch(p -> p.equalsIgnoreCase("production"));
    }
}
