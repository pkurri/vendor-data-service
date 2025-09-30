package com.vendor.vendordataservice.api.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApiFieldMaskingTest {

    @Test
    void masksDriverLicenseKeepingStateAndLast4() {
        String masked = ApiFieldMasking.maskDriverLicense("FL-ABC1234567");
        // 10 chars -> 6 stars + last4
        assertEquals("FL-******4567", masked);
    }

    @Test
    void returnsNullWhenNullInput() {
        assertNull(ApiFieldMasking.maskDriverLicense(null));
    }

    @Test
    void leavesShortNumbersUnchanged() {
        assertEquals("1234", ApiFieldMasking.maskDriverLicense("1234"));
    }
}
