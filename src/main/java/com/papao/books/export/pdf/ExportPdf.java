package com.papao.books.export.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.papao.books.FiltruAplicatie;
import com.papao.books.controller.ApplicationReportController;
import com.papao.books.export.ExportType;
import com.papao.books.export.VizualizareRapoarte;
import com.papao.books.model.ApplicationReport;
import com.papao.books.model.config.ExportPdfSetting;
import com.papao.books.model.config.TableSetting;
import com.papao.books.view.auth.EncodeLive;
import com.papao.books.view.custom.CWaitDlgClassic;
import com.papao.books.view.util.SettingsController;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public final class ExportPdf {

    private final static Logger logger = Logger.getLogger(ExportPdf.class);
    public final static int MAX_ROWS_PDF = 50000;

    public final static Font PDF_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
    public final static Font PDF_FONT_MIC = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL);

    public final static String PDF_FONT_ALIAS = "trilulilucrododilu";

    private ExportPdf() {
    }

    public static void exportPDF(final Table swtTable,
                                 final String reportName,
                                 final Class<?> clazz,
                                 final String tableKey,
                                 final ApplicationReportController controller) {
        Document document;
        PdfPTable pdfTable;
        Paragraph p;
        PdfPCell cell;
        int nrOfColumns;
        int nrOfItems;
        boolean[] selectedCols;
        int[] aligns;
        int[] widths;
        int[] order;
        String fileName;
        File output;
        CWaitDlgClassic wait = null;
        PdfWriter writer;
        Chunk chuckie;
        ExportPdfSetting exportSetting;

        Rectangle pageSize = PageSize.A4;
        String titleName = reportName;
        PdfName pdfVersion = PdfWriter.PDF_VERSION_1_2;
        Font font = ExportPdf.PDF_FONT;

        try {

            if (StringUtils.isNotEmpty(reportName)) {
                fileName = reportName + "_" + System.currentTimeMillis();
            } else {
                fileName = "Raport_PDF_" + System.currentTimeMillis();
            }

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
                if (selectedCols[order[i]]) {
                    atLeastOneCol = true;
                    break;
                }
            }

            if (!atLeastOneCol) {
                selectedCols[0] = true;
            }

            exportSetting = SettingsController.getExportPdfSetting();
            if (exportSetting == null) {
                exportSetting = new ExportPdfSetting();
                SettingsController.saveExportPdfSetting(exportSetting);
            }

            if (FiltruAplicatie.isReportsShowingOptions()) {
                ExportPdfSettings settings = new ExportPdfSettings();
                settings.setFont(font);
                settings.setNumeFisier(fileName);
                settings.setPageSize(pageSize);
                settings.setPdfVersion(pdfVersion);
                settings.setTitlu(titleName);
                settings.setNrOfItems(nrOfItems);
                settings.setSwtTable(swtTable);
                settings.setClazz(clazz);
                settings.setSufix(tableKey);

                ExportPdfOptionsView view = new ExportPdfOptionsView(swtTable.getShell(), settings);
                view.open();

                if (view.getUserAction() != SWT.OK) {
                    return;
                }

                settings = view.getSettings().cloneObject();

                fileName = settings.getNumeFisier();
                pageSize = settings.getPageSize();
                titleName = settings.getTitlu();
                font = settings.getFont();
                pdfVersion = settings.getPdfVersion();
                aligns = settings.getAligns();
                widths = settings.getDims();
                selectedCols = settings.getSelection();
                order = settings.getOrder();
            } else {
                fileName = exportSetting.getExportDir() + File.separator;
            }

            if (StringUtils.isEmpty(fileName)) {
                return;
            }

            if (!fileName.toLowerCase().endsWith(".pdf")) {
                fileName += ".pdf";
            }
            output = new File(fileName);
            logger.info("Exporting PDF content to file : " + fileName);

            /**
             * daca, prin absurd se doreste setarea unei culori pentru background-ul paginii, se vor
             * decomenta liniile urmatoare
             */
            /*
             * pageSize.setBackgroundColor(BaseColor.CYAN); Rectangle pageSize = new
             * Rectangle(PageSize.A4);
             */

            document = new Document(pageSize, 10, 10, 50, 50);
            writer = PdfWriter.getInstance(document, new FileOutputStream(output));
            writer.setCompressionLevel(exportSetting.getCompression());
            writer.setSpaceCharRatio(PdfWriter.NO_SPACE_CHAR_RATIO);
            writer.setPdfVersion(pdfVersion);
            if (exportSetting.isShowPageNumber()) {
                writer.setPageEvent(new MyPageEvent());
            }

            ExportPdf.addMetaData(document, reportName);

            document.open();

            final boolean showNrCrt = exportSetting.isShowNrCrt();
            final boolean isShowingGradient = exportSetting.isShowGrayEffect();

            if (exportSetting.isShowTitle()) {
                chuckie = new Chunk(titleName, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK));
                chuckie.setUnderline(0.7f, -2f);
                chuckie.setCharacterSpacing(0.5f);
                chuckie.setTextRenderMode(PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE, 0.7f, BaseColor.LIGHT_GRAY);
                // chuckie.setSkew(10f, 45f); - chestia asta roteste textul :-))
                /*
                 * chestia asta pune un background pe Chunk-ul cu numele raportului.
                 */
                // chuckie.setBackground(BaseColor.YELLOW, 50, 5, 50, 5);
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
            for (int i = 0; i < selectedCols.length; i++) {
                if (selectedCols[order[i]]) {
                    dims.put(i, widths[order[i]]);
                }
            }
            int[] sizes;
            int w = 0;
            if (showNrCrt) {
                sizes = new int[dims.size() + 1];
                sizes[0] = 70;
                w = 1;
            } else {
                sizes = new int[dims.size()];
            }
            int totalWidth = 0;
            for (Iterator<Integer> it = dims.values().iterator(); it.hasNext(); ) {
                sizes[w] = it.next().intValue();
                totalWidth += sizes[w];
                w++;
            }

            pdfTable = new PdfPTable(sizes.length);
            pdfTable.setSplitRows(false);
            pdfTable.setTotalWidth(totalWidth);
            pdfTable.setWidths(sizes);
            /**
             * daca se doreste ca ultima celula sa se intinda pana jos, se decomenteaza linia
             * urmatoare
             */
            // table.setExtendLastRow(true);
            // pdfTable.setLockedWidth(true);

            wait = new CWaitDlgClassic(nrOfItems);
            wait.setMessageLabel("Se genereaza fisierul...");
            wait.open();

            if (exportSetting.isShowHeader()) {
                ExportPdf.createHeader(swtTable, selectedCols, pdfTable, order, showNrCrt);
            }

            pdfTable.setHeaderRows(1);

            pdfTable.setSkipFirstHeader(true);

            ExportPdf.createFooter(pdfTable);

            if (exportSetting.isShowHeader()) {
                pdfTable.setHeaderRows(2);
            }
            pdfTable.setFooterRows(1);

            if (exportSetting.isShowHeader()) {
                ExportPdf.createHeader(swtTable, selectedCols, pdfTable, order, showNrCrt);
            }

            final TableItem[] items = swtTable.getItems();
            for (int i = 0; i < items.length; i++) {
                wait.advance(i);

                if (showNrCrt) {
                    p = new Paragraph(String.valueOf(i + 1), font);
                    p.setAlignment(Element.ALIGN_JUSTIFIED);
                    cell = new PdfPCell(p);
                    if (isShowingGradient) {
                        if (Math.abs(i) % 2 == 1) {
                            cell.setGrayFill(0.9f);
                        }
                    }
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfTable.addCell(cell);
                }

                for (int j = 0; j < nrOfColumns; j++) {
                    if (!selectedCols[order[j]]) {
                        continue;
                    }
                    p = new Paragraph(items[i].getText(order[j]), font);
                    p.setAlignment(Element.ALIGN_JUSTIFIED);

                    cell = new PdfPCell(p);
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
                    if (isShowingGradient) {
                        if (Math.abs(i) % 2 == 1) {
                            cell.setGrayFill(0.9f);
                        }
                    }

                    pdfTable.addCell(cell);
                }
                if ((i > 0) && (Math.abs(i) % 5000 == 0)) {
                    pdfTable.setSkipLastFooter(true);
                    document.add(pdfTable);
                    pdfTable.deleteBodyRows();
                }
            }

            pdfTable.setSkipLastFooter(true);
            document.add(pdfTable);
            pdfTable.deleteBodyRows();

            /**
             * aici vin alte chestii, de genul cine a intocmit raportul, semnaturi, etc, daca e
             * cazul.
             */

            /**
             * acum vom plasa in mod artificial chestia cu "generat de....", utilizand o noua
             * tabela, cu o singura linie, la care ii zicem sa extinda ultima linie pana jos.
             */
            pdfTable = new PdfPTable(2);
            pdfTable.setExtendLastRow(true);
            ExportPdf.createFooter(pdfTable);
            document.add(pdfTable);

            wait.setMessageLabel("Salvare fisier pe disc...va rugam asteptati...");

            document.close();

            wait.close();

            logger.info("PDF file saved succesfully " + " with a total data row count of " + nrOfItems);

            ApplicationReport dbRap = new ApplicationReport();
            dbRap.setCale(output.getCanonicalPath());
            dbRap.setIdUser(EncodeLive.getIdUser());
            dbRap.setNume(titleName);
            dbRap.setType(ExportType.PDF);
            controller.save(dbRap);

            VizualizareRapoarte.showRaport(dbRap, controller);
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

    private static void createHeader(final Table swtTable,
                                     final boolean[] selectedCols,
                                     final PdfPTable pdfTable,
                                     final int[] order,
                                     final boolean showNrCrt) {
        PdfPCell cell;

        if (showNrCrt) {
            cell = new PdfPCell(new Paragraph("Nr.Crt."));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.YELLOW);
            cell.setPadding(2.0f);
            pdfTable.addCell(cell);
        }

        for (int j = 0; j < swtTable.getColumnCount(); j++) {
            if (!selectedCols[order[j]]) {
                continue;
            }
            cell = new PdfPCell(new Paragraph(swtTable.getColumn(order[j]).getText()));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.YELLOW);
            cell.setPadding(2.0f);
            pdfTable.addCell(cell);
        }
    }

    public static void createFooter(final PdfPTable pdfTable) {
        Paragraph p;
        PdfPCell cell;
        p = new Paragraph(
                "Raport generat cu Books Manager, https://github.com/brontozaur",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.LIGHT_GRAY));
        p.setAlignment(Element.ALIGN_JUSTIFIED);

        cell = new PdfPCell(p);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setColspan(pdfTable.getNumberOfColumns());
        cell.setBorder(0);

        pdfTable.addCell(cell);
    }

    public static void addMetaData(final Document document, final String numeFisier) {
        document.addAuthor(EncodeLive.getCurrentUserName());
        document.addCreationDate();
        document.addCreator("Books manager \u00A9 brontozaur");
        document.addProducer();
        document.addSubject("Application time: " + EncodeLive.getUtilDateLogin().toString());
        document.addTitle(numeFisier);
        document.addKeywords(numeFisier + ",raport" + "," + EncodeLive.getCurrentUserName());
    }

    public static class MyPageEvent extends PdfPageEventHelper {
        protected PdfTemplate total;
        protected BaseFont helv;

        @Override
        public void onOpenDocument(final PdfWriter writer, final Document document) {
            this.total = writer.getDirectContent().createTemplate(100, 100);
            this.total.setBoundingBox(new Rectangle(0, 0, 100, 100));
            try {
                this.helv = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }

        @Override
        public void onEndPage(final PdfWriter writer, final Document document) {
            PdfContentByte cb = writer.getDirectContent();
            cb.saveState();
            String text = "Pag. " + writer.getPageNumber() + " / ";
            float textBase = document.bottom() - 20;
            float textSize = this.helv.getWidthPoint(text, 12);
            cb.beginText();
            cb.setFontAndSize(this.helv, 12);
            if ((Math.abs(writer.getPageNumber()) % 2) == 1) {
                cb.setTextMatrix(document.left() + 20, textBase);
                cb.showText(text);
                cb.endText();
                cb.addTemplate(this.total, document.left() + textSize + 20, textBase);
            } else {
                float adjust = this.helv.getWidthPoint("0", 12);
                cb.setTextMatrix(document.right() - textSize - adjust - 30, textBase);
                cb.showText(text);
                cb.endText();
                cb.addTemplate(this.total, Float.valueOf(document.right() - adjust - 30), textBase);
            }
            cb.restoreState();
        }

        @Override
        public void onCloseDocument(final PdfWriter writer, final Document document) {
            this.total.beginText();
            this.total.setFontAndSize(this.helv, 12);
            this.total.setTextMatrix(0, 0);
            this.total.showText(String.valueOf(writer.getPageNumber()));
            this.total.endText();
        }
    }

}