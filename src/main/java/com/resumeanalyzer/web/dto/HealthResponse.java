package com.resumeanalyzer.web.dto;

import java.time.LocalDateTime;

/**
 * Response DTO for health check endpoint.
 * Contains application status and version information.
 */
public class HealthResponse {
    private String status;
    private String message;
    private String version;
    private LocalDateTime timestamp;

    public HealthResponse(String status, String message, String version, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.version = version;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
