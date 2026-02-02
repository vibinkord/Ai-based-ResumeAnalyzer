package com.resumeanalyzer.web.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for batch analysis endpoint.
 * Contains results for each item in the batch.
 */
@Schema(
    name = "BatchAnalysisResponse",
    description = "Results of batch resume analysis"
)
public class BatchAnalysisResponse {
    @Schema(
        description = "Analysis results for each item in the batch"
    )
    private List<Item> results;

    @Schema(
        description = "Number of successfully analyzed items",
        example = "8",
        minimum = "0"
    )
    private int successCount;

    @Schema(
        description = "Number of failed analysis items",
        example = "2",
        minimum = "0"
    )
    private int failureCount;

    @Schema(
        description = "Timestamp when batch analysis was completed",
        example = "2024-01-15T10:30:45.123456"
    )
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
    @Schema(
        name = "BatchAnalysisResultItem",
        description = "Result of analyzing a single batch item"
    )
    public static class Item {
        @Schema(
            description = "Item identifier matching the request",
            example = "item-1"
        )
        private String id;

        @Schema(
            description = "Whether analysis was successful",
            example = "true"
        )
        private boolean success;

        @Schema(
            description = "Status message for this item",
            example = "Analysis completed"
        )
        private String message;

        @Schema(
            description = "Analysis result data (ResumeAnalysisResponse if successful)"
        )
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
