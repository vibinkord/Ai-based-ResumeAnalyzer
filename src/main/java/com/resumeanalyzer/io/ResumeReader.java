package com.resumeanalyzer.io;

import com.resumeanalyzer.util.TextFileUtils;
import java.io.IOException;

/**
 * Reads resume content from a file path.
 * Stage 1 supports TXT only; PDF added in Stage 2.
 */
public class ResumeReader {
    private final String path;

    public ResumeReader(String path) {
        this.path = path;
    }

    /**
     * Reads the resume as text.
     * @return full text content
     * @throws IOException if reading fails
     */
    public String read() throws IOException {
        return TextFileUtils.readTextFile(path);
    }

    public String getPath() {
        return path;
    }
}
