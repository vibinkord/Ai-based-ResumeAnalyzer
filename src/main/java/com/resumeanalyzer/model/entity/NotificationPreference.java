package com.resumeanalyzer.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * NotificationPreference Entity - User notification settings
 * Controls how and when users receive email notifications
 */
@Entity
@Table(name = "notification_preferences", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Email Notification Settings
    @Column(name = "email_enabled", nullable = false)
    @Builder.Default
    private Boolean emailEnabled = true;

    @Column(name = "job_alert_email_enabled", nullable = false)
    @Builder.Default
    private Boolean jobAlertEmailEnabled = true;

    @Column(name = "match_notification_enabled", nullable = false)
    @Builder.Default
    private Boolean matchNotificationEnabled = true;

    @Column(name = "weekly_digest_enabled", nullable = false)
    @Builder.Default
    private Boolean weeklyDigestEnabled = true;

    @Column(name = "analysis_reminder_enabled", nullable = false)
    @Builder.Default
    private Boolean analysisReminderEnabled = false;

    // Notification Frequency
    @Enumerated(EnumType.STRING)
    @Column(name = "digest_frequency", nullable = false)
    @Builder.Default
    private DigestFrequency digestFrequency = DigestFrequency.WEEKLY;

    // Preferred notification times
    @Column(name = "preferred_hour")
    @Builder.Default
    private Integer preferredHour = 9; // 0-23 in 24-hour format

    @Column(name = "preferred_minute")
    @Builder.Default
    private Integer preferredMinute = 0; // 0-59

    @Column(name = "preferred_day_of_week")
    @Builder.Default
    private Integer preferredDayOfWeek = 1; // 1=Monday, 7=Sunday

    // Match score threshold
    @Column(name = "min_match_threshold")
    @Builder.Default
    private Double minMatchThreshold = 60.0; // Don't notify for matches below this %

    // Timezone
    @Column(name = "timezone")
    @Builder.Default
    private String timezone = "UTC"; // e.g., "America/New_York"

    // Opt-in status
    @Column(name = "opted_in", nullable = false)
    @Builder.Default
    private Boolean optedIn = true;

    @Column(name = "opted_in_at")
    private LocalDateTime optedInAt;

    @Column(name = "opted_out_at")
    private LocalDateTime optedOutAt;

    // Audit
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_digest_sent_at")
    private LocalDateTime lastDigestSentAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (optedIn && optedInAt == null) {
            optedInAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Check if user should receive email notifications
     */
    public boolean shouldReceiveEmailNotifications() {
        return emailEnabled && optedIn;
    }

    /**
     * Check if job alert emails are enabled
     */
    public boolean shouldReceiveJobAlertEmails() {
        return jobAlertEmailEnabled && shouldReceiveEmailNotifications();
    }

    /**
     * Check if weekly digest should be sent
     */
    public boolean shouldReceiveWeeklyDigest() {
        return weeklyDigestEnabled && 
               digestFrequency == DigestFrequency.WEEKLY && 
               shouldReceiveEmailNotifications();
    }

    /**
     * Check if analysis reminder should be sent
     */
    public boolean shouldReceiveAnalysisReminder() {
        return analysisReminderEnabled && shouldReceiveEmailNotifications();
    }

    /**
     * Get preferred notification time
     */
    public LocalTime getPreferredTime() {
        return LocalTime.of(
            preferredHour != null ? preferredHour : 9,
            preferredMinute != null ? preferredMinute : 0
        );
    }

    /**
     * Opt user in to notifications
     */
    public void optIn() {
        this.optedIn = true;
        this.optedInAt = LocalDateTime.now();
        this.optedOutAt = null;
    }

    /**
     * Opt user out of notifications
     */
    public void optOut() {
        this.optedIn = false;
        this.optedOutAt = LocalDateTime.now();
    }

    /**
     * Mark digest as sent
     */
    public void markDigestAsSent() {
        this.lastDigestSentAt = LocalDateTime.now();
    }

    /**
     * Check if digest should be sent based on frequency
     */
    public boolean shouldSendDigest() {
        if (!shouldReceiveWeeklyDigest()) {
            return false;
        }

        if (lastDigestSentAt == null) {
            return true;
        }

        return switch (digestFrequency) {
            case DAILY -> lastDigestSentAt.plusDays(1).isBefore(LocalDateTime.now());
            case WEEKLY -> lastDigestSentAt.plusWeeks(1).isBefore(LocalDateTime.now());
            case MONTHLY -> lastDigestSentAt.plusMonths(1).isBefore(LocalDateTime.now());
        };
    }

    /**
     * Enum for digest frequency
     */
    public enum DigestFrequency {
        DAILY("Daily"),
        WEEKLY("Weekly"),
        MONTHLY("Monthly");

        private final String displayName;

        DigestFrequency(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
