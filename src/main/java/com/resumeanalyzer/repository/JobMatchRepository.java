package com.resumeanalyzer.repository;

import com.resumeanalyzer.model.entity.JobMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for JobMatch entity
 * Provides database operations for job match tracking
 */
@Repository
public interface JobMatchRepository extends JpaRepository<JobMatch, Long> {

    /**
     * Find all matches for a specific job alert
     */
    List<JobMatch> findByJobAlertId(Long alertId);

    /**
     * Find all matches for a specific user
     */
    List<JobMatch> findByUserId(Long userId);

    /**
     * Find unviewed matches for a user
     */
    List<JobMatch> findByUserIdAndIsViewedFalse(Long userId);

    /**
     * Find matches that haven't been notified yet
     */
    List<JobMatch> findByNotificationSentFalse();

    /**
     * Find matches that need notification within time window
     */
    @Query("SELECT jm FROM JobMatch jm WHERE jm.notificationSent = false AND " +
           "jm.createdAt > :since")
    List<JobMatch> findUnnotifiedMatchesSince(
        @Param("since") LocalDateTime since
    );

    /**
     * Find matches above score threshold for user
     */
    @Query("SELECT jm FROM JobMatch jm WHERE jm.user.id = :userId AND " +
           "jm.matchScore >= :threshold ORDER BY jm.matchScore DESC")
    List<JobMatch> findByUserIdAndScoreThreshold(
        @Param("userId") Long userId,
        @Param("threshold") Double threshold
    );

    /**
     * Find user's favorite/interested matches
     */
    List<JobMatch> findByUserIdAndIsInterestedTrue(Long userId);

    /**
     * Count unviewed matches for user
     */
    long countByUserIdAndIsViewedFalse(Long userId);

    /**
     * Count unnotified matches
     */
    long countByNotificationSentFalse();

    /**
     * Delete matches older than specified date
     */
    void deleteByCreatedAtBefore(LocalDateTime date);

    /**
     * Delete all matches for user
     */
    void deleteByUserId(Long userId);
}
