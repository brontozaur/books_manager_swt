package com.papao.books.ui;

import com.novocode.naf.swt.custom.BalloonNotification;
import com.novocode.naf.swt.custom.LiveSashForm;
import com.papao.books.ApplicationService;
import com.papao.books.BooksApplication;
import com.papao.books.config.BooleanSetting;
import com.papao.books.controller.*;
import com.papao.books.export.VizualizareRapoarte;
import com.papao.books.model.AbstractMongoDB;
import com.papao.books.model.Carte;
import com.papao.books.model.config.TableSetting;
import com.papao.books.ui.auth.EncodeLive;
import com.papao.books.ui.carte.AutorView;
import com.papao.books.ui.carte.AutoriView;
import com.papao.books.ui.carte.CarteView;
import com.papao.books.ui.config.AppConfigView;
import com.papao.books.ui.custom.*;
import com.papao.books.ui.menu.PlatformMenu;
import com.papao.books.ui.preluari.AutoriImportView;
import com.papao.books.ui.preluari.BookImportView;
import com.papao.books.ui.providers.AdbMongoContentProvider;
import com.papao.books.ui.providers.UnifiedStyledLabelProvider;
import com.papao.books.ui.providers.tree.IntValuePair;
import com.papao.books.ui.providers.tree.IntValuePairsWrapper;
import com.papao.books.ui.providers.tree.SimpleTextNode;
import com.papao.books.ui.providers.tree.TreeContentProvider;
import com.papao.books.ui.searcheable.BookSearchType;
import com.papao.books.ui.user.UsersView;
import com.papao.books.ui.util.*;
import com.papao.books.ui.util.sorter.AbstractColumnViewerSorter;
import com.papao.books.ui.util.sorter.AbstractTableColumnViewerSorter;
import com.papao.books.ui.util.sorter.AbstractTreeColumnViewerSorter;
import com.papao.books.ui.view.AbstractCViewAdapter;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class EncodePlatform extends AbstractCViewAdapter implements Listener, Observer {

    private static Logger logger = Logger.getLogger(EncodePlatform.class);
    private ToolTip appToolTip;
    private Tray appTray;
    private static ToolBar barDocking;
    private static EncodePlatform instance;
    private CBanner mainCBanner;
    private LiveSashForm verticalSash;
    private Composite compLeftTree;
    private LiveSashForm rightInnerSash;
    private CTabFolder bottomInnerTabFolderRight;
    private TableViewer tableViewer;
    private CTabFolder mainRightTabFolder;

    private static final String[] COLS = new String[]{"Autor", "Titlu", "Rating", "Editura", "An aparitie", "Limba"};
    private final static int IDX_AUTOR = 0;
    private final static int IDX_TITLU = 1;
    private final static int IDX_RATING = 2;
    private final static int IDX_EDITURA = 3;
    private final static int IDX_AN_APARITIE = 4;
    private final static int IDX_LIMBA = 5;

    private ToolItem toolItemAdd;
    private ToolItem toolItemMod;
    private ToolItem toolItemClone;
    private ToolItem toolItemDel;
    private ToolItem toolItemRefresh;
    private ToolItem toolItemGrupare;
    private Composite compRight;
    private UnifiedStyledLabelProvider leftTreeColumnProvider;
    private TreeViewer leftTreeViewer;

    private PaginationComposite paginationComposite;
    private BookSearchType searchType = BookSearchType.AUTOR;
    private Combo comboModAfisare;
    private DragAndDropTableComposite dragAndDropTableComposite;
    private LiveSashForm rightVerticalSash;
    private BookReadOnlyDetailsComposite readOnlyDetailsComposite;
    private ImageGalleryComposite galleryComposite;
    private ProgressBarComposite progressBarComposite;
    private ToolItem itemImport;
    private ToolItem itemConfig;
    private static final String TREE_KEY = "leftTreeViewer";
    private static final String TABLE_KEY = "booksViewer";
    private Text searchText;

    public EncodePlatform() {
        super(null, AbstractView.MODE_NONE);
        ApplicationService.getBookController().addObserver(this);
        /*
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
            getShell().setImages(new Image[]{AppImages.getImage32(AppImages.IMG_BORG_MAIN)});

            createTraySystem();

            if (!SettingsController.getBoolean(BooleanSetting.WINDOWS_USE_COORDS)) {
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
            BooksApplication.closeApplication(true);
        }
    }

    /*
        This is needed because the current class is injected and therefore it's
        constructor executed before the login (and therefore the current user detection)
        is performed. Since there are no current user at population time, and therefore
        the user specific data (e.g. user ratings) cannot be displayed.
     */
    @Override
    public void open() {
        fullRefresh();
        super.open();
    }

    private Menu createMainRightTabFolderMenu() {
        final Menu tabFolderMenu = new Menu(mainRightTabFolder.getShell(), SWT.POP_UP);
        MenuItem item = new MenuItem(tabFolderMenu, SWT.PUSH);
        item.setText("Afiseaza galerie");
        tabFolderMenu.addListener(SWT.Show, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                tabFolderMenu.getItem(0).setEnabled(mainRightTabFolder.getItemCount() == 1);
            }
        });
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                recreateGalleryTab();
            }
        });
        return tabFolderMenu;
    }

    private void recreateGalleryTab() {
        CTabItem galleryTab = createTabGallery(mainRightTabFolder);
        galleryTab.setControl(galleryComposite.getContent());
        ApplicationService.getBookController().addObserver(galleryComposite);
        galleryComposite.update(ApplicationService.getBookController(), null);
    }

    private void createComponents(Composite parent) {

        this.mainCBanner = new CBanner(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(this.mainCBanner);
        this.mainCBanner.setSimple(false);
        this.mainCBanner.setRight(createTopRightComponents(mainCBanner));

        this.mainCBanner.setLeft(createBarOps(mainCBanner));

        verticalSash = new LiveSashForm(parent, SWT.HORIZONTAL | SWT.SMOOTH);
        verticalSash.sashWidth = 4;
        GridDataFactory.fillDefaults().grab(true, true).span(((org.eclipse.swt.layout.GridLayout) getContainer().getLayout()).numColumns,
                1).applyTo(verticalSash);

        createCompLeftTree(verticalSash);

        this.compRight = new Composite(verticalSash, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this.compRight);
        GridLayoutFactory.fillDefaults().numColumns(1).spacing(0, 0).extendedMargins(0, 0, 0, 2).applyTo(compRight);

        rightVerticalSash = new LiveSashForm(compRight, SWT.HORIZONTAL | SWT.SMOOTH);
        rightVerticalSash.sashWidth = 4;
        GridDataFactory.fillDefaults().grab(true, true).applyTo(rightVerticalSash);

        this.mainRightTabFolder = new CTabFolder(rightVerticalSash, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(this.mainRightTabFolder);
        this.mainRightTabFolder.setSimple(true);
        this.mainRightTabFolder.setUnselectedImageVisible(true);
        this.mainRightTabFolder.setUnselectedCloseVisible(true);
        this.mainRightTabFolder.setMRUVisible(true);
        this.mainRightTabFolder.setMinimizeVisible(false);
        this.mainRightTabFolder.setMaximizeVisible(false);
        mainRightTabFolder.setSelectionBackground(ColorUtil.COLOR_SYSTEM);

        mainRightTabFolder.setMenu(createMainRightTabFolderMenu());

        CTabItem tabGrid = new CTabItem(this.mainRightTabFolder, SWT.NONE);
        tabGrid.setText("Lista");
        tabGrid.setImage(AppImages.getImage16(AppImages.IMG_LISTA));
        this.mainRightTabFolder.setSelection(tabGrid);

        Composite mainCompRightTab = new Composite(mainRightTabFolder, SWT.NONE);
        mainCompRightTab.setLayout(new GridLayout(4, false));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(mainCompRightTab);

        final Combo comboSearch = new Combo(mainCompRightTab, SWT.READ_ONLY);
        comboSearch.setItems(new String[]{"tabela", "baza de date"});
        comboSearch.select(1);

        searchText = new Text(mainCompRightTab, SWT.SEARCH);
        searchText.setMessage("cautare dupa...");
        GridDataFactory.fillDefaults().hint(150, SWT.DEFAULT).applyTo(searchText);
        searchText.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if ("tabela".equals(comboSearch.getText())) {
                    if (searchText.getText().isEmpty()) {
                        resetSearchFilters();
                        return;
                    }
                    searchInTable(searchText.getText());
                }
            }
        });
        searchText.addListener(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (event.character == SWT.CR) {
                    handleTableSearch(comboSearch);
                }
            }
        });

        ToolItem itemSearch = new ToolItem(new ToolBar(mainCompRightTab, SWT.FLAT | SWT.RIGHT), SWT.RIGHT);
        itemSearch.setImage(AppImages.getImage16(AppImages.IMG_SEARCH));
        itemSearch.setHotImage(AppImages.getImage16Focus(AppImages.IMG_SEARCH));
        itemSearch.setText("Cautare");
        itemSearch.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                handleTableSearch(comboSearch);
            }
        });

        progressBarComposite = new ProgressBarComposite(mainCompRightTab, SWT.SMOOTH);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.END, SWT.CENTER).applyTo(progressBarComposite);
        mainRightTabFolder.setTopRight(mainCompRightTab);

        CTabItem tabGallery = createTabGallery(mainRightTabFolder);

        galleryComposite = new ImageGalleryComposite(mainRightTabFolder, progressBarComposite);
        tabGallery.setControl(galleryComposite.getContent());

        this.verticalSash.setWeights(new int[]{2, 8});
