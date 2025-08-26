package com.vendor.vendordataservice.controller;

import com.vendor.vendordataservice.api.dto.SearchRequest;
import com.vendor.vendordataservice.api.dto.SearchResponse;
import com.vendor.vendordataservice.config.SecurityConfig;
import com.vendor.vendordataservice.service.SearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SearchController.class)
@Import(SecurityConfig.class)
class SearchControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    @Test
    @DisplayName("GET /api/v1/search without token returns 401")
    void getSearch_Unauthenticated_401() throws Exception {
        mockMvc.perform(get("/api/v1/search"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/search with wrong scope returns 403")
    void getSearch_WrongScope_403() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("SCOPE_other"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/v1/search with vendor.search scope returns 200 and empty list")
    void getSearch_WithScope_200() throws Exception {
        Mockito.when(searchService.search(any(SearchRequest.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/search")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("SCOPE_vendor.search")))
                        .param("firstName", "John")
                        .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("POST /api/v1/search invalid body triggers 400 validation error")
    void postSearch_Invalid_400() throws Exception {
        // ssnLast4 must be 4 digits per @Pattern
        String body = "{\"ssnLast4\":\"12\"}";

        mockMvc.perform(post("/api/v1/search")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("SCOPE_vendor.search")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
