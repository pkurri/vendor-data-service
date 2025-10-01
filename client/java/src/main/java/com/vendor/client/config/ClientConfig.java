package com.vendor.client.config;

import lombok.Builder;
import lombok.Data;

/**
 * Configuration for the Vendor Data Service client.
 */
@Data
@Builder
public class ClientConfig {
    
    /**
     * Base URL of the Vendor Data Service API
     */
    private final String baseUrl;
    
    /**
     * OAuth2 client ID for M2M authentication
     */
    private final String clientId;
    
    /**
     * OAuth2 client secret for M2M authentication
     */
    private final String clientSecret;
    
    /**
     * OAuth2 token endpoint URL
     */
    private final String tokenUrl;
    
    /**
     * OAuth2 scope (default: "vendor.search")
     */
    @Builder.Default
    private final String scope = "vendor.search";
    
    /**
     * Connection timeout in milliseconds (default: 30000)
     */
    @Builder.Default
    private final int connectTimeout = 30000;
    
    /**
     * Read timeout in milliseconds (default: 60000)
     */
    @Builder.Default
    private final int readTimeout = 60000;
    
    /**
     * Write timeout in milliseconds (default: 60000)
     */
    @Builder.Default
    private final int writeTimeout = 60000;
    
    /**
     * Maximum number of retry attempts (default: 3)
     */
    @Builder.Default
    private final int maxRetries = 3;
    
    /**
     * Initial retry delay in milliseconds (default: 1000)
     * Uses exponential backoff
     */
    @Builder.Default
    private final long retryDelay = 1000;
    
    /**
     * Maximum idle connections in the pool (default: 5)
     */
    @Builder.Default
    private final int maxIdleConnections = 5;
    
    /**
     * Keep-alive duration for connections in milliseconds (default: 300000)
     */
    @Builder.Default
    private final long keepAliveDuration = 300000;
    
    /**
     * Enable debug logging (default: false)
     */
    @Builder.Default
    private final boolean debugLogging = false;
    
    /**
     * Token refresh buffer in seconds (default: 60)
     * Refresh token this many seconds before expiration
     */
    @Builder.Default
    private final int tokenRefreshBuffer = 60;
}
