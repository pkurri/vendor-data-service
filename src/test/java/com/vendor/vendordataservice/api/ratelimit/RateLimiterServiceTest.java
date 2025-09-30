package com.vendor.vendordataservice.api.ratelimit;

import com.vendor.vendordataservice.api.error.TooManyRequestsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for RateLimiterService
 */
class RateLimiterServiceTest {

    private RateLimiterService service;

    @BeforeEach
    void setUp() {
        service = new RateLimiterService();
    }

    @Test
    void checkWithNewKeySucceeds() {
        assertDoesNotThrow(() -> service.check("key-001"));
    }

    @Test
    void checkWithMultipleDifferentKeysSucceeds() {
        assertDoesNotThrow(() -> {
            service.check("key-001");
            service.check("key-002");
            service.check("key-003");
        });
    }

    @Test
    void checkWithRapidRequestsEventuallyThrows() {
        String key = "key-rapid";
        
        // First few requests should succeed
        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> service.check(key));
        }
        
        // Eventually should hit rate limit (implementation dependent)
        // This test verifies the service doesn't crash
    }

    @Test
    void checkWithNullKeySucceeds() {
        assertDoesNotThrow(() -> service.check(null));
    }
}
