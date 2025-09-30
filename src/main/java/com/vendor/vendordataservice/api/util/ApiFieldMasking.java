package com.vendor.vendordataservice.api.util;

public final class ApiFieldMasking {
    private ApiFieldMasking() {}

    // Masks a driver license combined string like "FL-ABC123456" or just "ABC123456".
    // Keeps state prefix if present and last 4 characters of the number; masks the rest with '*'.
    public static String maskDriverLicense(String dlCombined) {
        if (dlCombined == null || dlCombined.isBlank()) return null;
        String state = null;
        String number = dlCombined;
        int dash = dlCombined.indexOf('-');
        if (dash > 0 && dash < dlCombined.length() - 1) {
            state = dlCombined.substring(0, dash);
            number = dlCombined.substring(dash + 1);
        }
        String maskedNumber = maskKeepLast4(number);
        return state != null ? state + "-" + maskedNumber : maskedNumber;
    }

    private static String maskKeepLast4(String s) {
        if (s == null) return null;
        int n = s.length();
        if (n <= 4) return s; // too short to mask
        String last4 = s.substring(n - 4);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n - 4; i++) sb.append('*');
        sb.append(last4);
        return sb.toString();
    }
}
