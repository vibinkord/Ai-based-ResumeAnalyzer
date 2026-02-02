package com.resumeanalyzer.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Minimal utility for reading a UTF-8 text file into a String.
 * Uses modern NIO APIs and is reusable across the project.
 */
public final class TextFileUtils {
    private TextFileUtils() {}

    /**
     * Reads the entire file as a UTF-8 string.
     * @param path file system path to the text file
     * @return content of the file as String
     * @throws IOException if reading fails
     */
    public static String readTextFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
