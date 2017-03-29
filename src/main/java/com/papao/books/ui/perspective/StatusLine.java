package com.papao.books.ui.perspective;

import com.papao.books.auth.EncodeLive;
import com.papao.books.util.ColorUtil;
import com.papao.books.util.FontUtil;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class StatusLine extends Composite {

    private CLabel labelNumeModul;
    private CLabel labelDataLogare;
    private CLabel labelNumeUtilizator;
    private CLabel labelComponentDescription;

    public StatusLine(final Composite parent) {
        super(parent, SWT.NONE);

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.END).grab(true, false).span(((GridLayout) parent.getLayout()).numColumns,
                1).applyTo(this);
        GridLayoutFactory.fillDefaults().numColumns(6).equalWidth(false).extendedMargins(3, 3, 0, 0).spacing(5, 0).applyTo(this);

        addContents();
    }

    private void addContents() {
        Label separator;
        separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).span(6, 1).applyTo(separator);

        setLabelNumeModul(new CLabel(this, SWT.BORDER));
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).hint(230, SWT.DEFAULT).applyTo(getLabelNumeModul());
        getLabelNumeModul().setToolTipText("Modulul curent");

        setLabelComponentDescription(new CLabel(this, SWT.NONE));
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(getLabelComponentDescription());
        getLabelComponentDescription().setForeground(ColorUtil.COLOR_ALBASTRU_INCHIS_ATOM);
        getLabelComponentDescription().setFont(FontUtil.TAHOMA12_BOLD);
        getLabelComponentDescription().setAlignment(SWT.CENTER);
        getLabelComponentDescription().setToolTipText("Locatia curenta din aplicatie");

        setLabelNumeUtilizator(new CLabel(this, SWT.BORDER));
        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(false, false).hint(150, SWT.DEFAULT).applyTo(getLabelNumeUtilizator());
        getLabelNumeUtilizator().setText(EncodeLive.getCurrentUserName());
        getLabelNumeUtilizator().setToolTipText("Utilizatorul logat in aplicatie");

        setLabelDataLogare(new CLabel(this, SWT.BORDER));
        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(false, false).hint(100, SWT.DEFAULT).applyTo(getLabelDataLogare());
        getLabelDataLogare().setAlignment(SWT.CENTER);
        getLabelDataLogare().setText(EncodeLive.getSQLDateLogin().toString());
        getLabelDataLogare().setToolTipText("Data logarii in sistem");

    }

    public final CLabel getLabelNumeModul() {
        return this.labelNumeModul;
    }

    public final void setLabelNumeModul(final CLabel labelNumeModul) {
        this.labelNumeModul = labelNumeModul;
    }

    public final CLabel getLabelDataLogare() {
        return this.labelDataLogare;
    }

    public final void setLabelDataLogare(final CLabel labelDataLogare) {
        this.labelDataLogare = labelDataLogare;
    }

    public final CLabel getLabelNumeUtilizator() {
        return this.labelNumeUtilizator;
    }

    public final void setLabelNumeUtilizator(final CLabel labelNumeUtilizator) {
        this.labelNumeUtilizator = labelNumeUtilizator;
    }

    public final CLabel getLabelComponentDescription() {
        return this.labelComponentDescription;
    }

    public final void setLabelComponentDescription(final CLabel labelComponentDescription) {
        this.labelComponentDescription = labelComponentDescription;
    }

}
