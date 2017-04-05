package com.papao.books.view.util.sorter;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TreeColumn;

public abstract class AbstractTreeColumnViewerSorter extends AbstractColumnViewerSorter {
    private TreeColumn column;

    public AbstractTreeColumnViewerSorter(final ColumnViewer viewer, final TreeViewerColumn column) {
        super(viewer);
        this.column = column.getColumn();
        this.column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (getViewer().getComparator() != null) {
                    if (getViewer().getComparator() == AbstractTreeColumnViewerSorter.this) {
                        int tdirection = AbstractTreeColumnViewerSorter.this.direction;

                        if (tdirection == AbstractColumnViewerSorter.ASC) {
                            setSorter(AbstractTreeColumnViewerSorter.this, AbstractColumnViewerSorter.DESC);
                        } else if (tdirection == AbstractColumnViewerSorter.DESC) {
                            setSorter(AbstractTreeColumnViewerSorter.this, AbstractColumnViewerSorter.NONE);
                        }
                    } else {
                        setSorter(AbstractTreeColumnViewerSorter.this, AbstractColumnViewerSorter.ASC);
                    }
                } else {
                    setSorter(AbstractTreeColumnViewerSorter.this, AbstractColumnViewerSorter.ASC);
                }
                ISelection selection = viewer.getSelection();
                if ((viewer instanceof TreeViewer) && (selection != null)) {
                    ((TreeViewer) viewer).collapseAll();
                    viewer.setSelection(selection, true);
                }
            }
        });
    }

    public AbstractTreeColumnViewerSorter(final ColumnViewer viewer, final TreeColumn column) {
        super(viewer);
        this.column = column;
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (getViewer().getComparator() != null) {
                    if (getViewer().getComparator() == AbstractTreeColumnViewerSorter.this) {
                        int tdirection = AbstractTreeColumnViewerSorter.this.direction;

                        if (tdirection == AbstractColumnViewerSorter.ASC) {
                            setSorter(AbstractTreeColumnViewerSorter.this, AbstractColumnViewerSorter.DESC);
                        } else if (tdirection == AbstractColumnViewerSorter.DESC) {
                            setSorter(AbstractTreeColumnViewerSorter.this, AbstractColumnViewerSorter.NONE);
                        }
                    } else {
                        setSorter(AbstractTreeColumnViewerSorter.this, AbstractColumnViewerSorter.ASC);
                    }
                } else {
                    setSorter(AbstractTreeColumnViewerSorter.this, AbstractColumnViewerSorter.ASC);
                }
            }
        });
    }

    public void setSorter(final AbstractTreeColumnViewerSorter sorter, final int direction) {
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