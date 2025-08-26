package com.vendor.vendordataservice.service.impl;

import com.vendor.vendordataservice.api.dto.CaseRecord;
import com.vendor.vendordataservice.api.dto.SearchRequest;
import com.vendor.vendordataservice.api.dto.SearchResponse;
import com.vendor.vendordataservice.service.SearchService;
import com.vendor.vendordataservice.repository.VendorCaseRepository;
import com.vendor.vendordataservice.repository.model.VendorCaseRow;
import com.vendor.vendordataservice.service.scoring.MatchScoringStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DefaultSearchService implements SearchService {
    private final VendorCaseRepository repository;
    private final MatchScoringStrategy scoringStrategy;

    @Override
    public List<SearchResponse> search(SearchRequest request) {
        // Query SQL Server for candidate rows
        List<VendorCaseRow> rows = repository.search(
                request.getSsnLast4(),
                request.getDob(),
                request.getFirstName(),
                request.getMiddleName(),
                request.getLastName()
        );

        // Group rows by person identity (name + dob + DL)
        Map<String, SearchResponse> byPerson = new LinkedHashMap<>();
        for (VendorCaseRow row : rows) {
            String dlCombined = combine(row.getDlState(), row.getDlNumber());
            String key = String.join("|",
                    nullSafeUpper(row.getFirstName()),
                    nullSafeUpper(row.getMiddleName()),
                    nullSafeUpper(row.getLastName()),
                    nullSafeUpper(row.getSuffix()),
                    row.getDob() != null ? row.getDob().toString() : "",
                    nullSafeUpper(row.getSex()),
                    nullSafeUpper(row.getRace()),
                    nullSafeUpper(dlCombined)
            );

            SearchResponse person = byPerson.computeIfAbsent(key, k -> {
                SearchResponse r = new SearchResponse();
                r.setFirstName(row.getFirstName());
                r.setMiddleName(row.getMiddleName());
                r.setLastName(row.getLastName());
                r.setSuffix(row.getSuffix());
                r.setDob(row.getDob());
                r.setSex(row.getSex());
                r.setRace(row.getRace());
                r.setDriverLicense(dlCombined);
                return r;
            });

            CaseRecord c = new CaseRecord();
            c.setCounty(row.getCounty());
            c.setState(row.getState());
            c.setCaseNumber(row.getCaseNumber());
            c.setCharge(row.getCharge());
            c.setChargeType(row.getChargeType());
            c.setDispositionType(row.getDispositionType());
            c.setDispositionDate(row.getDispositionDate());
            c.setFileDate(row.getFileDate());
            c.setOffenseDate(row.getOffenseDate());
            person.getCases().add(c);
        }

        // Apply a match score per person using strategy
        for (SearchResponse r : byPerson.values()) {
            r.setMatchScore(scoringStrategy.score(r, request));
        }

        return new ArrayList<>(byPerson.values());
    }

    private static String nullSafeUpper(String s) { return s == null ? "" : s.toUpperCase(); }
    private static String combine(String a, String b) {
        if (notBlank(a) && notBlank(b)) return a + "-" + b;
        if (notBlank(a)) return a;
        if (notBlank(b)) return b;
        return null;
    }
    private static boolean notBlank(String s) { return s != null && !s.isBlank(); }
}
