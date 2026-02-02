package com.resumeanalyzer.email;

import com.resumeanalyzer.config.EmailProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    @Autowired public JavaMailSender mailSender;
    @Autowired public EmailProperties emailProperties;

    public void sendAnalysisResultEmail(String toEmail, String resumeName, double matchPercentage) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(emailProperties.getFrom());
            msg.setTo(toEmail);
            msg.setSubject("Resume Analysis Complete: " + resumeName);
            msg.setText("Your resume analysis is complete!\n\nMatch: " + String.format("%.1f", matchPercentage) + "%\n\nCheck your dashboard for details.");
            mailSender.send(msg);
            log.info("Email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email", e);
        }
    }

    public void sendJobAlert(String toEmail, String jobTitle, String matchPercentage) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(emailProperties.getFrom());
            msg.setTo(toEmail);
            msg.setSubject("New Job Alert: " + jobTitle);
            msg.setText("A job matching your profile is available!\n\nMatch: " + matchPercentage + "%");
            mailSender.send(msg);
            log.info("Job alert sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send job alert", e);
        }
    }
}

