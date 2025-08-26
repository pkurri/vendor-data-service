package com.vendor.vendordataservice.service.impl;

import com.vendor.vendordataservice.api.dto.CaseRecord;
import com.vendor.vendordataservice.api.dto.SearchRequest;
import com.vendor.vendordataservice.api.dto.SearchResponse;
import com.vendor.vendordataservice.repository.VendorCaseRepository;
import com.vendor.vendordataservice.repository.model.VendorCaseRow;
import com.vendor.vendordataservice.service.scoring.MatchScoringStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultSearchServiceTest {

    @Mock
    VendorCaseRepository repository;

    @Mock
    MatchScoringStrategy scoringStrategy;

    @InjectMocks
    DefaultSearchService service;

    @Test
    void groupsRowsByPersonAndAppliesScore() {
        // Arrange repository rows representing same person but 2 cases
        VendorCaseRow row1 = new VendorCaseRow();
        row1.setFirstName("John");
        row1.setMiddleName("Q");
        row1.setLastName("Public");
        row1.setSuffix(null);
        row1.setDob(LocalDate.of(1980, 5, 10));
        row1.setSex("M");
        row1.setRace("W");
        row1.setDlState("FL");
        row1.setDlNumber("D123");
        row1.setCounty("Orange");
        row1.setState("FL");
        row1.setCaseNumber("C1");
        row1.setCharge("Speeding");
        row1.setChargeType("Traffic");
        row1.setDispositionType("Closed");

        VendorCaseRow row2 = new VendorCaseRow();
        row2.setFirstName("John");
        row2.setMiddleName("Q");
        row2.setLastName("Public");
        row2.setSuffix(null);
        row2.setDob(LocalDate.of(1980, 5, 10));
        row2.setSex("M");
        row2.setRace("W");
        row2.setDlState("FL");
        row2.setDlNumber("D123");
        row2.setCounty("Seminole");
        row2.setState("FL");
        row2.setCaseNumber("C2");
        row2.setCharge("Failure to yield");
        row2.setChargeType("Traffic");
        row2.setDispositionType("Open");

        when(repository.search(any(), any(), any(), any(), any())).thenReturn(List.of(row1, row2));

        SearchRequest req = new SearchRequest();
        req.setFirstName("John");
        req.setLastName("Public");
        req.setDob(LocalDate.of(1980, 5, 10));

        when(scoringStrategy.score(any(SearchResponse.class), eq(req))).thenReturn(88.0);

        // Act
        List<SearchResponse> results = service.search(req);

        // Assert
        assertThat(results).hasSize(1);
        SearchResponse person = results.get(0);
        assertThat(person.getFirstName()).isEqualTo("John");
        assertThat(person.getCases()).extracting(CaseRecord::getCaseNumber).containsExactlyInAnyOrder("C1", "C2");
        assertThat(person.getMatchScore()).isEqualTo(88.0);
    }
}
