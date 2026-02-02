package com.resumeanalyzer.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class JobAlertService {
    private static final Logger log = LoggerFactory.getLogger(JobAlertService.class);
    @Autowired(required = false) private EmailService emailService;

    @Scheduled(fixedDelay = 86400000) // Daily
    public void checkForNewJobAlerts() {
        if (emailService == null) {
            log.warn("EmailService not configured");
            return;
        }
        log.info("Checking for new job alerts");
    }

    public void sendJobAlert(String email, String jobTitle, String percentage) {
        if (emailService != null) {
            emailService.sendJobAlert(email, jobTitle, percentage);
        }
    }
}

