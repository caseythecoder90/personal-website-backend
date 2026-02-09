package com.caseyquinn.personal_website.security;

import com.caseyquinn.personal_website.config.RateLimitProperties;
import com.caseyquinn.personal_website.dto.response.Response;
import com.caseyquinn.personal_website.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Rate limiting filter using Bucket4j with per-IP, per-tier token buckets.
 * Classifies requests into login, admin, or public tiers with configurable limits.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitProperties rateLimitProperties;
    private final ObjectMapper objectMapper;

    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (!rateLimitProperties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = resolveClientIp(request);
        String tier = classifyRequest(request);
        String bucketKey = clientIp + ":" + tier;

        Bucket bucket = buckets.computeIfAbsent(bucketKey, key -> createBucket(tier));
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            long retryAfterSeconds = Duration.ofNanos(probe.getNanosToWaitForRefill()).toSeconds() + 1;
            log.warn("Rate limit exceeded for IP: {} on tier: {}", clientIp, tier);
            writeRateLimitResponse(response, retryAfterSeconds);
        }
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/actuator")
                || path.equals("/swagger-ui.html");
    }

    /**
     * Resolves the client IP address, checking X-Forwarded-For for reverse proxy support.
     *
     * @param request the HTTP request
     * @return the client IP address
     */
    private String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (isNotBlank(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Classifies the request into a rate limit tier based on path and HTTP method.
     *
     * @param request the HTTP request
     * @return the tier name: "login", "admin", or "public"
     */
    private String classifyRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (path.startsWith("/api/v1/auth/")) {
            return "login";
        }

        if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
            return "admin";
        }

        return "public";
    }

    /**
     * Creates a token bucket for the specified rate limit tier.
     *
     * @param tier the rate limit tier
     * @return a configured Bucket instance
     */
    private Bucket createBucket(String tier) {
        RateLimitProperties.Tier config = switch (tier) {
            case "login" -> rateLimitProperties.getLogin();
            case "admin" -> rateLimitProperties.getAdminApi();
            default -> rateLimitProperties.getPublicApi();
        };

        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(config.getRequests())
                        .refillGreedy(config.getRequests(), Duration.ofMinutes(config.getDurationMinutes()))
                        .build())
                .build();
    }

    /**
     * Writes a 429 Too Many Requests response with Retry-After header.
     *
     * @param response the HTTP response
     * @param retryAfterSeconds seconds until the client can retry
     * @throws IOException if writing the response fails
     */
    private void writeRateLimitResponse(HttpServletResponse response, long retryAfterSeconds) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Response<Void> errorResponse = Response.error(
                ErrorCode.RATE_LIMIT_EXCEEDED.getCode(),
                "Too many requests. Please try again in " + retryAfterSeconds + " seconds."
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
