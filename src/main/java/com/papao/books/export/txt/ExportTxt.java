package com.papao.books.export.txt;

import com.inamik.utils.SimpleTableFormatter;
import com.inamik.utils.TableFormatter;
import com.papao.books.config.BooleanSetting;
import com.papao.books.controller.ReportController;
import com.papao.books.controller.SettingsController;
import com.papao.books.export.ExportType;
import com.papao.books.export.VizualizareRapoarte;
import com.papao.books.model.ApplicationReport;
import com.papao.books.model.config.ExportTxtSetting;
import com.papao.books.model.config.TableSetting;
import com.papao.books.ui.auth.EncodeLive;
import com.papao.books.ui.custom.CWaitDlgClassic;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public final class ExportTxt {

    private static final Logger logger = Logger.getLogger(ExportTxt.class);

    private ExportTxt() {
    }

    public static void exportTxt(final Table table,
                                 final String numeRaport,
                                 final Class<?> clazz,
                                 final String tableKey) {
        TableFormatter tf;
        File output;
        PrintStream ps = null;
        int nrOfItems;
        int nrOfColumns;
        String[] tbl;
        CWaitDlgClassic wait = null;
        String fileName;
        boolean showNrCrt;
        String titleName = numeRaport;
        boolean[] selectedCols;
        int[] aligns;
        int[] widths;
        int[] order;
        ExportTxtSetting exportSetting;
        try {
            if ((table == null) || table.isDisposed()) {
                return;
            }
            nrOfItems = table.getItemCount();
            nrOfColumns = table.getColumnCount();

            if ((nrOfItems == 0) || (nrOfColumns <= 0)) {
                SWTeXtension.displayMessageW("Nu există date pentru a fi exportate!");
                return;
            }

            if (StringUtils.isNotEmpty(titleName)) {
                fileName = titleName + "_" + System.currentTimeMillis();
            } else {
                fileName = "Raport_TXT_" + System.currentTimeMillis();
            }

            TableSetting setting = SettingsController.getTableSetting(nrOfColumns, clazz, tableKey);
            if (setting == null) {
                selectedCols = new boolean[nrOfColumns];
                Arrays.fill(selectedCols, true);
                order = table.getColumnOrder();

                aligns = new int[nrOfColumns];
                widths = new int[nrOfColumns];
                for (int i = 0; i < nrOfColumns; i++) {
                    aligns[i] = table.getColumns()[i].getAlignment();
                    widths[i] = table.getColumns()[i].getWidth();
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

            exportSetting = SettingsController.getExportTxtSetting();
            if (exportSetting == null) {
                exportSetting = new ExportTxtSetting();
                SettingsController.saveExportTxtSetting(exportSetting);
            }

            if (SettingsController.getBoolean(BooleanSetting.REPORT_SHOW_OPTIONS)) {
                ExportTxtSettings settings = new ExportTxtSettings();
                settings.setNumeFisier(fileName);
                settings.setSwtTable(table);
                settings.setClazz(clazz);
                settings.setTableKey(tableKey);
                settings.setTitlu(titleName);
                settings.setNrOfItems(nrOfItems);

                ExportTxtOptionsView view = new ExportTxtOptionsView(table.getShell(), settings);
                view.open();

                if (view.getUserAction() != SWT.OK) {
                    return;
                }

                settings = view.getSettings().cloneObject();
                exportSetting = SettingsController.getExportTxtSetting();

                fileName = settings.getNumeFisier();
                titleName = settings.getTitlu();
                aligns = settings.getAligns();
                widths = settings.getDims();
                selectedCols = settings.getSelection();
                order = settings.getOrder();
            } else {
                fileName = exportSetting.getExportDir() + File.separator;
            }

            showNrCrt = exportSetting.isShowNrCrt();

            if (StringUtils.isEmpty(fileName)) {
                return;
            }

            if (fileName.toLowerCase().endsWith(".txt")) {
                output = new File(fileName);
            } else {
                output = new File(fileName + ".txt");
            }

            Map<Integer, Integer> dims = new TreeMap<Integer, Integer>();
            for (int i = 0; i < nrOfColumns; i++) {
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
            for (Iterator<Integer> it = dims.values().iterator(); it.hasNext(); ) {
                sizes[w] = it.next().intValue();
                w++;
            }

            tf = new SimpleTableFormatter(exportSetting.isHasBorder());
            tf.nextRow();
            if (showNrCrt) {
                tf.nextCell(TableFormatter.ALIGN_CENTER, TableFormatter.VALIGN_CENTER);
                tf.addLine("Nr crt.");
            }
            for (int i = 0; i < nrOfColumns; i++) {
                if (selectedCols[order[i]]) {
                    tf.nextCell(ExportTxt.getAlign(aligns[order[i]]), TableFormatter.VALIGN_CENTER);
                    tf.addLine(table.getColumn(order[i]).getText());
                }
            }

            wait = new CWaitDlgClassic(nrOfItems);
            wait.open();

            tf.nextRow();
            for (int i = 0; i < sizes.length; i++) {
                tf.nextCell();
                tf.addLine("");
            }

            for (int j = 0; j < nrOfItems; j++) {
                wait.advance(j);
                Display.getDefault().readAndDispatch();
                tf.nextRow();
                if (showNrCrt) {
                    tf.nextCell();
                    tf.addLine(String.valueOf(j + 1));
                }
                for (int i = 0; i < nrOfColumns; i++) {
                    if (selectedCols[order[i]]) {
                        tf.nextCell(ExportTxt.getAlign(aligns[order[i]]), TableFormatter.VALIGN_CENTER);
                        tf.addLine(table.getItem(j).getText(order[i]).concat("  "));
                    }
                }
            }

            wait.close();
            tbl = tf.getFormattedTable();

            logger.info("ExportTXT content to file : " + fileName);
            ps = new PrintStream(output);

            if (exportSetting.isShowTitle()) {
                ps.println(titleName);
                ps.println();
            }

            for (int i = 0, size = tbl.length; i < size; i++) {
                ps.println("\t" + tbl[i]);
                if (exportSetting.isHasBorder()) {
                    if (i == 2) {
                        ps.println();
                        i++;
                    }
                } else if (i == 0) {
                    ps.println();
                }
            }
            ps.println();
            ps.println("Raport generat cu Books Manager, https://github.com/brontozaur");
            ps.close();
            logger.info("ExportTXT content to file completed succesfully.");

            ApplicationReport dbRap = new ApplicationReport();
            dbRap.setCale(output.getCanonicalPath());
            dbRap.setIdUser(EncodeLive.getIdUser());
            dbRap.setNume(titleName);
            dbRap.setType(ExportType.TXT);

            ReportController.save(dbRap);

            VizualizareRapoarte.showRaport(dbRap);
        } catch (IOException exc) {
            wait.close();
            SWTeXtension.displayMessageE("A intervenit o eroare la generarea fisierului.", exc);
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    private static int getAlign(final int swtConstant) {
        if (swtConstant == SWT.LEFT) {
            return TableFormatter.ALIGN_LEFT;
        } else if (swtConstant == SWT.RIGHT) {
            return TableFormatter.ALIGN_RIGHT;
        } else if (swtConstant == SWT.CENTER) {
            return TableFormatter.ALIGN_CENTER;
        }
        return TableFormatter.ALIGN_DEFAULT;
    }
}
