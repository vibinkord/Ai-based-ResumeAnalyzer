package com.resumeanalyzer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for resume analysis functionality.
 * Maps properties from application.properties to Java objects.
 */
@Configuration
@ConfigurationProperties(prefix = "resume.analyzer")
public class ResumeAnalyzerProperties {

    private int maxResumeSize = 51200;
    private int maxJobDescriptionSize = 51200;
    private int minTextLength = 1;

    private Skills skills = new Skills();
    private Match match = new Match();
    private AiSuggestions aiSuggestions = new AiSuggestions();

    // Getters and Setters
    public int getMaxResumeSize() {
        return maxResumeSize;
    }

    public void setMaxResumeSize(int maxResumeSize) {
        this.maxResumeSize = maxResumeSize;
    }

    public int getMaxJobDescriptionSize() {
        return maxJobDescriptionSize;
    }

    public void setMaxJobDescriptionSize(int maxJobDescriptionSize) {
        this.maxJobDescriptionSize = maxJobDescriptionSize;
    }

    public int getMinTextLength() {
        return minTextLength;
    }

    public void setMinTextLength(int minTextLength) {
        this.minTextLength = minTextLength;
    }

    public Skills getSkills() {
        return skills;
    }

    public void setSkills(Skills skills) {
        this.skills = skills;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public AiSuggestions getAiSuggestions() {
        return aiSuggestions;
    }

    public void setAiSuggestions(AiSuggestions aiSuggestions) {
        this.aiSuggestions = aiSuggestions;
    }

    /**
     * Skill extraction configuration properties.
     */
    public static class Skills {
        private boolean enabled = true;
        private String configFile = "classpath:skills.json";
        private boolean useFallback = true;
        private boolean cacheEnabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getConfigFile() {
            return configFile;
        }

        public void setConfigFile(String configFile) {
            this.configFile = configFile;
        }

        public boolean isUseFallback() {
            return useFallback;
        }

        public void setUseFallback(boolean useFallback) {
            this.useFallback = useFallback;
        }

        public boolean isCacheEnabled() {
            return cacheEnabled;
        }

        public void setCacheEnabled(boolean cacheEnabled) {
            this.cacheEnabled = cacheEnabled;
        }
    }

    /**
     * Match percentage threshold configuration.
     */
    public static class Match {
        private int goodMatchThreshold = 70;
        private int acceptableMatchThreshold = 50;
        private int poorMatchThreshold = 30;

        public int getGoodMatchThreshold() {
            return goodMatchThreshold;
        }

        public void setGoodMatchThreshold(int goodMatchThreshold) {
            this.goodMatchThreshold = goodMatchThreshold;
        }

        public int getAcceptableMatchThreshold() {
            return acceptableMatchThreshold;
        }

        public void setAcceptableMatchThreshold(int acceptableMatchThreshold) {
            this.acceptableMatchThreshold = acceptableMatchThreshold;
        }

        public int getPoorMatchThreshold() {
            return poorMatchThreshold;
        }

        public void setPoorMatchThreshold(int poorMatchThreshold) {
            this.poorMatchThreshold = poorMatchThreshold;
        }
    }

    /**
     * AI suggestions configuration properties.
     */
    public static class AiSuggestions {
        private boolean enabled = true;
        private boolean fallbackToRules = true;
        private int timeoutSeconds = 30;
        private int maxRetries = 2;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isFallbackToRules() {
            return fallbackToRules;
        }

        public void setFallbackToRules(boolean fallbackToRules) {
            this.fallbackToRules = fallbackToRules;
        }

        public int getTimeoutSeconds() {
            return timeoutSeconds;
        }

        public void setTimeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
        }

        public int getMaxRetries() {
            return maxRetries;
        }

        public void setMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
        }
    }
}
