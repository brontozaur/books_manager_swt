package com.papao.books.export.html;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.HtmlWriter;
import com.papao.books.ApplicationService;
import com.papao.books.BooleanSetting;
import com.papao.books.export.ExportType;
import com.papao.books.export.VizualizareRapoarte;
import com.papao.books.model.ApplicationReport;
import com.papao.books.model.config.ExportHtmlSetting;
import com.papao.books.model.config.TableSetting;
import com.papao.books.view.auth.EncodeLive;
import com.papao.books.view.custom.CWaitDlgClassic;
import com.papao.books.view.util.SettingsController;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableItem;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public final class ExportHtml {

    private static final Logger logger = Logger.getLogger(ExportHtml.class);

    private ExportHtml() {
    }

    public static void exportHTML(final org.eclipse.swt.widgets.Table swtTable,
                                  final String reportName,
                                  final Class<?> clazz,
                                  final String tableKey) {
        Document document;
        Table iTextTable;
        Paragraph p;
        Cell cell;
        int nrOfItems;
        int nrOfColumns;
        boolean[] selectedCols;
        int[] aligns;
        int[] order;
        int[] widths;
        String fileName;
        File output;
        CWaitDlgClassic wait = null;
        Chunk chuckie;
        HeaderFooter hf;
        Font font = ExportHtmlOptionsView.HTML_FONT;
        String titleName = reportName;
        ExportHtmlSetting exportSetting;
        try {

            if (StringUtils.isNotEmpty(reportName)) {
                fileName = reportName + "_" + System.currentTimeMillis();
            } else {
                fileName = "Raport_HTML_" + System.currentTimeMillis();
            }

            nrOfItems = swtTable.getItemCount();
            nrOfColumns = swtTable.getColumnCount();

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
                widths = new int[nrOfColumns];
                for (int i = 0; i < nrOfColumns; i++) {
                    aligns[i] = swtTable.getColumns()[i].getAlignment();
                    widths[i] = swtTable.getColumns()[i].getWidth();
                }
            } else {
                selectedCols = setting.getVisibility();
                order = setting.getOrder();
                aligns = setting.getAligns();
                widths = setting.getWidths();
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

            exportSetting = SettingsController.getExportHtmlSetting();
            if (exportSetting == null) {
                exportSetting = new ExportHtmlSetting();
                SettingsController.saveExportHtmlSetting(exportSetting);
            }

            if (SettingsController.getBoolean(BooleanSetting.REPORT_SHOW_OPTIONS)) {
                ExportHtmlOptionsView view;
                ExportHtmlSettings settings = new ExportHtmlSettings();

                settings.setiTextFont(ExportHtmlOptionsView.HTML_FONT);
                settings.setNumeFisier(fileName);
                settings.setSwtTable(swtTable);
                settings.setClazz(clazz);
                settings.setSufix(tableKey);
                settings.setTitlu(titleName);
                settings.setNrOfItems(nrOfItems);

                view = new ExportHtmlOptionsView(swtTable.getShell(), settings);
                view.open();

                if (view.getUserAction() != SWT.OK) {
                    return;
                }

                exportSetting = SettingsController.getExportHtmlSetting();
                settings = view.getSettings().cloneObject();

                fileName = settings.getNumeFisier();
                titleName = settings.getTitlu();
                aligns = settings.getAligns();
                widths = settings.getDims();
                selectedCols = settings.getSelection();
                order = settings.getOrder();
                font = settings.getiTextFont();
            } else {
                fileName = exportSetting.getExportDir() + File.separator;
            }

            if (StringUtils.isEmpty(fileName)) {
                return;
            }

            if (fileName.toLowerCase().endsWith(".html")) {
                output = new File(fileName);
            } else {
                output = new File(fileName + ".html");
            }
            logger.info("Exporting HTML content to file : " + output.getCanonicalPath());

            Rectangle pageSize = PageSize.A4;

            /**
             * daca, prin absurd se doreste setarea unei culori pentru background-ul paginii, se vor
             * decomenta liniile urmatoare
             */
            /*
             * pageSize.setBackgroundColor(BaseColor.CYAN); Rectangle pageSize = new
             * Rectangle(PageSize.A4);
             */

            document = new Document(pageSize, 10, 10, 50, 50);
            HtmlWriter.getInstance(document, new FileOutputStream(output));

            ExportHtml.addMetaData(document, reportName);

            document.open();

            if (exportSetting.isShowTitle()) {
                chuckie = new Chunk(titleName, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Font.UNDERLINE | Font.BOLD, Color.BLACK));
                p = new Paragraph(chuckie);
                p.setAlignment(Element.ALIGN_CENTER);
                document.add(p);
                p = new Paragraph(" ");
                p.setAlignment(Element.ALIGN_CENTER);
                document.add(p);
            } else {
                document.add(new Paragraph(new Chunk(" ")));
            }

            Map<Integer, Integer> dims = new TreeMap<Integer, Integer>();
            for (int i = 0; i < nrOfColumns; i++) {
                if (selectedCols[order[i]]) {
                    dims.put(i, widths[order[i]]);
                }
            }
            int[] sizes;
            int w = 0;
            if (exportSetting.isShowNrCrt()) {
                sizes = new int[dims.size() + 1];
                sizes[0] = 70;
                w = 1;
            } else {
                sizes = new int[dims.size()];
            }
            for (Iterator<Integer> it = dims.values().iterator(); it.hasNext(); ) {
                sizes[w] = it.next().intValue();
                w++;
            }
            iTextTable = new Table(sizes.length);
            iTextTable.setPadding(2f);

            /**
             * daca se doreste ca ultima celula sa se intinda pana jos, se decomenteaza linia
             * urmatoare
             */
            // table.setExtendLastRow(true);

            wait = new CWaitDlgClassic(nrOfItems);
            wait.setMessageLabel("Se genereaza fisierul...");
            wait.open();

            if (exportSetting.isShowHeader()) {
                ExportHtml.createTableHeader(swtTable, iTextTable, exportSetting.isShowNrCrt(), selectedCols, order, widths, font);
            }

            hf = new HeaderFooter(new Phrase("Raport generat cu Books Manager, https://github.com/brontozaur"), false);
            hf.setAlignment(Element.ALIGN_CENTER);

            document.setFooter(hf);

            final TableItem[] items = swtTable.getItems();
            for (int i = 0; i < items.length; i++) {
                wait.advance(i);

                if (exportSetting.isShowNrCrt()) {
                    p = new Paragraph(String.valueOf(i + 1), font);
                    p.setAlignment(Element.ALIGN_JUSTIFIED);
                    cell = new Cell(p);
                    cell.setWidth(70f);
                    if (exportSetting.isShowGrayEffect()) {
                        if (Math.abs(i) % 2 == 1) {
                            cell.setGrayFill(0.9f);
                        }
                    }
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    iTextTable.addCell(cell);
                }

                for (int j = 0; j < nrOfColumns; j++) {
                    if (!selectedCols[order[j]]) {
                        continue;
                    }
                    p = new Paragraph(items[i].getText(order[j]), font);

                    cell = new Cell(p);
                    cell.setWidth(Float.valueOf(widths[order[j]]).floatValue());
                    if (aligns[order[j]] == SWT.CENTER) {
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        p.setAlignment(Element.ALIGN_CENTER);
                    } else if (aligns[order[j]] == SWT.RIGHT) {
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        p.setAlignment(Element.ALIGN_RIGHT);
                    } else {
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        p.setAlignment(Element.ALIGN_LEFT);
                    }
                    if (exportSetting.isShowHeader()) {
                        if (Math.abs(i) % 2 == 1) {
                            cell.setGrayFill(0.9f);
                        }
                    }

                    iTextTable.addCell(cell);
                }
                if ((i > 0) && (Math.abs(i) % 5000 == 0)) {
                    document.add(iTextTable);
                    iTextTable.deleteAllRows();
                }
            }

            document.add(iTextTable);
            iTextTable.deleteAllRows();

            wait.setMessageLabel("Salvare fisier pe disc...va rugam asteptati...");

            document.close();

            wait.close();

            logger.info("HTML file saved succesfully " + " with a total data row count of " + nrOfItems);

            ApplicationReport dbRap = new ApplicationReport();
            dbRap.setCale(output.getCanonicalPath());
            dbRap.setIdUser(EncodeLive.getIdUser());
            dbRap.setNume(titleName);
            dbRap.setType(ExportType.HTML);

            ApplicationService.getApplicationReportController().save(dbRap);
            VizualizareRapoarte.showRaport(dbRap);

        } catch (OutOfMemoryError exc) {
            if (wait != null) {
                wait.close();
            }
            throw exc;
        } catch (Exception exc) {
            if (wait != null) {
                wait.close();
            }
            SWTeXtension.displayMessageE("A intervenit o eroare la generarea fisierului.", exc);
        }
    }

    private static void addMetaData(final Document document, final String numeFisier) {
        document.addAuthor(EncodeLive.getCurrentUserName());
        document.addCreationDate();
        document.addCreator("Books Manager \u00A9 brontozaur");
        document.addProducer();
        document.addSubject("Application time: " + EncodeLive.getUtilDateLogin().toString());
        document.addTitle(numeFisier);
        document.addKeywords(numeFisier + ",raport" + "," + EncodeLive.getCurrentUserName());
    }

    private static void createTableHeader(final org.eclipse.swt.widgets.Table swtTable,
                                          final Table table,
                                          final boolean showNrCrt,
                                          final boolean[] selectedCols,
                                          final int[] order,
                                          final int[] widths,
                                          final Font font) throws BadElementException {
        Paragraph p;
        Cell cell;

        if (showNrCrt) {
            p = new Paragraph("Nr. crt", font);
            p.setAlignment(Element.ALIGN_JUSTIFIED);

            cell = new Cell(p);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(Color.YELLOW);
            cell.setWidth(70f);
            table.addCell(cell);
        }

        for (int j = 0; j < swtTable.getColumnCount(); j++) {
            if (!selectedCols[order[j]]) {
                continue;
            }
            p = new Paragraph(swtTable.getColumn(order[j]).getText(), font);
            p.setAlignment(Element.ALIGN_JUSTIFIED);

            cell = new Cell(p);
            cell.setWidth(Float.valueOf(widths[order[j]]).floatValue());
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(Color.YELLOW);
            table.addCell(cell);
        }
    }

}
