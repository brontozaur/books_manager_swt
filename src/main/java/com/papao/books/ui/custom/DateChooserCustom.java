package com.papao.books.ui.custom;

import com.papao.books.config.StringSetting;
import com.papao.books.controller.SettingsController;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.auth.EncodeLive;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.widgets.datechooser.DateChooser;
import org.eclipse.nebula.widgets.datechooser.DateChooserTheme;
import org.eclipse.nebula.widgets.formattedtext.DateFormatter;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;

import java.util.Date;
import java.util.Locale;

public class DateChooserCustom {

    private static final int SIZE = 16;

    private Composite content;
    Composite selectorComposite;
    private FormattedText formattedText;
    private DateChooserShell popupContent;
    private boolean footerVisible = false;
    private int gridVisible = DateChooser.GRID_FULL;
    private boolean weeksVisible = false;
    private DateChooserTheme theme = DateChooserTheme.GRAY;
    private Locale locale;

    public DateChooserCustom(Composite parent) {
        this(parent, true);
    }

    public DateChooserCustom(Composite parent, boolean addTodaySelector) {
        content = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(false, false).align(SWT.BEGINNING, SWT.CENTER).applyTo(content);
        GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(addTodaySelector ? 3 : 2).margins(0, 0).extendedMargins(0, 0, 0, 0).spacing(3, 0).applyTo(content);

        this.formattedText = new FormattedText(content, SWT.BORDER);
        this.formattedText.setFormatter(new DateFormatter(
                SettingsController.getString(StringSetting.APP_DATE_FORMAT),
                EncodeLive.ROMANIAN_LOCALE));
        GC gc = new GC(this.formattedText.getControl());
        int width = gc.textExtent("01/01/2000  ").x;
        gc.dispose();
        GridDataFactory.fillDefaults().hint(width, SWT.DEFAULT).applyTo(formattedText.getControl());

        selectorComposite = new Composite(content, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).hint(SIZE, SIZE).minSize(SIZE,
                SIZE).applyTo(selectorComposite);
        GridLayoutFactory.fillDefaults().margins(0, 0).extendedMargins(0, 0, 0, 0).spacing(0, 0).applyTo(selectorComposite);
        selectorComposite.setBackgroundImage(AppImages.getImage16(AppImages.IMG_CALENDAR));

        selectorComposite.addListener(SWT.MouseDown, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (popupContent != null && !popupContent.getShell().isDisposed() && popupContent.getShell().isVisible()) {
                    popupContent.getShell().close();
                }
                createPopupShell();
                popupContent.open();
            }
        });
        createPopupShell();
        content.setSize(content.computeSize(SWT.DEFAULT, 14));

        if (addTodaySelector) {
            ToolItem itemAzi = new ToolItem(new ToolBar(content, SWT.FLAT), SWT.FLAT);
            itemAzi.setImage(AppImages.getImage16(AppImages.IMG_SELECT));
            itemAzi.setHotImage(AppImages.getImage16Focus(AppImages.IMG_SELECT));
            itemAzi.setToolTipText("Selectează data de azi");
            itemAzi.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    formattedText.setValue(new Date());
                }
            });
        }

        this.formattedText.getControl().addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                popupContent.setSelectedDate((Date) formattedText.getValue());
            }
        });
    }

    private void createPopupShell() {
        popupContent = new DateChooserShell(content.getShell());
        popupContent.setSelectedDate((Date) formattedText.getValue());
        popupContent.footerVisible = footerVisible;
        popupContent.gridVisible = gridVisible;
        popupContent.locale = locale;
        popupContent.theme = theme;
        popupContent.weeksVisible = weeksVisible;
        Rectangle rect = formattedText.getControl().getBounds();
        Point pt = new Point(rect.x, rect.y + rect.height);
        pt = content.toDisplay(pt);
        popupContent.getShell().setLocation(pt.x, pt.y);
        popupContent.getShell().addListener(SWT.Close, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (popupContent.getSelectedDate() != null) {
                    formattedText.setValue(popupContent.getSelectedDate());
                }
            }
        });
    }

    public void setFooterVisible(boolean footerVisible) {
        this.footerVisible = footerVisible;
    }

    public void setGridVisible(int gridVisible) {
        this.gridVisible = gridVisible;
    }

    public void setWeeksVisible(boolean weeksVisible) {
        this.weeksVisible = weeksVisible;
    }

    public void setTheme(DateChooserTheme theme) {
        this.theme = theme;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setValue(Date value) {
        this.formattedText.setValue(value);
    }

    public void setEnabled(boolean enabled) {
        formattedText.getControl().setEnabled(enabled);
        selectorComposite.setEnabled(enabled);
    }

    public Text getFormattedText() {
        return this.formattedText.getControl();
    }

    public Date getValue() {
        return (Date) formattedText.getValue();
    }
}
