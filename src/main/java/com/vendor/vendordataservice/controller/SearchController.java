package com.vendor.vendordataservice.controller;

import com.vendor.vendordataservice.api.dto.SearchRequest;
import com.vendor.vendordataservice.api.dto.SearchResponse;
import com.vendor.vendordataservice.api.idempotency.RequestIdService;
import com.vendor.vendordataservice.api.ratelimit.RateLimiterService;
import com.vendor.vendordataservice.service.SearchService;
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

@RestController
@RequestMapping(path = "/api/v1/search", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@PreAuthorize("hasAuthority('SCOPE_vendor.search')")
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
    public List<SearchResponse> getSearch(
            @RequestHeader(name = "X-Request-Id") String requestId,
            @RequestParam(name = "ssnLast4", required = false) @Pattern(regexp = "^$|^[0-9]{4}$") String ssnLast4,
            @RequestParam(name = "dob", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dob,
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "middleName", required = false) String middleName,
            @RequestParam(name = "lastName", required = false) String lastName,
            @RequestParam(name = "page", required = false, defaultValue = "1") @Min(1) Integer page,
            @RequestParam(name = "pageSize", required = false, defaultValue = "50") @Min(1) @Max(50) Integer pageSize
    ) {
        requestIdService.validateUnique(requestId);
        rateLimiterService.check("search:" + requestId); // simple per-request limiter key
        SearchRequest req = new SearchRequest();
        req.setSsnLast4(ssnLast4);
        req.setDob(dob);
        req.setFirstName(firstName);
        req.setMiddleName(middleName);
        req.setLastName(lastName);
        req.setPage(page);
        req.setPageSize(pageSize);
        return searchService.search(req);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<SearchResponse> postSearch(@RequestHeader(name = "X-Request-Id") String requestId,
                                           @Valid @RequestBody SearchRequest request) {
        requestIdService.validateUnique(requestId);
        rateLimiterService.check("search:" + requestId);
        return searchService.search(request);
    }
}
