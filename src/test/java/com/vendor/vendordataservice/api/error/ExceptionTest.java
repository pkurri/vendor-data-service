package com.vendor.vendordataservice.api.error;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for custom exceptions
 */
class ExceptionTest {

    @Test
    void apiBadRequestExceptionCreation() {
        ApiBadRequestException ex = new ApiBadRequestException("TEST_CODE", "Test message");
        assertThat(ex.getErrorCode()).isEqualTo("TEST_CODE");
        assertThat(ex.getMessage()).isEqualTo("Test message");
    }

    @Test
    void duplicateRequestIdExceptionCreation() {
        DuplicateRequestIdException ex = new DuplicateRequestIdException("req-123");
        assertThat(ex.getRequestId()).isEqualTo("req-123");
        assertThat(ex.getMessage()).contains("req-123");
    }

    @Test
    void tooManyRequestsExceptionCreation() {
        TooManyRequestsException ex = new TooManyRequestsException("Rate limit exceeded", 30);
        assertThat(ex.getMessage()).isEqualTo("Rate limit exceeded");
        assertThat(ex.getRetryAfterSeconds()).isEqualTo(30);
    }

    @Test
    void tooManyRequestsExceptionWithoutRetryAfter() {
        TooManyRequestsException ex = new TooManyRequestsException("Rate limit exceeded");
        assertThat(ex.getMessage()).isEqualTo("Rate limit exceeded");
        assertThat(ex.getRetryAfterSeconds()).isNull();
    }
}
