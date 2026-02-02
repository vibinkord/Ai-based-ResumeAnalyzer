package com.resumeanalyzer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JobMatchResultDto - Result of matching a resume against a job alert
 * Contains detailed scoring breakdown
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobMatchResultDto {

    private Long alertId;
    private Long userId;
    private double matchScore;           // Overall match score (0-100)
    private double skillScore;           // Skill match score
    private double salaryScore;          // Salary compatibility score
    private double experienceScore;      // Experience level score
    private double locationScore;        // Location match score
    private int matchPercentage;         // Match as percentage
    private String matchedSkills;        // Comma-separated matched skills
    private String missingSkills;        // Comma-separated missing skills
    private boolean matched;             // Whether score meets threshold
    private LocalDateTime timestamp;     // When match was calculated
    private String jobTitle;             // Alert job title
    private String company;              // Alert company

    /**
     * Get match quality level
     */
    public String getQualityLevel() {
        if (matchScore >= 90) return "Excellent";
        if (matchScore >= 80) return "Very Good";
        if (matchScore >= 70) return "Good";
        if (matchScore >= 60) return "Fair";
        return "Poor";
    }

    /**
     * Get matched skills as array
     */
    public String[] getMatchedSkillsArray() {
        if (matchedSkills == null || matchedSkills.isEmpty()) {
            return new String[0];
        }
        return matchedSkills.split(",");
    }

    /**
     * Get missing skills as array
     */
    public String[] getMissingSkillsArray() {
        if (missingSkills == null || missingSkills.isEmpty()) {
            return new String[0];
        }
        return missingSkills.split(",");
    }

    /**
     * Get count of matched skills
     */
    public int getMatchedSkillsCount() {
        return getMatchedSkillsArray().length;
    }

    /**
     * Get count of missing skills
     */
    public int getMissingSkillsCount() {
        return getMissingSkillsArray().length;
    }

    /**
     * Format match score for display
     */
    public String getFormattedScore() {
        return String.format("%.1f%%", matchScore);
    }
}
