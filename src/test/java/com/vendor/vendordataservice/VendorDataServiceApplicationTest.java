package com.vendor.vendordataservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration test for application context
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8080/oauth2/jwks",
    "app.security.jwt.audience=vendor-data-api"
})
class VendorDataServiceApplicationTest {

    @Test
    void contextLoads() {
        // Verifies that the application context loads successfully
    }
}
