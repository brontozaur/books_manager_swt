package com.papao.books.util.importers;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public final class ReadExcelFileWithPOI {

    private final static Logger logger = Logger.getLogger(ReadExcelFileWithPOI.class);

    private ReadExcelFileWithPOI() {

    }

    public static List<String[]> readExcelFile(final String fileName) throws Exception {
        List<String[]> fileContents = new ArrayList<String[]>();
        POIFSFileSystem fs;
        HSSFWorkbook wb;
        HSSFSheet sheet;
        HSSFRow row;
        HSSFCell cell;
        int rows; // No of rows
        int cols = 0; // No of columns
        int tmp = 0;
        FileInputStream fileIOstream = null;
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
            fileIOstream = new FileInputStream(source);
            fs = new POIFSFileSystem(fileIOstream);
            wb = new HSSFWorkbook(fs);
            sheet = wb.getSheetAt(0);

            rows = sheet.getPhysicalNumberOfRows();

            // This trick ensures that we get the data properly even if it doesnt start from
            // first few rows
            for (int i = 0; (i < 10) || (i < rows); i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                    if (tmp > cols) {
                        cols = tmp;
                    }
                }
            }

            for (int r = 0; r < rows; r++) {
                row = sheet.getRow(r);
                if (row != null) {
                    String[] str = new String[cols];
                    for (int c = 0; c < cols; c++) {
                        cell = row.getCell(c);
                        str[c] = "";
                        if (cell != null) {
                            str[c] = cell.toString();
                        }
                    }
                    fileContents.add(str);
                }
            }
        } catch (Exception exc) {
            logger.warn(exc);
            fileContents.clear();
            throw exc;
        } finally {
            if (fileIOstream != null) {
                fileIOstream.close();
            }
        }
        return fileContents;
    }
}