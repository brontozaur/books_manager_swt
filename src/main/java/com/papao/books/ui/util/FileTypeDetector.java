package com.papao.books.ui.util;

import org.apache.tika.Tika;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Detects the mime type of files (ideally based on marker in file content)
 */
public class FileTypeDetector extends java.nio.file.spi.FileTypeDetector {

    private final Tika tika = new Tika();

    @Override
    public String probeContentType(Path path) throws IOException {
        return tika.detect(path.toFile());
    }
}
