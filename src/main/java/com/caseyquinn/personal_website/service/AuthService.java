package com.caseyquinn.personal_website.service;

import com.caseyquinn.personal_website.dto.request.LoginRequest;
import com.caseyquinn.personal_website.dto.response.AuthResponse;
import com.caseyquinn.personal_website.entity.User;
import com.caseyquinn.personal_website.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service handling user authentication operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Authenticates user credentials and generates a JWT token.
     *
     * @param request the login request containing username and password
     * @return authentication response with JWT token and user details
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        User user = (User) authentication.getPrincipal();

        log.info("User logged in successfully: {}", user.getUsername());

        return AuthResponse.builder()
                .token(jwt)
                .expiresIn(jwtExpiration)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}
