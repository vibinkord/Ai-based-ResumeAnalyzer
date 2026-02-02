-- V4__Add_Job_Alerts_And_Notifications.sql
-- Flyway migration to add job alerts and notification management tables

-- Create job_alerts table
CREATE TABLE job_alerts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    job_title VARCHAR(255) NOT NULL,
    company VARCHAR(255),
    description TEXT,
    required_skills TEXT,
    salary_min DECIMAL(10, 2),
    salary_max DECIMAL(10, 2),
    location VARCHAR(255),
    job_url TEXT,
    frequency VARCHAR(20) NOT NULL DEFAULT 'WEEKLY',
    is_active BOOLEAN NOT NULL DEFAULT true,
    match_threshold DECIMAL(5, 2) DEFAULT 60.0,
    send_email_notification BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_sent_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_is_active (is_active),
    INDEX idx_last_sent (last_sent_at)
);

-- Create job_matches table
CREATE TABLE job_matches (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_alert_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    matched_job_title VARCHAR(255) NOT NULL,
    matched_company VARCHAR(255),
    job_url TEXT,
    match_score DECIMAL(5, 2) NOT NULL,
    matched_skills TEXT,
    missing_skills TEXT,
    notification_sent BOOLEAN NOT NULL DEFAULT false,
    notification_sent_at TIMESTAMP,
    is_viewed BOOLEAN NOT NULL DEFAULT false,
    is_interested BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_alert_id) REFERENCES job_alerts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_job_alert_id (job_alert_id),
    INDEX idx_user_id (user_id),
    INDEX idx_notification_sent (notification_sent)
);

-- Create notification_preferences table
CREATE TABLE notification_preferences (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    email_enabled BOOLEAN NOT NULL DEFAULT true,
    job_alert_email_enabled BOOLEAN NOT NULL DEFAULT true,
    match_notification_enabled BOOLEAN NOT NULL DEFAULT true,
    weekly_digest_enabled BOOLEAN NOT NULL DEFAULT true,
    analysis_reminder_enabled BOOLEAN NOT NULL DEFAULT false,
    digest_frequency VARCHAR(20) NOT NULL DEFAULT 'WEEKLY',
    preferred_hour INTEGER DEFAULT 9,
    preferred_minute INTEGER DEFAULT 0,
    preferred_day_of_week INTEGER DEFAULT 1,
    min_match_threshold DECIMAL(5, 2) DEFAULT 60.0,
    timezone VARCHAR(50) DEFAULT 'UTC',
    opted_in BOOLEAN NOT NULL DEFAULT true,
    opted_in_at TIMESTAMP,
    opted_out_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_digest_sent_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
);

-- Create notification_log table for audit trail
CREATE TABLE notification_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    recipient_email VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    sent_at TIMESTAMP,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_sent_at (sent_at)
);

-- Add comments
ALTER TABLE job_alerts COMMENT = 'Job alert subscriptions for users';
ALTER TABLE job_matches COMMENT = 'Matched jobs for job alerts';
ALTER TABLE notification_preferences COMMENT = 'User notification settings and preferences';
ALTER TABLE notification_log COMMENT = 'Log of all notifications sent to users';
