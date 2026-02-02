package com.resumeanalyzer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.email")
public class EmailProperties {
    private String from = "noreply@resumeanalyzer.com";
    private String adminEmail = "admin@resumeanalyzer.com";
    private boolean enabled = true;

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}

