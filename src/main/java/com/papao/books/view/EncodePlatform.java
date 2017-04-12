package com.papao.books.view;

import com.mongodb.gridfs.GridFS;
import com.papao.books.FiltruAplicatie;
import com.papao.books.model.AbstractDB;
import com.papao.books.model.Carte;
import com.papao.books.repository.CarteRepository;
import com.papao.books.repository.UserRepository;
import com.papao.books.view.bones.impl.bones.ViewModeDetails;
import com.papao.books.view.carte.CarteView;
import com.papao.books.view.menu.PlatformMenu;
import com.papao.books.view.providers.AdbContentProvider;
import com.papao.books.view.providers.UnifiedStyledLabelProvider;
import com.papao.books.view.providers.tree.SimpleTextNode;
import com.papao.books.view.providers.tree.TreeContentProvider;
import com.papao.books.view.searcheable.AbstractSearchType;
import com.papao.books.view.searcheable.BorgSearchSystem;
import com.papao.books.view.user.UsersView;
import com.papao.books.view.util.*;
import com.papao.books.view.util.sorter.AbstractColumnViewerSorter;
import com.papao.books.view.util.sorter.AbstractTableColumnViewerSorter;
import com.papao.books.view.util.sorter.AbstractTreeColumnViewerSorter;
import com.papao.books.view.view.AbstractCViewAdapter;
import com.papao.books.view.view.AbstractView;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.*;
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
    private ToolItem toolItemClone;
    private ToolItem toolItemDel;
    private ToolItem toolItemRefresh;
    private ToolItem toolItemSearch;
    private Composite compRight;
    private UnifiedStyledLabelProvider leftTreeColumnProvider;
    private Link linkViewMode;
    private TreeViewer leftTreeViewer;
    private MongoTemplate mongoTemplate;

    private GridFS gridFS;
    private Canvas labelBackCover;
    private Canvas labelFrontCover;
    private Shell viewerShell;

    @Autowired
    public EncodePlatform(CarteRepository carteRepository,
                          UserRepository userRepository,
                          MongoTemplate mongoTemplate) {
        super(null, AbstractView.MODE_NONE);
        this.carteRepository = carteRepository;
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        this.gridFS = new GridFS(mongoTemplate.getDb());
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
            getShell().setImages(new Image[]{AppImages.getImage32(AppImages.IMG_BORG_MAIN)});

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
        this.mainTabFolder.setUnselectedImageVisible(true);
        this.mainTabFolder.setUnselectedCloseVisible(false);
        this.mainTabFolder.setMRUVisible(true);
        this.mainTabFolder.setMinimizeVisible(false);
        this.mainTabFolder.setMaximizeVisible(false);
        mainTabFolder.setSelectionBackground(ColorUtil.COLOR_SYSTEM);

        CTabItem booksTabItem = new CTabItem(this.mainTabFolder, SWT.NONE);
        booksTabItem.setText("Carti");
        booksTabItem.setImage(AppImages.getImage32(AppImages.IMG_DETAILS_NEW));
        this.mainTabFolder.setSelection(booksTabItem);

        createTopRightComponents(mainTabFolder);

        verticalSash = new SashForm(mainTabFolder, SWT.HORIZONTAL | SWT.SMOOTH);
        verticalSash.SASH_WIDTH = 4;
        GridDataFactory.fillDefaults().grab(true, true).span(((org.eclipse.swt.layout.GridLayout) getContainer().getLayout()).numColumns,
                1).applyTo(verticalSash);

        createCompLeftTree(verticalSash);

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
                if (e.keyCode == SWT.F3) {
                    handleSearchDisplay();
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
                modify(false);
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
        tabItemDetaliiCarte.setText("Detalii carte");
        tabItemDetaliiCarte.setImage(AppImages.getImage16(AppImages.IMG_DETAILS_NEW));
        this.mainTabFolder.setSelection(tabItemDetaliiCarte);

        tabItemDetaliiCarte.setControl(createTabDocuments(bottomInnerTabFolderRight));
        bottomInnerTabFolderRight.setSelection(tabItemDetaliiCarte);

        this.rightInnerSash.setWeights(new int[]{8, 5});

        WidgetCursorUtil.addHandCursorListener(this.tableViewer.getTable());
        SWTeXtension.addColoredFocusListener(this.tableViewer.getTable(), null);

        booksTabItem.setControl(verticalSash);

        this.tableViewer.setInput(carteRepository.findAll());
        getContainer().layout();
    }

    private Composite createTabDocuments(CTabFolder bottomInnerTabFolderRight) {
        final Composite comp = new Composite(bottomInnerTabFolderRight, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(comp);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(comp);
        new Label(comp, SWT.NONE).setText("Documents");
        return comp;
    }

    private void displayImage(Event event) {
        if (event.widget.getData() instanceof Image) {
            Image image = (Image) event.widget.getData();
            if (!image.isDisposed()) {
                if (viewerShell != null && !viewerShell.isDisposed()) {
                    viewerShell.close();
                    viewerShell = null;
                }
                viewerShell = new Shell(getShell(), SWT.NO_TRIM);
                viewerShell.setLayout(new FillLayout());
                viewerShell.setSize(image.getBounds().width, image.getBounds().height);
                viewerShell.setBackgroundImage(image);
                WidgetCompositeUtil.centerInDisplay(viewerShell);
                viewerShell.addListener(SWT.MouseExit, new Listener() {
                    @Override
                    public void handleEvent(Event event) {
                        viewerShell.close();
                    }
                });
                viewerShell.open();
            }
        }
    }

    private void displayBookData() {
        if ((this.tableViewer == null) || this.tableViewer.getControl().isDisposed()) {
            return;
        }
        if (this.tableViewer.getTable().getSelectionCount() != 0) {
            Carte carte = (Carte) this.tableViewer.getTable().getSelection()[0].getData();

//            linkGoodreadsUrl.setText("<a>" + carte.getGoodreadsUrl() + "</a>");
//            linkGoodreadsUrl.setData(carte.getGoodreadsUrl());

//            if (labelFrontCover.getBackgroundImage() != null && !labelFrontCover.getBackgroundImage().isDisposed()) {
//                labelFrontCover.getBackgroundImage().dispose();
//                ((Image) labelFrontCover.getData()).dispose();
//            }
//            labelFrontCover.setBackgroundImage(null);
//
//            if (labelBackCover.getBackgroundImage() != null && !labelBackCover.getBackgroundImage().isDisposed()) {
//                labelBackCover.getBackgroundImage().dispose();
//                ((Image) labelBackCover.getData()).dispose();
//            }
//            labelBackCover.setBackgroundImage(null);
//
//            if (carte.getCopertaFata() != null) {
//                GridFSDBFile frontCover = gridFS.find(carte.getCopertaFata().getId());
//                if (frontCover != null) {
//                    Image fullImage = new Image(labelFrontCover.getDisplay(), frontCover.getInputStream());
//                    labelFrontCover.setData(fullImage);
//                    Image resized = AppImages.getImage(fullImage, 128, 128);
//                    labelFrontCover.setBackgroundImage(resized);
//                }
//            }
//
//            if (carte.getCopertaSpate() != null) {
//                GridFSDBFile backCover = gridFS.find(carte.getCopertaSpate().getId());
//                if (backCover != null) {
//                    Image fullImage = new Image(labelBackCover.getDisplay(), backCover.getInputStream());
//                    labelBackCover.setData(fullImage);
//                    Image resized = AppImages.getImage(fullImage, 128, 128);
//                    labelBackCover.setBackgroundImage(resized);
//                }
//            }
//
        }
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
        toolItemAdd.setEnabled(true); // add
        toolItemMod.setEnabled(enable); // mod
        toolItemDel.setEnabled(enable); // del
        toolItemClone.setEnabled(enable); // clone
    }

    private void createCompLeftTree(SashForm verticalSash) {
        compLeftTree = new Composite(verticalSash, SWT.NONE);
        GridDataFactory.fillDefaults().grab(false, true).applyTo(compLeftTree);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(2, 2).extendedMargins(0,
                0,
                0,
                0).spacing(5, 3).applyTo(compLeftTree);

        Composite comp = new CLabel(compLeftTree, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).align(SWT.FILL, SWT.CENTER).hint(SWT.DEFAULT,
                25).applyTo(comp);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).extendedMargins(5, 5, 3, 2).applyTo(comp);

        new Label(comp, SWT.NONE).setText("Filtru");
        final Text textUpperSearch = new Text(comp, SWT.SEARCH);
        GridDataFactory.fillDefaults().grab(true, false).span(1, 1).applyTo(textUpperSearch);
        SWTeXtension.addColoredFocusListener(textUpperSearch, ColorUtil.COLOR_FOCUS_YELLOW);
        textUpperSearch.setMessage("Filtrare");
        textUpperSearch.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                leftTreeColumnProvider.setSearchText(textUpperSearch.getText());
                leftTreeViewer.setFilters(SimpleTextNode.getFilter(textUpperSearch.getText()));
                leftTreeViewer.expandToLevel(AbstractTreeViewer.ALL_LEVELS);
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

        leftTreeViewer = new TreeViewer(compLeftTree, SWT.SINGLE | SWT.FULL_SELECTION
                | SWT.BORDER);
        leftTreeViewer.setUseHashlookup(true);
        SWTeXtension.addColoredFocusListener(leftTreeViewer.getTree(),
                ColorUtil.COLOR_FOCUS_YELLOW);

        leftTreeViewer.setContentProvider(new TreeContentProvider());
        leftTreeViewer.getTree().setHeaderVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(leftTreeViewer.getTree());
        leftTreeViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);

        final TreeViewerColumn treeCol = new TreeViewerColumn(leftTreeViewer, SWT.NONE);
        treeCol.getColumn().setText("Grupare elemente");
        treeCol.getColumn().setWidth(160);
        treeCol.getColumn().setAlignment(SWT.CENTER);
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
                    return a.getName().compareTo(b.getName());
                }
            }

        };
        cSorter.setSorter(cSorter, AbstractColumnViewerSorter.ASC);
        leftTreeViewer.getTree().setSortColumn(null);

        leftTreeViewer.getTree().setCursor(WidgetCursorUtil.getCursor(SWT.CURSOR_HAND));
        leftTreeViewer.getTree().setMenu(createLeftTreeMenu());
        WidgetTreeUtil.customizeTree(leftTreeViewer.getTree(), getClass(), "aaa");

        leftTreeViewer.getTree().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                handleSelectionOnTree();
            }
        });

        populateLeftTreeByEditura();

        comp = new Composite(compLeftTree, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).align(SWT.FILL, SWT.CENTER).applyTo(comp);
        GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(5, 0, 0, 2).applyTo(comp);
        this.linkViewMode = new Link(comp, SWT.NONE);
        this.linkViewMode.setToolTipText("Schimba modul de afisare");
        this.linkViewMode.addListener(SWT.Selection, this);
        GridDataFactory.fillDefaults().grab(true, false).span(1, 1).align(SWT.BEGINNING, SWT.CENTER).applyTo(this.linkViewMode);
    }

    private void handleSelectionOnTree() {
        if ((leftTreeViewer == null) || leftTreeViewer.getControl().isDisposed()
                || (leftTreeViewer.getTree().getSelectionCount() <= 0)) {
            return;
        }
        final TreeItem item = leftTreeViewer.getTree().getSelection()[0];
        tableViewer.setInput(null);
        if (item.getText().contains(ViewModeDetails.ALL_STR)) {
            tableViewer.setInput(mongoTemplate.findAll(Carte.class));
        }
        tableViewer.setInput(((SimpleTextNode) item.getData()).getDbElements());
        if (tableViewer.getTable().getItemCount() > 0) {
            tableViewer.getTable().setSelection(tableViewer.getTable().getItemCount() - 1);
        }
    }

    private void populateLeftTreeByAuthor() {
        List<String> autori = mongoTemplate.getCollection("carte").distinct("autori");

    }

    private void populateLeftTreeByEditura() {
        SimpleTextNode invisibleRoot;
        boolean showNumbers;
        List<Carte> mapStart;
        SimpleTextNode baseNode;
        showNumbers = true;//getFiltru().isTreeShowingElementCount();

        if ((leftTreeViewer == null) || leftTreeViewer.getControl().isDisposed()) {
            return;
        }

        SimpleTextNode root = (SimpleTextNode) leftTreeViewer.getInput();
//        if (root != null) {
//            mapStart = root.getDbElements();
//        } else {
//            Query query = new Query();
//            query.addCriteria(Criteria.where(""))
        mapStart = mongoTemplate.findAll(Carte.class);
//        }


        Map<String, TreeMap<String, AbstractDB>> map = processDBElements(mapStart, Carte.class, "getEditura");

        if ((map == null) || map.isEmpty()) {
            leftTreeViewer.setInput(null);
            return;
        }

        invisibleRoot = new SimpleTextNode(null);
        Map<String, AbstractDB> rootMap = new HashMap<>();
        for (String key : map.keySet()) {
            rootMap.put(StringUtils.defaultIfBlank(key, "#"), new Carte());
        }
        invisibleRoot.setDbElements(rootMap);

//        if (FiltruAplicatie.isLeftTreeShowRecentActivity()) {
//            SimpleTextNode nodeRecent = new SimpleTextNode(invisibleRoot, SimpleTextNode.RECENT_NODE);
//            nodeRecent.setDbElements(new HashMap<String, AbstractDB>());
//            if (showNumbers) {
//                nodeRecent.setName(SimpleTextNode.RECENT_NODE + nodeRecent.getItemCountStr());
//            }
//            nodeRecent.setImage(AppImages.getImage16(AppImages.IMG_HOME));
//            invisibleRoot.add(nodeRecent);
//        }

//        if (FiltruAplicatie.isLeftTreeShowingAll()) {
        SimpleTextNode allNode = new SimpleTextNode(ViewModeDetails.ALL_STR);
//            allNode.setFont(FontUtil.TAHOMA8_BOLD);
        allNode.getDbElements().putAll(rootMap);
        allNode.setImage(AppImages.getImage16(AppImages.IMG_LISTA));
        if (showNumbers) {
            allNode.setName(ViewModeDetails.ALL_STR + " (" + mapStart.size() + ")");
        }
        invisibleRoot.add(allNode);
        baseNode = allNode;
//        } else {
//            baseNode = invisibleRoot;
//        }

        final Iterator<Map.Entry<String, TreeMap<String, AbstractDB>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, TreeMap<String, AbstractDB>> entry = iterator.next();
            StringBuilder numeEditura = new StringBuilder();
            numeEditura.append(StringUtils.defaultIfBlank(entry.getKey(), "#"));
            if (showNumbers) {
                numeEditura.append(" (");
                numeEditura.append(entry.getValue().size());
                numeEditura.append(")");
            }
            SimpleTextNode node = new SimpleTextNode(numeEditura.toString());
            node.setDbElements(entry.getValue());
            node.setImage(AppImages.getImage16(AppImages.IMG_BANCA));
            baseNode.add(node);
        }
        leftTreeViewer.setInput(invisibleRoot);
    }

    public static Map<String, TreeMap<String, AbstractDB>> processDBElements(final List<Carte> allBooks, final Class<? extends AbstractDB> clazz, final String methodName) {
        Map<String, TreeMap<String, AbstractDB>> map = new TreeMap<>();
        AbstractDB adb;
        Method meth;
        try {
            if (allBooks == null) {
                return map;
            }
            meth = clazz.getMethod(methodName, (Class<?>[]) null);
            if (meth == null) {
                throw new IllegalArgumentException("Class " + clazz.getCanonicalName() + " doesnt have the specified method [" + methodName + "]");
            }
            final Iterator<Carte> iterStartMap = allBooks.iterator();
            while (iterStartMap.hasNext()) {
                adb = iterStartMap.next();
                final String currentKey = meth.invoke(adb, (Object[]) null).toString();
                TreeMap<String, AbstractDB> temp = map.get(currentKey);
                if (temp == null) {
                    temp = new TreeMap<>();
                }
                temp.put(adb.getId(), adb);
                map.put(currentKey, temp);
            }

        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return new TreeMap<>();
        }
        return map;
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
//                final boolean isDateView = getFiltru().getTreeViewMode() == AbstractFilterViewMode.AFISARE_DUPA_DATA;
                boolean isDateView = false;
                menu.getItem(idx++).setEnabled(flagItemCount && isDateView); // expand all
                menu.getItem(idx++).setEnabled(flagItemCount && isDateView); // colapse all
//                menu.getItem(idx++).setEnabled(getFiltru().getTreeAlignment() == SWT.RIGHT);
//                menu.getItem(idx++).setEnabled(getFiltru().getTreeAlignment() == SWT.LEFT);
                menu.getItem(idx++).setEnabled(true); // separator
                menu.getItem(idx++).setEnabled(true); // selectie tip afisare
                menu.getItem(idx++).setEnabled(FiltruAplicatie.isLeftTreeShowRecentActivity());
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

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Aliniere la stanga");
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
            }
        });

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Distribuire operatii recente");
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
//                populateLeftTree(false);
                enableOps();
            }
        });
        return menu;
    }

    public final void handleSelectTreeViewMode() {
//        FiltruErrorsViewMode view = new FiltruErrorsViewMode(getTable().getShell(), getFiltru());
//        view.open();
//        if (view.getUserAction() == SWT.OK) {
//            setNumeCriteriuAfisare(view.getSelectionTypeName());
//            populateLeftTree(false);
//        }
    }

    public void swap(final boolean isCodeSelection) {
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
                modify(false);
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

        new ToolItem(barOps, SWT.SEPARATOR);

        this.toolItemClone = new ToolItem(barOps, SWT.PUSH | SWT.FLAT);
        this.toolItemClone.setImage(AppImages.getImage16(AppImages.IMG_COPY));
        this.toolItemClone.setHotImage(AppImages.getImage16Focus(AppImages.IMG_COPY));
        this.toolItemClone.setToolTipText("Duplicare");
        this.toolItemClone.setText("&Duplicare");
        this.toolItemClone.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                modify(true);
            }
        });
    }

    private void createTopRightComponents(Composite parent) {
        ToolBar bar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT | SWT.WRAP);

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
                            return carte.getNumeAutori(carte.getAutori());
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.tableViewer, col) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            Carte a = (Carte) e1;
                            Carte b = (Carte) e2;
                            return a.getNumeAutori(a.getAutori()).compareTo(b.getNumeAutori(b.getAutori()));
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
        view = new CarteView(this.tableViewer.getTable().getShell(), new Carte(), carteRepository, mongoTemplate, AbstractView.MODE_ADD);
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
            Carte carteDb = carteRepository.findOne(carte.getId());
            if (carteDb == null) {
                SWTeXtension.displayMessageW("Cartea nu mai exista in baza de date!");
                return false;
            }
            carteRepository.delete(carteDb);
            List<Carte> input = (List) tableViewer.getInput();
            input.remove(input.indexOf(carte));
            tableViewer.setInput(input);
            SWTeXtension.displayMessageI("Operatie executata cu succes!");
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
        if (carteRepository.findOne(carte.getId()) == null) {
            SWTeXtension.displayMessageI("Cartea selectata nu mai exista in baza de date!");
            return false;
        }
        int viewMode = MODE_MODIFY;
        if (createDuplicate) {
            carte = (Carte) ObjectCloner.copy(carte);
            carte.setId(null);
            viewMode = MODE_CLONE;
        }
        view = new CarteView(this.tableViewer.getTable().getShell(), carte, carteRepository, mongoTemplate, viewMode);
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
                            return searchType.compareValues(carte.getNumeAutori(carte.getAutori()));
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
        new CarteView(this.tableViewer.getTable().getShell(), carte, carteRepository, mongoTemplate, AbstractView.MODE_VIEW).open();
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
