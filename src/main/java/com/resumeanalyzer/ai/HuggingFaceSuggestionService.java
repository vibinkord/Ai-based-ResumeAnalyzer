package com.resumeanalyzer.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides AI-powered resume suggestions using Hugging Face Inference API.
 * Falls back gracefully when token is missing or calls fail.
 */
@Service
public class HuggingFaceSuggestionService {

    private static final Logger logger = LoggerFactory.getLogger(HuggingFaceSuggestionService.class);
    private static final String HF_API_URL =
            "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.2";

    @Value("${huggingface.api.token:}")
    private String apiToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> generateAISuggestions(
            String resumeText,
            String jobDescriptionText,
            Set<String> matchedSkills,
            Set<String> missingSkills,
            double matchPercentage) {

        if (apiToken == null || apiToken.isBlank()) {
            logger.warn("Hugging Face API token not configured. Skipping Hugging Face suggestions.");
            return Collections.emptyList();
        }

        try {
            String prompt = buildPrompt(
                    resumeText,
                    jobDescriptionText,
                    matchedSkills,
                    missingSkills,
                    matchPercentage
            );
            return callHuggingFace(prompt);
        } catch (Exception e) {
            logger.error("Hugging Face API failed, will try fallback AI", e);
            return Collections.emptyList();
        }
    }

    private String buildPrompt(
            String resumeText,
            String jobDescriptionText,
            Set<String> matchedSkills,
            Set<String> missingSkills,
            double matchPercentage) {

        return """
        You are a senior resume reviewer.
        Analyze the resume against the job description and provide ONLY up to 5 concise, actionable bullet-point suggestions.
        Use '-' at the start of each bullet. Avoid any explanations, headers, or markdown.

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
        - Keep bullets short and specific
        - Maximum 5 bullets
        - Return only the bullet points
        """.formatted(
                resumeText,
                jobDescriptionText,
                matchPercentage,
                matchedSkills,
                missingSkills
        );
    }

    private List<String> callHuggingFace(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiToken);

            JsonNode requestBody = objectMapper.createObjectNode()
                    .put("inputs", prompt);

            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

            String response = restTemplate.postForObject(HF_API_URL, entity, String.class);
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

        JsonNode root = objectMapper.readTree(response);
        List<String> generated = new ArrayList<>();

        if (root.isArray()) {
            for (JsonNode node : root) {
                String text = extractGeneratedText(node);
                if (text != null && !text.isBlank()) {
                    generated.add(text);
                }
            }
        } else {
            String text = extractGeneratedText(root);
            if (text != null && !text.isBlank()) {
                generated.add(text);
            }
        }

        if (generated.isEmpty()) {
            return Collections.emptyList();
        }

        // Use first generated block for consistency
        return normalizeLines(generated.get(0));
    }

    private String extractGeneratedText(JsonNode node) {
        JsonNode textNode = node.path("generated_text");
        if (textNode.isMissingNode() || textNode.isNull()) {
            return null;
        }
        return textNode.asText();
    }

    private List<String> normalizeLines(String text) {
        List<String> suggestions = new ArrayList<>();
        for (String line : text.split("\n")) {
            String cleaned = line.trim();
            if (cleaned.startsWith("-")) {
                cleaned = cleaned.substring(1).trim();
            }
            if (!cleaned.isEmpty()) {
                suggestions.add(cleaned);
            }
            if (suggestions.size() == 5) {
                break;
            }
        }
        return suggestions;
    }
}
