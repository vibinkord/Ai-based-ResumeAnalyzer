package com.resumeanalyzer.web.controller;

import com.resumeanalyzer.email.EmailService;
import com.resumeanalyzer.email.JobAlertService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/email")
@Tag(name = "Email", description = "Email notification endpoints")
public class EmailController {
    private static final Logger log = LoggerFactory.getLogger(EmailController.class);
    @Autowired(required = false) private EmailService emailService;
    @Autowired(required = false) private JobAlertService jobAlertService;

    @PostMapping("/send-analysis-result")
    @Operation(summary = "Send analysis result email")
    public ResponseEntity<Map<String, String>> sendAnalysisEmail(
            @RequestParam String toEmail,
            @RequestParam String resumeName,
            @RequestParam double matchPercentage) {
        if (emailService == null) {
            return ResponseEntity.ok(Map.of("status", "Email service not configured"));
        }
        emailService.sendAnalysisResultEmail(toEmail, resumeName, matchPercentage);
        return ResponseEntity.ok(Map.of("status", "Email sent"));
    }

    @PostMapping("/send-job-alert")
    @Operation(summary = "Send job alert email")
    public ResponseEntity<Map<String, String>> sendJobAlert(
            @RequestParam String toEmail,
            @RequestParam String jobTitle,
            @RequestParam String matchPercentage) {
        if (jobAlertService == null) {
            return ResponseEntity.ok(Map.of("status", "Job alert service not configured"));
        }
        jobAlertService.sendJobAlert(toEmail, jobTitle, matchPercentage);
        return ResponseEntity.ok(Map.of("status", "Job alert sent"));
    }
}
