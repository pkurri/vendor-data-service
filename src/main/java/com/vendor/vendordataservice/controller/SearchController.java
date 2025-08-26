package com.vendor.vendordataservice.controller;

import com.vendor.vendordataservice.api.dto.SearchRequest;
import com.vendor.vendordataservice.api.dto.SearchResponse;
import com.vendor.vendordataservice.service.SearchService;
import jakarta.validation.Valid;
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

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public List<SearchResponse> getSearch(
            @RequestParam(name = "ssnLast4", required = false) String ssnLast4,
            @RequestParam(name = "dob", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dob,
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "middleName", required = false) String middleName,
            @RequestParam(name = "lastName", required = false) String lastName
    ) {
        SearchRequest req = new SearchRequest();
        req.setSsnLast4(ssnLast4);
        req.setDob(dob);
        req.setFirstName(firstName);
        req.setMiddleName(middleName);
        req.setLastName(lastName);
        return searchService.search(req);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<SearchResponse> postSearch(@Valid @RequestBody SearchRequest request) {
        return searchService.search(request);
    }
}
