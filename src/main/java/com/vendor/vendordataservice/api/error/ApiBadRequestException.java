package com.vendor.vendordataservice.api.error;

import java.util.List;

public class ApiBadRequestException extends RuntimeException {
    private final String errorCode;
    private final List<String> details;

    public ApiBadRequestException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.details = null;
    }

    public ApiBadRequestException(String errorCode, String message, List<String> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    public String getErrorCode() { return errorCode; }
    public List<String> getDetails() { return details; }
}
