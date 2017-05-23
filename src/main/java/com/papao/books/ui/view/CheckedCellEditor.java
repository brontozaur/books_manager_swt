package com.papao.books.ui.view;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

public class CheckedCellEditor extends EditingSupport {

    private TableViewer viewer;

    public CheckedCellEditor(TableViewer viewer) {
        super(viewer);
        this.viewer = viewer;
    }

    @Override
    protected CellEditor getCellEditor(final Object paramObject) {
        return new CheckboxCellEditor(this.viewer.getTable(), SWT.CHECK
                | SWT.READ_ONLY);
    }

    @Override
    protected boolean canEdit(final Object element) {
        return element instanceof TableRow;
    }

    @Override
    protected Object getValue(final Object paramObject) {
        TableRow obj = (TableRow) paramObject;
        return obj.isChecked();
    }

    @Override
    protected void setValue(final Object element, final Object value) {
        TableRow obj = (TableRow) element;
        obj.setChecked((value instanceof Boolean) && (Boolean) value);
        this.viewer.refresh();
    }

}
