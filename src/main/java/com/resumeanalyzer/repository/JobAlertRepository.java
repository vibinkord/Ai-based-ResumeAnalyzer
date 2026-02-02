package com.resumeanalyzer.repository;

import com.resumeanalyzer.model.entity.JobAlert;
import com.resumeanalyzer.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for JobAlert entity
 * Provides database operations for job alert management
 */
@Repository
public interface JobAlertRepository extends JpaRepository<JobAlert, Long> {

    /**
     * Find all active alerts for a specific user
     */
    List<JobAlert> findByUserIdAndIsActiveTrue(Long userId);

    /**
     * Find all alerts for a user
     */
    List<JobAlert> findByUserId(Long userId);

    /**
     * Find all active alerts
     */
    List<JobAlert> findByIsActiveTrue();

    /**
     * Find alerts that need to be processed (should send based on frequency)
     */
    @Query("SELECT ja FROM JobAlert ja WHERE ja.isActive = true AND " +
           "(ja.lastSentAt IS NULL OR " +
           "(ja.frequency = 'DAILY' AND ja.lastSentAt < :oneDayAgo) OR " +
           "(ja.frequency = 'WEEKLY' AND ja.lastSentAt < :oneWeekAgo) OR " +
           "(ja.frequency = 'MONTHLY' AND ja.lastSentAt < :oneMonthAgo))")
    List<JobAlert> findAlertsToProcess(
        @Param("oneDayAgo") LocalDateTime oneDayAgo,
        @Param("oneWeekAgo") LocalDateTime oneWeekAgo,
        @Param("oneMonthAgo") LocalDateTime oneMonthAgo
    );

    /**
     * Find alert by ID and user ID (ensures user owns the alert)
     */
    Optional<JobAlert> findByIdAndUserId(Long id, Long userId);

    /**
     * Find alerts for a specific company
     */
    List<JobAlert> findByCompanyAndIsActiveTrue(String company);

    /**
     * Find alerts containing specific job title
     */
    @Query("SELECT ja FROM JobAlert ja WHERE ja.user.id = :userId AND " +
           "LOWER(ja.jobTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) AND ja.isActive = true")
    List<JobAlert> findByUserIdAndJobTitleContaining(
        @Param("userId") Long userId,
        @Param("keyword") String keyword
    );

    /**
     * Count active alerts for a user
     */
    long countByUserIdAndIsActiveTrue(Long userId);

    /**
     * Delete all alerts for a user (when account is deleted)
     */
    void deleteByUserId(Long userId);

    /**
     * Find all alerts for a user with pagination
     */
    Page<JobAlert> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Count total alerts for a user
     */
    long countByUserId(Long userId);
}
