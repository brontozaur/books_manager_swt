package com.papao.books.ui.view;

import org.eclipse.swt.SWT;

public final class TableRow {

    private String colName;
    private boolean checked;
    private int dim;
    private int align;
    private int order;

    public final static String[] ALIGNS = new String[]{
            "Stânga", "Centru", "Dreapta"};

    public TableRow(final String colName,
                    final boolean checked,
                    final int dim,
                    final int align,
                    final int order) {
        setColName(colName);
        setChecked(checked);
        setDim(dim);
        setAlign(align);
        setOrder(order);
    }

    public final String getAlignStr() {
        if (this.align == SWT.LEFT) {
            return ALIGNS[0];
        } else if (this.align == SWT.CENTER) {
            return ALIGNS[1];
        } else {
            return ALIGNS[2];
        }
    }

    public String getColName() {
        return this.colName;
    }

    public void setColName(final String colName) {
        this.colName = colName;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void setChecked(final boolean checked) {
        this.checked = checked;
    }

    public int getDim() {
        return this.dim;
    }

    public void setDim(final int dim) {
        this.dim = dim;
    }

    public int getAlign() {
        return this.align;
    }

    public void setAlign(final int align) {
        this.align = align;
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(final int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "Nume : " + getColName() + ", dim : " + getDim() + ", order " + getOrder()
                + ", alignment " + getAlignStr();
    }
}
