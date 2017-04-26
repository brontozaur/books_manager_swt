package com.novocode.naf.swt.custom;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

public class BalloonNotification {

    private static final Logger log = Logger.getLogger(BalloonNotification.class.getName());

    /**
     * Opens a notification window next to a control. The notification window
     * will show the given title as the title of the balloon, The given text as
     * the description.
     * <p>
     * The window will be hidden automatically after the value specified in the
     * timeout expires
     *
     * @param control the control, next to where the widget will appear
     * @param title   the title of the balloon
     * @param text    the text to display as contents
     * @param timeout the timeout in milliseconds for automatically hidding the
     *                balloon
     */
    public static void showNotification(Control control, String title,
                                        String text, int timeout) {

        if (control != null && control.isDisposed()) {
            control = null;
        }

        Shell parentShell = null;
        if (control != null) {
            parentShell = control.getShell();
            parentShell.forceActive();
            control.setFocus();
        }

        final BalloonWindow window = new BalloonWindow(Display.getDefault(), SWT.ON_TOP
                | SWT.CLOSE | SWT.TITLE);

        window.setText(title);

        // Adding the text to the contents. Pack() is required
        // so the size of the composite is recalculated, else
        // the contents won't show
        Composite c = window.getContents();
        c.setLayout(new FillLayout());
        Label l = new Label(c, SWT.NONE);
        l.setBackground(c.getBackground());
        l.setText(text);
        c.pack(true);

        // Locate the balloon to the widget location
        if (control != null) {
            Point widgetLocation = control.toDisplay(new Point(control.getBounds().x, 0));
            window.setLocation(widgetLocation);
        }

        // Runnable that will close the window after time has been expired
        final Runnable closeWindow = new Runnable() {

            public void run() {
                final Shell shell = window.getShell();
                if (shell.isDisposed())
                    return;
                shell.forceActive();
                window.close();
            }
        };
        window.getShell().getDisplay().timerExec(timeout, closeWindow);

        window.open();

    }
}