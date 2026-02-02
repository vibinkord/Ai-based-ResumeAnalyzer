package com.resumeanalyzer.model.dto;

import com.resumeanalyzer.model.entity.JobAlert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * JobAlertResponse DTO - Response object for job alert API endpoints
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobAlertResponse {

    private Long id;
    private String jobTitle;
    private String company;
    private String description;
    private String requiredSkills;
    private Double salaryMin;
    private Double salaryMax;
    private String location;
    private String jobUrl;
    private JobAlert.AlertFrequency frequency;
    private Boolean isActive;
    private Double matchThreshold;
    private Boolean sendEmailNotification;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSentAt;
    private Integer matchCount; // Number of matches found
    private Integer unreadMatchCount; // Unread matches

    /**
     * Convert entity to DTO
     */
    public static JobAlertResponse fromEntity(JobAlert entity) {
        return JobAlertResponse.builder()
                .id(entity.getId())
                .jobTitle(entity.getJobTitle())
                .company(entity.getCompany())
                .description(entity.getDescription())
                .requiredSkills(entity.getRequiredSkills())
                .salaryMin(entity.getSalaryMin())
                .salaryMax(entity.getSalaryMax())
                .location(entity.getLocation())
                .jobUrl(entity.getJobUrl())
                .frequency(entity.getFrequency())
                .isActive(entity.getIsActive())
                .matchThreshold(entity.getMatchThreshold())
                .sendEmailNotification(entity.getSendEmailNotification())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .lastSentAt(entity.getLastSentAt())
                .matchCount(entity.getMatches() != null ? entity.getMatches().size() : 0)
                .unreadMatchCount(entity.getMatches() != null ?
                        (int) entity.getMatches().stream()
                                .filter(m -> !m.getNotificationSent())
                                .count() : 0)
                .build();
    }

    /**
     * Convert entity to DTO with custom match count
     */
    public static JobAlertResponse fromEntity(JobAlert entity, int matchCount, int unreadMatchCount) {
        JobAlertResponse response = fromEntity(entity);
        response.setMatchCount(matchCount);
        response.setUnreadMatchCount(unreadMatchCount);
        return response;
    }
}
