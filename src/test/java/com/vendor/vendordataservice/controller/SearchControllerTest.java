package com.vendor.vendordataservice.controller;

import com.vendor.vendordataservice.api.dto.SearchResponse;
import com.vendor.vendordataservice.api.error.DuplicateRequestIdException;
import com.vendor.vendordataservice.api.error.TooManyRequestsException;
import com.vendor.vendordataservice.api.idempotency.RequestIdService;
import com.vendor.vendordataservice.api.ratelimit.RateLimiterService;
import com.vendor.vendordataservice.service.SearchService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SearchController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(com.vendor.vendordataservice.api.GlobalExceptionHandler.class)
class SearchControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SearchService searchService;

    @MockBean
    RequestIdService requestIdService;

    @MockBean
    RateLimiterService rateLimiterService;

    @Test
    void searchWithValidParamsReturns200() throws Exception {
        SearchResponse response = SearchResponse.builder()
                .apiVersion("v1")
                .page(1)
                .pageSize(50)
                .data(List.of())
                .build();
        
        when(searchService.search(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/search")
                        .queryParam("name_last", "Smith")
                        .queryParam("name_first", "John"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.api_version", is("v1")))
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.page_size", is(50)));
    }

    @Test
    void duplicateRequestIdReturns409() throws Exception {
        doThrow(new DuplicateRequestIdException("abc"))
                .when(requestIdService).validateUnique("abc");

        mockMvc.perform(get("/api/v1/search")
                        .header("X-Request-Id", "abc")
                        .queryParam("name_last", "Smith"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("DUPLICATE_REQUEST_ID")));
    }

    @Test
    void rateLimitExceededReturns429WithRetryAfter() throws Exception {
        doThrow(new TooManyRequestsException("Rate limit exceeded for this API key.", 15))
                .when(rateLimiterService).check(any());

        mockMvc.perform(get("/api/v1/search")
                        .header("X-Request-Id", "abc2")
                        .queryParam("name_last", "Smith"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("Retry-After", "15"))
                .andExpect(jsonPath("$.errorCode", is("RATE_LIMIT_EXCEEDED")));
    }

    @Test
    void invalidPaginationReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .header("X-Request-Id", "abc3")
                        .queryParam("name_last", "Smith")
                        .queryParam("page", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }
    
    @Test
    void searchWithAllParametersReturns200() throws Exception {
        SearchResponse response = SearchResponse.builder()
                .apiVersion("v1")
                .page(1)
                .pageSize(100)
                .data(List.of())
                .build();
        
        when(searchService.search(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/search")
                        .queryParam("name_last", "SMITH")
                        .queryParam("name_first", "JOHN")
                        .queryParam("dob", "1980-01-15")
                        .queryParam("ssn_last4", "1234")
                        .queryParam("filed_date_from", "2020-01-01")
                        .queryParam("filed_date_to", "2023-12-31")
                        .queryParam("include_charges", "true")
                        .queryParam("include_dockets", "true")
                        .queryParam("page", "1")
                        .queryParam("page_size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.api_version", is("v1")));
    }
}
