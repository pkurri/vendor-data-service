package com.vendor.vendordataservice.service;

import com.vendor.vendordataservice.api.dto.SearchRequest;
import com.vendor.vendordataservice.api.dto.SearchResponse;

/**
 * Service interface for court case data search
 */
public interface SearchService {
    /**
     * Search for court case records based on request criteria
     * @param request Search criteria
     * @return Complete API response envelope with case data
     */
    SearchResponse search(SearchRequest request);
}
