package com.papao.books.ui.custom;

import com.papao.books.model.AbstractDB;
import com.papao.books.model.AbstractDBDummy;
import com.papao.books.model.BlankDbObject;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.interfaces.IEncodeHelp;
import com.papao.books.ui.providers.AdbContentProvider;
import com.papao.books.ui.util.WidgetCompositeUtil;
import com.papao.books.ui.view.AbstractCView;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.SWTeXtension;
import com.papao.books.util.ColorUtil;
import com.papao.books.util.StringUtil;
import com.papao.books.util.sorter.AbstractColumnViewerSorter;
import com.papao.books.util.sorter.AbstractTableColumnViewerSorter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class AdbObjectsCheckView extends AbstractCView implements IEncodeHelp, Listener {

	private static Logger logger = Logger.getLogger(AdbObjectsCheckView.class);

	private Map<Long, ? extends AbstractDB> mapAllValues = new HashMap<Long, AbstractDB>();
	protected TableViewer tableViewer;
	private Map<Long, AbstractDB> mapSelected = new HashMap<Long, AbstractDB>();
	private final Class<? extends AbstractDB> clazz;
	private final String[] tableCols;
	private final int[] tableDims;
	private static final int COL_WIDTH = 100;
	private final String[] methods;
	private final AdbSelectorData data;
	private Button buttonExactMatch;
	private Button buttonCaseSensitive;
	private Button buttonContains;
	private Button buttonStartsWith;
	private Button buttonEndsWith;
	private Text textFilter;
	private ComboImage comboColumnSelection;
	private ToolItem itemSelectAll;
	private ToolItem itemDeSelectAll;

	public AdbObjectsCheckView(final Shell shell,
                               final AdbSelectorData data,
                               final Rectangle parentLocAndSize) {
		super(shell, parentLocAndSize, AbstractView.MODE_NONE);
		this.data = data;
		this.tableCols = data.getTableCols();
		this.tableDims = data.getTableDims();
		this.clazz = data.getClazz();
		this.methods = data.getGetterMethods();
		this.mapAllValues = data.getCacheMap();
		this.mapSelected = (Map<Long, AbstractDB>) data.getSelectedMap();
		if (this.mapSelected == null) {
			this.mapSelected = new HashMap<Long, AbstractDB>();
		}
		if (this.mapAllValues == null) {
		}
		if (this.mapSelected.isEmpty()) {
			this.mapSelected.putAll(this.mapAllValues);
		}

		addComponents();
		populateFields();
		updateMessage();
		setShellText(data.getInputShellName());
		this.textFilter.setFocus();
	}

	private void populateFields() {
		this.tableViewer.setInput(null);
		AbstractDBCheck[] input = new AbstractDBCheck[this.mapAllValues.size()];
		int i = 0;
		for (Iterator<?> it = this.mapAllValues.entrySet().iterator(); it.hasNext();) {
			Entry<Long, AbstractDB> entry = (Entry<Long, AbstractDB>) it.next();
			AbstractDB element = entry.getValue();
			AbstractDBCheck obj = new AbstractDBCheck();
			obj.setElement(element);
			obj.setChecked(this.mapSelected.get(entry.getKey()) != null);
			input[i++] = obj;
		}
		this.tableViewer.setInput(input);
	}

	@Override
	public final void customizeView() {
		setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		setViewOptions(AbstractView.ADD_OK | AbstractView.ADD_CANCEL);
		setShellImage(AppImages.getImage16(AppImages.IMG_SELECT));
		setShellText("Selectie inregistrari");
		setBigViewImage(AppImages.getImage24(AppImages.IMG_SELECT));
		setBigViewMessage("Selectie inregistrari");
		setShowSaveOKMessage(Boolean.FALSE);
	}

	public void addSpecificDetails() {
		// empty method, to be overrided in extensions.
	}

	private void addComponents() {
		Composite compFilters = new Composite(getContainer(), SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).span(getWidgetNumCols(), 1).grab(true,
				false).applyTo(compFilters);
		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).spacing(SWT.DEFAULT, 2).applyTo(compFilters);

		new Label(compFilters, SWT.NONE).setText("Coloana");
		ComboImage.CIDescriptor comboDescriptor = new ComboImage.CIDescriptor();
		comboDescriptor.setTextMethodName(BlankDbObject.EXTERNAL_REFLECT_GET_NAME);
		comboDescriptor.setAddEmptyElement(false);
		comboDescriptor.setAddContentProposal(false);
		comboDescriptor.setInput(this.tableCols);
		this.comboColumnSelection = new ComboImage(compFilters, comboDescriptor);
		this.comboColumnSelection.getCombo().select(0);
		this.comboColumnSelection.getCombo().addListener(SWT.Selection, this);
		GridDataFactory.fillDefaults().grab(Boolean.TRUE, Boolean.FALSE).align(SWT.FILL, SWT.CENTER).span(2,
				1).applyTo(this.comboColumnSelection);

		new Label(compFilters, SWT.NONE).setText("Text cautat");
		this.textFilter = new Text(compFilters, SWT.BORDER);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(this.textFilter);
		this.textFilter.addListener(SWT.KeyUp, this);
		this.textFilter.setToolTipText("Introduceti una sau mai multe valori, separate prin virgula");

		this.buttonCaseSensitive = new Button(compFilters, SWT.CHECK);
		this.buttonCaseSensitive.setText("case senzitiv");
		this.buttonCaseSensitive.addListener(SWT.Selection, this);

		this.buttonExactMatch = new Button(compFilters, SWT.CHECK);
		this.buttonExactMatch.setText("cautare exacta");
		this.buttonExactMatch.addListener(SWT.Selection, this);

		this.buttonContains = new Button(compFilters, SWT.CHECK);
		this.buttonContains.setText("contine");
		this.buttonContains.setSelection(true);
		this.buttonContains.addListener(SWT.Selection, this);

		this.buttonStartsWith = new Button(compFilters, SWT.CHECK);
		this.buttonStartsWith.setText("incepe cu");
		this.buttonStartsWith.addListener(SWT.Selection, this);

		this.buttonEndsWith = new Button(compFilters, SWT.CHECK);
		this.buttonEndsWith.setText("se termina cu");
		this.buttonEndsWith.addListener(SWT.Selection, this);

		addSpecificDetails();

		this.tableViewer = new TableViewer(getContainer(), SWT.MULTI | SWT.FULL_SELECTION
				| SWT.VIRTUAL | SWT.BORDER);
		this.tableViewer.setUseHashlookup(Boolean.TRUE);
		GridDataFactory.fillDefaults().grab(Boolean.TRUE, Boolean.TRUE).hint(SWT.DEFAULT, 300).span(getWidgetNumCols(),
				1).applyTo(this.tableViewer.getControl());
		this.tableViewer.getTable().setHeaderVisible(Boolean.TRUE);
		this.tableViewer.getTable().setLinesVisible(Boolean.TRUE);
		this.tableViewer.getTable().setMenu(createTableMenu());
		this.tableViewer.setContentProvider(new AdbContentProvider());

		for (int i = 0; i < this.tableCols.length; i++) {
			final int z = i;

			final TableViewerColumn tblCol = new TableViewerColumn(this.tableViewer, SWT.NONE);
			tblCol.getColumn().setText(this.tableCols[z]);
			if (this.tableDims != null) {
				tblCol.getColumn().setWidth(this.tableDims[z]);
			} else {
				tblCol.getColumn().setWidth(AdbObjectsCheckView.COL_WIDTH);
			}
			tblCol.getColumn().setAlignment(SWT.LEFT);
			tblCol.getColumn().setResizable(Boolean.TRUE);
			tblCol.getColumn().setMoveable(Boolean.TRUE);

			tblCol.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(final Object element) {
					AbstractDBCheck obj = (AbstractDBCheck) element;
					try {
						return String.valueOf(AdbObjectsCheckView.this.clazz.getMethod(AdbObjectsCheckView.this.methods[z],
								(Class<?>[]) null).invoke(obj.getElement(), (Object[]) null));
					}
					catch (Exception exc) {
						logger.error(exc.getMessage(), exc);
						return "???";
					}
				}

				@Override
				public Image getImage(final Object element) {
					if (z != 0) {
						return null;
					}
					AbstractDBCheck obj = (AbstractDBCheck) element;
					if (obj.isChecked()) {
						return AppImages.getImage16(AppImages.IMG_SELECT);
					}
					return AppImages.getImage16(AppImages.IMG_DESELECT);
				}
			});
			AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(
				this.tableViewer,
				tblCol) {
				@Override
				protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
					AbstractDBCheck a = (AbstractDBCheck) e1;
					AbstractDBCheck b = (AbstractDBCheck) e2;
					try {
						return String.valueOf(AdbObjectsCheckView.this.clazz.getMethod(AdbObjectsCheckView.this.methods[z],
								(Class<?>[]) null).invoke(a.getElement(), (Object[]) null)).compareTo(String.valueOf(AdbObjectsCheckView.this.clazz.getMethod(AdbObjectsCheckView.this.methods[z],
								(Class<?>[]) null).invoke(b.getElement(), (Object[]) null)));
					}
					catch (Exception exc) {
						logger.error(exc.getMessage(), exc);
						return -1;
					}
				}
			};
			cSorter.setSorter(cSorter, AbstractColumnViewerSorter.ASC);
			if (z == 0) {
				tblCol.setEditingSupport(new CheckedCellEditor());
			}
		}
		this.tableViewer.getTable().setSortColumn(null);

		compFilters = new Composite(getContainer(), SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.END).span(getWidgetNumCols(), 1).grab(true,
				false).applyTo(compFilters);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(compFilters);

		this.itemSelectAll = new ToolItem(new ToolBar(compFilters, SWT.FLAT | SWT.RIGHT), SWT.NONE);
		this.itemSelectAll.setImage(AppImages.getImage16(AppImages.IMG_SELECT_ALL));
		this.itemSelectAll.setHotImage(AppImages.getImage16Focus(AppImages.IMG_SELECT_ALL));
		this.itemSelectAll.setToolTipText("Selectare totala");
		this.itemSelectAll.setText("Selectare totala");
		this.itemSelectAll.addListener(SWT.Selection, this);

		this.itemDeSelectAll = new ToolItem(this.itemSelectAll.getParent(), SWT.NONE);
		this.itemDeSelectAll.setImage(AppImages.getImage16(AppImages.IMG_DESELECT_ALL));
		this.itemDeSelectAll.setHotImage(AppImages.getImage16Focus(AppImages.IMG_DESELECT_ALL));
		this.itemDeSelectAll.setToolTipText("Deselectare totala");
		this.itemDeSelectAll.setText("Deselectare totala");
		this.itemDeSelectAll.addListener(SWT.Selection, this);

		WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer(),
				ColorUtil.COLOR_FOCUS_YELLOW);

	}

	private Menu createTableMenu() {
		if ((this.tableViewer == null) || this.tableViewer.getTable().isDisposed()) {
			return null;
		}
		final Menu menu = new Menu(this.tableViewer.getTable());
		MenuItem menuItem = null;
		try {
			menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.setText("Selectare");
			menuItem.setImage(AppImages.getImage16(AppImages.IMG_SELECT));
			menuItem.addListener(SWT.Selection, new Listener() {
				@Override
				public final void handleEvent(final Event e) {
					try {
						TableItem[] selItems = AdbObjectsCheckView.this.tableViewer.getTable().getSelection();
						if ((selItems == null) || (selItems.length == 0)) {
							return;
						}
						for (TableItem it : selItems) {
							AbstractDBCheck str = (AbstractDBCheck) it.getData();
							str.setChecked(Boolean.TRUE);
							AdbObjectsCheckView.this.tableViewer.refresh(str);
						}
					}
					catch (Exception exc) {
						logger.error(exc.getMessage(), exc);
					}
				}
			});

			menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.setText("Deselectare");
			menuItem.setImage(AppImages.getImage16(AppImages.IMG_DESELECT));
			menuItem.addListener(SWT.Selection, new Listener() {
				@Override
				public final void handleEvent(final Event e) {
					TableItem[] selItems = AdbObjectsCheckView.this.tableViewer.getTable().getSelection();
					if ((selItems == null) || (selItems.length == 0)) {
						return;
					}
					for (TableItem it : selItems) {
						AbstractDBCheck str = (AbstractDBCheck) it.getData();
						str.setChecked(Boolean.FALSE);
						AdbObjectsCheckView.this.tableViewer.refresh(str);
					}
				}
			});

			new MenuItem(menu, SWT.SEPARATOR);

			menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.setText("Selectare totala");
			menuItem.setImage(AppImages.getImage16(AppImages.IMG_SELECT_ALL));
			menuItem.addListener(SWT.Selection, new Listener() {
				@Override
				public final void handleEvent(final Event e) {
					selectAll(Boolean.TRUE);
				}
			});

			menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.setText("Deselectare totala");
			menuItem.setImage(AppImages.getImage16(AppImages.IMG_DESELECT_ALL));
			menuItem.addListener(SWT.Selection, new Listener() {
				@Override
				public final void handleEvent(final Event e) {
					selectAll(Boolean.FALSE);
				}
			});

		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
		return menu;
	}

	private void selectAll(final boolean select) {
		AbstractDBCheck[] input = (AbstractDBCheck[]) this.tableViewer.getInput();
		for (int i = 0; i < input.length; i++) {
			input[i].setChecked(select);
		}
		this.tableViewer.refresh();
		updateMessage();
	}

    @Override
    protected void saveData() {
        this.mapSelected.clear();
        AbstractDBCheck[] input = (AbstractDBCheck[]) this.tableViewer.getInput();
        for (AbstractDBCheck obj : input) {
            if (obj.isChecked()) {
                this.mapSelected.put(obj.getElement().getId(), obj.getElement());
            }
        }
    }

	@Override
	public final void showHelp() {
		SWTeXtension.displayMessageI("Selectati elementele dorite din tabela." + "Apasati "
				+ getButtonOk().getText() + " pentru confirmarea selectiei.");
	}

	@Override
	public void handleEvent(final Event e) {
		try {
			if (e.type == SWT.Selection) {
				if (e.widget == this.buttonContains) {
					this.buttonExactMatch.setSelection(false);
				} else if (e.widget == this.buttonExactMatch) {
					this.buttonStartsWith.setSelection(false);
					this.buttonEndsWith.setSelection(false);
					this.buttonContains.setSelection(false);
				} else if (e.widget == this.buttonStartsWith) {
					this.buttonExactMatch.setSelection(false);
					this.buttonEndsWith.setSelection(false);
				} else if (e.widget == this.buttonEndsWith) {
					this.buttonExactMatch.setSelection(false);
					this.buttonStartsWith.setSelection(false);
				} else if (e.widget == this.buttonExactMatch) {
					this.buttonContains.setSelection(false);
				} else if (e.widget == this.itemSelectAll) {
					selectAll(true);
				} else if (e.widget == this.itemDeSelectAll) {
					selectAll(false);
				}
				if (!(e.widget instanceof ToolItem)) {
					this.buttonContains.setEnabled(!this.buttonExactMatch.getSelection());
					this.buttonExactMatch.setEnabled(!this.buttonContains.getSelection()
							&& !this.buttonStartsWith.getSelection()
							&& !this.buttonEndsWith.getSelection());
					this.buttonStartsWith.setEnabled(!this.buttonExactMatch.getSelection()
							&& !this.buttonEndsWith.getSelection());
					this.buttonEndsWith.setEnabled(!this.buttonExactMatch.getSelection()
							&& !this.buttonStartsWith.getSelection());
					search();
				}
			} else if (e.type == SWT.KeyUp) {
				search();
			}
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}

	private void search() {
		if (this.textFilter.isDisposed()) {
			return;
		}
		AbstractDBCheck[] input = (AbstractDBCheck[]) this.tableViewer.getInput();
		final String[] filtersStr = this.textFilter.getText().split(",");
		for (int i = 0; i < input.length; i++) {
			String itemName = "";
			try {
				itemName = String.valueOf(AdbObjectsCheckView.this.clazz.getMethod(AdbObjectsCheckView.this.methods[this.comboColumnSelection.getSelectionIndex()],
						(Class<?>[]) null).invoke(input[i].getElement(), (Object[]) null));
			}
			catch (Exception exc) {
				logger.error(exc.getMessage(), exc);
			}
			input[i].setChecked(false);
			if (filtersStr.length == 0) {
				input[i].setChecked(true);
				continue;
			}
			for (int j = 0; j < filtersStr.length; j++) {
				String str = filtersStr[j].trim();
				if (str.isEmpty()) {
					continue;
				}
				if (StringUtil.compareStrings(str,
						itemName,
						this.buttonCaseSensitive.getSelection(),
						this.buttonExactMatch.getSelection(),
						this.buttonContains.getSelection(),
						this.buttonStartsWith.getSelection(),
						this.buttonEndsWith.getSelection())) {
					input[i].setChecked(true);
					break;
				}
			}
		}
		this.tableViewer.refresh();
		updateMessage();
	}

	public final Map<Long, AbstractDB> getSelection() {
		return this.mapSelected;
	}

	public final int getMaxResults() {
		int result = 0;
		if (this.mapAllValues != null) {
			result = this.mapAllValues.size();
		}
		return result;
	}

	@Override
    protected boolean validate() {
        return true;
	}

	protected void updateMessage() {
		AbstractDBCheck[] input = (AbstractDBCheck[]) this.tableViewer.getInput();
		int totalSelectie = 0;
		for (AbstractDBCheck str : input) {
			if (str.isChecked()) {
				totalSelectie++;
			}
		}
		updateDetailMessage("Total selectie : " + totalSelectie + " din " + input.length);
	}

	/**
	 * aici e nevoie de chestia asta, pentru a parametriza fereastra dintr-un level inferior, acela
	 * al caller-ului. Este necesar pt ca nu shtiu decat la apel ce clasa se va randa si ce nume ar
	 * tb sa aiba shell-ul.
	 */
	@Override
	public void setShellText(final String shellText) {
		if (StringUtils.isNotEmpty(shellText) && (getShell() != null)) {
			getShell().setText(shellText);
		}
	}

	public final AdbSelectorData getDataTransport() {
		return this.data;
	}

	protected class AbstractDBCheck extends AbstractDBDummy {
		private boolean checked;
		private AbstractDB element;

		public boolean isChecked() {
			return this.checked;
		}

		public void setChecked(final boolean checked) {
			this.checked = checked;
		}

		public AbstractDB getElement() {
			return this.element;
		}

		public void setElement(final AbstractDB element) {
			this.element = element;
		}
	}

	private class CheckedCellEditor extends EditingSupport {

		public CheckedCellEditor() {
			super(AdbObjectsCheckView.this.tableViewer);
		}

		@Override
		protected CellEditor getCellEditor(final Object paramObject) {
			return new CheckboxCellEditor(
				AdbObjectsCheckView.this.tableViewer.getTable(),
				SWT.CHECK | SWT.READ_ONLY);
		}

		@Override
		protected boolean canEdit(final Object element) {
			return element instanceof AbstractDBCheck;
		}

		@Override
		protected Object getValue(final Object paramObject) {
			AbstractDBCheck obj = (AbstractDBCheck) paramObject;
			return obj.isChecked();
		}

		@Override
		protected void setValue(final Object element, final Object value) {
			AbstractDBCheck obj = (AbstractDBCheck) element;
			obj.setChecked((value instanceof Boolean) && (Boolean) value);
			AdbObjectsCheckView.this.tableViewer.refresh();
			updateMessage();
		}
	}

}
