package com.resumeanalyzer.web.file;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Service for extracting text from uploaded files.
 * Supports PDF and plain text formats.
 */
@Service
public class FileTextExtractorService {

    /**
     * Extracts text from the uploaded file.
     * Supports PDF and TXT formats.
     *
     * @param file the uploaded file
     * @return extracted text content
     * @throws IOException if file reading fails
     * @throws IllegalArgumentException if file format is not supported
     */
    public String extractText(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        if (filename == null) {
            throw new IllegalArgumentException("File must have a name");
        }

        // Handle PDF files
        if (contentType != null && contentType.contains("application/pdf")) {
            return extractTextFromPdf(file);
        }

        // Handle text files
        if (contentType != null && contentType.contains("text/plain")) {
            return extractTextFromPlainText(file);
        }

        // Check by file extension if content type is not provided
        if (filename.toLowerCase().endsWith(".pdf")) {
            return extractTextFromPdf(file);
        }

        if (filename.toLowerCase().endsWith(".txt")) {
            return extractTextFromPlainText(file);
        }

        throw new IllegalArgumentException(
                "Unsupported file format. Please upload a PDF or TXT file."
        );
    }

    /**
     * Extracts text from a PDF file using Apache PDFBox.
     */
    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            if (document.isEncrypted()) {
                throw new IllegalArgumentException("Encrypted PDFs are not supported");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException("No text found in PDF document");
            }

            return text;
        }
    }

    /**
     * Extracts text from a plain text file.
     */
    private String extractTextFromPlainText(MultipartFile file) throws IOException {
        String text = new String(file.getBytes(), StandardCharsets.UTF_8);

        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text file is empty");
        }

        return text;
    }
}
