package com.papao.books.view.searcheable;

import com.novocode.naf.swt.custom.LiveSashForm;
import com.papao.books.model.AbstractDB;
import com.papao.books.model.BlankDbObject;
import com.papao.books.view.AppImages;
import com.papao.books.view.custom.*;
import com.papao.books.view.interfaces.IEncodeReset;
import com.papao.books.view.util.ColorUtil;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class BorgSearchSystem extends Composite implements Listener, IEncodeReset {

	private final static String VISIBLE_FILTERS_GROUP_SPLIT_CHAR = "##";
	private Map<String, Boolean> visibleFiltersMap;

	private final LiveSashForm sashParinte;
	private Composite compRight;
	private ScrolledComposite scrolledComp;
	private Composite compCriterii;
	protected AdbSelectorComposite filtreComposite;
	private ColumnViewer viewer;
	private XButton buttonCauta;
	private Object parentInstance;

	private final Map<String, AbstractSearchType> visibleFilters = new TreeMap<String, AbstractSearchType>();
	private final Map<String, AbstractSearchType> hiddenFilters = new TreeMap<String, AbstractSearchType>();
	private final Map<String, Integer> indexMapping = new HashMap<String, Integer>();
	private final Map<Integer, String> indexMappingByPosition = new HashMap<Integer, String>();

	public BorgSearchSystem(final LiveSashForm sashParinte) {
		super(sashParinte, SWT.NONE);
		this.sashParinte = sashParinte;
		this.addListener(SWT.Dispose, this);

		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).minSize(110,
				SWT.DEFAULT).applyTo(this);
		GridLayoutFactory.swtDefaults().numColumns(1).equalWidth(true).margins(0, 0).applyTo(this);
		addComponents();
	}

	private void addComponents() {
		Label separator;
		separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(separator);

		Composite compActions = new Composite(this, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).span(((GridLayout) getLayout()).numColumns,
				1).applyTo(compActions);
		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).extendedMargins(5, 0, 0, 0).spacing(2,
				2).applyTo(compActions);

		new Label(compActions, SWT.NONE).setText("Filtre");

		AdbSelectorData adbData = new AdbSelectorData();
		adbData.setAddLabel(false);
		adbData.setInputShellName("Selectie filtre");
		adbData.setTableCols(new String[] {
			"Filtru" });
		this.filtreComposite = new AdbSelectorComposite(compActions, adbData);
		this.filtreComposite.replaceSelectionListener(new Listener() {
			@Override
			public void handleEvent(final Event event) {
				chooseObjects();
			}
		});
		this.filtreComposite.replaceKeyDownListener(new Listener() {
			@Override
			public void handleEvent(final Event event) {
				if (event.keyCode == SWT.F3) {
					chooseObjects();
				}
			}
		});

		Composite comp = new Composite(compActions, SWT.NONE);
		GridDataFactory.fillDefaults().grab(false, false).align(SWT.END, SWT.CENTER).applyTo(comp);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(0, 0).spacing(0, 0).applyTo(comp);

		XButtonData data = new XButtonData();
		data.setBorderColor(ColorUtil.COLOR_WHITE);
		data.setImage(AppImages.getImage16(AppImages.IMG_SEARCH));
		data.setHotImage(AppImages.getImage16Focus(AppImages.IMG_SEARCH));
		data.setMainText("Cautare");
		data.setToolTip("Cautare in tabela (se iau in calcul doar valorile completate/selectate)");
		data.setWidth(80);
		data.setTextAlignment(SWT.RIGHT);

		this.buttonCauta = new XButton(comp, data);

		separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.END).applyTo(separator);

		this.compRight = new Composite(this, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(this.compRight);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).applyTo(this.compRight);

		this.scrolledComp = new ScrolledComposite(this.compRight, SWT.H_SCROLL | SWT.V_SCROLL);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(this.scrolledComp);
		GridLayoutFactory.fillDefaults().margins(1, 1).numColumns(1).equalWidth(true).applyTo(this.scrolledComp);
		/**
		 * cum viteza scroll-ului in cazul asta este foarte mica, o marim de 5 ori.
		 */
		this.scrolledComp.getVerticalBar().setIncrement(this.scrolledComp.getVerticalBar().getIncrement() * 5);

		this.compCriterii = new Composite(this.scrolledComp, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(this.compCriterii);
		GridLayoutFactory.fillDefaults().margins(0, 0).spacing(SWT.DEFAULT, 0).spacing(0, 5).numColumns(1).equalWidth(true).applyTo(this.compCriterii);

		this.compCriterii.addListener(SWT.Resize, this);

		this.scrolledComp.setContent(this.compCriterii);
		this.scrolledComp.setExpandVertical(true);
		this.scrolledComp.setExpandHorizontal(true);
	}

	@Override
	public void reset() {
		for (Iterator<AbstractSearchType> it = this.visibleFilters.values().iterator(); it.hasNext();) {
			it.next().setVisible(false);
		}
		this.hiddenFilters.putAll(this.visibleFilters);
		this.visibleFilters.clear();
		this.compCriterii.layout();
		this.filtreComposite.getDataTransport().getSelectedMap().clear();
		this.filtreComposite.setTextInfoValue(getDataTransport().getSelectionAsText());

		getViewer().resetFilters();
	}

	public final void computeTotal() {
	}

	private void addFilters(final Map<String, AbstractDB> mapSelectie) {
		if ((mapSelectie == null) || mapSelectie.isEmpty()) {
			return;
		}
		this.filtreComposite.getDataTransport().setSelectedMap(mapSelectie);
		for (Iterator<? extends AbstractDB> it = mapSelectie.values().iterator(); it.hasNext();) {
			BlankDbObject blankDbo = (BlankDbObject) it.next();
			final String criteriuCurent = blankDbo.getName();
			AbstractSearchType st = this.visibleFilters.get(criteriuCurent);
			if (st != null) {
				st.moveAbove(this.compCriterii.getChildren()[0]);
				continue;
			}
			st = this.hiddenFilters.get(criteriuCurent);
			st.moveAbove(this.compCriterii.getChildren()[0]);
			st.setVisible(true);
			this.hiddenFilters.remove(criteriuCurent);
			this.visibleFilters.put(criteriuCurent, st);
		}
	}

	@Override
	public void handleEvent(final Event e) {
		switch (e.type) {
			case SWT.Resize: {
				if ((this.viewer instanceof TableViewer)
						&& (e.widget == ((TableViewer) this.viewer).getTable())) {
					if (this.sashParinte.getMaximizedControl() != null) {
						return;
					}
					this.scrolledComp.setMinSize(this.compCriterii.computeSize(SWT.DEFAULT,
							SWT.DEFAULT));
					this.sashParinte.setMaximizedControl(null);
					this.compCriterii.setVisible(true);
				} else if (e.widget == this.compCriterii) {
					this.scrolledComp.setMinSize(((Composite) e.widget).computeSize(SWT.DEFAULT,
							SWT.DEFAULT));
				} else if ((this.viewer instanceof TreeViewer)
						&& (e.widget == ((TreeViewer) this.viewer).getTree())) {
					if (this.sashParinte.getMaximizedControl() != null) {
						return;
					}
					this.scrolledComp.setMinSize(this.compCriterii.computeSize(SWT.DEFAULT,
							SWT.DEFAULT));
					this.sashParinte.setMaximizedControl(null);
					this.compCriterii.setVisible(true);
				}
				break;
			}
			case SWT.Dispose: {
				saveVisibleFilters();
				break;
			}
			default:
				break;
		}
	}

	private void saveVisibleFilters() {
	}

	private final Map<String, Boolean> getVisibleFiltersMap() {
		if (this.visibleFiltersMap != null) {
			return this.visibleFiltersMap;
		}
		this.visibleFiltersMap = new HashMap<String, Boolean>();
		return this.visibleFiltersMap;
	}

	public final boolean isColumnVisibleInPrefs(final String colName) {
		Object visible = getVisibleFiltersMap().get(colName.toLowerCase());
		if (!(visible instanceof Boolean)) {
			return false;
		}
		return (Boolean) visible;
	}

	public final XButton getSearchButton() {
		return this.buttonCauta;
	}

	public final Composite getCompCriterii() {
		return this.compCriterii;
	}

	public final ColumnViewer getViewer() {
		return this.viewer;
	}

	public final void setViewer(final ColumnViewer viewer) {
		this.viewer = viewer;
		if (this.viewer instanceof TableViewer) {
			((TableViewer) getViewer()).getTable().addListener(SWT.Resize, this);
		} else if (this.viewer instanceof TreeViewer) {
			((TreeViewer) getViewer()).getTree().addListener(SWT.Resize, this);
		}
	}

	public AdbSelectorData getDataTransport() {
		return this.filtreComposite.getDataTransport();
	}

	public void initCacheMap() {
		Map<String, String> cacheMap = new HashMap<>();
		for (Iterator<AbstractSearchType> it = this.hiddenFilters.values().iterator(); it.hasNext();) {
			final String str = it.next().getColName();
			cacheMap.put(str, str);
		}
		for (Iterator<AbstractSearchType> it = this.visibleFilters.values().iterator(); it.hasNext();) {
			final String str = it.next().getColName();
			cacheMap.put(str, str);
		}
		getDataTransport().setCacheMapForString(cacheMap);
		Map<String, AbstractDB> selectedMap = (Map<String, AbstractDB>) getDataTransport().getSelectedMap();
		for (Iterator<AbstractSearchType> it = this.visibleFilters.values().iterator(); it.hasNext();) {
			AbstractSearchType ast = it.next();
			ast.moveAbove(this.compCriterii.getChildren()[0]);
			AbstractDB adb = getDataTransport().getCacheMap().get(Long.valueOf(ast.getColName().hashCode()));
			selectedMap.put(adb.getId(), adb);
		}
		getDataTransport().setSelectedMap(selectedMap);
		this.filtreComposite.setTextInfoValue(getDataTransport().getSelectionAsText());
		this.compCriterii.pack();
	}

	public Object getParentInstance() {
		return this.parentInstance;
	}

	public void setParentInstance(final Object parentInstance) {
		this.parentInstance = parentInstance;
	}

	private void chooseObjects() {
		AdbObjectsCheckView view;
		view = new AdbObjectsCheckView(getShell(), getDataTransport(), null);
		view.open();
		if (view.getUserAction() == SWT.CANCEL) {
			return;
		}
		Map<String, AbstractDB> map = new HashMap<>();
		map.putAll(view.getDataTransport().getSelectedMap());
		reset();
		addFilters(map);
		this.filtreComposite.setTextInfoValue(view.getDataTransport().getSelectionAsText());
		this.compCriterii.pack();
		this.compCriterii.notifyListeners(SWT.Resize, new Event());
	}

	public final AdbSelectorComposite getFiltreComposite() {
		return this.filtreComposite;
	}

	public Map<String, AbstractSearchType> getVisibleFilters() {
		return this.visibleFilters;
	}

	public final int getColumnIndex(final String col) {
		return this.indexMapping.get(col);
	}

	public final String getColumnName(final int columnIndex) {
		return this.indexMappingByPosition.get(columnIndex);
	}

	public void createRatingSearch(final int columnIndex) {
		String colName = getColumnName(columnIndex);
		Map<String, AbstractSearchType> map = isColumnVisibleInPrefs(colName)	? this.visibleFilters
				: this.hiddenFilters;
		map.put(colName, new RatingSearch(this, colName));
	}

	public void createTextSearch(final int columnIndex) {
		String colName = getColumnName(columnIndex);
		Map<String, AbstractSearchType> map = isColumnVisibleInPrefs(colName)	? this.visibleFilters
																				: this.hiddenFilters;
		map.put(colName, new SimpleTextSearch(this, colName));
	}

	public void createNumericSearch(final int columnIndex) {
		String colName = getColumnName(columnIndex);
		Map<String, AbstractSearchType> map = isColumnVisibleInPrefs(colName)	? this.visibleFilters
																				: this.hiddenFilters;
		map.put(colName, new NumericSearch(this, colName));
	}

	public void createCheckBasedSearch(final int columnIndex, final Map<Integer, String> mapChecks) {
		String colName = getColumnName(columnIndex);
		Map<String, AbstractSearchType> map = isColumnVisibleInPrefs(colName)	? this.visibleFilters
																				: this.hiddenFilters;
		map.put(colName, new CheckBasedSearch(this, colName, mapChecks));
	}

	public void createDboSearch(final int columnIndex, final AdbSelectorData data) {
		String colName = getColumnName(columnIndex);
		Map<String, AbstractSearchType> map = isColumnVisibleInPrefs(colName)	? this.visibleFilters
																				: this.hiddenFilters;
		map.put(colName, new DboSearch(this, data, colName));
	}

	public void createDateSearch(final int columnIndex) {
		String colName = getColumnName(columnIndex);
		Map<String, AbstractSearchType> map = isColumnVisibleInPrefs(colName)	? this.visibleFilters
																				: this.hiddenFilters;
		map.put(colName, new TimeStampSearch(this, colName, TimeStampSearch.TYPE_DATE));
	}

	public void createTimeSearch(final int columnIndex) {
		String colName = getColumnName(columnIndex);
		Map<String, AbstractSearchType> map = isColumnVisibleInPrefs(colName)	? this.visibleFilters
																				: this.hiddenFilters;
		map.put(colName, new TimeStampSearch(this, colName, TimeStampSearch.TYPE_TIME));
	}

	public void createTimestampSearch(final int columnIndex) {
		String colName = getColumnName(columnIndex);
		Map<String, AbstractSearchType> map = isColumnVisibleInPrefs(colName)	? this.visibleFilters
																				: this.hiddenFilters;
		map.put(colName, new TimeStampSearch(this, colName, TimeStampSearch.TYPE_TIMESTAMP));
	}

	public final void indexColumns(final String[] cols) {
		for (int i = 0; i < cols.length; i++) {
			this.indexMapping.put(cols[i], i);
			this.indexMappingByPosition.put(i, cols[i]);
		}
	}

}
