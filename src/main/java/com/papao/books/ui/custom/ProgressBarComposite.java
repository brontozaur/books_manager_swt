package com.papao.books.ui.custom;

import com.papao.books.ui.util.FontUtil;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;

public class ProgressBarComposite extends Composite {

    private static Logger logger = Logger.getLogger(ProgressBarComposite.class);

    private ProgressBar progressBar;
    private int max = 1;

    /**
     * variabila care retine idx in timp la care s-a inceput rularea clasei
     */
    private final int pBarStyle;
    private final Color progressBarColor;

    public ProgressBarComposite(final Composite parent, final int pBarStyle) {
        this(parent, 1, pBarStyle);
    }

    public ProgressBarComposite(final Composite parent, final int max, final int pBarStyle) {
        this(parent, max, null, pBarStyle);
    }

    public ProgressBarComposite(final Composite parent, final int max, final Color progressBarColor, final int pBarStyle) {
        super(parent, SWT.NONE);
        this.pBarStyle = pBarStyle;
        this.max = max;
        if (this.max <= 0) {
            this.max = 1;
        }
        this.progressBarColor = progressBarColor;

        int numColumns = 1;
        if ((this.pBarStyle & SWT.INDETERMINATE) != SWT.INDETERMINATE) {
            numColumns++;
        }
        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(true, false).applyTo(this);
        GridLayoutFactory.fillDefaults().numColumns(numColumns).equalWidth(false).margins(0, 0).applyTo(this);

        addComponents();
    }

    private void addComponents() {
        this.progressBar = new ProgressBar(this, this.pBarStyle);
        this.progressBar.setMinimum(0);
        this.progressBar.setMaximum(this.max);
        if ((this.progressBarColor != null) && !this.progressBarColor.isDisposed()) {
            this.progressBar.setForeground(this.progressBarColor);
        }
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(this.progressBar);

        if ((this.pBarStyle & SWT.INDETERMINATE) != SWT.INDETERMINATE) {
            addPaintListener();
        }
    }

    private void addPaintListener() {
        this.progressBar.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(final PaintEvent e) {
                if ((ProgressBarComposite.this.pBarStyle & SWT.INDETERMINATE) == SWT.INDETERMINATE) {
                    return;
                }
                int selection = ProgressBarComposite.this.progressBar.getSelection();
                if (selection == 0) {
                    return;
                }
                String string = (int) ((selection * 1.0 / ProgressBarComposite.this.max) * 100) + "%";

                Point point = new Point(
                        ProgressBarComposite.this.progressBar.getBounds().width,
                        ProgressBarComposite.this.progressBar.getBounds().height);
                e.gc.setFont(FontUtil.TAHOMA14_NORMAL);
                e.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));

                int stringHeight = e.gc.getFontMetrics().getHeight();

                if (selection == ProgressBarComposite.this.max - 1) {
                    string = "COMPLET";
                }
                int xCoord = point.x / 2 - 10;
                if (xCoord <= 0) {
                    xCoord = point.x / 2;
                }
                e.gc.drawString(string, xCoord, (point.y - stringHeight) / 2 - 1, true);
                Display.getCurrent().readAndDispatch();
            }
        });
    }

    public void advance(final int idx) {
        try {
            if (isDisposed()) {
                return;
            }
            if ((this.pBarStyle & SWT.INDETERMINATE) == SWT.INDETERMINATE) {
                return;
            }
            if (idx < 0) {
                throw new IllegalArgumentException("Selection on progress bar should be positve integers. I found value [" + idx
                        + "] to be inacceptable!");
            }
            getDisplay().syncExec(new Runnable() {
                @Override
                public void run() {
                    if (ProgressBarComposite.this.progressBar == null) {
                        return;
                    }
                    if (ProgressBarComposite.this.progressBar.isDisposed()) {
                        return;
                    }
                    if (idx < ProgressBarComposite.this.max - 1) {
                        ProgressBarComposite.this.progressBar.setSelection(idx);
                        return;
                    }
                    ProgressBarComposite.this.progressBar.setSelection(ProgressBarComposite.this.progressBar.getMaximum());
                    Display.getCurrent().readAndDispatch();
                }
            });
        } catch (Exception exc) {
            logger.fatal(exc, exc);
            SWTeXtension.displayMessageE("A intervenit o eroare la afisarea progresului operatiei solicitate.", exc);
        }
    }

    public final void setMax(final int max) {
        if ((this.progressBar != null) && !this.progressBar.isDisposed()) {
            this.max = max;
            this.progressBar.setMaximum(this.max);
            this.progressBar.setSelection(0);
        }
    }

    public final int getSelection() {
        if ((this.progressBar != null) && !this.progressBar.isDisposed()) {
            return this.progressBar.getSelection();
        }
        return 0;
    }

    public final void advance() {
        advance(getSelection() + 1);
    }

    public final int getMax() {
        return this.max;
    }
}
