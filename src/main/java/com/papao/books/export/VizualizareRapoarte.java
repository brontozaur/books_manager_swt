package com.papao.books.export;

import com.papao.books.ApplicationService;
import com.papao.books.config.BooleanSetting;
import com.papao.books.config.StringSetting;
import com.papao.books.controller.ApplicationController;
import com.papao.books.controller.ReportController;
import com.papao.books.controller.SettingsController;
import com.papao.books.model.ApplicationReport;
import com.papao.books.model.config.TableSetting;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.providers.AdbMongoContentProvider;
import com.papao.books.ui.providers.UnifiedStyledLabelProvider;
import com.papao.books.ui.providers.tree.*;
import com.papao.books.ui.util.*;
import com.papao.books.ui.util.sorter.AbstractColumnViewerSorter;
import com.papao.books.ui.util.sorter.AbstractTableColumnViewerSorter;
import com.papao.books.ui.util.sorter.AbstractTreeColumnViewerSorter;
import com.papao.books.ui.view.AbstractCViewAdapter;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.tika.io.IOUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class VizualizareRapoarte extends AbstractCViewAdapter implements Listener {

    private static Logger logger = Logger.getLogger(VizualizareRapoarte.class);

    private static VizualizareRapoarte instance;
    private TreeViewer leftViewer;
    private TableViewer rightViewer;
    private CTabFolder folder;
    private CTabItem itemIstoric;
    private ToolItem itemShowRaport;
    private ToolItem itemLaunchRaport;
    private ToolItem itemRefresh;
    private ToolItem itemDelete;
    private static final String TREE_KEY = "leftTree";
    private static final String TABLE_KEY = "reportsViewer";
    private final static String[] COLS = new String[]{"Nume raport", "Tip raport", "Cale", "Data server"};
    private DateFormat df;

    private final static int IDX_NUME = 0;
    private final static int IDX_TIP = 1;
    private final static int IDX_CALE = 2;
    private final static int IDX_DATA_SERVER = 3;

    private VizualizareRapoarte() {
        super(null, AbstractView.MODE_VIEW);

        getShell().addListener(SWT.Dispose, this);
        getShell().setImages(new Image[]{AppImages.getImage16(AppImages.IMG_INFO), AppImages.getImage24(AppImages.IMG_INFO), AppImages.getImage32(AppImages.IMG_INFO)});

        try {
            addComponents();
            refresh();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            SWTeXtension.displayMessageEGeneric(e);
        }
    }

    private void addComponents() {
        this.folder = new CTabFolder(getContainer(), SWT.FLAT | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).hint(800, 500).applyTo(this.folder);
        this.folder.setSimple(false);
        this.folder.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        this.folder.setUnselectedImageVisible(false);
        this.folder.setUnselectedCloseVisible(true);
        this.folder.setMRUVisible(true);
        this.folder.setMinimizeVisible(false);
        this.folder.setMaximizeVisible(false);
        this.folder.setSelectionBackground(ColorUtil.COLOR_ALBASTRU_DESCHIS_WINDOWS);
        this.folder.addListener(SWT.Selection, this);

        this.itemIstoric = new CTabItem(this.folder, SWT.NONE);
        this.itemIstoric.setText("Rapoarte");
        this.itemIstoric.setImage(AppImages.getImage24(AppImages.IMG_INFO));
        this.itemIstoric.setControl(createIstoricComposite());

        this.folder.setSelection(0);
    }

    private Composite createIstoricComposite() {
        SashForm sash = new SashForm(this.itemIstoric.getParent(), SWT.HORIZONTAL);

        // left component
        this.leftViewer = new TreeViewer(sash, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
        this.leftViewer.setUseHashlookup(true);
        SWTeXtension.addColoredFocusListener(this.leftViewer.getTree(), ColorUtil.COLOR_FOCUS_YELLOW);

        this.leftViewer.setContentProvider(new TreeContentProvider());
        this.leftViewer.getTree().setHeaderVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this.leftViewer.getTree());
        this.leftViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);

        final TreeViewerColumn treeCol = new TreeViewerColumn(this.leftViewer, SWT.NONE);
        TableSetting setting = SettingsController.getTableSetting(1, getClass(), TREE_KEY);
        treeCol.getColumn().setText("Tip fisier");
        treeCol.getColumn().setWidth(setting.getWidths()[0]);
        treeCol.getColumn().setAlignment(setting.getAligns()[0]);
        treeCol.getColumn().setResizable(true);
        treeCol.getColumn().setMoveable(false);
        treeCol.setLabelProvider(new UnifiedStyledLabelProvider());

        AbstractTreeColumnViewerSorter treeSorter = new AbstractTreeColumnViewerSorter(this.leftViewer, treeCol) {
            @Override
            protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                SimpleTextNode a = ((SimpleTextNode) e1);
                SimpleTextNode b = ((SimpleTextNode) e2);
                if (a == null) {
                    return -1;
                } else if (b == null) {
                    return 1;
                } else {
                    return a.getName().compareToIgnoreCase(b.getName());
                }
            }

        };
        treeSorter.setSorter(treeSorter, AbstractColumnViewerSorter.ASC);
        this.leftViewer.getTree().setSortColumn(null);

        this.leftViewer.getTree().setCursor(WidgetCursorUtil.getCursor(SWT.CURSOR_HAND));
        WidgetTreeUtil.customizeTree(this.leftViewer.getTree(), getClass(), TREE_KEY);

        this.leftViewer.getTree().addListener(SWT.Selection, this);
        // right component

        Composite comp = new Composite(sash, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(comp);
        GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(5, 5, 5, 5).applyTo(comp);

        ToolBar bar = new ToolBar(comp, SWT.FLAT | SWT.RIGHT);

        this.itemRefresh = new ToolItem(bar, SWT.NONE);
        this.itemRefresh.setImage(AppImages.getImage24(AppImages.IMG_REFRESH));
        this.itemRefresh.setHotImage(AppImages.getImage24Focus(AppImages.IMG_REFRESH));
        this.itemRefresh.setText("&Refresh");
        this.itemRefresh.addListener(SWT.Selection, this);

        this.itemShowRaport = new ToolItem(bar, SWT.NONE);
        this.itemShowRaport.setImage(AppImages.getImage24(AppImages.IMG_MOD_VIZUALIZARE));
        this.itemShowRaport.setHotImage(AppImages.getImage24Focus(AppImages.IMG_MOD_VIZUALIZARE));
        this.itemShowRaport.addListener(SWT.Selection, this);
        this.itemShowRaport.setEnabled(false);
        this.itemShowRaport.setText("&Afisare raport");

        this.itemLaunchRaport = new ToolItem(bar, SWT.NONE);
        this.itemLaunchRaport.setImage(AppImages.getImage24(AppImages.IMG_EXPORT));
        this.itemLaunchRaport.setHotImage(AppImages.getImage24Focus(AppImages.IMG_EXPORT));
        this.itemLaunchRaport.addListener(SWT.Selection, this);
        this.itemLaunchRaport.setEnabled(false);
        this.itemLaunchRaport.setText("&Lansare aplicatie externa");

        this.itemDelete = new ToolItem(bar, SWT.NONE);
        this.itemDelete.setImage(AppImages.getImage24(AppImages.IMG_CANCEL));
        this.itemDelete.setHotImage(AppImages.getImage24Focus(AppImages.IMG_CANCEL));
        this.itemDelete.addListener(SWT.Selection, this);
        this.itemDelete.setEnabled(false);
        this.itemDelete.setText("Sterge");

        this.rightViewer = new TableViewer(comp, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        this.rightViewer.setUseHashlookup(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this.rightViewer.getControl());
        this.rightViewer.getTable().setHeaderVisible(true);
        this.rightViewer.getTable().setLinesVisible(true);
        this.rightViewer.getTable().addListener(SWT.Selection, this);
        this.rightViewer.getTable().addListener(SWT.KeyDown, this);
        this.rightViewer.getTable().setMenu(createTableMenu());
        this.rightViewer.getTable().addListener(SWT.MouseDoubleClick, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (itemShowRaport.isEnabled()) {
                    itemShowRaport.notifyListeners(SWT.Selection, new Event());
                } else {
                    itemLaunchRaport.notifyListeners(SWT.Selection, new Event());
                }
            }
        });

        this.rightViewer.setContentProvider(new AdbMongoContentProvider());
        TableSetting tableSetting = SettingsController.getTableSetting(COLS.length, getClass(), TABLE_KEY);
        int[] dims = tableSetting.getWidths();
        int[] aligns = tableSetting.getAligns();
        boolean[] visible = tableSetting.getVisibility();

        for (int i = 0; i < VizualizareRapoarte.COLS.length; i++) {
            final TableViewerColumn tblCol = new TableViewerColumn(this.rightViewer, SWT.NONE);
            tblCol.getColumn().setText(VizualizareRapoarte.COLS[i]);
            tblCol.getColumn().setWidth(visible[i] ? dims[i] : 0);
            tblCol.getColumn().setAlignment(aligns[i]);
            tblCol.getColumn().setResizable(visible[i]);
            tblCol.getColumn().setMoveable(true);

            switch (i) {
                case IDX_NUME: {
                    tblCol.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            ApplicationReport obj = (ApplicationReport) element;
                            return obj.getNume();
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.rightViewer, tblCol) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            ApplicationReport a = (ApplicationReport) e1;
                            ApplicationReport b = (ApplicationReport) e2;
                            return a.getNume().compareToIgnoreCase(b.getNume());
                        }
                    };
                    cSorter.setSorter(cSorter, AbstractColumnViewerSorter.ASC);
                    break;
                }
                case IDX_CALE: {
                    tblCol.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            ApplicationReport obj = (ApplicationReport) element;
                            return obj.getCale();
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.rightViewer, tblCol) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            ApplicationReport a = (ApplicationReport) e1;
                            ApplicationReport b = (ApplicationReport) e2;
                            return a.getCale().compareToIgnoreCase(b.getCale());
                        }
                    };
                    cSorter.setSorter(cSorter, AbstractColumnViewerSorter.ASC);
                    break;
                }
                case IDX_TIP: {
                    tblCol.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            ApplicationReport obj = (ApplicationReport) element;
                            return obj.getType().toString();
                        }

                        @Override
                        public Image getImage(final Object element) {
                            ApplicationReport obj = (ApplicationReport) element;
                            return AppImages.getImage16(obj.getType().getImage());
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.rightViewer, tblCol) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            ApplicationReport a = (ApplicationReport) e1;
                            ApplicationReport b = (ApplicationReport) e2;
                            return a.getType().compareTo(b.getType());
                        }
                    };
                    cSorter.setSorter(cSorter, AbstractColumnViewerSorter.ASC);
                    break;
                }
                case IDX_DATA_SERVER: {
                    tblCol.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            ApplicationReport obj = (ApplicationReport) element;
                            return getDateFormat().format(obj.getCreatedAt());

                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.rightViewer, tblCol) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            ApplicationReport a = (ApplicationReport) e1;
                            ApplicationReport b = (ApplicationReport) e2;
                            return a.getCreatedAt().compareTo(b.getCreatedAt());
                        }
                    };
                    cSorter.setSorter(cSorter, AbstractColumnViewerSorter.ASC);
                    break;
                }
            }
        }

        this.rightViewer.getTable().setSortColumn(null);
        WidgetTableUtil.customizeTable(this.rightViewer.getTable(), getClass(), TABLE_KEY);

        sash.SASH_WIDTH = 4;
        sash.setWeights(new int[]{2, 8});

        WidgetCompositeUtil.addColoredFocusListener2Childrens(sash);

        return sash;
    }

    public final Menu createTableMenu() {
        if ((this.rightViewer == null) || this.rightViewer.getTable().isDisposed()) {
            return null;
        }
        final Menu menu = new Menu(this.rightViewer.getTable());
        MenuItem menuItem;
        menu.addListener(SWT.Show, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                int idx = 0;
                boolean hasSelection = VizualizareRapoarte.this.rightViewer.getTable().getSelectionCount() > 0;
                boolean isSupported = false;
                if (hasSelection) {
                    ApplicationReport rp = (ApplicationReport) VizualizareRapoarte.this.rightViewer.getTable().getSelection()[0].getData();
                    isSupported = (rp.getType() != ExportType.XLS) && (rp.getType() != ExportType.RTF);
                }
                menu.getItem(idx++).setEnabled(true); // refresh
                menu.getItem(idx++).setEnabled(isSupported); // afisare aici
                menu.getItem(idx++).setEnabled(hasSelection); // lansare app externa
                menu.getItem(idx++).setEnabled(hasSelection); // deschide folder
                menu.getItem(idx++).setEnabled(hasSelection); // delete
            }
        });

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Refresh	F5");
        menuItem.setImage(AppImages.getImage16(AppImages.IMG_REFRESH));
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                refresh();
            }
        });

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Afisare raport");
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                VizualizareRapoarte.this.itemShowRaport.notifyListeners(SWT.Selection, new Event());
            }
        });

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Lansare aplicatie externa");
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                VizualizareRapoarte.this.itemLaunchRaport.notifyListeners(SWT.Selection, new Event());
            }
        });

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Deschide dosarul");
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                if (VizualizareRapoarte.this.rightViewer.getTable().getSelectionIndex() == -1) {
                    return;
                }
                ApplicationReport rp = (ApplicationReport) VizualizareRapoarte.this.rightViewer.getTable().getSelection()[0].getData();
                if (rp.getCale().contains(File.separator)) {
                    Program.launch(rp.getCale().substring(0, rp.getCale().lastIndexOf(File.separator)));
                }
            }
        });

        menuItem = new MenuItem(menu, SWT.NONE);
        menuItem.setText("Stergere");
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                VizualizareRapoarte.this.itemDelete.notifyListeners(SWT.Selection, new Event());
            }
        });

        return menu;
    }

    private void refresh() {
        this.leftViewer.setInput(null);
        this.rightViewer.setInput(null);
        this.leftViewer.getTree().setSortColumn(null);
        this.rightViewer.getTable().setSortColumn(null);

        IntValuePairsWrapper wrapper = ApplicationController.getDistinctStringPropertyValues(ApplicationService.getApplicationConfig().getReportsCollectionName(), "type");
        createTreeNodes(wrapper);
    }

    private void createTreeNodes(IntValuePairsWrapper wrapper) {
        SimpleTextNode baseNode;
        boolean showAllNode = SettingsController.getBoolean(BooleanSetting.LEFT_TREE_SHOW_ALL);
        boolean showNumbers = SettingsController.getBoolean(BooleanSetting.LEFT_TREE_SHOW_NUMBERS);
        SimpleTextNode invisibleRoot = new SimpleTextNode(null);
        if (showAllNode) {
            SimpleTextNode allNode = new SimpleTextNode("Rapoarte");
            allNode.setImage(AppImages.getImage16(AppImages.IMG_LISTA));
            allNode.setCount(wrapper.getValidDistinctValues());
            allNode.setNodeType(NodeType.ALL);
            allNode.setQueryValue(null);
            if (showNumbers) {
                allNode.setName("Rapoarte" + " (" + allNode.getCount() + ")");
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
            node.setImage(AppImages.getImage16(ExportType.valueOf(valuePair.getValue()).getImage()));
            node.setCount(valuePair.getCount());
            node.setQueryValue(valuePair.getQueryValue());
            baseNode.add(node);
        }
        leftViewer.setInput(invisibleRoot);
    }

    private void enableButtons() {
        boolean hasSelection = this.rightViewer.getTable().getSelectionCount() > 0;
        this.itemLaunchRaport.setEnabled(hasSelection);
        this.itemDelete.setEnabled(hasSelection);
        boolean isSupported = false;
        if (hasSelection) {
            ApplicationReport rp = (ApplicationReport) this.rightViewer.getTable().getSelection()[0].getData();
            isSupported = (rp.getType() != ExportType.XLS) && (rp.getType() != ExportType.RTF);
        }
        this.itemShowRaport.setEnabled(isSupported);
    }

    @Override
    public void handleEvent(final Event e) {
        try {
            if (e.type == SWT.Dispose) {
                if (e.widget == getShell()) {
                    VizualizareRapoarte.instance = null;
                }
            } else if (e.type == SWT.Selection) {
                if (e.widget == this.folder) {
                    if (this.folder.getSelection() != null) {
                        getShell().setText(this.folder.getSelection().getText());
                        setBigViewMessage(this.folder.getSelection().getText());
                        if (this.folder.getSelection().getData() instanceof ApplicationReport) {
                            ApplicationReport dbRap = (ApplicationReport) this.folder.getSelection().getData();
                            updateDetailMessage(dbRap.getCale());
                            if (getTextDetail() != null) {
                                getTextDetail().setEnabled(true);
                                getBigLabelText().setText("Cale raport:");
                            }
                        } else {
                            updateDetailMessage("Istoric rapoarte");
                            if (getTextDetail() != null) {
                                getTextDetail().setEnabled(false);
                                getBigLabelText().setText("Rapoarte");
                            }
                        }
                    }
                } else if (e.widget == this.leftViewer.getTree()) {
                    boolean all = true;
                    Object value = "";
                    if (!leftViewer.getSelection().isEmpty()) {
                        TreeItem item = leftViewer.getTree().getSelection()[0];
                        SimpleTextNode selectedNode = (SimpleTextNode) item.getData();
                        all = selectedNode.getNodeType() == NodeType.ALL;
                        value = selectedNode.getQueryValue();
                    }
                    rightViewer.setInput(null);
                    rightViewer.setInput(ReportController.getReports(all, all ? null : ExportType.valueOf(value.toString())));

                } else if (e.widget == this.rightViewer.getTable()) {
                    enableButtons();
                } else if (e.widget == this.itemLaunchRaport) {
                    if (this.rightViewer.getTable().getSelectionIndex() == -1) {
                        return;
                    }
                    Display.getDefault().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            for (TableItem item : rightViewer.getTable().getSelection()) {
                                if (item.isDisposed()) {
                                    return;
                                }
                                ApplicationReport rp = (ApplicationReport) item.getData();
                                Program.launch(rp.getCale());
                                Display.getDefault().readAndDispatch();
                            }
                        }
                    });
                } else if (e.widget == this.itemShowRaport) {
                    if (this.rightViewer.getTable().getSelectionIndex() == -1) {
                        return;
                    }
                    Display.getDefault().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            for (TableItem item : rightViewer.getTable().getSelection()) {
                                if (item.isDisposed()) {
                                    return;
                                }
                                ApplicationReport rp = (ApplicationReport) item.getData();
                                VizualizareRapoarte.showRaport(rp);
                                Display.getDefault().readAndDispatch();
                            }
                        }
                    });
                } else if (e.widget == this.itemRefresh) {
                    refresh();
                } else if (e.widget == this.itemDelete) {
                    if (this.rightViewer.getTable().getSelectionIndex() == -1) {
                        return;
                    }
                    Display.getDefault().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            for (TableItem item : rightViewer.getTable().getSelection()) {
                                if (item.isDisposed()) {
                                    return;
                                }
                                ApplicationReport rp = (ApplicationReport) item.getData();
                                ReportController.delete(rp);
                                item.dispose();
                                Display.getDefault().readAndDispatch();
                            }
                            refresh();
                        }
                    });
                }
            } else if (e.type == SWT.KeyDown) {
                if (e.widget == this.rightViewer.getTable()) {
                    if (e.keyCode == SWT.F5) {
                        refresh();
                    }
                }
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            SWTeXtension.displayMessageE(exc.getMessage(), exc);
        }
    }

    @Override
    protected void customizeView() {
        setShellStyle(SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE);
        setViewOptions(AbstractView.ADD_CANCEL);
        setObjectName("rapoarte");
        setAddEscapeTraverseClose(true);
    }

    public static void show() {
        showRaport(null);
    }

    public static void showRaport(final ApplicationReport raport) {
        VizualizareRapoarte.getInstance();

        if (raport == null) {
            if (VizualizareRapoarte.instance.getShell().getMinimized() && !VizualizareRapoarte.instance.getShell().getMaximized()) {
                VizualizareRapoarte.instance.getShell().setMinimized(false);
            }
            if (!VizualizareRapoarte.instance.isOpened()) {
                VizualizareRapoarte.instance.open();
            }
            return;
        }

        CTabItem item = null;

        for (int i = 0; i < VizualizareRapoarte.instance.folder.getItemCount(); i++) {
            if (raport.equals(VizualizareRapoarte.instance.folder.getItem(i).getData())) {
                item = VizualizareRapoarte.instance.folder.getItem(i);
                break;
            }
        }

        if ((item == null) || item.isDisposed()) {
            item = new CTabItem(VizualizareRapoarte.instance.folder, SWT.CLOSE);
            item.setText(raport.getNume());
            item.setImage(AppImages.getImage24(AppImages.IMG_MOD_VIZUALIZARE));
            Browser browser = VizualizareRapoarte.createBrowser(item);
            item.setControl(browser);
            item.getParent().setSelection(item);
            item.setData(raport);
            try {
                if (raport.getType() == ExportType.TXT) {
                    File reportFile = new File(raport.getCale());
                    if (reportFile.exists() && reportFile.isFile()) {
                        FileInputStream fileInputStream = new FileInputStream(reportFile);
                        java.util.List<String> fileContents = IOUtils.readLines(fileInputStream);
                        fileInputStream.close();
                        StringBuilder sb = new StringBuilder();
                        for (String str : fileContents) {
                            sb.append(StringEscapeUtils.escapeHtml4(str)).append("<br/>");
                        }
                        browser.setText(sb.toString());
                    }
                } else {
                    browser.setUrl(raport.getCale());
                }
            } catch (IOException ioex) {
                logger.error(ioex.getMessage(), ioex);
                SWTeXtension.displayMessageE(ioex.getMessage(), ioex);
                browser.setUrl(raport.getCale());
            }
            if (VizualizareRapoarte.instance.getShell().getMinimized() && !VizualizareRapoarte.instance.getShell().getMaximized()) {
                VizualizareRapoarte.instance.getShell().setMinimized(false);
            }
            VizualizareRapoarte.instance.folder.notifyListeners(SWT.Selection, new Event());
            browser.setFocus();
            if (!VizualizareRapoarte.instance.isOpened()) {
                VizualizareRapoarte.instance.open();
            }
        } else {
            VizualizareRapoarte.instance.folder.setSelection(item);
        }
    }

    private static Browser createBrowser(final CTabItem item) {
        return new Browser(item.getParent(), SWT.NONE);
    }

    private static VizualizareRapoarte getInstance() {
        if (VizualizareRapoarte.instance == null) {
            VizualizareRapoarte.instance = new VizualizareRapoarte();
        }
        return VizualizareRapoarte.instance;
    }

    private DateFormat getDateFormat() {
        if (df == null) {
            final String dateFormat = SettingsController.getString(StringSetting.APP_DATE_FORMAT) + " " + SettingsController.getString(StringSetting.APP_TIME_FORMAT);
            df = new SimpleDateFormat(dateFormat);
        }
        return df;
    }

}
