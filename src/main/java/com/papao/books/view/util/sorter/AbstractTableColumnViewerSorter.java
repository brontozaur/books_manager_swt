package com.papao.books.view.util.sorter;

import com.papao.books.view.interfaces.ITableBone;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TableColumn;

public abstract class AbstractTableColumnViewerSorter extends AbstractColumnViewerSorter {

    private final TableColumn column;

    public AbstractTableColumnViewerSorter(final ITableBone bone, final TableViewerColumn column) {
        this(bone.getViewer(), column, bone);
    }

    public AbstractTableColumnViewerSorter(final ColumnViewer viewer, final TableViewerColumn column) {
        this(viewer, column, null);
    }

    public AbstractTableColumnViewerSorter(final ColumnViewer viewer, final TableViewerColumn column, final Object parentInstance) {
        super(viewer);
        this.column = column.getColumn();
        this.column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (getViewer().getComparator() != null) {
                    if (getViewer().getComparator() == AbstractTableColumnViewerSorter.this) {
                        int tdirection = AbstractTableColumnViewerSorter.this.direction;

                        if (tdirection == AbstractColumnViewerSorter.ASC) {
                            setSorter(AbstractTableColumnViewerSorter.this, AbstractColumnViewerSorter.DESC);
                        } else if (tdirection == AbstractColumnViewerSorter.DESC) {
                            setSorter(AbstractTableColumnViewerSorter.this, AbstractColumnViewerSorter.NONE);
                        }
                    } else {
                        setSorter(AbstractTableColumnViewerSorter.this, AbstractColumnViewerSorter.ASC);
                    }
                } else {
                    setSorter(AbstractTableColumnViewerSorter.this, AbstractColumnViewerSorter.ASC);
                }
            }
        });
    }

    public AbstractTableColumnViewerSorter(final ColumnViewer viewer, final TableColumn column) {
        this(viewer, column, null);
    }

    public AbstractTableColumnViewerSorter(final ColumnViewer viewer, final TableColumn column, final Object parentInstance) {
        super(viewer);
        this.column = column;
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (getViewer().getComparator() != null) {
                    if (getViewer().getComparator() == AbstractTableColumnViewerSorter.this) {
                        int tdirection = AbstractTableColumnViewerSorter.this.direction;

                        if (tdirection == AbstractColumnViewerSorter.ASC) {
                            setSorter(AbstractTableColumnViewerSorter.this, AbstractColumnViewerSorter.DESC);
                        } else if (tdirection == AbstractColumnViewerSorter.DESC) {
                            setSorter(AbstractTableColumnViewerSorter.this, AbstractColumnViewerSorter.NONE);
                        }
                    } else {
                        setSorter(AbstractTableColumnViewerSorter.this, AbstractColumnViewerSorter.ASC);
                    }
                } else {
                    setSorter(AbstractTableColumnViewerSorter.this, AbstractColumnViewerSorter.ASC);
                }
            }
        });
    }

    public void setSorter(final AbstractTableColumnViewerSorter sorter, final int direction) {
        if (direction == AbstractColumnViewerSorter.NONE) {
            this.column.getParent().setSortColumn(null);
            this.column.getParent().setSortDirection(SWT.NONE);
            getViewer().setComparator(null);
        } else {
            this.column.getParent().setSortColumn(this.column);
            sorter.direction = direction;

            if (direction == AbstractColumnViewerSorter.ASC) {
                this.column.getParent().setSortDirection(SWT.DOWN);
            } else {
                this.column.getParent().setSortDirection(SWT.UP);
            }

            if (getViewer().getComparator() == sorter) {
                getViewer().refresh();
            } else {
                getViewer().setComparator(sorter);
            }
        }
    }
}