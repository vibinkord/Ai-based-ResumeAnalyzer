package com.resumeanalyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.Arrays;
import java.util.List;

/**
 * EmailService - Handles all email sending operations
 * Supports both plain text and HTML emails
 * Integrates with Spring Mail for SMTP configuration
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@resumeanalyzer.io}")
    private String fromEmail;

    @Value("${spring.mail.from-name:AI Resume Analyzer}")
    private String fromName;

    @Value("${mail.enabled:true}")
    private boolean mailEnabled;

    /**
     * Send a plain text email
     */
    public boolean sendPlainTextEmail(String to, String subject, String body) {
        return sendPlainTextEmail(Arrays.asList(to), subject, body);
    }

    /**
     * Send plain text email to multiple recipients
     */
    public boolean sendPlainTextEmail(List<String> recipients, String subject, String body) {
        if (!mailEnabled) {
            log.debug("Email sending is disabled. Skipping email to: {}", recipients);
            return true; // Consider it successful even if disabled
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(recipients.toArray(new String[0]));
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Plain text email sent successfully to {} recipients. Subject: {}", recipients.size(), subject);
            return true;

        } catch (Exception e) {
            log.error("Failed to send plain text email to {}. Error: {}", recipients, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Send HTML email
     */
    public boolean sendHtmlEmail(String to, String subject, String htmlContent) {
        return sendHtmlEmail(Arrays.asList(to), subject, htmlContent);
    }

    /**
     * Send HTML email to multiple recipients
     */
    public boolean sendHtmlEmail(List<String> recipients, String subject, String htmlContent) {
        if (!mailEnabled) {
            log.debug("Email sending is disabled. Skipping HTML email to: {}", recipients);
            return true; // Consider it successful even if disabled
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(recipients.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(message);
            log.info("HTML email sent successfully to {} recipients. Subject: {}", recipients.size(), subject);
            return true;

        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("Failed to send HTML email to {}. Error: {}", recipients, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Send email with attachment
     */
    public boolean sendEmailWithAttachment(String to, String subject, String htmlContent, 
                                          String attachmentFileName, byte[] attachmentData) {
        if (!mailEnabled) {
            log.debug("Email sending is disabled. Skipping email with attachment to: {}", to);
            return true;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.addAttachment(attachmentFileName, () -> new java.io.ByteArrayInputStream(attachmentData));

            mailSender.send(message);
            log.info("Email with attachment sent successfully to: {}. Subject: {}", to, subject);
            return true;

        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("Failed to send email with attachment to {}. Error: {}", to, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Send job alert notification email
     */
    public boolean sendJobAlertEmail(String recipientEmail, String recipientName, String jobTitle, 
                                    String company, String jobUrl, double matchScore, 
                                    String matchedSkills, String missingSkills) {
        String htmlContent = buildJobAlertTemplate(recipientName, jobTitle, company, jobUrl, 
                                                   matchScore, matchedSkills, missingSkills);
        String subject = String.format("🎯 New Job Match: %s at %s (%d%% match)", jobTitle, company, (int)matchScore);
        
        return sendHtmlEmail(recipientEmail, subject, htmlContent);
    }

    /**
     * Send weekly digest email
     */
    public boolean sendWeeklyDigestEmail(String recipientEmail, String recipientName, String digest) {
        String htmlContent = buildWeeklyDigestTemplate(recipientName, digest);
        String subject = "📊 Your Weekly Job Alert Digest - Resume Analyzer";
        
        return sendHtmlEmail(recipientEmail, subject, htmlContent);
    }

    /**
     * Send welcome email
     */
    public boolean sendWelcomeEmail(String recipientEmail, String recipientName) {
        String htmlContent = buildWelcomeTemplate(recipientName);
        String subject = "🎉 Welcome to AI Resume Analyzer!";
        
        return sendHtmlEmail(recipientEmail, subject, htmlContent);
    }

    /**
     * Send password reset email
     */
    public boolean sendPasswordResetEmail(String recipientEmail, String resetLink) {
        String htmlContent = buildPasswordResetTemplate(resetLink);
        String subject = "🔑 Reset Your Password - Resume Analyzer";
        
        return sendHtmlEmail(recipientEmail, subject, htmlContent);
    }

    /**
     * Build job alert email template
     */
    private String buildJobAlertTemplate(String name, String jobTitle, String company, String jobUrl,
                                        double matchScore, String matchedSkills, String missingSkills) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); 
                              color: white; padding: 20px; border-radius: 8px 8px 0 0; text-align: center; }
                    .content { background: #f8fafc; padding: 20px; border-radius: 0 0 8px 8px; }
                    .score { font-size: 2em; font-weight: bold; color: #22c55e; }
                    .job-title { font-size: 1.5em; color: #0f172a; margin: 10px 0; }
                    .skills { background: white; padding: 15px; border-radius: 6px; margin: 15px 0; }
                    .matched { color: #22c55e; }
                    .missing { color: #ef4444; }
                    .btn { display: inline-block; background: #3b82f6; color: white; 
                           padding: 10px 20px; text-decoration: none; border-radius: 6px; margin: 20px 0; }
                    .footer { text-align: center; color: #999; font-size: 0.9em; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎯 New Job Match Found!</h1>
                    </div>
                    <div class="content">
                        <p>Hi <strong>%s</strong>,</p>
                        <p>We found a job that matches your profile:</p>
                        
                        <div class="job-title">%s</div>
                        <p><strong>Company:</strong> %s</p>
                        <div class="score">%d%% Match</div>
                        
                        <div class="skills">
                            <h3>Matched Skills:</h3>
                            <p class="matched">%s</p>
                            
                            <h3>Skills to Develop:</h3>
                            <p class="missing">%s</p>
                        </div>
                        
                        <a href="%s" class="btn">View Job Details →</a>
                        
                        <p>Best regards,<br><strong>AI Resume Analyzer Team</strong></p>
                    </div>
                    <div class="footer">
                        <p>You received this email because you subscribed to job alerts.<br>
                        <a href="#unsubscribe">Manage preferences</a></p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(name, jobTitle, company, (int)matchScore, matchedSkills, missingSkills, jobUrl);
    }

    /**
     * Build weekly digest email template
     */
    private String buildWeeklyDigestTemplate(String name, String digest) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); 
                              color: white; padding: 20px; border-radius: 8px 8px 0 0; text-align: center; }
                    .content { background: #f8fafc; padding: 20px; border-radius: 0 0 8px 8px; }
                    .digest { background: white; padding: 15px; border-radius: 6px; margin: 15px 0; }
                    .btn { display: inline-block; background: #3b82f6; color: white; 
                           padding: 10px 20px; text-decoration: none; border-radius: 6px; margin: 20px 0; }
                    .footer { text-align: center; color: #999; font-size: 0.9em; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>📊 Your Weekly Job Alert Digest</h1>
                    </div>
                    <div class="content">
                        <p>Hi <strong>%s</strong>,</p>
                        <p>Here's a summary of job matches from this week:</p>
                        
                        <div class="digest">
                            %s
                        </div>
                        
                        <a href="#view-all" class="btn">View All Matches →</a>
                        
                        <p>Best regards,<br><strong>AI Resume Analyzer Team</strong></p>
                    </div>
                    <div class="footer">
                        <p>You received this email because you subscribed to weekly digests.<br>
                        <a href="#unsubscribe">Manage preferences</a></p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(name, digest);
    }

    /**
     * Build welcome email template
     */
    private String buildWelcomeTemplate(String name) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); 
                              color: white; padding: 20px; border-radius: 8px 8px 0 0; text-align: center; }
                    .content { background: #f8fafc; padding: 20px; border-radius: 0 0 8px 8px; }
                    .features { background: white; padding: 15px; border-radius: 6px; margin: 15px 0; }
                    .btn { display: inline-block; background: #3b82f6; color: white; 
                           padding: 10px 20px; text-decoration: none; border-radius: 6px; margin: 20px 0; }
                    .footer { text-align: center; color: #999; font-size: 0.9em; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 Welcome to AI Resume Analyzer!</h1>
                    </div>
                    <div class="content">
                        <p>Hi <strong>%s</strong>,</p>
                        <p>Thank you for joining us! We're excited to help you advance your career.</p>
                        
                        <div class="features">
                            <h3>✨ Here's what you can do:</h3>
                            <ul>
                                <li><strong>Analyze Your Resume</strong> - Get AI-powered insights and suggestions</li>
                                <li><strong>Set Job Alerts</strong> - Receive notifications for matching opportunities</li>
                                <li><strong>Track History</strong> - Review all your analyses in one place</li>
                                <li><strong>Customize Preferences</strong> - Control your notification settings</li>
                            </ul>
                        </div>
                        
                        <a href="#get-started" class="btn">Get Started Now →</a>
                        
                        <p><strong>Quick Tips:</strong></p>
                        <ul>
                            <li>Upload your resume and a job description to get started</li>
                            <li>Check your match score and see which skills you need to develop</li>
                            <li>Create job alerts to be notified of matching opportunities</li>
                            <li>Customize your notification preferences anytime</li>
                        </ul>
                        
                        <p>Best regards,<br><strong>AI Resume Analyzer Team</strong></p>
                    </div>
                    <div class="footer">
                        <p>Have questions? Contact our support team.<br>
                        <a href="#help">Help Center</a> | <a href="#settings">Settings</a></p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(name);
    }

    /**
     * Build password reset email template
     */
    private String buildPasswordResetTemplate(String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); 
                              color: white; padding: 20px; border-radius: 8px 8px 0 0; text-align: center; }
                    .content { background: #f8fafc; padding: 20px; border-radius: 0 0 8px 8px; }
                    .warning { background: #fef08a; padding: 15px; border-radius: 6px; margin: 15px 0; 
                               border-left: 4px solid #eab308; }
                    .btn { display: inline-block; background: #3b82f6; color: white; 
                           padding: 10px 20px; text-decoration: none; border-radius: 6px; margin: 20px 0; }
                    .footer { text-align: center; color: #999; font-size: 0.9em; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔑 Password Reset Request</h1>
                    </div>
                    <div class="content">
                        <p>We received a request to reset your password.</p>
                        
                        <div class="warning">
                            <strong>⚠️ Security Note:</strong> If you didn't request this, 
                            please ignore this email or contact support immediately.
                        </div>
                        
                        <p>Click the button below to reset your password:</p>
                        <a href="%s" class="btn">Reset Password →</a>
                        
                        <p><strong>This link will expire in 24 hours.</strong></p>
                        
                        <p>Or copy and paste this link in your browser:<br>
                        <code>%s</code></p>
                        
                        <p>If you have any questions, contact our support team.</p>
                        <p>Best regards,<br><strong>AI Resume Analyzer Team</strong></p>
                    </div>
                    <div class="footer">
                        <p><a href="#help">Help Center</a> | <a href="#support">Contact Support</a></p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(resetLink, resetLink);
    }

    /**
     * Check if email service is enabled
     */
    public boolean isEnabled() {
        return mailEnabled;
    }
}
