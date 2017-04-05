package com.papao.books.view.custom;

import com.papao.books.view.util.BorgDateUtil;
import com.papao.books.view.util.FontUtil;
import com.papao.books.view.view.SWTeXtension;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

import java.sql.Time;

public class ProgressBarComposite extends Composite {

	private static Logger logger = Logger.getLogger(ProgressBarComposite.class);

    private ProgressBar progressBar;
    private int max = 1;

    /**
     * variabila care retine idx in timp la care s-a inceput rularea clasei
     */
    private long time0;
    private Label labelDurata;
    public final static String DURATA_ZERO = "00:00/00:00";
    private final int pBarStyle;
    private final Color pBarColor;

    private final StringBuilder durataAfisata = new StringBuilder(ProgressBarComposite.DURATA_ZERO);

    public ProgressBarComposite(final Composite parent, final int pBarStyle) {
        this(parent, 1, pBarStyle);
    }

    public ProgressBarComposite(final Composite parent, final int max, final int pBarStyle) {
        this(parent, max, null, pBarStyle);
    }

    public ProgressBarComposite(final Composite parent, final int max, final Color pBarColor, final int pBarStyle) {
        super(parent, SWT.NONE);
        this.pBarStyle = pBarStyle;
        this.max = max;
        if (this.max <= 0) {
            this.max = 1;
        }
        this.pBarColor = pBarColor;

        if ((parent == null) || parent.isDisposed()) {
            return;
        }

        this.time0 = System.currentTimeMillis();

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
        if ((this.pBarColor != null) && !this.pBarColor.isDisposed()) {
            this.progressBar.setForeground(this.pBarColor);
        }
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(this.progressBar);

        if ((this.pBarStyle & SWT.INDETERMINATE) != SWT.INDETERMINATE) {
            addPaintListener();
            this.labelDurata = new Label(this, SWT.NONE);
            this.labelDurata.setText(ProgressBarComposite.DURATA_ZERO);
            GridDataFactory.fillDefaults().grab(false, false).hint(65, SWT.DEFAULT).minSize(65, SWT.DEFAULT).align(SWT.RIGHT,
                    SWT.CENTER).applyTo(this.labelDurata);
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
                e.gc.setFont(FontUtil.TAHOMA10_BOLD);
                e.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

                int stringHeight = e.gc.getFontMetrics().getHeight();

                if (selection == ProgressBarComposite.this.max - 1) {
                    string = "COMPLET";
                }
                int xCoord = point.x / 2 - 10;
                if (xCoord <= 0) {
                    xCoord = point.x / 2;
                }
                e.gc.drawString(string, xCoord, (point.y - stringHeight) / 2, true);
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
            Time trecut = new Time(System.currentTimeMillis() - this.time0);
            long minute = BorgDateUtil.getMinute(trecut.getTime());
            long secunde = BorgDateUtil.getSecunde(trecut.getTime());
            /**
             * calculam secundele totale
             */
            long totalSec = (this.max * (secunde + minute * 60)) / (idx > 0 ? idx : 1);
            long totMin = totalSec / 60;
            long totSec = (totalSec % 360) % 60;
            this.durataAfisata.setLength(0);
            if (minute < 10) {
                this.durataAfisata.append(0);
            }
            this.durataAfisata.append(minute);
            this.durataAfisata.append(':');
            if (secunde < 10) {
                this.durataAfisata.append(0);
            }
            this.durataAfisata.append(secunde);
            this.durataAfisata.append('/');
            if (totMin < 10) {
                this.durataAfisata.append(0);
            }
            this.durataAfisata.append(totMin);
            this.durataAfisata.append(':');
            if (totSec < 10) {
                this.durataAfisata.append(0);
            }
            this.durataAfisata.append(totSec);

            /**
             * afisare rezultate
             */

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
                        ProgressBarComposite.this.labelDurata.setText(ProgressBarComposite.this.durataAfisata.toString());
                        return;
                    }
                    ProgressBarComposite.this.progressBar.setSelection(ProgressBarComposite.this.progressBar.getMaximum());
                    ProgressBarComposite.this.labelDurata.setText(ProgressBarComposite.this.durataAfisata.toString());
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
        if ((this.labelDurata != null) && !this.labelDurata.isDisposed()) {
            this.labelDurata.setText(ProgressBarComposite.DURATA_ZERO);
        }
        this.time0 = System.currentTimeMillis();
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
