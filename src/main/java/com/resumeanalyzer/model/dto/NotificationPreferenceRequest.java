package com.resumeanalyzer.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * NotificationPreferenceRequest DTO - Request body for updating notification preferences
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreferenceRequest {

    @NotNull(message = "Email enabled preference is required")
    private Boolean emailEnabled;

    @NotNull(message = "Job alert email preference is required")
    private Boolean jobAlertEmailEnabled;

    @NotNull(message = "Match notification preference is required")
    private Boolean matchNotificationEnabled;

    @NotNull(message = "Weekly digest preference is required")
    private Boolean weeklyDigestEnabled;

    @Min(value = 0, message = "Hour must be between 0 and 23")
    @Max(value = 23, message = "Hour must be between 0 and 23")
    private Integer preferredNotificationHour;

    @Min(value = 0, message = "Minute must be between 0 and 59")
    @Max(value = 59, message = "Minute must be between 0 and 59")
    private Integer preferredNotificationMinute;

    @Min(value = 1, message = "Day of week must be between 1 (Monday) and 7 (Sunday)")
    @Max(value = 7, message = "Day of week must be between 1 (Monday) and 7 (Sunday)")
    private Integer preferredDigestDayOfWeek;

    private String timezone;

    @Min(value = 0, message = "Match threshold must be at least 0")
    @Max(value = 100, message = "Match threshold must not exceed 100")
    private Double minimumMatchThreshold;
}
