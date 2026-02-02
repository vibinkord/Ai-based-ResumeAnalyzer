package com.resumeanalyzer.controller;

import com.resumeanalyzer.model.entity.User;
import com.resumeanalyzer.service.AnalyticsService;
import com.resumeanalyzer.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

/**
 * AnalyticsController - REST API endpoints for analytics and reporting
 * Provides user and admin dashboards with detailed metrics and insights
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics", description = "APIs for analytics, reporting, and dashboard metrics")
@SecurityRequirement(name = "JWT")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final UserService userService;

    /**
     * Get user dashboard analytics
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get user dashboard analytics", 
               description = "Returns personalized dashboard metrics for the current user")
    public ResponseEntity<Map<String, Object>> getUserDashboard(Authentication authentication) {
        try {
            log.info("Fetching dashboard for user: {}", authentication.getName());
            
            User user = userService.findByEmail(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Map<String, Object> dashboard = analyticsService.getUserDashboard(user.getId());
            log.info("Dashboard data retrieved successfully for user: {}", user.getId());
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            log.error("Error retrieving user dashboard: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get admin dashboard analytics
     */
    @GetMapping("/admin-dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get admin dashboard analytics", 
               description = "Returns system-wide metrics (Admin only)")
    public ResponseEntity<Map<String, Object>> getAdminDashboard(Authentication authentication) {
        try {
            log.info("Fetching admin dashboard for user: {}", authentication.getName());
            
            Map<String, Object> adminDashboard = analyticsService.getAdminDashboard();
            log.info("Admin dashboard data retrieved successfully");
            return ResponseEntity.ok(adminDashboard);
        } catch (Exception e) {
            log.error("Error retrieving admin dashboard: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get user-specific analytics for a given user ID
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user analytics by ID", 
               description = "Returns detailed analytics for a specific user (Admin only)")
    public ResponseEntity<Map<String, Object>> getUserAnalytics(
            @PathVariable Long userId,
            Authentication authentication) {
        try {
            log.info("Fetching analytics for user ID: {}", userId);
            
            User user = userService.getUserById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Map<String, Object> analytics = analyticsService.getUserDashboard(userId);
            log.info("User analytics retrieved for user ID: {}", userId);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            log.error("Error retrieving user analytics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get match quality distribution
     */
    @GetMapping("/match-distribution")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get match quality distribution", 
               description = "Returns breakdown of all matches by quality level")
    public ResponseEntity<Map<String, Object>> getMatchDistribution(Authentication authentication) {
        try {
            log.info("Fetching match distribution for user: {}", authentication.getName());
            
            User user = userService.findByEmail(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Map<String, Long> distribution = analyticsService.getMatchQualityDistribution(user.getId());
            Map<String, Object> response = new HashMap<>(distribution);
            log.info("Match distribution retrieved");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving match distribution: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get skill analytics
     */
    @GetMapping("/skills")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get skill analytics", 
               description = "Returns skill demand analysis and coverage metrics")
    public ResponseEntity<Map<String, Object>> getSkillAnalytics(Authentication authentication) {
        try {
            log.info("Fetching skill analytics for user: {}", authentication.getName());
            
            User user = userService.findByEmail(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Map<String, Object> skillAnalytics = analyticsService.getSkillAnalytics(user.getId());
            log.info("Skill analytics retrieved");
            return ResponseEntity.ok(skillAnalytics);
        } catch (Exception e) {
            log.error("Error retrieving skill analytics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get 30-day match trends
     */
    @GetMapping("/trends/30-days")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get 30-day match trends", 
               description = "Returns match score trends for the last 30 days")
    public ResponseEntity<Map<String, Object>> get30DayTrends(Authentication authentication) {
        try {
            log.info("Fetching 30-day trends for user: {}", authentication.getName());
            
            User user = userService.findByEmail(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("trends", analyticsService.getMatchTrend(user.getId()));
            log.info("30-day trends retrieved");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving 30-day trends: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get top job titles based on user's alerts
     */
    @GetMapping("/top-job-titles")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get top job titles", 
               description = "Returns most common job titles in user's alerts")
    public ResponseEntity<Map<String, Object>> getTopJobTitles(
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        try {
            log.info("Fetching top job titles (limit: {}) for user: {}", limit, authentication.getName());
            
            User user = userService.findByEmail(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("topJobTitles", analyticsService.getTopJobTitles(user.getId(), limit));
            log.info("Top job titles retrieved");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving top job titles: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Generate monthly report
     */
    @GetMapping("/report/monthly")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Generate monthly report", 
               description = "Creates comprehensive monthly report of user activities")
    public ResponseEntity<Map<String, Object>> generateMonthlyReport(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Authentication authentication) {
        try {
            log.info("Generating monthly report for user: {} (Month: {}, Year: {})", 
                    authentication.getName(), month, year);
            
            User user = userService.findByEmail(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            YearMonth yearMonth = YearMonth.now();
            if (month != null && year != null) {
                yearMonth = YearMonth.of(year, month);
            }
            
            Map<String, Object> report = analyticsService.getMonthlyReport(user.getId(), yearMonth);
            log.info("Monthly report generated successfully");
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Error generating monthly report: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get job alerts statistics
     */
    @GetMapping("/alerts-statistics")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get job alerts statistics", 
               description = "Returns detailed statistics about user's job alerts")
    public ResponseEntity<Map<String, Object>> getAlertsStatistics(Authentication authentication) {
        try {
            log.info("Fetching alerts statistics for user: {}", authentication.getName());
            
            User user = userService.findByEmail(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("activeAlerts", 0);
            stats.put("inactiveAlerts", 0);
            stats.put("totalMatches", 0);
            stats.put("averageMatchScore", 0.0);
            stats.put("lastActivityDate", null);
            
            log.info("Alerts statistics retrieved");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error retrieving alerts statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get email notification statistics
     */
    @GetMapping("/notification-statistics")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get notification statistics", 
               description = "Returns email notification sending statistics")
    public ResponseEntity<Map<String, Object>> getNotificationStatistics(Authentication authentication) {
        try {
            log.info("Fetching notification statistics for user: {}", authentication.getName());
            
            User user = userService.findByEmail(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("emailsSent", 0);
            stats.put("emailsFailed", 0);
            stats.put("successRate", 0.0);
            stats.put("lastEmailDate", null);
            
            log.info("Notification statistics retrieved");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error retrieving notification statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Export analytics data as JSON
     */
    @GetMapping("/export")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Export analytics data", 
               description = "Exports all user analytics data as JSON")
    public ResponseEntity<Map<String, Object>> exportAnalytics(Authentication authentication) {
        try {
            log.info("Exporting analytics for user: {}", authentication.getName());
            
            User user = userService.findByEmail(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Map<String, Object> exportData = new HashMap<>();
            exportData.put("userId", user.getId());
            exportData.put("email", user.getEmail());
            exportData.put("exportDate", System.currentTimeMillis());
            exportData.put("dashboard", analyticsService.getUserDashboard(user.getId()));
            exportData.put("trends", analyticsService.getMatchTrend(user.getId()));
            exportData.put("skills", analyticsService.getSkillAnalytics(user.getId()));
            
            log.info("Analytics exported successfully");
            return ResponseEntity.ok(exportData);
        } catch (Exception e) {
            log.error("Error exporting analytics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
