package com.vendor.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vendor.client.auth.OAuth2TokenManager;
import com.vendor.client.config.ClientConfig;
import com.vendor.client.dto.SearchRequest;
import com.vendor.client.dto.SearchResponse;
import com.vendor.client.exception.VendorClientException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Main client for interacting with the Vendor Data Service API.
 * Supports M2M authentication via OAuth2 Client Credentials flow.
 */
public class VendorDataClient implements AutoCloseable {
    
    private static final Logger logger = LoggerFactory.getLogger(VendorDataClient.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    private final ClientConfig config;
    private final OkHttpClient httpClient;
    private final OAuth2TokenManager tokenManager;
    private final ObjectMapper objectMapper;
    
    /**
     * Creates a new VendorDataClient with the specified configuration.
     *
     * @param config Client configuration
     */
    public VendorDataClient(ClientConfig config) {
        this.config = config;
        this.objectMapper = createObjectMapper();
        this.httpClient = createHttpClient();
        this.tokenManager = new OAuth2TokenManager(config, httpClient, objectMapper);
    }
    
    /**
     * Performs a search using GET method with query parameters.
     *
     * @param request Search request parameters
     * @return Search response with results
     * @throws VendorClientException if the request fails
     */
    public SearchResponse searchGet(SearchRequest request) throws VendorClientException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(config.getBaseUrl() + "/api/v1/search").newBuilder();
        
        // Add query parameters matching service API
        if (request.getNameFirst() != null) {
            urlBuilder.addQueryParameter("name_first", request.getNameFirst());
        }
        if (request.getNameLast() != null) {
            urlBuilder.addQueryParameter("name_last", request.getNameLast());
        }
        if (request.getDob() != null) {
            urlBuilder.addQueryParameter("dob", request.getDob().toString());
        }
        if (request.getSsnLast4() != null) {
            urlBuilder.addQueryParameter("ssn_last4", request.getSsnLast4());
        }
        if (request.getCaseType() != null && !request.getCaseType().isEmpty()) {
            request.getCaseType().forEach(ct -> 
                urlBuilder.addQueryParameter("case_type", ct));
        }
        if (request.getFiledDateFrom() != null) {
            urlBuilder.addQueryParameter("filed_date_from", request.getFiledDateFrom().toString());
        }
        if (request.getFiledDateTo() != null) {
            urlBuilder.addQueryParameter("filed_date_to", request.getFiledDateTo().toString());
        }
        if (request.getCountyCodes() != null && !request.getCountyCodes().isEmpty()) {
            request.getCountyCodes().forEach(cc -> 
                urlBuilder.addQueryParameter("county_codes", cc.toString()));
        }
        if (request.getIncludeDockets() != null) {
            urlBuilder.addQueryParameter("include_dockets", request.getIncludeDockets().toString());
        }
        if (request.getIncludeCharges() != null) {
            urlBuilder.addQueryParameter("include_charges", request.getIncludeCharges().toString());
        }
        if (request.getIncludeEvents() != null) {
            urlBuilder.addQueryParameter("include_events", request.getIncludeEvents().toString());
        }
        if (request.getIncludeDefendants() != null) {
            urlBuilder.addQueryParameter("include_defendants", request.getIncludeDefendants().toString());
        }
        if (request.getPage() != null) {
            urlBuilder.addQueryParameter("page", request.getPage().toString());
        }
        if (request.getPageSize() != null) {
            urlBuilder.addQueryParameter("page_size", request.getPageSize().toString());
        }
        if (request.getSortBy() != null) {
            urlBuilder.addQueryParameter("sort_by", request.getSortBy());
        }
        if (request.getSortDir() != null) {
            urlBuilder.addQueryParameter("sort_dir", request.getSortDir());
        }
        if (request.getMatchMode() != null) {
            urlBuilder.addQueryParameter("match_mode", request.getMatchMode());
        }
        if (request.getClientRequestId() != null) {
            urlBuilder.addQueryParameter("client_request_id", request.getClientRequestId());
        }
        
        Request httpRequest = new Request.Builder()
                .url(urlBuilder.build())
                .header("Authorization", "Bearer " + tokenManager.getAccessToken())
                .header("X-Request-Id", UUID.randomUUID().toString())
                .get()
                .build();
        
        return executeRequest(httpRequest);
    }
    
    /**
     * Performs a search using POST method with JSON body.
     *
     * @param request Search request parameters
     * @return Search response with results
     * @throws VendorClientException if the request fails
     */
    public SearchResponse search(SearchRequest request) throws VendorClientException {
        try {
            String json = objectMapper.writeValueAsString(request);
            RequestBody body = RequestBody.create(json, JSON);
            
            Request httpRequest = new Request.Builder()
                    .url(config.getBaseUrl() + "/api/v1/search")
                    .header("Authorization", "Bearer " + tokenManager.getAccessToken())
                    .header("X-Request-Id", UUID.randomUUID().toString())
                    .post(body)
                    .build();
            
            return executeRequest(httpRequest);
        } catch (IOException e) {
            throw new VendorClientException("Failed to serialize request", e);
        }
    }
    
    /**
     * Checks the health status of the service.
     *
     * @return true if the service is healthy
     */
    public boolean healthCheck() {
        try {
            Request request = new Request.Builder()
                    .url(config.getBaseUrl() + "/actuator/health")
                    .get()
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (IOException e) {
            logger.error("Health check failed", e);
            return false;
        }
    }
    
    private SearchResponse executeRequest(Request request) throws VendorClientException {
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts < config.getMaxRetries()) {
            try {
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "No error body";
                        
                        // Handle token expiration
                        if (response.code() == 401) {
                            tokenManager.refreshToken();
                            // Retry with new token
                            Request newRequest = request.newBuilder()
                                    .header("Authorization", "Bearer " + tokenManager.getAccessToken())
                                    .build();
                            return executeRequest(newRequest);
                        }
                        
                        throw new VendorClientException(
                                String.format("Request failed with status %d: %s", response.code(), errorBody)
                        );
                    }
                    
                    String responseBody = response.body().string();
                    return objectMapper.readValue(responseBody, SearchResponse.class);
                }
            } catch (IOException e) {
                lastException = e;
                attempts++;
                
                if (attempts < config.getMaxRetries()) {
                    long delay = config.getRetryDelay() * (long) Math.pow(2, attempts - 1);
                    logger.warn("Request failed, retrying in {}ms (attempt {}/{})", 
                            delay, attempts, config.getMaxRetries());
                    
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new VendorClientException("Request interrupted", ie);
                    }
                }
            }
        }
        
        throw new VendorClientException(
                String.format("Request failed after %d attempts", config.getMaxRetries()), 
                lastException
        );
    }
    
    private OkHttpClient createHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofMillis(config.getConnectTimeout()))
                .readTimeout(Duration.ofMillis(config.getReadTimeout()))
                .writeTimeout(Duration.ofMillis(config.getWriteTimeout()))
                .connectionPool(new ConnectionPool(
                        config.getMaxIdleConnections(),
                        config.getKeepAliveDuration(),
                        TimeUnit.MILLISECONDS
                ));
        
        if (config.isDebugLogging()) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(logger::debug);
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }
        
        return builder.build();
    }
    
    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
    
    @Override
    public void close() {
        if (httpClient != null) {
            httpClient.dispatcher().executorService().shutdown();
            httpClient.connectionPool().evictAll();
        }
    }
}
