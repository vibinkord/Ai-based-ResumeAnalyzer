package com.resumeanalyzer.model.dto;

import com.resumeanalyzer.model.entity.NotificationPreference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * NotificationPreferenceResponse DTO - Response object for notification preferences
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreferenceResponse {

    private Long id;
    private Boolean emailEnabled;
    private Boolean jobAlertEmailEnabled;
    private Boolean matchNotificationEnabled;
    private Boolean weeklyDigestEnabled;
    private Integer preferredNotificationHour;
    private Integer preferredNotificationMinute;
    private Integer preferredDigestDayOfWeek;
    private String timezone;
    private Double minimumMatchThreshold;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String preferredNotificationTime; // Formatted string like "09:30"
    private String digestDayName; // Monday, Tuesday, etc.

    /**
     * Convert entity to DTO
     */
    public static NotificationPreferenceResponse fromEntity(NotificationPreference entity) {
        String dayName = getDayName(entity.getPreferredDayOfWeek());
        String timeString = String.format("%02d:%02d",
                entity.getPreferredHour() != null ? entity.getPreferredHour() : 0,
                entity.getPreferredMinute() != null ? entity.getPreferredMinute() : 0);

        return NotificationPreferenceResponse.builder()
                .id(entity.getId())
                .emailEnabled(entity.getEmailEnabled())
                .jobAlertEmailEnabled(entity.getJobAlertEmailEnabled())
                .matchNotificationEnabled(entity.getMatchNotificationEnabled())
                .weeklyDigestEnabled(entity.getWeeklyDigestEnabled())
                .preferredNotificationHour(entity.getPreferredHour())
                .preferredNotificationMinute(entity.getPreferredMinute())
                .preferredDigestDayOfWeek(entity.getPreferredDayOfWeek())
                .timezone(entity.getTimezone())
                .minimumMatchThreshold(entity.getMinMatchThreshold())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .preferredNotificationTime(timeString)
                .digestDayName(dayName)
                .build();
    }

    /**
     * Get day name from day of week number
     */
    private static String getDayName(Integer dayOfWeek) {
        if (dayOfWeek == null) return "Monday";
        return switch (dayOfWeek) {
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            case 3 -> "Wednesday";
            case 4 -> "Thursday";
            case 5 -> "Friday";
            case 6 -> "Saturday";
            case 7 -> "Sunday";
            default -> "Monday";
        };
    }
}
