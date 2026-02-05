package com.caseyquinn.personal_website.controller;

import com.caseyquinn.personal_website.annotations.OperationsApiResponses;
import com.caseyquinn.personal_website.dto.request.EncryptionRequest;
import com.caseyquinn.personal_website.dto.response.EncryptionResponse;
import com.caseyquinn.personal_website.dto.response.HealthResponse;
import com.caseyquinn.personal_website.dto.response.Response;
import com.caseyquinn.personal_website.service.OperationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Operations", description = "Service health and utility operations")
public class OperationsController {

    private final OperationsService operationsService;

    @OperationsApiResponses.Health
    @Operation(summary = "Health check", description = "Get service health status")
    @GetMapping("/health")
    public ResponseEntity<Response<HealthResponse>> health() {
        return ResponseEntity.ok(Response.success(operationsService.getHealth(), "Service is healthy"));
    }

    @OperationsApiResponses.Encrypt
    @Operation(summary = "Encrypt text",
            description = "Encrypt plaintext for use in property files. Returns an ENC(...) wrapped value. Not available in production.")
    @PostMapping("/operations/encrypt")
    public ResponseEntity<Response<EncryptionResponse>> encrypt(@Valid @RequestBody EncryptionRequest request) {
        return ResponseEntity.ok(Response.success(operationsService.encrypt(request.getText()), "Text encrypted successfully"));
    }

    @OperationsApiResponses.Decrypt
    @Operation(summary = "Decrypt text",
            description = "Decrypt an ENC(...) wrapped ciphertext back to plaintext. Not available in production.")
    @PostMapping("/operations/decrypt")
    public ResponseEntity<Response<EncryptionResponse>> decrypt(@Valid @RequestBody EncryptionRequest request) {
        return ResponseEntity.ok(Response.success(operationsService.decrypt(request.getText()), "Text decrypted successfully"));
    }
}
