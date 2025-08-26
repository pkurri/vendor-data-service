package com.vendor.vendordataservice.service.scoring.impl;

import com.vendor.vendordataservice.api.dto.SearchRequest;
import com.vendor.vendordataservice.api.dto.SearchResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DefaultMatchScoringStrategyTest {

    @Test
    void computesScoreBasedOnProvidedFields() {
        DefaultMatchScoringStrategy strategy = new DefaultMatchScoringStrategy();
        SearchResponse r = new SearchResponse();
        r.setFirstName("John");
        r.setLastName("Doe");
        r.setDob(LocalDate.of(1990, 1, 1));

        SearchRequest q = new SearchRequest();
        q.setFirstName("john"); // case-insensitive match
        q.setLastName("doe");
        q.setDob(LocalDate.of(1990, 1, 1));
        q.setSsnLast4("1234");

        // total = first,last,dob,ssn = 4; hits = first,last,dob,ssn(boost) = 4 -> 100
        Double score = strategy.score(r, q);
        assertNotNull(score);
        assertEquals(100.0, score);
    }

    @Test
    void partialMatchesYieldLowerScore() {
        DefaultMatchScoringStrategy strategy = new DefaultMatchScoringStrategy();
        SearchResponse r = new SearchResponse();
        r.setFirstName("Jane");
        r.setLastName("Smith");

        SearchRequest q = new SearchRequest();
        q.setFirstName("Jane");
        q.setLastName("Different");
        // total = first,last = 2; hits = first = 1 -> 50.0
        Double score = strategy.score(r, q);
        assertEquals(50.0, score);
    }
}
