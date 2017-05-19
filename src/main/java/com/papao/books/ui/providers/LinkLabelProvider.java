package com.papao.books.ui.providers;

import com.papao.books.controller.SettingsController;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;

import java.util.ArrayList;
import java.util.Arrays;

import static com.papao.books.config.BooleanSetting.PERSPECTIVE_AUTHOR_LINKS;

public class LinkLabelProvider extends StyledCellLabelProvider {

    private final CellLabelProvider cellLabelProvider;
    private int columnIndex = -1;
    private final LinkOpener click;
    private int charWidth;

    public LinkLabelProvider(CellLabelProvider cellLabelProvider, LinkOpener onClick) {
        this.cellLabelProvider = cellLabelProvider;
        click = onClick;
    }

    public CellLabelProvider getLabelProvider() {
        return this.cellLabelProvider;
    }

    @Override
    public void initialize(ColumnViewer viewer, ViewerColumn column) {
        super.initialize(viewer, column);
        Listener mouseListener = new Listener(viewer);
        viewer.getControl().addMouseListener(mouseListener);
    }

    @Override
    protected void paint(Event event, Object element) {
        super.paint(event, element);
        charWidth = event.gc.getFontMetrics().getAverageCharWidth();
    }

    @Override
    public void update(ViewerCell cell) {
        cellLabelProvider.update(cell);
        columnIndex = cell.getColumnIndex();
        java.util.List<StyleRange> allRanges = new ArrayList<>();
        if (SettingsController.getBoolean(PERSPECTIVE_AUTHOR_LINKS)) {
            StyleRange styleRange = new StyleRange();
            styleRange.foreground = cell.getItem().getDisplay().getSystemColor(SWT.COLOR_BLUE);
            styleRange.underline = true;
            styleRange.start = 0;
            styleRange.length = cell.getText().length();
            allRanges.add(styleRange);
            if (cell.getStyleRanges() != null) {
                allRanges.addAll(Arrays.asList(cell.getStyleRanges()));
            }
            cell.setStyleRanges(allRanges.toArray(new StyleRange[allRanges.size()]));
        }
    }

    private final class Listener extends MouseAdapter {
        private final ColumnViewer column;

        public Listener(ColumnViewer viewer) {
            column = viewer;
        }

        @Override
        public void mouseDown(MouseEvent e) {
            Point point = new Point(e.x, e.y);
            ViewerCell cell = column.getCell(point);
            if (cell != null && cell.getColumnIndex() == columnIndex) {
                Rectangle rect = cell.getTextBounds();
                rect.width = cell.getText().length() * charWidth;
                if (rect.contains(point)) {
                    if (SettingsController.getBoolean(PERSPECTIVE_AUTHOR_LINKS)) {
                        click.openLink(cell.getElement());
                    }
                }
            }
        }
    }
}

