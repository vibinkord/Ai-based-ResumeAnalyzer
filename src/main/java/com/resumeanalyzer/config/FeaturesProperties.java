package com.resumeanalyzer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for feature flags.
 * Maps properties from application.properties to Java objects.
 */
@Configuration
@ConfigurationProperties(prefix = "features")
public class FeaturesProperties {

    private HealthCheck healthCheck = new HealthCheck();
    private SkillsList skillsList = new SkillsList();
    private BatchAnalysis batchAnalysis = new BatchAnalysis();
    private ResumeComparison resumeComparison = new ResumeComparison();
    private FileUpload fileUpload = new FileUpload();
    private AiSuggestions aiSuggestions = new AiSuggestions();

    public HealthCheck getHealthCheck() {
        return healthCheck;
    }

    public void setHealthCheck(HealthCheck healthCheck) {
        this.healthCheck = healthCheck;
    }

    public SkillsList getSkillsList() {
        return skillsList;
    }

    public void setSkillsList(SkillsList skillsList) {
        this.skillsList = skillsList;
    }

    public BatchAnalysis getBatchAnalysis() {
        return batchAnalysis;
    }

    public void setBatchAnalysis(BatchAnalysis batchAnalysis) {
        this.batchAnalysis = batchAnalysis;
    }

    public ResumeComparison getResumeComparison() {
        return resumeComparison;
    }

    public void setResumeComparison(ResumeComparison resumeComparison) {
        this.resumeComparison = resumeComparison;
    }

    public FileUpload getFileUpload() {
        return fileUpload;
    }

    public void setFileUpload(FileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }

    public AiSuggestions getAiSuggestions() {
        return aiSuggestions;
    }

    public void setAiSuggestions(AiSuggestions aiSuggestions) {
        this.aiSuggestions = aiSuggestions;
    }

    // Feature flag classes
    public static class HealthCheck {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class SkillsList {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class BatchAnalysis {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class ResumeComparison {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class FileUpload {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class AiSuggestions {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
