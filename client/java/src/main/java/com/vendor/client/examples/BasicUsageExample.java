package com.vendor.client.examples;

import com.vendor.client.VendorDataClient;
import com.vendor.client.config.ClientConfig;
import com.vendor.client.dto.SearchRequest;
import com.vendor.client.dto.SearchResponse;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * Basic usage example for the Vendor Data Service client.
 */
public class BasicUsageExample {
    
    public static void main(String[] args) {
        // Configure the client for vendor-auth-server (M2M OAuth2)
        ClientConfig config = ClientConfig.builder()
                .baseUrl("http://localhost:8081")  // vendor-data-service
                .clientId("m2m-client")            // Default M2M client
                .clientSecret("m2m-secret")        // Default M2M secret
                .tokenUrl("http://localhost:9000/oauth2/token")  // vendor-auth-server
                .scope("read write")               // M2M scopes
                .debugLogging(true)
                .build();
        
        // Create client instance (use try-with-resources for auto-close)
        try (VendorDataClient client = new VendorDataClient(config)) {
            
            // Check service health
            if (!client.healthCheck()) {
                System.err.println("Service is not healthy!");
                return;
            }
            
            // Example 1: Search by name and DOB
            SearchRequest request1 = SearchRequest.builder()
                    .nameFirst("John")
                    .nameLast("Doe")
                    .dob(LocalDate.of(1990, 1, 1))
                    .page(1)
                    .pageSize(50)
                    .build();
            
            SearchResponse response1 = client.search(request1);
            System.out.println("Found records: " + response1.getData().size());
            
            // Example 2: Search with case type filter
            SearchRequest request2 = SearchRequest.builder()
                    .nameLast("Smith")
                    .caseType(Arrays.asList("CF", "MM"))
                    .includeCharges(true)
                    .includeDefendants(true)
                    .build();
            
            SearchResponse response2 = client.searchGet(request2);
            response2.getData().forEach(caseRecord -> {
                System.out.println("Case: " + caseRecord.getCaseNumber());
                System.out.println("UCN: " + caseRecord.getUcn());
                System.out.println("Case Type: " + caseRecord.getCaseType());
            });
            
            // Example 3: Search with date range and county
            SearchRequest request3 = SearchRequest.builder()
                    .nameLast("Johnson")
                    .filedDateFrom(LocalDate.of(2024, 1, 1))
                    .filedDateTo(LocalDate.of(2024, 12, 31))
                    .countyCodes(Arrays.asList(37)) // Marion County code
                    .page(1)
                    .pageSize(100)
                    .build();
            
            SearchResponse response3 = client.search(request3);
            System.out.println("Cases filed in 2024: " + response3.getData().size());
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
