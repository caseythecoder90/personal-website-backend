package com.caseyquinn.personal_website.service;

import com.caseyquinn.personal_website.dto.response.EncryptionResponse;
import com.caseyquinn.personal_website.dto.response.HealthResponse;
import com.caseyquinn.personal_website.exception.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Service providing operational utilities including health checks and encryption operations.
 */
@Service
@Slf4j
public class OperationsService {

    private final StringEncryptor stringEncryptor;
    private final Environment environment;

    /**
     * Constructs the OperationsService with required dependencies.
     *
     * @param stringEncryptor the Jasypt string encryptor
     * @param environment the Spring environment
     */
    public OperationsService(
            @Qualifier("jasyptStringEncryptor") StringEncryptor stringEncryptor,
            Environment environment) {
        this.stringEncryptor = stringEncryptor;
        this.environment = environment;
    }

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${app.version:1.0.0}")
    private String version;

    /**
     * Retrieves the current health status of the application.
     *
     * @return health response containing status, version, and environment information
     */
    public HealthResponse getHealth() {
        return HealthResponse.builder()
                .status("UP")
                .service(applicationName)
                .version(version)
                .environment(String.join(", ", environment.getActiveProfiles()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Encrypts plaintext using Jasypt encryption (non-production environments only).
     *
     * @param plaintext the text to encrypt
     * @return encryption response containing the encrypted text
     */
    public EncryptionResponse encrypt(String plaintext) {
        requireNonProductionProfile();
        log.info("Encrypting text");
        String encrypted = stringEncryptor.encrypt(plaintext);
        return EncryptionResponse.builder()
                .text("ENC(" + encrypted + ")")
                .build();
    }

    /**
     * Decrypts ciphertext using Jasypt decryption (non-production environments only).
     *
     * @param ciphertext the encrypted text to decrypt
     * @return encryption response containing the decrypted text
     */
    public EncryptionResponse decrypt(String ciphertext) {
        requireNonProductionProfile();
        log.info("Decrypting text");
        String input = ciphertext;
        if (input.startsWith("ENC(") && input.endsWith(")")) {
            input = input.substring(4, input.length() - 1);
        }
        String decrypted = stringEncryptor.decrypt(input);
        return EncryptionResponse.builder()
                .text(decrypted)
                .build();
    }

    /**
     * Generates a BCrypt hash for the provided password (non-production environments only).
     * Temporary utility for generating password hashes for database seeding.
     *
     * @param password the plain text password to hash
     * @return encryption response containing the BCrypt hash
     */
    public EncryptionResponse hashPassword(String password) {
        requireNonProductionProfile();
        log.info("Generating BCrypt hash for password");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(password);
        return EncryptionResponse.builder()
                .text(hash)
                .build();
    }

    private void requireNonProductionProfile() {
        if (Arrays.stream(environment.getActiveProfiles())
                .anyMatch(p -> p.equalsIgnoreCase("production"))) {
            throw new ForbiddenException("This endpoint is not available in production");
        }
    }
}
