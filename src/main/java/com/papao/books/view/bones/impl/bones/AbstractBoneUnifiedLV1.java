package com.papao.books.view.bones.impl.bones;

import com.papao.books.view.AppImages;
import com.papao.books.view.bones.AbstractBone;
import com.papao.books.view.bones.AbstractBoneDescriptor;
import com.papao.books.view.bones.filter.AbstractBoneFilter;
import com.papao.books.view.interfaces.ITableBone;
import com.papao.books.view.interfaces.ITreeBone;
import com.papao.books.view.searcheable.BorgSearchSystem;
import com.papao.books.view.util.WidgetCursorUtil;
import com.papao.books.view.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public abstract class AbstractBoneUnifiedLV1 extends AbstractBone implements Listener {

	private static final Logger logger = Logger.getLogger(AbstractBoneUnifiedLV1.class);

	private SashForm verticalSash;
	private SashForm rightInnerSash;
	private Composite compLeftTree;
	private Composite mainCompRight;
	private Composite innerCompRight;
	private Composite compRight;
	private ToolBar barOps;
	private ScrolledComposite compDetailsScrolled;
	private ToolItem itemShowSearch;
	private SashForm searchSash;
	private Table table;
	private Tree treeDocs;
	public int[] verticalSashWeights = new int[] {
			3, 12 };

	public AbstractBoneUnifiedLV1(	final Composite parent,
									final String name,
									final Integer idBone,
									final AbstractBoneFilter filtru,
									final AbstractBoneDescriptor descriptor) {
		super(parent, name, idBone, filtru, descriptor);
	}

	@Override
    public void finishImplementation() {
		setVerticalSash(new SashForm(getContainer(), SWT.HORIZONTAL | SWT.SMOOTH));
		getVerticalSash().SASH_WIDTH = 4;
		GridDataFactory.fillDefaults().grab(true, true).span(((GridLayout) getContainer().getLayout()).numColumns,
				1).hint(900, 400).applyTo(getVerticalSash());

		setCompLeftTree(new Composite(getVerticalSash(), SWT.NONE));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(getCompLeftTree());
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(2, 2).extendedMargins(0,
				0,
				0,
				0).spacing(5, 3).applyTo(getCompLeftTree());

		setMainCompRight(new Composite(getVerticalSash(), SWT.NONE));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(getMainCompRight());
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(0, 0).applyTo(getMainCompRight());

		int barStyle = SWT.FLAT | SWT.WRAP;
		try {
			if (getFiltru() != null) {
				barStyle |= getFiltru().getBarOpsStyle();
			}
		}
		catch (Exception exc) {
			logger.warn(exc);
		}

		barStyle = SWT.FLAT | SWT.WRAP;// | SWT.RIGHT;

		setBarOps(new ToolBar(getMainCompRight(), barStyle));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(getBarOps());

		createBarOpsItems();

		getBarOps().setMenu(createBarOpsMenu());

		getMainCompRight().addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				Rectangle rect = getMainCompRight().getClientArea();
				Point size = getBarOps().computeSize(rect.width, SWT.DEFAULT);
				getBarOps().setSize(size);
			}
		});

		int style = SWT.SMOOTH | SWT.HORIZONTAL;
		setSearchSash(new SashForm(getMainCompRight(), style));
		getSearchSash().SASH_WIDTH = 4;
		GridDataFactory.fillDefaults().grab(true, true).applyTo(getSearchSash());
		setSearchSystem(new BorgSearchSystem(getSearchSash()));

		int sashStyle = SWT.VERTICAL;
		if (this instanceof AbstractBoneUnifiedLV3) {
			try {
				sashStyle = getFiltru().getInnerRightSashStyle();
			}
			catch (IllegalArgumentException exc) {
				sashStyle = SWT.VERTICAL;
				logger.warn(getFiltru().getClass().getCanonicalName()
						+ " doesnt specify a value for the inner right sash style! Defaults are used...");
			}
		}
		setRightInnerSash(new SashForm(getSearchSash(), sashStyle | SWT.SMOOTH));
		getRightInnerSash().SASH_WIDTH = 4;
		getRightInnerSash().setLayout(new GridLayout(2, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(getRightInnerSash());

		setInnerCompRight(new Composite(getRightInnerSash(), SWT.NONE));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(getInnerCompRight());
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(0, 0).extendedMargins(SWT.DEFAULT,
				SWT.DEFAULT,
				0,
				0).applyTo(getInnerCompRight());

		getDescriptor().createViewer(getInnerCompRight());

		if (this instanceof ITableBone) {
			setTable(((ITableBone) this).getViewer().getTable());
			getTable().setHeaderVisible(true);
			getTable().setLinesVisible(true);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(getTable());
			getTable().setMenu(((ITableBone) this).createTableMenu());
			getTable().addListener(SWT.Selection, this);
			getTable().addListener(SWT.KeyDown, this);
			getTable().addListener(SWT.DefaultSelection, this);

			WidgetCursorUtil.addHandCursorListener(getTable());
			SWTeXtension.addColoredFocusListener(getTable(), null);
		} else if (this instanceof ITreeBone) {
			setTreeDocs(((ITreeBone) this).getViewer().getTree());
			getTreeDocs().setHeaderVisible(true);
			getTreeDocs().setLinesVisible(true);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(getTreeDocs());
			getTreeDocs().setMenu(((ITreeBone) this).createTreeDocsMenu());
			getTreeDocs().addListener(SWT.Selection, this);
			getTreeDocs().addListener(SWT.KeyDown, this);
			getTreeDocs().addListener(SWT.DefaultSelection, this);

			WidgetCursorUtil.addHandCursorListener(getTreeDocs());
			SWTeXtension.addColoredFocusListener(getTreeDocs(), null);
		}

		// operations order is very important here!
		getDescriptor().initViewerCols(getFiltru());
		getSearchSystem().indexColumns(getDescriptor().getTableCols());
		getSearchSystem().setViewer(getDescriptor().getViewer());
		getDescriptor().createViewerFilters(getSearchSystem());
		getSearchSystem().initCacheMap();

		getSearchSystem().getSearchButton().registerListeners(SWT.MouseUp, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				try {
					setDlgMessage("Asteptati efectuarea cautarii...");
					getDescriptor().search();
					closeDlg();
				}
				catch (Exception exc) {
					closeDlg();
					logger.error(exc.getMessage(), exc);
				}
			}
		});

		setCompDetailsScrolled(new ScrolledComposite(getRightInnerSash(), SWT.H_SCROLL
				| SWT.V_SCROLL));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(getCompDetailsScrolled());
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).margins(2, 2).applyTo(getCompDetailsScrolled());
		/**
		 * setand focus-ul pe scrolled comp, va functiona scroll-ul componentelor desenate pe el,
		 * folosing rotitza din mijloc a mouse-ului.
		 */
		getCompDetailsScrolled().addListener(SWT.Activate, this);
		/**
		 * cum viteza scroll-ului in cazul asta este foarte mica, o marim de 5 ori.
		 */
		getCompDetailsScrolled().getVerticalBar().setIncrement(getCompDetailsScrolled().getVerticalBar().getIncrement() * 5);

		setCompRight(new Composite(getCompDetailsScrolled(), SWT.NONE));
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.CENTER).applyTo(getCompRight());
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).margins(0, 0).spacing(SWT.DEFAULT,
				0).applyTo(getCompRight());

		getCompDetailsScrolled().setContent(getCompRight());
		getCompDetailsScrolled().setExpandVertical(true);
		getCompDetailsScrolled().setExpandHorizontal(true);
		getCompDetailsScrolled().addListener(SWT.Resize, this);
		getCompDetailsScrolled().addListener(SWT.Activate, this);

		getVerticalSash().setWeights(this.verticalSashWeights);
		getVerticalSash().setMaximizedControl(getMainCompRight());
		getSearchSash().setWeights(new int[] {
				5, 14 });
		getSearchSash().setMaximizedControl(getRightInnerSash());

		getRightInnerSash().setWeights(new int[] {
				7, 3 });
		getRightInnerSash().setMaximizedControl(getInnerCompRight());
	}

	/**
	 * asta vine apelata din fiecare copil, pentru a sigura pozitionarea tool item-ului la index-ul
	 * dorit. Exemplu apelare : super.createToolItemShowLeftTree(). La apasarea acestui "boton" se
	 * afiseaza sau nu componenta de cautare in tabela;
	 */
	public final void createToolItemShowSearch() {
		setItemShowSearch(new ToolItem(getBarOps(), SWT.CHECK));
		getItemShowSearch().setImage(AppImages.getImage16(AppImages.IMG_SEARCH));
		getItemShowSearch().setHotImage(AppImages.getImage16Focus(AppImages.IMG_SEARCH));
		getItemShowSearch().setToolTipText("Afisare sau nu componenta de cautare in tabela");
		getItemShowSearch().addListener(SWT.Selection, this);
		if ((getFiltru() != null) && getFiltru().isBarOpsShowingText()) {
			getItemShowSearch().setText("Cautare");
		}
	}

	public final void handleSearchDisplay(final boolean isCodeSelection) {
		if (isCodeSelection) {
			getItemShowSearch().setSelection(!getItemShowSearch().getSelection());
		}
		if (getItemShowSearch().getSelection()) {
			getSearchSash().setMaximizedControl(null);
			getSearchSystem().getFiltreComposite().getTextInfo().setFocus();
		} else {
			getSearchSash().setMaximizedControl(getRightInnerSash());
		}
	}

	@Override
	public void handleEvent(final Event e) {
		switch (e.type) {
			case SWT.Selection: {
				if (e.widget == getItemShowSearch()) {
					handleSearchDisplay(false);
				}
				break;
			}
			case SWT.KeyDown: {
				if ((e.widget == getTable()) || (e.widget == getTreeDocs())) {
					if (e.keyCode == SWT.F3) {
						handleSearchDisplay(true);
					}
				}
				processKeyStrokes(e);
				break;
			}
			case SWT.DefaultSelection: {
				if ((e.widget == getTable()) || (e.widget == getTreeDocs())) {
					showItemData();
				}
				break;
			}
			case SWT.Activate: {
				if (e.widget == getCompDetailsScrolled()) {
					getCompDetailsScrolled().setFocus();
				}
				break;
			}
			default:

		}
	}

	public abstract void createBarOpsItems();

	public abstract void enableOps();

	public abstract Menu createBarOpsMenu();

	public abstract void processKeyStrokes(final Event e);

	public abstract void showItemData();

	public final SashForm getVerticalSash() {
		return this.verticalSash;
	}

	private final void setVerticalSash(final SashForm verticalSash) {
		this.verticalSash = verticalSash;
	}

	public final SashForm getRightInnerSash() {
		return this.rightInnerSash;
	}

	private final void setRightInnerSash(final SashForm rightInnerSash) {
		this.rightInnerSash = rightInnerSash;
	}

	public final Composite getCompLeftTree() {
		return this.compLeftTree;
	}

	private final void setCompLeftTree(final Composite compLeftTree) {
		this.compLeftTree = compLeftTree;
	}

	public final Composite getMainCompRight() {
		return this.mainCompRight;
	}

	private final void setMainCompRight(final Composite mainCompRight) {
		this.mainCompRight = mainCompRight;
	}

	public final ToolBar getBarOps() {
		return this.barOps;
	}

	private final void setBarOps(final ToolBar barOps) {
		this.barOps = barOps;
	}

	public final ToolItem getItemShowSearch() {
		return this.itemShowSearch;
	}

	private final void setItemShowSearch(final ToolItem itemShowSearch) {
		this.itemShowSearch = itemShowSearch;
	}

	public final SashForm getSearchSash() {
		return this.searchSash;
	}

	private final void setSearchSash(final SashForm searchSash) {
		this.searchSash = searchSash;
	}

	public final ScrolledComposite getCompDetailsScrolled() {
		return this.compDetailsScrolled;
	}

	private final void setCompDetailsScrolled(final ScrolledComposite compDetailsScrolled) {
		this.compDetailsScrolled = compDetailsScrolled;
	}

	public final Table getTable() {
		return this.table;
	}

	private final void setTable(final Table table) {
		this.table = table;
	}

	public final Composite getCompRight() {
		return this.compRight;
	}

	private final void setCompRight(final Composite compRight) {
		this.compRight = compRight;
	}

	public final Composite getInnerCompRight() {
		return this.innerCompRight;
	}

	private final void setInnerCompRight(final Composite innerCompRight) {
		this.innerCompRight = innerCompRight;
	}

	public final Tree getTreeDocs() {
		return this.treeDocs;
	}

	private final void setTreeDocs(final Tree treeDocs) {
		this.treeDocs = treeDocs;
	}

}
