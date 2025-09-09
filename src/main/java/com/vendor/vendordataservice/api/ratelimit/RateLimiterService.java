package com.vendor.vendordataservice.api.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.vendor.vendordataservice.api.error.TooManyRequestsException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Very simple fixed-window rate limiter per key.
 * Allows up to limit requests per windowSeconds.
 */
@Service
public class RateLimiterService {
    private final Cache<String, AtomicInteger> counters;
    private final int limit;
    private final long windowSeconds;

    public RateLimiterService() {
        this(60, 30); // default: 60 req per 30s window per key
    }

    public RateLimiterService(int limit, long windowSeconds) {
        this.limit = limit;
        this.windowSeconds = windowSeconds;
        this.counters = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(windowSeconds))
                .maximumSize(100_000)
                .build();
    }

    public void check(String key) {
        AtomicInteger counter = counters.get(key, k -> new AtomicInteger(0));
        int val = counter.incrementAndGet();
        if (val > limit) {
            throw new TooManyRequestsException("Rate limit exceeded for this API key.", windowSeconds);
        }
    }
}
