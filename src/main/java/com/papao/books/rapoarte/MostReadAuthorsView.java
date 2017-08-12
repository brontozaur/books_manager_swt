package com.papao.books.rapoarte;

import com.mongodb.gridfs.GridFSDBFile;
import com.novocode.naf.swt.custom.LiveSashForm;
import com.papao.books.ApplicationService;
import com.papao.books.config.StringSetting;
import com.papao.books.controller.ApplicationController;
import com.papao.books.controller.AutorController;
import com.papao.books.controller.SettingsController;
import com.papao.books.controller.UserController;
import com.papao.books.model.Autor;
import com.papao.books.model.Carte;
import com.papao.books.model.User;
import com.papao.books.model.UserActivity;
import com.papao.books.model.config.TableSetting;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.auth.EncodeLive;
import com.papao.books.ui.custom.ComboImage;
import com.papao.books.ui.custom.DateChooserCustom;
import com.papao.books.ui.custom.ImageSelectorComposite;
import com.papao.books.ui.custom.ProgressBarComposite;
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
import java.util.*;
import java.util.List;

public class MostReadAuthorsView extends AbstractCViewAdapter {

    private Button buttonStart;
    private Button buttonEnd;
    private DateChooserCustom dateChooserStart;
    private DateChooserCustom dateChooserEnd;
    private ComboImage comboUsers;
    private static final String[] COLS = new String[]{"Nume", "Nr carti"};

    private final static int IDX_NUME = 0;
    private final static int IDX_BOOK_COUNT = 1;

    private static final String[] BOOK_COLS = new String[]{"Titlu", "Rating", "Inceputa la", "Terminata la"};
    private final static int IDX_BOOK_TITLE = 0;
    private final static int IDX_BOOK_RATING = 1;
    private final static int IDX_BOOK_START = 2;
    private final static int IDX_BOOK_END = 3;

    private TableViewer autoriTableViewer;
    private TableViewer booksTableViewer;
    private ImageSelectorComposite autorImageComposite;
    private ImageSelectorComposite booksImageComposite;

    private static final String AUTORI_TABLE_KEY = "autoriTable";
    private static final String BOOKS_TABLE_KEY = "booksTable";

    private Map<Autor, List<Carte>> booksAndAuthorsMap;

    final String dateFormat = SettingsController.getString(StringSetting.APP_DATE_FORMAT) + " " + SettingsController.getString(StringSetting.APP_TIME_FORMAT);
    final DateFormat df = new SimpleDateFormat(dateFormat);

    private static final Logger logger = Logger.getLogger(MostReadAuthorsView.class);

    public MostReadAuthorsView(Shell parent) {
        super(parent, MODE_NONE);

        addComponents();
    }

