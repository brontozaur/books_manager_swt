package com.papao.books.view.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public final class FontUtil {

    private FontUtil() {}

    public final static Font TAHOMA8_BOLD = new Font(Display.getDefault(), "Tahoma", 8, SWT.BOLD);
    public final static Font TAHOMA10_BOLD = new Font(Display.getDefault(), "Tahoma", 10, SWT.BOLD);

    public final static Font VERDANA10_BOLD = new Font(Display.getDefault(), "Verdana", 10, SWT.BOLD);

    public final static Font VERDANA14_ITALIC = new Font(Display.getDefault(), "Verdana", 14, SWT.ITALIC);
    public final static Font VERDANA14_NORMAL = new Font(Display.getDefault(), "Verdana", 14, SWT.NORMAL);
    public final static Font VERDANA12_NORMAL = new Font(Display.getDefault(), "Verdana", 12, SWT.NORMAL);
    public final static Font TAHOMA14_NORMAL = new Font(Display.getDefault(), "Tahoma", 14, SWT.NORMAL);
    public final static Font TAHOMA12_NORMAL = new Font(Display.getDefault(), "Tahoma", 12, SWT.NORMAL);
    public final static Font TAHOMA10_NORMAL = new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL);
    public final static Font TAHOMA12_BOLD = new Font(Display.getDefault(), "Tahoma", 12, SWT.BOLD);
}
