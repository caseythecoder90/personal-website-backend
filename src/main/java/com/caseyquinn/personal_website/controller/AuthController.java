package com.caseyquinn.personal_website.controller;

import com.caseyquinn.personal_website.annotations.AuthApiResponses;
import com.caseyquinn.personal_website.dto.request.LoginRequest;
import com.caseyquinn.personal_website.dto.response.AuthResponse;
import com.caseyquinn.personal_website.dto.response.Response;
import com.caseyquinn.personal_website.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user authentication.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Authentication", description = "User authentication endpoints")
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticates user and returns JWT token.
     *
     * @param request login credentials
     * @return response containing JWT token and user details
     */
    @AuthApiResponses.Login
    @PostMapping("/login")
    public ResponseEntity<Response<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Processing login request for user: {}", request.getUsername());
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(Response.success(authResponse, "Login successful"));
    }
}
