package com.resumeanalyzer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI Configuration for the Resume Analyzer API.
 * 
 * This configuration class sets up the OpenAPI 3.0 documentation for all REST API endpoints.
 * The API documentation is automatically generated and available at:
 * - Swagger UI: http://localhost:8084/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8084/v3/api-docs
 * - OpenAPI YAML: http://localhost:8084/v3/api-docs.yaml
 * 
 * The configuration provides:
 * - General API information (title, version, description)
 * - Contact and license information
 * - Server URLs for different environments
 * - API tags for organizing endpoints by category
 * 
 * @author AI Resume Analyzer Team
 * @version 1.0.0
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configures the OpenAPI 3.0 specification for the Resume Analyzer API.
     * 
     * This bean provides metadata about the API including:
     * - API title and version
     * - Detailed description of the API's purpose
     * - Contact information for support
     * - License information
     * - Server URLs for different deployment environments
     * 
     * @return OpenAPI configuration object with all API metadata
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Resume Analyzer API")
                        .version("1.0.0")
                        .description("A comprehensive REST API for analyzing resumes, extracting skills, " +
                                "matching job descriptions, and providing AI-powered suggestions using " +
                                "Google Gemini API. The API supports multiple file formats (PDF, DOCX, TXT) " +
                                "and provides features like batch processing, resume comparison, and skill " +
                                "inventory management.")
                        .contact(new Contact()
                                .name("AI Resume Analyzer Team")
                                .url("https://github.com/vibinkord/Ai-based-ResumeAnalyzer")
                                .email("support@resumeanalyzer.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addServersItem(new Server()
                        .url("http://localhost:8084")
                        .description("Development Server"))
                .addServersItem(new Server()
                        .url("https://api.resumeanalyzer.com")
                        .description("Production Server"));
    }
}
