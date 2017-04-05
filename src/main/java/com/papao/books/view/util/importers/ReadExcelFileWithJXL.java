package com.papao.books.view.util.importers;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public final class ReadExcelFileWithJXL {

    private final static Logger logger = Logger.getLogger(ReadExcelFileWithJXL.class);

    private ReadExcelFileWithJXL() {}

    public static List<String[]> readExcelFile(final String fileName) throws Exception {
        Workbook wb;
        List<String[]> fileContents = new ArrayList<String[]>();
        FileInputStream input;
        Sheet sheet;
        Cell cell;
        File source;
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
            input = new FileInputStream(source);
            wb = Workbook.getWorkbook(input);
            sheet = wb.getSheet(0);
            input.close();
            fileContents = new ArrayList<String[]>();
            final int rowCount = sheet.getRows();
            final int columnCount = sheet.getColumns();
            for (int i = 0; i < rowCount; i++) {
                String[] str = new String[columnCount];
                for (int j = 0; j < columnCount; j++) {
                    cell = sheet.getCell(j, i);
                    if (cell == null) {
                        str[j] = "";
                        continue;
                    }
                    str[j] = cell.getContents();
                }
                fileContents.add(str);
            }
        } catch (Exception exc) {
            logger.warn(exc);
            fileContents.clear();
            throw exc;
        }
        return fileContents;
    }
}