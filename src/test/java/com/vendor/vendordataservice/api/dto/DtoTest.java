package com.vendor.vendordataservice.api.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for DTOs - ensuring proper construction and getters/setters
 */
class DtoTest {

    @Test
    void caseRecordBuilderWorks() {
        CaseRecord record = CaseRecord.builder()
                .caseId("C123")
                .caseNumber("2020-CF-001234")
                .firstName("John")
                .lastName("Smith")
                .dateOfBirth(LocalDate.of(1980, 1, 15))
                .filedDate(LocalDate.of(2020, 5, 15))
                .build();

        assertThat(record.getCaseId()).isEqualTo("C123");
        assertThat(record.getCaseNumber()).isEqualTo("2020-CF-001234");
        assertThat(record.getFirstName()).isEqualTo("John");
        assertThat(record.getLastName()).isEqualTo("Smith");
    }

    @Test
    void chargeDtoBuilderWorks() {
        ChargeDto charge = ChargeDto.builder()
                .chargeSequenceNumber("001")
                .offenseDate(LocalDate.of(2020, 5, 10))
                .initialFlStatuteNumber("812.014")
                .initialFlStatuteDescription("THEFT")
                .build();

        assertThat(charge.getChargeSequenceNumber()).isEqualTo("001");
        assertThat(charge.getInitialFlStatuteNumber()).isEqualTo("812.014");
    }

    @Test
    void sentenceDtoBuilderWorks() {
        SentenceDto sentence = SentenceDto.builder()
                .sentenceSequenceNumber("001")
                .sentenceImposedDate(LocalDate.of(2021, 3, 20))
                .sentenceCode(10)
                .build();

        assertThat(sentence.getSentenceSequenceNumber()).isEqualTo("001");
        assertThat(sentence.getSentenceCode()).isEqualTo(10);
    }

    @Test
    void defendantDtoBuilderWorks() {
        DefendantDto defendant = DefendantDto.builder()
                .partyId("P001")
                .firstName("John")
                .lastName("Smith")
                .dob(LocalDate.of(1980, 1, 15))
                .aka(List.of("Johnny Smith", "J. Smith"))
                .build();

        assertThat(defendant.getPartyId()).isEqualTo("P001");
        assertThat(defendant.getAka()).hasSize(2);
    }

    @Test
    void docketDtoBuilderWorks() {
        DocketDto docket = DocketDto.builder()
                .docketId("D001")
                .docketActionDate(LocalDate.of(2020, 5, 15))
                .docketCode("FILED")
                .docketText("Case filed")
                .build();

        assertThat(docket.getDocketId()).isEqualTo("D001");
        assertThat(docket.getDocketCode()).isEqualTo("FILED");
    }

    @Test
    void eventDtoBuilderWorks() {
        EventDto event = EventDto.builder()
                .eventId("E001")
                .courtAppearanceDate(OffsetDateTime.now())
                .judgeCode("JUDGE001")
                .courtEventDescription("Arraignment")
                .build();

        assertThat(event.getEventId()).isEqualTo("E001");
        assertThat(event.getJudgeCode()).isEqualTo("JUDGE001");
    }

    @Test
    void searchRequestBuilderWorks() {
        SearchRequest request = SearchRequest.builder()
                .nameLast("SMITH")
                .nameFirst("JOHN")
                .dob(LocalDate.of(1980, 1, 15))
                .ssnLast4("1234")
                .page(1)
                .pageSize(50)
                .includeCharges(true)
                .includeDockets(false)
                .build();

        assertThat(request.getNameLast()).isEqualTo("SMITH");
        assertThat(request.getNameFirst()).isEqualTo("JOHN");
        assertThat(request.getPage()).isEqualTo(1);
        assertThat(request.getIncludeCharges()).isTrue();
    }

    @Test
    void searchResponseBuilderWorks() {
        SearchResponse response = SearchResponse.builder()
                .apiVersion("v1")
                .page(1)
                .pageSize(50)
                .totalRecordsIsEstimate(true)
                .data(new ArrayList<>())
                .warnings(new ArrayList<>())
                .build();

        assertThat(response.getApiVersion()).isEqualTo("v1");
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getData()).isEmpty();
    }

    @Test
    void errorResponseCreation() {
        ErrorResponse error = new ErrorResponse();
        error.setErrorCode("TEST_ERROR");
        error.setMessage("Test message");
        error.setStatus(400);

        assertThat(error.getErrorCode()).isEqualTo("TEST_ERROR");
        assertThat(error.getMessage()).isEqualTo("Test message");
        assertThat(error.getStatus()).isEqualTo(400);
    }
}
