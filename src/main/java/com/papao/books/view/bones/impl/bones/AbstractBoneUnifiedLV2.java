package com.papao.books.view.bones.impl.bones;

import com.papao.books.BooleanSetting;
import com.papao.books.view.AppImages;
import com.papao.books.view.bones.AbstractBone;
import com.papao.books.view.bones.AbstractBoneDescriptor;
import com.papao.books.view.bones.filter.AbstractBoneFilter;
import com.papao.books.view.bones.impl.filters.AbstractFilterViewMode;
import com.papao.books.view.providers.UnifiedStyledLabelProvider;
import com.papao.books.view.providers.tree.SimpleTextNode;
import com.papao.books.view.providers.tree.TreeContentProvider;
import com.papao.books.view.util.*;
import com.papao.books.view.util.sorter.AbstractColumnViewerSorter;
import com.papao.books.view.util.sorter.AbstractTreeColumnViewerSorter;
import com.papao.books.view.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.*;

public abstract class AbstractBoneUnifiedLV2 extends AbstractBoneUnifiedLV1 {

	private static Logger logger = Logger.getLogger(AbstractBoneUnifiedLV2.class);

	private Text upperTextSearch;
	private TreeViewer leftTreeViewer;
	private ToolItem itemShowLeftTree;
	private Link linkViewMode;
	private UnifiedStyledLabelProvider leftTreeColumnProvider;

	public AbstractBoneUnifiedLV2(	final Composite parent,
									final String name,
									final Integer idBone,
									final AbstractBoneFilter filtru,
									final AbstractBoneDescriptor descriptor) {
		super(parent, name, idBone, filtru, descriptor);
	}

