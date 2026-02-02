package com.resumeanalyzer.service;

import com.resumeanalyzer.model.entity.*;
import com.resumeanalyzer.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AnalyticsService - Provides detailed analytics and reporting
 * Calculates metrics, trends, and insights for users and administrators
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AnalyticsService {

    private final JobAlertRepository jobAlertRepository;
    private final JobMatchRepository jobMatchRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final AnalysisRepository analysisRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;

    /**
     * Get user dashboard analytics
     */
    public Map<String, Object> getUserDashboard(Long userId) {
        log.info("Fetching dashboard analytics for user ID: {}", userId);

        Map<String, Object> dashboard = new HashMap<>();

        // Job alerts metrics
        long activeAlerts = jobAlertRepository.countByUserIdAndIsActiveTrue(userId);
        long totalAlerts = jobAlertRepository.countByUserId(userId);

        // Job matches metrics
        List<JobMatch> userMatches = jobMatchRepository.findByUserId(userId);
        long totalMatches = userMatches.size();
        long notificationsSent = userMatches.stream()
                .filter(JobMatch::getNotificationSent)
                .count();
        double averageMatchScore = userMatches.stream()
                .mapToDouble(JobMatch::getMatchScore)
                .average()
                .orElse(0);

        // Resume metrics
        List<Resume> userResumes = resumeRepository.findByUserId(userId);
        long totalResumes = userResumes.size();

        // Analysis metrics
        List<Analysis> userAnalyses = analysisRepository.findByUserId(userId);
        long totalAnalyses = userAnalyses.size();

        dashboard.put("activeAlerts", activeAlerts);
        dashboard.put("totalAlerts", totalAlerts);
        dashboard.put("totalMatches", totalMatches);
        dashboard.put("notificationsSent", notificationsSent);
        dashboard.put("averageMatchScore", Math.round(averageMatchScore * 100.0) / 100.0);
        dashboard.put("totalResumes", totalResumes);
        dashboard.put("totalAnalyses", totalAnalyses);
        dashboard.put("lastActivityDate", getLastActivityDate(userId));
        dashboard.put("matchTrend", getMatchTrend(userId));

        return dashboard;
    }

    /**
     * Get administrator dashboard analytics
     */
    public Map<String, Object> getAdminDashboard() {
        log.info("Fetching admin dashboard analytics");

        Map<String, Object> dashboard = new HashMap<>();

        // User metrics
        long totalUsers = userRepository.count();
        long activeUsers = countActiveUsers();
        long newUsersThisMonth = countNewUsersThisMonth();

        // Alert metrics
        long totalAlerts = jobAlertRepository.count();
        long activeAlerts = countActiveAlerts();

        // Match metrics
        long totalMatches = jobMatchRepository.count();
        double averageMatchScore = getAverageMatchScore();

        // Email metrics
        long emailEnabledUsers = notificationPreferenceRepository.countEmailEnabledUsers();
        long digestEnabledUsers = notificationPreferenceRepository.findUsersWithWeeklyDigestEnabled().size();

        // System metrics
        Map<String, Long> systemMetrics = getSystemMetrics();

        dashboard.put("totalUsers", totalUsers);
        dashboard.put("activeUsers", activeUsers);
        dashboard.put("newUsersThisMonth", newUsersThisMonth);
        dashboard.put("totalAlerts", totalAlerts);
        dashboard.put("activeAlerts", activeAlerts);
        dashboard.put("totalMatches", totalMatches);
        dashboard.put("averageMatchScore", Math.round(averageMatchScore * 100.0) / 100.0);
        dashboard.put("emailEnabledUsers", emailEnabledUsers);
        dashboard.put("digestEnabledUsers", digestEnabledUsers);
        dashboard.put("systemMetrics", systemMetrics);

        return dashboard;
    }

    /**
     * Get match quality distribution for a user
     */
    public Map<String, Long> getMatchQualityDistribution(Long userId) {
        log.info("Calculating match quality distribution for user ID: {}", userId);

        List<JobMatch> userMatches = jobMatchRepository.findByUserId(userId);

        Map<String, Long> distribution = new HashMap<>();
        distribution.put("excellent", userMatches.stream()
                .filter(m -> m.getMatchScore() >= 90)
                .count());
        distribution.put("veryGood", userMatches.stream()
                .filter(m -> m.getMatchScore() >= 80 && m.getMatchScore() < 90)
                .count());
        distribution.put("good", userMatches.stream()
                .filter(m -> m.getMatchScore() >= 70 && m.getMatchScore() < 80)
                .count());
        distribution.put("fair", userMatches.stream()
                .filter(m -> m.getMatchScore() >= 60 && m.getMatchScore() < 70)
                .count());
        distribution.put("poor", userMatches.stream()
                .filter(m -> m.getMatchScore() < 60)
                .count());

        return distribution;
    }

    /**
     * Get most active job titles for a user
     */
    public List<Map<String, Object>> getTopJobTitles(Long userId, int limit) {
        log.info("Fetching top job titles for user ID: {}", userId);

        List<JobAlert> userAlerts = jobAlertRepository.findByUserId(userId);

        return userAlerts.stream()
                .collect(Collectors.groupingBy(
                        JobAlert::getJobTitle,
                        Collectors.summingInt(alert -> alert.getMatches().size())
                ))
                .entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("jobTitle", entry.getKey());
                    map.put("matchCount", entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get match trend over time (last 30 days)
     */
    public List<Map<String, Object>> getMatchTrend(Long userId) {
        log.info("Calculating match trend for user ID: {}", userId);

        List<JobMatch> userMatches = jobMatchRepository.findByUserId(userId);
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        Map<LocalDate, Long> trendData = new TreeMap<>();
        for (int i = 0; i < 30; i++) {
            trendData.put(LocalDate.now().minusDays(i), 0L);
        }

        userMatches.stream()
                .filter(m -> m.getCreatedAt().isAfter(thirtyDaysAgo))
                .forEach(m -> {
                    LocalDate date = m.getCreatedAt().toLocalDate();
                    trendData.put(date, trendData.getOrDefault(date, 0L) + 1);
                });

        return trendData.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", entry.getKey());
                    map.put("matchCount", entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get skill analytics for a user
     */
    public Map<String, Object> getSkillAnalytics(Long userId) {
        log.info("Calculating skill analytics for user ID: {}", userId);

        Map<String, Object> analytics = new HashMap<>();

        List<JobMatch> userMatches = jobMatchRepository.findByUserId(userId);

        // Count skill frequencies
        Map<String, Integer> skillCounts = new HashMap<>();
        for (JobMatch match : userMatches) {
            if (match.getMatchedSkills() != null) {
                String[] skills = match.getMatchedSkills().split(",");
                for (String skill : skills) {
                    skillCounts.put(skill.trim(), skillCounts.getOrDefault(skill.trim(), 0) + 1);
                }
            }
        }

        // Get top 10 skills
        List<Map.Entry<String, Integer>> topSkills = skillCounts.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .collect(Collectors.toList());

        analytics.put("topSkills", topSkills);
        analytics.put("totalUniqueSkills", skillCounts.size());
        analytics.put("skillDemand", topSkills.stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                )));

        return analytics;
    }

    /**
     * Get monthly report for a user
     */
    public Map<String, Object> getMonthlyReport(Long userId, YearMonth month) {
        log.info("Generating monthly report for user ID: {} for month: {}", userId, month);

        Map<String, Object> report = new HashMap<>();

        LocalDateTime monthStart = month.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = month.atEndOfMonth().atTime(23, 59, 59);

        List<JobMatch> monthMatches = jobMatchRepository.findByUserId(userId).stream()
                .filter(m -> m.getCreatedAt().isAfter(monthStart) && m.getCreatedAt().isBefore(monthEnd))
                .collect(Collectors.toList());

        report.put("month", month.toString());
        report.put("totalMatches", monthMatches.size());
        report.put("averageScore", monthMatches.stream()
                .mapToDouble(JobMatch::getMatchScore)
                .average()
                .orElse(0));
        report.put("notificationsSent", monthMatches.stream()
                .filter(JobMatch::getNotificationSent)
                .count());

        return report;
    }

    // Helper methods

    private LocalDateTime getLastActivityDate(Long userId) {
        return jobMatchRepository.findByUserId(userId).stream()
                .map(JobMatch::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    private long countActiveUsers() {
        // Users who had activity in last 30 days
        return userRepository.count(); // Placeholder
    }

    private long countNewUsersThisMonth() {
        LocalDateTime monthStart = YearMonth.now().atDay(1).atStartOfDay();
        return userRepository.findAll().stream()
                .filter(u -> u.getCreatedAt().isAfter(monthStart))
                .count();
    }

    private long countActiveAlerts() {
        return jobAlertRepository.findByIsActiveTrue().size();
    }

    private double getAverageMatchScore() {
        List<JobMatch> allMatches = jobMatchRepository.findAll();
        return allMatches.stream()
                .mapToDouble(JobMatch::getMatchScore)
                .average()
                .orElse(0);
    }

    private Map<String, Long> getSystemMetrics() {
        Map<String, Long> metrics = new HashMap<>();
        metrics.put("totalResumes", resumeRepository.count());
        metrics.put("totalAnalyses", analysisRepository.count());
        metrics.put("totalAlertMatches", jobMatchRepository.count());
        return metrics;
    }
}
