package com.resumeanalyzer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for security features.
 * Maps properties from application.properties to Java objects.
 */
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private InputValidation inputValidation = new InputValidation();
    private int maxRequestSizeBytes = 52428800; // 50MB

    public InputValidation getInputValidation() {
        return inputValidation;
    }

    public void setInputValidation(InputValidation inputValidation) {
        this.inputValidation = inputValidation;
    }

    public int getMaxRequestSizeBytes() {
        return maxRequestSizeBytes;
    }

    public void setMaxRequestSizeBytes(int maxRequestSizeBytes) {
        this.maxRequestSizeBytes = maxRequestSizeBytes;
    }

    /**
     * Input validation security configuration.
     */
    public static class InputValidation {
        private boolean enabled = true;
        private boolean sanitizeInput = true;
        private boolean blockSqlInjection = true;
        private boolean blockScriptInjection = true;
        private boolean blockNullBytes = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isSanitizeInput() {
            return sanitizeInput;
        }

        public void setSanitizeInput(boolean sanitizeInput) {
            this.sanitizeInput = sanitizeInput;
        }

        public boolean isBlockSqlInjection() {
            return blockSqlInjection;
        }

        public void setBlockSqlInjection(boolean blockSqlInjection) {
            this.blockSqlInjection = blockSqlInjection;
        }

        public boolean isBlockScriptInjection() {
            return blockScriptInjection;
        }

        public void setBlockScriptInjection(boolean blockScriptInjection) {
            this.blockScriptInjection = blockScriptInjection;
        }

        public boolean isBlockNullBytes() {
            return blockNullBytes;
        }

        public void setBlockNullBytes(boolean blockNullBytes) {
            this.blockNullBytes = blockNullBytes;
        }
    }
}
