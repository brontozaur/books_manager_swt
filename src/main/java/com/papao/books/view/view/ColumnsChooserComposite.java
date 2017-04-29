package com.papao.books.view.view;

import com.papao.books.model.AbstractDB;
import com.papao.books.model.AbstractDBDummy;
import com.papao.books.view.AppImages;
import com.papao.books.view.interfaces.IEncodeReset;
import com.papao.books.view.providers.AdbStringContentProvider;
import com.papao.books.view.util.FilterUtil;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

public class ColumnsChooserComposite extends Composite implements Listener, IEncodeReset {

	private static Logger logger = Logger.getLogger(ColumnsChooserComposite.class);

	private Table table;
	private Tree tree;
	private final Class<?> clazz;
	private final String sufix;
	private TableViewer viewer;
	private final static int IDX_NUME = 0;
	private final static int IDX_DIM = 1;
	private final static int IDX_ALIGN = 2;
	private final static String[] ALIGNS = new String[] {
			"St�nga", "Centru", "Dreapta" };
	private final static String[] COLS = new String[] {
			"Coloana", "Dimensiune", "Aliniere" };

	private ToolItem toolSelectAll;
	private ToolItem toolDeSelectAll;
	private ToolItem itemUp;
	private ToolItem itemDown;

	private int[] gridDims;
	private int[] gridAligns;
	private int[] gridOrder;
	private boolean[] gridSelectie;

	public ColumnsChooserComposite(	final Composite parent,
									final Table table,
									final Class<?> clazz,
									final String sufix) {
		super(parent, SWT.NONE);
		this.table = table;
		this.clazz = clazz;
		this.sufix = sufix;
		addComponents();
		populateFields();
	}

