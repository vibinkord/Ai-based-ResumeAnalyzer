package com.resumeanalyzer.web.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Request DTO for batch analysis endpoint.
 * Contains multiple resume-job pairs for batch processing.
 */
public class BatchAnalysisRequest {
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
    public static class Item {
        private String id;
        private String resumeText;
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
