package com.papao.books.export.rtf;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.document.output.RtfDataCache;
import com.papao.books.FiltruAplicatie;
import com.papao.books.controller.ApplicationReportController;
import com.papao.books.export.ExportType;
import com.papao.books.export.pdf.ExportPdfPrefs;
import com.papao.books.model.ApplicationReport;
import com.papao.books.view.auth.EncodeLive;
import com.papao.books.view.custom.CWaitDlgClassic;
import com.papao.books.view.util.FilterUtil;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableItem;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public final class ExportRtf {

    private static final Logger logger = Logger.getLogger(ExportRtf.class);

    private ExportRtf() {
    }

    public static void exportRTF(final org.eclipse.swt.widgets.Table swtTable,
                                 final String reportName,
                                 final Class<?> clazz,
                                 final String sufix,
                                 ApplicationReportController controller) {
        Document document;
        Table rtfTable;
        Paragraph p;
        Cell cell;
        int nrOfItems;
        int nrOfColumns;
        boolean[] selectedCols;
        int[] aligns;
        int[] widths;
        int[] order;
        String fileName;
        File output;
        CWaitDlgClassic wait = null;
        RtfWriter2 writer;
        Chunk chuckie;
        HeaderFooter hf;
        Font font = ExportRtfOptionsView.RTF_FONT;
        Rectangle pageSize = PageSize.A4;
        String titleName = reportName;
        try {

            if (StringUtils.isNotEmpty(reportName)) {
                fileName = reportName + "_" + System.currentTimeMillis();
            } else {
                fileName = "Raport_RTF_" + System.currentTimeMillis();
            }

            nrOfItems = swtTable.getItemCount();
            nrOfColumns = swtTable.getColumnCount();

            if ((nrOfItems == 0) || (nrOfColumns <= 0)) {
                SWTeXtension.displayMessageW("Nu exista date pentru a fi exportate!");
                return;
            }

            selectedCols = FilterUtil.getSavedVisibleCols(nrOfColumns, clazz, sufix);
            aligns = FilterUtil.getSavedGridAligns(nrOfColumns, clazz, sufix);
            widths = FilterUtil.getSavedGridDims(nrOfColumns, clazz, sufix);
            order = FilterUtil.getSavedGridColumnOrder(nrOfColumns, clazz, sufix);

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

            if (FiltruAplicatie.isReportsShowingOptions()) {
                ExportRtfSettings settings = new ExportRtfSettings();
                settings.setiTextFont(font);
                settings.setNumeFisier(fileName);
                settings.setPageSize(pageSize);
                settings.setTitlu(titleName);
                settings.setNrOfItems(nrOfItems);
                settings.setSwtTable(swtTable);
                settings.setClazz(clazz);
                settings.setSufix(sufix);

                ExportRtfOptionsView view = new ExportRtfOptionsView(swtTable.getShell(), settings);
                view.open();

                if (view.getUserAction() != SWT.OK) {
                    return;
                }

                settings = view.getSettings().cloneObject();

                fileName = settings.getNumeFisier();
                titleName = settings.getTitlu();
                font = settings.getiTextFont();
                aligns = settings.getAligns();
                widths = settings.getDims();
                selectedCols = settings.getSelection();
                order = settings.getOrder();
                font = settings.getiTextFont();
                pageSize = settings.getPageSize();
            } else {
                fileName = ExportPdfPrefs.getExportPath() + File.separator;
            }

            if (StringUtils.isEmpty(fileName)) {
                return;
            }

            if (fileName.toLowerCase().endsWith(".rtf")) {
                output = new File(fileName);
            } else {
                output = new File(fileName + ".rtf");
            }
            logger.info("Exporting RTF content to file : " + fileName);

            /**
             * daca, prin absurd se doreste setarea unei culori pentru background-ul paginii, se vor
             * decomenta liniile urmatoare
             */
            /*
             * pageSize.setBackgroundColor(BaseColor.CYAN); Rectangle pageSize = new
             * Rectangle(PageSize.A4);
             */

            document = new Document(pageSize, 10, 10, 50, 50);
            writer = RtfWriter2.getInstance(document, new FileOutputStream(output));
            writer.getDocumentSettings().setDataCacheStyle(RtfDataCache.CACHE_DISK);

            document.open();

            ExportRtf.addMetaData(document, titleName);

            Map<Integer, Integer> dims = new TreeMap<Integer, Integer>();
            for (int i = 0; i < nrOfColumns; i++) {
                if (selectedCols[order[i]]) {
                    dims.put(i, widths[order[i]]);
                }
            }
            int[] sizes;
            int w = 0;
            if (ExportRtfPrefs.isUsingNrCrt()) {
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
            rtfTable = new Table(sizes.length);
            rtfTable.setWidths(sizes);
            rtfTable.setPadding(2f);
            /**
             * daca se doreste ca ultima celula sa se intinda pana jos, se decomenteaza linia
             * urmatoare
             */
            // table.setExtendLastRow(true);

            if (ExportRtfPrefs.isUsingTitle()) {
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

            wait = new CWaitDlgClassic(nrOfItems);
            wait.setMessageLabel("Se genereaza fisierul...");
            wait.open();

            if (ExportRtfPrefs.isShowingHeader()) {
                ExportRtf.createTableHeader(swtTable, rtfTable, ExportRtfPrefs.isUsingNrCrt(), selectedCols, order, font);
            }

            if (ExportRtfPrefs.isUsingPageNo()) {
                hf = new HeaderFooter(new Phrase("Pag. "), new Phrase(" - a fost generata cu Books Manager, https://github.com/brontozaur"));
            } else {
                hf = new HeaderFooter(new Phrase("Raport generat cu Books Manager, https://github.com/brontozaur"), false);
            }
            hf.setAlignment(Element.ALIGN_CENTER);

            document.setFooter(hf);

            final TableItem[] items = swtTable.getItems();
            for (int i = 0; i < items.length; i++) {
                wait.advance(i);

                if (ExportRtfPrefs.isUsingNrCrt()) {
                    p = new Paragraph(String.valueOf(i + 1), font);
                    p.setAlignment(Element.ALIGN_JUSTIFIED);
                    cell = new Cell(p);
                    if (ExportRtfPrefs.isUsingGrayEffect()) {
                        if (Math.abs(i) % 2 == 1) {
                            cell.setGrayFill(0.9f);
                        }
                    }
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    rtfTable.addCell(cell);
                }

                for (int j = 0; j < nrOfColumns; j++) {
                    if (!selectedCols[order[j]]) {
                        continue;
                    }
                    p = new Paragraph(items[i].getText(order[j]), font);
                    p.setAlignment(Element.ALIGN_JUSTIFIED);

                    cell = new Cell(p);
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
                    if (ExportRtfPrefs.isUsingGrayEffect()) {
                        if (Math.abs(i) % 2 == 1) {
                            cell.setGrayFill(0.9f);
                        }
                    }

                    rtfTable.addCell(cell);
                }
                if ((i > 0) && (i % 5000 == 0)) {
                    document.add(rtfTable);
                    rtfTable.deleteAllRows();
                }
            }

            document.add(rtfTable);
            rtfTable.deleteAllRows();

            wait.setMessageLabel("Salvare fisier pe disc...va rugam asteptati...");

            document.close();
            writer.close();

            wait.close();

            logger.info("RTF file saved succesfully " + " with a total data row count of " + nrOfItems);

            ApplicationReport dbRap = new ApplicationReport();
            dbRap.setCale(output.getCanonicalPath());
            dbRap.setIdUser(EncodeLive.getIdUser());
            dbRap.setNume(titleName);
            dbRap.setType(ExportType.RTF);

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
                                          final Font font) throws BadElementException {
        Paragraph p;
        Cell cell;

        if (showNrCrt) {
            p = new Paragraph("Nr. crt", font);
            p.setAlignment(Element.ALIGN_JUSTIFIED);

            cell = new Cell(p);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(Color.YELLOW);
            table.addCell(cell);
        }

        for (int j = 0; j < swtTable.getColumnCount(); j++) {
            if (!selectedCols[order[j]]) {
                continue;
            }
            p = new Paragraph(swtTable.getColumn(order[j]).getText(), font);
            p.setAlignment(Element.ALIGN_JUSTIFIED);

            cell = new Cell(p);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(Color.YELLOW);
            table.addCell(cell);
        }
    }

}