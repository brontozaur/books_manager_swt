package com.papao.books.ui.view;

import org.eclipse.swt.SWT;

public final class TableRow {

    private String colName;
    private boolean checked;
    private int dim;
    private int align;
    private int order;
    private boolean sort;

    public final static String[] ALIGNS = new String[]{
            "Stânga", "Centru", "Dreapta"};

    public TableRow(final String colName,
                    final boolean checked,
                    final int dim,
                    final int align,
                    final int order,
                    final boolean sort) {
        this.colName = colName;
        this.checked = checked;
        this.dim = dim;
        this.align = align;
        this.order = order;
        this.sort = sort;
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
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public int getAlign() {
        return align;
    }

    public void setAlign(int align) {
        this.align = align;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isSort() {
        return sort;
    }

    public void setSort(boolean sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "Nume : " + getColName() + ", dim : " + getDim() + ", order " + getOrder()
                + ", alignment " + getAlignStr();
    }
}
