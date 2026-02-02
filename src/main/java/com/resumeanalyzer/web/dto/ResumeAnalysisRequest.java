package com.resumeanalyzer.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for resume analysis.
 * Accepts raw resume and job description text from the client.
 */
@Schema(
    name = "ResumeAnalysisRequest",
    description = "Request object for analyzing a resume against a job description"
)
public class ResumeAnalysisRequest {
    @Schema(
        description = "Resume text content. Should contain skills, experience, and qualifications",
        example = "Senior Java Developer with 5 years experience...",
        minLength = 50,
        maxLength = 50000
    )
    private String resumeText;

    @Schema(
        description = "Job description text. Alternative to jobDescriptionUrl",
        example = "We are hiring a Java Developer with Spring Boot experience...",
        minLength = 20,
        maxLength = 50000
    )
    private String jobDescriptionText;

    @Schema(
        description = "URL to fetch job description from. Used if jobDescriptionText is not provided",
        example = "https://example.com/job/123"
    )
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
