package com.papao.books.ui.carte;

import com.mongodb.gridfs.GridFSDBFile;
import com.novocode.naf.swt.custom.LiveSashForm;
import com.papao.books.ApplicationService;
import com.papao.books.config.BooleanSetting;
import com.papao.books.controller.ApplicationController;
import com.papao.books.controller.AutorController;
import com.papao.books.controller.SettingsController;
import com.papao.books.controller.UserController;
import com.papao.books.export.ExportType;
import com.papao.books.export.Exporter;
import com.papao.books.model.AbstractMongoDB;
import com.papao.books.model.Autor;
import com.papao.books.model.Carte;
import com.papao.books.model.config.TableSetting;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.auth.EncodeLive;
import com.papao.books.ui.custom.ImageSelectorComposite;
import com.papao.books.ui.interfaces.*;
import com.papao.books.ui.providers.AdbMongoContentProvider;
import com.papao.books.ui.providers.UnifiedStyledLabelProvider;
import com.papao.books.ui.util.StringUtil;
import com.papao.books.ui.util.WidgetCursorUtil;
import com.papao.books.ui.util.WidgetTableUtil;
import com.papao.books.ui.util.sorter.AbstractColumnViewerSorter;
import com.papao.books.ui.util.sorter.AbstractTableColumnViewerSorter;
import com.papao.books.ui.view.AbstractCView;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.IOException;
import java.util.ArrayList;

public class AutoriView extends AbstractCView implements IRefresh, IAdd, IModify, IDelete, IExport, ISearchWithHighlight {

    private static Logger logger = Logger.getLogger(AutoriView.class);

    private static final String[] COLS = new String[]{"Nume", "Titlu"};

    private final static int IDX_NUME = 0;
    private final static int IDX_TITLU = 1;

    private static final String[] BOOK_COLS = new String[]{"Titlu", "Rating", "Editura"};
    private final static int IDX_BOOK_TITLE = 0;
    private final static int IDX_BOOK_RATING = 1;
    private final static int IDX_BOOK_EDITURA = 2;

    private TableViewer autoriTableViewer;
    private TableViewer booksTableViewer;
    private ImageSelectorComposite autorImageComposite;
    private ImageSelectorComposite booksImageComposite;

    private static final String AUTORI_TABLE_KEY = "autoriTable";
    private static final String BOOKS_TABLE_KEY = "booksTable";

    public AutoriView(final Shell parent) {
        super(parent, AbstractView.MODE_NONE);

        getShell().setText("Autori");
        getShell().setImage(AppImages.getImage16(AppImages.IMG_CONFIG));

        addComponents();

        this.autoriTableViewer.setInput(AutorController.findAll());
        this.autoriTableViewer.getTable().setFocus();
    }

    @Override
    public final boolean add() {
        AutorView view;
        if ((this.autoriTableViewer == null) || this.autoriTableViewer.getControl().isDisposed()) {
            return false;
        }
        view = new AutorView(this.autoriTableViewer.getTable().getShell(), new Autor(), AbstractView.MODE_ADD);
        view.open();
        if (view.getUserAction() == SWT.CANCEL) {
            return true;
        }
        refresh();
        if (SettingsController.getBoolean(BooleanSetting.WINDOWS_REENTER_DATA)) {
            add();
        }
        return true;
    }

    @Override
    public final boolean modify() {
        AutorView view;
        if ((this.autoriTableViewer == null) || this.autoriTableViewer.getControl().isDisposed() || (this.autoriTableViewer.getTable().getSelectionCount() <= 0)) {
            return false;
        }
        Autor autor = (Autor) this.autoriTableViewer.getTable().getSelection()[0].getData();
        if (autor == null) {
            SWTeXtension.displayMessageI("Autorul selectat este invalid!");
            return false;
        }
        if (AutorController.findOne(autor.getId()) == null) {
            SWTeXtension.displayMessageI("Autorul selectat este invalid!");
            return false;
        }
        view = new AutorView(this.autoriTableViewer.getTable().getShell(), autor, AbstractView.MODE_MODIFY);
        view.open();
        if (view.getUserAction() == SWT.CANCEL) {
            return true;
        }
        autoriTableViewer.refresh(view.getAutor(), true, true);
        autoriTableViewer.setSelection(new StructuredSelection(view.getAutor()));
        return true;
    }

