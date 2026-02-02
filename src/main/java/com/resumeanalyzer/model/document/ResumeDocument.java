package com.resumeanalyzer.model.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Elasticsearch Document Model for Resume Search
 * 
 * This document represents a resume indexed in Elasticsearch for:
 * - Full-text search on resume content
 * - Skill matching and extraction
 * - Fast retrieval for analytics
 * - Advanced filtering and aggregations
 * 
 * Indexing Strategy:
 * - Resume content stored with text analyzer for full-text search
 * - Skills stored as keyword field for exact matching
 * - Title and summary with edge-gram tokenizer for autocomplete
 * - Experience and education indexed for filtering
 * 
 * Index Settings:
 * - Shards: 1 (single node), 5 (cluster)
 * - Replicas: 1
 * - Refresh interval: 1s
 * 
 * @author Resume Analyzer Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "resumes", createIndex = true)
public class ResumeDocument {

    /**
     * Unique document ID (maps to Resume entity ID)
     */
    @Id
    private String id;

    /**
     * Resume owner user ID for access control
     */
    @Field(type = FieldType.Keyword)
    private String userId;

    /**
     * Job title/professional title
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    /**
     * Professional summary or objective
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String summary;

    /**
     * Full resume content (text extracted from PDF/DOC)
     * Indexed with standard analyzer for full-text search
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String content;

    /**
     * List of technical skills extracted from resume
     * Stored as keywords for exact matching
     */
    @Field(type = FieldType.Keyword)
    private List<String> skills;

    /**
     * List of certifications
     */
    @Field(type = FieldType.Keyword)
    private List<String> certifications;

    /**
     * Education details
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String education;

    /**
     * Work experience details
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String experience;

    /**
     * Years of total experience
     */
    @Field(type = FieldType.Integer)
    private Integer yearsOfExperience;

    /**
     * Primary domain/specialization
     */
    @Field(type = FieldType.Keyword)
    private String domain;

    /**
     * List of programming languages
     */
    @Field(type = FieldType.Keyword)
    private List<String> programmingLanguages;

    /**
     * List of frameworks/technologies
     */
    @Field(type = FieldType.Keyword)
    private List<String> technologies;

    /**
     * Professional email (for contact)
     */
    @Field(type = FieldType.Keyword)
    private String email;

    /**
     * Phone number
     */
    @Field(type = FieldType.Keyword)
    private String phone;

    /**
     * LinkedIn profile URL
     */
    @Field(type = FieldType.Keyword)
    private String linkedinUrl;

    /**
     * GitHub profile URL
     */
    @Field(type = FieldType.Keyword)
    private String githubUrl;

    /**
     * Current location/city
     */
    @Field(type = FieldType.Keyword)
    private String location;

    /**
     * Resume file name
     */
    @Field(type = FieldType.Keyword)
    private String fileName;

    /**
     * File size in bytes
     */
    @Field(type = FieldType.Long)
    private Long fileSizeBytes;

    /**
     * Language of the resume
     */
    @Field(type = FieldType.Keyword)
    private String language;

    /**
     * Overall quality score (0-100)
     */
    @Field(type = FieldType.Integer)
    private Integer qualityScore;

    /**
     * Completeness percentage (0-100)
     */
    @Field(type = FieldType.Integer)
    private Integer completeness;

    /**
     * Document created/indexed timestamp
     */
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    /**
     * Document last updated timestamp
     */
    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;

    /**
     * Resume upload/creation date
     */
    @Field(type = FieldType.Date)
    private LocalDateTime uploadedAt;

    /**
     * Flag indicating if resume is active
     */
    @Field(type = FieldType.Boolean)
    private Boolean isActive;

    /**
     * Flag indicating if content is indexed
     */
    @Field(type = FieldType.Boolean)
    private Boolean isIndexed;
}
