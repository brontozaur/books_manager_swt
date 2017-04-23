package com.papao.books.view.custom;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.util.ArrayList;
import java.util.List;

public class LinkedInSimpleValuesComposite extends Composite {

    private List<String> values = new ArrayList<>();

    public LinkedInSimpleValuesComposite(Composite parent) {
        super(parent, SWT.NONE);

        GridDataFactory.fillDefaults().grab(true, false).hint(100, SWT.DEFAULT).applyTo(this);
        RowLayoutFactory.fillDefaults().extendedMargins(3, 5, 2, 2).spacing(1).margins(0, 0).pack(true).wrap(true).applyTo(this);
    }

    private void populateFields() {
        for (String valoareInitiala : values) {
            new SimpleCanvas(this, valoareInitiala);
        }
        if (values.size() > 0) {
            layoutEverything();
        }
    }

    private void layoutEverything() {
        this.setSize(computeSize(SWT.DEFAULT, SWT.DEFAULT));
        this.layout();
        getParent().layout();
    }

    public void setValues(List values) {
        this.values.clear();
        for (Control children: getChildren()) {
            children.dispose();
        }
        if (values != null) {
            for (Object value : values) {
                this.values.add(String.valueOf(value));
            }
        }
        populateFields();
    }
}