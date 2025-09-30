package com.vendor.vendordataservice.service.impl;

import com.vendor.vendordataservice.api.dto.CaseRecord;
import com.vendor.vendordataservice.api.dto.ChargeDto;
import com.vendor.vendordataservice.api.dto.SearchRequest;
import com.vendor.vendordataservice.api.dto.SearchResponse;
import com.vendor.vendordataservice.repository.mybatis.CaseMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for DefaultSearchService with MyBatis
 */
@ExtendWith(MockitoExtension.class)
class DefaultSearchServiceTest {

    @Mock
    CaseMapper caseMapper;

    @InjectMocks
    DefaultSearchService service;

    @Test
    void searchReturnsMultipleCases() {
        // Arrange - Mock MyBatis mapper to return case records
        CaseRecord case1 = CaseRecord.builder()
                .caseId("C1")
                .caseNumber("2020-CF-001234")
                .firstName("John")
                .middleName("Q")
                .lastName("Public")
                .dateOfBirth(LocalDate.of(1980, 5, 10))
                .sexCode("M")
                .raceCode("W")
                .filedDate(LocalDate.of(2020, 5, 15))
                .countyId(12)
                .charges(new ArrayList<>())
                .dockets(new ArrayList<>())
                .events(new ArrayList<>())
                .defendants(new ArrayList<>())
                .build();

        CaseRecord case2 = CaseRecord.builder()
                .caseId("C2")
                .caseNumber("2021-CF-005678")
                .firstName("John")
                .middleName("Q")
                .lastName("Public")
                .dateOfBirth(LocalDate.of(1980, 5, 10))
                .sexCode("M")
                .raceCode("W")
                .filedDate(LocalDate.of(2021, 3, 20))
                .countyId(25)
                .charges(new ArrayList<>())
                .dockets(new ArrayList<>())
                .events(new ArrayList<>())
                .defendants(new ArrayList<>())
                .build();

        when(caseMapper.searchCases(any(SearchRequest.class)))
                .thenReturn(List.of(case1, case2));

        SearchRequest req = SearchRequest.builder()
                .nameFirst("John")
                .nameLast("Public")
                .dob(LocalDate.of(1980, 5, 10))
                .page(1)
                .pageSize(50)
                .build();

        // Act
        SearchResponse response = service.search(req);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getApiVersion()).isEqualTo("v1");
        assertThat(response.getData()).hasSize(2);
        assertThat(response.getData()).extracting(CaseRecord::getCaseNumber)
                .containsExactlyInAnyOrder("2020-CF-001234", "2021-CF-005678");
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getPageSize()).isEqualTo(50);
    }

    @Test
    void searchWithIncludeChargesFlagFiltersCorrectly() {
        // Arrange
        ChargeDto charge = ChargeDto.builder()
                .chargeSequenceNumber("001")
                .offenseDate(LocalDate.of(2020, 5, 10))
                .initialFlStatuteNumber("812.014")
                .initialFlStatuteDescription("THEFT")
                .build();

        CaseRecord caseWithCharges = CaseRecord.builder()
                .caseId("C1")
                .caseNumber("2020-CF-001234")
                .firstName("John")
                .lastName("Smith")
                .charges(List.of(charge))
                .dockets(new ArrayList<>())
                .events(new ArrayList<>())
                .defendants(new ArrayList<>())
                .build();

        when(caseMapper.searchCases(any(SearchRequest.class)))
                .thenReturn(List.of(caseWithCharges));

        SearchRequest req = SearchRequest.builder()
                .nameLast("Smith")
                .includeCharges(false) // Don't include charges
                .page(1)
                .pageSize(50)
                .build();

        // Act
        SearchResponse response = service.search(req);

        // Assert
        assertThat(response.getData()).hasSize(1);
        assertThat(response.getData().get(0).getCharges()).isNull(); // Filtered out
    }

    @Test
    void validateMissingSearchKeyThrows() {
        SearchRequest req = SearchRequest.builder().build();
        assertThrows(RuntimeException.class, () -> service.search(req));
    }

    @Test
    void validateInvalidPaginationThrows() {
        SearchRequest req = SearchRequest.builder()
                .nameFirst("John")
                .page(0) // invalid
                .build();
        assertThrows(RuntimeException.class, () -> service.search(req));
    }

    @Test
    void validatePageSizeTooLargeThrows() {
        SearchRequest req = SearchRequest.builder()
                .nameFirst("John")
                .pageSize(600) // invalid > 500
                .build();
        assertThrows(RuntimeException.class, () -> service.search(req));
    }

    @Test
    void searchWithAllIncludeFlagsTrue() {
        // Arrange
        CaseRecord caseRecord = CaseRecord.builder()
                .caseId("C1")
                .caseNumber("2020-CF-001234")
                .firstName("John")
                .lastName("Smith")
                .charges(List.of(ChargeDto.builder().chargeSequenceNumber("001").build()))
                .dockets(new ArrayList<>())
                .events(new ArrayList<>())
                .defendants(new ArrayList<>())
                .build();

        when(caseMapper.searchCases(any(SearchRequest.class)))
                .thenReturn(List.of(caseRecord));

        SearchRequest req = SearchRequest.builder()
                .nameLast("Smith")
                .includeCharges(true)
                .includeDockets(true)
                .includeEvents(true)
                .includeDefendants(true)
                .page(1)
                .pageSize(50)
                .build();

        // Act
        SearchResponse response = service.search(req);

        // Assert
        assertThat(response.getData()).hasSize(1);
        CaseRecord result = response.getData().get(0);
        assertThat(result.getCharges()).isNotNull();
        assertThat(result.getDockets()).isNotNull();
        assertThat(result.getEvents()).isNotNull();
        assertThat(result.getDefendants()).isNotNull();
    }
}
