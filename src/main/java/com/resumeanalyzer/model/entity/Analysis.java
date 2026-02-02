package com.resumeanalyzer.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "analyses", indexes = {
    @Index(name = "idx_analyses_resume_id", columnList = "resume_id"),
    @Index(name = "idx_analyses_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    @Column(nullable = false)
    private Double matchPercentage;

    @Column(columnDefinition = "TEXT")
    private String matchedSkills; // Stored as JSON array string

    @Column(columnDefinition = "TEXT")
    private String missingSkills; // Stored as JSON array string

    @Column(columnDefinition = "TEXT")
    private String suggestions; // Stored as JSON array string

    @Column(columnDefinition = "TEXT")
    private String aiSuggestions; // Stored as JSON array string

    @Column(columnDefinition = "TEXT")
    private String report;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Constructor for creating a new analysis
     */
    public Analysis(Resume resume, String jobDescription, Double matchPercentage) {
        this.resume = resume;
        this.jobDescription = jobDescription;
        this.matchPercentage = matchPercentage;
    }

    /**
     * Get match percentage as formatted string
     */
    public String getFormattedMatchPercentage() {
        return String.format("%.1f%%", matchPercentage);
    }

    /**
     * Check if this is a good match (>= 70%)
     */
    public boolean isGoodMatch() {
        return matchPercentage >= 70.0;
    }

    /**
     * Check if this is an acceptable match (50-70%)
     */
    public boolean isAcceptableMatch() {
        return matchPercentage >= 50.0 && matchPercentage < 70.0;
    }

    /**
     * Check if this is a poor match (< 50%)
     */
    public boolean isPoorMatch() {
        return matchPercentage < 50.0;
    }
}
