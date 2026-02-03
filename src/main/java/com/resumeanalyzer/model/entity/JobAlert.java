package com.resumeanalyzer.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

/**
 * JobAlert Entity - Represents a job alert subscription
 * Users can create alerts for specific job titles, companies, and required skills
 * Alerts trigger notifications when matching jobs are found
 */
@Entity
@Table(name = "job_alerts", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_is_active", columnList = "is_active"),
    @Index(name = "idx_last_sent", columnList = "last_sent_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "job_title", nullable = false, length = 255)
    private String jobTitle;

    @Column(name = "company", length = 255)
    private String company;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "required_skills", columnDefinition = "TEXT")
    private String requiredSkills; // Comma-separated skills

    @Column(name = "salary_min")
    private Double salaryMin;

    @Column(name = "salary_max")
    private Double salaryMax;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "job_url", columnDefinition = "TEXT")
    private String jobUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false)
    private AlertFrequency frequency; // DAILY, WEEKLY, MONTHLY

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_sent_at")
    private LocalDateTime lastSentAt;

    @Column(name = "match_threshold")
    @Builder.Default
    private Double matchThreshold = 60.0; // Minimum match percentage required

    @Column(name = "send_email_notification", nullable = false)
    @Builder.Default
    private Boolean sendEmailNotification = true;

    @OneToMany(mappedBy = "jobAlert", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<JobMatch> matches = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (frequency == null) {
            frequency = AlertFrequency.WEEKLY;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Parse required skills from comma-separated string
     */
    public Set<String> getRequiredSkillsSet() {
        if (requiredSkills == null || requiredSkills.trim().isEmpty()) {
            return new HashSet<>();
        }
        Set<String> skills = new HashSet<>();
        for (String skill : requiredSkills.split(",")) {
            String trimmed = skill.trim();
            if (!trimmed.isEmpty()) {
                skills.add(trimmed);
            }
        }
        return skills;
    }

    /**
     * Check if alert should be processed based on frequency
     */
    public boolean shouldProcessAlert() {
        if (!isActive || lastSentAt == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        return switch (frequency) {
            case DAILY -> lastSentAt.plusDays(1).isBefore(now);
            case WEEKLY -> lastSentAt.plusWeeks(1).isBefore(now);
            case MONTHLY -> lastSentAt.plusMonths(1).isBefore(now);
        };
    }

    /**
     * Mark alert as sent
     */
    public void markAsSent() {
        this.lastSentAt = LocalDateTime.now();
    }

    /**
     * Enum for alert frequency
     */
    public enum AlertFrequency {
        DAILY("Every day"),
        WEEKLY("Every week"),
        MONTHLY("Every month");

        private final String displayName;

        AlertFrequency(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
