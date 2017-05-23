package com.papao.books.ui.view;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

public class ComboCellEditor extends EditingSupport {

    private TableViewer viewer;

    public ComboCellEditor(TableViewer viewer) {
        super(viewer);
        this.viewer = viewer;
    }

    @Override
    protected CellEditor getCellEditor(final Object paramObject) {
        return new ComboBoxCellEditor(
                this.viewer.getTable(),
                TableRow.ALIGNS,
                SWT.READ_ONLY);
    }

    @Override
    protected boolean canEdit(final Object element) {
        TableRow obj = (TableRow) element;
        return obj.isChecked();
    }

    @Override
    protected Object getValue(final Object paramObject) {
        TableRow obj = (TableRow) paramObject;
        return obj.getAlign();
    }

    @Override
    protected void setValue(final Object element, final Object value) {
        TableRow obj = (TableRow) element;
        final Integer selection = (Integer) value;
        if (selection == -1) {
            return;
        }
        if (selection == 0) {
            obj.setAlign(SWT.LEFT);
        } else if (selection == 1) {
            obj.setAlign(SWT.CENTER);
        } else {
            obj.setAlign(SWT.RIGHT);
        }
        this.viewer.refresh();
    }
}