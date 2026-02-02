package com.resumeanalyzer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * AnalyticsDashboardDto - Represents user's dashboard analytics
 * Contains summary metrics and key performance indicators
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsDashboardDto {
    
    // User Information
    private Long userId;
    private String userName;
    private String userEmail;
    
    // Resume Metrics
    private Integer totalResumes;
    private Integer resumesAnalyzed;
    private Double averageAnalysisScore;
    
    // Job Alert Metrics
    private Integer activeJobAlerts;
    private Integer inactiveJobAlerts;
    private Integer totalJobAlerts;
    
    // Job Match Metrics
    private Integer totalMatches;
    private Integer excellentMatches;
    private Integer veryGoodMatches;
    private Integer goodMatches;
    private Integer fairMatches;
    private Integer poorMatches;
    
    // Match Quality
    private Double averageMatchScore;
    private Double highQualityMatchPercentage;
    private Integer matchesLastWeek;
    private Integer matchesLastMonth;
    
    // Email Statistics
    private Integer emailsSent;
    private Integer emailsDelivered;
    private Integer emailsFailed;
    private Double emailSuccessRate;
    
    // Skill Analytics
    private Integer uniqueSkillsRequired;
    private Integer userSkillsCovered;
    private Double skillCoveragePercentage;
    
    // Activity Metrics
    private LocalDateTime lastActivityDate;
    private LocalDateTime lastAnalysisDate;
    private LocalDateTime lastJobAlertDate;
    private Integer activityCountLastWeek;
    private Integer activityCountLastMonth;
    
    // Trend Information
    private List<DailyMetric> dailyTrendData;
    private Map<String, Object> topSkills;
    private Map<String, Object> topJobTitles;
    
    // Timestamps
    private LocalDateTime dashboardGeneratedAt;
    
    /**
     * Daily metric data for trend charts
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyMetric {
        private String date;
        private Integer matchCount;
        private Double averageScore;
        private Integer emailsSent;
        private Integer analysisCount;
    }
}
