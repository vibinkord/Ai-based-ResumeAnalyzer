package com.resumeanalyzer.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * JobMatch Entity - Represents a matching result for a job alert
 * Tracks when jobs are matched against alerts and if notifications were sent
 */
@Entity
@Table(name = "job_matches", indexes = {
    @Index(name = "idx_job_alert_id", columnList = "job_alert_id"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_notification_sent", columnList = "notification_sent")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_alert_id", nullable = false)
    private JobAlert jobAlert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "matched_job_title", nullable = false, length = 255)
    private String matchedJobTitle;

    @Column(name = "matched_company", length = 255)
    private String matchedCompany;

    @Column(name = "job_url", columnDefinition = "TEXT")
    private String jobUrl;

    @Column(name = "match_score", nullable = false)
    private Double matchScore; // Percentage 0-100

    @Column(name = "matched_skills", columnDefinition = "TEXT")
    private String matchedSkills; // Comma-separated

    @Column(name = "missing_skills", columnDefinition = "TEXT")
    private String missingSkills; // Comma-separated

    @Column(name = "notification_sent", nullable = false)
    @Builder.Default
    private Boolean notificationSent = false;

    @Column(name = "notification_sent_at")
    private LocalDateTime notificationSentAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_viewed", nullable = false)
    @Builder.Default
    private Boolean isViewed = false;

    @Column(name = "is_interested", nullable = false)
    @Builder.Default
    private Boolean isInterested = false; // User marked as interested

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Mark notification as sent
     */
    public void markNotificationSent() {
        this.notificationSent = true;
        this.notificationSentAt = LocalDateTime.now();
    }

    /**
     * Mark as viewed
     */
    public void markAsViewed() {
        this.isViewed = true;
    }

    /**
     * Mark as interested
     */
    public void markAsInterested() {
        this.isInterested = true;
    }
}
