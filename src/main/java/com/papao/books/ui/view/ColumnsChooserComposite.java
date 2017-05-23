package com.papao.books.ui.view;

import com.papao.books.controller.SettingsController;
import com.papao.books.model.config.TableSetting;
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

public class ColumnsChooserComposite extends Composite implements Listener, IReset {

    private static Logger logger = Logger.getLogger(ColumnsChooserComposite.class);

    private Table table;
    private Tree tree;
    private final Class<?> clazz;
    private final String tableKey;
    private TableViewer viewer;
    private final static int IDX_NUME = 0;
    private final static int IDX_DIM = 1;
    private final static int IDX_ALIGN = 2;
    private final static String[] COLS = new String[]{
            "Coloana", "Dimensiune", "Aliniere"};

    private ToolItem toolSelectAll;
    private ToolItem toolDeSelectAll;
    private ToolItem itemUp;
    private ToolItem itemDown;

    private int[] gridDims;
    private int[] gridAligns;
    private int[] gridOrder;
    private boolean[] gridVisibility;

    public ColumnsChooserComposite(final Composite parent,
                                   final Table table,
                                   final Class<?> clazz,
                                   final String tableKey) {
        super(parent, SWT.NONE);
        this.table = table;
        this.clazz = clazz;
        this.tableKey = tableKey;
        addComponents();
        populateFields();
    }

    public ColumnsChooserComposite(final Composite parent,
                                   final Tree tree,
                                   final Class<?> clazz,
                                   final String tableKey) {
        super(parent, SWT.NONE);
        this.tree = tree;
        this.clazz = clazz;
        this.tableKey = tableKey;
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
        for (int i = 0; i < ColumnsChooserComposite.COLS.length; i++) {
            final TableViewerColumn tblCol = new TableViewerColumn(this.viewer, SWT.NONE);
            tblCol.getColumn().setText(ColumnsChooserComposite.COLS[i]);
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
                default:
            }
        }

