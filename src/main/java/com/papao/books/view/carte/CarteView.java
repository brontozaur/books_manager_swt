package com.papao.books.view.carte;

import com.mongodb.gridfs.GridFSDBFile;
import com.papao.books.controller.AutorController;
import com.papao.books.controller.BookController;
import com.papao.books.model.Carte;
import com.papao.books.model.EditiaOriginala;
import com.papao.books.model.Limba;
import com.papao.books.model.TipCoperta;
import com.papao.books.view.AppImages;
import com.papao.books.view.bones.impl.view.AbstractCSaveView;
import com.papao.books.view.custom.ImageSelectorComposite;
import com.papao.books.view.custom.LinkedinComposite;
import com.papao.books.view.custom.LinkedinCompositeAutori;
import com.papao.books.view.providers.ContentProposalProvider;
import com.papao.books.view.util.NumberUtil;
import com.papao.books.view.util.StringUtil;
import com.papao.books.view.util.WidgetCompositeUtil;
import com.papao.books.view.view.AbstractView;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarteView extends AbstractCSaveView {

    private static final Logger logger = LoggerFactory.getLogger(CarteView.class);

    private Carte carte;
    private final BookController carteController;
    private final AutorController autorController;
    private String observableProperty;

    private Text textTitlu;
    private Text textSubtitlu;
    private Text textEditura;
    private Text textAnAparitie;
    private Text textEditia;
    private FormattedText textInaltime;
    private FormattedText textLatime;
    private FormattedText textGreutate;
    private FormattedText textNrPagini;
    private Text textSerie;
    private LinkedinCompositeAutori compositeAutori;
    private LinkedinComposite compositeTraducatori;
    private Text textIsbn;
    private Button buttonCuAutograf;
    private LinkedinComposite compositeAutoriIlustratii;
    private LinkedinComposite compositeTehnoredactori;
    private Text textImprimerie;
    private Combo comboTipCoperta;
    private Combo comboLimba;
    private Combo comboTraducereDin;
    private Text textGoodreadsUrl;
    private Text textWikiUrl;
    private Text textWebsite;
    private ImageSelectorComposite frontCoverComposite;
    private ImageSelectorComposite backCoverComposite;

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
    private ToolItem itemInformatiiEsentiale;
    private ToolItem itemEditiaOriginala;
    private ToolItem itemTaguri;
    private ToolItem itemBookDetails;
    private ToolItem itemBackCover;

    public CarteView(final Shell parent, final Carte carte,
                     final BookController carteController,
                     final AutorController autorController,
                     final int viewMode) {
        super(parent, viewMode, carte.getId());
        this.carte = carte;
        this.carteController = carteController;
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
        GridDataFactory.fillDefaults().grab(true, true).applyTo(mainComp);

        final Composite mainPropertiesComposite = createMainPropertiesTab(mainComp);
        final Composite editiaOriginalaComposite = createEditiaOriginalaTab(mainComp);
        final Composite tagsAndDescriptionComposite = createTagsAndDescriptionTab(mainComp);
        final Composite bookDetailsComposite = createBookDetailsTab(mainComp);
        final Composite copertaSpateComposite = createCopertaSpateTab(mainComp);

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

        WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());
    }

    private Composite createEditiaOriginalaTab(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(8).extendedMargins(5, 5, 5, 5).applyTo(comp);

        new Label(comp, SWT.NONE).setText("Titlu original");
        this.textEditiaPrincepsTitlu = new Text(comp, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(7, 1).applyTo(this.textEditiaPrincepsTitlu);

        new Label(comp, SWT.NONE).setText("Limba originala");
        comboEditiaPrincepsLimba = new Combo(comp, SWT.READ_ONLY);
        GridDataFactory.fillDefaults().applyTo(this.comboEditiaPrincepsLimba);
        comboEditiaPrincepsLimba.setItems(Limba.getComboItems());

        new Label(comp, SWT.NONE).setText("Editura");
        this.textEditiaPrincepsEditura = new Text(comp, SWT.BORDER);
        GridDataFactory.fillDefaults().hint(150, SWT.DEFAULT).applyTo(this.textEditiaPrincepsEditura);
        ContentProposalProvider.addContentProposal(textEditiaPrincepsEditura, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "editiaOriginala.editura"));

        new Label(comp, SWT.NONE).setText("An aparitie");
        textEditiaPrincepsAn = new Text(comp, SWT.BORDER);
        ContentProposalProvider.addContentProposal(textEditiaPrincepsAn, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "editiaOriginala.an"));
        GridDataFactory.fillDefaults().minSize(50, SWT.DEFAULT).hint(50, SWT.DEFAULT).applyTo(this.textEditiaPrincepsAn);

        new Label(comp, SWT.NONE).setText("Tara");
        textEditiaPrincepsTara = new Text(comp, SWT.BORDER);
        ContentProposalProvider.addContentProposal(textEditiaPrincepsTara, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "editiaOriginala.tara"));
        GridDataFactory.fillDefaults().hint(150, SWT.DEFAULT).applyTo(this.textEditiaPrincepsTara);

        Label labelAutoriIlustratii = new Label(comp, SWT.NONE);
        labelAutoriIlustratii.setText("Autori ilustratii");
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(labelAutoriIlustratii);
        this.compositeEditiaPrincepsAutoriIlustratii = new LinkedinComposite(comp, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "editiaOriginala.ilustratori"), carte.getEditiaOriginala().getIlustratori());
        ((GridData) compositeEditiaPrincepsAutoriIlustratii.getLayoutData()).horizontalSpan = 7;

        return comp;
    }

    private Composite createTagsAndDescriptionTab(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(5, 5, 5, 5).applyTo(comp);

        new Label(comp, SWT.NONE).setText("Taguri");
        this.compositeTags = new LinkedinComposite(comp, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "tags"), carte.getTags());

        Label labelDescriere = new Label(comp, SWT.NONE);
        labelDescriere.setText("Descriere");
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(labelDescriere);
        this.textDescriere = new Text(comp, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 100).applyTo(textDescriere);

        return comp;
    }

    private Composite createMainPropertiesTab(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(5, 5, 5, 5).applyTo(comp);

        Composite mainCompLeft = new Composite(comp, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(8).equalWidth(false).applyTo(mainCompLeft);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(mainCompLeft);

        Composite compImages = new Composite(comp, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).applyTo(compImages);
        GridDataFactory.fillDefaults().grab(false, true).applyTo(compImages);

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
        data.horizontalAlignment = SWT.BEGINNING;

        new Label(mainCompLeft, SWT.NONE).setText("Titlu");
        this.textTitlu = new Text(mainCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(7, 1).grab(true, false).applyTo(this.textTitlu);
        this.textTitlu.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                markAsChanged();
            }
        });

        new Label(mainCompLeft, SWT.NONE).setText("Subtitlu");
        this.textSubtitlu = new Text(mainCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(7, 1).grab(true, false).applyTo(this.textSubtitlu);

        Label labelAutori = new Label(mainCompLeft, SWT.NONE);
        labelAutori.setText("Autori");
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(labelAutori);
        compositeAutori = new LinkedinCompositeAutori(mainCompLeft, carte.getIdAutori(), autorController);
        ((GridData) compositeAutori.getLayoutData()).horizontalSpan = 7;
        this.compositeAutori.getCompSelections().addListener(SWT.Resize, new Listener() {
            @Override
            public void handleEvent(Event event) {
                markAsChanged();
            }
        });

        Label labelTraducatori = new Label(mainCompLeft, SWT.NONE);
        labelTraducatori.setText("Traducatori");
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(labelTraducatori);
        this.compositeTraducatori = new LinkedinComposite(mainCompLeft, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "traducatori"), carte.getTraducatori());
        ((GridData) compositeTraducatori.getLayoutData()).horizontalSpan = 7;

        new Label(mainCompLeft, SWT.NONE).setText("Editura");
        this.textEditura = new Text(mainCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(3, 1).grab(true, false).applyTo(this.textEditura);
        ContentProposalProvider.addContentProposal(textEditura, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "editura"));

        new Label(mainCompLeft, SWT.NONE).setText("Serie");
        this.textSerie = new Text(mainCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(3, 1).grab(true, false).applyTo(this.textSerie);
        ContentProposalProvider.addContentProposal(textSerie, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "serie"));

        new Label(mainCompLeft, SWT.NONE).setText("Tip coperta");
        comboTipCoperta = new Combo(mainCompLeft, SWT.READ_ONLY);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).span(3, 1).grab(false, false).applyTo(this.comboTipCoperta);
        comboTipCoperta.setItems(TipCoperta.getComboItems());

        new Label(mainCompLeft, SWT.NONE).setText("Isbn");
        this.textIsbn = new Text(mainCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(3, 1).grab(true, false).applyTo(this.textIsbn);

        new Label(mainCompLeft, SWT.NONE).setText("Editia");
        this.textEditia = new Text(mainCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(3, 1).grab(true, false).applyTo(this.textEditia);

        new Label(mainCompLeft, SWT.NONE).setText("An aparitie");
        textAnAparitie = new Text(mainCompLeft, SWT.BORDER);
        ContentProposalProvider.addContentProposal(textAnAparitie, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "anAparitie"));
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(50, SWT.DEFAULT).hint(50, SWT.DEFAULT).applyTo(this.textAnAparitie);

        new Label(mainCompLeft, SWT.NONE).setText("Nr pagini");
        this.textNrPagini = new FormattedText(mainCompLeft, SWT.BORDER);
        this.textNrPagini.setFormatter(new IntegerFormatter());
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(50, SWT.DEFAULT).hint(50, SWT.DEFAULT).applyTo(this.textNrPagini.getControl());
        ((NumberFormatter) this.textNrPagini.getFormatter()).setFixedLengths(false, true);

        textNrPagini.getControl().addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (event.detail == SWT.TRAVERSE_TAB_NEXT) {
                    itemInformatiiEsentiale.setSelection(false);
                    itemEditiaOriginala.setSelection(true);
                    itemEditiaOriginala.notifyListeners(SWT.Selection, new Event());
                }
            }
        });

        return comp;
    }

    private Composite createBookDetailsTab(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(5, 5, 5, 5).applyTo(comp);

        Label labelTehnoredactori = new Label(comp, SWT.NONE);
        labelTehnoredactori.setText("Tehnoredactori");
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(labelTehnoredactori);
        this.compositeTehnoredactori = new LinkedinComposite(comp, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "tehnoredactori"), carte.getTehnoredactori());

        Label labelAutoriIlustratii = new Label(comp, SWT.NONE);
        labelAutoriIlustratii.setText("Autori ilustratii");
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(labelAutoriIlustratii);
        this.compositeAutoriIlustratii = new LinkedinComposite(comp, carteController.getDistinctFieldAsContentProposal(carteController.getBooksCollectionName(), "autoriIlustratii"), carte.getAutoriIlustratii());

        new Label(comp, SWT.NONE).setText("Imprimerie");
        this.textImprimerie = new Text(comp, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.textImprimerie);

        return comp;
    }

    private Composite createCopertaSpateTab(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(5, 5, 5, 5).applyTo(comp);

        Composite mainCompLeft = new Composite(comp, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(6).equalWidth(false).applyTo(mainCompLeft);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(mainCompLeft);

        new Label(mainCompLeft, SWT.NONE).setText("Lungime (cm)");
        this.textInaltime = new FormattedText(mainCompLeft, SWT.BORDER);
        this.textInaltime.setFormatter(NumberUtil.getFormatter(0, true));
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(50, SWT.DEFAULT).hint(50, SWT.DEFAULT).applyTo(this.textInaltime.getControl());
        ((NumberFormatter) this.textInaltime.getFormatter()).setFixedLengths(false, true);

        new Label(mainCompLeft, SWT.NONE).setText("Latime (cm)");
        this.textLatime = new FormattedText(mainCompLeft, SWT.BORDER);
        this.textLatime.setFormatter(NumberUtil.getFormatter(0, true));
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(50, SWT.DEFAULT).hint(50, SWT.DEFAULT).applyTo(this.textLatime.getControl());
        ((NumberFormatter) this.textLatime.getFormatter()).setFixedLengths(false, true);

        new Label(mainCompLeft, SWT.NONE).setText("Greutate (kg)");
        this.textGreutate = new FormattedText(mainCompLeft, SWT.BORDER);
        this.textGreutate.setFormatter(NumberUtil.getFormatter(2, true));
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(50, SWT.DEFAULT).hint(50, SWT.DEFAULT).applyTo(this.textGreutate.getControl());
        ((NumberFormatter) this.textGreutate.getFormatter()).setFixedLengths(false, true);

        new Label(mainCompLeft, SWT.NONE).setText("Goodreads");
        this.textGoodreadsUrl = new Text(mainCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(5, 1).grab(true, false).applyTo(this.textGoodreadsUrl);
        this.textGoodreadsUrl.addListener(SWT.KeyUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
                textGoodreadsUrl.setText(StringUtil.decodeUrl(textGoodreadsUrl.getText()));
            }
        });

        new Label(mainCompLeft, SWT.NONE).setText("Wikipedia");
        this.textWikiUrl = new Text(mainCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(5, 1).grab(true, false).applyTo(this.textWikiUrl);
        this.textWikiUrl.addListener(SWT.KeyUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
                textWikiUrl.setText(StringUtil.decodeUrl(textWikiUrl.getText()));
            }
        });

        new Label(mainCompLeft, SWT.NONE).setText("Pagina web");
        this.textWebsite = new Text(mainCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(5, 1).grab(true, false).applyTo(this.textWebsite);
        this.textWebsite.addListener(SWT.KeyUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
                textWebsite.setText(StringUtil.decodeUrl(textWebsite.getText()));
            }
        });

        new Label(mainCompLeft, SWT.NONE).setText("Limba");
        comboLimba = new Combo(mainCompLeft, SWT.READ_ONLY);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).span(2, 1).grab(false, false).applyTo(this.comboLimba);
        comboLimba.setItems(Limba.getComboItems());

        new Label(mainCompLeft, SWT.NONE).setText("Traducere din");
        comboTraducereDin = new Combo(mainCompLeft, SWT.READ_ONLY);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false)
                .applyTo(this.comboTraducereDin);
        comboTraducereDin.setItems(Limba.getComboItems());

        this.buttonCuAutograf = new Button(mainCompLeft, SWT.CHECK);
        buttonCuAutograf.setText("cu autograf");

        GridFSDBFile backCover = getGridFsFile(carte.getCopertaSpate().getId());
        Image image = null;
        String fileName = null;
        if (backCover != null) {
            image = new Image(Display.getDefault(), backCover.getInputStream());
            fileName = backCover.getFilename();
        }

        backCoverComposite = new ImageSelectorComposite(comp, image, fileName, carteController.getAppImagesFolder());
        GridData data = (GridData) backCoverComposite.getLayoutData();
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = false;
        data.verticalAlignment = SWT.BEGINNING;
        data.horizontalAlignment = SWT.BEGINNING;

        return comp;
    }

    private GridFSDBFile getGridFsFile(ObjectId imageId) {
        return carteController.getImageData(imageId);
    }

    private void populateFields() {
        this.textTitlu.setText(this.carte.getTitlu());
        this.textSubtitlu.setText(this.carte.getSubtitlu());
        this.textEditura.setText(this.carte.getEditura());
        this.textAnAparitie.setText(this.carte.getAnAparitie());
        this.textInaltime.setValue(this.carte.getInaltime());
        this.textLatime.setValue(this.carte.getLatime());
        this.textNrPagini.setValue(this.carte.getNrPagini());
        this.textGreutate.setValue(this.carte.getGreutate());
        this.textSerie.setText(this.carte.getSerie());
        this.textEditia.setText(this.carte.getEditia());
        this.textIsbn.setText(this.carte.getIsbn());
        this.buttonCuAutograf.setSelection(this.carte.isCuAutograf());
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
        this.carte.setInaltime(Integer.valueOf(textInaltime.getValue().toString()));
        this.carte.setLatime(Integer.valueOf(textLatime.getValue().toString()));
        this.carte.setNrPagini(Integer.valueOf(textNrPagini.getValue().toString()));
        this.carte.setGreutate(Double.valueOf(textGreutate.getValue().toString()));
        this.carte.setSerie(textSerie.getText());
        this.carte.setEditia(textEditia.getText());
        this.carte.setTraducatori(compositeTraducatori.getValoriIntroduse());
        this.carte.setIsbn(textIsbn.getText());
        this.carte.setAutoriIlustratii(compositeAutoriIlustratii.getValoriIntroduse());
        this.carte.setTehnoredactori(compositeTehnoredactori.getValoriIntroduse());
        this.carte.setImprimerie(textImprimerie.getText());
        this.carte.setTipCoperta(TipCoperta.valueOf(comboTipCoperta.getText()));
        this.carte.setLimba(Limba.valueOf(comboLimba.getText()));
        this.carte.setTraducereDin(Limba.valueOf(comboTraducereDin.getText()));
        this.carte.setCuAutograf(buttonCuAutograf.getSelection());
        this.carte.setGoodreadsUrl(textGoodreadsUrl.getText());
        this.carte.setWikiUrl(textWikiUrl.getText());
        this.carte.setWebsite(textWebsite.getText());
        this.carte.setIdAutori(compositeAutori.getSelectedIds());

        if (frontCoverComposite.imageChanged()) {
            carteController.removeImageData(carte.getCopertaFata().getId());
            carte.setCopertaFata(null);

            if (frontCoverComposite.getSelectedFile() != null) {
                carte.setCopertaFata(carteController.saveDocument(frontCoverComposite.getSelectedFile(), frontCoverComposite.getWebPath()));
            }
        }

        if (backCoverComposite.imageChanged()) {
            carteController.removeImageData(carte.getCopertaSpate().getId());
            carte.setCopertaSpate(null);

            if (backCoverComposite.getSelectedFile() != null) {
                carte.setCopertaSpate(carteController.saveDocument(backCoverComposite.getSelectedFile(), backCoverComposite.getWebPath()));
            }
        }

        EditiaOriginala editiaOriginala = carte.getEditiaOriginala();
        editiaOriginala.setTitlu(this.textEditiaPrincepsTitlu.getText());
        editiaOriginala.setEditura(this.textEditiaPrincepsEditura.getText());
        editiaOriginala.setLimba(Limba.valueOf(comboEditiaPrincepsLimba.getText()));
        editiaOriginala.setAn(this.textEditiaPrincepsAn.getText());
        editiaOriginala.setTara(this.textEditiaPrincepsTara.getText());
        editiaOriginala.setIlustratori(compositeEditiaPrincepsAutoriIlustratii.getValoriIntroduse());
        this.carte.setEditiaOriginala(editiaOriginala);

        carteController.save(carte);
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

    private void setCarte(final Carte carte) {
        this.carte = carte;
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
