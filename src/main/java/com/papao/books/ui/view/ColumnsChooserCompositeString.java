package com.papao.books.ui.view;

import com.papao.books.ui.AppImages;
import com.papao.books.ui.interfaces.IReset;
import com.papao.books.ui.providers.AdbStringContentProvider;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

import java.util.Arrays;

public class ColumnsChooserCompositeString extends Composite implements Listener, IReset {

    private static Logger logger = Logger.getLogger(ColumnsChooserCompositeString.class);

    private TableViewer viewer;
    private final static int IDX_NUME = 0;
    private final static int IDX_DIM = 1;
    private final static int IDX_ALIGN = 2;
    private final static int IDX_SORT = 3;
    private final static String[] COLS_NO_SORT = new String[]{
            "Coloana", "Dimensiune", "Aliniere"};
    private final static String[] COLS_WITH_SORT = new String[]{
            "Coloana", "Dimensiune", "Aliniere", "Sort?"};

    private ToolItem toolSelectAll;
    private ToolItem toolDeSelectAll;
    private ToolItem itemUp;
    private ToolItem itemDown;

    private int[] gridDims;
    private int[] gridAligns;
    private int[] gridOrder;
    private boolean[] gridVisibility;
    private boolean[] sort;
    private boolean sortPropertySupport;

    private java.util.List<String> columnNames;

    public ColumnsChooserCompositeString(final Composite parent,
                                         final java.util.List<String> columnNames,
                                         final boolean sortPropertySupport) {
        super(parent, SWT.NONE);
        this.columnNames = columnNames;
        this.sortPropertySupport = sortPropertySupport;
        addComponents();
        populateFields();
    }

