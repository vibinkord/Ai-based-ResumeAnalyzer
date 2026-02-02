package com.resumeanalyzer.config;

import com.resumeanalyzer.model.entity.JobAlert;
import com.resumeanalyzer.service.JobAlertService;
import com.resumeanalyzer.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SchedulingConfig - Configuration for scheduled email tasks
 * Handles automatic processing of job alerts and digest emails
 */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulingConfig {

    private final JobAlertService jobAlertService;
    private final NotificationService notificationService;

    /**
     * Process daily job alerts
     * Runs every day at 8:00 AM
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void processDailyAlerts() {
        log.info("Starting scheduled task: Process daily job alerts");
        try {
            List<JobAlert> alertsToProcess = jobAlertService.getAlertsToProcess();
            long dailyAlerts = alertsToProcess.stream()
                    .filter(alert -> alert.getFrequency() == JobAlert.AlertFrequency.DAILY)
                    .count();

            log.info("Found {} daily alerts to process", dailyAlerts);

            alertsToProcess.stream()
                    .filter(alert -> alert.getFrequency() == JobAlert.AlertFrequency.DAILY)
                    .forEach(alert -> {
                        try {
                            int matchCount = alert.getMatches().size();
                            if (matchCount > 0 && alert.getSendEmailNotification()) {
                                notificationService.sendJobAlertEmail(alert.getUser().getId(), alert, matchCount);
                            }
                        } catch (Exception e) {
                            log.error("Error processing daily alert ID: {}", alert.getId(), e);
                        }
                    });

            log.info("Completed scheduled task: Process daily job alerts");
        } catch (Exception e) {
            log.error("Error in processDailyAlerts scheduled task", e);
        }
    }

    /**
     * Process weekly job alerts
     * Runs every Monday at 8:00 AM
     */
    @Scheduled(cron = "0 0 8 ? * MON")
    public void processWeeklyAlerts() {
        log.info("Starting scheduled task: Process weekly job alerts");
        try {
            List<JobAlert> alertsToProcess = jobAlertService.getAlertsToProcess();
            long weeklyAlerts = alertsToProcess.stream()
                    .filter(alert -> alert.getFrequency() == JobAlert.AlertFrequency.WEEKLY)
                    .count();

            log.info("Found {} weekly alerts to process", weeklyAlerts);

            alertsToProcess.stream()
                    .filter(alert -> alert.getFrequency() == JobAlert.AlertFrequency.WEEKLY)
                    .forEach(alert -> {
                        try {
                            int matchCount = alert.getMatches().size();
                            if (matchCount > 0 && alert.getSendEmailNotification()) {
                                notificationService.sendJobAlertEmail(alert.getUser().getId(), alert, matchCount);
                            }
                        } catch (Exception e) {
                            log.error("Error processing weekly alert ID: {}", alert.getId(), e);
                        }
                    });

            log.info("Completed scheduled task: Process weekly job alerts");
        } catch (Exception e) {
            log.error("Error in processWeeklyAlerts scheduled task", e);
        }
    }

    /**
     * Process monthly job alerts
     * Runs on the first day of every month at 8:00 AM
     */
    @Scheduled(cron = "0 0 8 1 * *")
    public void processMonthlyAlerts() {
        log.info("Starting scheduled task: Process monthly job alerts");
        try {
            List<JobAlert> alertsToProcess = jobAlertService.getAlertsToProcess();
            long monthlyAlerts = alertsToProcess.stream()
                    .filter(alert -> alert.getFrequency() == JobAlert.AlertFrequency.MONTHLY)
                    .count();

            log.info("Found {} monthly alerts to process", monthlyAlerts);

            alertsToProcess.stream()
                    .filter(alert -> alert.getFrequency() == JobAlert.AlertFrequency.MONTHLY)
                    .forEach(alert -> {
                        try {
                            int matchCount = alert.getMatches().size();
                            if (matchCount > 0 && alert.getSendEmailNotification()) {
                                notificationService.sendJobAlertEmail(alert.getUser().getId(), alert, matchCount);
                            }
                        } catch (Exception e) {
                            log.error("Error processing monthly alert ID: {}", alert.getId(), e);
                        }
                    });

            log.info("Completed scheduled task: Process monthly job alerts");
        } catch (Exception e) {
            log.error("Error in processMonthlyAlerts scheduled task", e);
        }
    }

    /**
     * Send weekly digest emails to all opted-in users
     * Runs every Monday at 9:00 AM
     */
    @Scheduled(cron = "0 0 9 ? * MON")
    public void sendWeeklyDigests() {
        log.info("Starting scheduled task: Send weekly digest emails");
        try {
            List<Long> userIds = notificationService.getUsersWithWeeklyDigestEnabled();
            log.info("Sending weekly digests to {} users", userIds.size());

            userIds.forEach(userId -> {
                try {
                    notificationService.sendWeeklyDigest(userId);
                } catch (Exception e) {
                    log.error("Error sending weekly digest to user ID: {}", userId, e);
                }
            });

            log.info("Completed scheduled task: Send weekly digest emails");
        } catch (Exception e) {
            log.error("Error in sendWeeklyDigests scheduled task", e);
        }
    }

    /**
     * Process all pending alerts (flexible schedule)
     * Runs every 2 hours
     */
    @Scheduled(fixedDelay = 7200000) // 2 hours in milliseconds
    public void processAllAlerts() {
        log.debug("Starting scheduled task: Process all pending alerts");
        try {
            List<JobAlert> alertsToProcess = jobAlertService.getAlertsToProcess();
            log.debug("Found {} total alerts to process", alertsToProcess.size());

            alertsToProcess.forEach(alert -> {
                try {
                    if (alert.getSendEmailNotification() && alert.getMatches().size() > 0) {
                        notificationService.sendJobAlertEmail(alert.getUser().getId(), alert, alert.getMatches().size());
                        jobAlertService.markAlertAsProcessed(alert.getId());
                    }
                } catch (Exception e) {
                    log.error("Error processing alert ID: {}", alert.getId(), e);
                }
            });

            log.debug("Completed scheduled task: Process all pending alerts");
        } catch (Exception e) {
            log.error("Error in processAllAlerts scheduled task", e);
        }
    }

    /**
     * Clean up old notification logs (archive)
     * Runs daily at 2:00 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupOldNotifications() {
        log.info("Starting scheduled task: Cleanup old notifications");
        try {
            // Implementation would depend on a NotificationLog entity
            // For now, just log the task execution
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            log.info("Cleaning up notifications older than: {}", thirtyDaysAgo);
            // TODO: Implement notification log cleanup when NotificationLog entity is added
            log.info("Completed scheduled task: Cleanup old notifications");
        } catch (Exception e) {
            log.error("Error in cleanupOldNotifications scheduled task", e);
        }
    }
}
