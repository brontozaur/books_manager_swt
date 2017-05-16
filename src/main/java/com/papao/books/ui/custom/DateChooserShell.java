package com.papao.books.ui.custom;

import com.papao.books.ui.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.nebula.widgets.datechooser.DateChooser;
import org.eclipse.nebula.widgets.datechooser.DateChooserTheme;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import java.util.Date;
import java.util.Locale;

public class DateChooserShell {

    private final static Logger logger = Logger.getLogger(DateChooserShell.class);
    private Shell shell;
    public boolean footerVisible = false;
    public int gridVisible = DateChooser.GRID_NONE;
    public boolean weeksVisible = false;
    public DateChooserTheme theme;
    public Locale locale;
    private DateChooser cal;
    private Date selectedDate;

    public DateChooserShell(Shell parent) {
        shell = new Shell(parent, SWT.NO_TRIM);
        shell.setLayout(new GridLayout());
        shell.addListener(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (event.keyCode == SWT.ESC) {
                    shell.close();
                }
            }
        });
    }

    private void show() {
        try {
            cal = new DateChooser(shell, SWT.NONE);
            cal.setSelectedDate(selectedDate);
            cal.setTheme(this.theme != null ? this.theme : DateChooserTheme.GRAY);
            if (this.locale != null) {
                cal.setLocale(this.locale);
            }

            cal.setGridVisible(this.gridVisible);
            cal.setFooterVisible(this.footerVisible);
            cal.setWeeksVisible(this.weeksVisible);
            cal.setAutoSelectOnFooter(true);
            cal.pack();

            cal.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    selectedDate = cal.getSelectedDate();
                    shell.close();
                }
            });

            this.shell.setSize(this.shell.computeSize(SWT.DEFAULT, SWT.DEFAULT));

            shell.open();
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            SWTeXtension.displayMessageE(exc.getMessage(), exc);
        }
    }

    public Shell getShell() {
        return this.shell;
    }

    public void open() {
        show();
    }

    public Date getSelectedDate() {
        return this.selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }
}
