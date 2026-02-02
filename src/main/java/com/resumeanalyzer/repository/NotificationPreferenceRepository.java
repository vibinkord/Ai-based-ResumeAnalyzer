package com.resumeanalyzer.repository;

import com.resumeanalyzer.model.entity.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for NotificationPreference entity
 * Provides database operations for user notification settings
 */
@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {

    /**
     * Find notification preference for a specific user
     */
    Optional<NotificationPreference> findByUserId(Long userId);

    /**
     * Find all users who want to receive email notifications
     */
    @Query("SELECT np FROM NotificationPreference np WHERE np.emailEnabled = true AND np.optedIn = true")
    List<NotificationPreference> findAllEmailOptedInUsers();

    /**
     * Find users who want job alert emails
     */
    @Query("SELECT np FROM NotificationPreference np WHERE np.jobAlertEmailEnabled = true AND " +
           "np.emailEnabled = true AND np.optedIn = true")
    List<NotificationPreference> findUsersWithJobAlertEmailsEnabled();

    /**
     * Find users who want weekly digest
     */
    @Query("SELECT np FROM NotificationPreference np WHERE np.weeklyDigestEnabled = true AND " +
           "np.emailEnabled = true AND np.optedIn = true")
    List<NotificationPreference> findUsersWithWeeklyDigestEnabled();

    /**
     * Find users opted out of notifications
     */
    @Query("SELECT np FROM NotificationPreference np WHERE np.optedIn = false")
    List<NotificationPreference> findAllOptedOutUsers();

    /**
     * Count users with email notifications enabled
     */
    @Query("SELECT COUNT(np) FROM NotificationPreference np WHERE np.emailEnabled = true AND np.optedIn = true")
    long countEmailEnabledUsers();

    /**
     * Check if user has notifications enabled
     */
    @Query("SELECT CASE WHEN COUNT(np) > 0 THEN true ELSE false END FROM NotificationPreference np " +
           "WHERE np.user.id = :userId AND np.emailEnabled = true AND np.optedIn = true")
    boolean isNotificationEnabledForUser(@Param("userId") Long userId);

    /**
     * Delete preference for a user (when account is deleted)
     */
    void deleteByUserId(Long userId);
}
