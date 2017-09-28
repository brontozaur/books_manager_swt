package com.papao.books.ui;

import com.novocode.naf.swt.custom.BalloonNotification;
import com.novocode.naf.swt.custom.LiveSashForm;
import com.papao.books.ApplicationService;
import com.papao.books.BooksApplication;
import com.papao.books.config.BooleanSetting;
import com.papao.books.controller.*;
import com.papao.books.export.SerializareCompletaView;
import com.papao.books.export.VizualizareRapoarte;
import com.papao.books.model.AbstractMongoDB;
import com.papao.books.model.Carte;
import com.papao.books.model.config.TableSetting;
import com.papao.books.rapoarte.BooksWithNoCoverView;
import com.papao.books.rapoarte.MostReadAuthorsView;
import com.papao.books.ui.auth.EncodeLive;
import com.papao.books.ui.carte.AutorView;
import com.papao.books.ui.carte.AutoriView;
import com.papao.books.ui.carte.CarteView;
import com.papao.books.ui.config.AppConfigView;
import com.papao.books.ui.custom.*;
import com.papao.books.ui.interfaces.*;
import com.papao.books.ui.menu.PlatformMenu;
import com.papao.books.ui.preluari.AutoriImportView;
import com.papao.books.ui.preluari.BookImportView;
import com.papao.books.ui.providers.*;
import com.papao.books.ui.providers.tree.*;
import com.papao.books.ui.searcheable.BookSearchType;
import com.papao.books.ui.user.UsersView;
import com.papao.books.ui.util.*;
import com.papao.books.ui.util.sorter.AbstractColumnViewerSorter;
import com.papao.books.ui.util.sorter.AbstractTableColumnViewerSorter;
import com.papao.books.ui.util.sorter.AbstractTreeColumnViewerSorter;
import com.papao.books.ui.view.AbstractCViewAdapter;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.commons.lang3.ObjectUtils;
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
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class EncodePlatform extends AbstractCViewAdapter implements Listener, Observer, IAdd, IModify, IDelete, IRefresh, IDuplicate {

    private static Logger logger = Logger.getLogger(EncodePlatform.class);
    private ToolTip appToolTip;
    private Tray appTray;
    private static ToolBar barDocking;
    private static EncodePlatform instance;
    private LiveSashForm verticalSash;
    private Composite compLeftTree;
    private LiveSashForm rightInnerSash;
    private CTabFolder bottomInnerTabFolderRight;
    private TableViewer tableViewer;
    private CTabFolder mainRightTabFolder;

    private static final String[] COLS = new String[]{"Autor", "Titlu", "Subtitlu", "Rating", "Editura", "An aparitie", "Limba"};
    private final static int IDX_AUTOR = 0;
    private final static int IDX_TITLU = 1;
    private final static int IDX_SUBTITLU = 2;
    private final static int IDX_RATING = 3;
    private final static int IDX_EDITURA = 4;
    private final static int IDX_AN_APARITIE = 5;
    private final static int IDX_LIMBA = 6;

    private ToolItem toolItemGrupare;
    private ToolItem toolItemRandom;
    private Composite compRight;
    private UnifiedStyledLabelProvider leftTreeColumnProvider;
    private TreeViewer leftTreeViewer;

    private PaginationComposite paginationComposite;
    private BookSearchType searchType;
    private Combo comboModAfisare;
    private DragAndDropTableComposite dragAndDropTableComposite;
    private LiveSashForm rightVerticalSash;
    private BookReadOnlyDetailsComposite readOnlyDetailsComposite;
    private ImageGalleryComposite galleryComposite;
    private ProgressBarComposite progressBarComposite;
    private ToolItem itemImport;
    private ToolItem itemExport;
    private ToolItem itemRapoarte;
    private ToolItem itemConfig;
    private static final String TREE_KEY = "leftTreeViewer";
    private static final String TABLE_KEY = "booksViewer";
    private Text searchText;
    private CarteCitateTableComposite citateComposite;
    private CarteNotiteTableComposite notiteTableComposite;
    private CarteCapitoleTableComposite capitoleComposite;
    private CartePersonajTableComposite cartePersonajComposite;
    private CarteReviewComposite carteReviewComposite;

    private SimpleTextNode lastTreeSelection;

    public EncodePlatform() {
        super(null, AbstractView.MODE_NONE);
        String defaultSearchType = ApplicationService.getApplicationConfig().getDefaultSearchType();
        for (BookSearchType searchType : BookSearchType.values()) {
            if (searchType.getNume().toLowerCase().contains(defaultSearchType.toLowerCase())) {
                this.searchType = searchType;
                break;
            }
        }
        if (this.searchType == null) {
            this.searchType = BookSearchType.Autor;
        }
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
        refresh();
        super.open();
    }

    private Menu createMainRightTabFolderMenu() {
        final Menu tabFolderMenu = new Menu(mainRightTabFolder.getShell(), SWT.POP_UP);
        MenuItem item = new MenuItem(tabFolderMenu, SWT.PUSH);
        item.setText("Afișare galerie");
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

        createAdditionalToolItems();
        ((CBanner) getUpperComp()).setRight(createTopRightComponents(getUpperComp()));

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
        tabGrid.setText("Listă");
        tabGrid.setImage(AppImages.getImage16(AppImages.IMG_LISTA));
        this.mainRightTabFolder.setSelection(tabGrid);

        Composite mainCompRightTab = new Composite(mainRightTabFolder, SWT.NONE);
        mainCompRightTab.setLayout(new GridLayout(4, false));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(mainCompRightTab);

        final Combo comboSearch = new Combo(mainCompRightTab, SWT.READ_ONLY);
        comboSearch.setItems(new String[]{"tabelă", "baza de date"});
        comboSearch.select(1);

        searchText = new Text(mainCompRightTab, SWT.SEARCH);
        searchText.setMessage("căutare după...");
        GridDataFactory.fillDefaults().hint(150, SWT.DEFAULT).applyTo(searchText);
        searchText.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if ("tabelă".equals(comboSearch.getText())) {
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
        itemSearch.setText("Căutare");
        itemSearch.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                handleTableSearch(comboSearch);
            }
        });

        progressBarComposite = new ProgressBarComposite(mainCompRightTab, SWT.SMOOTH);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.END, SWT.CENTER).applyTo(progressBarComposite);
        mainRightTabFolder.setTopRight(mainCompRightTab);

        galleryComposite = new ImageGalleryComposite(mainRightTabFolder, progressBarComposite);

        if (SettingsController.getBoolean(BooleanSetting.PERSPECTIVE_SHOW_GALLERY)) {
            CTabItem tabGallery = createTabGallery(mainRightTabFolder);
            tabGallery.setControl(galleryComposite.getContent());
        } else {
            ApplicationService.getBookController().deleteObserver(galleryComposite);
        }

        this.verticalSash.setWeights(new int[]{2, 8});

        rightInnerSash = new LiveSashForm(mainRightTabFolder, SWT.VERTICAL | SWT.SMOOTH);
        rightInnerSash.sashWidth = 4;
        rightInnerSash.setLayout(new GridLayout(2, false));
        GridDataFactory.fillDefaults().grab(true, true).applyTo(rightInnerSash);

        this.tableViewer = new TableViewer(rightInnerSash, SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI);
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
        SWTeXtension.addKeyDownListeners(this.tableViewer.getTable(), this);
        SWTeXtension.addSelectAllListener(tableViewer.getTable());
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

        CTabItem tabDocumente = new CTabItem(this.bottomInnerTabFolderRight, SWT.NONE);
        tabDocumente.setText("Documente");
        tabDocumente.setImage(AppImages.getImage16(AppImages.IMG_DETAILS_NEW));
        this.bottomInnerTabFolderRight.setSelection(tabDocumente);

        tabDocumente.setControl(createTabDocuments(bottomInnerTabFolderRight));

        CTabItem tabCitate = new CTabItem(this.bottomInnerTabFolderRight, SWT.NONE);
        tabCitate.setText("Citate");
        tabCitate.setImage(AppImages.getImage16(AppImages.IMG_APP_EVENT));
        citateComposite = new CarteCitateTableComposite(bottomInnerTabFolderRight);
        tabCitate.setControl(citateComposite);

        CTabItem tabNotite = new CTabItem(this.bottomInnerTabFolderRight, SWT.NONE);
        tabNotite.setText("Notițe");
        tabNotite.setImage(AppImages.getImage16(AppImages.IMG_APP_EVENT));
        notiteTableComposite = new CarteNotiteTableComposite(bottomInnerTabFolderRight);
        tabNotite.setControl(notiteTableComposite);

        CTabItem tabCapitole = new CTabItem(this.bottomInnerTabFolderRight, SWT.NONE);
        tabCapitole.setText("Capitole");
        tabCapitole.setImage(AppImages.getImage16(AppImages.IMG_APP_EVENT));
        capitoleComposite = new CarteCapitoleTableComposite(bottomInnerTabFolderRight);
        tabCapitole.setControl(capitoleComposite);

        CTabItem tabPersonaje = new CTabItem(this.bottomInnerTabFolderRight, SWT.NONE);
        tabPersonaje.setText("Personaje");
        tabPersonaje.setImage(AppImages.getImage16(AppImages.IMG_APP_EVENT));
        cartePersonajComposite = new CartePersonajTableComposite(bottomInnerTabFolderRight);
        tabPersonaje.setControl(cartePersonajComposite);

        CTabItem tabReview = new CTabItem(this.bottomInnerTabFolderRight, SWT.NONE);
        tabReview.setText("Comentariu");
        tabReview.setImage(AppImages.getImage16(AppImages.IMG_APP_EVENT));
        carteReviewComposite = new CarteReviewComposite(bottomInnerTabFolderRight);
        tabReview.setControl(carteReviewComposite);


        this.rightInnerSash.setWeights(new int[]{8, 5});
        tabGrid.setControl(rightInnerSash);

        readOnlyDetailsComposite = new BookReadOnlyDetailsComposite(rightVerticalSash);

        // table viewer is notified when rating changes on the details composite
        readOnlyDetailsComposite.addObserver(this);
        // read only composite is notified when grid selection changes
        readOnlyDetailsComposite.addObserver(galleryComposite);
        // gallery composite is notified when a new image was selected in the image selector
        galleryComposite.addObserver(readOnlyDetailsComposite);

        //when a book is selected the readonly composite should display its details
        this.addObserver(readOnlyDetailsComposite);

        //when a book is selected the citat composite should display it's quotes, if any
        this.addObserver(citateComposite);

        //when a book is selected the citat composite should display it's notes, if any
        this.addObserver(notiteTableComposite);

        //when a book is selected the capitole composite should display it's chapters, if any
        this.addObserver(capitoleComposite);

        //when a book is selected the personaje composite should display it personages
        this.addObserver(cartePersonajComposite);

        //when a book is selected the review composite should display it's review
        this.addObserver(carteReviewComposite);

        //when a book is selected the documents table should populate
        this.addObserver(dragAndDropTableComposite);

        rightVerticalSash.setWeights(new int[]{9, 4});
        rightVerticalSash.setMaximizedControl(null);

        paginationComposite = new PaginationComposite(compRight);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(paginationComposite);

        WidgetCursorUtil.addHandCursorListener(this.tableViewer.getTable());
        SWTeXtension.addColoredFocusListener(this.tableViewer.getTable(), null);

        getContainer().layout();

        leftTreeViewer.getTree().setFocus();
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
        ((UnifiedStyledLabelProvider) ((LinkLabelProvider) tableViewer.getLabelProvider(IDX_AUTOR)).getLabelProvider()).setSearchText("");
        ((UnifiedStyledLabelProvider) tableViewer.getLabelProvider(IDX_TITLU)).setSearchText("");
        ((UnifiedStyledLabelProvider) tableViewer.getLabelProvider(IDX_SUBTITLU)).setSearchText("");
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
        if ("tabelă".equals(comboSearch.getText())) {
            searchInTable(searchText.getText().trim());
        } else if ("baza de date".equals(comboSearch.getText())) {
            searchInDatabase(searchText.getText().trim());
        }
        displayBookData();
    }

    private void searchInDatabase(String text) {
        if (text.contains("ObjectId(\"")) {
            text = text.replace("ObjectId(\"", "");
            text = text.replace("\")", "");
        }
        paginationComposite.setSearchQuery(text);
        if (!ObjectId.isValid(text)) {
            searchInTable(text);
        }
    }

    private void searchInTable(final String text) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                tableViewer.resetFilters();
                java.util.List<ViewerFilter> listFilters = new ArrayList<>();
                ((UnifiedStyledLabelProvider) ((LinkLabelProvider) tableViewer.getLabelProvider(IDX_AUTOR)).getLabelProvider()).setSearchText(text);
                ((UnifiedStyledLabelProvider) tableViewer.getLabelProvider(IDX_TITLU)).setSearchText(text);
                ((UnifiedStyledLabelProvider) tableViewer.getLabelProvider(IDX_SUBTITLU)).setSearchText(text);
                listFilters.add(new ViewerFilter() {
                    @Override
                    public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
                        Carte carte = (Carte) element;
                        final String searchTerm = text.toLowerCase();
                        return StringUtil.compareStrings(searchTerm,
                                ApplicationService.getBookController().getBookAuthorNamesOrderByNumeComplet(carte).toLowerCase())
                                || StringUtil.compareStrings(searchTerm, carte.getTitlu().toLowerCase())
                                || StringUtil.compareStrings(searchTerm, carte.getSubtitlu().toLowerCase())
                                || StringUtil.compareStrings(searchTerm, carte.getSerie().getNume().toLowerCase())
                                || StringUtil.compareStrings(searchTerm, carte.getVolum().toLowerCase());
                    }
                });
                tableViewer.setFilters(listFilters.toArray(new ViewerFilter[listFilters.size()]));
                Display.getDefault().readAndDispatch();
            }
        });
    }

    private Composite createTabDocuments(CTabFolder bottomInnerTabFolderRight) {
        final Composite comp = new Composite(bottomInnerTabFolderRight, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).applyTo(comp);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(comp);

        dragAndDropTableComposite = new DragAndDropTableComposite(comp, null,
                new Carte(), true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(dragAndDropTableComposite);

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
        setObservableObject(carte);

        setChanged();
        notifyObservers();
    }

    private void enableOps() {
        if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed()) {
            return;
        }
        boolean enable = this.tableViewer.getTable().getSelectionCount() != 0
                && this.tableViewer.getTable().getSelection()[0].getData() instanceof AbstractMongoDB;
        getToolItemAdd().setEnabled(true); // add
        getToolItemMod().setEnabled(enable); // mod
        getToolItemDel().setEnabled(enable); // del
        getToolItemDuplicate().setEnabled(enable); //duplicate
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
        WidgetTreeUtil.customizeTreeBehaviour(leftTreeViewer.getTree());
        SWTeXtension.addKeyDownListeners(this.leftTreeViewer.getTree(), this);

        TableSetting setting = SettingsController.getTableSetting(1, getClass(), TREE_KEY);
        int[] dims = setting.getWidths();
        int[] aligns = setting.getAligns();

        final TreeViewerColumn treeCol = new TreeViewerColumn(leftTreeViewer, SWT.NONE);
//        treeCol.getColumn().setText("Grupare elemente");
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
                } else if (searchType == BookSearchType.Creata ||
                        searchType == BookSearchType.Actualizata ||
                        searchType == BookSearchType.Data_cumpararii ||
                        searchType == BookSearchType.Nota_carte ||
                        searchType == BookSearchType.Nota_traducere ||
                        searchType == BookSearchType.Pret ||
                        searchType == BookSearchType.Lipsa_informatii) {
                    return StringUtil.romanianCompare(a.getInvisibleName(), b.getInvisibleName());
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

        new Label(compLeftTree, SWT.NONE).setText("Grupare după");
        comboModAfisare = new Combo(compLeftTree, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.END).applyTo(comboModAfisare);
        for (BookSearchType searchType : BookSearchType.values()) {
            comboModAfisare.add(searchType.getNume());
        }
        comboModAfisare.select(comboModAfisare.indexOf(searchType.getNume()));
        ContentProposalProvider.addContentProposal(comboModAfisare, comboModAfisare.getItems(), true);
        comboModAfisare.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                try {
                    searchType = BookSearchType.getByNume(comboModAfisare.getText());
                } catch (Exception exc) {
                    return;
                }
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
        if (!leftTreeViewer.getSelection().isEmpty()) {
            TreeItem item = leftTreeViewer.getTree().getSelection()[0];
            lastTreeSelection = (SimpleTextNode) item.getData();
        }
        switch (searchType) {
            case Editura: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctStringPropertyValues(ApplicationService.getApplicationConfig().getBooksCollectionName(), "editura", false, true);
                createTreeNodes(wrapper, "Edituri");
                break;
            }
            case Colectie: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctStringPropertyValues(ApplicationService.getApplicationConfig().getBooksCollectionName(), "colectie", false, true);
                createTreeNodes(wrapper, "Colectii");
                break;
            }
            case Pret: {
                SimpleTextNode rootNode = ApplicationController.buildPriceTree(ApplicationService.getApplicationConfig().getBooksCollectionName(), "pret.pret");
                leftTreeViewer.setInput(rootNode);
                break;
            }
            case Lipsa_informatii: {
                SimpleTextNode rootNode = ApplicationController.buildMissingInfoTree(ApplicationService.getApplicationConfig().getBooksCollectionName());
                leftTreeViewer.setInput(rootNode);
                break;
            }
            case Locatie: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctStringPropertyValues(ApplicationService.getApplicationConfig().getBooksCollectionName(), "locatie", false, true);
                createTreeNodes(wrapper, "Locatii");
                break;
            }
            case Taguri: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctArrayPropertyValues(ApplicationService.getApplicationConfig().getBooksCollectionName(), "tags");
                createTreeNodes(wrapper, "Taguri");
                break;
            }
            case Gen_literar: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctArrayPropertyValues(ApplicationService.getApplicationConfig().getBooksCollectionName(), "genLiterar");
                createTreeNodes(wrapper, "Gen literar");
                break;
            }
            case Autor: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctValuesForReferenceCollection(ApplicationService.getApplicationConfig().getBooksCollectionName(),
                        "idAutori",
                        ApplicationService.getApplicationConfig().getAutoriCollectionName(),
                        "_id",
                        "numeComplet",
                        "titlu");
                createTreeNodes(wrapper, "Autori");
                break;
            }
            case Nota_carte: {
                SimpleTextNode rootNode = ApplicationController.buildRatingTreeForCurrentUser(
                        "userId",
                        EncodeLive.getIdUser(),
                        "rating",
                        "Rating");
                leftTreeViewer.setInput(rootNode);
                break;
            }
            case Nota_traducere: {
                SimpleTextNode rootNode = ApplicationController.buildRatingTreeForCurrentUser(
                        "userId",
                        EncodeLive.getIdUser(),
                        "translationRating",
                        "Calitate traducere");
                leftTreeViewer.setInput(rootNode);
                break;
            }
            case Cititori: {
                SimpleTextNode rootNode = ApplicationController.buildUserActivityTree("userId", "carteCitita.citita", true, "Carti citite");
                leftTreeViewer.setInput(rootNode);
                break;
            }
            case Utilizatori: {
                SimpleTextNode rootNode = ApplicationController.buildUserActivityTree("userId", null, null, "Activitate utilizator");
                leftTreeViewer.setInput(rootNode);
                break;
            }
            case Traducator: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctArrayPropertyValues(ApplicationService.getApplicationConfig().getBooksCollectionName(), "traducatori");
                createTreeNodes(wrapper, "Traducatori");
                break;
            }
            case An_aparitie: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctStringPropertyValues(ApplicationService.getApplicationConfig().getBooksCollectionName(), "anAparitie", false, true);
                createTreeNodes(wrapper, "Ani aparitie");
                break;
            }
            case Limba: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctStringPropertyValues(ApplicationService.getApplicationConfig().getBooksCollectionName(), "limba", false, true);
                createTreeNodes(wrapper, "Limba textului");
                break;
            }
            case Limba_originala: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctStringPropertyValues(ApplicationService.getApplicationConfig().getBooksCollectionName(), "editiaOriginala.limba", false, true);
                createTreeNodes(wrapper, BookSearchType.Limba_originala.getNume());
                break;
            }
            case Tip_coperta: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctStringPropertyValues(ApplicationService.getApplicationConfig().getBooksCollectionName(), "tipCoperta", false, true);
                createTreeNodes(wrapper, " Tipuri coperta");
                break;
            }
            case Titlu: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctStringPropertyValues(ApplicationService.getApplicationConfig().getBooksCollectionName(), "titlu", true, true);
                createTreeNodes(wrapper, "Toate titlurile");
                break;
            }
            case Serie: {
                IntValuePairsWrapper wrapper = ApplicationController.getDistinctStringPropertyValues(ApplicationService.getApplicationConfig().getBooksCollectionName(), "serie.nume", false, false);
                createTreeNodes(wrapper, "Toate seriile");
                break;
            }
            case Creata: {
                SimpleTextNode invisibleRoot = ApplicationController.getDateTreeStructure(ApplicationService.getApplicationConfig().getBooksCollectionName(),
                        "createdAt",
                        leftTreeViewer.getAutoExpandLevel() == AbstractTreeViewer.ALL_LEVELS);
                leftTreeViewer.setInput(invisibleRoot);
                break;
            }
            case Actualizata: {
                SimpleTextNode invisibleRoot = ApplicationController.getDateTreeStructure(ApplicationService.getApplicationConfig().getBooksCollectionName(),
                        "updatedAt",
                        leftTreeViewer.getAutoExpandLevel() == AbstractTreeViewer.ALL_LEVELS);
                leftTreeViewer.setInput(invisibleRoot);
                break;
            }
            case Data_cumpararii: {
                SimpleTextNode invisibleRoot = ApplicationController.getShortDateTreeStructure(ApplicationService.getApplicationConfig().getBooksCollectionName(),
                        "pret.dataCumpararii",
                        leftTreeViewer.getAutoExpandLevel() == AbstractTreeViewer.ALL_LEVELS, true);
                leftTreeViewer.setInput(invisibleRoot);
                break;
            }
            default:
                SWTeXtension.displayMessageI("Vizualizarea după " + searchType + " nu este implementată încă!");
        }
    }

    private void createTreeNodes(IntValuePairsWrapper wrapper, String rootNodeName) {
        SimpleTextNode baseNode;
        SimpleTextNode invisibleRoot = new SimpleTextNode(null);
        boolean showNumbers = SettingsController.getBoolean(BooleanSetting.LEFT_TREE_SHOW_NUMBERS);
        if (SettingsController.getBoolean(BooleanSetting.LEFT_TREE_SHOW_ALL)) {
            SimpleTextNode allNode = new SimpleTextNode(rootNodeName);
            allNode.setImage(AppImages.getImage16(AppImages.IMG_LISTA));
            allNode.setCount(wrapper.getValidDistinctValues());
            allNode.setNodeType(NodeType.ALL);
            allNode.setQueryValue(null);
            if (showNumbers) {
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
                    autorEnabled = !selectedNode.isAllNode() && searchType == BookSearchType.Autor;
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
        menuItem.setText("Aliniere la stânga");
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
        menuItem.setText("Afișare galerie");
        menuItem.setImage(AppImages.getImage16(AppImages.IMG_SHOW));
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                recreateGalleryTab();
            }
        });

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Modificare autor");
        menuItem.setImage(AppImages.getImage16(AppImages.IMG_USER));
        menuItem.setEnabled(searchType == BookSearchType.Autor);
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                SimpleTextNode selectedNode = (SimpleTextNode) leftTreeViewer.getTree().getSelection()[0].getData();
                String idAutor = (String) selectedNode.getQueryValue();
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
                    comboModAfisare.select(comboModAfisare.indexOf(searchType.getNume()));
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

    private void createAdditionalToolItems() {
        this.toolItemGrupare = new ToolItem(getMainToolBar(), SWT.CHECK);
        this.toolItemGrupare.setImage(AppImages.getImage24(AppImages.IMG_SHOW));
        this.toolItemGrupare.setHotImage(AppImages.getImage24Focus(AppImages.IMG_HIDE));
        this.toolItemGrupare.setToolTipText("Afișare sau ascundere grupare documente");
        this.toolItemGrupare.setText("Grupare");
        this.toolItemGrupare.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                handleLeftTreeDisplay();
            }
        });

        new ToolItem(getMainToolBar(), SWT.SEPARATOR);

        this.toolItemRandom = new ToolItem(getMainToolBar(), SWT.PUSH | SWT.FLAT);
        this.toolItemRandom.setImage(AppImages.getImage24(AppImages.IMG_WARNING));
        this.toolItemRandom.setHotImage(AppImages.getImage24Focus(AppImages.IMG_WARNING));
        this.toolItemRandom.setText("Aleator!");
        this.toolItemRandom.setToolTipText("Alege carte aleatorie!");
        this.toolItemRandom.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                pickRandomBook();
            }
        });

        final Menu importMenu = new Menu(getShell(), SWT.POP_UP);
        MenuItem item = new MenuItem(importMenu, SWT.PUSH);
        item.setText("Import cărți");
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

        itemImport = new ToolItem(getMainToolBar(), SWT.DROP_DOWN);
        itemImport.setImage(AppImages.getImage24(AppImages.IMG_IMPORT));
        itemImport.setHotImage(AppImages.getImage24Focus(AppImages.IMG_IMPORT));
        itemImport.setToolTipText("Import documente");
        itemImport.setText("Import");
        itemImport.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                Rectangle rect = itemImport.getBounds();
                Point pt = new Point(rect.x, rect.y + rect.height);
                pt = getMainToolBar().toDisplay(pt);
                importMenu.setLocation(pt.x, pt.y);
                importMenu.setVisible(true);
            }
        });

        final Menu exportMenu = new Menu(getShell(), SWT.POP_UP);
        item = new MenuItem(exportMenu, SWT.PUSH);
        item.setText("Serializare completă");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                new SerializareCompletaView(getShell()).export();
            }
        });

        itemExport = new ToolItem(getMainToolBar(), SWT.DROP_DOWN);
        itemExport.setImage(AppImages.getImage24(AppImages.IMG_EXPORT));
        itemExport.setHotImage(AppImages.getImage24Focus(AppImages.IMG_EXPORT));
        itemExport.setToolTipText("Export");
        itemExport.setText("Export");
        itemExport.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                Rectangle rect = itemExport.getBounds();
                Point pt = new Point(rect.x, rect.y + rect.height);
                pt = getMainToolBar().toDisplay(pt);
                exportMenu.setLocation(pt.x, pt.y);
                exportMenu.setVisible(true);
            }
        });

        final Menu rapoarteMenu = new Menu(getShell(), SWT.POP_UP);
        item = new MenuItem(rapoarteMenu, SWT.PUSH);
        item.setText("Cei mai citiți autori");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                new MostReadAuthorsView(getShell()).open(true, true);
            }
        });

        item = new MenuItem(rapoarteMenu, SWT.PUSH);
        item.setText("Cărți fără imagine");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                new BooksWithNoCoverView(getShell()).open(true, true);
            }
        });

        itemRapoarte = new ToolItem(getMainToolBar(), SWT.DROP_DOWN);
        itemRapoarte.setImage(AppImages.getImage24(AppImages.IMG_ADOBE));
        itemRapoarte.setHotImage(AppImages.getImage24Focus(AppImages.IMG_ADOBE));
        itemRapoarte.setToolTipText("Diferite statistici");
        itemRapoarte.setText("Rapoarte");
        itemRapoarte.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                Rectangle rect = itemRapoarte.getBounds();
                Point pt = new Point(rect.x, rect.y + rect.height);
                pt = getMainToolBar().toDisplay(pt);
                rapoarteMenu.setLocation(pt.x, pt.y);
                rapoarteMenu.setVisible(true);
            }
        });

    }

    private void pickRandomBook() {
        try {
            ObjectId objectId = ApplicationController.getRandomBook(ApplicationService.getApplicationConfig().getBooksCollectionName());
            tableViewer.resetFilters();
            searchInDatabase(objectId.toString());
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            SWTeXtension.displayMessageE("Oops...eroare!", exc);
        }
    }

    private void handleLeftTreeDisplay() {
        if (this.toolItemGrupare.getSelection()) {
            verticalSash.setMaximizedControl(compRight);
        } else {
            verticalSash.setMaximizedControl(null);
        }
        if (this.toolItemGrupare.getSelection()) {
            this.toolItemGrupare.setImage(AppImages.getImage24(AppImages.IMG_HIDE));
        } else {
            this.toolItemGrupare.setImage(AppImages.getImage24(AppImages.IMG_SHOW));
        }
    }

    private ToolBar createTopRightComponents(Composite parent) {
        ToolBar bar = new ToolBar(parent, SWT.FLAT | SWT.WRAP);

        ToolItem item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage24(AppImages.IMG_MOD_VIZUALIZARE));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_MOD_VIZUALIZARE));
        item.setToolTipText("Afișare fișiere exportate");
        item.setText("Exporturi");
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
        item.setToolTipText("Configurare aplicație");
        item.setText("Setări");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                new AppConfigView(getShell()).open(true, true);
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
        item.setToolTipText("Închidere aplicație");
        item.setText(" Exit ");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                performShellClose(new Event());
            }
        });

        return bar;
    }

    private void initViewerCols() {
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
                    LinkOpener linkHandler = new LinkOpener() {
                        @Override
                        public void openLink(Object rowObject) {
                            Carte carte = (Carte) rowObject;
                            tableViewer.resetFilters();
                            tableViewer.setInput(null);
                            paginationComposite.setIdAutori(carte.getIdAutori());
                        }
                    };
                    UnifiedStyledLabelProvider columnLabelProvider = new UnifiedStyledLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            Carte carte = (Carte) element;
                            return ApplicationService.getBookController().getBookAuthorNamesOrderByNumeComplet(carte);
                        }
                    };
                    col.setLabelProvider(new LinkLabelProvider(columnLabelProvider, linkHandler));
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.tableViewer, col) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            Carte a = (Carte) e1;
                            Carte b = (Carte) e2;
                            return StringUtil.romanianCompare(ApplicationService.getBookController().getBookAuthorNamesOrderByNumeComplet(a), ApplicationService.getBookController().getBookAuthorNamesOrderByNumeComplet(b));
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
                            return carte.getTitluVolumSiSerie();
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.tableViewer, col) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            Carte a = (Carte) e1;
                            Carte b = (Carte) e2;
                            return StringUtil.romanianCompare(a.getTitluVolumSiSerie(),
                                    b.getTitluVolumSiSerie());
                        }

                    };
                    cSorter.setSorter(cSorter, AbstractColumnViewerSorter.ASC);
                    break;
                }
                case IDX_SUBTITLU: {
                    col.setLabelProvider(new UnifiedStyledLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            Carte carte = (Carte) element;
                            return carte.getSubtitlu();
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.tableViewer, col) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            Carte a = (Carte) e1;
                            Carte b = (Carte) e2;
                            return StringUtil.romanianCompare(a.getSubtitlu(), b.getSubtitlu());
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
                            return "";
                        }

                        @Override
                        public Image getImage(Object element) {
                            Carte carte = (Carte) element;
                            int rating = UserController.getPersonalRating(EncodeLive.getIdUser(), carte.getId());
                            if (rating > 0) {
                                return AppImages.getRatingStars(rating);
                            }
                            return null;
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
        menuItem.setText("Actualizare   F5");
        menuItem.setImage(AppImages.getImage16(AppImages.IMG_REFRESH));
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                refreshTableViewer();
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Adăugare");
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
        menuItem.setText("Ștergere  Del");
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
                duplicate();
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
            menuItem.setText("Afișare Books Manager");
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
            boolean close = SWTeXtension.displayMessageQ("Sunteți sigur că doriți să închideți aplicația?", "Închidere aplicație") == SWT.NO;
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
        setCreateUpperCompRightArea(true);
//        setBigViewMessage("12:15. Press return.");
//        setBigViewImage(AppImages.getImage32(AppImages.IMG_HOME));
    }

    @Override
    public boolean duplicate() {
        return modify(true);
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

        populateLeftTree();
        if (lastTreeSelection != null) {
            TreeItem[] items = WidgetTreeUtil.getTreeItemsX(leftTreeViewer.getTree());
            for (TreeItem item : items) {
                SimpleTextNode node = (SimpleTextNode) item.getData();
                if (node != null && ObjectUtils.equals(node.getQueryValue(), lastTreeSelection.getQueryValue())) {
                    this.leftTreeViewer.getTree().setSelection(item);
                }
            }
        }

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
                if (SWTeXtension.displayMessageQ("Sunteti siguri ca doriti sa stergeti cele " + selectedCount + " carti selectate si toate informatiile asociate lor?", "Confirmare stergere multipla") == SWT.NO) {
                    return true;
                }
            } else {
                if (SWTeXtension.displayMessageQ("Sunteți siguri că doriți să ștergeți cartea selectată și toate informațiile asociate?", "Confirmare ștergere") == SWT.NO) {
                    return true;
                }
            }
            for (TableItem item : selected) {
                Carte carte = (Carte) item.getData();
                Carte carteDb = ApplicationService.getBookController().findOne(carte.getId());
                if (carteDb == null) {
                    SWTeXtension.displayMessageW("Cartea nu mai există în baza de date!");
                    return false;
                }
                ApplicationService.getBookController().delete(carteDb);
                UserController.deleteAllBookUserActivity(carteDb.getId());
                tableViewer.remove(carte);
                if (!leftTreeViewer.getSelection().isEmpty()) {
                    TreeItem treeItem = leftTreeViewer.getTree().getSelection()[0];
                    lastTreeSelection = (SimpleTextNode) treeItem.getData();
                    lastTreeSelection.decrement();
                    lastTreeSelection.modifyCount(true, false);
                    treeItem.setText(lastTreeSelection.getName());
                }
            }
            if (selectedCount == 1) {
                SWTeXtension.displayMessageI("Cartea selectată a fost ștearsă cu succes!");
            } else {
                SWTeXtension.displayMessageI("Carțile selectate au fost șterse cu succes (" + selectedCount + " cărți)!");
            }
            displayBookData();
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return false;
        }
        return true;
    }

    @Override
    public boolean modify() {
        return modify(false);
    }

    public boolean modify(boolean createDuplicate) {
        boolean galleryTabSelected = mainRightTabFolder.getSelectionIndex() == 1;
        Carte carte;
        CarteView view;
        if (galleryTabSelected) {
            carte = this.galleryComposite.getSelected();
            if (carte == null) {
                SWTeXtension.displayMessageI("Nu ați selectat nici o carte!");

            }
        } else {
            if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed() || (this.tableViewer.getTable().getSelectionCount() <= 0)) {
                return false;
            }
            carte = (Carte) this.tableViewer.getTable().getSelection()[0].getData();
            if (carte == null) {
                SWTeXtension.displayMessageI("Cartea selectată este invalidă!");
                return false;
            }
        }
        Carte carteDatabase = ApplicationService.getBookController().findOne(carte.getId());
        if (carteDatabase == null) {
            SWTeXtension.displayMessageI("Cartea selectată nu mai există în baza de date!");
            return false;
        }
        int viewMode = MODE_MODIFY;
        if (createDuplicate) {
            carteDatabase = (Carte) ObjectCloner.copy(carteDatabase);
            carteDatabase.initCopy();
            viewMode = MODE_CLONE;
        }
        view = new CarteView(this.tableViewer.getTable().getShell(), carteDatabase, viewMode);
        view.open();
        if (view.getUserAction() == SWT.CANCEL) {
            return true;
        }
        if (galleryTabSelected) {
            galleryComposite.populateFields(view.getCarte());
        } else {
            if (createDuplicate) {
                tableViewer.add(view.getCarte());
                tableViewer.setSelection(new StructuredSelection(view.getCarte()));
            } else {
                tableViewer.refresh(view.getCarte(), true, true);
                tableViewer.setSelection(new StructuredSelection(view.getCarte()));
                tableViewer.getTable().getItem(((List<Carte>) tableViewer.getInput()).indexOf(view.getCarte())).setData(view.getCarte());
            }
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
            SWTeXtension.displayMessageI("Cartea selectată este invalidă!");
            return;
        }
        Carte carteDatabase = ApplicationService.getBookController().findOne(carte.getId());
        if (carteDatabase == null) {
            SWTeXtension.displayMessageI("Cartea selectată nu mai există în baza de date!");
            return;
        }
        new CarteView(this.tableViewer.getTable().getShell(), carteDatabase, AbstractView.MODE_VIEW).open();
    }

    @Override
    public void refresh() {
        populateLeftTree();
        refreshTableViewer();

        if (lastTreeSelection != null) {
            TreeItem[] items = WidgetTreeUtil.getTreeItemsX(leftTreeViewer.getTree());
            for (TreeItem item : items) {
                SimpleTextNode node = (SimpleTextNode) item.getData();
                if (node != null && ObjectUtils.equals(node.getQueryValue(), lastTreeSelection.getQueryValue())) {
                    this.leftTreeViewer.getTree().setSelection(item);
                    this.leftTreeViewer.getTree().notifyListeners(SWT.Selection, new Event());
                }
            }
        }
    }

    private void refreshTableViewer() {
        this.tableViewer.resetFilters();
        tableViewer.setInput(null);
        if (!leftTreeViewer.getSelection().isEmpty()) {
            TreeItem item = leftTreeViewer.getTree().getSelection()[0];
            SimpleTextNode selectedNode = (SimpleTextNode) item.getData();
            Pageable pageable = paginationComposite.getPageable(true);
            ApplicationService.getBookController().requestSearch(this.searchType, selectedNode, pageable);
        }
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
        item.setText("Închidere ferestre");
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
        item.setText("Afișare ferestre");
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
