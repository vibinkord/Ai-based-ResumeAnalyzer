package com.resumeanalyzer.web.dto;

/**
 * Request DTO for resume comparison endpoint.
 * Contains two resume texts to compare.
 */
public class ComparisonRequest {
    private String resume1;
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
