package com.resumeanalyzer.web.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.resumeanalyzer.ai.GeminiSuggestionService;
import com.resumeanalyzer.analysis.SkillExtractor;
import com.resumeanalyzer.analysis.SkillMatcher;
import com.resumeanalyzer.report.ResumeReportGenerator;
import com.resumeanalyzer.suggestions.ResumeSuggestionEngine;
import com.resumeanalyzer.web.dto.ResumeAnalysisRequest;
import com.resumeanalyzer.web.dto.ResumeAnalysisResponse;
import com.resumeanalyzer.web.file.FileTextExtractorService;
import com.resumeanalyzer.web.service.JobDescriptionFetcher;

/**
 * REST controller for resume analysis API.
 * Exposes the analyzer logic through a clean, stateless HTTP interface.
 * Integrates both rule-based and AI-powered suggestions via Gemini LLM.
 * Base path: /api
 */
@RestController
@RequestMapping("/api")
public class ResumeAnalysisController {

    private final SkillExtractor skillExtractor;
    private final SkillMatcher skillMatcher;
    private final ResumeSuggestionEngine suggestionEngine;
    private final ResumeReportGenerator reportGenerator;
    private final FileTextExtractorService fileTextExtractor;
    private final GeminiSuggestionService geminiSuggestionService;
    private final JobDescriptionFetcher jobDescriptionFetcher;

    @Autowired
    public ResumeAnalysisController(FileTextExtractorService fileTextExtractor,
                                    GeminiSuggestionService geminiSuggestionService,
                                    JobDescriptionFetcher jobDescriptionFetcher) {
        this.skillExtractor = new SkillExtractor();
        this.skillMatcher = new SkillMatcher();
        this.suggestionEngine = new ResumeSuggestionEngine();
        this.reportGenerator = new ResumeReportGenerator();
        this.fileTextExtractor = fileTextExtractor;
        this.geminiSuggestionService = geminiSuggestionService;
        this.jobDescriptionFetcher = jobDescriptionFetcher;
    }

    /**
     * Analyzes resume against job description.
     * POST /api/analyze
     * 
     * Generates both rule-based and AI-enhanced suggestions.
     * If LLM fails, gracefully falls back to rule-based suggestions only.
     *
     * @param request ResumeAnalysisRequest containing resume and job description text
     * @return ResponseEntity with ResumeAnalysisResponse containing analysis results
     */
    @PostMapping("/analyze")
    public ResponseEntity<ResumeAnalysisResponse> analyze(@RequestBody ResumeAnalysisRequest request) {
        String jobDescriptionText;
        try {
            jobDescriptionText = resolveJobDescriptionText(
                request.getJobDescriptionText(),
                request.getJobDescriptionUrl()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (jobDescriptionText == null || jobDescriptionText.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Extract skills from resume and job description
        Set<String> resumeSkills = skillExtractor.extractSkills(request.getResumeText());
        Set<String> jobSkills = skillExtractor.extractSkills(jobDescriptionText);

        // Match resume skills against job skills
        SkillMatcher.Result matchResult = skillMatcher.match(resumeSkills, jobSkills);

        // Generate rule-based improvement suggestions
        List<String> suggestions = suggestionEngine.generateSuggestions(matchResult);

        // Generate AI-enhanced suggestions via Gemini API
        List<String> aiSuggestions = geminiSuggestionService.generateAISuggestions(
            request.getResumeText(),
            jobDescriptionText,
            matchResult.getMatchedSkills(),
            matchResult.getMissingSkills(),
            matchResult.getMatchPercentage()
        );

        // Generate formatted report
        String report = reportGenerator.generateReport(matchResult, suggestions);

        // Populate response DTO with both rule-based and AI suggestions
        ResumeAnalysisResponse response = new ResumeAnalysisResponse(
                matchResult.getMatchPercentage(),
                matchResult.getMatchedSkills(),
                matchResult.getMissingSkills(),
                suggestions,
                aiSuggestions,
                report
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Analyzes uploaded resume file against job description.
     * POST /api/analyze-file
     * Supports PDF and TXT file uploads.
     *
     * @param resumeFile the uploaded resume file (PDF or TXT)
     * @param jobDescriptionText the job description text
     * @return ResponseEntity with ResumeAnalysisResponse containing analysis results
     */
    @PostMapping("/analyze-file")
    public ResponseEntity<ResumeAnalysisResponse> analyzeFile(
            @RequestParam("resumeFile") MultipartFile resumeFile,
            @RequestParam(value = "jobDescriptionText", required = false) String jobDescriptionText,
            @RequestParam(value = "jobDescriptionUrl", required = false) String jobDescriptionUrl) {
        
        if (resumeFile == null || resumeFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // Extract text from uploaded file
            String resumeText = fileTextExtractor.extractText(resumeFile);

            String resolvedJobDescriptionText = resolveJobDescriptionText(jobDescriptionText, jobDescriptionUrl);
            if (resolvedJobDescriptionText == null || resolvedJobDescriptionText.isBlank()) {
                return ResponseEntity.badRequest().build();
            }

            // Create a request object with extracted text
            ResumeAnalysisRequest request = new ResumeAnalysisRequest(resumeText, resolvedJobDescriptionText);

            // Reuse existing analysis logic
            return analyze(request);

        } catch (IllegalArgumentException e) {
            // Return bad request for unsupported formats
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            // Return internal server error for file processing errors
            return ResponseEntity.internalServerError().build();
        }
    }

    private String resolveJobDescriptionText(String jobDescriptionText, String jobDescriptionUrl) throws IOException {
        boolean hasText = jobDescriptionText != null && !jobDescriptionText.trim().isEmpty();
        boolean hasUrl = jobDescriptionUrl != null && !jobDescriptionUrl.trim().isEmpty();

        if (hasText) {
            return jobDescriptionText;
        }
        if (hasUrl) {
            return jobDescriptionFetcher.fetchJobDescription(jobDescriptionUrl);
        }
        throw new IllegalArgumentException("Job description text or URL is required");
    }
}
