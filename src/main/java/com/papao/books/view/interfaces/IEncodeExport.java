package com.papao.books.view.interfaces;

import org.eclipse.swt.widgets.Control;

/**
 * Callers should include the Encode[C]View.ADD_TI_EXPORT option, so, the print tool item will be
 * displayed.
 */
public interface IEncodeExport {

    void exportTxt();

    void exportPDF();

    void exportExcel();

    void exportRTF();

    void exportHTML();

    void createExportMenu(final Control c);
}
