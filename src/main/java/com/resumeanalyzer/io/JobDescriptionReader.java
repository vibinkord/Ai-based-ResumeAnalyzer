package com.resumeanalyzer.io;

import com.resumeanalyzer.util.TextFileUtils;
import java.io.IOException;

/**
 * Reads job description text from a file path.
 * Stage 1 supports TXT only.
 */
public class JobDescriptionReader {
    private final String path;

    public JobDescriptionReader(String path) {
        this.path = path;
    }

    /**
     * Reads the job description as text.
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
