package com.papao.books.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public final class ColorUtil {

    private ColorUtil() {}

    public static final Color COLOR_LABEL_FIRMA = new Color(Display.getDefault(), new RGB(255, 255, 247));

    public static final Color COLOR_LABEL_DATA = new Color(Display.getDefault(), new RGB(255, 255, 243));

    public static final Color COLOR_LABEL_USER = new Color(Display.getDefault(), new RGB(255, 255, 237));

    public static final Color COLOR_LABEL_PASSWORD = new Color(Display.getDefault(), new RGB(255, 250, 228));

    public static final Color COLOR_FOCUS_YELLOW = new Color(Display.getDefault(), new RGB(255, 255, 220));;//Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
    /*
     * new Color(Display.getDefault(), new RGB( 252, 249, 194));
     */

    public static final Color COLOR_ALBASTRU_DESCHIS = new Color(Display.getDefault(), new RGB(232, 242, 254));

    public static final Color COLOR_ALBASTRU_DESCHIS_WINDOWS = new Color(Display.getDefault(), new RGB(213, 230, 248));

    public static final Color COLOR_ALBASTRU_DESCHIS_PGROUP = new Color(Display.getDefault(), new RGB(216, 223, 230));

    public static final Color COLOR_VERDE_FOARTE_DESCHIS = new Color(Display.getDefault(), new RGB(228, 242, 207));

    public static final Color COLOR_ROSU_SEMI_ROSU = new Color(Display.getDefault(), new RGB(231, 118, 113));

    public static final Color COLOR_ROSU_SEMI_ROSU2 = new Color(Display.getDefault(), new RGB(251, 203, 198));

    public static final Color COLOR_WHITE = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);

    public static final Color COLOR_ALBASTRU_INCHIS = new Color(Display.getDefault(), new RGB(68, 97, 155));

    public static final Color COLOR_SYSTEM = new Color(Display.getDefault(), new RGB(212, 208, 200));

    // public static final Color COLOR_ALBASTRU_FACEBOOK = new Color(Display.getDefault(), new RGB(
    // 59,
    // 89,
    // 152));
    public static final Color COLOR_ALBASTRU_FACEBOOK = new Color(Display.getDefault(), new RGB(59, 92, 145));

    public static final Color COLOR_VERDE_DESCHIS1 = new Color(Display.getDefault(), new RGB(47, 190, 25));

    public static final Color COLOR_ALBASTRU_INCHIS_ATOM = new Color(Display.getDefault(), new RGB(10, 36, 106));

    public static final Color COLOR_ALBASTRU_INCHIS_ATOM2 = new Color(Display.getDefault(), new RGB(54, 98, 156));

    public static final Color COLOR_VERDE_FRUNZE = new Color(Display.getDefault(), new RGB(147, 197, 50));

    public static final Color COLOR_ALBASTRU_DESCHIS_PHEX = new Color(Display.getDefault(), new RGB(172, 210, 248));

    public static final Color COLOR_ALBASTRU_DESCHIS_ATOM = new Color(Display.getDefault(), new RGB(166, 202, 240));

    public static final Color COLOR_ALBASTRU_DESCHIS_ATOM2 = new Color(Display.getDefault(), new RGB(100, 163, 208));

    public static final Color COLOR_BLACK = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);

    public static final Color COLOR_ALBASTRU_DESCHIS_RB = new Color(Display.getDefault(), new RGB(200, 217, 238));

    public final static Color[] DEFAULT_TAB_SELECTION_BACKGROUND = new Color[] {
            ColorUtil.COLOR_FOCUS_YELLOW, ColorUtil.COLOR_SYSTEM, ColorUtil.COLOR_FOCUS_YELLOW, ColorUtil.COLOR_SYSTEM };

    public final static int[] DEFAULT_TAB_SELECTION_PERCENTS = new int[] {
            25, 50, 100 };

}
