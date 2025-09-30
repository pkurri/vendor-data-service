package com.vendor.vendordataservice.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Search request with all query parameters from API specification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {
    
    // Name fields
    @JsonProperty("name_last")
    @Size(max = 30)
    private String nameLast;
    
    @JsonProperty("name_first")
    @Size(max = 20)
    private String nameFirst;
    
    @JsonProperty("dob")
    private LocalDate dob;
    
    @JsonProperty("ssn_last4")
    @Pattern(regexp = "^$|^[0-9]{4}$", message = "ssn_last4 must be exactly 4 digits")
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
    private Boolean includeDockets = false;
    
    @JsonProperty("include_charges")
    private Boolean includeCharges = false;
    
    @JsonProperty("include_events")
    private Boolean includeEvents = false;
    
    @JsonProperty("include_defendants")
    private Boolean includeDefendants = true;
    
    // Pagination
    @JsonProperty("page")
    @Min(value = 1, message = "page must be >= 1")
    private Integer page = 1;
    
    @JsonProperty("page_size")
    @Min(value = 1, message = "page_size must be >= 1")
    @Max(value = 500, message = "page_size must be <= 500")
    private Integer pageSize = 100;
    
    // Sorting
    @JsonProperty("sort_by")
    private String sortBy;
    
    @JsonProperty("sort_dir")
    private String sortDir = "asc";
    
    @JsonProperty("match_mode")
    private String matchMode = "loose";
    
    @JsonProperty("client_request_id")
    private String clientRequestId;
    
    @JsonProperty("must_by")
    private Integer mustBy;
    
    @JsonProperty("test_by")
    private String testBy;
}
