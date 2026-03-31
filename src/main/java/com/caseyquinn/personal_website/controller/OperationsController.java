package com.caseyquinn.personal_website.controller;

import static com.caseyquinn.personal_website.constants.ResponseMessages.*;

import com.caseyquinn.personal_website.annotations.OperationsApiResponses;
import com.caseyquinn.personal_website.dto.request.EncryptionRequest;
import com.caseyquinn.personal_website.dto.response.EncryptionResponse;
import com.caseyquinn.personal_website.dto.response.HealthResponse;
import com.caseyquinn.personal_website.dto.response.Response;
import com.caseyquinn.personal_website.service.OperationsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for operational and utility endpoints including health checks and encryption utilities.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Operations", description = "Service health and utility operations")
public class OperationsController {

    private final OperationsService operationsService;

    /**
     * Retrieves the health status of the application.
     *
     * @return response entity containing health status information
     */
    @OperationsApiResponses.Health
    @GetMapping("/health")
    public ResponseEntity<Response<HealthResponse>> health() {
        return ResponseEntity.ok(Response.success(operationsService.getHealth(), SERVICE_HEALTHY));
    }

    /**
     * Encrypts the provided text using Jasypt encryption.
     *
     * @param request the encryption request containing text to encrypt
     * @return response entity containing encrypted text
     */
    @OperationsApiResponses.Encrypt
    @PostMapping("/operations/encrypt")
    public ResponseEntity<Response<EncryptionResponse>> encrypt(@Valid @RequestBody EncryptionRequest request) {
        return ResponseEntity.ok(Response.success(operationsService.encrypt(request.getText()), TEXT_ENCRYPTED));
    }

    /**
     * Decrypts the provided encrypted text using Jasypt decryption.
     *
     * @param request the decryption request containing text to decrypt
     * @return response entity containing decrypted text
     */
    @OperationsApiResponses.Decrypt
    @PostMapping("/operations/decrypt")
    public ResponseEntity<Response<EncryptionResponse>> decrypt(@Valid @RequestBody EncryptionRequest request) {
        return ResponseEntity.ok(Response.success(operationsService.decrypt(request.getText()), TEXT_DECRYPTED));
    }

    /**
     * Generates a BCrypt hash for the provided password.
     * Temporary utility endpoint for generating password hashes.
     *
     * @param request the request containing the password to hash
     * @return response entity containing BCrypt hash
     */
    @OperationsApiResponses.HashPassword
    @PostMapping("/operations/hash-password")
    public ResponseEntity<Response<EncryptionResponse>> hashPassword(@Valid @RequestBody EncryptionRequest request) {
        return ResponseEntity.ok(Response.success(operationsService.hashPassword(request.getText()), PASSWORD_HASHED));
    }
}