    private void addComponents() {
        Group upperComp = new Group(getContainer(), SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(4).applyTo(upperComp);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(upperComp);
        upperComp.setText("Filtre raport");

        buttonStart = new Button(upperComp, SWT.CHECK);
        buttonStart.setText("de la");
        dateChooserStart = new DateChooserCustom(upperComp);
        dateChooserStart.setEnabled(false);

        buttonStart.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                dateChooserStart.setEnabled(buttonStart.getSelection());
            }
        });

        buttonEnd = new Button(upperComp, SWT.CHECK);
        buttonEnd.setText("pana la");
        dateChooserEnd = new DateChooserCustom(upperComp);
        dateChooserEnd.setEnabled(false);

        buttonEnd.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                dateChooserEnd.setEnabled(buttonEnd.getSelection());
            }
        });

        new Label(upperComp, SWT.NONE).setText("Utilizator");
        ComboImage.CIDescriptor desc = new ComboImage.CIDescriptor();
        desc.setAddContentProposal(true);
        desc.setClazz(User.class);
        desc.setInput(new ArrayList<User>());
        desc.setTextMethodName("getNumeComplet");

        this.comboUsers = new ComboImage(upperComp, desc);
        comboUsers.setInput(UserController.findAll());

        Composite compAplica = new Composite(getContainer(), SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compAplica);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compAplica);

        ToolItem itemAplica = new ToolItem(new ToolBar(compAplica, SWT.RIGHT | SWT.FLAT), SWT.RIGHT | SWT.FLAT);
        itemAplica.setText("Aplica filtrele");
        itemAplica.setImage(AppImages.getImage16(AppImages.IMG_REFRESH));
        itemAplica.setHotImage(AppImages.getImage16Focus(AppImages.IMG_REFRESH));
        itemAplica.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                aplica();
            }
        });

        ProgressBarComposite progressBarComposite = new ProgressBarComposite(compAplica, SWT.SMOOTH);

        LiveSashForm mainSash = new LiveSashForm(getContainer(), SWT.VERTICAL | SWT.SMOOTH);
        mainSash.sashWidth = 4;
        mainSash.setLayout(new GridLayout(2, false));
        GridDataFactory.fillDefaults().grab(true, true).applyTo(mainSash);

        Composite otherComp = new Composite(mainSash, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(otherComp);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(otherComp);

        this.autoriTableViewer = new TableViewer(otherComp, SWT.FULL_SELECTION | SWT.BORDER | SWT.SINGLE);
        this.autoriTableViewer.getTable().setHeaderVisible(true);
        this.autoriTableViewer.getTable().setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this.autoriTableViewer.getControl());
        this.autoriTableViewer.getTable().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
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

                booksTableViewer.setInput(booksAndAuthorsMap.get(autor));
                booksImageComposite.setImage(null, null);
                if (booksTableViewer.getTable().getItemCount() > 0) {
                    booksTableViewer.getTable().select(0);
                    booksTableViewer.getTable().notifyListeners(SWT.Selection, new Event());
                }
            }
        });

        initViewerCols();
        WidgetTableUtil.customizeTable(this.autoriTableViewer.getTable(), getClass(), AUTORI_TABLE_KEY);
        WidgetCursorUtil.addHandCursorListener(this.autoriTableViewer.getTable());
        SWTeXtension.addColoredFocusListener(this.autoriTableViewer.getTable(), null);

        autorImageComposite = new ImageSelectorComposite(otherComp, null, null);
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

                setObservableProperty(carte.getTitlu());
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

    private void aplica() {
        booksAndAuthorsMap = new TreeMap<>();
        Date dataStart = null;
        if (buttonStart.getSelection()) {
            dataStart = dateChooserStart.getValue();
        }
        Date dataEnd = null;
        if (buttonEnd.getSelection()) {
            dataEnd = dateChooserEnd.getValue();
        }
        User user = (User) comboUsers.getSelectedElement();
        List<UserActivity> allActivities = user != null ? UserController.getReadedBookForUser(user.getId()) : UserController.getAllCartiCitite();
        for (UserActivity activity : allActivities) {
            if (dataStart != null) {
                if (activity.getCarteCitita().getDataStart() == null || activity.getCarteCitita().getDataStart().before(dataStart)) {
                    continue;
                }
            }
            if (dataEnd != null) {
                if (activity.getCarteCitita().getDataStop() == null || activity.getCarteCitita().getDataStop().after(dataEnd)) {
                    continue;
                }
            }
            final Carte carte = ApplicationService.getBookController().findOne(activity.getBookId());
            if (carte == null) {
                logger.error("Nu am gasit cartea cu id " + activity.getBookId());
                continue;
            }
            carte.setReadStartDate(activity.getCarteCitita().getDataStart());
            carte.setReadEndDate(activity.getCarteCitita().getDataStop());
            List<Autor> autori = AutorController.findByIdsOrderByNumeComplet(carte.getIdAutori());
            for (Autor autor : autori) {
                List<Carte> carti = booksAndAuthorsMap.get(autor);
                if (carti == null) {
                    carti = new ArrayList<>();
                }
                carti.add(carte);
                booksAndAuthorsMap.put(autor, carti);
            }
        }
        autoriTableViewer.setInput(booksAndAuthorsMap.keySet());
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
                case IDX_BOOK_COUNT: {
                    col.setLabelProvider(new UnifiedStyledLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            Autor autor = (Autor) element;
                            return booksAndAuthorsMap.get(autor).size() + "";
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.autoriTableViewer, col) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            Autor a = (Autor) e1;
                            Autor b = (Autor) e2;
                            return booksAndAuthorsMap.get(a).size() - booksAndAuthorsMap.get(b).size();
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
                            return StringUtil.romanianCompare(a.getTitluVolumSiSerie(), b.getTitluVolumSiSerie());
                        }

                    };
                    cSorter.setSorter(cSorter, AbstractColumnViewerSorter.ASC);
                    break;
                }
                case IDX_BOOK_START: {
                    col.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            Carte carte = (Carte) element;
                            if (carte.getReadStartDate() != null) {
                                return df.format(carte.getReadStartDate());
                            }
                            return "";
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.booksTableViewer, col) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            Carte a = (Carte) e1;
                            Carte b = (Carte) e2;
                            if (a.getReadStartDate() == null) {
                                return -1;
                            } else if (b.getReadStartDate() == null) {
                                return 1;
                            }
                            return a.getReadStartDate().compareTo(b.getReadStartDate());
                        }

                    };
                    cSorter.setSorter(cSorter, AbstractColumnViewerSorter.ASC);
                    break;
                }
                case IDX_BOOK_END: {
                    col.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(final Object element) {
                            Carte carte = (Carte) element;
                            if (carte.getReadEndDate() != null) {
                                return df.format(carte.getReadEndDate());
                            }
                            return "";
                        }
                    });
                    AbstractTableColumnViewerSorter cSorter = new AbstractTableColumnViewerSorter(this.booksTableViewer, col) {
                        @Override
                        protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                            Carte a = (Carte) e1;
                            Carte b = (Carte) e2;
                            if (a.getReadEndDate() == null) {
                                return -1;
                            } else if (b.getReadEndDate() == null) {
                                return 1;
                            }
                            return a.getReadEndDate().compareTo(b.getReadEndDate());
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

    @Override
    protected void customizeView() {
        setShellText("Cei mai cititi autori");
        setViewOptions(ADD_CANCEL);
    }
}
