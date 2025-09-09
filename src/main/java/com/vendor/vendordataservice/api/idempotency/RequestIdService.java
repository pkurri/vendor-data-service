package com.vendor.vendordataservice.api.idempotency;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.vendor.vendordataservice.api.error.DuplicateRequestIdException;
import com.vendor.vendordataservice.api.error.ApiBadRequestException;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RequestIdService {
    private final Cache<String, Boolean> cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(10))
            .maximumSize(100_000)
            .build();

    public void validateUnique(String requestId) {
        if (requestId == null || requestId.isBlank()) {
            throw new ApiBadRequestException("MISSING_REQUEST_ID", "Field 'request_id' is required (header X-Request-Id)");
        }
        Boolean prev = cache.asMap().putIfAbsent(requestId, Boolean.TRUE);
        if (prev != null) {
            throw new DuplicateRequestIdException(requestId);
        }
    }
}
