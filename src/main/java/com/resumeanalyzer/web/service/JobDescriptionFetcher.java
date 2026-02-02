package com.resumeanalyzer.web.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

/**
 * Fetches and cleans job description text from supported job posting URLs.
 * Currently supports LinkedIn and Internshala job links.
 */
@Service
public class JobDescriptionFetcher {

    private static final List<String> ALLOWED_HOSTS = Arrays.asList(
            "linkedin.com",
            "www.linkedin.com",
            "internshala.com",
            "www.internshala.com"
    );

    /**
     * Retrieve visible text from a supported job description URL.
     *
     * @param rawUrl LinkedIn or Internshala job URL
     * @return cleaned job description text
     * @throws IOException if the remote content cannot be fetched
     * @throws IllegalArgumentException if the URL is missing/unsupported
     */
    public String fetchJobDescription(String rawUrl) throws IOException {
        if (rawUrl == null || rawUrl.isBlank()) {
            throw new IllegalArgumentException("Job description URL is required");
        }

        String normalizedUrl = normalizeUrl(rawUrl);
        URI uri = toUri(normalizedUrl);

        if (!isAllowedHost(uri.getHost())) {
            throw new IllegalArgumentException("Only LinkedIn or Internshala job URLs are supported");
        }

        Document document = Jsoup.connect(normalizedUrl)
                .userAgent("Mozilla/5.0 (compatible; ResumeAnalyzer/1.0; +https://example.com)")
                .timeout(8000)
                .get();

        String text = extractVisibleText(document);
        if (text.isBlank()) {
            throw new IOException("No readable text found at the provided URL");
        }
        return text.trim();
    }

    private String normalizeUrl(String rawUrl) {
        String trimmed = rawUrl.trim();
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            return "https://" + trimmed;
        }
        return trimmed;
    }

    private URI toUri(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid job URL", e);
        }
    }

    private boolean isAllowedHost(String host) {
        if (host == null) {
            return false;
        }
        return ALLOWED_HOSTS.stream().anyMatch(host::equalsIgnoreCase) ||
                host.toLowerCase().endsWith(".linkedin.com") ||
                host.toLowerCase().endsWith(".internshala.com");
    }

    private String extractVisibleText(Document document) {
        // Prefer main content if available, fallback to body text
        String mainText = document.select("main").text();
        if (mainText != null && !mainText.isBlank()) {
            return mainText;
        }
        return document.body() != null ? document.body().text() : "";
    }
}
