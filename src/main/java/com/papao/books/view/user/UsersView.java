package com.papao.books.view.user;

import com.papao.books.model.AbstractDB;
import com.papao.books.model.User;
import com.papao.books.repository.UserRepository;
import com.papao.books.view.AppImages;
import com.papao.books.view.bones.impl.view.AbstractCView;
import com.papao.books.view.interfaces.*;
import com.papao.books.view.providers.AdbContentProvider;
import com.papao.books.view.searcheable.AbstractSearchType;
import com.papao.books.view.searcheable.BorgSearchSystem;
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

import java.util.*;

public class UsersView extends AbstractCView implements IEncodeRefresh, IAdd, IModify, IDelete, IEncodeSearch {

    private static Logger logger = LoggerFactory.getLogger(UsersView.class);

    private static final String[] COLS = new String[]{"Nume", "Prenume"};

    private final static int IDX_NUME = 0;
    private final static int IDX_PRENUME = 1;

    protected TableViewer tableViewer;
    private SashForm sash;
    private Composite compRight;
    protected BorgSearchSystem searchSystem;
    private UserRepository userRepository;

    public UsersView(final Shell parent, UserRepository userRepository) {
        super(parent, AbstractView.MODE_NONE);
        this.userRepository = userRepository;

        getShell().setText("Utilizatori aplicatie");
        getShell().setImage(AppImages.getImage16(AppImages.IMG_CONFIG));

        addComponents();

        this.tableViewer.setInput(userRepository.findAll());
    }

    @Override
    public final boolean add() {
        UserView view;
        if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed()) {
            return false;
        }
        view = new UserView(this.tableViewer.getTable().getShell(), new User(), userRepository, AbstractView.MODE_ADD);
        view.open();
        if (view.getUserAction() == SWT.CANCEL) {
            return true;
        }
        refresh();
        return true;
    }

    @Override
    public final boolean modify() {
        UserView view;
        if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed() || (this.tableViewer.getTable().getSelectionCount() <= 0)) {
            return false;
        }
        User usr = (User) this.tableViewer.getTable().getSelection()[0].getData();
        if (usr == null) {
            SWTeXtension.displayMessageI("Utilizatorul selectat este invalid!");
            return false;
        }
        if (userRepository.findOne(usr.getId()) == null) {
            SWTeXtension.displayMessageI("Utilizatorul selectat este invalid!");
            return false;
        }
        view = new UserView(this.tableViewer.getTable().getShell(), usr, userRepository, AbstractView.MODE_MODIFY);
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
            User usr = (User) this.tableViewer.getTable().getSelection()[0].getData();
            if (usr == null) {
                SWTeXtension.displayMessageI("Utilizatorul selectat este invalid!");
                return false;
            }
            if (SWTeXtension.displayMessageQ("Sunteti siguri ca doriti sa stergeti utilizatorul selectat?", "Confirmare stergere utilizator") == SWT.NO) {
                return true;
            }
            usr = userRepository.findOne(usr.getId());
            if (usr == null) {
                SWTeXtension.displayMessageW("Utilizatorul nu mai exista!");
                return false;
            }
            userRepository.delete(usr);
            java.util.List<User> input = (java.util.List)tableViewer.getInput();
            input.remove(input.indexOf(usr));
//            this.tableViewer.setInput(input);
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
        User usr = (User) this.tableViewer.getTable().getSelection()[0].getData();
        if (usr == null) {
            SWTeXtension.displayMessageI("Utilizatorul selectat este invalid!");
            return;
        }
        if (userRepository.findOne(usr.getId()) == null) {
            SWTeXtension.displayMessageI("Utilizatorul selectat este invalid!");
            return;
        }
        new UserView(this.tableViewer.getTable().getShell(), usr, userRepository, AbstractView.MODE_VIEW).open();
    }
    @Override
    public void refresh() {
        this.tableViewer.setInput(userRepository.findAll());
    }

    @Override
    protected void customizeView() {
        setShellStyle(SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE);
        setViewOptions(AbstractView.ADD_CANCEL | AbstractView.SHOW_OPS_LABELS);
        setBigViewMessage("Configurare utilizatori aplicatie");
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
            enable = this.tableViewer.getTable().getSelection()[0].getData() instanceof AbstractDB;
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
        for (Iterator<AbstractSearchType> it = this.searchSystem.getVisibleFilters().values().iterator(); it.hasNext();) {
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
                            User usr = (User) element;
                            return searchType.compareValues(usr.getNume());
                        }
                    };
                    break;
                }
                case IDX_PRENUME: {
                    filter = new ViewerFilter() {
                        @Override
                        public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
                            User usr = (User) element;
                            return searchType.compareValues(usr.getPrenume());
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
        GridDataFactory.fillDefaults().grab(true, true).hint(480, 320).applyTo(this.tableViewer.getControl());
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
        this.searchSystem.createTextSearch(IDX_PRENUME);
    }

    public final void initViewerCols() {
        if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed()) {
            return;
        }

        this.tableViewer.setContentProvider(new AdbContentProvider());
        int[] dims = new int[]{250, 250};
        int[] aligns = new int[]{SWT.LEFT, SWT.LEFT};
        boolean[] visible = new boolean[]{true, true};
        for (int i = 0; i < UsersView.COLS.length; i++) {
            final TableViewerColumn col = new TableViewerColumn(this.tableViewer, SWT.NONE);
            col.getColumn().setText(UsersView.COLS[i]);
            col.getColumn().setWidth(visible[i] ? dims[i] : 0);
            col.getColumn().setAlignment(aligns[i]);
            col.getColumn().setResizable(visible[i]);
            col.getColumn().setMoveable(true);
            switch (i) {
                case IDX_NUME: {
                    col.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            User usr = (User) element;
                            return usr.getNume();
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.tableViewer, col) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            User a = (User) e1;
                            User b = (User) e2;
                            return a.getNume().compareTo(b.getNume());
                        }

                    };
                    cSorter.setSorter(cSorter, AbstractColumnViewerSorter.ASC);
                    break;
                }
                case IDX_PRENUME: {
                    col.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            User usr = (User) element;
                            return usr.getPrenume();
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.tableViewer, col) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            User a = (User) e1;
                            User b = (User) e2;
                            return a.getPrenume().compareTo(b.getPrenume());
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
