package com.papao.books.ui.perspective;

import com.papao.books.ui.AppImages;
import com.papao.books.ui.EncodePlatform;
import com.papao.books.ui.bones.AbstractBone;
import com.papao.books.ui.bones.filter.AbstractBoneFilter;
import com.papao.books.ui.providers.UnifiedStyledLabelProvider;
import com.papao.books.ui.providers.tree.SimpleTextNode;
import com.papao.books.ui.providers.tree.TreeContentProvider;
import com.papao.books.ui.util.WidgetCursorUtil;
import com.papao.books.ui.util.WidgetTreeUtil;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.SWTeXtension;
import com.papao.books.util.BorgDateUtil;
import com.papao.books.util.ColorUtil;
import com.papao.books.util.sorter.AbstractColumnViewerSorter;
import com.papao.books.util.sorter.AbstractTreeColumnViewerSorter;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.*;

public final class WelcomePerspective extends Composite {

    private static Logger logger = Logger.getLogger(WelcomePerspective.class);

    private WelcomeStatusLine statusLine;
    private ToolBar barDocking;
    public static WelcomePerspective instance;
    private CTabFolder mainTabFolder;
    SashForm verticalSash;
    SashForm rightInnerSash;
    Composite compLeftTree;
    Composite mainCompRight;
    Composite innerCompRight;
    Composite compRight;
    ToolBar barOps;
    private Text textUpperSearch;
    TreeViewer leftTreeViewer;
    UnifiedStyledLabelProvider leftTreeColumnProvider;

