package com.vendor.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Search request parameters for querying vendor data.
 * Matches the service API specification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchRequest {
    
    // Name fields
    @JsonProperty("name_last")
    private String nameLast;
    
    @JsonProperty("name_first")
    private String nameFirst;
    
    @JsonProperty("dob")
    private LocalDate dob;
    
    @JsonProperty("ssn_last4")
    private String ssnLast4;
    
    // Case type filters
    @JsonProperty("case_type")
    private List<String> caseType;
    
    @JsonProperty("filed_date_from")
    private LocalDate filedDateFrom;
    
    @JsonProperty("filed_date_to")
    private LocalDate filedDateTo;
    
    @JsonProperty("county_codes")
    private List<Integer> countyCodes;
    
    // Include flags
    @JsonProperty("include_dockets")
    @Builder.Default
    private Boolean includeDockets = false;
    
    @JsonProperty("include_charges")
    @Builder.Default
    private Boolean includeCharges = false;
    
    @JsonProperty("include_events")
    @Builder.Default
    private Boolean includeEvents = false;
    
    @JsonProperty("include_defendants")
    @Builder.Default
    private Boolean includeDefendants = true;
    
    // Pagination
    @JsonProperty("page")
    @Builder.Default
    private Integer page = 1;
    
    @JsonProperty("page_size")
    @Builder.Default
    private Integer pageSize = 100;
    
    // Sorting
    @JsonProperty("sort_by")
    private String sortBy;
    
    @JsonProperty("sort_dir")
    @Builder.Default
    private String sortDir = "asc";
    
    @JsonProperty("match_mode")
    @Builder.Default
    private String matchMode = "loose";
    
    @JsonProperty("client_request_id")
    private String clientRequestId;
    
    @JsonProperty("must_by")
    private Integer mustBy;
    
    @JsonProperty("test_by")
    private String testBy;
}