    private void addComponents() {

        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(0, 0).applyTo(this);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(this);

        this.viewer = new TableViewer(this, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER
                | SWT.H_SCROLL | SWT.V_SCROLL);
        this.viewer.setContentProvider(new AdbStringContentProvider());
        this.viewer.getTable().setLinesVisible(true);
        this.viewer.getTable().setHeaderVisible(true);
        this.viewer.getTable().addListener(SWT.Selection, this);
        GridDataFactory.fillDefaults().grab(true, true).hint(350, 400).span(1, 2).applyTo(this.viewer.getControl());
        String[] cols = this.sortPropertySupport ? COLS_WITH_SORT : COLS_NO_SORT;
        for (int i = 0; i < cols.length; i++) {
            final TableViewerColumn tblCol = new TableViewerColumn(this.viewer, SWT.NONE);
            tblCol.getColumn().setText(cols[i]);
            if (i == 0) {
                tblCol.getColumn().setWidth(150);
            } else {
                tblCol.getColumn().setWidth(100);
            }
            tblCol.getColumn().setAlignment(SWT.LEFT);
            tblCol.getColumn().setResizable(true);
            tblCol.getColumn().setMoveable(true);
            switch (i) {
                case IDX_NUME: {
                    tblCol.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            TableRow obj = (TableRow) element;
                            return obj.getColName();
                        }

                        @Override
                        public Color getForeground(final Object element) {
                            TableRow obj = (TableRow) element;
                            if (obj.isChecked()) {
                                return null;
                            }
                            return Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);
                        }

                        @Override
                        public Image getImage(final Object element) {
                            TableRow obj = (TableRow) element;
                            if (obj.isChecked()) {
                                return AppImages.getImage16(AppImages.IMG_SELECT);
                            }
                            return AppImages.getImage16(AppImages.IMG_DESELECT);
                        }
                    });
                    tblCol.setEditingSupport(new CheckedCellEditor(viewer));
                    break;
                }
                case IDX_DIM: {
                    tblCol.setLabelProvider(new ColumnLabelProvider() {

                        @Override
                        public String getText(final Object element) {
                            TableRow obj = (TableRow) element;
                            return obj.getDim() + "";
                        }

                        @Override
                        public Color getForeground(final Object element) {
                            TableRow obj = (TableRow) element;
                            if (obj.isChecked()) {
                                return null;
                            }
                            return Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);
                        }

                    });
                    tblCol.setEditingSupport(new IntegerEditingSupport(viewer));
                    break;
                }
                case IDX_ALIGN: {
                    tblCol.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            TableRow obj = (TableRow) element;
                            return obj.getAlignStr();
                        }

                        @Override
                        public Color getForeground(final Object element) {
                            TableRow obj = (TableRow) element;
                            if (obj.isChecked()) {
                                return null;
                            }
                            return Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);
                        }
                    });
                    tblCol.setEditingSupport(new ComboCellEditor(viewer));
                    break;
                }
                case IDX_SORT: {
                    tblCol.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            TableRow obj = (TableRow) element;
                            return obj.isSort() ? "DA" : "NU";
                        }

                        @Override
                        public Color getForeground(final Object element) {
                            TableRow obj = (TableRow) element;
                            if (obj.isSort()) {
                                return null;
                            }
                            return Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE);
                        }

                        @Override
                        public Image getImage(final Object element) {
                            TableRow obj = (TableRow) element;
                            if (obj.isSort()) {
                                return AppImages.getImage16(AppImages.IMG_SELECT);
                            }
                            return AppImages.getImage16(AppImages.IMG_DESELECT);
                        }
                    });
                    tblCol.setEditingSupport(new SortedCellEditor(viewer));
                    break;
                }
                default:
            }
        }

        final ToolBar barOrder = new ToolBar(this, SWT.FLAT);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.BEGINNING).applyTo(barOrder);

        this.itemUp = new ToolItem(barOrder, SWT.NONE);
        this.itemUp.setImage(AppImages.getImage24(AppImages.IMG_ARROW_UP));
        this.itemUp.setHotImage(AppImages.getImage24Focus(AppImages.IMG_ARROW_UP));
        this.itemUp.setText("Spre început");
        this.itemUp.addListener(SWT.Selection, this);
        this.itemUp.setEnabled(false);

        final ToolBar barOrder2 = new ToolBar(this, SWT.FLAT);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.BEGINNING).applyTo(barOrder);

        this.itemDown = new ToolItem(barOrder2, SWT.NONE);
        this.itemDown.setImage(AppImages.getImage24(AppImages.IMG_ARROW_DOWN));
        this.itemDown.setHotImage(AppImages.getImage24Focus(AppImages.IMG_ARROW_DOWN));
        this.itemDown.setText("Spre sfârșit");
        this.itemDown.addListener(SWT.Selection, this);
        this.itemDown.setEnabled(false);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.BEGINNING).applyTo(barOrder2);

        final ToolBar bar = new ToolBar(this, SWT.FLAT);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.END).span(2, 1).applyTo(bar);
        this.toolSelectAll = new ToolItem(bar, SWT.NONE);
        this.toolSelectAll.setImage(AppImages.getImage24(AppImages.IMG_SELECT_ALL));
        this.toolSelectAll.setHotImage(AppImages.getImage24Focus(AppImages.IMG_SELECT_ALL));
        this.toolSelectAll.setText("Toate");
        this.toolSelectAll.addListener(SWT.Selection, this);

        this.toolDeSelectAll = new ToolItem(bar, SWT.NONE);
        this.toolDeSelectAll.setImage(AppImages.getImage24(AppImages.IMG_DESELECT_ALL));
        this.toolDeSelectAll.setHotImage(AppImages.getImage24Focus(AppImages.IMG_DESELECT_ALL));
        this.toolDeSelectAll.setText("Nici una");
        this.toolDeSelectAll.addListener(SWT.Selection, this);

        SWTeXtension.processToolBarItems(bar);
    }

    private TableRow[] getInputFromTable() {
        TableRow[] input;
        final int length = columnNames.size();
        input = new TableRow[length];
        int[] order = new int[length];
        for (int i = 0; i < length; i++) {
            order[i] = i;
        }
        int[] dims = new int[length];
        Arrays.fill(dims, 100);
        boolean[] visibleCols = new boolean[length];
        Arrays.fill(visibleCols, true);
        int[] aligns = new int[length];
        Arrays.fill(aligns, SWT.LEFT);
        boolean[] sort = new boolean[length];
        Arrays.fill(sort, false);
        for (int i = 0; i < length; i++) {
            final String colName = columnNames.get(i);
            TableRow row = new TableRow(colName, visibleCols[i], dims[i], aligns[i], order[i], sort[i]);
            input[i] = row;
        }
        return input;
    }

    private void populateFields() {
        this.viewer.setInput(getInputFromTable());
    }

    public boolean validate() {
        try {
            TableRow[] elements = (TableRow[]) this.viewer.getInput();
            boolean hasSelection = false;
            for (TableRow row : elements) {
                if (row.isChecked()) {
                    hasSelection = true;
                }
                if ((row.getDim() == 0) && row.isChecked()) {
                    SWTeXtension.displayMessageW("Dimensiunea unei coloane selectate este zero. "
                            + "Prin urmare, această coloană nu poate fi afișată.");
                    return false;
                }
                if ((row.getAlign() != SWT.LEFT) && (row.getAlign() != SWT.RIGHT)
                        && (row.getAlign() != SWT.CENTER)) {
                    SWTeXtension.displayMessageW("Aliniere incorectă pentru una din coloanele tabelei!");
                    return false;
                }
            }
            if (!hasSelection) {
                SWTeXtension.displayMessageW("Nu ați selectat nici o coloană!");
                return false;
            }
        } catch (Exception exc) {
            SWTeXtension.displayMessageEGeneric(exc);
            return false;
        }
        return true;
    }

    public boolean save() {
        try {
            TableRow[] elements = (TableRow[]) this.viewer.getInput();
            this.gridDims = new int[elements.length];
            this.gridAligns = new int[elements.length];
            this.gridOrder = new int[elements.length];
            this.gridVisibility = new boolean[elements.length];
            this.sort = new boolean[elements.length];
            for (int i = 0; i < elements.length; i++) {
                TableRow row = elements[i];
                this.gridDims[row.getOrder()] = row.getDim();
                this.gridAligns[row.getOrder()] = row.getAlign();
                this.gridOrder[i] = row.getOrder();
                this.gridVisibility[row.getOrder()] = row.isChecked();
                this.sort[row.getOrder()] = row.isSort();
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            SWTeXtension.displayMessageE("A intervenit o eroare.", exc);
            return false;
        }
        return true;
    }

    public final int[] getDims() {
        return this.gridDims;
    }

    public final int[] getAligns() {
        return this.gridAligns;
    }

    public final int[] getOrder() {
        return this.gridOrder;
    }

    public final boolean[] getSelection() {
        return this.gridVisibility;
    }

    public final boolean[] getSort() {
        return this.sort;
    }

    public final java.util.List<String> getColumnNames() {
        return this.columnNames;
    }

    private void selectAll(final boolean select) {
        if ((this.viewer == null) || this.viewer.getControl().isDisposed()
                || (this.viewer.getInput() == null)) {
            return;
        }
        TableRow[] input = (TableRow[]) this.viewer.getInput();
        for (int i = 0; i < input.length; i++) {
            TableRow row = input[i];
            row.setChecked(select);
            input[i] = row;
        }
        this.viewer.refresh();
    }

    private void order(final int direction) {
        if (this.viewer.getControl().isDisposed() || (this.viewer.getInput() == null)
                || (this.viewer.getTable().getSelectionCount() == 0)) {
            return;
        }
        TableRow[] input = (TableRow[]) this.viewer.getInput();
        if ((this.viewer.getTable().getSelectionIndex() == 0) && (direction == SWT.UP)) {
            SWTeXtension.displayMessageW("Coloana selectată va fi afișată pe prima poziție.");
            return;
        }
        if ((this.viewer.getTable().getSelectionIndex() == (input.length - 1))
                && (direction == SWT.DOWN)) {
            SWTeXtension.displayMessageW("Coloana selectată va fi afișată pe prima poziție.");
            return;
        }
        final int selectedIndex = this.viewer.getTable().getSelectionIndex();
        TableRow row = input[selectedIndex];
        final int swappedIndex = direction == SWT.UP ? selectedIndex - 1 : selectedIndex + 1;
        TableRow swappedRow = input[swappedIndex];
        input[selectedIndex] = swappedRow;
        input[swappedIndex] = row;
        this.viewer.refresh();
    }

    @Override
    public final void handleEvent(final Event e) {
        if (e.type == SWT.Selection) {
            if (e.widget == this.toolSelectAll) {
                selectAll(true);
            } else if (e.widget == this.toolDeSelectAll) {
                selectAll(false);
            } else if (e.widget == this.itemUp) {
                order(SWT.UP);
            } else if (e.widget == this.itemDown) {
                order(SWT.DOWN);
            }
        }
        this.itemUp.setEnabled(this.viewer.getTable().getSelectionCount() > 0);
        this.itemDown.setEnabled(this.viewer.getTable().getSelectionCount() > 0);
    }

    @Override
    public void reset() {
        TableRow[] input = (TableRow[]) this.viewer.getInput();
        TableRow[] inputNou = new TableRow[input.length];
        for (TableRow element : input) {
            element.setAlign(SWT.LEFT);
            element.setChecked(true);
            element.setDim(100);
            element.setOrder(getColumnIndex(element.getColName()));
            element.setSort(false);
            inputNou[element.getOrder()] = element;
        }
        this.viewer.setInput(inputNou);
        this.itemUp.setEnabled(this.viewer.getTable().getSelectionCount() > 0);
        this.itemDown.setEnabled(this.viewer.getTable().getSelectionCount() > 0);
    }

    private int getColumnIndex(final String columnName) {
        return columnNames.indexOf(columnName);
    }

}
