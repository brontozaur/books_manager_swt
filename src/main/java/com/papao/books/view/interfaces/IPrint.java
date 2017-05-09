package com.papao.books.view.interfaces;

import org.eclipse.swt.widgets.Control;

/**
 * Callers should
 * include the Encode[C]View.ADD_TI_PRINT option, so, the print tool item will be displayed.
 */
public interface IPrint {

    void printPDF();

    void printPrinter();

    void createPrintMenu(final Control c);

}
