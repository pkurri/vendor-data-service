package com.vendor.client.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendor.client.config.ClientConfig;
import com.vendor.client.exception.VendorClientException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

/**
 * Manages OAuth2 access tokens for M2M authentication.
 * Handles token acquisition and automatic refresh.
 */
public class OAuth2TokenManager {
    
    private static final Logger logger = LoggerFactory.getLogger(OAuth2TokenManager.class);
    private static final MediaType FORM_URLENCODED = MediaType.get("application/x-www-form-urlencoded");
    
    private final ClientConfig config;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    private volatile String accessToken;
    private volatile Instant tokenExpiry;
    
    public OAuth2TokenManager(ClientConfig config, OkHttpClient httpClient, ObjectMapper objectMapper) {
        this.config = config;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Gets a valid access token, refreshing if necessary.
     *
     * @return Valid access token
     * @throws VendorClientException if token acquisition fails
     */
    public synchronized String getAccessToken() throws VendorClientException {
        if (needsRefresh()) {
            refreshToken();
        }
        return accessToken;
    }
    
    /**
     * Forces a token refresh.
     *
     * @throws VendorClientException if token refresh fails
     */
    public synchronized void refreshToken() throws VendorClientException {
        logger.debug("Refreshing OAuth2 token");
        
        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", config.getClientId())
                .add("client_secret", config.getClientSecret())
                .add("scope", config.getScope())
                .build();
        
        Request request = new Request.Builder()
                .url(config.getTokenUrl())
                .post(formBody)
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                throw new VendorClientException(
                        String.format("Token request failed with status %d: %s", response.code(), errorBody)
                );
            }
            
            String responseBody = response.body().string();
            Map<String, Object> tokenResponse = objectMapper.readValue(responseBody, Map.class);
            
            this.accessToken = (String) tokenResponse.get("access_token");
            Integer expiresIn = (Integer) tokenResponse.get("expires_in");
            
            if (this.accessToken == null) {
                throw new VendorClientException("Token response missing access_token");
            }
            
            // Set expiry with buffer
            if (expiresIn != null) {
                this.tokenExpiry = Instant.now().plusSeconds(expiresIn - config.getTokenRefreshBuffer());
            } else {
                // Default to 1 hour if not specified
                this.tokenExpiry = Instant.now().plusSeconds(3600 - config.getTokenRefreshBuffer());
            }
            
            logger.debug("Token refreshed successfully, expires at {}", tokenExpiry);
            
        } catch (IOException e) {
            throw new VendorClientException("Failed to refresh token", e);
        }
    }
    
    private boolean needsRefresh() {
        return accessToken == null || tokenExpiry == null || Instant.now().isAfter(tokenExpiry);
    }
}
