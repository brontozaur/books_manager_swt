package com.papao.books.view.carte;

import com.papao.books.controller.AutorController;
import com.papao.books.model.AbstractMongoDB;
import com.papao.books.model.Autor;
import com.papao.books.view.AppImages;
import com.papao.books.view.bones.impl.view.AbstractCView;
import com.papao.books.view.interfaces.*;
import com.papao.books.view.providers.AdbMongoContentProvider;
import com.papao.books.view.searcheable.AbstractSearchType;
import com.papao.books.view.searcheable.BorgSearchSystem;
import com.papao.books.view.util.StringUtil;
import com.papao.books.view.util.WidgetCursorUtil;
import com.papao.books.view.util.WidgetTableUtil;
import com.papao.books.view.util.sorter.AbstractColumnViewerSorter;
import com.papao.books.view.util.sorter.AbstractTableColumnViewerSorter;
import com.papao.books.view.view.AbstractView;
import com.papao.books.view.view.SWTeXtension;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;

public class AutoriView extends AbstractCView implements IEncodeRefresh, IAdd, IModify, IDelete, IEncodeSearch {

    private static Logger logger = LoggerFactory.getLogger(AutoriView.class);

    private static final String[] COLS = new String[]{"Nume"};

    private final static int IDX_NUME = 0;

    protected TableViewer tableViewer;
    private SashForm sash;
    private Composite compRight;
    protected BorgSearchSystem searchSystem;
    private AutorController autorController;

    public AutoriView(final Shell parent, AutorController autorController) {
        super(parent, AbstractView.MODE_NONE);
        this.autorController = autorController;

        getShell().setText("Autori");
        getShell().setImage(AppImages.getImage16(AppImages.IMG_CONFIG));

        addComponents();

        this.tableViewer.setInput(this.autorController.findAll());
        this.tableViewer.getTable().setFocus();
    }

