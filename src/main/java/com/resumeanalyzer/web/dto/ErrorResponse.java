package com.resumeanalyzer.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * Standard error response DTO for REST API errors.
 * Provides structured error information including error code, message, timestamp, and optional details.
 * Used by GlobalExceptionHandler to return consistent error responses to clients.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    private String error;
    private String code;
    private LocalDateTime timestamp;
    private String details;
    private String path;

    /**
     * Default constructor for JSON deserialization.
     */
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor with error message and code.
     *
     * @param error the error message
     * @param code the error code
     */
    public ErrorResponse(String error, String code) {
        this.error = error;
        this.code = code;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor with error message, code, and details.
     *
     * @param error the error message
     * @param code the error code
     * @param details additional error details
     */
    public ErrorResponse(String error, String code, String details) {
        this.error = error;
        this.code = code;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor with all fields.
     *
     * @param error the error message
     * @param code the error code
     * @param details additional error details
     * @param path the request path where error occurred
     */
    public ErrorResponse(String error, String code, String details, String path) {
        this.error = error;
        this.code = code;
        this.details = details;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "error='" + error + '\'' +
                ", code='" + code + '\'' +
                ", timestamp=" + timestamp +
                ", details='" + details + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
