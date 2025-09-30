package com.vendor.vendordataservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for SecurityConfig
 */
@SpringBootTest(classes = SecurityConfig.class)
@TestPropertySource(properties = {
    "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8080/oauth2/jwks",
    "app.security.jwt.audience=vendor-data-api",
    "app.security.cors.allowed-origins=*"
})
class SecurityConfigTest {

    @Autowired(required = false)
    private SecurityConfig securityConfig;

    @Test
    void contextLoads() {
        assertThat(securityConfig).isNotNull();
    }

    @Test
    void jwtAuthenticationConverterBeanExists() {
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();
        assertThat(converter).isNotNull();
    }

    @Test
    void corsConfigurationSourceBeanExists() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        assertThat(source).isNotNull();
    }

    @Test
    void jwtDecoderBeanExists() {
        JwtDecoder decoder = securityConfig.jwtDecoder();
        assertThat(decoder).isNotNull();
    }
}
