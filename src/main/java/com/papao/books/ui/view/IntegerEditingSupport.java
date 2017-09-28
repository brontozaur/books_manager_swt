package com.papao.books.ui.view;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

public class IntegerEditingSupport extends EditingSupport {

    private TableViewer viewer;

    public IntegerEditingSupport(TableViewer viewer) {
        super(viewer);
        this.viewer = viewer;
    }

    @Override
    protected CellEditor getCellEditor(final Object element) {
        return new TextCellEditor(this.viewer.getTable());
    }

    @Override
    protected boolean canEdit(final Object element) {
        TableRow obj = (TableRow) element;
        return obj.isChecked();
    }

    @Override
    protected Object getValue(final Object element) {
        TableRow obj = (TableRow) element;
        return obj.getDim() + "";
    }

    @Override
    protected void setValue(final Object element, final Object value) {
        TableRow obj = (TableRow) element;
        try {
            final int intValue = Integer.valueOf(value.toString());
            if (intValue <= 0) {
                SWTeXtension.displayMessageW("Introduceți doar numere pozitive!");
                return;
            }
            obj.setDim(intValue);
            this.viewer.refresh();
        } catch (NumberFormatException exc) {
            SWTeXtension.displayMessageW("Introduceți doar numere!");
        }
    }
}
