package com.papao.books.view;

import com.papao.books.FiltruAplicatie;
import com.papao.books.model.Autor;
import com.papao.books.model.Carte;
import com.papao.books.repository.CarteRepository;
import com.papao.books.repository.UserRepository;
import com.papao.books.view.carte.CarteView;
import com.papao.books.view.menu.PlatformMenu;
import com.papao.books.view.providers.AdbContentProvider;
import com.papao.books.view.searcheable.AbstractSearchType;
import com.papao.books.view.searcheable.BorgSearchSystem;
import com.papao.books.view.user.UsersView;
import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.util.WidgetCursorUtil;
import com.papao.books.view.util.WidgetTableUtil;
import com.papao.books.view.util.sorter.AbstractColumnViewerSorter;
import com.papao.books.view.util.sorter.AbstractTableColumnViewerSorter;
import com.papao.books.view.view.AbstractCViewAdapter;
import com.papao.books.view.view.AbstractView;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.List;

@org.springframework.stereotype.Component
public class EncodePlatform extends AbstractCViewAdapter implements Listener {

    private static Logger logger = Logger.getLogger(EncodePlatform.class);
    private ToolTip appToolTip;
    private Tray appTray;
    private CarteRepository carteRepository;
    private UserRepository userRepository;
    private static ToolBar barDocking;
    private static EncodePlatform instance;
    private CTabFolder mainTabFolder;
    private SashForm verticalSash;
    private Composite compLeftTree;
    private SashForm rightSash;
    private BorgSearchSystem searchSystem;
    private SashForm rightInnerSash;
    private CTabFolder bottomInnerTabFolderRight;
    private TableViewer tableViewer;
    private static final String[] COLS = new String[]{"Autor", "Titlu"};
    private final static int IDX_AUTOR = 0;
    private final static int IDX_TITLU = 1;

    private ToolItem toolItemAdd;
    private ToolItem toolItemMod;
    private ToolItem toolItemDel;
    private ToolItem toolItemRefresh;
    private ToolItem toolItemSearch;
    private Composite compRight;