	private final Menu createLeftTreeMenu() {
		if ((getLeftTreeViewer() == null) || getLeftTreeViewer().getControl().isDisposed()) {
			return null;
		}
		final Menu menu = new Menu(getLeftTreeViewer().getTree());
		MenuItem menuItem;
		menu.addListener(SWT.Show, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				int idx = 0;
				final boolean flagItemCount = (getLeftTreeViewer().getTree().getItemCount() > 0);
				final boolean isDateView = getFiltru().getTreeViewMode() == AbstractFilterViewMode.AFISARE_DUPA_DATA;
				menu.getItem(idx++).setEnabled(flagItemCount && isDateView); // expand all
				menu.getItem(idx++).setEnabled(flagItemCount && isDateView); // colapse all
				menu.getItem(idx++).setEnabled(getFiltru().getTreeAlignment() == SWT.RIGHT);
				menu.getItem(idx++).setEnabled(getFiltru().getTreeAlignment() == SWT.LEFT);
				menu.getItem(idx++).setEnabled(true); // separator
				menu.getItem(idx++).setEnabled(true); // selectie tip afisare
				menu.getItem(idx++).setEnabled(SettingsController.getBoolean(BooleanSetting.LEFT_TREE_SHOW_RECENT));
			}
		});

		menuItem = new MenuItem(menu, SWT.NONE);
		menuItem.setText("Expandare");
		menuItem.setImage(AppImages.getImage16(AppImages.IMG_EXPAND));
		menuItem.addListener(SWT.Selection, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				getLeftTreeViewer().expandAll();
			}
		});

		menuItem = new MenuItem(menu, SWT.NONE);
		menuItem.setText("Colapsare");
		menuItem.setImage(AppImages.getImage16(AppImages.IMG_COLLAPSE));
		menuItem.addListener(SWT.Selection, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				getLeftTreeViewer().collapseAll();
			}
		});

		menuItem = new MenuItem(menu, SWT.NONE);
		menuItem.setText("Aliniere la stânga");
		menuItem.addListener(SWT.Selection, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				swap(false);
			}
		});

		menuItem = new MenuItem(menu, SWT.NONE);
		menuItem.setText("Aliniere la dreapta");
		menuItem.addListener(SWT.Selection, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				swap(false);
			}
		});

		new MenuItem(menu, SWT.SEPARATOR);

		menuItem = new MenuItem(menu, SWT.NONE);
		menuItem.setImage(AppImages.getImage16(AppImages.IMG_MOD_VIZUALIZARE));
		menuItem.setText("Selectie mod afisare");
		menuItem.addListener(SWT.Selection, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				handleSelectTreeViewMode();
				enableOps();
			}
		});

		menuItem = new MenuItem(menu, SWT.NONE);
		menuItem.setText("Distribuire operatii recente");
		menuItem.addListener(SWT.Selection, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				populateLeftTree(false);
				enableOps();
			}
		});
		return menu;
	}

	public abstract void populateLeftTree(final boolean reload);

	public abstract void handleSelectionOnTree();

	public abstract void handleSelectTreeViewMode();

	@Override
    public void finishImplementation() {
		super.finishImplementation();

		Composite comp = new CLabel(getCompLeftTree(), SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).align(SWT.FILL, SWT.CENTER).hint(SWT.DEFAULT,
				25).applyTo(comp);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).extendedMargins(5, 5, 3, 2).applyTo(comp);

		new Label(comp, SWT.NONE).setText("Filtru");
		setUpperTextSearch(new Text(comp, SWT.SEARCH));
		GridDataFactory.fillDefaults().grab(true, false).span(1, 1).applyTo(getUpperTextSearch());
		SWTeXtension.addColoredFocusListener(getUpperTextSearch(), ColorUtil.COLOR_FOCUS_YELLOW);
		getUpperTextSearch().setMessage(AbstractBone.FILTRARE_DEFAULT);
		getUpperTextSearch().addListener(SWT.Modify, this);
		getUpperTextSearch().addListener(SWT.FocusIn, this);

		setLeftTreeViewer(new TreeViewer(getCompLeftTree(), SWT.SINGLE | SWT.FULL_SELECTION
				| SWT.BORDER));
		getLeftTreeViewer().setUseHashlookup(true);
		SWTeXtension.addColoredFocusListener(getLeftTreeViewer().getTree(),
				ColorUtil.COLOR_FOCUS_YELLOW);

		getLeftTreeViewer().setContentProvider(new TreeContentProvider());
		getLeftTreeViewer().getTree().setHeaderVisible(true);
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(getLeftTreeViewer().getTree());
		getLeftTreeViewer().setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);


		final TreeViewerColumn treeCol = new TreeViewerColumn(getLeftTreeViewer(), SWT.NONE);
		treeCol.getColumn().setText("Grupare elemente");
		treeCol.getColumn().setWidth(200);
		treeCol.getColumn().setAlignment(SWT.CENTER);
		treeCol.getColumn().setResizable(true);
		treeCol.getColumn().setMoveable(false);
		this.leftTreeColumnProvider = new UnifiedStyledLabelProvider();
		treeCol.setLabelProvider(this.leftTreeColumnProvider);

		AbstractTreeColumnViewerSorter cSorter = new AbstractTreeColumnViewerSorter(
			getLeftTreeViewer(),
			treeCol) {
			@Override
			protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
				SimpleTextNode a = ((SimpleTextNode) e1);
				SimpleTextNode b = ((SimpleTextNode) e2);
				if (a == null) {
					return -1;
				} else if (b == null) {
					return 1;
				} else {
					int x1 = 0, x2 = 0;
					if (a.getName().startsWith(BorgDateUtil.IAN)) {
						x1 = 1;
					} else if (a.getName().startsWith(BorgDateUtil.FEB)) {
						x1 = 2;
					} else if (a.getName().startsWith(BorgDateUtil.MAR)) {
						x1 = 3;
					} else if (a.getName().startsWith(BorgDateUtil.APR)) {
						x1 = 4;
					} else if (a.getName().startsWith(BorgDateUtil.MAI)) {
						x1 = 5;
					} else if (a.getName().startsWith(BorgDateUtil.IUN)) {
						x1 = 6;
					} else if (a.getName().startsWith(BorgDateUtil.IUL)) {
						x1 = 7;
					} else if (a.getName().startsWith(BorgDateUtil.AUG)) {
						x1 = 8;
					} else if (a.getName().startsWith(BorgDateUtil.SEP)) {
						x1 = 9;
					} else if (a.getName().startsWith(BorgDateUtil.OCT)) {
						x1 = 10;
					} else if (a.getName().startsWith(BorgDateUtil.NOI)) {
						x1 = 11;
					} else if (a.getName().startsWith(BorgDateUtil.DEC)) {
						x1 = 12;
					}
					if (x1 == 0) {
						return a.getName().compareToIgnoreCase(b.getName());
					}

					if (b.getName().startsWith(BorgDateUtil.IAN)) {
						x2 = 1;
					} else if (b.getName().startsWith(BorgDateUtil.FEB)) {
						x2 = 2;
					} else if (b.getName().startsWith(BorgDateUtil.MAR)) {
						x2 = 3;
					} else if (b.getName().startsWith(BorgDateUtil.APR)) {
						x2 = 4;
					} else if (b.getName().startsWith(BorgDateUtil.MAI)) {
						x2 = 5;
					} else if (b.getName().startsWith(BorgDateUtil.IUN)) {
						x2 = 6;
					} else if (b.getName().startsWith(BorgDateUtil.IUL)) {
						x2 = 7;
					} else if (b.getName().startsWith(BorgDateUtil.AUG)) {
						x2 = 8;
					} else if (b.getName().startsWith(BorgDateUtil.SEP)) {
						x2 = 9;
					} else if (b.getName().startsWith(BorgDateUtil.OCT)) {
						x2 = 10;
					} else if (b.getName().startsWith(BorgDateUtil.NOI)) {
						x2 = 11;
					} else if (b.getName().startsWith(BorgDateUtil.DEC)) {
						x2 = 12;
					}

					if ((x1 != 0) || (x2 != 0)) {
						return x1 - x2;
					}
					return a.getName().compareToIgnoreCase(b.getName());
				}
			}

		};
		cSorter.setSorter(cSorter, AbstractColumnViewerSorter.ASC);
		getLeftTreeViewer().getTree().setSortColumn(null);

		getLeftTreeViewer().getTree().setCursor(WidgetCursorUtil.getCursor(SWT.CURSOR_HAND));
		getLeftTreeViewer().getTree().setMenu(createLeftTreeMenu());
		WidgetTreeUtil.customizeTree(getLeftTree(), getClass(), getBoneName());

		getLeftTreeViewer().getTree().addListener(SWT.Selection, this);

		comp = new Composite(getCompLeftTree(), SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).align(SWT.FILL, SWT.CENTER).applyTo(comp);
		GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(5, 0, 0, 2).applyTo(comp);
		this.linkViewMode = new Link(comp, SWT.NONE);
		this.linkViewMode.setToolTipText("Schimba modul de afisare");
		this.linkViewMode.addListener(SWT.Selection, this);
		GridDataFactory.fillDefaults().grab(true, false).span(1, 1).align(SWT.BEGINNING, SWT.CENTER).applyTo(this.linkViewMode);

		getVerticalSash().setMaximizedControl(null);

		try {
			if ((getFiltru() != null) && !getFiltru().isShowingTree()) {
				getVerticalSash().setMaximizedControl(getMainCompRight());
				getItemShowLeftTree().setSelection(false);
				getItemShowLeftTree().setImage(AppImages.getImage16(AppImages.IMG_HIDE));
			}
		}
		catch (Exception exc) {
			logger.warn(exc);
		}
	}

	/**
	 * asta vine apelata din fiecare copil, pentru a sigura pozitionarea tool item-ului la index-ul
	 * dorit. Exemplu apelare : super.createToolItemShowLeftTree();
	 */
	public final void createToolItemShowLeftTree() {
		setItemShowLeftTree(new ToolItem(getBarOps(), SWT.CHECK));
		getItemShowLeftTree().setImage(AppImages.getImage16(AppImages.IMG_SHOW));
		getItemShowLeftTree().setHotImage(AppImages.getImage16Focus(AppImages.IMG_HIDE));
		getItemShowLeftTree().setToolTipText("Afisare sau nu componenta din partea stanga. Daca sunteti pozitionat in tabela, "
				+ "apasati [Ctrl+G] pentru a schimba selectia.");
		getItemShowLeftTree().addListener(SWT.Selection, this);
		if ((getFiltru() != null) && getFiltru().isBarOpsShowingText()) {
			getItemShowLeftTree().setText("Grupare");
		}
	}

	@Override
	public void handleEvent(final Event e) {
		super.handleEvent(e);
		switch (e.type) {
			case SWT.Selection: {
				if (e.widget == getLeftTreeViewer().getTree()) {
					/*
					 * linia asta rezolva un bug legat de apelul getViewer().setInput(null). Din
					 * cand in cand (destul de des) crapa aici, daca existau si totaluri afisate in
					 * tabela, pt ca probabil se facea intern o de-mapare a elementelor din tabela,
					 * si liniile cu total nu erau mapate. Clasa care genera eroarea este
					 * org.eclipse.jface.viewers.CustomHashtable.hashCode(CustomHashtable.java:264).
					 */
					getTable().removeAll();
					handleSelectionOnTree();
					computeTotal();
				} else if (e.widget == getItemShowLeftTree()) {
					handleLeftTreeDisplay(false);
				} else if (e.widget == this.linkViewMode) {
					handleSelectTreeViewMode();
				}
				break;
			}
			case SWT.KeyDown: {
				if ((e.widget == getTable()) || (e.widget == getTreeDocs())) {
					/**
					 * detectam acum daca e apasat Ctrl
					 */
					if ((e.stateMask & SWT.CTRL) != 0) {
						if ((e.keyCode == 'g') || (e.keyCode == 'G')) {
							handleLeftTreeDisplay(true);
						}
					}
				}
				break;
			}
			case SWT.Modify: {
				if (e.widget == getUpperTextSearch()) {
					this.leftTreeColumnProvider.setSearchText(getUpperTextSearch().getText());
					getLeftTreeViewer().setFilters(SimpleTextNode.getFilter(getUpperTextSearch().getText()));
					getLeftTreeViewer().expandToLevel(AbstractTreeViewer.ALL_LEVELS);
				}
				break;
			}
			case SWT.FocusIn: {
				if ((e.widget == getUpperTextSearch())
						&& getUpperTextSearch().getText().equals(AbstractBone.FILTRARE_DEFAULT)) {
					getUpperTextSearch().setText("");
				}
				break;
			}
			default:

		}
	}

	/**
	 * @param isCodeSelection
	 */
	public void handleLeftTreeDisplay(final boolean isCodeSelection) {
		if (getItemShowLeftTree() == null) {
			return;
		}
		if (isCodeSelection) {
			getItemShowLeftTree().setSelection(!getItemShowLeftTree().getSelection());
		}
		if (getItemShowLeftTree().getSelection()) {
			getVerticalSash().setMaximizedControl(null);
		} else {
			getVerticalSash().setMaximizedControl(getMainCompRight());
		}
		if (getItemShowLeftTree().getSelection()) {
			getItemShowLeftTree().setImage(AppImages.getImage16(AppImages.IMG_HIDE));
		} else {
			getItemShowLeftTree().setImage(AppImages.getImage16(AppImages.IMG_SHOW));
		}
		if (getFiltru() != null) {
			getFiltru().putBoolean(AbstractBoneFilter.IS_SHOWING_TREE,
					getItemShowLeftTree().getSelection());
		}
	}

	public void swap(final boolean isCodeSelection) {
		int ALIGN = SWT.LEFT;
		if (getFiltru() != null) {
			ALIGN = getFiltru().getTreeAlignment();
		}
		if ((getCompLeftTree().getLocation().x < getMainCompRight().getLocation().x)
				|| (isCodeSelection && (ALIGN == SWT.RIGHT))) {
			getCompLeftTree().moveBelow(getMainCompRight());
			getVerticalSash().setWeights(new int[] {
					10, 3 });
			if (!isCodeSelection && (getFiltru() != null)) {
				getFiltru().putInt(AbstractBoneFilter.LEFT_TREE_ALIGNMENT, SWT.RIGHT);
			}
		} else {
			getCompLeftTree().moveAbove(getMainCompRight());
			getVerticalSash().setWeights(new int[] {
					3, 10 });
			if ((getFiltru() != null) && !isCodeSelection) {
				getFiltru().putInt(AbstractBoneFilter.LEFT_TREE_ALIGNMENT, SWT.LEFT);
			}
		}
		super.verticalSashWeights = getVerticalSash().getWeights().clone();
	}

	@Override
	public final void refresh() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