    public WelcomePerspective() {
        super(EncodePlatform.instance.getAppMainForm(), SWT.DOUBLE_BUFFERED | SWT.NO_REDRAW_RESIZE
                | SWT.EMBEDDED | SWT.NO_FOCUS);
        WelcomePerspective.instance = this;

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(this);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).margins(0, 0).extendedMargins(0,
                0,
                0,
                0).spacing(0, 0).applyTo(this);
        addComponents();
    }

    public void addComponents() {

        this.mainTabFolder = new CTabFolder(this, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(this.mainTabFolder);
        this.mainTabFolder.setSimple(false);
        this.mainTabFolder.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        this.mainTabFolder.setUnselectedImageVisible(true);
        this.mainTabFolder.setUnselectedCloseVisible(false);
        this.mainTabFolder.setMRUVisible(true);
        this.mainTabFolder.setMinimizeVisible(false);
        this.mainTabFolder.setMaximizeVisible(false);
        this.mainTabFolder.setSelectionBackground(ColorUtil.COLOR_ALBASTRU_DESCHIS_WINDOWS);

        CTabItem booksTabItem = new CTabItem(this.mainTabFolder, SWT.NONE);
        booksTabItem.setText("Carti");
        booksTabItem.setImage(AppImages.getImage32(AppImages.IMG_DETAILS_NEW));
        booksTabItem.setControl(createBooksGrid(this.mainTabFolder));

        createTopRightComponents(mainTabFolder);

        Composite lowerCompBarDocking = new Composite(this, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.END).grab(true, false).applyTo(lowerCompBarDocking);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).extendedMargins(0,
                0,
                0,
                0).spacing(0, 0).applyTo(lowerCompBarDocking);
        lowerCompBarDocking.setBackground(ColorUtil.COLOR_WHITE);
        lowerCompBarDocking.setBackgroundMode(SWT.INHERIT_DEFAULT);

        this.barDocking = new ToolBar(lowerCompBarDocking, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this.barDocking);
        GridLayoutFactory.fillDefaults().margins(0, 0).applyTo(this.barDocking);
        new ToolItem(barDocking, SWT.NONE).setText("Test");
        this.barDocking.setMenu(createBarDockingMenu());

        setStatusLine(new WelcomeStatusLine(this));
        getStatusLine().getLabelNumeModul().setText("Selectati un modul");
    }

    private void createTopRightComponents(Composite parent) {
        ToolBar bar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT | SWT.WRAP);
        bar.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

        ToolItem item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage24(AppImages.IMG_ARROW_RIGHT));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_ARROW_RIGHT));
        item.setToolTipText("Logout");
        item.setText("Logout");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                EncodePlatform.instance.logout(true);
            }
        });

        item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage24(AppImages.IMG_CANCEL));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_CANCEL));
        item.setToolTipText("Exit");
        item.setText("Exit");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                EncodePlatform.instance.performShellClose(new Event());
            }
        });
        this.mainTabFolder.setTopRight(bar);
    }

    private Composite createBooksGrid(Composite parent) {

        final Composite canvas = new Canvas(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(canvas);
        GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(2, 2, 5, 2).applyTo(canvas);
        canvas.addListener(SWT.Paint, new Listener() {

            @Override
            public void handleEvent(final Event e) {
                e.gc.setForeground(ColorUtil.COLOR_ALBASTRU_FACEBOOK);
                e.gc.drawRoundRectangle(0,
                        0,
                        canvas.getClientArea().width - 1,
                        canvas.getClientArea().height - 1,
                        8,
                        8);

            }
        });

        verticalSash = new SashForm(canvas, SWT.HORIZONTAL | SWT.SMOOTH);
        verticalSash.SASH_WIDTH = 4;
        GridDataFactory.fillDefaults().grab(true, true).applyTo(verticalSash);

        compLeftTree = new Composite(verticalSash, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(compLeftTree);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(2, 2).extendedMargins(0,
                0,
                0,
                0).spacing(5, 3).applyTo(compLeftTree);

        createCompLeftTreeWidgets();

        compRight = new Composite(verticalSash, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(compRight);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(0, 0).applyTo(compRight);

        ToolBar bar = new ToolBar(compRight, SWT.NONE);

        ToolItem item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage24(AppImages.IMG_PLUS));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_PLUS));
        item.setToolTipText("Adauga o carte noua");
        item.setText("Adauga");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {

            }
        });

        item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage24(AppImages.IMG_UPDATE));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_UPDATE));
        item.setToolTipText("Schimba detaliile cartii selectate");
        item.setText("Modifica");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {

            }
        });

        item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage24(AppImages.IMG_STOP));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_STOP));
        item.setToolTipText("Sterge cartea selectata");
        item.setText("Sterge");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {

            }
        });

        item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage24(AppImages.IMG_DETAILS_NEW));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_DETAILS_NEW));
        item.setToolTipText("Vizualizare detalii");
        item.setText("Vezi");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {

            }
        });

        verticalSash.setWeights(new int[]{2,8});
        return canvas;
    }

    private void createCompLeftTreeWidgets() {
        Composite comp = new CLabel(compLeftTree, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).align(SWT.FILL, SWT.CENTER).hint(SWT.DEFAULT,
                25).applyTo(comp);
        GridLayoutFactory.fillDefaults().extendedMargins(5, 5, 3, 2).applyTo(comp);

        textUpperSearch = new Text(comp, SWT.SEARCH);
        GridDataFactory.fillDefaults().grab(true, false).span(1, 1).grab(true, false).applyTo(textUpperSearch);
        SWTeXtension.addColoredFocusListener(textUpperSearch, ColorUtil.COLOR_FOCUS_YELLOW);
        textUpperSearch.setMessage(AbstractBone.FILTRARE_DEFAULT);
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
                if ((event.widget == textUpperSearch)
                        && textUpperSearch.getText().equals(AbstractBone.FILTRARE_DEFAULT)) {
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
        treeCol.getColumn().setWidth(200);
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
        leftTreeViewer.getTree().setSortColumn(null);

        leftTreeViewer.getTree().setCursor(WidgetCursorUtil.getCursor(SWT.CURSOR_HAND));
        leftTreeViewer.getTree().setMenu(createLeftTreeMenu());
        WidgetTreeUtil.customizeTree(leftTreeViewer.getTree(), getClass(), "Carti");

        leftTreeViewer.getTree().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                /*
					 * linia asta rezolva un bug legat de apelul getViewer().setInput(null). Din
					 * cand in cand (destul de des) crapa aici, daca existau si totaluri afisate in
					 * tabela, pt ca probabil se facea intern o de-mapare a elementelor din tabela,
					 * si liniile cu total nu erau mapate. Clasa care genera eroarea este
					 * org.eclipse.jface.viewers.CustomHashtable.hashCode(CustomHashtable.java:264).
					 */
//                getTable().removeAll();
//                handleSelectionOnTree();
//                computeTotal();
            }
        });

        comp = new Composite(leftTreeViewer.getTree(), SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).align(SWT.FILL, SWT.CENTER).applyTo(comp);
        GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(5, 0, 0, 2).applyTo(comp);
        Link linkViewMode = new Link(comp, SWT.NONE);
        linkViewMode.setToolTipText("Schimba modul de afisare");
        linkViewMode.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                handleSelectTreeViewMode();
            }
        });
        GridDataFactory.fillDefaults().grab(true, false).span(1, 1).align(SWT.BEGINNING, SWT.CENTER).applyTo(linkViewMode);

    }

    private void handleSelectTreeViewMode() {
    }

    private final Menu createLeftTreeMenu() {
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
                final boolean isDateView = false;
                menu.getItem(idx++).setEnabled(flagItemCount && isDateView); // expand all
                menu.getItem(idx++).setEnabled(flagItemCount && isDateView); // colapse all
                menu.getItem(idx++).setEnabled(true); // separator
                menu.getItem(idx++).setEnabled(true); // selectie tip afisare
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
        menuItem.setImage(AppImages.getImage16(AppImages.IMG_MOD_VIZUALIZARE));
        menuItem.setText("Selectie mod afisare");
        menuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
//                handleSelectTreeViewMode();
//                enableOps();
            }
        });

        return menu;
    }

    public void swap() {
        int ALIGN = SWT.LEFT;
        if (compLeftTree.getLocation().x < compRight.getLocation().x) {
            compLeftTree.moveBelow(compRight);
            verticalSash.setWeights(new int[] {
                    10, 3 });
        } else {
            compLeftTree.moveAbove(compRight);
            verticalSash.setWeights(new int[] {
                    3, 10 });
        }
    }

    private Menu createBarDockingMenu() {
        final Menu menu = new Menu(this.barDocking);
        menu.addListener(SWT.Show, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                menu.getItem(0).setEnabled(WelcomePerspective.this.barDocking.getItemCount() > 0);
                menu.getItem(1).setEnabled(WelcomePerspective.this.barDocking.getItemCount() > 0);
                menu.getItem(2).setEnabled(WelcomePerspective.this.barDocking.getItemCount() > 0);
            }
        });

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Inchidere ferestre");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                for (ToolItem it : WelcomePerspective.this.barDocking.getItems()) {
                    if (it.getData() instanceof AbstractView) {
                        ((AbstractView) it.getData()).close(SWT.CANCEL);
                    }
                    it.dispose();
                }
                WelcomePerspective.this.barDocking.layout();
                WelcomePerspective.this.barDocking.getParent().layout();
                WelcomePerspective.this.barDocking.getParent().getParent().layout();
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_SIMPLE_MAXIMIZE));
        item.setText("Afisare ferestre");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                for (ToolItem it : WelcomePerspective.this.barDocking.getItems()) {
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
                for (ToolItem it : WelcomePerspective.this.barDocking.getItems()) {
                    if (it.getData() instanceof AbstractView) {
                        ((AbstractView) it.getData()).getShell().setMinimized(true);
                    }
                }
            }
        });

        return menu;
    }

    public final static ToolBar getBarDocking() {
        return WelcomePerspective.instance.barDocking;
    }

    public Composite getContent() {
        return this;
    }

    public WelcomeStatusLine getStatusLine() {
        return this.statusLine;
    }

    public void setStatusLine(final WelcomeStatusLine statusLine) {
        this.statusLine = statusLine;
    }

}
