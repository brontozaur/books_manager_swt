package com.papao.books.ui.util.importers;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public final class ReadTabDelimitedFile {

    private static final Logger logger = Logger.getLogger(ReadTabDelimitedFile.class);

    private ReadTabDelimitedFile() {
    }

    public static List<String[]> readTabDelimitedFile(final String fileName, final String delimiter) throws Exception {
        List<String[]> fileContents = new ArrayList<String[]>();
        File source;
        BufferedReader reader = null;
        StringBuilder contents = new StringBuilder();
        try {
            if (StringUtils.isEmpty(fileName)) {
                logger.warn("Nu se poate importa un fisier null!");
                return fileContents;
            }
            source = new File(fileName);
            if (!source.isFile() || !source.canRead()) {
                logger.warn("Fisierul " + fileName + " nu a putut fi importat!");
                return fileContents;
            }
            logger.info("Loading file..." + fileName + " into memory");
            reader = new BufferedReader(new FileReader(source));
            String currentLine = null;
            while ((currentLine = reader.readLine()) != null) {
                contents.append(currentLine).append(System.getProperty("line.separator"));
            }
            StringTokenizer tokenizer = new StringTokenizer(contents.toString(), System.getProperty("line.separator"));
            while (tokenizer.hasMoreTokens()) {
                String[] tokens = tokenizer.nextToken().split(delimiter);
                fileContents.add(tokens);
            }
        } catch (Exception exc) {
            logger.warn(exc);
            fileContents.clear();
            throw exc;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return fileContents;
    }
}
