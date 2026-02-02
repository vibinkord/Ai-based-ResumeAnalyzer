package com.resumeanalyzer.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for resume comparison endpoint.
 * Contains two resume texts to compare.
 */
@Schema(
    name = "ComparisonRequest",
    description = "Request to compare two resumes and identify similarities and differences"
)
public class ComparisonRequest {
    @Schema(
        description = "First resume text to compare",
        example = "Senior Developer with 5 years Java experience...",
        minLength = 50,
        maxLength = 50000
    )
    private String resume1;

    @Schema(
        description = "Second resume text to compare",
        example = "Mid-level Developer with 3 years Python experience...",
        minLength = 50,
        maxLength = 50000
    )
    private String resume2;

    public ComparisonRequest() {
    }

    public ComparisonRequest(String resume1, String resume2) {
        this.resume1 = resume1;
        this.resume2 = resume2;
    }

    public String getResume1() {
        return resume1;
    }

    public void setResume1(String resume1) {
        this.resume1 = resume1;
    }

    public String getResume2() {
        return resume2;
    }

    public void setResume2(String resume2) {
        this.resume2 = resume2;
    }
}
