package com.resumeanalyzer.web.dto;

/**
 * Request DTO for resume analysis endpoint.
 */
public class AnalyzeRequest {
    private String resumeText;
    private String jobDescriptionText;

    public AnalyzeRequest() {}

    public AnalyzeRequest(String resumeText, String jobDescriptionText) {
        this.resumeText = resumeText;
        this.jobDescriptionText = jobDescriptionText;
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
