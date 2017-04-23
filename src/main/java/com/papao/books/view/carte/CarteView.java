package com.papao.books.view.carte;

import com.mongodb.gridfs.GridFSDBFile;
import com.papao.books.controller.AutorController;
import com.papao.books.controller.BookController;
import com.papao.books.controller.UserController;
import com.papao.books.model.*;
import com.papao.books.view.AppImages;
import com.papao.books.view.auth.EncodeLive;
import com.papao.books.view.bones.impl.view.AbstractCSaveView;
import com.papao.books.view.custom.*;
import com.papao.books.view.custom.starrating.StarRating;
import com.papao.books.view.providers.ContentProposalProvider;
import com.papao.books.view.util.NumberUtil;
import com.papao.books.view.util.WidgetCompositeUtil;
import com.papao.books.view.view.AbstractView;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.IntegerFormatter;
import org.eclipse.nebula.widgets.formattedtext.NumberFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;
import java.util.List;

public class CarteView extends AbstractCSaveView {

    private static final Logger logger = Logger.getLogger(CarteView.class);

    private Carte carte;
    private final BookController carteController;
    private final UserController userController;
    private final AutorController autorController;
    private String observableProperty;

    private Text textTitlu;
    private Text textSubtitlu;
    private Text textEditura;
    private Text textAnAparitie;
    private Text textEditia;
    private FormattedText textLungime;
    private FormattedText textLatime;
    private FormattedText textGreutate;
    private FormattedText textNrPagini;
    private Text textSerie;
    private LinkedinCompositeAutori compositeAutori;
    private Text textIsbn;
    private LinkedinComposite compositeAutoriIlustratii;
    private LinkedinComposite compositeTehnoredactori;
    private Text textImprimerie;
    private Combo comboTipCoperta;
    private Combo comboLimba;
    private Text textGoodreadsUrl;
    private Text textWikiUrl;
    private Text textWebsite;
    private ImageSelectorComposite frontCoverComposite;
    private StarRating starRating;
    private ImageSelectorComposite backCoverComposite;
    private ImageSelectorComposite autografComposite;
    private LinkedinComposite compositeGenLiterar;

    private Combo comboTraducereDin;
    private LinkedinComposite compositeTraducatori;
    private DragAndDropTableComposite dragAndDropTableComposite;

    //editia princeps tab
    private Text textEditiaPrincepsTitlu;
    private Text textEditiaPrincepsEditura;
    private LinkedinComposite compositeEditiaPrincepsAutoriIlustratii;
    private Text textEditiaPrincepsTara;
    private Text textEditiaPrincepsAn;
    private Combo comboEditiaPrincepsLimba;

    //tags and description tag
    private LinkedinComposite compositeTags;
    private Text textDescriere;
    private Text textMotto;
    private ToolItem itemInformatiiEsentiale;
    private ToolItem itemTraducere;
    private ToolItem itemEditiaOriginala;
    private ToolItem itemTaguri;
    private ToolItem itemBookDetails;
    private ToolItem itemBackCover;
    private PremiiLiterareComposite premiiLiterareComposite;

    public CarteView(final Shell parent, final Carte carte,
                     final BookController carteController,
                     final UserController userController,
                     final AutorController autorController,
                     final int viewMode) {
        super(parent, viewMode, carte.getId());
        this.carte = carte;
        this.carteController = carteController;
        this.userController = userController;
        this.autorController = autorController;

        addComponents();
        populateFields();

        this.addObserver(frontCoverComposite);
        this.addObserver(backCoverComposite);
        markAsChanged();

        textTitlu.setFocus();
    }

