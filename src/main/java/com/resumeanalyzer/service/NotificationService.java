package com.resumeanalyzer.service;

import com.resumeanalyzer.model.dto.NotificationPreferenceRequest;
import com.resumeanalyzer.model.dto.NotificationPreferenceResponse;
import com.resumeanalyzer.model.entity.JobAlert;
import com.resumeanalyzer.model.entity.JobMatch;
import com.resumeanalyzer.model.entity.NotificationPreference;
import com.resumeanalyzer.model.entity.User;
import com.resumeanalyzer.repository.JobAlertRepository;
import com.resumeanalyzer.repository.JobMatchRepository;
import com.resumeanalyzer.repository.NotificationPreferenceRepository;
import com.resumeanalyzer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * NotificationService - Coordinates all notification sending and preferences
 * Handles notification preferences, email sending, and scheduling
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NotificationService {

    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final UserRepository userRepository;
    private final JobAlertRepository jobAlertRepository;
    private final JobMatchRepository jobMatchRepository;
    private final EmailService emailService;

    /**
     * Get notification preferences for a user
     *
     * @param userId the user's ID
     * @return NotificationPreferenceResponse
     * @throws IllegalArgumentException if user not found
     */
    public NotificationPreferenceResponse getUserPreferences(Long userId) {
        log.info("Fetching notification preferences for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        NotificationPreference preference = user.getNotificationPreference();
        if (preference == null) {
            // Create default preferences if not exist
            preference = createDefaultPreferences(user);
        }

        return NotificationPreferenceResponse.fromEntity(preference);
    }

    /**
     * Update notification preferences for a user
     *
     * @param userId  the user's ID
     * @param request the update request
     * @return updated NotificationPreferenceResponse
     * @throws IllegalArgumentException if user not found
     */
    @Transactional
    public NotificationPreferenceResponse updateUserPreferences(Long userId, NotificationPreferenceRequest request) {
        log.info("Updating notification preferences for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        NotificationPreference preference = user.getNotificationPreference();
        if (preference == null) {
            preference = createDefaultPreferences(user);
        }

        // Update preference fields (using correct field names from entity)
        preference.setEmailEnabled(request.getEmailEnabled());
        preference.setJobAlertEmailEnabled(request.getJobAlertEmailEnabled());
        preference.setMatchNotificationEnabled(request.getMatchNotificationEnabled());
        preference.setWeeklyDigestEnabled(request.getWeeklyDigestEnabled());
        preference.setPreferredHour(request.getPreferredNotificationHour());
        preference.setPreferredMinute(request.getPreferredNotificationMinute());
        preference.setPreferredDayOfWeek(request.getPreferredDigestDayOfWeek());
        preference.setTimezone(request.getTimezone());
        preference.setMinMatchThreshold(request.getMinimumMatchThreshold());

        NotificationPreference updated = notificationPreferenceRepository.save(preference);
        log.info("Notification preferences updated successfully for user: {}", userId);

        return NotificationPreferenceResponse.fromEntity(updated);
    }

    /**
     * Create default notification preferences for a user
     *
     * @param user the user
     * @return the created NotificationPreference
     */
    @Transactional
    public NotificationPreference createDefaultPreferences(User user) {
        log.debug("Creating default notification preferences for user: {}", user.getId());

        NotificationPreference preference = NotificationPreference.builder()
                .user(user)
                .emailEnabled(true)
                .jobAlertEmailEnabled(true)
                .matchNotificationEnabled(true)
                .weeklyDigestEnabled(true)
                .analysisReminderEnabled(false)
                .preferredHour(9)
                .preferredMinute(0)
                .preferredDayOfWeek(1) // Monday
                .timezone("UTC")
                .minMatchThreshold(60.0)
                .digestFrequency(NotificationPreference.DigestFrequency.WEEKLY)
                .optedIn(true)
                .build();

        NotificationPreference saved = notificationPreferenceRepository.save(preference);
        user.setNotificationPreference(saved);
        userRepository.save(user);

        return saved;
    }

    /**
     * Opt in user for email notifications
     *
     * @param userId the user's ID
     */
    @Transactional
    public void optInForNotifications(Long userId) {
        log.info("Opting in user ID: {} for email notifications", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        NotificationPreference preference = user.getNotificationPreference();
        if (preference == null) {
            preference = createDefaultPreferences(user);
        } else {
            preference.setEmailEnabled(true);
            notificationPreferenceRepository.save(preference);
        }
    }

    /**
     * Opt out user from email notifications
     *
     * @param userId the user's ID
     */
    @Transactional
    public void optOutFromNotifications(Long userId) {
        log.info("Opting out user ID: {} from email notifications", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        NotificationPreference preference = user.getNotificationPreference();
        if (preference != null) {
            preference.setEmailEnabled(false);
            notificationPreferenceRepository.save(preference);
        }
    }

    /**
     * Check if user should receive notifications
     *
     * @param userId the user's ID
     * @return true if user opted in
     */
    public boolean shouldSendNotification(Long userId) {
        NotificationPreference preference = notificationPreferenceRepository.findByUserId(userId).orElse(null);
        if (preference == null) {
            return false;
        }
        return preference.getEmailEnabled() && preference.getJobAlertEmailEnabled();
    }

    /**
     * Check if user should receive digest notifications
     *
     * @param userId the user's ID
     * @return true if user enabled weekly digest
     */
    public boolean shouldSendDigest(Long userId) {
        NotificationPreference preference = notificationPreferenceRepository.findByUserId(userId).orElse(null);
        if (preference == null) {
            return false;
        }
        return preference.getEmailEnabled() && preference.getWeeklyDigestEnabled();
    }

    /**
     * Send a job alert notification email
     *
     * @param userId      the user's ID
     * @param alert       the job alert
     * @param matchCount  the number of matches found
     * @return true if email sent successfully
     */
    @Transactional
    public boolean sendJobAlertEmail(Long userId, JobAlert alert, int matchCount) {
        log.info("Sending job alert email for alert ID: {} to user: {}", alert.getId(), userId);

        if (!shouldSendNotification(userId)) {
            log.debug("User ID: {} has opted out from notifications", userId);
            return false;
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("User not found with ID: {}", userId);
            return false;
        }

        User user = userOpt.get();
        
        // Get the first match if available for detailed email
        JobMatch firstMatch = alert.getMatches().stream().findFirst().orElse(null);
        
        boolean sent;
        if (firstMatch != null) {
            sent = emailService.sendJobAlertEmail(
                    user.getEmail(),
                    user.getFullName(),
                    alert.getJobTitle(),
                    alert.getCompany() != null ? alert.getCompany() : "Unknown Company",
                    alert.getJobUrl(),
                    firstMatch.getMatchScore(),
                    firstMatch.getMatchedSkills(),
                    firstMatch.getMissingSkills()
            );
        } else {
            // Fallback to generic job alert email
            sent = emailService.sendPlainTextEmail(
                    user.getEmail(),
                    String.format("Job Alert: %d matches for '%s'", matchCount, alert.getJobTitle()),
                    String.format("We found %d matches for your job alert: %s\n\nPlease login to view details.",
                            matchCount, alert.getJobTitle())
            );
        }

        if (sent) {
            alert.markAsSent();
            jobAlertRepository.save(alert);
            log.info("Job alert email sent successfully for alert ID: {}", alert.getId());
        } else {
            log.warn("Failed to send job alert email for alert ID: {}", alert.getId());
        }

        return sent;
    }

    /**
     * Send weekly digest email to user
     *
     * @param userId the user's ID
     * @return true if email sent successfully
     */
    @Transactional
    public boolean sendWeeklyDigest(Long userId) {
        log.info("Sending weekly digest to user ID: {}", userId);

        if (!shouldSendDigest(userId)) {
            log.debug("User ID: {} has opted out from weekly digest", userId);
            return false;
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("User not found with ID: {}", userId);
            return false;
        }

        User user = userOpt.get();

        // Get all active alerts with matches
        List<JobAlert> alerts = jobAlertRepository.findByUserIdAndIsActiveTrue(userId);
        List<JobMatch> allMatches = alerts.stream()
                .flatMap(alert -> alert.getMatches().stream())
                .filter(match -> !match.getNotificationSent())
                .collect(Collectors.toList());

        if (allMatches.isEmpty()) {
            log.debug("No new matches for user ID: {}, skipping digest", userId);
            return false;
        }

        // Build digest content
        StringBuilder digestContent = new StringBuilder();
        digestContent.append("Weekly Job Match Summary\n\n");
        digestContent.append("Total Matches: ").append(allMatches.size()).append("\n\n");
        
        for (JobMatch match : allMatches) {
            digestContent.append("- Match Score: ").append(String.format("%.1f%%", match.getMatchScore())).append("\n");
            digestContent.append("  Skills: ").append(match.getMatchedSkills()).append("\n\n");
        }

        String subject = String.format("Weekly Job Match Digest - %d new matches", allMatches.size());
        boolean sent = emailService.sendWeeklyDigestEmail(user.getEmail(), user.getFullName(), digestContent.toString());

        if (sent) {
            log.info("Weekly digest email sent successfully to user: {}", userId);
        } else {
            log.warn("Failed to send weekly digest email to user: {}", userId);
        }

        return sent;
    }

    /**
     * Send welcome email to new user
     *
     * @param userId the user's ID
     * @return true if email sent successfully
     */
    public boolean sendWelcomeEmail(Long userId) {
        log.info("Sending welcome email to user ID: {}", userId);

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("User not found with ID: {}", userId);
            return false;
        }

        User user = userOpt.get();
        return emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());
    }

    /**
     * Get all users who opted in for email notifications
     *
     * @return list of user IDs
     */
    public List<Long> getAllEmailOptedInUsers() {
        log.debug("Fetching all users opted in for email notifications");
        return notificationPreferenceRepository.findAllEmailOptedInUsers()
                .stream()
                .map(pref -> pref.getUser().getId())
                .collect(Collectors.toList());
    }

    /**
     * Get all users who enabled job alert emails
     *
     * @return list of user IDs
     */
    public List<Long> getUsersWithJobAlertEmailsEnabled() {
        log.debug("Fetching users with job alert emails enabled");
        return notificationPreferenceRepository.findUsersWithJobAlertEmailsEnabled()
                .stream()
                .map(pref -> pref.getUser().getId())
                .collect(Collectors.toList());
    }

    /**
     * Get all users with weekly digest enabled
     *
     * @return list of user IDs
     */
    public List<Long> getUsersWithWeeklyDigestEnabled() {
        log.debug("Fetching users with weekly digest enabled");
        return notificationPreferenceRepository.findUsersWithWeeklyDigestEnabled()
                .stream()
                .map(pref -> pref.getUser().getId())
                .collect(Collectors.toList());
    }

    /**
     * Count total users with emails enabled
     *
     * @return total count
     */
    public long getTotalEmailEnabledUsers() {
        return notificationPreferenceRepository.countEmailEnabledUsers();
    }
}
