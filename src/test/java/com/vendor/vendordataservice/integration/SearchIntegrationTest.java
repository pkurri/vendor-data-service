package com.vendor.vendordataservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendor.vendordataservice.api.dto.SearchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for search endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8080/oauth2/jwks",
    "app.security.jwt.audience=vendor-data-api"
})
class SearchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = "SCOPE_vendor.search")
    void searchWithGetRequest() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("name_last", "SMITH")
                        .param("name_first", "JOHN")
                        .param("page", "1")
                        .param("page_size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.api_version").value("v1"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.page_size").value(50))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_vendor.search")
    void searchWithPostRequest() throws Exception {
        SearchRequest request = SearchRequest.builder()
                .nameLast("SMITH")
                .nameFirst("JOHN")
                .dob(LocalDate.of(1980, 1, 15))
                .page(1)
                .pageSize(50)
                .includeCharges(true)
                .build();

        mockMvc.perform(post("/api/v1/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.api_version").value("v1"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void searchWithoutAuthenticationReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("name_last", "SMITH"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_wrong.scope")
    void searchWithWrongScopeReturns403() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("name_last", "SMITH"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_vendor.search")
    void searchWithInvalidPaginationReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("name_last", "SMITH")
                        .param("page", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_vendor.search")
    void searchWithAllParametersWorks() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("name_last", "SMITH")
                        .param("name_first", "JOHN")
                        .param("dob", "1980-01-15")
                        .param("ssn_last4", "1234")
                        .param("filed_date_from", "2020-01-01")
                        .param("filed_date_to", "2023-12-31")
                        .param("county_codes", "12", "25")
                        .param("case_type", "CR", "CV")
                        .param("include_charges", "true")
                        .param("include_dockets", "true")
                        .param("include_events", "false")
                        .param("include_defendants", "true")
                        .param("page", "1")
                        .param("page_size", "100")
                        .param("sort_by", "filed_date")
                        .param("sort_dir", "desc")
                        .param("match_mode", "exact"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.api_version").value("v1"));
    }
}
