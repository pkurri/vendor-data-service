package com.vendor.vendordataservice.api;

import com.vendor.vendordataservice.api.dto.ErrorResponse;
import com.vendor.vendordataservice.api.error.ApiBadRequestException;
import com.vendor.vendordataservice.api.error.DuplicateRequestIdException;
import com.vendor.vendordataservice.api.error.TooManyRequestsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for GlobalExceptionHandler
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleApiBadRequestException() {
        ApiBadRequestException ex = new ApiBadRequestException("INVALID_INPUT", "Invalid input provided");
        
        ResponseEntity<ErrorResponse> response = handler.handleApiBadRequest(ex);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("INVALID_INPUT");
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid input provided");
        assertThat(response.getBody().getStatus()).isEqualTo(400);
    }

    @Test
    void handleDuplicateRequestIdException() {
        DuplicateRequestIdException ex = new DuplicateRequestIdException("req-123");
        
        ResponseEntity<ErrorResponse> response = handler.handleDuplicateRequestId(ex);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("DUPLICATE_REQUEST_ID");
        assertThat(response.getBody().getStatus()).isEqualTo(409);
    }

    @Test
    void handleTooManyRequestsException() {
        TooManyRequestsException ex = new TooManyRequestsException("Rate limit exceeded", 30);
        
        ResponseEntity<ErrorResponse> response = handler.handleTooManyRequests(ex);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("RATE_LIMIT_EXCEEDED");
        assertThat(response.getBody().getStatus()).isEqualTo(429);
        assertThat(response.getHeaders().get("Retry-After")).containsExactly("30");
    }

    @Test
    void handleTooManyRequestsExceptionWithoutRetryAfter() {
        TooManyRequestsException ex = new TooManyRequestsException("Rate limit exceeded");
        
        ResponseEntity<ErrorResponse> response = handler.handleTooManyRequests(ex);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(response.getHeaders().get("Retry-After")).isNull();
    }

    @Test
    void handleValidationException() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("searchRequest", "page", "must be greater than 0");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        
        ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().getStatus()).isEqualTo(400);
    }

    @Test
    void handleGenericException() {
        Exception ex = new RuntimeException("Unexpected error");
        
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getBody().getStatus()).isEqualTo(500);
    }
}
