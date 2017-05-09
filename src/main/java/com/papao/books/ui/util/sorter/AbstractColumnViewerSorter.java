package com.papao.books.ui.util.sorter;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public abstract class AbstractColumnViewerSorter extends ViewerComparator {

    public static final int ASC = 1;

    public static final int NONE = 0;

    public static final int DESC = -1;

    protected int direction = 0;

    private final ColumnViewer viewer;

    public AbstractColumnViewerSorter(final ColumnViewer viewer) {
        this.viewer = viewer;
    }

    protected final ColumnViewer getViewer() {
        return this.viewer;
    }

    @Override
    public int compare(final Viewer v, final Object e1, final Object e2) {
        return this.direction * doCompare(v, e1, e2);
    }

    protected abstract int doCompare(final Viewer v, final Object e1, final Object e2);
}