    @Autowired
    public EncodePlatform(CarteRepository carteRepository, UserRepository userRepository) {
        super(null, AbstractView.MODE_NONE);
        this.carteRepository = carteRepository;
        this.userRepository = userRepository;
        /**
         * linia asta ne scapa de o intrebare tampita, si falsa, cauzata de listenerul pe SWT.Close
         * din AbstractView,
         * de pe shell. Avem nevoie de acest listener, pt a nu repeta codul care salveaza
         * dimensiunea si coordonatele
         * shell-ului, ca sa-l redeschida pe ultimele valori setate, daca e bifata chestia asta in
         * configurare.
         */
        try {
            setExitChoice(SWT.OK);
            setAddCloseListener(true);
            setUseDocking(false);
            instance = this;
            getShell().setMenuBar(PlatformMenu.createShellMenu());

            getShell().setImage(AppImages.getImage16(AppImages.IMG_BORG_MAIN));
            GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).margins(0, 0).spacing(SWT.DEFAULT, 0).applyTo(getShell());
            getShell().addListener(SWT.Close, this);
            getShell().setImages(new Image[]{
                    AppImages.getImage16(AppImages.IMG_BORG_MAIN), AppImages.getImage24(AppImages.IMG_BORG_MAIN),
                    AppImages.getImage32(AppImages.IMG_BORG_MAIN)});

            createTraySystem();

            if (!FiltruAplicatie.isWindowsUsingCoords()) {
                int width;
                int height;

                if (Display.getDefault().getPrimaryMonitor().getBounds().width > 1280) {
                    width = 1280;
                } else if (Display.getDefault().getPrimaryMonitor().getBounds().width > 1024) {
                    width = 1200;
                } else {
                    width = 800;
                }

                if (Display.getDefault().getPrimaryMonitor().getBounds().height > 1024) {
                    height = 800;
                } else if (Display.getDefault().getPrimaryMonitor().getBounds().height > 768) {
                    height = 600;
                } else {
                    height = 480;
                }
                GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).hint(width, height).applyTo(getContainer());
                getShell().setSize(width, height);
            } else {
                GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(getContainer());
            }
            GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).applyTo(getContainer());

            createComponents(getContainer());

            this.barDocking = new ToolBar(getShell(), SWT.FLAT | SWT.WRAP | SWT.RIGHT);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.END).grab(true, false).applyTo(this.barDocking);
            GridLayoutFactory.fillDefaults().margins(0, 0).applyTo(this.barDocking);
            this.barDocking.setMenu(createBarDockingMenu());

        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            closeApplication(true);
        }
    }

    private void createComponents(Composite parent) {

        this.mainTabFolder = new CTabFolder(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(this.mainTabFolder);
        this.mainTabFolder.setSimple(true);
        this.mainTabFolder.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        this.mainTabFolder.setUnselectedImageVisible(true);
        this.mainTabFolder.setUnselectedCloseVisible(false);
        this.mainTabFolder.setMRUVisible(true);
        this.mainTabFolder.setMinimizeVisible(false);
        this.mainTabFolder.setMaximizeVisible(false);
        this.mainTabFolder.setSelectionBackground(ColorUtil.COLOR_WHITE);

        CTabItem booksTabItem = new CTabItem(this.mainTabFolder, SWT.NONE);
        booksTabItem.setText("Carti");
        booksTabItem.setImage(AppImages.getImage32(AppImages.IMG_DETAILS_NEW));
        this.mainTabFolder.setSelection(booksTabItem);

        createTopRightComponents(mainTabFolder);

        verticalSash = new SashForm(mainTabFolder, SWT.HORIZONTAL | SWT.SMOOTH);
        verticalSash.SASH_WIDTH = 4;
        GridDataFactory.fillDefaults().grab(true, true).span(((org.eclipse.swt.layout.GridLayout) getContainer().getLayout()).numColumns,
                1).applyTo(verticalSash);

        compLeftTree = new Composite(verticalSash, SWT.NONE);
        GridDataFactory.fillDefaults().grab(false, true).applyTo(compLeftTree);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(2, 2).extendedMargins(0,
                0,
                0,
                0).spacing(5, 3).applyTo(compLeftTree);

        this.compRight = new Composite(verticalSash, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this.compRight);
        GridLayout lay = new GridLayout(1, false);
        lay.verticalSpacing = 0;
        lay.marginHeight = 2;
        this.compRight.setLayout(lay);

        createBarOps(compRight);

        rightSash = new SashForm(compRight, SWT.SMOOTH | SWT.HORIZONTAL);
        rightSash.SASH_WIDTH = 4;
        GridDataFactory.fillDefaults().grab(true, true).applyTo(rightSash);

        this.verticalSash.setWeights(new int[]{2, 8});
//        this.verticalSash.setMaximizedControl(rightSash);

        searchSystem = new BorgSearchSystem(rightSash);


        rightInnerSash = new SashForm(rightSash, SWT.VERTICAL | SWT.SMOOTH);
        rightInnerSash.SASH_WIDTH = 4;
        rightInnerSash.setLayout(new GridLayout(2, false));
        GridDataFactory.fillDefaults().grab(true, true).applyTo(rightInnerSash);

        rightSash.setWeights(new int[]{2, 8});
        rightSash.setMaximizedControl(rightInnerSash);

        int style = SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.BORDER | SWT.SINGLE;
        this.tableViewer = new TableViewer(rightInnerSash, style);
        this.tableViewer.setUseHashlookup(true);
        this.tableViewer.getTable().setHeaderVisible(true);
        this.tableViewer.getTable().setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this.tableViewer.getControl());
        this.tableViewer.getTable().addListener(SWT.Selection, this);
        this.tableViewer.getTable().addListener(SWT.KeyDown, this);
        this.tableViewer.getTable().addListener(SWT.DefaultSelection, this);
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
//                enableOps();
            }
        });

        this.bottomInnerTabFolderRight = new CTabFolder(rightInnerSash, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(this.bottomInnerTabFolderRight);
        this.bottomInnerTabFolderRight.setSimple(true);
        this.bottomInnerTabFolderRight.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        this.bottomInnerTabFolderRight.setUnselectedImageVisible(true);
        this.bottomInnerTabFolderRight.setUnselectedCloseVisible(false);
        this.bottomInnerTabFolderRight.setMRUVisible(true);
        this.bottomInnerTabFolderRight.setMinimizeVisible(false);
        this.bottomInnerTabFolderRight.setMaximizeVisible(false);
        this.bottomInnerTabFolderRight.setSelectionBackground(ColorUtil.COLOR_WHITE);

        CTabItem tabItemDetaliiCarte = new CTabItem(this.bottomInnerTabFolderRight, SWT.NONE);
        tabItemDetaliiCarte.setText("Detalii carte");
        tabItemDetaliiCarte.setImage(AppImages.getImage16(AppImages.IMG_DETAILS_NEW));
        this.mainTabFolder.setSelection(tabItemDetaliiCarte);

        Label bookTitle = new Label(bottomInnerTabFolderRight, SWT.NONE);
        bookTitle.setText("Test");
        tabItemDetaliiCarte.setControl(bookTitle);

//        createTopRightComponents(mainTabFolder);

        this.rightInnerSash.setWeights(new int[]{8, 5});
//        this.rightInnerSash.setMaximizedControl(topInnerCompRight);

        WidgetCursorUtil.addHandCursorListener(this.tableViewer.getTable());
        SWTeXtension.addColoredFocusListener(this.tableViewer.getTable(), null);

        this.searchSystem.getSearchButton().registerListeners(SWT.MouseUp, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                search();
//                enableOps();
            }
        });

        booksTabItem.setControl(verticalSash);

        this.tableViewer.setInput(carteRepository.findAll());
        getContainer().layout();
    }

    private void createBarOps(Composite parent) {
        ToolBar barOps = new ToolBar(parent, SWT.FLAT);

        this.toolItemAdd = new ToolItem(barOps, SWT.PUSH | SWT.FLAT);
        this.toolItemAdd.setImage(AppImages.getImage16(AppImages.IMG_PLUS));
        this.toolItemAdd.setHotImage(AppImages.getImage16Focus(AppImages.IMG_PLUS));
        this.toolItemAdd.setToolTipText("Adaugare");
        this.toolItemAdd.setText("&Adaugare");
        this.toolItemAdd.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                add();
            }
        });
        this.toolItemMod = new ToolItem(barOps, SWT.PUSH | SWT.FLAT);
        this.toolItemMod.setImage(AppImages.getImage16(AppImages.IMG_MODIFICARE));
        this.toolItemMod.setHotImage(AppImages.getImage16Focus(AppImages.IMG_MODIFICARE));
        this.toolItemMod.setToolTipText("Modificare");
        this.toolItemMod.setText("&Modificare");
        this.toolItemMod.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                modify();
            }
        });
        this.toolItemDel = new ToolItem(barOps, SWT.PUSH | SWT.FLAT);
        this.toolItemDel.setImage(AppImages.getImage16(AppImages.IMG_CANCEL));
        this.toolItemDel.setHotImage(AppImages.getImage16Focus(AppImages.IMG_CANCEL));
        this.toolItemDel.setToolTipText("Stergere");
        this.toolItemDel.setText("Stergere");
        this.toolItemDel.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                delete();
            }
        });
        new ToolItem(barOps, SWT.SEPARATOR);
        this.toolItemSearch = new ToolItem(barOps, SWT.CHECK);
        this.toolItemSearch.setImage(AppImages.getImage16(AppImages.IMG_SEARCH));
        this.toolItemSearch.setHotImage(AppImages.getImage16Focus(AppImages.IMG_SEARCH));
        this.toolItemSearch.setToolTipText("Cautare");
        this.toolItemSearch.setText("Cautare");
        this.toolItemSearch.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                handleSearchDisplay();
            }
        });
        new ToolItem(barOps, SWT.SEPARATOR);
        this.toolItemRefresh = new ToolItem(barOps, SWT.PUSH | SWT.FLAT);
        this.toolItemRefresh.setImage(AppImages.getImage16(AppImages.IMG_REFRESH));
        this.toolItemRefresh.setHotImage(AppImages.getImage16Focus(AppImages.IMG_REFRESH));
        this.toolItemRefresh.setToolTipText("Reactualizare informatii");
        this.toolItemRefresh.setText("Refresh");
        this.toolItemRefresh.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                refresh();
            }
        });
    }

    private void createTopRightComponents(Composite parent) {
        ToolBar bar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT | SWT.WRAP);
        bar.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

        ToolItem item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage24(AppImages.IMG_USER));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_USER));
        item.setToolTipText("Configurare cititori");
        item.setText("Utilizatori");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                configUsers();
            }
        });

        item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage24(AppImages.IMG_STOP));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_STOP));
        item.setToolTipText("Inchidere aplicatie");
        item.setText("Inchide aplicatia");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                performShellClose(new Event());
            }
        });
        this.mainTabFolder.setTopRight(bar);
    }

    public final void createViewerFilters() {
        this.searchSystem.createTextSearch(IDX_AUTOR);
        this.searchSystem.createTextSearch(IDX_TITLU);
    }

    public final void initViewerCols() {
        if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed()) {
            return;
        }

        this.tableViewer.setContentProvider(new AdbContentProvider());
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
                case IDX_AUTOR: {
                    col.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            Carte carte = (Carte) element;
                            if (carte.getAutor() != null) {
                                return carte.getAutor().getNume();
                            }
                            return "";
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.tableViewer, col) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            Carte a = (Carte) e1;
                            Carte b = (Carte) e2;
                            Autor a1 = a.getAutor();
                            Autor b1 = b.getAutor();
                            if (a1 == null) {
                                return 1;
                            } else if (b1 == null) {
                                return -1;
                            }
                            return a1.getNume().compareTo(b1.getNume());
                        }

                    };
                    cSorter.setSorter(cSorter, AbstractColumnViewerSorter.ASC);
                    break;
                }
                case IDX_TITLU: {
                    col.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            Carte carte = (Carte) element;
                            return carte.getTitlu();
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.tableViewer, col) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            Carte a = (Carte) e1;
                            Carte b = (Carte) e2;
                            if (a.getTitlu() == null) {
                                return 1;
                            } else if (b.getTitlu() == null) {
                                return -1;
                            }
                            return a.getTitlu().compareTo(b.getTitlu());
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

    private Menu createTableMenu() {
        return null;
    }

    public void closeApplication(boolean forced) {
        try {
            if (appTray != null) {
                appTray.dispose();
            }
            if (forced) {
                logger.error("forced shutdown sequence initiated..");
                logger.info("**********APPLICATION TERMINATED WITH ERROR**********");
                Display.getDefault().dispose();
                Runtime.getRuntime().exit(-1);
            } else {
                logger.info("normal shutdown sequence initiated..");
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        } finally {
            Display.getDefault().readAndDispatch();// nu sunt sigur daca trebuie sau nu.
            Display.getDefault().dispose();
            Runtime.getRuntime().exit(0);
        }
    }

    private final void createTraySystem() {
        TrayItem item;
        MenuItem menuItem;

        try {
            if (!SystemTray.isSupported()) {
                return;
            }
            getShell().addListener(SWT.Iconify, this);

            final Menu trayItemMenu = new Menu(getShell(), SWT.POP_UP);

            this.appToolTip = new ToolTip(getShell(), SWT.BALLOON | SWT.ICON_INFORMATION);
            this.appToolTip.setAutoHide(true);

            this.appTray = Display.getDefault().getSystemTray();
            if (getAppTray() == null) {
                logger.warn("System tray is not available...");
                return;
            }

            item = new TrayItem(getAppTray(), SWT.NONE);
            item.setToolTipText(getShellText());
            item.setImage(AppImages.getImage16(AppImages.IMG_BORG_MAIN));
            item.setToolTip(this.appToolTip);
            item.addListener(SWT.DefaultSelection, new Listener() {
                @Override
                public void handleEvent(final Event event) {
                    if (getShell().isVisible()) {
                        getShell().setFocus();
                    } else {
                        getShell().open();
                        getShell().setMinimized(false);
                    }
                }
            });

            menuItem = new MenuItem(trayItemMenu, SWT.PUSH);
            menuItem.setText("Afisare Encode Borg");
            menuItem.setImage(AppImages.getImage16(AppImages.IMG_BORG_MAIN));
            menuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(final Event event) {
                    if (getShell().isVisible()) {
                        getShell().setFocus();
                    } else {
                        getShell().open();
                        getShell().setMinimized(false);
                    }
                }
            });
            trayItemMenu.setDefaultItem(menuItem);

            menuItem = new MenuItem(trayItemMenu, SWT.PUSH);
            menuItem.setText("Schimbare firma/data/utilizator");
            menuItem.setImage(AppImages.getImage16(AppImages.IMG_USER));
            menuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event event) {
                    configUsers();
                }
            });

            new MenuItem(trayItemMenu, SWT.SEPARATOR);

            menuItem = new MenuItem(trayItemMenu, SWT.PUSH);
            menuItem.setText("Inchide aplicatia");
            menuItem.setImage(AppImages.getImage16(AppImages.IMG_STOP));
            menuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event event) {
                    performShellClose(event);
                }
            });

            item.addListener(SWT.MenuDetect, new Listener() {
                @Override
                public final void handleEvent(final Event event) {
                    if (!trayItemMenu.isDisposed()) {
                        trayItemMenu.setVisible(true);
                    }
                }
            });

        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public final void performShellClose(Event e) {
        try {
            if (e.type != SWT.Close) {
                e.type = SWT.Close;
            }
            boolean close = SWTeXtension.displayMessageQ("Sunteti sigur ca doriti sa inchideti aplicatia?", "Inchidere aplicatie") == SWT.NO;
            if (close) {
                e.doit = false;
            } else {
                getShell().removeListener(SWT.Close, this);
                getShell().notifyListeners(SWT.Close, e);
                closeApplication(false);
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public final void configUsers() {
        new UsersView(new Shell(), userRepository).open();
    }

    @Override
    public final void handleEvent(final Event e) {
        if (e.type == SWT.Close) {
            if (e.widget == getShell()) {
                performShellClose(e);
            }
        } else if (e.type == SWT.Iconify) {
            if (e.widget == getShell()) {
                getShell().setVisible(false);
            }
        }
    }

    public final void showTrayMessage(final String message) {
        if ((this.appToolTip == null) || this.appToolTip.isDisposed() || StringUtils.isEmpty(message)) {
            return;
        }
        this.appToolTip.setMessage(message);
        this.appToolTip.setVisible(true);
    }

    public final void closeTrayMessage() {
        if ((getAppTray() != null) && (getAppTray().getItemCount() > 0)) {
            getAppTray().getItem(0).setVisible(false);
            getAppTray().getItem(0).setVisible(true);
        }
    }

    public final Tray getAppTray() {
        return this.appTray;
    }

    @Override
    public void customizeView() {
        setShellText("Books Manager");
        setViewOptions(AbstractView.SHOW_OPS_LABELS);
//        setBigViewMessage("12:15. Press return.");
//        setBigViewImage(AppImages.getImage32(AppImages.IMG_HOME));
    }

    public CarteRepository getCarteRepository() {
        return this.carteRepository;
    }

    public boolean add() {
        CarteView view;
        if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed()) {
            return false;
        }
        view = new CarteView(this.tableViewer.getTable().getShell(), new Carte(), carteRepository, AbstractView.MODE_ADD);
        view.open();
        if (view.getUserAction() == SWT.CANCEL) {
            return true;
        }
        refresh();
        return true;
    }

    public boolean delete() {
        try {
            if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed() || (this.tableViewer.getTable().getSelectionCount() <= 0)) {
                return false;
            }
            Carte carte = (Carte) this.tableViewer.getTable().getSelection()[0].getData();
            if (carte == null) {
                SWTeXtension.displayMessageI("Cartea selectata este invalida!");
                return false;
            }
            if (SWTeXtension.displayMessageQ("Sunteti siguri ca doriti sa stergeti cartea selectata?", "Confirmare stergere carte") == SWT.NO) {
                return true;
            }
            carte = carteRepository.findOne(carte.getId());
            if (carte == null) {
                SWTeXtension.displayMessageW("Cartea nu mai exista in baza de date!");
                return false;
            }
            carteRepository.delete(carte);
            List<Carte> input = (List) tableViewer.getInput();
            input.remove(input.indexOf(carte));
            SWTeXtension.displayMessageI("Operatie executata cu succes!");
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return false;
        }
        return true;
    }

    public boolean modify() {
        CarteView view;
        if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed() || (this.tableViewer.getTable().getSelectionCount() <= 0)) {
            return false;
        }
        Carte carte = (Carte) this.tableViewer.getTable().getSelection()[0].getData();
        if (carte == null) {
            SWTeXtension.displayMessageI("Cartea selectata este invalida!");
            return false;
        }
        if (carteRepository.findOne(carte.getId()) == null) {
            SWTeXtension.displayMessageI("Cartea selectata nu mai exista in baza de date!");
            return false;
        }
        view = new CarteView(this.tableViewer.getTable().getShell(), carte, carteRepository, AbstractView.MODE_MODIFY);
        view.open();
        if (view.getUserAction() == SWT.CANCEL) {
            return true;
        }
        refresh();
        return true;
    }

    public void search() {
        this.tableViewer.resetFilters();
        java.util.List<ViewerFilter> listFilters = new ArrayList<ViewerFilter>();
        for (Iterator<AbstractSearchType> it = this.searchSystem.getVisibleFilters().values().iterator(); it.hasNext(); ) {
            ViewerFilter filter = null;
            final AbstractSearchType searchType = it.next();
            if (!searchType.isModified()) {
                continue;
            }
            switch (this.searchSystem.getColumnIndex(searchType.getColName())) {
                case IDX_AUTOR: {
                    filter = new ViewerFilter() {
                        @Override
                        public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
                            Carte carte = (Carte) element;
                            return searchType.compareValues(carte.getAutor());
                        }
                    };
                    break;
                }
                case IDX_TITLU: {
                    filter = new ViewerFilter() {
                        @Override
                        public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
                            Carte carte = (Carte) element;
                            return searchType.compareValues(carte.getTitlu());
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

    public void view() {
        if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed() || (this.tableViewer.getTable().getSelectionCount() <= 0)) {
            return;
        }
        Carte carte = (Carte) this.tableViewer.getTable().getSelection()[0].getData();
        if (carte == null) {
            SWTeXtension.displayMessageI("Cartea selectata este invalida!");
            return;
        }
        if (carteRepository.findOne(carte.getId()) == null) {
            SWTeXtension.displayMessageI("Cartea selectata nu mai exista in baza de date!");
            return;
        }
        new CarteView(this.tableViewer.getTable().getShell(), carte, carteRepository, AbstractView.MODE_VIEW).open();
    }

    public void refresh() {
        this.tableViewer.setInput(carteRepository.findAll());
    }

    public void handleSearchDisplay() {
        if (toolItemSearch.getSelection()) {
            this.rightSash.setMaximizedControl(null);
        } else {
            this.rightSash.setMaximizedControl(rightInnerSash);
        }
    }

    public static ToolBar getBarDocking() {
        return barDocking;
    }

    private Menu createBarDockingMenu() {
        final Menu menu = new Menu(this.barDocking);
        menu.addListener(SWT.Show, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                menu.getItem(0).setEnabled(barDocking.getItemCount() > 0);
                menu.getItem(1).setEnabled(barDocking.getItemCount() > 0);
                menu.getItem(2).setEnabled(barDocking.getItemCount() > 0);
            }
        });

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Inchidere ferestre");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                for (ToolItem it : barDocking.getItems()) {
                    if (it.getData() instanceof AbstractView) {
                        ((AbstractView) it.getData()).close(SWT.CANCEL);
                    }
                    it.dispose();
                }
                barDocking.layout();
                barDocking.getParent().layout();
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_SIMPLE_MAXIMIZE));
        item.setText("Afisare ferestre");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                for (ToolItem it : barDocking.getItems()) {
                    if (it.getData() instanceof AbstractView) {
                        ((AbstractView) it.getData()).getDockingItem().notifyListeners(SWT.Selection,
                                new Event());
                    }
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Minimizare ferestre");
        item.setImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_SIMPLE_MINIMIZE));
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                for (ToolItem it : barDocking.getItems()) {
                    if (it.getData() instanceof AbstractView) {
                        ((AbstractView) it.getData()).getShell().setMinimized(true);
                    }
                }
            }
        });

        return menu;
    }

    public UserRepository getUserRepository() {
        return this.userRepository;
    }

    public static EncodePlatform getInstance() {
        return instance;
    }
}
