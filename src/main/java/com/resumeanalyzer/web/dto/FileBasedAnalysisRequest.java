package com.resumeanalyzer.web.dto;

/**
 * Request DTO for file-based resume analysis.
 * Handles both resume file upload and job description text.
 */
public class FileBasedAnalysisRequest {
    private String jobDescriptionText;

    public FileBasedAnalysisRequest() {
    }

    public FileBasedAnalysisRequest(String jobDescriptionText) {
        this.jobDescriptionText = jobDescriptionText;
    }

    public String getJobDescriptionText() {
        return jobDescriptionText;
    }

    public void setJobDescriptionText(String jobDescriptionText) {
        this.jobDescriptionText = jobDescriptionText;
    }
}
