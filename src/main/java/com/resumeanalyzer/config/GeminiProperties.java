package com.resumeanalyzer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Gemini API integration.
 * Maps properties from application.properties to Java objects.
 */
@Configuration
@ConfigurationProperties(prefix = "gemini")
public class GeminiProperties {

    private Api api = new Api();
    private Model model = new Model();

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * Gemini API endpoint and credentials.
     */
    public static class Api {
        private String key;
        private String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent";

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    /**
     * Gemini model configuration.
     */
    public static class Model {
        private String name = "gemini-flash-latest";
        private double temperature = 0.7;
        private int maxOutputTokens = 500;
        private double topP = 0.9;
        private int topK = 40;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getTemperature() {
            return temperature;
        }

        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }

        public int getMaxOutputTokens() {
            return maxOutputTokens;
        }

        public void setMaxOutputTokens(int maxOutputTokens) {
            this.maxOutputTokens = maxOutputTokens;
        }

        public double getTopP() {
            return topP;
        }

        public void setTopP(double topP) {
            this.topP = topP;
        }

        public int getTopK() {
            return topK;
        }

        public void setTopK(int topK) {
            this.topK = topK;
        }
    }
}
