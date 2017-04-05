package com.papao.books.view.util;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public final class WidgetCursorUtil {

	private static Logger logger = Logger.getLogger(WidgetCursorUtil.class);

    private WidgetCursorUtil() {}

    private final static Cursor CURSOR_APPSTARTING = new Cursor(Display.getDefault(), SWT.CURSOR_APPSTARTING);
    private final static Cursor CURSOR_ARROW = new Cursor(Display.getDefault(), SWT.CURSOR_ARROW);
    private final static Cursor CURSOR_CROSS = new Cursor(Display.getDefault(), SWT.CURSOR_CROSS);
    private final static Cursor CURSOR_HAND = new Cursor(Display.getDefault(), SWT.CURSOR_HAND);
    private final static Cursor CURSOR_HELP = new Cursor(Display.getDefault(), SWT.CURSOR_HELP);
    private final static Cursor CURSOR_UPARROW = new Cursor(Display.getDefault(), SWT.CURSOR_UPARROW);
    private final static Cursor CURSOR_WAIT = new Cursor(Display.getDefault(), SWT.CURSOR_WAIT);

    /**
     * @param style
     *            one of the cursor styles implemented here.
     * @return The corresponding value, as a static member of this class, so, dispose is NOT
     *         required and NOT recommended. That is the soul reason why the defined cursors have
     *         declared private access.
     */
    public static Cursor getCursor(final int style) {
        try {
            switch (style) {
                case SWT.CURSOR_ARROW:
                    return WidgetCursorUtil.CURSOR_ARROW;
                case SWT.CURSOR_CROSS:
                    return WidgetCursorUtil.CURSOR_CROSS;
                case SWT.CURSOR_HAND:
                    return WidgetCursorUtil.CURSOR_HAND;
                case SWT.CURSOR_HELP:
                    return WidgetCursorUtil.CURSOR_HELP;
                case SWT.CURSOR_UPARROW:
                    return WidgetCursorUtil.CURSOR_UPARROW;
                case SWT.CURSOR_WAIT:
                    return WidgetCursorUtil.CURSOR_WAIT;
                case SWT.CURSOR_APPSTARTING:
                    return WidgetCursorUtil.CURSOR_APPSTARTING;
                default: {
					logger.info("Style undefined..default cursor returned");
                    return WidgetCursorUtil.CURSOR_ARROW;
                }
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return WidgetCursorUtil.CURSOR_ARROW;
        }
    }

    /**
     * @param c
     *            the control on wich we'll add 2 listeners, responsible for cursor change on
     *            SWT.MouseEnter and SWT.MouseExit events.
     * @param cursorIn
     *            the desired cursor for the SWT.MouseEnter event. If null value is passed as
     *            parameter, the CURSOR_HAND value will be used.
     * @param cursorOut
     *            the desired cursor for the SWT.MouseExit event.If null value is passed as
     *            parameter, the CURSOR_ARROW value will be used.
     */
    public static void addHandCursorListener(final Control c,
                                             final Cursor cursorIn,
                                             final Cursor cursorOut) {
        try {
            if ((c == null) || c.isDisposed()) {
                return;
            }
            c.addListener(SWT.MouseEnter, new Listener() {
                @Override
                public void handleEvent(final Event e) {
                    if ((cursorIn != null) && !cursorIn.isDisposed()) {
                        c.setCursor(cursorIn);
                        return;
                    }
                    c.setCursor(WidgetCursorUtil.CURSOR_HAND);
                }
            });
            c.addListener(SWT.MouseExit, new Listener() {
                @Override
                public void handleEvent(final Event e) {
                    if ((cursorOut != null) && !cursorOut.isDisposed()) {
                        c.setCursor(cursorOut);
                        return;
                    }
                    c.setCursor(WidgetCursorUtil.CURSOR_ARROW);
                }
            });
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public static void addHandCursorListener(final Control c) {
        WidgetCursorUtil.addHandCursorListener(c, null, null);
    }
}
