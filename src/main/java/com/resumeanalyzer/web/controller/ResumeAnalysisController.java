package com.resumeanalyzer.web.controller;

import com.resumeanalyzer.analysis.SkillExtractor;
import com.resumeanalyzer.analysis.SkillMatcher;
import com.resumeanalyzer.report.ResumeReportGenerator;
import com.resumeanalyzer.suggestions.ResumeSuggestionEngine;
import com.resumeanalyzer.web.dto.AnalyzeRequest;
import com.resumeanalyzer.web.dto.AnalyzeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * REST controller for resume analysis endpoints.
 * Exposes existing domain logic via HTTP POST.
 */
@RestController
@RequestMapping("/api")
public class ResumeAnalysisController {

    private final SkillExtractor skillExtractor;
    private final SkillMatcher skillMatcher;
    private final ResumeSuggestionEngine suggestionEngine;
    private final ResumeReportGenerator reportGenerator;

    public ResumeAnalysisController() {
        this.skillExtractor = new SkillExtractor();
        this.skillMatcher = new SkillMatcher();
        this.suggestionEngine = new ResumeSuggestionEngine();
        this.reportGenerator = new ResumeReportGenerator();
    }

    /**
     * POST /api/analyze
     * Analyzes resume against job description and returns match results.
     */
    @PostMapping("/analyze")
    public ResponseEntity<AnalyzeResponse> analyzeResume(@RequestBody AnalyzeRequest request) {
        // Validate input
        if (request.getResumeText() == null || request.getResumeText().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getJobDescriptionText() == null || request.getJobDescriptionText().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Extract skills from both texts (existing Stage 2 logic)
        Set<String> resumeSkills = skillExtractor.extractSkills(request.getResumeText());
        Set<String> jobSkills = skillExtractor.extractSkills(request.getJobDescriptionText());

        // Match skills (existing Stage 3 logic)
        SkillMatcher.Result matchResult = skillMatcher.match(resumeSkills, jobSkills);

        // Generate suggestions (existing Stage 4 logic)
        List<String> suggestions = suggestionEngine.generateSuggestions(matchResult);

        // Generate final report (existing Stage 5 logic)
        String report = reportGenerator.generateReport(matchResult, suggestions);

        // Build response DTO
        AnalyzeResponse response = new AnalyzeResponse(
                matchResult.getMatchPercentage(),
                matchResult.getMatchedSkills(),
                matchResult.getMissingSkills(),
                suggestions,
                report
        );

        return ResponseEntity.ok(response);
    }
}