        final ToolBar barOrder = new ToolBar(this, SWT.FLAT);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.BEGINNING).applyTo(barOrder);

        this.itemUp = new ToolItem(barOrder, SWT.NONE);
        this.itemUp.setImage(AppImages.getImage24(AppImages.IMG_ARROW_UP));
        this.itemUp.setHotImage(AppImages.getImage24Focus(AppImages.IMG_ARROW_UP));
        this.itemUp.setText("Spre inceput");
        this.itemUp.addListener(SWT.Selection, this);
        this.itemUp.setEnabled(false);

        final ToolBar barOrder2 = new ToolBar(this, SWT.FLAT);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.BEGINNING).applyTo(barOrder);

        this.itemDown = new ToolItem(barOrder2, SWT.NONE);
        this.itemDown.setImage(AppImages.getImage24(AppImages.IMG_ARROW_DOWN));
        this.itemDown.setHotImage(AppImages.getImage24Focus(AppImages.IMG_ARROW_DOWN));
        this.itemDown.setText("Spre sfarsit");
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
        final int length = this.table == null ? this.tree.getColumnCount()
                : this.table.getColumnCount();
        input = new TableRow[length];
        TableSetting tableSetting = SettingsController.getTableSetting(length, this.clazz, this.tableKey);
        int[] order = tableSetting.getOrder();
        int[] dims = tableSetting.getCorrectDims();
        boolean[] visibleCols = tableSetting.getCorrectVisible();
        int[] aligns = tableSetting.getCorrectAligns();
        for (int i = 0; i < length; i++) {
            final String colName = this.table == null ? this.tree.getColumn(order[i]).getText()
                    : this.table.getColumn(order[i]).getText();
            TableRow row = new TableRow(colName, visibleCols[i], dims[i], aligns[i], order[i], false);
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
                            + "Prin urmare, aceasta coloana nu poate fi afisata.");
                    return false;
                }
                if ((row.getAlign() != SWT.LEFT) && (row.getAlign() != SWT.RIGHT)
                        && (row.getAlign() != SWT.CENTER)) {
                    SWTeXtension.displayMessageW("Aliniere incorecta pentru una din coloanele tabelei!");
                    return false;
                }
            }
            if (!hasSelection) {
                SWTeXtension.displayMessageW("Nu ati selectat nici o coloana!");
                return false;
            }
        } catch (Exception exc) {
            SWTeXtension.displayMessageEGeneric(exc);
            return false;
        }
        return true;
    }

    public boolean save(final boolean makeitPermanent) {
        try {
            TableRow[] elements = (TableRow[]) this.viewer.getInput();
            this.gridDims = new int[elements.length];
            this.gridAligns = new int[elements.length];
            this.gridOrder = new int[elements.length];
            this.gridVisibility = new boolean[elements.length];
            if (this.table != null) {
                for (int i = 0; i < elements.length; i++) {
                    TableRow row = elements[i];
                    if (makeitPermanent) {
                        this.table.getColumn(row.getOrder()).setAlignment(row.getAlign());
                        this.table.getColumn(row.getOrder()).setWidth(row.isChecked() ? row.getDim()
                                : 0);
                        this.table.getColumn(row.getOrder()).setResizable(row.isChecked());
                    }
                    this.gridDims[row.getOrder()] = row.getDim();
                    this.gridAligns[row.getOrder()] = row.getAlign();
                    this.gridOrder[i] = row.getOrder();
                    this.gridVisibility[row.getOrder()] = row.isChecked();
                }
                if (makeitPermanent) {
                    this.table.setColumnOrder(this.gridOrder);
                }
            } else {
                for (int i = 0; i < elements.length; i++) {
                    TableRow row = elements[i];
                    if (makeitPermanent) {
                        this.tree.getColumn(row.getOrder()).setAlignment(row.getAlign());
                        this.tree.getColumn(row.getOrder()).setWidth(row.isChecked() ? row.getDim()
                                : 0);
                        this.tree.getColumn(row.getOrder()).setResizable(row.isChecked());
                    }
                    this.gridDims[row.getOrder()] = row.getDim();
                    this.gridAligns[row.getOrder()] = row.getAlign();
                    this.gridOrder[i] = row.getOrder();
                    this.gridVisibility[row.getOrder()] = row.isChecked();
                }
                if (makeitPermanent) {
                    this.tree.setColumnOrder(this.gridOrder);
                }
            }

            if (makeitPermanent) {
                TableSetting tableSetting = SettingsController.getTableSetting(this.gridAligns.length, this.clazz, this.tableKey);
                tableSetting.setOrder(this.gridOrder);
                tableSetting.setVisibility(this.gridVisibility);
                tableSetting.setWidths(this.gridDims);
                tableSetting.setAligns(this.gridAligns);
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
            SWTeXtension.displayMessageW("Coloana selectata va fi afisata pe prima pozitie.");
            return;
        }
        if ((this.viewer.getTable().getSelectionIndex() == (input.length - 1))
                && (direction == SWT.DOWN)) {
            SWTeXtension.displayMessageW("Coloana selectata va fi afisata pe ultima pozitie.");
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
            inputNou[element.getOrder()] = element;
        }
        this.viewer.setInput(inputNou);
        this.itemUp.setEnabled(this.viewer.getTable().getSelectionCount() > 0);
        this.itemDown.setEnabled(this.viewer.getTable().getSelectionCount() > 0);
    }

    private int getColumnIndex(final String columnName) {
        if (this.table != null) {
            for (int i = 0; i < this.table.getColumnCount(); i++) {
                if (this.table.getColumn(i).getText().equals(columnName)) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < this.tree.getColumnCount(); i++) {
                if (this.tree.getColumn(i).getText().equals(columnName)) {
                    return i;
                }
            }
        }
        return -1;
    }

}