    @Override
    public final boolean add() {
        AutorView view;
        if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed()) {
            return false;
        }
        view = new AutorView(this.tableViewer.getTable().getShell(), new Autor(), autorController, AbstractView.MODE_ADD);
        view.open();
        if (view.getUserAction() == SWT.CANCEL) {
            return true;
        }
        refresh();
        return true;
    }

    @Override
    public final boolean modify() {
        AutorView view;
        if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed() || (this.tableViewer.getTable().getSelectionCount() <= 0)) {
            return false;
        }
        Autor autor = (Autor) this.tableViewer.getTable().getSelection()[0].getData();
        if (autor == null) {
            SWTeXtension.displayMessageI("Autorul selectat este invalid!");
            return false;
        }
        if (autorController.findOne(autor.getId()) == null) {
            SWTeXtension.displayMessageI("Autorul selectat este invalid!");
            return false;
        }
        view = new AutorView(this.tableViewer.getTable().getShell(), autor, autorController, AbstractView.MODE_MODIFY);
        view.open();
        if (view.getUserAction() == SWT.CANCEL) {
            return true;
        }
        refresh();
        return true;
    }

    @Override
    public final boolean delete() {
        try {
            if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed() || (this.tableViewer.getTable().getSelectionCount() <= 0)) {
                return false;
            }
            Autor autor = (Autor) this.tableViewer.getTable().getSelection()[0].getData();
            if (autor == null) {
                SWTeXtension.displayMessageI("Autorul selectat este invalid!");
                return false;
            }
            if (SWTeXtension.displayMessageQ("Sunteti siguri ca doriti sa stergeti autorul selectat?", "Confirmare stergere autor") == SWT.NO) {
                return true;
            }
            autor = autorController.findOne(autor.getId());
            if (autor == null) {
                SWTeXtension.displayMessageW("Autorul nu mai exista!");
                return false;
            }
            //TODO foreign key checks!!
            autorController.delete(autor);
            tableViewer.setInput(autorController.findAll());
            SWTeXtension.displayMessageI("Operatie executata cu succes!");
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return false;
        }
        return true;
    }

    public final void view() {
        if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed() || (this.tableViewer.getTable().getSelectionCount() <= 0)) {
            return;
        }
        Autor autor = (Autor) this.tableViewer.getTable().getSelection()[0].getData();
        if (autor == null) {
            SWTeXtension.displayMessageI("Autor selectat este invalid!");
            return;
        }
        if (autorController.findOne(autor.getId()) == null) {
            SWTeXtension.displayMessageI("Autor selectat este invalid!");
            return;
        }
        new AutorView(this.tableViewer.getTable().getShell(), autor, autorController, AbstractView.MODE_VIEW).open();
    }

    @Override
    public void refresh() {
        this.tableViewer.setInput(autorController.findAll());
    }

    @Override
    protected void customizeView() {
        setShellStyle(SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE);
        setViewOptions(AbstractView.ADD_CANCEL | AbstractView.SHOW_OPS_LABELS);
        setBigViewMessage("Configurare autori");
        setBigViewImage(AppImages.getImage24(AppImages.IMG_USER));
    }

    @Override
    protected boolean validate() {
        return false;
    }

    @Override
    protected void saveData() {

    }

    protected final void enableOps() {
        boolean enable = true;
        if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed()) {
            return;
        }
        if (this.tableViewer.getTable().getSelectionCount() == 0) {
            enable = false;
        } else {
            enable = this.tableViewer.getTable().getSelection()[0].getData() instanceof AbstractMongoDB;
        }
        getToolItemAdd().setEnabled(true); // add
        getToolItemMod().setEnabled(enable); // mod
        getToolItemDel().setEnabled(enable); // del
    }

    protected Menu createTableMenu() {
        if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed()) {
            return null;
        }
        final Menu menu = new Menu(this.tableViewer.getTable());
        MenuItem menuItem;
        menu.addListener(SWT.Show, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                int idx = 0;
                final int selIdx = tableViewer.getTable().getSelectionIndex();
                idx++; // refresh
                idx++; // separator
                idx++; // add
                menu.getItem(idx++).setEnabled(selIdx != -1); // mod
                menu.getItem(idx++).setEnabled(selIdx != -1); // del
                idx++; // sep
                menu.getItem(idx++).setEnabled(selIdx != -1); // view
            }
        });

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Refresh   F5");
        menuItem.setImage(AppImages.getImage16(AppImages.IMG_REFRESH));
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                refresh();
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Adaugare");
        menuItem.setImage(AppImages.getImage16(AppImages.IMG_PLUS));
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                add();
                enableOps();
            }
        });

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Modificare");
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                modify();
                enableOps();
            }
        });

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Stergere  Del");
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                delete();
                enableOps();
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Vizualizare   Enter");
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                view();
            }
        });
        return menu;
    }

    @Override
    public final void search() {
        this.tableViewer.resetFilters();
        java.util.List<ViewerFilter> listFilters = new ArrayList<ViewerFilter>();
        for (Iterator<AbstractSearchType> it = this.searchSystem.getVisibleFilters().values().iterator(); it.hasNext(); ) {
            ViewerFilter filter = null;
            final AbstractSearchType searchType = it.next();
            if (!searchType.isModified()) {
                continue;
            }
            switch (this.searchSystem.getColumnIndex(searchType.getColName())) {
                case IDX_NUME: {
                    filter = new ViewerFilter() {
                        @Override
                        public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
                            Autor autor = (Autor) element;
                            return searchType.compareValues(autor.getNumeComplet());
                        }
                    };
                    break;
                }
                default:
            }
            if (filter != null) {
                listFilters.add(filter);
                searchType.filter = filter;
            }
        }
        this.tableViewer.setFilters(listFilters.toArray(new ViewerFilter[listFilters.size()]));
    }

    private void addComponents() {
        int style = SWT.SMOOTH | SWT.HORIZONTAL;
        this.sash = new SashForm(getContainer(), style);
        this.sash.SASH_WIDTH = 4;
        GridDataFactory.fillDefaults().grab(true, true).minSize(350, 250).applyTo(this.sash);

        this.searchSystem = new BorgSearchSystem(this.sash);
        this.searchSystem.setParentInstance(this);

        this.compRight = new Composite(this.sash, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this.compRight);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(0, 0).applyTo(this.compRight);

        style = SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.BORDER | SWT.SINGLE;
        this.tableViewer = new TableViewer(this.compRight, style);
        this.tableViewer.setUseHashlookup(true);
        this.tableViewer.getTable().setHeaderVisible(true);
        this.tableViewer.getTable().setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).hint(600, 400).applyTo(this.tableViewer.getControl());
        this.tableViewer.getTable().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                enableOps();
            }
        });
        this.tableViewer.getTable().addListener(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (e.keyCode == SWT.F3) {
                    handleSearchDisplay(true);
                }
                if (e.character == SWT.DEL) {
                    delete();
                }
                if (e.keyCode == SWT.F5) {
                    refresh();
                }
            }
        });
        this.tableViewer.getTable().addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                modify();
            }
        });
        this.tableViewer.getTable().setMenu(createTableMenu());

        initViewerCols();
        WidgetTableUtil.customizeTable(this.tableViewer.getTable(), getClass());

        this.searchSystem.setViewer(this.tableViewer);
        this.searchSystem.indexColumns(COLS);
        createViewerFilters();
        this.searchSystem.initCacheMap();

        this.searchSystem.getSearchButton().registerListeners(SWT.MouseUp, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                search();
                enableOps();
            }
        });

        this.sash.setWeights(new int[]{
                5, 8});
        this.sash.setMaximizedControl(this.compRight);

        WidgetCursorUtil.addHandCursorListener(this.tableViewer.getTable());
        SWTeXtension.addColoredFocusListener(this.tableViewer.getTable(), null);

        getToolItemSearch().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                handleSearchDisplay(false);
            }
        });
    }

    public final void createViewerFilters() {
        this.searchSystem.createTextSearch(IDX_NUME);
    }

    public final void initViewerCols() {
        if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed()) {
            return;
        }

        this.tableViewer.setContentProvider(new AdbMongoContentProvider());
        int[] dims = new int[]{250, 250};
        int[] aligns = new int[]{SWT.LEFT, SWT.LEFT};
        boolean[] visible = new boolean[]{true, true};
        for (int i = 0; i < COLS.length; i++) {
            final TableViewerColumn col = new TableViewerColumn(this.tableViewer, SWT.NONE);
            col.getColumn().setText(COLS[i]);
            col.getColumn().setWidth(visible[i] ? dims[i] : 0);
            col.getColumn().setAlignment(aligns[i]);
            col.getColumn().setResizable(visible[i]);
            col.getColumn().setMoveable(true);
            switch (i) {
                case IDX_NUME: {
                    col.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            Autor autor = (Autor) element;
                            return autor.getNumeComplet();
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.tableViewer, col) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            Autor a = (Autor) e1;
                            Autor b = (Autor) e2;
                            return StringUtil.romanianCompare(a.getNumeComplet(), b.getNumeComplet());
                        }

                    };
                    cSorter.setSorter(cSorter, AbstractColumnViewerSorter.ASC);
                    break;
                }
                default:
            }
        }
        this.tableViewer.getTable().setSortColumn(null);
    }

    @Override
    public final void handleSearchDisplay(final boolean isCodeSelection) {
        if (isCodeSelection) {
            getToolItemSearch().setSelection(!getToolItemSearch().getSelection());
        }
        if (getToolItemSearch().getSelection()) {
            this.sash.setMaximizedControl(null);
        } else {
            this.sash.setMaximizedControl(this.compRight);
        }
    }
}
