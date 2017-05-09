package org.mihalis.opal.notify;

import com.papao.books.ui.AppImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

public class NotifierSnippet {
    /**
     * @param args
     */
    public static void main(final String[] args) {
        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setText("Notifier Snippet");
        shell.setSize(200, 200);
        shell.setLayout(new FillLayout(SWT.VERTICAL));

        final int[] counter = new int[1];
        counter[0] = 0;

        // Yellow theme (default)
        final Button testerYellow = new Button(shell, SWT.PUSH);
        testerYellow.setText("Push me [Yellow theme]!");
        testerYellow.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(final Event event) {
                Notifier.notify(AppImages.getImage16(AppImages.IMG_SHOW), "Notificare", "Se termina berea!!!", NotifierColorsFactory.NotifierTheme.YELLOW_THEME);
                counter[0]++;
            }

        });

        // Blue theme
        final Button testerBlue = new Button(shell, SWT.PUSH);
        testerBlue.setText("Push me [Blue theme]!");
        testerBlue.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(final Event event) {
                Notifier.notify("New Mail message", "Laurent CARON (lcaron@...)<br/><br/>Test message #" + counter[0] + "...", NotifierColorsFactory.NotifierTheme.BLUE_THEME);
                counter[0]++;
            }

        });

        // Grey theme
        final Button testerGrey = new Button(shell, SWT.PUSH);
        testerGrey.setText("Push me [Gray theme]!");
        testerGrey.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(final Event event) {
                Notifier.notify("New Mail message", "Laurent CARON (lcaron@...)<br/><br/>Test message #" + counter[0] + "...", NotifierColorsFactory.NotifierTheme.GRAY_THEME);
                counter[0]++;
            }

        });

        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }
}
