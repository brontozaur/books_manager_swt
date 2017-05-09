package com.papao.books.ui.util;

import java.io.File;

public final class FileUtil {

    private FileUtil() {}

    public static String getFileExtension(final File file) {
        return FileUtil.getFileExtension(file.getAbsolutePath());
    }

    public static String getFileExtension(final String fileName) {
        if (fileName.lastIndexOf('.') != -1) {
            return fileName.substring(fileName.lastIndexOf('.') + 1);
        }
        return "";
    }
}
