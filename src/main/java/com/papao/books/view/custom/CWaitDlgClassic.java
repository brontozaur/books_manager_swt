package com.papao.books.view.custom;

import com.papao.books.view.util.WidgetCompositeUtil;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public final class CWaitDlgClassic {

    private Shell shell;
    private Shell parentShell;
    private Label labelMessage;
    private ProgressBarComposite cpbar;
	private static Logger logger = Logger.getLogger(CWaitDlgClassic.class);

    public CWaitDlgClassic() {
        this(0);
    }

    public CWaitDlgClassic(final String msg) {
        this(null, msg, 0);
    }

    public CWaitDlgClassic(final String msg, final int max) {
        this(null, msg, max);
    }

    public CWaitDlgClassic(final int MAX) {
        this(null, "Va rugam asteptati procesarea informatiilor..", MAX);
    }

    public CWaitDlgClassic(final Shell parent, final String operatie, final int MAX_SIZE) {

        try {
            if (parent != null) {
                this.parentShell = parent;
            }
            if (this.parentShell == null) {
                this.parentShell = new Shell(Display.getDefault());
            }
            this.shell = new Shell(this.parentShell, SWT.BORDER | SWT.TITLE | SWT.ON_TOP);
            GridLayout gridLayout = new GridLayout(2, false);
            this.shell.setLayout(gridLayout);
            this.shell.setText("Procesare date...");

            Canvas canvasImage = new Canvas(this.shell, SWT.NONE);
            GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).minSize(32, 32).hint(32, 32).applyTo(canvasImage);
            canvasImage.addListener(SWT.Paint, new Listener() {
                @Override
                public void handleEvent(final Event e) {
                    e.gc.drawImage(Display.getDefault().getSystemImage(SWT.ICON_INFORMATION), 0, 0);
                }
            });

            this.labelMessage = new Label(this.shell, SWT.WRAP);
            if (operatie != null) {
                this.labelMessage.setText(operatie);
            } else {
                this.labelMessage.setText("Va rugam asteptati procesarea datelor...");
            }
            GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.CENTER).hint(400, SWT.DEFAULT).minSize(400,
                    SWT.DEFAULT).applyTo(this.labelMessage);
            new Label(this.shell, SWT.NONE);

            this.cpbar = new ProgressBarComposite(this.shell, MAX_SIZE, MAX_SIZE > 0 ? SWT.SMOOTH : SWT.INDETERMINATE);
            GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(this.cpbar);
        } catch (Exception exc) {
            close();
            logger.error(exc.getMessage(), exc);
        }
    }

    public void setMessageLabel(final String message) {
        this.labelMessage.setText(message);
    }

    public void setMax(final int count) {
        if ((this.cpbar != null) && !this.cpbar.isDisposed()) {
            this.cpbar.setMax(count);
        }
    }

    public final int getMax() {
        if ((this.cpbar != null) && !this.cpbar.isDisposed()) {
            this.cpbar.getMax();
        }
        return 1;
    }

    public void open() {
        try {
            this.shell.getDisplay().syncExec(new Runnable() {
                @Override
                public void run() {
                    shell.pack();
                    WidgetCompositeUtil.centerInDisplay(shell);
                    shell.open();
                }
            });
        } catch (Exception exc) {
            close();
            logger.error(exc.getMessage(), exc);
        }
    }

    public void close() {
        try {
            if (this.shell != null) {
                if (!this.shell.isDisposed()) {
                    this.shell.close();
                }
                this.shell.dispose();
            }
            this.shell = null;
            if (this.labelMessage != null) {
                if (!this.labelMessage.isDisposed()) {
                    this.labelMessage.dispose();
                }
                this.labelMessage = null;
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public boolean isClosed() {
        return (this.shell == null) || this.shell.isDisposed();
    }

    public void advance() {
        this.cpbar.advance(this.cpbar.getSelection() + 1);
    }

    public void advance(final int idx) {
        this.cpbar.advance(idx);
    }

    public String getMessage() {
        if (isClosed()) {
            return "";
        }
        return this.labelMessage.getText();
    }

    public int getSelection() {
        if (isClosed()) {
            return 0;
        }
        return this.cpbar.getSelection();
    }
}