    @Override
    public final boolean delete() {
        try {
            if ((this.autoriTableViewer == null) || this.autoriTableViewer.getControl().isDisposed() || (this.autoriTableViewer.getTable().getSelectionCount() <= 0)) {
                return false;
            }
            Autor autor = (Autor) this.autoriTableViewer.getTable().getSelection()[0].getData();
            if (autor == null) {
                SWTeXtension.displayMessageI("Autorul selectat este invalid!");
                return false;
            }
            autor = AutorController.findOne(autor.getId());
            if (autor == null) {
                SWTeXtension.displayMessageW("Autorul nu mai exista!");
                return false;
            }
            java.util.List<Carte> cartileAutorului = ApplicationService.getBookController().getRepository().getByIdAutoriContains(autor.getId());
            if (cartileAutorului != null && !cartileAutorului.isEmpty()) {
                SWTeXtension.displayMessageW("Nu se poate sterge autorul selectat, pentru ca exista " + cartileAutorului.size() +
                        " carti cu acest autor in baza de date!");
                return false;
            }
            if (SWTeXtension.displayMessageQ("Sunteti siguri ca doriti sa stergeti autorul selectat?", "Confirmare stergere autor") == SWT.NO) {
                return true;
            }
            AutorController.delete(autor);
            refresh();
            SWTeXtension.displayMessageI("Operatie executata cu succes!");
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return false;
        }
        return true;
    }

    public final void view() {
        if ((this.autoriTableViewer == null) || this.autoriTableViewer.getControl().isDisposed() || (this.autoriTableViewer.getTable().getSelectionCount() <= 0)) {
            return;
        }
        Autor autor = (Autor) this.autoriTableViewer.getTable().getSelection()[0].getData();
        if (autor == null) {
            SWTeXtension.displayMessageI("Autor selectat este invalid!");
            return;
        }
        if (AutorController.findOne(autor.getId()) == null) {
            SWTeXtension.displayMessageI("Autor selectat este invalid!");
            return;
        }
        new AutorView(this.autoriTableViewer.getTable().getShell(), autor, AbstractView.MODE_VIEW).open();
    }

    @Override
    public void refresh() {
        this.autoriTableViewer.setInput(AutorController.findAll());
    }

    @Override
    protected void customizeView() {
        setShellStyle(SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE);
        setViewOptions(AbstractView.ADD_CANCEL | AbstractView.SHOW_OPS_LABELS);
        setBigViewMessage("Configurare autori");
        setBigViewImage(AppImages.getImage24(AppImages.IMG_USER));
        setTextSearchWithHighlightWidth(175);
    }

    @Override
    protected boolean validate() {
        return false;
    }

    @Override
    protected void saveData() {

    }

    private void enableOps() {
        if ((this.autoriTableViewer == null) || this.autoriTableViewer.getControl().isDisposed()) {
            return;
        }
        boolean enable = this.autoriTableViewer.getTable().getSelectionCount() != 0
                && this.autoriTableViewer.getTable().getSelection()[0].getData() instanceof AbstractMongoDB;
        getToolItemAdd().setEnabled(true); // add
        getToolItemMod().setEnabled(enable); // mod
        getToolItemDel().setEnabled(enable); // del
    }

