package com.papao.books.ui.custom;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class CarteTitluVolumComposite extends Composite {

    private Text textTitlu;
    private Text textVolum;

    public CarteTitluVolumComposite(Composite parent, String titlu, String volum) {
        super(parent, SWT.NONE);

        GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).spacing(0, 0).applyTo(this);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this);

        this.textTitlu = new Text(this, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).hint(100, SWT.DEFAULT).applyTo(this.textTitlu);

        new Label(this, SWT.NONE).setText("#");

        this.textVolum = new Text(this, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(false, false).hint(20, SWT.DEFAULT).applyTo(this.textVolum);

        this.textTitlu.setText(titlu);
        this.textVolum.setText(volum);
    }

    @Override
    public boolean setFocus() {
        return textTitlu.setFocus();
    }

    public Text getTextTitlu() {
        return textTitlu;
    }

    public String getTitlu() {
        return textTitlu.getText().trim();
    }

    public String getVolum() {
        return textVolum.getText().trim();
    }
}
