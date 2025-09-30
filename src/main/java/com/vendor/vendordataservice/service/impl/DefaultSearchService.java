package com.vendor.vendordataservice.service.impl;

import com.vendor.vendordataservice.api.dto.CaseRecord;
import com.vendor.vendordataservice.api.dto.SearchRequest;
import com.vendor.vendordataservice.api.dto.SearchResponse;
import com.vendor.vendordataservice.api.error.ApiBadRequestException;
import com.vendor.vendordataservice.service.SearchService;
import com.vendor.vendordataservice.repository.mybatis.CaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of SearchService using MyBatis
 */
@Service
@RequiredArgsConstructor
public class DefaultSearchService implements SearchService {
    private final CaseMapper caseMapper;
    
    private static final String API_VERSION = "v1";

    @Override
    public SearchResponse search(SearchRequest request) {
        validateRequest(request);

        int page = request.getPage() != null ? request.getPage() : 1;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 100;

        // Use MyBatis to query cases with nested objects
        List<CaseRecord> caseRecords = caseMapper.searchCases(request);
        
        // Filter nested collections based on include flags
        if (!Boolean.TRUE.equals(request.getIncludeCharges())) {
            caseRecords.forEach(c -> c.setCharges(null));
        }
        if (!Boolean.TRUE.equals(request.getIncludeDockets())) {
            caseRecords.forEach(c -> c.setDockets(null));
        }
        if (!Boolean.TRUE.equals(request.getIncludeEvents())) {
            caseRecords.forEach(c -> c.setEvents(null));
        }
        if (!Boolean.TRUE.equals(request.getIncludeDefendants())) {
            caseRecords.forEach(c -> c.setDefendants(null));
        }
        
        // Build response envelope
        SearchResponse response = SearchResponse.builder()
                .apiVersion(API_VERSION)
                .clientRequestId(request.getClientRequestId())
                .generatedAt(OffsetDateTime.now())
                .page(page)
                .pageSize(pageSize)
                .totalRecordsIsEstimate(true)
                .warnings(new ArrayList<>())
                .data(caseRecords)
                .build();
        
        return response;
    }

    private static String nullSafeUpper(String s) { return s == null ? "" : s.toUpperCase(); }
    private static String combine(String a, String b) {
        if (notBlank(a) && notBlank(b)) return a + "-" + b;
        if (notBlank(a)) return a;
        if (notBlank(b)) return b;
        return null;
    }
    private static boolean notBlank(String s) { return s != null && !s.isBlank(); }

    private void validateRequest(SearchRequest req) {
        boolean hasName = notBlank(req.getNameFirst()) || notBlank(req.getNameLast());
        boolean hasDob = req.getDob() != null;
        boolean hasSsn = notBlank(req.getSsnLast4());
        if (!(hasName || hasDob || hasSsn)) {
            throw new ApiBadRequestException("MISSING_SEARCH_KEY", "Provide at least one search key (name_last + name_first, dob, or ssn_last4)");
        }
        if (req.getPage() != null && req.getPage() < 1) {
            throw new ApiBadRequestException("INVALID_PAGINATION", "page must be >= 1");
        }
        if (req.getPageSize() != null && (req.getPageSize() < 1 || req.getPageSize() > 500)) {
            throw new ApiBadRequestException("INVALID_PAGINATION", "page_size must be between 1 and 500");
        }
    }
}
