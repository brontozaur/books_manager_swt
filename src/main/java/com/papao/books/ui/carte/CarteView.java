package com.papao.books.ui.carte;

import com.github.haixing_hu.swt.starrating.StarRating;
import com.papao.books.ApplicationService;
import com.papao.books.controller.ApplicationController;
import com.papao.books.controller.UserController;
import com.papao.books.model.*;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.auth.EncodeLive;
import com.papao.books.ui.custom.DragAndDropTableComposite;
import com.papao.books.ui.custom.ImageSelectorComposite;
import com.papao.books.ui.custom.LinkedinComposite;
import com.papao.books.ui.custom.LinkedinCompositeAutori;
import com.papao.books.ui.providers.ContentProposalProvider;
import com.papao.books.ui.util.NumberUtil;
import com.papao.books.ui.util.WidgetCompositeUtil;
import com.papao.books.ui.view.AbstractCSaveView;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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
    private Text textLocatie;
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
                     final int viewMode) {
        super(parent, viewMode, carte.getId());
        this.carte = carte;

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
        itemInformatiiEsentiale.setImage(AppImages.getImage16(AppImages.IMG_ARROW_UP_OPAL));
//        itemInformatiiEsentiale.setHotImage(AppImages.getImage16Focus(AppImages.IMG_ARROW_UP_OPAL));
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
        itemTraducere.setImage(AppImages.getImage16(AppImages.IMG_ARROW_RIGHT_OPAL));
//        itemTraducere.setHotImage(AppImages.getImage16Focus(AppImages.IMG_ARROW_RIGHT_OPAL));
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
        itemBookDetails.setImage(AppImages.getImage16(AppImages.IMG_ARROW_RIGHT_OPAL));
//        itemBookDetails.setHotImage(AppImages.getImage16Focus(AppImages.IMG_ARROW_RIGHT_OPAL));
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
        itemEditiaOriginala.setImage(AppImages.getImage16(AppImages.IMG_ARROW_RIGHT_OPAL));
//        itemEditiaOriginala.setHotImage(AppImages.getImage16Focus(AppImages.IMG_ARROW_RIGHT_OPAL));
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
        itemTaguri.setImage(AppImages.getImage16(AppImages.IMG_ARROW_RIGHT_OPAL));
//        itemTaguri.setHotImage(AppImages.getImage16Focus(AppImages.IMG_ARROW_RIGHT_OPAL));
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
        itemBackCover.setImage(AppImages.getImage16(AppImages.IMG_ARROW_RIGHT_OPAL));
//        itemBackCover.setHotImage(AppImages.getImage16Focus(AppImages.IMG_ARROW_RIGHT_OPAL));
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
        this.compositeTraducatori = new LinkedinComposite(comp, ApplicationController.getDistinctFieldAsContentProposal(ApplicationService.getApplicationConfig().getBooksCollectionName(), "traducatori"), carte.getTraducatori());
        ((GridData) compositeTraducatori.getLayoutData()).horizontalSpan = 5;

        label(comp, "Documente");
        dragAndDropTableComposite = new DragAndDropTableComposite(comp, carte, false);
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

        Image frontCover = ApplicationService.getBookController().getImage(carte.getCopertaFata());

        frontCoverComposite = new ImageSelectorComposite(compImages, frontCover, carte.getCopertaFata().getFileName());
        GridData data = frontCoverComposite.getLayoutData();
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
        compositeAutori = new LinkedinCompositeAutori(mainCompLeft, carte.getIdAutori());
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
        ContentProposalProvider.addContentProposal(textSerie, ApplicationController.getDistinctFieldAsContentProposal(ApplicationService.getApplicationConfig().getBooksCollectionName(), "serie"));

        label(mainCompLeft, "ISBN");
        this.textIsbn = new Text(mainCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(false, false).span(1, 1).applyTo(this.textIsbn);

        label(mainCompLeft, "Editura");
        this.textEditura = new Text(mainCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(false, false).span(3, 1).applyTo(this.textEditura);
        ContentProposalProvider.addContentProposal(textEditura, ApplicationController.getDistinctFieldAsContentProposal(ApplicationService.getApplicationConfig().getBooksCollectionName(), "editura"));

        label(mainCompLeft, "Coperta");
        comboTipCoperta = new Combo(mainCompLeft, SWT.READ_ONLY);
        GridDataFactory.fillDefaults().grab(false, false).span(1, 1).applyTo(this.comboTipCoperta);
        comboTipCoperta.setItems(TipCoperta.getComboItems());

        label(mainCompLeft, "Imprimerie");
        this.textImprimerie = new Text(mainCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(this.textImprimerie);

        label(mainCompLeft, "Locatie");
        this.textLocatie = new Text(mainCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(1, 1).applyTo(this.textLocatie);
        ContentProposalProvider.addContentProposal(textLocatie, ApplicationController.getDistinctFieldAsContentProposal(ApplicationService.getApplicationConfig().getBooksCollectionName(), "locatie"));

        label(mainCompLeft, "An aparitie");
        textAnAparitie = new Text(mainCompLeft, SWT.BORDER);
        ContentProposalProvider.addContentProposal(textAnAparitie, ApplicationController.getDistinctFieldAsContentProposal(ApplicationService.getApplicationConfig().getBooksCollectionName(), "anAparitie"));
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
        this.compositeTehnoredactori = new LinkedinComposite(mainCompLeft, ApplicationController.getDistinctFieldAsContentProposal(ApplicationService.getApplicationConfig().getBooksCollectionName(), "tehnoredactori"), carte.getTehnoredactori());
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
        ContentProposalProvider.addContentProposal(textEditiaPrincepsTara, ApplicationController.getDistinctFieldAsContentProposal(ApplicationService.getApplicationConfig().getBooksCollectionName(), "editiaOriginala.tara"));
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
        ContentProposalProvider.addContentProposal(textEditiaPrincepsEditura, ApplicationController.getDistinctFieldAsContentProposal(ApplicationService.getApplicationConfig().getBooksCollectionName(), "editiaOriginala.editura"));

        new Label(comp, SWT.NONE);

        label(comp, "An aparitie");
        textEditiaPrincepsAn = new Text(comp, SWT.BORDER);
        ContentProposalProvider.addContentProposal(textEditiaPrincepsAn, ApplicationController.getDistinctFieldAsContentProposal(ApplicationService.getApplicationConfig().getBooksCollectionName(), "editiaOriginala.an"));
        GridDataFactory.fillDefaults().hint(40, SWT.DEFAULT).applyTo(this.textEditiaPrincepsAn);

        new Label(comp, SWT.NONE);
        new Label(comp, SWT.NONE);
        new Label(comp, SWT.NONE);
        new Label(comp, SWT.NONE);

        label(comp, "Autori ilustratii");
        this.compositeEditiaPrincepsAutoriIlustratii = new LinkedinComposite(comp, ApplicationController.getDistinctFieldAsContentProposal(ApplicationService.getApplicationConfig().getBooksCollectionName(), "editiaOriginala.ilustratori"), carte.getEditiaOriginala().getIlustratori());
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
        this.compositeTags = new LinkedinComposite(comp, ApplicationController.getDistinctFieldAsContentProposal(ApplicationService.getApplicationConfig().getBooksCollectionName(), "tags"), carte.getTags());
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
        this.compositeAutoriIlustratii = new LinkedinComposite(comp, ApplicationController.getDistinctFieldAsContentProposal(ApplicationService.getApplicationConfig().getBooksCollectionName(), "autoriIlustratii"), carte.getAutoriIlustratii());
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

        Image backCover = ApplicationService.getBookController().getImage(carte.getCopertaSpate());

        backCoverComposite = new ImageSelectorComposite(comp, backCover, carte.getCopertaSpate().getFileName());
        GridData backCoverData = backCoverComposite.getLayoutData();
        backCoverData.grabExcessHorizontalSpace = false;
        backCoverData.grabExcessVerticalSpace = false;
        backCoverData.verticalAlignment = SWT.BEGINNING;
        backCoverData.horizontalAlignment = SWT.BEGINNING;

        Image autograf = ApplicationService.getBookController().getImage(carte.getAutograf());

        autografComposite = new ImageSelectorComposite(comp, autograf, carte.getAutograf().getFileName());
        GridData data = autografComposite.getLayoutData();
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = false;
        data.verticalAlignment = SWT.BEGINNING;
        data.horizontalAlignment = SWT.BEGINNING;


        return comp;
    }

    private void populateFields() {
        this.starRating.setCurrentNumberOfStars(UserController.getPersonalRating(EncodeLive.getIdUser(), this.carte.getId()));
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
        this.textLocatie.setText(this.carte.getLocatie());
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
        this.carte.setTitlu(this.textTitlu.getText().trim());
        this.carte.setSubtitlu(this.textSubtitlu.getText().trim());
        this.carte.setEditura(textEditura.getText().trim());
        this.carte.setAnAparitie(textAnAparitie.getText().trim());
        this.carte.setInaltime(Integer.valueOf(textLungime.getValue().toString()));
        this.carte.setLatime(Integer.valueOf(textLatime.getValue().toString()));
        this.carte.setNrPagini(Integer.valueOf(textNrPagini.getValue().toString()));
        this.carte.setGreutate(Double.valueOf(textGreutate.getValue().toString()));
        this.carte.setSerie(textSerie.getText().trim());
        this.carte.setEditia(textEditia.getText().trim());
        this.carte.setIsbn(textIsbn.getText().trim());
        this.carte.setAutoriIlustratii(compositeAutoriIlustratii.getValoriIntroduse());
        this.carte.setTehnoredactori(compositeTehnoredactori.getValoriIntroduse());
        this.carte.setImprimerie(textImprimerie.getText().trim());
        this.carte.setLocatie(textLocatie.getText().trim());
        this.carte.setTipCoperta(TipCoperta.valueOf(comboTipCoperta.getText()));
        this.carte.setLimba(Limba.valueOf(comboLimba.getText()));
        this.carte.setGoodreadsUrl(textGoodreadsUrl.getText().trim());
        this.carte.setWikiUrl(textWikiUrl.getText().trim());
        this.carte.setWebsite(textWebsite.getText().trim());
        this.carte.setIdAutori(compositeAutori.getSelectedIds());
        this.carte.setMotto(textMotto.getText().trim());
        this.carte.setDescriere(textDescriere.getText().trim());
        this.carte.setPremii(premiiLiterareComposite.getResult());

        this.carte.setTraducatori(compositeTraducatori.getValoriIntroduse());
        this.carte.setTraducereDin(Limba.valueOf(comboTraducereDin.getText()));

        if (frontCoverComposite.imageChanged()) {
            if (carte.getCopertaFata().exists()) {
                ApplicationController.removeDocument(carte.getCopertaFata().getId());
                carte.setCopertaFata(null);
            }
            carte.setCopertaFata(ApplicationController.saveDocument(frontCoverComposite));
        }

        EditiaOriginala editiaOriginala = carte.getEditiaOriginala();
        editiaOriginala.setTitlu(this.textEditiaPrincepsTitlu.getText().trim());
        editiaOriginala.setEditura(this.textEditiaPrincepsEditura.getText().trim());
        editiaOriginala.setLimba(Limba.valueOf(comboEditiaPrincepsLimba.getText()));
        editiaOriginala.setAn(this.textEditiaPrincepsAn.getText().trim());
        editiaOriginala.setTara(this.textEditiaPrincepsTara.getText().trim());
        editiaOriginala.setIlustratori(compositeEditiaPrincepsAutoriIlustratii.getValoriIntroduse());
        this.carte.setEditiaOriginala(editiaOriginala);

        if (backCoverComposite.imageChanged()) {
            if (carte.getCopertaFata().exists()) {
                ApplicationController.removeDocument(carte.getCopertaSpate().getId());
                carte.setCopertaSpate(null);
            }
            carte.setCopertaSpate(ApplicationController.saveDocument(backCoverComposite));
        }

        if (autografComposite.imageChanged()) {
            if (carte.getAutograf().exists()) {
                ApplicationController.removeDocument(carte.getAutograf().getId());
                carte.setAutograf(null);
            }
            carte.setAutograf(ApplicationController.saveDocument(autografComposite));
        }

        List<GenLiterar> genuriLiterare = new ArrayList<>();
        for (Enum value : compositeGenLiterar.getEnumValues()) {
            genuriLiterare.add((GenLiterar) value);
        }
        carte.setGenLiterar(genuriLiterare);
        carte.setTags(compositeTags.getValoriIntroduse());

        if (dragAndDropTableComposite.isChanged()) {
            for (DocumentData doc : dragAndDropTableComposite.getDeleted()) {
                ApplicationController.removeDocument(doc.getId());
            }
            List<DocumentData> documents = dragAndDropTableComposite.getResult();
            List<DocumentData> docs = new ArrayList<>();
            for (DocumentData doc : documents) {
                if (doc.getId() == null) {
                    docs.add(ApplicationController.saveDocument(doc.getFilePath(), doc.getContentType()));
                } else {
                    docs.add(doc);
                }
            }
            carte.setDocuments(docs);
        }

        carte = ApplicationService.getBookController().save(carte);

        UserController.saveBookRatingForCurrentUser(carte.getId(), starRating.getCurrentNumberOfStars());
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
