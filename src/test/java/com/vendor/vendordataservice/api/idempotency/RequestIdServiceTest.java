package com.vendor.vendordataservice.api.idempotency;

import com.vendor.vendordataservice.api.error.DuplicateRequestIdException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for RequestIdService
 */
class RequestIdServiceTest {

    private RequestIdService service;

    @BeforeEach
    void setUp() {
        service = new RequestIdService();
    }

    @Test
    void validateUniqueWithNewIdSucceeds() {
        assertDoesNotThrow(() -> service.validateUnique("req-001"));
    }

    @Test
    void validateUniqueWithDuplicateIdThrows() {
        service.validateUnique("req-002");
        
        DuplicateRequestIdException ex = assertThrows(
            DuplicateRequestIdException.class,
            () -> service.validateUnique("req-002")
        );
        
        assertThat(ex.getRequestId()).isEqualTo("req-002");
    }

    @Test
    void validateUniqueWithMultipleDifferentIdsSucceeds() {
        assertDoesNotThrow(() -> {
            service.validateUnique("req-001");
            service.validateUnique("req-002");
            service.validateUnique("req-003");
        });
    }

    @Test
    void validateUniqueWithNullIdSucceeds() {
        assertDoesNotThrow(() -> service.validateUnique(null));
    }

    @Test
    void validateUniqueWithEmptyIdSucceeds() {
        assertDoesNotThrow(() -> service.validateUnique(""));
    }
}
