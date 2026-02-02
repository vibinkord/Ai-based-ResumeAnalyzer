package com.resumeanalyzer.web.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Response DTO for resume analysis results.
 * Contains the complete analysis output including match percentage,
 * matched/missing skills, suggestions (both rule-based and AI-enhanced), and the full report.
 */
public class ResumeAnalysisResponse {
    private double matchPercentage;
    private Set<String> matchedSkills;
    private Set<String> missingSkills;
    private List<String> suggestions;
    private List<String> aiSuggestions;
    private String report;

    public ResumeAnalysisResponse() {
    }

    public ResumeAnalysisResponse(double matchPercentage, Set<String> matchedSkills,
                                  Set<String> missingSkills, List<String> suggestions, String report) {
        this.matchPercentage = matchPercentage;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.suggestions = suggestions;
        this.aiSuggestions = new ArrayList<>();
        this.report = report;
    }

    public ResumeAnalysisResponse(double matchPercentage, Set<String> matchedSkills,
                                  Set<String> missingSkills, List<String> suggestions, 
                                  List<String> aiSuggestions, String report) {
        this.matchPercentage = matchPercentage;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.suggestions = suggestions;
        this.aiSuggestions = aiSuggestions;
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

    public List<String> getAiSuggestions() {
        return aiSuggestions;
    }

    public void setAiSuggestions(List<String> aiSuggestions) {
        this.aiSuggestions = aiSuggestions;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
}