//				if (FiltruAplicatie.isTabsUsingCount()) {
					final int count = getCount();
//					if ((count != 0) && (count != Integer.MAX_VALUE)
//							&& (count > FiltruAplicatie.getCount())) {
//						if (SWTeXtension.displayMessageQ("Exista " + count
//								+ " inregistrari in baza de date. Doriti afisarea lor?",
//								"Avertizare inregistrari") == SWT.NO) {
//							return;
//						}
//					}
//				}
				populateLeftTree(true);
				enableOps();
			}
		});
	}

	public final Text getUpperTextSearch() {
		return this.upperTextSearch;
	}

	private final void setUpperTextSearch(final Text upperTextSearch) {
		this.upperTextSearch = upperTextSearch;
	}

	public final TreeViewer getLeftTreeViewer() {
		return this.leftTreeViewer;
	}

	private final void setLeftTreeViewer(final TreeViewer leftTreeViewer) {
		this.leftTreeViewer = leftTreeViewer;
	}

	public final Tree getLeftTree() {
		return this.leftTreeViewer.getTree();
	}

	public final ToolItem getItemShowLeftTree() {
		return this.itemShowLeftTree;
	}

	private final void setItemShowLeftTree(final ToolItem itemShowLeftTree) {
		this.itemShowLeftTree = itemShowLeftTree;
	}

	public final void setNumeCriteriuAfisare(final String str) {
		if ((str == null) || this.linkViewMode.isDisposed()) {
			return;
		}

		this.linkViewMode.setText("Mod afisare: <a>" + str + "</a>");
		this.linkViewMode.update();
		this.linkViewMode.getParent().layout();
	}

}
