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
    private Label labelMsg;
    private ProgressBarComposite cpbar;
    private Canvas canvasImage;
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
            setShell(new Shell(this.parentShell, SWT.BORDER | SWT.TITLE | SWT.ON_TOP));
            GridLayout gridLayout = new GridLayout(2, false);
            getShell().setLayout(gridLayout);
            getShell().setText("Procesare date...");

            setCanvasImage(new Canvas(getShell(), SWT.NONE));
            GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).minSize(32, 32).hint(32, 32).applyTo(getCanvasImage());
            getCanvasImage().addListener(SWT.Paint, new Listener() {
                @Override
                public void handleEvent(final Event e) {
                    e.gc.drawImage(Display.getDefault().getSystemImage(SWT.ICON_INFORMATION), 0, 0);
                }
            });

            setLabelMsg(new Label(getShell(), SWT.WRAP));
            if (operatie != null) {
                getLabelMsg().setText(operatie);
            } else {
                getLabelMsg().setText("Va rugam asteptati procesarea datelor...");
            }
            GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.CENTER).hint(400, SWT.DEFAULT).minSize(400,
                    SWT.DEFAULT).applyTo(getLabelMsg());
            new Label(getShell(), SWT.NONE);

            this.cpbar = new ProgressBarComposite(getShell(), MAX_SIZE, MAX_SIZE > 0 ? SWT.SMOOTH : SWT.INDETERMINATE);
            GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(this.cpbar);
        } catch (Exception exc) {
            close();
            logger.error(exc.getMessage(), exc);
        }
    }

    public void setMessageLabel(final String message) {
        getLabelMsg().setText(message);
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
            getShell().getDisplay().syncExec(new Runnable() {
                @Override
                public void run() {
                    getShell().pack();
                    WidgetCompositeUtil.centerInDisplay(getShell());
                    getShell().open();
                }
            });
        } catch (Exception exc) {
            close();
            logger.error(exc.getMessage(), exc);
        }
    }

    public void close() {
        try {
            if (getShell() != null) {
                if (!getShell().isDisposed()) {
                    getShell().close();
                }
                getShell().dispose();
            }
            setShell(null);
            if (getLabelMsg() != null) {
                if (!getLabelMsg().isDisposed()) {
                    getLabelMsg().dispose();
                }
                setLabelMsg(null);
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public boolean isClosed() {
        return (getShell() == null) || getShell().isDisposed();
    }

    public void advance() {
        this.cpbar.advance(this.cpbar.getSelection() + 1);
    }

    public void advance(final int idx) {
        this.cpbar.advance(idx);
    }

    private Label getLabelMsg() {
        return this.labelMsg;
    }

    private void setLabelMsg(final Label labelMsg) {
        this.labelMsg = labelMsg;
    }

    private Canvas getCanvasImage() {
        return this.canvasImage;
    }

    private void setCanvasImage(final Canvas canvasImage) {
        this.canvasImage = canvasImage;
    }

    private void setShell(final Shell shell) {
        this.shell = shell;
    }

    private Shell getShell() {
        return this.shell;
    }

    public String getMessage() {
        if (isClosed()) {
            return "";
        }
        return this.labelMsg.getText();
    }

    public int getSelection() {
        if (isClosed()) {
            return 0;
        }
        return this.cpbar.getSelection();
    }
}