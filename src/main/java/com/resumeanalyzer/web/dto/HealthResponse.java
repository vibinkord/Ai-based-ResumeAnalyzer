package com.resumeanalyzer.web.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for health check endpoint.
 * Contains application status and version information.
 */
@Schema(
    name = "HealthResponse",
    description = "Health status of the API"
)
public class HealthResponse {
    @Schema(
        description = "Current health status of the API",
        example = "UP",
        allowableValues = {"UP", "DOWN"}
    )
    private String status;

    @Schema(
        description = "Human-readable health status message",
        example = "AI Resume Analyzer is running"
    )
    private String message;

    @Schema(
        description = "API version",
        example = "1.0.0"
    )
    private String version;

    @Schema(
        description = "Current server timestamp",
        example = "2024-01-15T10:30:45.123456"
    )
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
