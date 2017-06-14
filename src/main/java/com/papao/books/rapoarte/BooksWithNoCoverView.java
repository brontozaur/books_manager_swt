package com.papao.books.rapoarte;

import com.mongodb.gridfs.GridFSDBFile;
import com.papao.books.ApplicationService;
import com.papao.books.config.StringSetting;
import com.papao.books.controller.ApplicationController;
import com.papao.books.controller.SettingsController;
import com.papao.books.model.Carte;
import com.papao.books.model.config.TableSetting;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.custom.ImageSelectorComposite;
import com.papao.books.ui.custom.SimplePaginationComposite;
import com.papao.books.ui.providers.AdbMongoContentProvider;
import com.papao.books.ui.providers.UnifiedStyledLabelProvider;
import com.papao.books.ui.util.StringUtil;
import com.papao.books.ui.util.WidgetCursorUtil;
import com.papao.books.ui.util.WidgetTableUtil;
import com.papao.books.ui.util.sorter.AbstractColumnViewerSorter;
import com.papao.books.ui.util.sorter.AbstractTableColumnViewerSorter;
import com.papao.books.ui.view.AbstractCViewAdapter;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.Observer;

public class BooksWithNoCoverView extends AbstractCViewAdapter implements Observer {

    private static final String[] BOOK_COLS = new String[]{"Autor", "Titlu"};
    private final static int IDX_BOOK_AUTHOR = 0;
    private final static int IDX_BOOK_TITLE = 1;

    private TableViewer booksTableViewer;
    private ImageSelectorComposite booksImageComposite;
    private SimplePaginationComposite simplePaginationComposite;

    private static final String BOOKS_TABLE_KEY = "booksTable";

    final String dateFormat = SettingsController.getString(StringSetting.APP_DATE_FORMAT) + " " + SettingsController.getString(StringSetting.APP_TIME_FORMAT);
    final DateFormat df = new SimpleDateFormat(dateFormat);

    private static final Logger logger = Logger.getLogger(BooksWithNoCoverView.class);

    public BooksWithNoCoverView(Shell parent) {
        super(parent, MODE_NONE);

        addComponents();
    }

    private void addComponents() {
        getContainer().setLayout(new GridLayout(2, false));

        Composite compAplica = new Composite(getContainer(), SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(compAplica);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(compAplica);

        ToolItem itemAplica = new ToolItem(new ToolBar(compAplica, SWT.RIGHT | SWT.FLAT), SWT.RIGHT | SWT.FLAT);
        itemAplica.setText("Afiseaza cartile");
        itemAplica.setImage(AppImages.getImage16(AppImages.IMG_REFRESH));
        itemAplica.setHotImage(AppImages.getImage16Focus(AppImages.IMG_REFRESH));
        itemAplica.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                aplica();
            }
        });

        this.booksTableViewer = new TableViewer(getContainer(), SWT.FULL_SELECTION | SWT.BORDER | SWT.SINGLE);
        this.booksTableViewer.getTable().setHeaderVisible(true);
        this.booksTableViewer.getTable().setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this.booksTableViewer.getControl());

        booksImageComposite = new ImageSelectorComposite(getContainer(), null, null);
        this.addObserver(booksImageComposite);
        GridData data = booksImageComposite.getLayoutData();
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = false;
        data.verticalSpan = 2;
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

        simplePaginationComposite = new SimplePaginationComposite(getContainer());
        simplePaginationComposite.addObserver(this);

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

                observableProperty += ApplicationService.getBookController().getBookAuthorNamesOrderByNumeComplet(carte) + " - ";

                observableProperty += carte.getTitlu();
                setObservableProperty(observableProperty);
                setChanged();
                notifyObservers();
            }
        });

        initBooksTableCols();
        WidgetTableUtil.customizeTable(this.booksTableViewer.getTable(), getClass(), BOOKS_TABLE_KEY);
        WidgetCursorUtil.addHandCursorListener(this.booksTableViewer.getTable());
        SWTeXtension.addColoredFocusListener(this.booksTableViewer.getTable(), null);
    }

    private void aplica() {
        simplePaginationComposite.reset();
        simplePaginationComposite.search();
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
                            return carte.getTitlu();
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
                case IDX_BOOK_AUTHOR: {
                    col.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            Carte carte = (Carte) element;
                            return ApplicationService.getBookController().getBookAuthorNamesOrderByNumeComplet(carte);
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.booksTableViewer, col) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            Carte a = (Carte) e1;
                            Carte b = (Carte) e2;
                            return ApplicationService.getBookController().getBookAuthorNamesOrderByNumeComplet(a).compareTo(ApplicationService.getBookController().getBookAuthorNamesOrderByNumeComplet(b));
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

    @Override
    protected void customizeView() {
        setShellText("Carti fara imagine");
        setViewOptions(ADD_CANCEL);
        setUseCoords(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        this.booksImageComposite.setImageChanged(false);
        this.booksImageComposite.setImage(null, null);
        SimplePaginationComposite paginationComposite = (SimplePaginationComposite) o;
        booksTableViewer.setInput(paginationComposite.getBooks());
    }
}
