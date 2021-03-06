package com.papao.books.ui.interfaces;

import org.eclipse.swt.widgets.Control;

/**
 * Callers should include the Encode[C]View.ADD_TI_EXPORT option, so, the print tool item will be
 * displayed.
 */
public interface IExport {

    void exportTxt();

    void exportPDF();

    void exportExcel();

    void exportRTF();

    void exportHTML();

    void createExportMenu(final Control c);
}