	public ColumnsChooserComposite(	final Composite parent,
									final Tree tree,
									final Class<?> clazz,
									final String sufix) {
		super(parent, SWT.NONE);
		this.tree = tree;
		this.clazz = clazz;
		this.sufix = sufix;
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
			final int z = i;
			switch (z) {
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
					tblCol.setEditingSupport(new CheckedCellEditor());
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
					tblCol.setEditingSupport(new IntegerEditingSupport());
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
					tblCol.setEditingSupport(new ComboCellEditor());
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

	private static int[] getCorrectDims(final int[] dims, final int[] order) {
		int[] dd = new int[dims.length];
		for (int i = 0; i < order.length; i++) {
			dd[i] = dims[order[i]];
		}
		return dd;
	}

	private static int[] getCorrectAligns(final int[] aligns, final int[] order) {
		int[] dd = new int[aligns.length];
		for (int i = 0; i < order.length; i++) {
			dd[i] = aligns[order[i]];
		}
		return dd;
	}

	private static boolean[] getCorrectVisible(final boolean[] dims, final int[] order) {
		boolean[] dd = new boolean[dims.length];
		for (int i = 0; i < order.length; i++) {
			dd[i] = dims[order[i]];
		}
		return dd;
	}

	private AbstractDB[] getInputFromTable() {
		AbstractDB[] input;
		final int length = this.table == null	? this.tree.getColumnCount()
												: this.table.getColumnCount();
		input = new AbstractDB[length];
		int[] order = FilterUtil.getSavedGridColumnOrder(length, this.clazz, this.sufix);
		int[] dims = getCorrectDims(FilterUtil.getSavedGridDims(length, this.clazz, this.sufix),
				order);
		boolean[] visibleCols = getCorrectVisible(FilterUtil.getSavedVisibleCols(length,
				this.clazz,
				this.sufix), order);
		int[] aligns = getCorrectAligns(FilterUtil.getSavedGridAligns(length,
				this.clazz,
				this.sufix), order);
		for (int i = 0; i < length; i++) {
			final String colName = this.table == null	? this.tree.getColumn(order[i]).getText()
														: this.table.getColumn(order[i]).getText();
			TableRow row = new TableRow(colName, visibleCols[i], dims[i], aligns[i], order[i]);
			input[i] = row;
		}
		return input;
	}

	private void populateFields() {
		this.viewer.setInput(getInputFromTable());
	}

	public boolean validate() {
		try {
			AbstractDB[] elements = (AbstractDB[]) this.viewer.getInput();
			boolean hasSelection = false;
			for (int i = 0; i < elements.length; i++) {
				TableRow row = (TableRow) elements[i];
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
		}
		catch (Exception exc) {
			SWTeXtension.displayMessageEGeneric(exc);
			return false;
		}
		return true;
	}

	public boolean save(final boolean makeitPermanent) {
		try {
			AbstractDB[] elements = (AbstractDB[]) this.viewer.getInput();
			this.gridDims = new int[elements.length];
			this.gridAligns = new int[elements.length];
			this.gridOrder = new int[elements.length];
			this.gridSelectie = new boolean[elements.length];
			if (this.table != null) {
				for (int i = 0; i < elements.length; i++) {
					TableRow row = (TableRow) elements[i];
					if (makeitPermanent) {
						this.table.getColumn(row.getOrder()).setAlignment(row.getAlign());
						this.table.getColumn(row.getOrder()).setWidth(row.isChecked()	? row.getDim()
																						: 0);
						this.table.getColumn(row.getOrder()).setResizable(row.isChecked());
					}
					this.gridDims[row.getOrder()] = row.getDim();
					this.gridAligns[row.getOrder()] = row.getAlign();
					this.gridOrder[i] = row.getOrder();
					this.gridSelectie[row.getOrder()] = row.isChecked();
				}
				if (makeitPermanent) {
					this.table.setColumnOrder(this.gridOrder);
				}
			} else {
				for (int i = 0; i < elements.length; i++) {
					TableRow row = (TableRow) elements[i];
					if (makeitPermanent) {
						this.tree.getColumn(row.getOrder()).setAlignment(row.getAlign());
						this.tree.getColumn(row.getOrder()).setWidth(row.isChecked() ? row.getDim()
																					: 0);
						this.tree.getColumn(row.getOrder()).setResizable(row.isChecked());
					}
					this.gridDims[row.getOrder()] = row.getDim();
					this.gridAligns[row.getOrder()] = row.getAlign();
					this.gridOrder[i] = row.getOrder();
					this.gridSelectie[row.getOrder()] = row.isChecked();
				}
				if (makeitPermanent) {
					this.tree.setColumnOrder(this.gridOrder);
				}
			}

			if (makeitPermanent) {
				FilterUtil.saveVisibleCols(this.gridSelectie, this.clazz, this.sufix);
				FilterUtil.saveAligns(this.gridAligns, this.clazz, this.sufix);
				FilterUtil.saveDims(this.gridDims, this.clazz, this.sufix);
				FilterUtil.saveOrder(this.gridOrder, this.clazz, this.sufix);
			}

		}
		catch (Exception exc) {
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
		return this.gridSelectie;
	}

	public final class TableRow extends AbstractDBDummy {

		private String colName;
		private boolean checked;
		private int dim;
		private int align;
		private int order;

		public TableRow(final String colName,
						final boolean checked,
						final int dim,
						final int align,
						final int order) {
			setColName(colName);
			setChecked(checked);
			setDim(dim);
			setAlign(align);
			setOrder(order);
		}

		public final String getAlignStr() {
			if (this.align == SWT.LEFT) {
				return ColumnsChooserComposite.ALIGNS[0];
			} else if (this.align == SWT.CENTER) {
				return ColumnsChooserComposite.ALIGNS[1];
			} else {
				return ColumnsChooserComposite.ALIGNS[2];
			}
		}

		public String getColName() {
			return this.colName;
		}

		public void setColName(final String colName) {
			this.colName = colName;
		}

		public boolean isChecked() {
			return this.checked;
		}

		public void setChecked(final boolean checked) {
			this.checked = checked;
		}

		public int getDim() {
			return this.dim;
		}

		public void setDim(final int dim) {
			this.dim = dim;
		}

		public int getAlign() {
			return this.align;
		}

		public void setAlign(final int align) {
			this.align = align;
		}

		public int getOrder() {
			return this.order;
		}

		public void setOrder(final int order) {
			this.order = order;
		}

		@Override
		public String toString() {
			return "Nume : " + getColName() + ", dim : " + getDim() + ", order " + getOrder()
					+ ", alignment " + getAlignStr();
		}
	}

	public class IntegerEditingSupport extends EditingSupport {

		public IntegerEditingSupport() {
			super(ColumnsChooserComposite.this.viewer);
		}

		@Override
		protected CellEditor getCellEditor(final Object element) {
			return new TextCellEditor(ColumnsChooserComposite.this.viewer.getTable());
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
					SWTeXtension.displayMessageW("Introduceti doar numere pozitive!");
					return;
				}
				obj.setDim(intValue);
				ColumnsChooserComposite.this.viewer.refresh();
			}
			catch (NumberFormatException exc) {
				SWTeXtension.displayMessageW("Introduceti doar numere!");
			}
		}
	}

	private class CheckedCellEditor extends EditingSupport {

		public CheckedCellEditor() {
			super(ColumnsChooserComposite.this.viewer);
		}

		@Override
		protected CellEditor getCellEditor(final Object paramObject) {
			return new CheckboxCellEditor(ColumnsChooserComposite.this.viewer.getTable(), SWT.CHECK
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
			ColumnsChooserComposite.this.viewer.refresh();
		}

	}

	private class ComboCellEditor extends EditingSupport {

		public ComboCellEditor() {
			super(ColumnsChooserComposite.this.viewer);
		}

		@Override
		protected CellEditor getCellEditor(final Object paramObject) {
			return new ComboBoxCellEditor(
				ColumnsChooserComposite.this.viewer.getTable(),
				ColumnsChooserComposite.ALIGNS,
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
			ColumnsChooserComposite.this.viewer.refresh();
		}

	}

	private void selectAll(final boolean select) {
		if ((this.viewer == null) || this.viewer.getControl().isDisposed()
				|| (this.viewer.getInput() == null)) {
			return;
		}
		AbstractDB[] input = (AbstractDB[]) this.viewer.getInput();
		for (int i = 0; i < input.length; i++) {
			TableRow row = (TableRow) input[i];
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
		AbstractDB[] input = (AbstractDB[]) this.viewer.getInput();
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
		TableRow row = (TableRow) input[selectedIndex];
		final int swappedIndex = direction == SWT.UP ? selectedIndex - 1 : selectedIndex + 1;
		TableRow swappedRow = (TableRow) input[swappedIndex];
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
		AbstractDB[] input = (AbstractDB[]) this.viewer.getInput();
		AbstractDB[] inputNou = new AbstractDB[input.length];
		for (int i = 0; i < input.length; i++) {
			TableRow element = (TableRow) input[i];
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