    private void addComponents() {

        ToolBar toolBar = new ToolBar(getContainer(), SWT.RIGHT | SWT.FLAT);

        Label separator = new Label(getContainer(), SWT.SEPARATOR | SWT.HORIZONTAL);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(separator);

        final Composite mainComp = new Composite(getContainer(), SWT.NONE);
        mainComp.setLayout(new StackLayout());
        GridDataFactory.fillDefaults().grab(false, false).applyTo(mainComp);

        final Composite mainPropertiesComposite = createMainPropertiesTab(mainComp);

        itemInformatiiEsentiale = new ToolItem(toolBar, SWT.RADIO);
        itemInformatiiEsentiale.setText("Informatii esentiale");
        itemInformatiiEsentiale.setImage(AppImages.getImage16(AppImages.IMG_HOME));
        itemInformatiiEsentiale.setHotImage(AppImages.getImage16Focus(AppImages.IMG_HOME));
        itemInformatiiEsentiale.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (((ToolItem) event.widget).getSelection()) {
                    ((StackLayout) mainComp.getLayout()).topControl = mainPropertiesComposite;
                    mainComp.layout();
                }
            }
        });

        final Composite traducereComposite = createTraducereComposite(mainComp);

        itemTraducere = new ToolItem(toolBar, SWT.RADIO);
        itemTraducere.setText("Traducere");
        itemTraducere.setImage(AppImages.getImage16(AppImages.IMG_HOME));
        itemTraducere.setHotImage(AppImages.getImage16Focus(AppImages.IMG_HOME));
        itemTraducere.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (((ToolItem) event.widget).getSelection()) {
                    ((StackLayout) mainComp.getLayout()).topControl = traducereComposite;
                    mainComp.layout();
                }
            }
        });

        final Composite bookDetailsComposite = createBookDetailsTab(mainComp);

        itemBookDetails = new ToolItem(toolBar, SWT.RADIO);
        itemBookDetails.setText("Detalii");
        itemBookDetails.setImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_SIMPLE_NEXT));
        itemBookDetails.setHotImage(AppImages.getImageMiscFocusByName(AppImages.IMG_MISC_SIMPLE_NEXT));
        itemBookDetails.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (((ToolItem) event.widget).getSelection()) {
                    ((StackLayout) mainComp.getLayout()).topControl = bookDetailsComposite;
                    mainComp.layout();
                }
            }
        });

        final Composite editiaOriginalaComposite = createEditiaOriginalaTab(mainComp);

        itemEditiaOriginala = new ToolItem(toolBar, SWT.RADIO);
        itemEditiaOriginala.setText("Editia originala");
        itemEditiaOriginala.setImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_SIMPLE_NEXT));
        itemEditiaOriginala.setHotImage(AppImages.getImageMiscFocusByName(AppImages.IMG_MISC_SIMPLE_NEXT));
        itemEditiaOriginala.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (((ToolItem) event.widget).getSelection()) {
                    ((StackLayout) mainComp.getLayout()).topControl = editiaOriginalaComposite;
                    mainComp.layout();
                }
            }
        });

        final Composite tagsAndDescriptionComposite = createTagsAndDescriptionTab(mainComp);

        itemTaguri = new ToolItem(toolBar, SWT.RADIO);
        itemTaguri.setText("Taguri");
        itemTaguri.setImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_SIMPLE_NEXT));
        itemTaguri.setHotImage(AppImages.getImageMiscFocusByName(AppImages.IMG_MISC_SIMPLE_NEXT));
        itemTaguri.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (((ToolItem) event.widget).getSelection()) {
                    ((StackLayout) mainComp.getLayout()).topControl = tagsAndDescriptionComposite;
                    mainComp.layout();
                }
            }
        });

        final Composite copertaSpateComposite = createCopertaSpateTab(mainComp);

        itemBackCover = new ToolItem(toolBar, SWT.RADIO);
        itemBackCover.setText("Coperta spate");
        itemBackCover.setImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_SIMPLE_NEXT));
        itemBackCover.setHotImage(AppImages.getImageMiscFocusByName(AppImages.IMG_MISC_SIMPLE_NEXT));
        itemBackCover.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (((ToolItem) event.widget).getSelection()) {
                    ((StackLayout) mainComp.getLayout()).topControl = copertaSpateComposite;
                    mainComp.layout();
                }
            }
        });

        ((StackLayout) mainComp.getLayout()).topControl = mainPropertiesComposite;
        itemInformatiiEsentiale.setSelection(true);

        //traverse next
        handleTraverseNextEvent(compositeTehnoredactori.getTextSearch(), itemInformatiiEsentiale, itemTraducere);
        handleTraverseNextEvent(compositeTraducatori.getTextSearch(), itemTraducere, itemBookDetails);
        handleTraverseNextEvent(comboLimba, itemBookDetails, itemEditiaOriginala);
        handleTraverseNextEvent(premiiLiterareComposite.getTable(), itemEditiaOriginala, itemTaguri);
        handleTraverseNextEvent(compositeTags.getTextSearch(), itemTaguri, itemBackCover);

        //traverse previous
        handleTraversePreviousEvent(textMotto, itemTaguri, itemEditiaOriginala);
        handleTraversePreviousEvent(textEditiaPrincepsTitlu, itemEditiaOriginala, itemBookDetails);
        handleTraversePreviousEvent(textGoodreadsUrl, itemBookDetails, itemTraducere);
        handleTraversePreviousEvent(comboTraducereDin, itemTraducere, itemInformatiiEsentiale);

        WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());
    }

    private Composite createTraducereComposite(Composite mainComp) {
        Composite comp = new Composite(mainComp, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(6).equalWidth(false).applyTo(comp);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(comp);

        label(comp, "Traducere din");
        comboTraducereDin = new Combo(comp, SWT.READ_ONLY);
        comboTraducereDin.setItems(Limba.getComboItems());

        label(comp, "");
        label(comp, "");
        label(comp, "");
        label(comp, "");

        label(comp, "Traducatori");
        this.compositeTraducatori = new LinkedinComposite(comp, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "traducatori"), carte.getTraducatori());
        ((GridData) compositeTraducatori.getLayoutData()).horizontalSpan = 5;

        label(comp, "Documente");
        dragAndDropTableComposite = new DragAndDropTableComposite(comp, carteController, carte, false);
        ((GridData) dragAndDropTableComposite.getLayoutData()).horizontalSpan = 5;

        return comp;
    }

    private void label(Composite parent, String labelName) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(labelName);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.BEGINNING).applyTo(label);
    }

    private Composite createMainPropertiesTab(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(5, 5, 5, 5).applyTo(comp);

        Composite mainCompLeft = new Composite(comp, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(6).equalWidth(false).applyTo(mainCompLeft);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(mainCompLeft);

        Composite compImages = new Composite(comp, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).extendedMargins(10, 0, 0, 0).applyTo(compImages);
        GridDataFactory.fillDefaults().grab(false, true).applyTo(compImages);

        starRating = new StarRating(compImages, SWT.READ_ONLY, StarRating.Size.SMALL, 5);
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(starRating);

        GridFSDBFile frontCover = getGridFsFile(carte.getCopertaFata().getId());
        Image image = null;
        String fileName = null;
        if (frontCover != null) {
            image = new Image(Display.getDefault(), frontCover.getInputStream());
            fileName = frontCover.getFilename();
        }

        frontCoverComposite = new ImageSelectorComposite(compImages, image, fileName, carteController.getAppImagesFolder());
        GridData data = (GridData) frontCoverComposite.getLayoutData();
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = false;
        data.verticalAlignment = SWT.BEGINNING;
        data.horizontalAlignment = SWT.CENTER;

        label(mainCompLeft, "Titlu");
        this.textTitlu = new Text(mainCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(5, 1).applyTo(this.textTitlu);
        this.textTitlu.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                markAsChanged();
            }
        });

        label(mainCompLeft, "Subtitlu");
        this.textSubtitlu = new Text(mainCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(5, 1).applyTo(this.textSubtitlu);

        label(mainCompLeft, "Autori");
        compositeAutori = new LinkedinCompositeAutori(mainCompLeft, carte.getIdAutori(), autorController);
        ((GridData) compositeAutori.getLayoutData()).horizontalSpan = 5;
        ((GridData) compositeAutori.getLayoutData()).grabExcessHorizontalSpace = true;
        this.compositeAutori.getCompSelections().addListener(SWT.Resize, new Listener() {
            @Override
            public void handleEvent(Event event) {
                markAsChanged();
            }
        });

        label(mainCompLeft, "Serie");
        this.textSerie = new Text(mainCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(this.textSerie);
        ContentProposalProvider.addContentProposal(textSerie, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "serie"));

        label(mainCompLeft, "ISBN");
        this.textIsbn = new Text(mainCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(false, false).span(1, 1).applyTo(this.textIsbn);

        label(mainCompLeft, "Editura");
        this.textEditura = new Text(mainCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(false, false).span(3, 1).applyTo(this.textEditura);
        ContentProposalProvider.addContentProposal(textEditura, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "editura"));

        label(mainCompLeft, "Coperta");
        comboTipCoperta = new Combo(mainCompLeft, SWT.READ_ONLY);
        GridDataFactory.fillDefaults().grab(false, false).span(1, 1).applyTo(this.comboTipCoperta);
        comboTipCoperta.setItems(TipCoperta.getComboItems());

        label(mainCompLeft, "Imprimerie");
        this.textImprimerie = new Text(mainCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(5, 1).applyTo(this.textImprimerie);

        label(mainCompLeft, "An aparitie");
        textAnAparitie = new Text(mainCompLeft, SWT.BORDER);
        ContentProposalProvider.addContentProposal(textAnAparitie, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "anAparitie"));
        GridDataFactory.fillDefaults().grab(false, false).hint(40, SWT.DEFAULT).applyTo(this.textAnAparitie);

        label(mainCompLeft, "Numar pagini");
        this.textNrPagini = new FormattedText(mainCompLeft, SWT.BORDER);
        this.textNrPagini.setFormatter(new IntegerFormatter());
        GridDataFactory.fillDefaults().grab(false, false).hint(40, SWT.DEFAULT).applyTo(this.textNrPagini.getControl());
        ((NumberFormatter) this.textNrPagini.getFormatter()).setFixedLengths(false, true);

        label(mainCompLeft, "Editia");
        this.textEditia = new Text(mainCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(1, 1).applyTo(this.textEditia);

        label(mainCompLeft, "Tehnoredactori");
        this.compositeTehnoredactori = new LinkedinComposite(mainCompLeft, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "tehnoredactori"), carte.getTehnoredactori());
        ((GridData) compositeTehnoredactori.getLayoutData()).horizontalSpan = 5;
        ((GridData) compositeTehnoredactori.getLayoutData()).grabExcessHorizontalSpace = true;

        return comp;
    }

    private Composite createEditiaOriginalaTab(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(6).equalWidth(false).extendedMargins(5, 5, 5, 5).applyTo(comp);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(comp);

        label(comp, "Titlu original");
        this.textEditiaPrincepsTitlu = new Text(comp, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(5, 1).applyTo(this.textEditiaPrincepsTitlu);

        label(comp, "Tara");
        textEditiaPrincepsTara = new Text(comp, SWT.BORDER);
        ContentProposalProvider.addContentProposal(textEditiaPrincepsTara, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "editiaOriginala.tara"));
        GridDataFactory.fillDefaults().span(3, 1).applyTo(this.textEditiaPrincepsTara);

        new Label(comp, SWT.NONE);
        new Label(comp, SWT.NONE);

        label(comp, "Limba originala");
        comboEditiaPrincepsLimba = new Combo(comp, SWT.READ_ONLY);
        GridDataFactory.fillDefaults().span(3, 1).grab(false, false).applyTo(this.comboEditiaPrincepsLimba);
        comboEditiaPrincepsLimba.setItems(Limba.getComboItems());

        new Label(comp, SWT.NONE);
        new Label(comp, SWT.NONE);

        label(comp, "Editura");
        this.textEditiaPrincepsEditura = new Text(comp, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(false, false).span(4, 1).hint(150, SWT.DEFAULT).applyTo(this.textEditiaPrincepsEditura);
        ContentProposalProvider.addContentProposal(textEditiaPrincepsEditura, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "editiaOriginala.editura"));

        new Label(comp, SWT.NONE);

        label(comp, "An aparitie");
        textEditiaPrincepsAn = new Text(comp, SWT.BORDER);
        ContentProposalProvider.addContentProposal(textEditiaPrincepsAn, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "editiaOriginala.an"));
        GridDataFactory.fillDefaults().hint(40, SWT.DEFAULT).applyTo(this.textEditiaPrincepsAn);

        new Label(comp, SWT.NONE);
        new Label(comp, SWT.NONE);
        new Label(comp, SWT.NONE);
        new Label(comp, SWT.NONE);

        label(comp, "Autori ilustratii");
        this.compositeEditiaPrincepsAutoriIlustratii = new LinkedinComposite(comp, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "editiaOriginala.ilustratori"), carte.getEditiaOriginala().getIlustratori());
        ((GridData) compositeEditiaPrincepsAutoriIlustratii.getLayoutData()).horizontalSpan = 5;
        ((GridData) compositeEditiaPrincepsAutoriIlustratii.getLayoutData()).grabExcessHorizontalSpace = true;

        label(comp, "Premii literare");
        premiiLiterareComposite = new PremiiLiterareComposite(comp, this.carte.getPremii());
        ((GridData) premiiLiterareComposite.getLayoutData()).horizontalSpan = 5;

        return comp;
    }

    private Composite createTagsAndDescriptionTab(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(5, 5, 5, 5).applyTo(comp);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(comp);

        label(comp, "Motto");
        this.textMotto = new Text(comp, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().grab(true, true).hint(400, 75).applyTo(textMotto);
        textMotto.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (event.detail == SWT.TRAVERSE_TAB_NEXT) {
                    event.doit = true;
                    textDescriere.setFocus();
                }
            }
        });
        textMotto.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (event.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
                    itemTaguri.setSelection(false);
                    itemEditiaOriginala.setSelection(true);
                    itemEditiaOriginala.notifyListeners(SWT.Selection, new Event());
                }
            }
        });

        label(comp, "Descriere");
        this.textDescriere = new Text(comp, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().grab(true, true).hint(400, 150).applyTo(textDescriere);
        this.textDescriere.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (event.detail == SWT.TRAVERSE_TAB_NEXT) {
                    event.doit = true;
                    compositeTags.getTextSearch().setFocus();
                }
            }
        });

        label(comp, "Taguri");
        this.compositeTags = new LinkedinComposite(comp, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "tags"), carte.getTags());
        ((GridData) compositeTags.getLayoutData()).widthHint = 350;

        return comp;
    }

    private void handleTraverseNextEvent(final Widget source,
                                         final ToolItem current,
                                         final ToolItem next) {
        source.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (event.detail == SWT.TRAVERSE_TAB_NEXT) {
                    current.setSelection(false);
                    next.setSelection(true);
                    next.notifyListeners(SWT.Selection, new Event());
                }
            }
        });
    }

    private void handleTraversePreviousEvent(final Widget source,
                                             final ToolItem current,
                                             final ToolItem previous) {
        source.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (event.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
                    current.setSelection(false);
                    previous.setSelection(true);
                    previous.notifyListeners(SWT.Selection, new Event());
                }
            }
        });
    }

    private Composite createBookDetailsTab(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(8).extendedMargins(5, 5, 5, 5).applyTo(comp);

        label(comp, "Goodreads");
        this.textGoodreadsUrl = new Text(comp, SWT.BORDER);
        GridDataFactory.fillDefaults().span(7, 1).grab(true, false).hint(400, SWT.DEFAULT).applyTo(this.textGoodreadsUrl);
        this.textGoodreadsUrl.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                encodeUrl((Text) event.widget);
            }
        });

        label(comp, "Wikipedia");
        this.textWikiUrl = new Text(comp, SWT.BORDER);
        GridDataFactory.fillDefaults().span(7, 1).grab(true, false).hint(400, SWT.DEFAULT).applyTo(this.textWikiUrl);
        this.textWikiUrl.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                encodeUrl((Text) event.widget);
            }
        });

        label(comp, "Pagina web");
        this.textWebsite = new Text(comp, SWT.BORDER);
        GridDataFactory.fillDefaults().span(7, 1).grab(true, false).hint(400, SWT.DEFAULT).applyTo(this.textWebsite);
        this.textWebsite.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                encodeUrl((Text) event.widget);
            }
        });

        label(comp, "Gen literar");
        this.compositeGenLiterar = new LinkedinComposite(comp, GenLiterar.class, carte.getGenLiterar());
        ((GridData) compositeGenLiterar.getLayoutData()).horizontalSpan = 7;

        label(comp, "Autori ilustratii");
        this.compositeAutoriIlustratii = new LinkedinComposite(comp, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "autoriIlustratii"), carte.getAutoriIlustratii());
        ((GridData) compositeAutoriIlustratii.getLayoutData()).horizontalSpan = 7;
        ((GridData) compositeAutoriIlustratii.getLayoutData()).grabExcessHorizontalSpace = true;

        label(comp, "Greutate (kg)");
        this.textGreutate = new FormattedText(comp, SWT.BORDER);
        this.textGreutate.setFormatter(NumberUtil.getFormatter(2, true));
        GridDataFactory.fillDefaults().grab(false, false).hint(40, SWT.DEFAULT).applyTo(this.textGreutate.getControl());
        ((NumberFormatter) this.textGreutate.getFormatter()).setFixedLengths(false, true);

        label(comp, "Lungime (cm)");
        this.textLungime = new FormattedText(comp, SWT.BORDER);
        this.textLungime.setFormatter(NumberUtil.getFormatter(0, true));
        GridDataFactory.fillDefaults().grab(false, false).hint(40, SWT.DEFAULT).applyTo(this.textLungime.getControl());
        ((NumberFormatter) this.textLungime.getFormatter()).setFixedLengths(false, true);

        label(comp, "Latime (cm)");
        this.textLatime = new FormattedText(comp, SWT.BORDER);
        this.textLatime.setFormatter(NumberUtil.getFormatter(0, true));
        GridDataFactory.fillDefaults().grab(false, false).hint(40, SWT.DEFAULT).applyTo(this.textLatime.getControl());
        ((NumberFormatter) this.textLatime.getFormatter()).setFixedLengths(false, true);

        label(comp, "");
        label(comp, "");

        //read only combos receive focus on OSX, only after enabling
        // System Preferences -> Keyboard -> Keyboard Shortcuts -> All Controls
        //see https://bugs.eclipse.org/bugs/show_bug.cgi?id=376039
        label(comp, "Limba");
        comboLimba = new Combo(comp, SWT.READ_ONLY);
        comboLimba.setItems(Limba.getComboItems());

        return comp;
    }

    private Composite createCopertaSpateTab(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(5, 5, 5, 5).applyTo(comp);

        new Label(comp, SWT.NONE).setText("Coperta spate");

        new Label(comp, SWT.NONE).setText("Autograf");

        GridFSDBFile backCover = getGridFsFile(carte.getCopertaSpate().getId());
        Image image = null;
        String fileName = null;
        if (backCover != null) {
            image = new Image(Display.getDefault(), backCover.getInputStream());
            fileName = backCover.getFilename();
        }

        backCoverComposite = new ImageSelectorComposite(comp, image, fileName, carteController.getAppImagesFolder());
        GridData backCoverData = (GridData) backCoverComposite.getLayoutData();
        backCoverData.grabExcessHorizontalSpace = false;
        backCoverData.grabExcessVerticalSpace = false;
        backCoverData.verticalAlignment = SWT.BEGINNING;
        backCoverData.horizontalAlignment = SWT.BEGINNING;

        GridFSDBFile autograf = getGridFsFile(carte.getAutograf().getId());
        Image autografImage = null;
        String autografFileName = null;
        if (autograf != null) {
            autografImage = new Image(Display.getDefault(), autograf.getInputStream());
            autografFileName = autograf.getFilename();
        }

        autografComposite = new ImageSelectorComposite(comp, autografImage, autografFileName, carteController.getAppImagesFolder());
        GridData data = (GridData) autografComposite.getLayoutData();
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = false;
        data.verticalAlignment = SWT.BEGINNING;
        data.horizontalAlignment = SWT.BEGINNING;


        return comp;
    }

    private GridFSDBFile getGridFsFile(ObjectId imageId) {
        return carteController.getDocumentData(imageId);
    }

    private void populateFields() {
        this.starRating.setCurrentNumberOfStars(userController.getPersonalRating(EncodeLive.getIdUser(), this.carte.getId()));
        this.textTitlu.setText(this.carte.getTitlu());
        this.textSubtitlu.setText(this.carte.getSubtitlu());
        this.textEditura.setText(this.carte.getEditura());
        this.textAnAparitie.setText(this.carte.getAnAparitie());
        this.textLungime.setValue(this.carte.getInaltime());
        this.textLatime.setValue(this.carte.getLatime());
        this.textNrPagini.setValue(this.carte.getNrPagini());
        this.textGreutate.setValue(this.carte.getGreutate());
        this.textSerie.setText(this.carte.getSerie());
        this.textEditia.setText(this.carte.getEditia());
        this.textIsbn.setText(this.carte.getIsbn());
        this.textImprimerie.setText(this.carte.getImprimerie());
        this.comboTipCoperta.select(comboTipCoperta.indexOf(this.carte.getTipCoperta().name()));
        this.comboLimba.select(comboLimba.indexOf(this.carte.getLimba().name()));
        this.comboTraducereDin.select(comboTraducereDin.indexOf(this.carte.getTraducereDin().name()));
        this.textGoodreadsUrl.setText(this.carte.getGoodreadsUrl());
        this.textWikiUrl.setText(this.carte.getWikiUrl());
        this.textWebsite.setText(this.carte.getWebsite());

        this.textEditiaPrincepsTitlu.setText(this.carte.getEditiaOriginala().getTitlu());
        this.comboEditiaPrincepsLimba.select(comboEditiaPrincepsLimba.indexOf(this.carte.getEditiaOriginala().getLimba().name()));
        this.textEditiaPrincepsEditura.setText(this.carte.getEditiaOriginala().getEditura());
        this.textEditiaPrincepsTara.setText(this.carte.getEditiaOriginala().getTara());
        this.textEditiaPrincepsAn.setText(this.carte.getEditiaOriginala().getAn());

        this.textMotto.setText(this.carte.getMotto());
        this.textDescriere.setText(this.carte.getDescriere());

        if (!isViewEnabled()) {
            WidgetCompositeUtil.enableGUI(getContainer(), false);
            WidgetCompositeUtil.enableGUI(getCompHIRE(), false);
            getContainer().setEnabled(true);
        }
    }

    @Override
    protected void saveData() throws Exception {
        this.carte.setTitlu(this.textTitlu.getText());
        this.carte.setSubtitlu(this.textSubtitlu.getText());
        this.carte.setEditura(textEditura.getText());
        this.carte.setAnAparitie(textAnAparitie.getText());
        this.carte.setInaltime(Integer.valueOf(textLungime.getValue().toString()));
        this.carte.setLatime(Integer.valueOf(textLatime.getValue().toString()));
        this.carte.setNrPagini(Integer.valueOf(textNrPagini.getValue().toString()));
        this.carte.setGreutate(Double.valueOf(textGreutate.getValue().toString()));
        this.carte.setSerie(textSerie.getText());
        this.carte.setEditia(textEditia.getText());
        this.carte.setIsbn(textIsbn.getText());
        this.carte.setAutoriIlustratii(compositeAutoriIlustratii.getValoriIntroduse());
        this.carte.setTehnoredactori(compositeTehnoredactori.getValoriIntroduse());
        this.carte.setImprimerie(textImprimerie.getText());
        this.carte.setTipCoperta(TipCoperta.valueOf(comboTipCoperta.getText()));
        this.carte.setLimba(Limba.valueOf(comboLimba.getText()));
        this.carte.setGoodreadsUrl(textGoodreadsUrl.getText());
        this.carte.setWikiUrl(textWikiUrl.getText());
        this.carte.setWebsite(textWebsite.getText());
        this.carte.setIdAutori(compositeAutori.getSelectedIds());
        this.carte.setMotto(textMotto.getText());
        this.carte.setDescriere(textDescriere.getText());
        this.carte.setPremii(premiiLiterareComposite.getResult());

        this.carte.setTraducatori(compositeTraducatori.getValoriIntroduse());
        this.carte.setTraducereDin(Limba.valueOf(comboTraducereDin.getText()));

        if (frontCoverComposite.imageChanged()) {
            carteController.removeDocument(carte.getCopertaFata().getId());
            carte.setCopertaFata(null);
            carte.setCopertaFata(carteController.saveDocument(frontCoverComposite));
        }

        EditiaOriginala editiaOriginala = carte.getEditiaOriginala();
        editiaOriginala.setTitlu(this.textEditiaPrincepsTitlu.getText());
        editiaOriginala.setEditura(this.textEditiaPrincepsEditura.getText());
        editiaOriginala.setLimba(Limba.valueOf(comboEditiaPrincepsLimba.getText()));
        editiaOriginala.setAn(this.textEditiaPrincepsAn.getText());
        editiaOriginala.setTara(this.textEditiaPrincepsTara.getText());
        editiaOriginala.setIlustratori(compositeEditiaPrincepsAutoriIlustratii.getValoriIntroduse());
        this.carte.setEditiaOriginala(editiaOriginala);

        if (backCoverComposite.imageChanged()) {
            carteController.removeDocument(carte.getCopertaSpate().getId());
            carte.setCopertaSpate(null);
            carte.setCopertaSpate(carteController.saveDocument(backCoverComposite));
        }

        if (autografComposite.imageChanged()) {
            carteController.removeDocument(carte.getAutograf().getId());
            carte.setAutograf(null);
            carte.setAutograf(carteController.saveDocument(autografComposite));
        }

        List<GenLiterar> genuriLiterare = new ArrayList<>();
        for (Enum value : compositeGenLiterar.getEnumValues()) {
            genuriLiterare.add((GenLiterar) value);
        }
        carte.setGenLiterar(genuriLiterare);
        carte.setTags(compositeTags.getValoriIntroduse());

        if (dragAndDropTableComposite.isChanged()) {
            for (DocumentData doc : dragAndDropTableComposite.getDeleted()) {
                carteController.removeDocument(doc.getId());
            }
            List<DocumentData> documents = dragAndDropTableComposite.getResult();
            List<DocumentData> docs = new ArrayList<>();
            for (DocumentData doc : documents) {
                if (doc.getId() == null) {
                    docs.add(carteController.saveDocument(doc.getFilePath(), doc.getContentType()));
                } else {
                    docs.add(doc);
                }
            }
            carte.setDocuments(docs);
        }

        carte = carteController.save(carte);

        userController.saveBookRatingForCurrentUser(carte.getId(), starRating.getCurrentNumberOfStars());
    }

    @Override
    public final void customizeView() {
        setShellStyle(SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE);
        setViewOptions(AbstractView.ADD_CANCEL | AbstractView.ADD_OK);
        setObjectName("carte");
    }

    @Override
    protected boolean validate() {
        try {
            if (StringUtils.isEmpty(this.textTitlu.getText())) {
                SWTeXtension.displayMessageW("Titlul cartii nu este introdus!");
                textTitlu.setFocus();
                return false;
            }

        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return false;
        }
        return true;
    }

    public final Carte getCarte() {
        return this.carte;
    }

    private void markAsChanged() {
        observableProperty = compositeAutori.getGoogleSearchTerm() + " - " + this.textTitlu.getText();
        if (!observableProperty.equals(" - ")) {
            if (compositeAutori.getGoogleSearchTerm().isEmpty()) {
                getBigLabelText().setText(this.textTitlu.getText());
            } else if (this.textTitlu.getText().isEmpty()) {
                getBigLabelText().setText(compositeAutori.getGoogleSearchTerm());
            } else {
                getBigLabelText().setText(observableProperty);
            }
        }
        setChanged();
        notifyObservers();
    }

    @Override
    public String getObservableProperty() {
        return this.observableProperty;
    }
}
