package com.vendor.vendordataservice.controller;

import com.vendor.vendordataservice.api.dto.SearchRequest;
import com.vendor.vendordataservice.api.dto.SearchResponse;
import com.vendor.vendordataservice.api.idempotency.RequestIdService;
import com.vendor.vendordataservice.api.ratelimit.RateLimiterService;
import com.vendor.vendordataservice.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Court case data search API controller
 * Secured with JWT authentication from vendor-auth-service
 */
@RestController
@RequestMapping(path = "/api/v1/search", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@PreAuthorize("hasAuthority('SCOPE_vendor.search')")
@Tag(name = "Search API", description = "Court case data search endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class SearchController {

    private final SearchService searchService;
    private final RequestIdService requestIdService;
    private final RateLimiterService rateLimiterService;

    public SearchController(SearchService searchService,
                            RequestIdService requestIdService,
                            RateLimiterService rateLimiterService) {
        this.searchService = searchService;
        this.requestIdService = requestIdService;
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping
    @Operation(summary = "Search court cases via query parameters", 
               description = "Search for court case records using name, DOB, SSN last 4, and other filters")
    public SearchResponse getSearch(
            @RequestHeader(name = "X-Request-Id", required = false) 
            @Parameter(description = "Client-generated unique request ID for idempotency") 
            String requestId,
            
            @RequestParam(name = "name_last", required = false) 
            @Parameter(description = "Last name (uppercase/normalized)") 
            String nameLast,
            
            @RequestParam(name = "name_first", required = false) 
            @Parameter(description = "First name") 
            String nameFirst,
            
            @RequestParam(name = "dob", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Date of birth (YYYY-MM-DD)") 
            LocalDate dob,
            
            @RequestParam(name = "ssn_last4", required = false) 
            @Pattern(regexp = "^$|^[0-9]{4}$")
            @Parameter(description = "Last 4 digits of SSN") 
            String ssnLast4,
            
            @RequestParam(name = "filed_date_from", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Filed date from (YYYY-MM-DD)") 
            LocalDate filedDateFrom,
            
            @RequestParam(name = "filed_date_to", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Filed date to (YYYY-MM-DD)") 
            LocalDate filedDateTo,
            
            @RequestParam(name = "county_codes", required = false)
            @Parameter(description = "County codes (0-67)") 
            List<Integer> countyCodes,
            
            @RequestParam(name = "case_type", required = false)
            @Parameter(description = "Case types to filter") 
            List<String> caseType,
            
            @RequestParam(name = "include_dockets", required = false, defaultValue = "false")
            @Parameter(description = "Include docket entries") 
            Boolean includeDockets,
            
            @RequestParam(name = "include_charges", required = false, defaultValue = "false")
            @Parameter(description = "Include charges") 
            Boolean includeCharges,
            
            @RequestParam(name = "include_events", required = false, defaultValue = "false")
            @Parameter(description = "Include court events") 
            Boolean includeEvents,
            
            @RequestParam(name = "include_defendants", required = false, defaultValue = "true")
            @Parameter(description = "Include defendants") 
            Boolean includeDefendants,
            
            @RequestParam(name = "page", required = false, defaultValue = "1") 
            @Min(1) 
            @Parameter(description = "Page number (1-based)") 
            Integer page,
            
            @RequestParam(name = "page_size", required = false, defaultValue = "100") 
            @Min(1) @Max(500)
            @Parameter(description = "Page size (max 500)") 
            Integer pageSize,
            
            @RequestParam(name = "sort_by", required = false)
            @Parameter(description = "Sort field") 
            String sortBy,
            
            @RequestParam(name = "sort_dir", required = false, defaultValue = "asc")
            @Parameter(description = "Sort direction (asc/desc)") 
            String sortDir,
            
            @RequestParam(name = "match_mode", required = false, defaultValue = "loose")
            @Parameter(description = "Match mode (loose/exact)") 
            String matchMode
    ) {
        if (requestId != null) {
            requestIdService.validateUnique(requestId);
            rateLimiterService.check("search:" + requestId);
        }
        
        SearchRequest req = SearchRequest.builder()
                .nameLast(nameLast)
                .nameFirst(nameFirst)
                .dob(dob)
                .ssnLast4(ssnLast4)
                .filedDateFrom(filedDateFrom)
                .filedDateTo(filedDateTo)
                .countyCodes(countyCodes)
                .caseType(caseType)
                .includeDockets(includeDockets)
                .includeCharges(includeCharges)
                .includeEvents(includeEvents)
                .includeDefendants(includeDefendants)
                .page(page)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .matchMode(matchMode)
                .clientRequestId(requestId)
                .build();
        
        return searchService.search(req);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Search court cases via JSON body", 
               description = "Search for court case records using JSON request body")
    public SearchResponse postSearch(
            @RequestHeader(name = "X-Request-Id", required = false) 
            @Parameter(description = "Client-generated unique request ID") 
            String requestId,
            
            @Valid @RequestBody SearchRequest request) {
        
        if (requestId != null) {
            requestIdService.validateUnique(requestId);
            rateLimiterService.check("search:" + requestId);
        }
        
        // Set client request ID from header if not in body
        if (request.getClientRequestId() == null && requestId != null) {
            request.setClientRequestId(requestId);
        }
        
        return searchService.search(request);
    }
}
