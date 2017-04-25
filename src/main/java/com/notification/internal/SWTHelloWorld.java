package com.notification.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SWTHelloWorld {

    public static void main (String [] args) {
        Display display = new Display();
        Shell shell = new Shell(display);

        Text helloWorldTest = new Text(shell, SWT.NONE);
        helloWorldTest.setText("Hello World SWT");
        helloWorldTest.pack();

        shell.pack();
        shell.open ();

        NotifierDialog.notify("Title", "message", NotificationType.INFO);
        NotifierDialog.notify("Title", "message", NotificationType.CONNECTED);
        NotifierDialog.notify("Title", "message", NotificationType.ERROR);
        NotifierDialog.notify("Title", "message", NotificationType.LIBRARY);
        NotifierDialog.notify("Title", "message", NotificationType.HINT);
        NotifierDialog.notify("Title", "message", NotificationType.CONNECTION_FAILED);

        while (!shell.isDisposed ()) {
            if (!display.readAndDispatch ()) display.sleep ();
        }
        display.dispose ();
    }
}