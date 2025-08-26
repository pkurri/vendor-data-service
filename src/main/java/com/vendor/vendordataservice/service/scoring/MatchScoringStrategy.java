package com.vendor.vendordataservice.service.scoring;

import com.vendor.vendordataservice.api.dto.SearchRequest;
import com.vendor.vendordataservice.api.dto.SearchResponse;

/**
 * Strategy interface to compute match score for a person result against a query.
 * Allows per-vendor or environment-specific scoring rules.
 */
public interface MatchScoringStrategy {
    /**
     * Returns a score in the range 0-100 (decimals allowed).
     */
    Double score(SearchResponse response, SearchRequest query);
}