//        this.verticalSash.setMaximizedControl(rightSash);

        rightInnerSash = new LiveSashForm(mainRightTabFolder, SWT.VERTICAL | SWT.SMOOTH);
        rightInnerSash.sashWidth = 4;
        rightInnerSash.setLayout(new GridLayout(2, false));
        GridDataFactory.fillDefaults().grab(true, true).applyTo(rightInnerSash);

        int style = SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI;
        this.tableViewer = new TableViewer(rightInnerSash, style);
        this.tableViewer.setUseHashlookup(true);
        this.tableViewer.getTable().setHeaderVisible(true);
        this.tableViewer.getTable().setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this.tableViewer.getControl());
        this.tableViewer.getTable().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                enableOps();
                displayBookData();
            }
        });
        this.tableViewer.getTable().addListener(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (SWTeXtension.getDeleteTrigger(e)) {
                    delete();
                }
                if (e.keyCode == SWT.F5) {
                    refreshTableViewer();
                }
            }
        });
        this.tableViewer.getTable().addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                modify(false);
            }
        });
        this.tableViewer.getTable().setMenu(createTableMenu());

        initViewerCols();
        WidgetTableUtil.customizeTable(this.tableViewer.getTable(), getClass(), TABLE_KEY);

        this.bottomInnerTabFolderRight = new CTabFolder(rightInnerSash, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(this.bottomInnerTabFolderRight);
        this.bottomInnerTabFolderRight.setSimple(true);
        this.bottomInnerTabFolderRight.setUnselectedImageVisible(true);
        this.bottomInnerTabFolderRight.setUnselectedCloseVisible(false);
        this.bottomInnerTabFolderRight.setMRUVisible(true);
        this.bottomInnerTabFolderRight.setMinimizeVisible(false);
        this.bottomInnerTabFolderRight.setMaximizeVisible(false);
        bottomInnerTabFolderRight.setSelectionBackground(ColorUtil.COLOR_SYSTEM);

        CTabItem tabItemDetaliiCarte = new CTabItem(this.bottomInnerTabFolderRight, SWT.NONE);
        tabItemDetaliiCarte.setText("Documente");
        tabItemDetaliiCarte.setImage(AppImages.getImage16(AppImages.IMG_DETAILS_NEW));
        this.bottomInnerTabFolderRight.setSelection(tabItemDetaliiCarte);

        tabItemDetaliiCarte.setControl(createTabDocuments(bottomInnerTabFolderRight));

        this.rightInnerSash.setWeights(new int[]{8, 5});
        tabGrid.setControl(rightInnerSash);

        readOnlyDetailsComposite = new BookReadOnlyDetailsComposite(rightVerticalSash);
        //table viewer is notified when rating changes on the details composite
        readOnlyDetailsComposite.addObserver(this);
        galleryComposite.addObserver(this);
        galleryComposite.addObserver(readOnlyDetailsComposite);

        rightVerticalSash.setWeights(new int[]{9, 3});
        rightVerticalSash.setMaximizedControl(null);

        paginationComposite = new PaginationComposite(compRight);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(paginationComposite);

        WidgetCursorUtil.addHandCursorListener(this.tableViewer.getTable());
        SWTeXtension.addColoredFocusListener(this.tableViewer.getTable(), null);

        getContainer().layout();
    }

    private CTabItem createTabGallery(CTabFolder parent) {
        final CTabItem tabGallery = new CTabItem(parent, SWT.CLOSE);
        tabGallery.setText("Galerie");
        tabGallery.setShowClose(true);
        tabGallery.setImage(AppImages.getImage16(AppImages.IMG_SHOW));
        parent.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                event.doit = true;
                displayBookData();
            }
        });
        parent.addCTabFolder2Listener(new CTabFolder2Adapter() {
            public void close(CTabFolderEvent event) {
                if (event.item.equals(tabGallery)) {
                    ApplicationService.getBookController().deleteObserver(galleryComposite);
                }
            }
        });
        return tabGallery;
    }

    private void resetSearchFilters() {
        tableViewer.resetFilters();
        ((UnifiedStyledLabelProvider) tableViewer.getLabelProvider(IDX_AUTOR)).setSearchText("");
        ((UnifiedStyledLabelProvider) tableViewer.getLabelProvider(IDX_TITLU)).setSearchText("");
        refreshTableViewer();
    }

    private void handleTableSearch(Combo comboSearch) {
        if (searchText.getText().isEmpty()) {
            resetSearchFilters();
            return;
        }
        if (searchText.getText().length() == 1) {
            BalloonNotification.showNotification(searchText, "Notificare", "Introduceti minim 2 caractere!", 1500);
            return;
        }
        if ("tabela".equals(comboSearch.getText())) {
            searchInTable(searchText.getText());
        } else if ("baza de date".equals(comboSearch.getText())) {
            searchInDatabase(searchText.getText());
        }
    }

    private void searchInDatabase(String text) {
        paginationComposite.setSearchQuery(text);
        searchInTable(text);
    }

    private void searchInTable(final String text) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                tableViewer.resetFilters();
                java.util.List<ViewerFilter> listFilters = new ArrayList<>();
                ((UnifiedStyledLabelProvider) tableViewer.getLabelProvider(IDX_AUTOR)).setSearchText(text);
                ((UnifiedStyledLabelProvider) tableViewer.getLabelProvider(IDX_TITLU)).setSearchText(text);
                listFilters.add(new ViewerFilter() {
                    @Override
                    public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
                        Carte carte = (Carte) element;
                        return StringUtil.compareStrings(text.toLowerCase(),
                                ApplicationService.getBookController().getBookAuthorNames(carte).toLowerCase())
                                || StringUtil.compareStrings(text.toLowerCase(), carte.getTitlu().toLowerCase());
                    }
                });
                tableViewer.setFilters(listFilters.toArray(new ViewerFilter[listFilters.size()]));
                Display.getDefault().readAndDispatch();
            }
        });
    }

    private Composite createTabDocuments(CTabFolder bottomInnerTabFolderRight) {
        final Composite comp = new Composite(bottomInnerTabFolderRight, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(comp);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(comp);

        dragAndDropTableComposite = new DragAndDropTableComposite(comp, bottomInnerTabFolderRight,
                new Carte(), true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(dragAndDropTableComposite);
        bottomInnerTabFolderRight.setTopRight(dragAndDropTableComposite.getBarOps());

        return comp;
    }

    private void displayBookData() {
        Carte carte = new Carte();
        if (mainRightTabFolder.getSelectionIndex() == 0) {
            if (tableViewer.getTable().getSelectionCount() > 0) {
                carte = (Carte) tableViewer.getTable().getSelection()[0].getData();
            }
        }
        rightVerticalSash.setMaximizedControl(null);
        dragAndDropTableComposite.setCarte(carte);

        readOnlyDetailsComposite.populateFields(carte);
    }

    private void enableOps() {
        if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed()) {
            return;
        }
        boolean enable = this.tableViewer.getTable().getSelectionCount() != 0
                && this.tableViewer.getTable().getSelection()[0].getData() instanceof AbstractMongoDB;
        toolItemAdd.setEnabled(true); // add
        toolItemMod.setEnabled(enable); // mod
        toolItemDel.setEnabled(enable); // del
        toolItemClone.setEnabled(enable); // clone
    }

    private void createCompLeftTree(LiveSashForm verticalSash) {
        compLeftTree = new Composite(verticalSash, SWT.NONE);
        GridDataFactory.fillDefaults().grab(false, true).applyTo(compLeftTree);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(2, 2).extendedMargins(0,
                0,
                0,
                0).spacing(5, 3).applyTo(compLeftTree);

        Composite comp = new Composite(compLeftTree, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(comp);
        GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).applyTo(comp);
        new Label(comp, SWT.NONE).setText(" Filtru");
        final Text textUpperSearch = new Text(comp, SWT.SEARCH);
        GridDataFactory.fillDefaults().grab(true, false).span(1, 1).applyTo(textUpperSearch);
        SWTeXtension.addColoredFocusListener(textUpperSearch, ColorUtil.COLOR_FOCUS_YELLOW);
        textUpperSearch.setMessage("Filtrare");
        textUpperSearch.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        leftTreeColumnProvider.setSearchText(textUpperSearch.getText());
                        leftTreeViewer.setFilters(SimpleTextNode.getFilter(textUpperSearch.getText()));
                        leftTreeViewer.expandToLevel(AbstractTreeViewer.ALL_LEVELS);
                        Display.getDefault().readAndDispatch();
                    }
                });
            }
        });
        textUpperSearch.addListener(SWT.FocusIn, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (textUpperSearch.getText().equals("Filtrare")) {
                    textUpperSearch.setText("");
                }
            }
        });

        ToolItem itemTreeRefresh = new ToolItem(new ToolBar(comp, SWT.FLAT), SWT.NONE);
        itemTreeRefresh.setImage(AppImages.getImage16(AppImages.IMG_REFRESH));
        itemTreeRefresh.setHotImage(AppImages.getImage16Focus(AppImages.IMG_REFRESH));
        itemTreeRefresh.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                populateLeftTree();
            }
        });

        leftTreeViewer = new TreeViewer(compLeftTree, SWT.SINGLE | SWT.FULL_SELECTION
                | SWT.BORDER);
        leftTreeViewer.setUseHashlookup(true);
        SWTeXtension.addColoredFocusListener(leftTreeViewer.getTree(),
                ColorUtil.COLOR_FOCUS_YELLOW);

        leftTreeViewer.setContentProvider(new TreeContentProvider());
        leftTreeViewer.getTree().setHeaderVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(leftTreeViewer.getTree());
        leftTreeViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);

        TableSetting setting = SettingsController.getTableSetting(1, getClass(), TREE_KEY);
        int[] dims = setting.getWidths();
        int[] aligns = setting.getAligns();

        final TreeViewerColumn treeCol = new TreeViewerColumn(leftTreeViewer, SWT.NONE);
        treeCol.getColumn().setText("Grupare elemente");
        treeCol.getColumn().setWidth(dims[0]);
        treeCol.getColumn().setAlignment(aligns[0]);
        treeCol.getColumn().setResizable(true);
        treeCol.getColumn().setMoveable(false);
        this.leftTreeColumnProvider = new UnifiedStyledLabelProvider();
        treeCol.setLabelProvider(this.leftTreeColumnProvider);

        AbstractTreeColumnViewerSorter cSorter = new AbstractTreeColumnViewerSorter(
                leftTreeViewer,
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
                    return StringUtil.romanianCompare(a.getName(), b.getName());
                }
            }

        };
        cSorter.setSorter(cSorter, AbstractColumnViewerSorter.ASC);
        leftTreeViewer.getTree().setSortColumn(null);

        leftTreeViewer.getTree().setCursor(WidgetCursorUtil.getCursor(SWT.CURSOR_HAND));
        leftTreeViewer.getTree().setMenu(createLeftTreeMenu());
        WidgetTreeUtil.customizeTree(leftTreeViewer.getTree(), getClass(), TREE_KEY);

        leftTreeViewer.getTree().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                handleSelectionOnTree();
            }
        });

        new Label(compLeftTree, SWT.NONE).setText("Grupare dupa");
        comboModAfisare = new Combo(compLeftTree, SWT.READ_ONLY | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.END).applyTo(comboModAfisare);
        for (BookSearchType searchType : BookSearchType.values()) {
            if (searchType != BookSearchType.CITITORI) {
                comboModAfisare.add(searchType.name());
            }
        }
        comboModAfisare.select(comboModAfisare.indexOf(searchType.name()));
        comboModAfisare.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                searchType = BookSearchType.valueOf(comboModAfisare.getText());
                populateLeftTree();
            }
        });
    }

    private void handleSelectionOnTree() {
        if ((leftTreeViewer == null) || leftTreeViewer.getControl().isDisposed()) {
            return;
        }
        refreshTableViewer();
    }

    private void populateLeftTree() {
        if ((leftTreeViewer == null) || leftTreeViewer.getControl().isDisposed()) {
            return;
        }
        switch (searchType) {
            case EDITURA: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctStringPropertyValues(ApplicationService.getApplicationConfig().getBooksCollectionName(), "editura");
                createTreeNodes(wrapper, "Edituri");
                break;
            }
            case AUTOR: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctValuesForReferenceCollection(ApplicationService.getApplicationConfig().getBooksCollectionName(),
                        "idAutori",
                        ApplicationService.getApplicationConfig().getAutoriCollectionName(),
                        "_id",
                        "numeComplet",
                        "titlu");
                createTreeNodes(wrapper, "Autori");
                break;
            }
            case TRADUCATOR: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctArrayPropertyValues(ApplicationService.getApplicationConfig().getBooksCollectionName(), "traducere.traducatori");
                createTreeNodes(wrapper, "Traducatori");
                break;
            }
            case AN_APARITIE: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctStringPropertyValues(ApplicationService.getApplicationConfig().getBooksCollectionName(), "anAparitie");
                createTreeNodes(wrapper, "Ani aparitie");
                break;
            }
            case LIMBA: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctStringPropertyValues(ApplicationService.getApplicationConfig().getBooksCollectionName(), "limba");
                createTreeNodes(wrapper, "Limba textului");
                break;
            }
            case LIMBA_ORIGINALA: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctStringPropertyValues(ApplicationService.getApplicationConfig().getBooksCollectionName(), "editiaOriginala.limba");
                createTreeNodes(wrapper, "Limba originala");
                break;
            }
            case TIP_COPERTA: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctStringPropertyValues(ApplicationService.getApplicationConfig().getBooksCollectionName(), "tipCoperta");
                createTreeNodes(wrapper, " Tipuri coperta");
                break;
            }
            case TITLU: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctStringPropertyValues(ApplicationService.getApplicationConfig().getBooksCollectionName(), "titlu", true);
                createTreeNodes(wrapper, "Toate titlurile");
                break;
            }
            default:
                SWTeXtension.displayMessageI("Vizualizarea dupa " + searchType + " nu este implementata inca!");
        }
    }

    private void createTreeNodes(IntValuePairsWrapper wrapper, String rootNodeName) {
        SimpleTextNode baseNode;
        SimpleTextNode invisibleRoot = new SimpleTextNode(null);
        boolean showNumbers = SettingsController.getBoolean(BooleanSetting.LEFT_TREE_SHOW_NUMBERS);
        boolean showAll = SettingsController.getBoolean(BooleanSetting.LEFT_TREE_SHOW_NUMBERS);
        if (SettingsController.getBoolean(BooleanSetting.LEFT_TREE_SHOW_ALL)) {
            SimpleTextNode allNode = new SimpleTextNode(rootNodeName);
            allNode.setImage(AppImages.getImage16(AppImages.IMG_LISTA));
            allNode.setCount(wrapper.getValidDistinctValues());
            allNode.setAllNode(true);
            allNode.setQueryValue(null);
            if (showAll) {
                allNode.setName(rootNodeName + " (" + allNode.getCount() + ")");
            }
            invisibleRoot.add(allNode);
            baseNode = allNode;
        } else {
            baseNode = invisibleRoot;
        }

        for (IntValuePair valuePair : wrapper.getPairs()) {
            StringBuilder numeEditura = new StringBuilder(valuePair.getValue());
            if (showNumbers) {
                numeEditura.append(" (");
                numeEditura.append(valuePair.getCount());
                numeEditura.append(")");
            }
            SimpleTextNode node = new SimpleTextNode(numeEditura.toString());
            node.setImage(AppImages.getImage16(AppImages.IMG_BANCA));
            node.setCount(valuePair.getCount());
            node.setQueryValue(valuePair.getQueryValue());
            baseNode.add(node);
        }
        leftTreeViewer.setInput(invisibleRoot);
    }

    private Menu createLeftTreeMenu() {
        if ((leftTreeViewer == null) || leftTreeViewer.getControl().isDisposed()) {
            return null;
        }
        final Menu menu = new Menu(leftTreeViewer.getTree());
        MenuItem menuItem;
        menu.addListener(SWT.Show, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                int idx = 0;
                final boolean flagItemCount = (leftTreeViewer.getTree().getItemCount() > 0);
                menu.getItem(idx++).setEnabled(flagItemCount); // expand all
                menu.getItem(idx++).setEnabled(flagItemCount); // colapse all
                idx++; //separator
                menu.getItem(idx++).setEnabled(true); // aliniere stanga
                menu.getItem(idx++).setEnabled(true); // aliniere dreapta
                idx++; //separator
                menu.getItem(idx++).setEnabled(mainRightTabFolder.getItemCount() == 1); // afisare galerie
                boolean autorEnabled = false;
                if (flagItemCount) {
                    SimpleTextNode selectedNode = (SimpleTextNode) leftTreeViewer.getTree().getSelection()[0].getData();
                    autorEnabled = !selectedNode.isAllNode() && searchType == BookSearchType.AUTOR;
                }
                menu.getItem(idx++).setEnabled(autorEnabled); // edit autor
            }
        });

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Expandare");
        menuItem.setImage(AppImages.getImage16(AppImages.IMG_EXPAND));
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                leftTreeViewer.expandAll();
            }
        });

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Colapsare");
        menuItem.setImage(AppImages.getImage16(AppImages.IMG_COLLAPSE));
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                leftTreeViewer.collapseAll();
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Aliniere la stanga");
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                swap();
            }
        });

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Aliniere la dreapta");
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                swap();
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Afiseaza galerie");
        menuItem.setImage(AppImages.getImage16(AppImages.IMG_SHOW));
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                recreateGalleryTab();
            }
        });

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Edit autor");
        menuItem.setImage(AppImages.getImage16(AppImages.IMG_USER));
        menuItem.setEnabled(searchType == BookSearchType.AUTOR);
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                SimpleTextNode selectedNode = (SimpleTextNode) leftTreeViewer.getTree().getSelection()[0].getData();
                String idAutor = selectedNode.getQueryValue();
                new AutorView(getShell(), AutorController.findOne(new ObjectId(idAutor)), AbstractView.MODE_MODIFY).open();
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        MenuItem itemViewMode = new MenuItem(menu, SWT.CASCADE);
        itemViewMode.setText("Grupare");
        itemViewMode.setMenu(createViewModeMenu(itemViewMode));
        return menu;
    }

    private Menu createViewModeMenu(MenuItem itemViewMode) {
        Menu menu = new Menu(itemViewMode);
        for (final BookSearchType type : BookSearchType.values()) {
            MenuItem item = new MenuItem(menu, SWT.PUSH);
            item.setText(type.name());
            item.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    searchType = type;
                    comboModAfisare.select(comboModAfisare.indexOf(searchType.name()));
                    populateLeftTree();
                }
            });
        }
        return menu;
    }

    private void swap() {
        if (compLeftTree.getLocation().x < compRight.getLocation().x) {
            compLeftTree.moveBelow(compRight);
            verticalSash.setWeights(new int[]{
                    10, 3});
        } else {
            compLeftTree.moveAbove(compRight);
            verticalSash.setWeights(new int[]{
                    3, 10});
        }
    }

    private ToolBar createBarOps(Composite parent) {
        final ToolBar barOps = new ToolBar(parent, SWT.FLAT | SWT.NO_FOCUS);

        this.toolItemAdd = new ToolItem(barOps, SWT.PUSH | SWT.FLAT);
        this.toolItemAdd.setImage(AppImages.getImage24(AppImages.IMG_PLUS));
        this.toolItemAdd.setHotImage(AppImages.getImage24Focus(AppImages.IMG_PLUS));
        this.toolItemAdd.setToolTipText("Adaugare");
        this.toolItemAdd.setText("&Adaugare");
        this.toolItemAdd.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                add();
            }
        });

        this.toolItemMod = new ToolItem(barOps, SWT.PUSH | SWT.FLAT);
        this.toolItemMod.setImage(AppImages.getImage24(AppImages.IMG_MODIFICARE));
        this.toolItemMod.setHotImage(AppImages.getImage24Focus(AppImages.IMG_MODIFICARE));
        this.toolItemMod.setToolTipText("Modificare");
        this.toolItemMod.setText("&Modificare");
        this.toolItemMod.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                modify(false);
            }
        });

        this.toolItemDel = new ToolItem(barOps, SWT.PUSH | SWT.FLAT);
        this.toolItemDel.setImage(AppImages.getImage24(AppImages.IMG_CANCEL));
        this.toolItemDel.setHotImage(AppImages.getImage24Focus(AppImages.IMG_CANCEL));
        this.toolItemDel.setToolTipText("Stergere");
        this.toolItemDel.setText("Stergere");
        this.toolItemDel.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                delete();
            }
        });

        new ToolItem(barOps, SWT.SEPARATOR);

        this.toolItemGrupare = new ToolItem(barOps, SWT.CHECK);
        this.toolItemGrupare.setImage(AppImages.getImage24(AppImages.IMG_SHOW));
        this.toolItemGrupare.setHotImage(AppImages.getImage24Focus(AppImages.IMG_HIDE));
        this.toolItemGrupare.setToolTipText("Afisare sau ascundere grupare documente");
        this.toolItemGrupare.setText("Grupare");
        this.toolItemGrupare.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                handleLeftTreeDisplay();
            }
        });

        new ToolItem(barOps, SWT.SEPARATOR);

        this.toolItemRefresh = new ToolItem(barOps, SWT.PUSH | SWT.FLAT);
        this.toolItemRefresh.setImage(AppImages.getImage24(AppImages.IMG_REFRESH));
        this.toolItemRefresh.setHotImage(AppImages.getImage24Focus(AppImages.IMG_REFRESH));
        this.toolItemRefresh.setToolTipText("Reactualizare informatii");
        this.toolItemRefresh.setText("Refresh");
        this.toolItemRefresh.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                refreshTableViewer();
            }
        });

        new ToolItem(barOps, SWT.SEPARATOR);

        this.toolItemClone = new ToolItem(barOps, SWT.PUSH | SWT.FLAT);
        this.toolItemClone.setImage(AppImages.getImage24(AppImages.IMG_COPY));
        this.toolItemClone.setHotImage(AppImages.getImage24Focus(AppImages.IMG_COPY));
        this.toolItemClone.setToolTipText("Duplicare");
        this.toolItemClone.setText("&Duplicare");
        this.toolItemClone.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                modify(true);
            }
        });

        final Menu importMenu = new Menu(getShell(), SWT.POP_UP);
        MenuItem item = new MenuItem(importMenu, SWT.PUSH);
        item.setText("Import carti");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                new BookImportView(getShell()).open();
            }
        });

        item = new MenuItem(importMenu, SWT.PUSH);
        item.setText("Import autori");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                new AutoriImportView(getShell()).open();
            }
        });

        itemImport = new ToolItem(barOps, SWT.DROP_DOWN);
        itemImport.setImage(AppImages.getImage24(AppImages.IMG_IMPORT));
        itemImport.setHotImage(AppImages.getImage24Focus(AppImages.IMG_IMPORT));
        itemImport.setToolTipText("Import documente");
        itemImport.setText("Import");
        itemImport.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                Rectangle rect = itemImport.getBounds();
                Point pt = new Point(rect.x, rect.y + rect.height);
                pt = barOps.toDisplay(pt);
                importMenu.setLocation(pt.x, pt.y);
                importMenu.setVisible(true);
            }
        });

        return barOps;
    }

    private void handleLeftTreeDisplay() {
        if (this.toolItemGrupare.getSelection()) {
            verticalSash.setMaximizedControl(compRight);
        } else {
            verticalSash.setMaximizedControl(null);
        }
        if (this.toolItemGrupare.getSelection()) {
            this.toolItemGrupare.setImage(AppImages.getImage16(AppImages.IMG_HIDE));
        } else {
            this.toolItemGrupare.setImage(AppImages.getImage16(AppImages.IMG_SHOW));
        }
    }

    private ToolBar createTopRightComponents(Composite parent) {
        ToolBar bar = new ToolBar(parent, SWT.FLAT | SWT.WRAP);

        ToolItem item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage24(AppImages.IMG_MOD_VIZUALIZARE));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_MOD_VIZUALIZARE));
        item.setToolTipText("Afisare rapoarte");
        item.setText("Rapoarte");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                VizualizareRapoarte.show();
            }
        });

        item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage24(AppImages.IMG_OK));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_OK));
        item.setToolTipText("Validator diferite coduri");
        item.setText("Validator");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                new ValidareCoduriView(getShell()).open();
            }
        });

        item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage24(AppImages.IMG_LISTA));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_LISTA));
        item.setToolTipText("Configurare autori");
        item.setText("Autori");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                configAutori();
            }
        });

        item = new ToolItem(bar, SWT.NONE);
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
        item.setImage(AppImages.getImage24(AppImages.IMG_CONFIG));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_CONFIG));
        item.setToolTipText("Configurare aplicatie");
        item.setText("Setari");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                new AppConfigView(getShell()).open();
            }
        });

        item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage24(AppImages.IMG_HOME));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_HOME));
        item.setToolTipText("Schimbare utilizator");
        item.setText("Logout");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                BooksApplication.getInstance().open();
            }
        });

        item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage24(AppImages.IMG_STOP));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_STOP));
        item.setToolTipText("Inchidere aplicatie");
        item.setText(" Exit ");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                performShellClose(new Event());
            }
        });

        return bar;
    }

    private final void initViewerCols() {
        if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed()) {
            return;
        }

        this.tableViewer.setContentProvider(new AdbMongoContentProvider());
        TableSetting setting = SettingsController.getTableSetting(COLS.length, getClass(), TABLE_KEY);
        int[] dims = setting.getWidths();
        int[] aligns = setting.getAligns();
        boolean[] visible = setting.getVisibility();
        for (int i = 0; i < COLS.length; i++) {
            final TableViewerColumn col = new TableViewerColumn(this.tableViewer, SWT.NONE);
            col.getColumn().setText(COLS[i]);
            col.getColumn().setWidth(visible[i] ? dims[i] : 0);
            col.getColumn().setAlignment(aligns[i]);
            col.getColumn().setResizable(visible[i]);
            col.getColumn().setMoveable(true);
            switch (i) {
                case IDX_AUTOR: {
                    col.setLabelProvider(new UnifiedStyledLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            Carte carte = (Carte) element;
                            return ApplicationService.getBookController().getBookAuthorNames(carte);
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.tableViewer, col) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            Carte a = (Carte) e1;
                            Carte b = (Carte) e2;
                            return StringUtil.romanianCompare(ApplicationService.getBookController().getBookAuthorNames(a), ApplicationService.getBookController().getBookAuthorNames(b));
                        }

                    };
                    cSorter.setSorter(cSorter, AbstractColumnViewerSorter.ASC);
                    break;
                }
                case IDX_TITLU: {
                    col.setLabelProvider(new UnifiedStyledLabelProvider() {
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
                            return StringUtil.romanianCompare(a.getTitlu(), b.getTitlu());
                        }

                    };
                    cSorter.setSorter(cSorter, AbstractColumnViewerSorter.ASC);
                    break;
                }
                case IDX_EDITURA: {
                    col.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            Carte carte = (Carte) element;
                            return carte.getEditura();
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.tableViewer, col) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            Carte a = (Carte) e1;
                            Carte b = (Carte) e2;
                            return StringUtil.romanianCompare(a.getEditura(), b.getEditura());
                        }

                    };
                    cSorter.setSorter(cSorter, AbstractColumnViewerSorter.ASC);
                    break;
                }
                case IDX_RATING: {
                    col.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            Carte carte = (Carte) element;
                            return UserController.getPersonalRating(EncodeLive.getIdUser(), carte.getId()) + "";
                        }

                        @Override
                        public Image getImage(Object element) {
                            return AppImages.getImage16(AppImages.IMG_FULL_STAR);
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.tableViewer, col) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            Carte a = (Carte) e1;
                            Carte b = (Carte) e2;
                            return UserController.getPersonalRating(EncodeLive.getIdUser(), a.getId())
                                    - UserController.getPersonalRating(EncodeLive.getIdUser(), b.getId());
                        }

                    };
                    cSorter.setSorter(cSorter, AbstractColumnViewerSorter.ASC);
                    break;
                }
                case IDX_AN_APARITIE: {
                    col.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            Carte carte = (Carte) element;
                            return carte.getAnAparitie();
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.tableViewer, col) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            Carte a = (Carte) e1;
                            Carte b = (Carte) e2;
                            return a.getAnAparitie().compareTo(b.getAnAparitie());
                        }

                    };
                    cSorter.setSorter(cSorter, AbstractColumnViewerSorter.ASC);
                    break;
                }
                case IDX_LIMBA: {
                    col.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            Carte carte = (Carte) element;
                            return carte.getLimba().name();
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.tableViewer, col) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            Carte a = (Carte) e1;
                            Carte b = (Carte) e2;
                            return a.getLimba().compareTo(b.getLimba());
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
                idx++;
                menu.getItem(idx++).setEnabled(selIdx != -1); // clone
            }
        });

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Refresh   F5");
        menuItem.setImage(AppImages.getImage16(AppImages.IMG_REFRESH));
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                refreshTableViewer();
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
                modify(false);
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

        new MenuItem(menu, SWT.SEPARATOR);

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Duplicare");
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                modify(true);
                enableOps();
            }
        });

        return menu;
    }

    private void createTraySystem() {
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
            getShell().addListener(SWT.Dispose, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    if (appTray != null && !appTray.isDisposed()) {
                        appTray.dispose();
                    }
                }
            });

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
            menuItem.setText("Afisare Books Manager");
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
            menuItem.setText("Logout");
            menuItem.setImage(AppImages.getImage16(AppImages.IMG_USER));
            menuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event event) {
                    getShell().setVisible(false);
                    BooksApplication.getInstance().open();
                }
            });

            new MenuItem(trayItemMenu, SWT.SEPARATOR);

            menuItem = new MenuItem(trayItemMenu, SWT.PUSH);
            menuItem.setText("Exit");
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
                BooksApplication.closeApplication(false);
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    private void configAutori() {
        new AutoriView(new Shell()).open();
    }

    private void configUsers() {
        new UsersView(getShell()).open();
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
        setShellText("Books Manager [utilizator: $$$]");
        setViewOptions(AbstractView.SHOW_OPS_LABELS);
//        setBigViewMessage("12:15. Press return.");
//        setBigViewImage(AppImages.getImage32(AppImages.IMG_HOME));
    }

    public boolean add() {
        CarteView view;
        if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed()) {
            return false;
        }
        view = new CarteView(this.tableViewer.getTable().getShell(), new Carte(), AbstractView.MODE_ADD);
        view.open();
        if (view.getUserAction() == SWT.CANCEL) {
            return true;
        }
        tableViewer.add(view.getCarte());
        tableViewer.setSelection(new StructuredSelection(view.getCarte()));
        if (SettingsController.getBoolean(BooleanSetting.WINDOWS_REENTER_DATA)) {
            return add();
        } else {
            displayBookData();
        }
        return true;
    }

    public boolean delete() {
        try {
            if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed() || (this.tableViewer.getTable().getSelectionCount() <= 0)) {
                return false;
            }
            TableItem[] selected = tableViewer.getTable().getSelection();
            final int selectedCount = selected.length;
            if (selected.length > 1) {
                if (SWTeXtension.displayMessageQ("Sunteti siguri ca doriti sa stergeti cele " + selectedCount + " carti selectate?", "Confirmare stergere") == SWT.NO) {
                    return true;
                }
            } else {
                if (SWTeXtension.displayMessageQ("Sunteti siguri ca doriti sa stergeti cartea selectata?", "Confirmare stergere") == SWT.NO) {
                    return true;
                }
            }
            for (TableItem item : selected) {
                Carte carte = (Carte) item.getData();
                Carte carteDb = ApplicationService.getBookController().findOne(carte.getId());
                if (carteDb == null) {
                    SWTeXtension.displayMessageW("Cartea nu mai exista in baza de date!");
                    return false;
                }
                ApplicationService.getBookController().delete(carteDb);
                tableViewer.remove(carte);
            }
            if (selectedCount == 1) {
                SWTeXtension.displayMessageI("Cartea selectata a fost stearsa cu succes!");
            } else {
                SWTeXtension.displayMessageI("Cartile selectate au fost sterse cu succes (" + selectedCount + " carti)!");
            }
            displayBookData();
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return false;
        }
        return true;
    }

    public boolean modify(boolean createDuplicate) {
        CarteView view;
        if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed() || (this.tableViewer.getTable().getSelectionCount() <= 0)) {
            return false;
        }
        Carte carte = (Carte) this.tableViewer.getTable().getSelection()[0].getData();
        if (carte == null) {
            SWTeXtension.displayMessageI("Cartea selectata este invalida!");
            return false;
        }
        if (ApplicationService.getBookController().findOne(carte.getId()) == null) {
            SWTeXtension.displayMessageI("Cartea selectata nu mai exista in baza de date!");
            return false;
        }
        int viewMode = MODE_MODIFY;
        if (createDuplicate) {
            carte = (Carte) ObjectCloner.copy(carte);
            carte.initCopy();
            viewMode = MODE_CLONE;
        }
        view = new CarteView(this.tableViewer.getTable().getShell(), carte, viewMode);
        view.open();
        if (view.getUserAction() == SWT.CANCEL) {
            return true;
        }
        if (createDuplicate) {
            tableViewer.add(view.getCarte());
            tableViewer.setSelection(new StructuredSelection(view.getCarte()));
        } else {
            tableViewer.refresh(view.getCarte(), true, true);
            tableViewer.setSelection(new StructuredSelection(view.getCarte()));
        }
        displayBookData();
        return true;
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
        if (ApplicationService.getBookController().findOne(carte.getId()) == null) {
            SWTeXtension.displayMessageI("Cartea selectata nu mai exista in baza de date!");
            return;
        }
        new CarteView(this.tableViewer.getTable().getShell(), carte, AbstractView.MODE_VIEW).open();
    }

    private void fullRefresh() {
        populateLeftTree();
        refreshTableViewer();
    }

    private void refreshTableViewer() {
        String value = null;
        boolean all = true;
        this.tableViewer.resetFilters();
        if (!leftTreeViewer.getSelection().isEmpty()) {
            TreeItem item = leftTreeViewer.getTree().getSelection()[0];
            SimpleTextNode selectedNode = (SimpleTextNode) item.getData();
            all = selectedNode.isAllNode();
            value = selectedNode.getQueryValue();
        }
        tableViewer.setInput(null);
        Pageable pageable = paginationComposite.getPageable(true);
        ApplicationService.getBookController().requestSearch(this.searchType, value, pageable, all);
    }

    public static ToolBar getBarDocking() {
        return barDocking;
    }

    private Menu createBarDockingMenu() {
        final Menu menu = new Menu(barDocking);
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

    public static EncodePlatform getInstance() {
        return instance;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof BookReadOnlyDetailsComposite) {
            Carte carte = ((BookReadOnlyDetailsComposite) o).getCarte();
            tableViewer.refresh(carte, true, true);
            return;
        } else if (o instanceof ImageGalleryComposite) {
            Carte carte = ((ImageGalleryComposite) o).getSelected();
            tableViewer.refresh(carte, true, true);
            return;
        }
        BookController controller = (BookController) o;
        Page<Carte> page = controller.getSearchResult();
        if (!tableViewer.getTable().isDisposed()) {
            tableViewer.setInput(page.getContent());
            if (tableViewer.getTable().getItemCount() > 0) {
                Carte carte = (Carte) tableViewer.getTable().getItem(tableViewer.getTable().getItemCount() - 1).getData();
                tableViewer.setSelection(new StructuredSelection(carte));
                displayBookData();
            }
        }
    }
}
