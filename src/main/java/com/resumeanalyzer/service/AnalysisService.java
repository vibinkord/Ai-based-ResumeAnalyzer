package com.resumeanalyzer.service;

import com.resumeanalyzer.model.entity.Analysis;
import com.resumeanalyzer.model.entity.Resume;
import com.resumeanalyzer.repository.AnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AnalysisService {
    private final AnalysisRepository analysisRepository;

    /**
     * Save a new analysis
     *
     * @param resume            the resume being analyzed
     * @param jobDescription    the job description
     * @param matchPercentage   the match percentage
     * @return the saved analysis
     */
    @Transactional
    public Analysis saveAnalysis(Resume resume, String jobDescription, Double matchPercentage) {
        Analysis analysis = new Analysis(resume, jobDescription, matchPercentage);
        Analysis savedAnalysis = analysisRepository.save(analysis);
        log.info("Saved analysis for resume ID: {} with match: {}%", resume.getId(), matchPercentage);
        return savedAnalysis;
    }

    /**
     * Save a complete analysis with all details
     *
     * @param resume           the resume being analyzed
     * @param jobDescription   the job description
     * @param matchPercentage  the match percentage
     * @param matchedSkills    JSON string of matched skills
     * @param missingSkills    JSON string of missing skills
     * @param suggestions      JSON string of suggestions
     * @param aiSuggestions    JSON string of AI suggestions
     * @param report           the analysis report
     * @return the saved analysis
     */
    @Transactional
    public Analysis saveCompleteAnalysis(
        Resume resume,
        String jobDescription,
        Double matchPercentage,
        String matchedSkills,
        String missingSkills,
        String suggestions,
        String aiSuggestions,
        String report) {
        
        Analysis analysis = new Analysis(resume, jobDescription, matchPercentage);
        analysis.setMatchedSkills(matchedSkills);
        analysis.setMissingSkills(missingSkills);
        analysis.setSuggestions(suggestions);
        analysis.setAiSuggestions(aiSuggestions);
        analysis.setReport(report);
        
        Analysis savedAnalysis = analysisRepository.save(analysis);
        log.info("Saved complete analysis for resume ID: {} with match: {}%", resume.getId(), matchPercentage);
        return savedAnalysis;
    }

    /**
     * Get analysis by ID
     *
     * @param analysisId the analysis ID
     * @return Optional containing the analysis if found
     */
    public Optional<Analysis> getAnalysisById(Long analysisId) {
        return analysisRepository.findById(analysisId);
    }

    /**
     * Get all analyses for a resume
     *
     * @param resumeId the resume ID
     * @return list of analyses (ordered by creation date, newest first)
     */
    public List<Analysis> getResumeAnalyses(Long resumeId) {
        return analysisRepository.findByResumeIdOrderByCreatedAtDesc(resumeId);
    }

    /**
     * Get analysis count for a resume
     *
     * @param resumeId the resume ID
     * @return number of analyses
     */
    public long getAnalysisCountForResume(Long resumeId) {
        return analysisRepository.countByResumeId(resumeId);
    }

    /**
     * Get all analyses for a user
     *
     * @param userId the user ID
     * @return list of all analyses for the user
     */
    public List<Analysis> getUserAnalyses(Long userId) {
        return analysisRepository.findByUserId(userId);
    }

    /**
     * Get good matches (match >= 70%)
     *
     * @return list of analyses with good match percentage
     */
    public List<Analysis> getGoodMatches() {
        return analysisRepository.findGoodMatches();
    }

    /**
     * Get analyses within a date range
     *
     * @param startDate start date
     * @param endDate   end date
     * @return list of analyses created between dates
     */
    public List<Analysis> getAnalysesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return analysisRepository.findByDateRange(startDate, endDate);
    }

    /**
     * Update an analysis
     *
     * @param analysis the analysis to update
     * @return the updated analysis
     */
    @Transactional
    public Analysis updateAnalysis(Analysis analysis) {
        Analysis updated = analysisRepository.save(analysis);
        log.info("Updated analysis with ID: {}", analysis.getId());
        return updated;
    }

    /**
     * Delete analysis by ID
     *
     * @param analysisId the analysis ID
     */
    @Transactional
    public void deleteAnalysis(Long analysisId) {
        analysisRepository.deleteById(analysisId);
        log.info("Deleted analysis with ID: {}", analysisId);
    }

    /**
     * Delete all analyses for a resume
     *
     * @param resumeId the resume ID
     */
    @Transactional
    public void deleteAllAnalysesForResume(Long resumeId) {
        List<Analysis> analyses = analysisRepository.findByResumeId(resumeId);
        analysisRepository.deleteAll(analyses);
        log.info("Deleted all analyses for resume ID: {}", resumeId);
    }

    /**
     * Get average match percentage across all analyses
     *
     * @return average match percentage
     */
    public Double getAverageMatchPercentage() {
        return analysisRepository.getAverageMatchPercentage();
    }

    /**
     * Get average match percentage for a user's analyses
     *
     * @param userId the user ID
     * @return average match percentage
     */
    public Double getAverageMatchPercentageForUser(Long userId) {
        return analysisRepository.getAverageMatchPercentageForUser(userId);
    }

    /**
     * Get total number of analyses
     *
     * @return total analysis count
     */
    public long getTotalAnalysisCount() {
        return analysisRepository.count();
    }

    /**
     * Check if analysis exists
     *
     * @param analysisId the analysis ID
     * @return true if analysis exists
     */
    public boolean analysisExists(Long analysisId) {
        return analysisRepository.existsById(analysisId);
    }
}