    private Menu createTableMenu() {
        if ((this.autoriTableViewer == null) || this.autoriTableViewer.getControl().isDisposed()) {
            return null;
        }
        final Menu menu = new Menu(this.autoriTableViewer.getTable());
        MenuItem menuItem;
        menu.addListener(SWT.Show, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                int idx = 0;
                final int selIdx = autoriTableViewer.getTable().getSelectionIndex();
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

    private void addComponents() {
        LiveSashForm mainSash = new LiveSashForm(getContainer(), SWT.VERTICAL | SWT.SMOOTH);
        mainSash.sashWidth = 4;
        mainSash.setLayout(new GridLayout(2, false));
        GridDataFactory.fillDefaults().grab(true, true).applyTo(mainSash);

        Composite upperComp = new Composite(mainSash, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(upperComp);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(upperComp);

        this.autoriTableViewer = new TableViewer(upperComp, SWT.FULL_SELECTION | SWT.BORDER | SWT.SINGLE);
        this.autoriTableViewer.getTable().setHeaderVisible(true);
        this.autoriTableViewer.getTable().setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this.autoriTableViewer.getControl());
        this.autoriTableViewer.getTable().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                enableOps();

                Autor autor = (Autor) autoriTableViewer.getTable().getSelection()[0].getData();
                setObservableProperty(autor.getNumeComplet());
                deleteObserver(booksImageComposite);
                setChanged();
                notifyObservers();
                addObserver(booksImageComposite);

                Image mainImage;
                String imageName;
                autorImageComposite.setImage(null, null);
                if (autor.getMainImage().exists()) {
                    GridFSDBFile image = ApplicationController.getDocumentData(autor.getMainImage().getId());
                    if (image != null) {
                        imageName = image.getFilename();
                        mainImage = new Image(Display.getDefault(), image.getInputStream());
                        autorImageComposite.setImage(mainImage, imageName);
                    }
                }

                booksTableViewer.setInput(ApplicationService.getBookController().getByIdAutoriContains(autor.getId()));
                booksImageComposite.setImage(null, null);
                if (booksTableViewer.getTable().getItemCount() > 0) {
                    booksTableViewer.getTable().select(0);
                    booksTableViewer.getTable().notifyListeners(SWT.Selection, new Event());
                }
            }
        });
        SWTeXtension.addKeyDownListeners(this.autoriTableViewer.getTable(), this);
        this.autoriTableViewer.getTable().addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                modify();
            }
        });
        this.autoriTableViewer.getTable().setMenu(createTableMenu());

        initViewerCols();
        WidgetTableUtil.customizeTable(this.autoriTableViewer.getTable(), getClass(), AUTORI_TABLE_KEY);
        WidgetCursorUtil.addHandCursorListener(this.autoriTableViewer.getTable());
        SWTeXtension.addColoredFocusListener(this.autoriTableViewer.getTable(), null);

        autorImageComposite = new ImageSelectorComposite(upperComp, null, null);
        this.addObserver(autorImageComposite);
        GridData mainImageData = autorImageComposite.getLayoutData();
        mainImageData.grabExcessHorizontalSpace = false;
        mainImageData.grabExcessVerticalSpace = false;
        mainImageData.verticalAlignment = SWT.BEGINNING;
        mainImageData.horizontalAlignment = SWT.BEGINNING;
        autorImageComposite.getLabelImage().addListener(SWT.Paint, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (autorImageComposite.imageChanged() && autoriTableViewer.getTable().getSelectionCount() == 1) {
                    Autor autor = (Autor) autoriTableViewer.getTable().getSelection()[0].getData();
                    if (autor.getMainImage().exists()) {
                        ApplicationController.removeDocument(autor.getMainImage().getId());
                        autor.setMainImage(null);
                    }
                    try {
                        autor.setMainImage(ApplicationController.saveDocument(autorImageComposite));
                        AutorController.save(autor);
                        SWTeXtension.displayMessageI("Imaginea a fost salvata cu succes!", "Modificare autor");

                        autorImageComposite.setImageChanged(false);
                    } catch (IOException e) {
                        SWTeXtension.displayMessageE(e.getMessage(), e);
                    }
                }
            }
        });

        Composite lowerComp = new Composite(mainSash, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(lowerComp);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(lowerComp);

        this.booksTableViewer = new TableViewer(lowerComp, SWT.FULL_SELECTION | SWT.BORDER | SWT.SINGLE);
        this.booksTableViewer.getTable().setHeaderVisible(true);
        this.booksTableViewer.getTable().setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this.booksTableViewer.getControl());

        booksImageComposite = new ImageSelectorComposite(lowerComp, null, null);
        this.addObserver(booksImageComposite);
        GridData data = booksImageComposite.getLayoutData();
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = false;
        data.verticalAlignment = SWT.BEGINNING;
        data.horizontalAlignment = SWT.BEGINNING;
        booksImageComposite.getLabelImage().addListener(SWT.Paint, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (booksImageComposite.imageChanged() && booksTableViewer.getTable().getSelectionCount() == 1) {
                    Carte carte = (Carte) booksTableViewer.getTable().getSelection()[0].getData();
                    if (carte.getCopertaFata().exists()) {
                        ApplicationController.removeDocument(carte.getCopertaFata().getId());
                        carte.setCopertaFata(null);
                    }
                    try {
                        carte.setCopertaFata(ApplicationController.saveDocument(booksImageComposite));
                        ApplicationService.getBookController().save(carte);
                        SWTeXtension.displayMessageI("Imaginea a fost salvata cu succes!", "Modificare carte");

                        booksImageComposite.setImageChanged(false);
                    } catch (IOException e) {
                        SWTeXtension.displayMessageE(e.getMessage(), e);
                    }
                }
            }
        });

        this.booksTableViewer.getTable().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (booksTableViewer.getTable().isDisposed() || booksTableViewer.getTable().getSelectionCount() != 1) {
                    return;
                }
                Carte carte = (Carte) booksTableViewer.getTable().getSelection()[0].getData();
                Image mainImage;
                String imageName;
                booksImageComposite.setImage(null, null);
                if (carte.getCopertaFata() != null) {
                    GridFSDBFile image = ApplicationController.getDocumentData(carte.getCopertaFata().getId());
                    if (image != null) {
                        imageName = image.getFilename();
                        mainImage = new Image(Display.getDefault(), image.getInputStream());
                        booksImageComposite.setImage(mainImage, imageName);
                    }
                }

                String observableProperty = "";
                if (autoriTableViewer.getTable().getSelectionCount() == 1) {
                    Autor autor = (Autor) autoriTableViewer.getTable().getSelection()[0].getData();
                    observableProperty += autor.getNumeComplet() + " - ";
                }
                observableProperty += carte.getTitlu();
                setObservableProperty(observableProperty);
                deleteObserver(autorImageComposite);
                setChanged();
                notifyObservers();
                addObserver(autorImageComposite);
            }
        });

        initBooksTableCols();
        WidgetTableUtil.customizeTable(this.booksTableViewer.getTable(), getClass(), BOOKS_TABLE_KEY);
        WidgetCursorUtil.addHandCursorListener(this.booksTableViewer.getTable());
        SWTeXtension.addColoredFocusListener(this.booksTableViewer.getTable(), null);

        mainSash.setWeights(new int[]{5, 5});
    }

    private void initViewerCols() {
        if ((this.autoriTableViewer == null) || this.autoriTableViewer.getControl().isDisposed()) {
            return;
        }

        this.autoriTableViewer.setContentProvider(new AdbMongoContentProvider());
        TableSetting setting = SettingsController.getTableSetting(COLS.length, getClass(), AUTORI_TABLE_KEY);
        int[] dims = setting.getWidths();
        int[] aligns = setting.getAligns();
        boolean[] visible = setting.getVisibility();
        for (int i = 0; i < COLS.length; i++) {
            final TableViewerColumn col = new TableViewerColumn(this.autoriTableViewer, SWT.NONE);
            col.getColumn().setText(COLS[i]);
            col.getColumn().setWidth(visible[i] ? dims[i] : 0);
            col.getColumn().setAlignment(aligns[i]);
            col.getColumn().setResizable(visible[i]);
            col.getColumn().setMoveable(true);
            switch (i) {
                case IDX_NUME: {
                    col.setLabelProvider(new UnifiedStyledLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            Autor autor = (Autor) element;
                            return autor.getNumeComplet();
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.autoriTableViewer, col) {
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
                case IDX_TITLU: {
                    col.setLabelProvider(new UnifiedStyledLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            Autor autor = (Autor) element;
                            return autor.getTitlu();
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.autoriTableViewer, col) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            Autor a = (Autor) e1;
                            Autor b = (Autor) e2;
                            return StringUtil.romanianCompare(a.getTitlu(), b.getTitlu());
                        }

                    };
                    cSorter.setSorter(cSorter, AbstractColumnViewerSorter.ASC);
                    break;
                }
                default:
            }
        }
        this.autoriTableViewer.getTable().setSortColumn(null);
    }

    @Override
    public void exportTxt() {
        Exporter.export(ExportType.TXT, autoriTableViewer.getTable(), "Autori", getClass(), AUTORI_TABLE_KEY);
    }

    @Override
    public void exportPDF() {
        Exporter.export(ExportType.PDF, autoriTableViewer.getTable(), "Autori", getClass(), AUTORI_TABLE_KEY);
    }

    @Override
    public void exportExcel() {
        Exporter.export(ExportType.XLS, autoriTableViewer.getTable(), "Autori", getClass(), AUTORI_TABLE_KEY);
    }

    @Override
    public void exportRTF() {
        Exporter.export(ExportType.RTF, autoriTableViewer.getTable(), "Autori", getClass(), AUTORI_TABLE_KEY);
    }

    @Override
    public void exportHTML() {
        Exporter.export(ExportType.HTML, autoriTableViewer.getTable(), "Autori", getClass(), AUTORI_TABLE_KEY);
    }

    @Override
    public void searchWithHighlight() {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {

                if (getTextSearchWithHighlight().isDisposed()) {
                    return;
                }
                autoriTableViewer.resetFilters();
                booksImageComposite.setImage(null, null);
                autorImageComposite.setImage(null, null);
                final String filtersStr = getTextSearchWithHighlight().getText();
                java.util.List<ViewerFilter> listFilters = new ArrayList<>();
                ((UnifiedStyledLabelProvider) autoriTableViewer.getLabelProvider(IDX_NUME)).setSearchText(filtersStr);
                ((UnifiedStyledLabelProvider) autoriTableViewer.getLabelProvider(IDX_TITLU)).setSearchText(filtersStr);
                listFilters.add(new ViewerFilter() {
                    @Override
                    public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
                        Autor autor = (Autor) element;
                        return StringUtil.compareStrings(filtersStr.toLowerCase(), autor.getNumeComplet().toLowerCase())
                                || StringUtil.compareStrings(filtersStr.toLowerCase(), autor.getTitlu().toLowerCase());
                    }
                });
                autoriTableViewer.setFilters(listFilters.toArray(new ViewerFilter[listFilters.size()]));
                Display.getDefault().readAndDispatch();

            }
        });
    }

    private void initBooksTableCols() {
        if ((this.booksTableViewer == null) || this.booksTableViewer.getControl().isDisposed()) {
            return;
        }

        this.booksTableViewer.setContentProvider(new AdbMongoContentProvider());
        TableSetting setting = SettingsController.getTableSetting(BOOK_COLS.length, getClass(), BOOKS_TABLE_KEY);
        int[] dims = setting.getWidths();
        int[] aligns = setting.getAligns();
        boolean[] visible = setting.getVisibility();
        for (int i = 0; i < BOOK_COLS.length; i++) {
            final TableViewerColumn col = new TableViewerColumn(this.booksTableViewer, SWT.NONE);
            col.getColumn().setText(BOOK_COLS[i]);
            col.getColumn().setWidth(visible[i] ? dims[i] : 0);
            col.getColumn().setAlignment(aligns[i]);
            col.getColumn().setResizable(visible[i]);
            col.getColumn().setMoveable(true);
            switch (i) {
                case IDX_BOOK_TITLE: {
                    col.setLabelProvider(new UnifiedStyledLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            Carte carte = (Carte) element;
                            String titlu = carte.getTitlu();
                            if (StringUtils.isNotEmpty(carte.getSerie().getNume())) {
                                titlu += " (" + carte.getSerie().getFormattedValue() + ")";
                            }
                            return titlu;
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.booksTableViewer, col) {
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
                case IDX_BOOK_EDITURA: {
                    col.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            Carte carte = (Carte) element;
                            return carte.getEditura();
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.booksTableViewer, col) {
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
                case IDX_BOOK_RATING: {
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
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.booksTableViewer, col) {
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
                default:
            }
        }
        this.booksTableViewer.getTable().setSortColumn(null);
    }
}
