package com.resumeanalyzer.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * LLM-powered suggestion service using Google Gemini API.
 * Safe, null-checked, and fallback-friendly.
 */
@Service
public class GeminiSuggestionService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiSuggestionService.class);

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Public entry point for AI suggestions.
     * Results are cached for identical resume/job description combinations.
     */
    @Cacheable(
        value = "resume-suggestions",
        key = "#resumeText.hashCode() + '-' + #jobDescriptionText.hashCode() + '-' + #matchPercentage",
        unless = "#result == null || #result.isEmpty()"
    )
    public List<String> generateAISuggestions(
            String resumeText,
            String jobDescriptionText,
            Set<String> matchedSkills,
            Set<String> missingSkills,
            double matchPercentage) {

        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("Gemini API key not configured. Skipping AI suggestions.");
            return Collections.emptyList();
        }

        try {
            String prompt = buildPrompt(
                    resumeText,
                    jobDescriptionText,
                    matchedSkills,
                    missingSkills,
                    matchPercentage);
            return callGemini(prompt);
        } catch (Exception e) {
            logger.error("Gemini API failed, falling back to rule-based suggestions", e);
            return Collections.emptyList();
        }
    }

    // -------------------- INTERNAL METHODS --------------------

    private String buildPrompt(
            String resumeText,
            String jobDescriptionText,
            Set<String> matchedSkills,
            Set<String> missingSkills,
            double matchPercentage) {

        return """
                You are a senior resume reviewer and career coach.

                Analyze the resume against the job description and provide ONLY
                up to 5 concise, actionable resume improvement bullet points.

                RESUME:
                %s

                JOB DESCRIPTION:
                %s

                ANALYSIS SUMMARY:
                - Match Percentage: %.1f%%
                - Matched Skills: %s
                - Missing Skills: %s

                RULES:
                - Focus on missing skills and alignment gaps
                - Avoid generic advice
                - Use '-' for bullets
                - No preamble or explanations
                - Max 5 bullets
                """.formatted(
                resumeText,
                jobDescriptionText,
                matchPercentage,
                matchedSkills,
                missingSkills);
    }

    private List<String> callGemini(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = """
                    {
                      "contents": [
                        {
                          "parts": [
                            { "text": "%s" }
                          ]
                        }
                      ],
                      "generationConfig": {
                        "temperature": 0.7,
                        "maxOutputTokens": 2000
                      }
                    }
                    """.formatted(escape(prompt));

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            String response = restTemplate.postForObject(
                    geminiApiUrl + "?key=" + apiKey,
                    entity,
                    String.class);

            return parseResponse(response);

        } catch (RestClientException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> parseResponse(String response) throws Exception {
        if (response == null || response.isBlank()) {
            return Collections.emptyList();
        }

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode candidates = root.path("candidates");

            if (!candidates.isArray() || candidates.isEmpty()) {
                logger.warn("Gemini returned no candidates. Response: {}", response);
                return Collections.emptyList();
            }

            JsonNode candidate = candidates.get(0);
            JsonNode content = candidate.path("content");
            JsonNode parts = content.path("parts");

            if (!parts.isArray() || parts.isEmpty()) {
                logger.warn("Gemini response has no parts. Response: {}", response);
                return Collections.emptyList();
            }

            JsonNode textNode = parts.get(0).path("text");

            if (textNode.isMissingNode() || textNode.isNull()) {
                return Collections.emptyList();
            }

            List<String> results = new ArrayList<>();
            for (String line : textNode.asText().split("\n")) {
                line = line.trim();
                // Remove markdown formatting like bolding **
                line = line.replace("**", "").trim();
                if (line.startsWith("-")) {
                    line = line.substring(1).trim();
                }
                if (!line.isEmpty()) {
                    results.add(line);
                }
                if (results.size() == 5)
                    break;
            }

            return results;
        } catch (Exception e) {
            logger.error("Error parsing Gemini response: {}", response, e);
            return Collections.emptyList();
        }
    }

    private String escape(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
