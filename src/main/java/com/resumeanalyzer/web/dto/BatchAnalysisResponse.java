package com.resumeanalyzer.web.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for batch analysis endpoint.
 * Contains results for each item in the batch.
 */
public class BatchAnalysisResponse {
    private List<Item> results;
    private int successCount;
    private int failureCount;
    private LocalDateTime timestamp;

    public BatchAnalysisResponse(List<Item> results, int successCount, int failureCount, LocalDateTime timestamp) {
        this.results = results;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.timestamp = timestamp;
    }

    public List<Item> getResults() {
        return results;
    }

    public void setResults(List<Item> results) {
        this.results = results;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Represents the analysis result for a single batch item.
     */
    public static class Item {
        private String id;
        private boolean success;
        private String message;
        private Object data;

        public Item(String id, boolean success, String message, Object data) {
            this.id = id;
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }
}
