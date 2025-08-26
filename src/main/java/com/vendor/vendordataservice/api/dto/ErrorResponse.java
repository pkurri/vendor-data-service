package com.vendor.vendordataservice.api.dto;

import java.time.OffsetDateTime;
import java.util.List;

public class ErrorResponse {
    private String path;
    private int status;
    private String error;
    private String message;
    private OffsetDateTime timestamp = OffsetDateTime.now();
    private List<String> details;

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }
    public List<String> getDetails() { return details; }
    public void setDetails(List<String> details) { this.details = details; }
}
