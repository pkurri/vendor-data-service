package com.vendor.vendordataservice.service.scoring.impl;

import com.vendor.vendordataservice.api.dto.SearchRequest;
import com.vendor.vendordataservice.api.dto.SearchResponse;
import com.vendor.vendordataservice.service.scoring.MatchScoringStrategy;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DefaultMatchScoringStrategy implements MatchScoringStrategy {

    @Override
    public Double score(SearchResponse r, SearchRequest q) {
        int total = 0;
        int hits = 0;
        if (notBlank(q.getFirstName())) { total++; if (equalsIgnoreCaseSafe(q.getFirstName(), r.getFirstName())) hits++; }
        if (notBlank(q.getMiddleName())) { total++; if (equalsIgnoreCaseSafe(q.getMiddleName(), r.getMiddleName())) hits++; }
        if (notBlank(q.getLastName())) { total++; if (equalsIgnoreCaseSafe(q.getLastName(), r.getLastName())) hits++; }
        if (q.getDob() != null) { total++; if (Objects.equals(q.getDob(), r.getDob())) hits++; }
        // We don't store SSN in response, but treat presence as an additional matching signal filtered in SQL
        if (notBlank(q.getSsnLast4())) { total++; hits++; }
        if (total == 0) return 0d;
        return round1((hits * 100.0) / total);
    }

    private static boolean equalsIgnoreCaseSafe(String a, String b) { return a != null && b != null && a.equalsIgnoreCase(b); }
    private static boolean notBlank(String s) { return s != null && !s.isBlank(); }
    private static double round1(double v) { return Math.round(v * 10.0) / 10.0; }
}
