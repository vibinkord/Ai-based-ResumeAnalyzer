package com.resumeanalyzer.web.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for batch analysis endpoint.
 * Contains multiple resume-job pairs for batch processing.
 */
@Schema(
    name = "BatchAnalysisRequest",
    description = "Request to batch analyze multiple resume-job pairs"
)
public class BatchAnalysisRequest {
    @Schema(
        description = "List of resume-job pairs to analyze (minimum 1 item required)"
    )
    private List<Item> items;

    public BatchAnalysisRequest() {
    }

    public BatchAnalysisRequest(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    /**
     * Represents a single analysis item in the batch.
     */
    @Schema(
        name = "BatchAnalysisItem",
        description = "Single resume-job pair for batch analysis"
    )
    public static class Item {
        @Schema(
            description = "Unique identifier for this batch item",
            example = "item-1"
        )
        private String id;

        @Schema(
            description = "Resume text for analysis",
            example = "Senior Java Developer with 5 years experience...",
            minLength = 50,
            maxLength = 50000
        )
        private String resumeText;

        @Schema(
            description = "Job description text",
            example = "We are hiring a Java Developer with Spring Boot experience...",
            minLength = 20,
            maxLength = 50000
        )
        private String jobDescriptionText;

        public Item() {
        }

        public Item(String id, String resumeText, String jobDescriptionText) {
            this.id = id;
            this.resumeText = resumeText;
            this.jobDescriptionText = jobDescriptionText;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getResumeText() {
            return resumeText;
        }

        public void setResumeText(String resumeText) {
            this.resumeText = resumeText;
        }

        public String getJobDescriptionText() {
            return jobDescriptionText;
        }

        public void setJobDescriptionText(String jobDescriptionText) {
            this.jobDescriptionText = jobDescriptionText;
        }
    }
}
