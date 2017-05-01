package com.papao.books.export.xls;

import com.papao.books.FiltruAplicatie;
import com.papao.books.controller.ApplicationReportController;
import com.papao.books.export.ExportType;
import com.papao.books.model.ApplicationReport;
import com.papao.books.model.config.ExportXlsSetting;
import com.papao.books.model.config.TableSetting;
import com.papao.books.view.auth.EncodeLive;
import com.papao.books.view.custom.CWaitDlgClassic;
import com.papao.books.view.util.Constants;
import com.papao.books.view.util.SettingsController;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class ExportXls {

    private final static Logger logger = Logger.getLogger(ExportXls.class);

    public final static String XLS_EXTENSION = ".xls";
    public final static String XLSX_EXTENSION = ".xlsx";
    public final static int MAX_ROWS = 65536;
    public final static int MAX_ELEMENTS_FOR_XLSX = 30000;
    public final static int MAX_ELEMENTS_FOR_AUTO_RESIZE = 10000;
    public final static int MAX_ELEMENTS_FOR_WARNING_650MB = 50000;

    public final static String TEMPLATE_EXCEL = "com/papao/books/export/xls/ExcelTemplate.txt";
    public final static String TEMPLATE_EXCEL2007 = "com/papao/books/export/xls/ExcelTemplate2007.txt";

    private ExportXls() {

    }

    public static void exportExcel(final Table swtTable,
                                   final String reportName,
                                   final Class<?> clazz,
                                   final String tableKey,
                                   ApplicationReportController controller) {
        Workbook workBook;
        Sheet sheet = null;
        int nrOfItems;
        int nrOfColumns;
        boolean[] selectedCols;
        int[] order;
        int[] aligns;
        File output;
        BufferedOutputStream fileOut = null;
        Row row;
        Cell cell;
        CWaitDlgClassic wait = null;
        int sheetColumnIndex = 0;
        String fileExtension = "";
        String numeSheet = "";
        String titleName = "";
        boolean afisareHeader = true;
        String fileName;
        boolean isXLSX = false;
        ExportXlsSetting exportSetting;
        try {

            fileExtension = isXLSX ? ExportXls.XLSX_EXTENSION : ExportXls.XLS_EXTENSION;
            numeSheet = reportName;
            if (StringUtils.isNotEmpty(reportName)) {
                fileName = reportName + "_" + System.currentTimeMillis();
            } else {
                fileName = "Raport_Excel_" + System.currentTimeMillis();
            }
            titleName = reportName;

            nrOfColumns = swtTable.getColumnCount();
            nrOfItems = swtTable.getItemCount();

            if ((nrOfItems == 0) || (nrOfColumns <= 0)) {
                SWTeXtension.displayMessageW("Nu exista date pentru a fi exportate!");
                return;
            }

            TableSetting setting = SettingsController.getTableSetting(nrOfColumns, clazz, tableKey);
            if (setting == null) {
                selectedCols = new boolean[nrOfColumns];
                Arrays.fill(selectedCols, true);
                order = swtTable.getColumnOrder();

                aligns = new int[nrOfColumns];
                for (int i = 0; i < nrOfColumns; i++) {
                    aligns[i] = swtTable.getColumns()[i].getAlignment();
                }
            } else {
                selectedCols = setting.getVisibility();
                order = setting.getOrder();
                aligns = setting.getAligns();
            }

            boolean atLeastOneCol = false;
            for (int i = 0; i < nrOfColumns; i++) {
                if (selectedCols[i]) {
                    atLeastOneCol = true;
                    break;
                }
            }

            if (!atLeastOneCol) {
                selectedCols[0] = true;
            }

            exportSetting = SettingsController.getExportXlsSetting();
            if (exportSetting == null) {
                exportSetting = new ExportXlsSetting();
                SettingsController.saveExportXlsSetting(exportSetting);
            }

            if (FiltruAplicatie.isReportsShowingOptions()) {
                ExportXlsSettings settings = new ExportXlsSettings();
                settings.setNumeFisier(fileName);
                settings.setSwtTable(swtTable);
                settings.setClazz(clazz);
                settings.setSufix(tableKey);
                settings.setTitlu(titleName);
                settings.setSheetName(numeSheet);
                settings.setNrOfItems(nrOfItems);

                ExportXlsOptionsView view = new ExportXlsOptionsView(swtTable.getShell(), settings);
                view.open();

                if (view.getUserAction() != SWT.OK) {
                    return;
                }

                settings = view.getSettings().cloneObject();
                fileExtension = exportSetting.getExtension();
                fileName = settings.getNumeFisier();
                numeSheet = settings.getSheetName();
                afisareHeader = exportSetting.isShowHeader();
                titleName = settings.getTitlu();
                aligns = settings.getAligns();
                selectedCols = settings.getSelection();
                order = settings.getOrder();

                exportSetting = SettingsController.getExportXlsSetting();
            } else {
                fileName = exportSetting.getExportDir() + File.separator;
            }

            final int NR_OF_SHEETS = nrOfItems / ExportXls.MAX_ROWS + 1;

            if (!fileName.endsWith(ExportXls.XLS_EXTENSION) && !fileName.endsWith(ExportXls.XLSX_EXTENSION)) {
                fileName += exportSetting.getExtension();
            }

            output = new File(fileName);
            logger.info("Exporting Excel content to file : " + fileName);

            if (fileExtension.intern() == ExportXls.XLSX_EXTENSION.intern()) {
                // workBook = new XSSFWorkbook();
                try {
                    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(ExportXls.TEMPLATE_EXCEL2007);
                    workBook = new XSSFWorkbook(is);
                    if (workBook.getNumberOfSheets() > 0) {
                        while (workBook.getNumberOfSheets() > 0) {
                            workBook.removeSheetAt(0);
                        }
                    }
                    if (StringUtils.isEmpty(numeSheet)) {
                        sheet = workBook.createSheet("Sheet1");
                    } else {
                        sheet = workBook.createSheet(NR_OF_SHEETS > 1 ? numeSheet + (1) : numeSheet);
                    }
                    sheet.setAutobreaks(true);
                } catch (Exception exc) {
                    logger.error(exc, exc);
                    workBook = new XSSFWorkbook();
                    sheet = null;
                }
            } else {
                try {
                    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(ExportXls.TEMPLATE_EXCEL);
                    POIFSFileSystem poiFS = new POIFSFileSystem(is);
                    workBook = new HSSFWorkbook(poiFS);
                    if (workBook.getNumberOfSheets() > 0) {
                        while (workBook.getNumberOfSheets() > 0) {
                            workBook.removeSheetAt(0);
                        }
                    }
                    if (StringUtils.isEmpty(numeSheet)) {
                        sheet = workBook.createSheet("Sheet1");
                    } else {
                        sheet = workBook.createSheet(NR_OF_SHEETS > 1 ? numeSheet + (1) : numeSheet);
                    }
                    sheet.setAutobreaks(true);
                } catch (Exception exc) {
                    logger.error(exc, exc);
                    workBook = new HSSFWorkbook();
                    sheet = null;
                }
            }

            CreationHelper createHelper = workBook.getCreationHelper();

            fileOut = new BufferedOutputStream(new FileOutputStream(output));

            wait = new CWaitDlgClassic(1);
            wait.open();

            int crtInputIndex = 0;

            for (int w = 0; w < NR_OF_SHEETS; ) {
                wait.setMessageLabel("Generare fisier..." + (NR_OF_SHEETS > 1 ? (" generare sheet " + (w + 1)) : ""));
                if ((w > 0) || (sheet == null)) {
                    if (StringUtils.isEmpty(numeSheet)) {
                        sheet = workBook.createSheet(NR_OF_SHEETS > 1 ? "Sheet" + (w + 1) : "");
                    } else {
                        sheet = workBook.createSheet(NR_OF_SHEETS > 1 ? numeSheet + (w + 1) : numeSheet);
                    }
                    sheet.setAutobreaks(true);
                }

                Map<Integer, CellStyle> mapStyles = ExportXls.convertTableColumStyles(workBook, swtTable, selectedCols, order, aligns);

                if (afisareHeader) {

                    CellStyle headerStyle;
                    Font titleFont = workBook.createFont();

                    for (int j = 0; j < nrOfColumns; j++) {
                        if (!selectedCols[order[j]]) {
                            continue;
                        }
                        sheetColumnIndex++;
                    }

                    row = sheet.createRow(0);
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, sheetColumnIndex - 1));
                    sheetColumnIndex = 0;

                    titleFont.setFontHeightInPoints(Short.valueOf("20"));
                    titleFont.setBoldweight(Short.valueOf("5"));
                    titleFont.setColor(IndexedColors.DARK_TEAL.getIndex());
                    headerStyle = workBook.createCellStyle();
                    headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
                    headerStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                    headerStyle.setFont(titleFont);
                    headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

                    cell = row.createCell(0);
                    Drawing drawing = sheet.createDrawingPatriarch();
                    ClientAnchor anchor = createHelper.createClientAnchor();
                    Comment comment = drawing.createCellComment(anchor);
                    RichTextString str = createHelper.createRichTextString("Generat de " + Constants.ENCODE_SHELL_TEXT);

                    comment.setString(str);
                    comment.setAuthor(Constants.ENCODE_SHELL_TEXT);
                    // assign the comment to the cell
                    cell.setCellComment(comment);

                    cell.setCellStyle(headerStyle);
                    if (StringUtils.isNotEmpty(titleName)) {
                        cell.setCellValue(titleName);
                    } else {
                        cell.setCellValue(Constants.ENCODE_SHELL_TEXT);
                    }

                    titleFont = workBook.createFont();
                    titleFont.setFontHeightInPoints(Short.valueOf("14"));
                    titleFont.setBoldweight(Short.valueOf("5"));
                    titleFont.setColor(IndexedColors.DARK_BLUE.getIndex());
                    headerStyle = workBook.createCellStyle();
                    headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
                    headerStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                    headerStyle.setFont(titleFont);
                    headerStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                    headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
                    headerStyle.setBorderBottom(CellStyle.BORDER_DOUBLE);
                    headerStyle.setBorderTop(CellStyle.BORDER_DOUBLE);

                    row = sheet.createRow(1);

                    for (int j = 0; j < nrOfColumns; j++) {
                        if (!selectedCols[order[j]]) {
                            continue;
                        }
                        cell = row.createCell(sheetColumnIndex);
                        cell.setCellStyle(headerStyle);
                        cell.setCellValue(swtTable.getColumn(order[j]).getText());
                        sheetColumnIndex++;
                    }
                    sheetColumnIndex = 0;

                    // adding now an empty row, to separate table header from the table content
                    row = sheet.createRow(2);
                    for (int j = 0; j < nrOfColumns; j++) {
                        if (!selectedCols[order[j]]) {
                            continue;
                        }
                        cell = row.createCell(sheetColumnIndex++);
                        cell.setCellValue("");
                    }

                    sheetColumnIndex = 0;

                }

                int MAX_ROWS_CURRENT_SHEET = 0;

                if (nrOfItems + (afisareHeader ? 3 : 0) - w * ExportXls.MAX_ROWS < ExportXls.MAX_ROWS) {
                    MAX_ROWS_CURRENT_SHEET = nrOfItems - w * ExportXls.MAX_ROWS;
                } else { // valoarea intermediara
                    MAX_ROWS_CURRENT_SHEET = ExportXls.MAX_ROWS;
                }

                if (w == NR_OF_SHEETS - 1) {
                    int tmp = afisareHeader ? 3 : 0;
                    tmp = tmp * w;
                    logger.info("Sheet " + (w + 1) + " data row count: " + (MAX_ROWS_CURRENT_SHEET + tmp));
                } else {
                    logger.info("Sheet " + (w + 1) + " data row count: " + (MAX_ROWS_CURRENT_SHEET - (afisareHeader ? 3 : 0)));
                }

                wait.setMax(MAX_ROWS_CURRENT_SHEET);

                for (int i = 0; (i < ExportXls.MAX_ROWS - (afisareHeader ? 3 : 0)) && (crtInputIndex < nrOfItems); i++) {
                    wait.advance(i);
                    row = sheet.createRow(afisareHeader ? (i + 3) : i);
                    sheetColumnIndex = 0;
                    final TableItem it = swtTable.getItem(crtInputIndex++);
                    for (int j = 0; j < nrOfColumns; j++) {
                        if (!selectedCols[order[j]]) {
                            continue;
                        }
                        cell = row.createCell(sheetColumnIndex);
                        cell.setCellStyle(mapStyles.get(order[j]));
                        cell.setCellValue(it.getText(order[j]));
                        sheetColumnIndex++;
                    }
                }

                // chestia asta dureaza 1.12 minute pentru 64k linii.Ca sa pastram decentza, pt mai
                // mult de 10k linii, NU se va apela.
                if ((sheet.getLastRowNum() < ExportXls.MAX_ELEMENTS_FOR_AUTO_RESIZE) && (NR_OF_SHEETS == 1)) {
                    wait.setMax(sheetColumnIndex);
                    wait.setMessageLabel("Ajustare automata dimensiuni coloane...");
                    wait.setMax(sheetColumnIndex);
                    for (int i = 0; i < sheetColumnIndex; i++) {
                        wait.advance(i);
                        sheet.autoSizeColumn(i);
                    }
                }

                sheetColumnIndex = 0;
                w++;
            }

            wait.setMessageLabel("Salvare fisier pe disc...va rugam asteptati...");
            workBook.write(fileOut);
            fileOut.close();

            wait.close();

            logger.info("Excel file saved succesfully. Content : " + NR_OF_SHEETS + " sheet(s), with a total data row count of " + nrOfItems);

            ApplicationReport dbRap = new ApplicationReport();
            dbRap.setCale(output.getCanonicalPath());
            dbRap.setIdUser(EncodeLive.getIdUser());
            dbRap.setNume(titleName);
            dbRap.setType(ExportType.XLS);
            controller.save(dbRap);

            SWTeXtension.displayMessageI("Fisierul \n" + output.getCanonicalPath() + " a fost generat cu succes!",
                    "Export efectuat cu succes",
                    output.getCanonicalPath());

        } catch (OutOfMemoryError exc) {
            if (wait != null) {
                wait.close();
            }
            throw exc;
        } catch (Exception exc) {
            if (wait != null) {
                wait.close();
            }
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException ioex) {
                    logger.fatal(ioex, ioex);
                    SWTeXtension.displayMessageEGeneric(ioex);
                }
            }
            SWTeXtension.displayMessageE("A intervenit o eroare la generarea fisierului.", exc);
        }
    }

    public static Map<Integer, CellStyle> convertTableColumStyles(final Workbook wb,
                                                                  final Table table,
                                                                  final boolean[] selectedCols,
                                                                  final int[] order,
                                                                  final int[] aligns) {
        Map<Integer, CellStyle> map = new HashMap<Integer, CellStyle>();
        if ((table == null) || table.isDisposed()) {
            return map;
        }
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (!selectedCols[order[i]]) {
                continue;
            }
            CellStyle cellStyle = wb.createCellStyle();
            final int colStyle = aligns[order[i]];
            if (colStyle == SWT.RIGHT) {
                cellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
                cellStyle.setVerticalAlignment(CellStyle.ALIGN_GENERAL);
            } else if (colStyle == SWT.CENTER) {
                cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
                cellStyle.setVerticalAlignment(CellStyle.ALIGN_GENERAL);
            } else { // vom presupune SWT.LEFT, care e default al metodei getAlignment() din TableColumn
                cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
                cellStyle.setVerticalAlignment(CellStyle.ALIGN_GENERAL);
            }
            map.put(order[i], cellStyle);
        }
        return map;
    }

}
