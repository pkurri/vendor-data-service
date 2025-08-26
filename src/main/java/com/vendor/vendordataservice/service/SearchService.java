package com.vendor.vendordataservice.service;

import com.vendor.vendordataservice.api.dto.SearchRequest;
import com.vendor.vendordataservice.api.dto.SearchResponse;

import java.util.List;

public interface SearchService {
    List<SearchResponse> search(SearchRequest request);
}
