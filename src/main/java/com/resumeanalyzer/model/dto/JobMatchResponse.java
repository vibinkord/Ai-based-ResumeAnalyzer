package com.resumeanalyzer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * JobMatchResponse DTO - Response object for job matches
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobMatchResponse {

    private Long id;
    private Long jobAlertId;
    private Double matchScore;
    private String matchedSkills; // Comma-separated matched skills
    private String missingSkills; // Comma-separated missing skills
    private Boolean notificationSent;
    private Boolean isViewed;
    private Boolean isInterested;
    private LocalDateTime createdAt;
    private LocalDateTime notificationSentAt;

    /**
     * Get matched skills as a set
     */
    public Set<String> getMatchedSkillsSet() {
        if (matchedSkills == null || matchedSkills.trim().isEmpty()) {
            return Set.of();
        }
        return Set.of(matchedSkills.split(","));
    }

    /**
     * Get missing skills as a set
     */
    public Set<String> getMissingSkillsSet() {
        if (missingSkills == null || missingSkills.trim().isEmpty()) {
            return Set.of();
        }
        return Set.of(missingSkills.split(","));
    }

    /**
     * Get match score as percentage string
     */
    public String getMatchScorePercentage() {
        return String.format("%.1f%%", matchScore);
    }
}
