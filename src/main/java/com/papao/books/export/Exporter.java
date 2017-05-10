package com.papao.books.export;

import com.papao.books.export.html.ExportHtml;
import com.papao.books.export.pdf.ExportPdf;
import com.papao.books.export.rtf.ExportRtf;
import com.papao.books.export.txt.ExportTxt;
import com.papao.books.export.xls.ExportXls;
import com.papao.books.ui.view.SWTeXtension;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

public class Exporter {

    public static void export(final ExportType type,
                              final Table table,
                              final String reportName,
                              final Class<?> clazz,
                              final String sufix) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case PDF: {
                        ExportPdf.exportPDF(table, reportName, clazz, sufix);
                        break;
                    }
                    case XLS: {
                        ExportXls.exportExcel(table, reportName, clazz, sufix);
                        break;
                    }
                    case RTF: {
                        ExportRtf.exportRTF(table, reportName, clazz, sufix);
                        break;
                    }
                    case HTML: {
                        ExportHtml.exportHTML(table, reportName, clazz, sufix);
                        break;
                    }
                    case TXT: {
                        ExportTxt.exportTxt(table, reportName, clazz, sufix);
                        break;
                    }
                    default:
                        SWTeXtension.displayMessageW("Tip export necunoscut.", "Export necunoscut");
                }
            }
        });
    }

}
