package com.resumeanalyzer.web.dto;

import java.util.List;
import java.util.Set;

/**
 * Response DTO for resume analysis endpoint.
 */
public class AnalyzeResponse {
    private double matchPercentage;
    private Set<String> matchedSkills;
    private Set<String> missingSkills;
    private List<String> suggestions;
    private String report;

    public AnalyzeResponse() {}

    public AnalyzeResponse(double matchPercentage, Set<String> matchedSkills, 
                          Set<String> missingSkills, List<String> suggestions, String report) {
        this.matchPercentage = matchPercentage;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.suggestions = suggestions;
        this.report = report;
    }

    public double getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(double matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public Set<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(Set<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public Set<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(Set<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
}
