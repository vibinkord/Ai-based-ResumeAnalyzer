package com.resumeanalyzer.web.dto;

/**
 * Request DTO for resume analysis.
 * Accepts raw resume and job description text from the client.
 */
public class ResumeAnalysisRequest {
    private String resumeText;
    private String jobDescriptionText;
    private String jobDescriptionUrl;

    public ResumeAnalysisRequest() {
    }

    public ResumeAnalysisRequest(String resumeText, String jobDescriptionText) {
        this.resumeText = resumeText;
        this.jobDescriptionText = jobDescriptionText;
    }

    public ResumeAnalysisRequest(String resumeText, String jobDescriptionText, String jobDescriptionUrl) {
        this.resumeText = resumeText;
        this.jobDescriptionText = jobDescriptionText;
        this.jobDescriptionUrl = jobDescriptionUrl;
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

    public String getJobDescriptionUrl() {
        return jobDescriptionUrl;
    }

    public void setJobDescriptionUrl(String jobDescriptionUrl) {
        this.jobDescriptionUrl = jobDescriptionUrl;
    }
}
