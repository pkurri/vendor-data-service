package com.vendor.vendordataservice.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Complete API response envelope matching JSON schema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {
    
    @JsonProperty("api_version")
    private String apiVersion;
    
    @JsonProperty("client_request_id")
    private String clientRequestId;
    
    @JsonProperty("generated_at")
    private OffsetDateTime generatedAt;
    
    @JsonProperty("page")
    private Integer page;
    
    @JsonProperty("page_size")
    private Integer pageSize;
    
    @JsonProperty("total_records_is_estimate")
    private Boolean totalRecordsIsEstimate;
    
    @JsonProperty("warnings")
    private List<String> warnings;
    
    @JsonProperty("data")
    @Builder.Default
    private List<CaseRecord> data = new ArrayList<>();
}
