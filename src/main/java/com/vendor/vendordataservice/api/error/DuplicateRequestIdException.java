package com.vendor.vendordataservice.api.error;

public class DuplicateRequestIdException extends RuntimeException {
    public DuplicateRequestIdException(String requestId) {
        super("request_id must be unique for each request: " + requestId);
    }
}
