package com.caseyquinn.personal_website.exception.data;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Retries on transient database connection failures with exponential backoff.
 * Non-transient exceptions (e.g. DataIntegrityViolationException, NotFoundException)
 * propagate immediately without retry.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Retryable(
        retryFor = {DataAccessResourceFailureException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2))
public @interface RetryableDataAccess {
}
