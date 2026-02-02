package com.resumeanalyzer.controller;

import com.resumeanalyzer.model.dto.NotificationPreferenceRequest;
import com.resumeanalyzer.model.dto.NotificationPreferenceResponse;
import com.resumeanalyzer.service.NotificationService;
import com.resumeanalyzer.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * NotificationController - REST endpoints for notification preferences
 * Handles user notification preferences and settings
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notifications", description = "Notification preference endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasAnyRole('USER', 'PREMIUM', 'ANALYST')")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Get current user ID from JWT token
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = jwtTokenProvider.getTokenFromRequest(request);
        return jwtTokenProvider.getUserIdFromToken(token);
    }

    /**
     * Get notification preferences for current user
     *
     * @param httpRequest the HTTP request
     * @return notification preference response
     */
    @GetMapping("/preferences")
    @Operation(summary = "Get notification preferences", description = "Retrieve notification preferences for current user")
    public ResponseEntity<NotificationPreferenceResponse> getNotificationPreferences(
            HttpServletRequest httpRequest) {
        log.info("Fetching notification preferences for current user");
        Long userId = getCurrentUserId(httpRequest);

        try {
            NotificationPreferenceResponse response = notificationService.getUserPreferences(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching notification preferences", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Update notification preferences for current user
     *
     * @param request the preference update request
     * @param httpRequest the HTTP request
     * @return updated notification preference response
     */
    @PutMapping("/preferences")
    @Operation(summary = "Update notification preferences", description = "Update notification preferences for current user")
    public ResponseEntity<NotificationPreferenceResponse> updateNotificationPreferences(
            @Valid @RequestBody NotificationPreferenceRequest request,
            HttpServletRequest httpRequest) {
        log.info("Updating notification preferences for current user");
        Long userId = getCurrentUserId(httpRequest);

        try {
            NotificationPreferenceResponse response = notificationService.updateUserPreferences(userId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating notification preferences", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Opt in user for email notifications
     *
     * @param httpRequest the HTTP request
     * @return response entity
     */
    @PostMapping("/opt-in")
    @Operation(summary = "Opt in for emails", description = "Opt in user for email notifications")
    public ResponseEntity<Map<String, String>> optInForNotifications(HttpServletRequest httpRequest) {
        log.info("Opting in current user for email notifications");
        Long userId = getCurrentUserId(httpRequest);

        try {
            notificationService.optInForNotifications(userId);
            return ResponseEntity.ok(Map.of("message", "Successfully opted in for email notifications"));
        } catch (Exception e) {
            log.error("Error opting in for notifications", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to opt in"));
        }
    }

    /**
     * Opt out user from email notifications
     *
     * @param httpRequest the HTTP request
     * @return response entity
     */
    @PostMapping("/opt-out")
    @Operation(summary = "Opt out from emails", description = "Opt out user from email notifications")
    public ResponseEntity<Map<String, String>> optOutFromNotifications(HttpServletRequest httpRequest) {
        log.info("Opting out current user from email notifications");
        Long userId = getCurrentUserId(httpRequest);

        try {
            notificationService.optOutFromNotifications(userId);
            return ResponseEntity.ok(Map.of("message", "Successfully opted out from email notifications"));
        } catch (Exception e) {
            log.error("Error opting out from notifications", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to opt out"));
        }
    }

    /**
     * Check if user should receive notifications
     *
     * @param httpRequest the HTTP request
     * @return notification status
     */
    @GetMapping("/status")
    @Operation(summary = "Check notification status", description = "Check if user receives notifications")
    public ResponseEntity<Map<String, Object>> checkNotificationStatus(HttpServletRequest httpRequest) {
        log.info("Checking notification status for current user");
        Long userId = getCurrentUserId(httpRequest);

        boolean shouldReceive = notificationService.shouldSendNotification(userId);
        boolean shouldReceiveDigest = notificationService.shouldSendDigest(userId);

        return ResponseEntity.ok(Map.of(
                "notificationsEnabled", shouldReceive,
                "digestEnabled", shouldReceiveDigest,
                "emailEnabledUsersTotal", notificationService.getTotalEmailEnabledUsers()
        ));
    }

    /**
     * Send test email for current user (for admin/testing)
     *
     * @param httpRequest the HTTP request
     * @return response entity
     */
    @PostMapping("/send-test-email")
    @Operation(summary = "Send test email", description = "Send a test email to verify delivery")
    public ResponseEntity<Map<String, String>> sendTestEmail(HttpServletRequest httpRequest) {
        log.info("Sending test email to current user");
        Long userId = getCurrentUserId(httpRequest);

        try {
            boolean sent = notificationService.sendWelcomeEmail(userId);
            if (sent) {
                return ResponseEntity.ok(Map.of("message", "Test email sent successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Failed to send test email"));
            }
        } catch (Exception e) {
            log.error("Error sending test email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error sending test email: " + e.getMessage()));
        }
    }

    /**
     * Admin endpoint: Get statistics about email notifications
     * Requires ADMIN or ANALYST role
     *
     * @return notification statistics
     */
    @GetMapping("/admin/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Get notification statistics", description = "Get overall notification statistics (admin only)")
    public ResponseEntity<Map<String, Object>> getNotificationStatistics() {
        log.info("Fetching notification statistics");

        long emailEnabledUsers = notificationService.getTotalEmailEnabledUsers();
        long jobAlertEmailUsers = notificationService.getUsersWithJobAlertEmailsEnabled().size();
        long digestUsers = notificationService.getUsersWithWeeklyDigestEnabled().size();

        return ResponseEntity.ok(Map.of(
                "totalEmailEnabledUsers", emailEnabledUsers,
                "jobAlertEmailUsers", jobAlertEmailUsers,
                "weeklyDigestUsers", digestUsers
        ));
    }
}
