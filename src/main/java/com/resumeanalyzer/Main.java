package com.resumeanalyzer;

import com.resumeanalyzer.analysis.SkillExtractor;
import com.resumeanalyzer.io.JobDescriptionReader;
import com.resumeanalyzer.io.ResumeReader;
import com.resumeanalyzer.report.ResumeReportGenerator;
import com.resumeanalyzer.suggestions.ResumeSuggestionEngine;
import java.io.IOException;
import java.util.List;

/**
 * Entry point for Stage 1: Basic project setup and TXT reading.
 * Later stages will add PDF reading, skill extraction, analysis, and reporting.
 */
public class Main {
    public static void main(String[] args) {
        // Allow passing file paths via args; default to sample data
        String resumePath = args.length > 0 ? args[0] : "data/sample_resume.txt";
        String jdPath = args.length > 1 ? args[1] : "data/sample_job.txt";

        System.out.println("=== AI Resume Analyzer ===");
        System.out.println("Resume path: " + resumePath);
        System.out.println("Job description path: " + jdPath);

        try {
            ResumeReader resumeReader = new ResumeReader(resumePath);
            String resumeText = resumeReader.read();

            JobDescriptionReader jdReader = new JobDescriptionReader(jdPath);
            String jdText = jdReader.read();

            System.out.println("Resume length (chars): " + resumeText.length());
            System.out.println("Job description length (chars): " + jdText.length());

            System.out.println("Resume preview: " + preview(resumeText));
            System.out.println("JD preview: " + preview(jdText));

            // --- TEMPORARY: Stage 2 Skill Extraction Testing ---
            SkillExtractor extractor = new SkillExtractor();
            java.util.Set<String> resumeSkills = extractor.extractSkills(resumeText);
            java.util.Set<String> jobSkills = extractor.extractSkills(jdText);
            System.out.println("\nExtracted skills (Resume): " + resumeSkills);
            System.out.println("Extracted skills (Job Description): " + jobSkills);

            // --- TEMPORARY: Stage 3 Skill Matching & Scoring Testing ---
            com.resumeanalyzer.analysis.SkillMatcher matcher = new com.resumeanalyzer.analysis.SkillMatcher();
            com.resumeanalyzer.analysis.SkillMatcher.Result result = matcher.match(resumeSkills, jobSkills);
            System.out.println("\nMatched Skills: " + result.getMatchedSkills());
            System.out.println("Missing Skills: " + result.getMissingSkills());
            System.out.println("Resume Match Score: " + "%.1f%%".formatted(result.getMatchPercentage()));

            // --- TEMPORARY: Stage 4 Resume Suggestion Testing ---
            ResumeSuggestionEngine suggestionEngine = new ResumeSuggestionEngine();
            List<String> suggestions = suggestionEngine.generateSuggestions(result);
            System.out.println("\nResume Improvement Suggestions:");
            for (String suggestion : suggestions) {
                System.out.println("- " + suggestion);
            }

            // --- TEMPORARY: Stage 5 Final Report Generation ---
            ResumeReportGenerator reportGenerator = new ResumeReportGenerator();
            String finalReport = reportGenerator.generateReport(result, suggestions);
            System.out.println("\n" + finalReport);

            System.out.println("Stage 5 complete: Final report generation works.");
        } catch (IOException e) {
            System.err.println("Error reading files: " + e.getMessage());
            System.exit(1);
        }
    }

    private static String preview(String text) {
        int max = Math.min(180, text.length());
        return text.substring(0, max).replaceAll("\\s+", " ");
    }
}
