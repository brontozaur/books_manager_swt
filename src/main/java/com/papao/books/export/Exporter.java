package com.papao.books.export;

import com.papao.books.controller.ApplicationReportController;
import com.papao.books.export.html.ExportHtml;
import com.papao.books.export.pdf.ExportPdf;
import com.papao.books.export.rtf.ExportRtf;
import com.papao.books.export.txt.ExportTxt;
import com.papao.books.export.xls.ExportXls;
import com.papao.books.view.view.SWTeXtension;
import org.eclipse.swt.widgets.Table;

public class Exporter {

    public static final void export(final ExportType type,
                                    final Table table,
                                    final String reportName,
                                    final Class<?> clazz,
                                    ApplicationReportController controller) {
        Exporter.export(type, table, reportName, clazz, null, controller);
    }

    public static final void export(final ExportType type,
                                    final Table table,
                                    final String reportName,
                                    final Class<?> clazz,
                                    final String sufix,
                                    ApplicationReportController controller) {
        switch (type) {
            case PDF: {
                ExportPdf.exportPDF(table, reportName, clazz, sufix, controller);
                break;
            }
            case XLS: {
                ExportXls.exportExcel(table, reportName, clazz, sufix, controller);
                break;
            }
            case RTF: {
                ExportRtf.exportRTF(table, reportName, clazz, sufix, controller);
                break;
            }
            case HTML: {
                ExportHtml.exportHTML(table, reportName, clazz, sufix, controller);
                break;
            }
            case TXT: {
                ExportTxt.exportTxt(table, reportName, clazz, sufix, controller);
                break;
            }
            default:
                SWTeXtension.displayMessageW("Tip export necunoscut.", "Export necunoscut");
        }
    }

}
