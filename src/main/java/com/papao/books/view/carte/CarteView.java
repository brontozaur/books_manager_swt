package com.papao.books.view.carte;

import com.papao.books.model.*;
import com.papao.books.repository.CarteRepository;
import com.papao.books.view.bones.impl.view.AbstractCSaveView;
import com.papao.books.view.custom.ComboImage;
import com.papao.books.view.custom.LinkedinComposite;
import com.papao.books.view.providers.ContentProposalProvider;
import com.papao.books.view.util.NumberUtil;
import com.papao.books.view.util.WidgetCompositeUtil;
import com.papao.books.view.view.AbstractView;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.NumberFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CarteView extends AbstractCSaveView {

    private Carte carte;
    private CarteRepository carteRepository;
    private MongoTemplate mongoTemplate;

    private LinkedinComposite compositeAutori;
    private Text textTitlu;
    private Text textEditura;
    private ComboImage comboAnAparitie;
    private Text textTitluOriginal;
    private Text textEditia;
    private FormattedText textInaltime;
    private FormattedText textLatime;
    private FormattedText textGreutate;
    private FormattedText textNrPagini;
    private Text textSerie;
    private LinkedinComposite compositeTraducatori;
    private Text textIsbn;
    private Button buttonCuIlustratii;
    private Button buttonCuAutograf;
    private LinkedinComposite compositeAutoriIlustratii;
    private LinkedinComposite compositeTehnoredactori;
    private Text textImprimerie;
    private Combo comboTipCoperta;
    private Combo comboLimba;
    private Combo comboTraducereDin;
    private Combo comboLimbaOriginala;
    private LinkedinComposite compositeDistinctiiAcordate;
    private Text textGoodreadsUrl;
    private Text textWikiUrl;

    public CarteView(final Shell parent, final Carte carte,
                     final CarteRepository carteRepository,
                     MongoTemplate mongoTemplate,
                     final int viewMode) {
        super(parent, viewMode, carte.getId());
        this.carte = carte;
        this.carteRepository = carteRepository;
        this.mongoTemplate = mongoTemplate;

        addComponents();
        populateFields();
    }

    private void addComponents() {
        setWidgetLayout(new GridLayout(4, false));
        getContainer().setLayout(getWidgetLayout());

        Label labelAutor = new Label(getContainer(), SWT.NONE);
        labelAutor.setText("Autori");
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(labelAutor);
        this.compositeAutori = new LinkedinComposite(getContainer(), mongoTemplate.getCollection("carte").distinct("autori"), carte.getAutori());

        new Label(getContainer(), SWT.NONE).setText("Titlu");
        this.textTitlu = new Text(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(300, SWT.DEFAULT).hint(300, SWT.DEFAULT).applyTo(this.textTitlu);

        new Label(getContainer(), SWT.NONE).setText("Titlu original");
        this.textTitluOriginal = new Text(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(300, SWT.DEFAULT).hint(300, SWT.DEFAULT).applyTo(this.textTitluOriginal);

        new Label(getContainer(), SWT.NONE).setText("Editura");
        this.textEditura = new Text(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(150, SWT.DEFAULT).hint(150, SWT.DEFAULT).applyTo(this.textEditura);
        ContentProposalProvider.addContentProposal(textEditura, mongoTemplate.getCollection("carte").distinct("editura"));

        new Label(getContainer(), SWT.NONE).setText("Serie");
        this.textSerie = new Text(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(150, SWT.DEFAULT).hint(150, SWT.DEFAULT).applyTo(this.textSerie);
        ContentProposalProvider.addContentProposal(textSerie, mongoTemplate.getCollection("carte").distinct("serie"));

        new Label(getContainer(), SWT.NONE).setText("Editia");
        this.textEditia = new Text(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(150, SWT.DEFAULT).hint(150, SWT.DEFAULT).applyTo(this.textEditia);

        new Label(getContainer(), SWT.NONE).setText("ISBN");
        this.textIsbn = new Text(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(150, SWT.DEFAULT).hint(150, SWT.DEFAULT).applyTo(this.textIsbn);

        List<BlankDbObject> aniObjects = new ArrayList<>();
        for (int i = 1900; i < Calendar.getInstance().get(Calendar.YEAR); i++) {
            final String currentYear = String.valueOf(i);
            aniObjects.add(new BlankDbObject(currentYear, currentYear));
        }
        new Label(getContainer(), SWT.NONE).setText("An aparitie");
        ComboImage.CIDescriptor comboDescriptor = new ComboImage.CIDescriptor();
        comboDescriptor.setTextMethodName(BlankDbObject.EXTERNAL_REFLECT_GET_NAME);
        comboDescriptor.setAddEmptyElement(false);
        comboDescriptor.setAddContentProposal(false);
        comboDescriptor.setClazz(BlankDbObject.class);
        comboDescriptor.setInput(aniObjects);
        comboAnAparitie = new ComboImage(getContainer(), comboDescriptor);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(75, SWT.DEFAULT).hint(75, SWT.DEFAULT).applyTo(this.comboAnAparitie);

        new Label(getContainer(), SWT.NONE).setText("Lungime (cm)");
        this.textInaltime = new FormattedText(getContainer(), SWT.BORDER);
        this.textInaltime.setFormatter(NumberUtil.getFormatter(0, true));
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(50, SWT.DEFAULT).hint(50, SWT.DEFAULT).applyTo(this.textInaltime.getControl());
        ((NumberFormatter) this.textInaltime.getFormatter()).setFixedLengths(false, true);

        new Label(getContainer(), SWT.NONE).setText("Latime (cm)");
        this.textLatime = new FormattedText(getContainer(), SWT.BORDER);
        this.textLatime.setFormatter(NumberUtil.getFormatter(0, true));
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(50, SWT.DEFAULT).hint(50, SWT.DEFAULT).applyTo(this.textLatime.getControl());
        ((NumberFormatter) this.textLatime.getFormatter()).setFixedLengths(false, true);

        new Label(getContainer(), SWT.NONE).setText("Greutate (kg)");
        this.textGreutate = new FormattedText(getContainer(), SWT.BORDER);
        this.textGreutate.setFormatter(NumberUtil.getFormatter(2, true));
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(50, SWT.DEFAULT).hint(50, SWT.DEFAULT).applyTo(this.textGreutate.getControl());
        ((NumberFormatter) this.textGreutate.getFormatter()).setFixedLengths(false, true);

        new Label(getContainer(), SWT.NONE).setText("Nr pagini");
        this.textNrPagini = new FormattedText(getContainer(), SWT.BORDER);
        this.textNrPagini.setFormatter(NumberUtil.getFormatter(0, true));
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(50, SWT.DEFAULT).hint(50, SWT.DEFAULT).applyTo(this.textNrPagini.getControl());
        ((NumberFormatter) this.textNrPagini.getFormatter()).setFixedLengths(false, true);

        Label labelTraducatori = new Label(getContainer(), SWT.NONE);
        labelTraducatori.setText("Traducatori");
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(labelTraducatori);
        this.compositeTraducatori = new LinkedinComposite(getContainer(), mongoTemplate.getCollection("carte").distinct("traducatori"), carte.getTraducatori());

        Label labelAutoriIlustratii = new Label(getContainer(), SWT.NONE);
        labelAutoriIlustratii.setText("Autori ilustratii");
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(labelAutoriIlustratii);
        this.compositeAutoriIlustratii = new LinkedinComposite(getContainer(), mongoTemplate.getCollection("carte").distinct("autoriIlustratii"), carte.getAutoriIlustratii());

        Label labelTehnoredactori = new Label(getContainer(), SWT.NONE);
        labelTehnoredactori.setText("Tehnoredactori");
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(labelTehnoredactori);
        this.compositeTehnoredactori = new LinkedinComposite(getContainer(), mongoTemplate.getCollection("carte").distinct("tehnoredactori"), carte.getTehnoredactori());

        Label labelDistinctii = new Label(getContainer(), SWT.NONE);
        labelDistinctii.setText("Distinctii acordate");
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(labelDistinctii);
        this.compositeDistinctiiAcordate = new LinkedinComposite(getContainer(), mongoTemplate.getCollection("carte").distinct("distinctiiAcordate"), carte.getDistinctiiAcordate());

        this.buttonCuIlustratii = new Button(getContainer(), SWT.CHECK);
        buttonCuIlustratii.setText("cu ilustratii");

        this.buttonCuAutograf = new Button(getContainer(), SWT.CHECK);
        buttonCuAutograf.setText("cu autograf");

        new Label(getContainer(), SWT.NONE).setText("Imprimerie");
        this.textImprimerie = new Text(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(150, SWT.DEFAULT).hint(150, SWT.DEFAULT).applyTo(this.textImprimerie);

        new Label(getContainer(), SWT.NONE).setText("Tip coperta");
        comboTipCoperta = new Combo(getContainer(), SWT.READ_ONLY);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(150, SWT.DEFAULT).hint(150, SWT.DEFAULT).applyTo(this.comboTipCoperta);
        comboTipCoperta.setItems(TipCoperta.getComboItems());

        new Label(getContainer(), SWT.NONE).setText("Limba");
        comboLimba = new Combo(getContainer(), SWT.READ_ONLY);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(150, SWT.DEFAULT).hint(150, SWT.DEFAULT).applyTo(this.comboLimba);
        comboLimba.setItems(Limba.getComboItems());

        new Label(getContainer(), SWT.NONE).setText("Limba originala");
        comboLimbaOriginala = new Combo(getContainer(), SWT.READ_ONLY);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(150, SWT.DEFAULT).hint(150, SWT.DEFAULT).applyTo(this.comboLimbaOriginala);
        comboLimbaOriginala.setItems(Limba.getComboItems());

        new Label(getContainer(), SWT.NONE).setText("Traducere din");
        comboTraducereDin = new Combo(getContainer(), SWT.READ_ONLY);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(150, SWT.DEFAULT).hint(150, SWT.DEFAULT).applyTo(this.comboTraducereDin);
        comboTraducereDin.setItems(Limba.getComboItems());

        new Label(getContainer(), SWT.NONE).setText("Goodreads url");
        this.textGoodreadsUrl = new Text(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(300, SWT.DEFAULT).hint(300, SWT.DEFAULT).applyTo(this.textGoodreadsUrl);

        new Label(getContainer(), SWT.NONE).setText("Wikipedia url");
        this.textWikiUrl = new Text(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(300, SWT.DEFAULT).hint(300, SWT.DEFAULT).applyTo(this.textWikiUrl);

        WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());
    }

    private void populateFields() {
        this.textTitlu.setText(this.carte.getTitlu());
        this.textTitluOriginal.setText(this.carte.getTitluOriginal());
        this.textEditura.setText(this.carte.getEditura());
        this.comboAnAparitie.select(this.carte.getAnAparitie());
        this.textInaltime.setValue(this.carte.getInaltime());
        this.textLatime.setValue(this.carte.getLatime());
        this.textNrPagini.setValue(this.carte.getNrPagini());
        this.textGreutate.setValue(this.carte.getGreutate());
        this.textSerie.setText(this.carte.getSerie());
        this.textEditia.setText(this.carte.getEditia());
        this.textIsbn.setText(this.carte.getIsbn());
        this.buttonCuAutograf.setSelection(this.carte.isCuAutograf());
        this.buttonCuIlustratii.setSelection(this.carte.isCuIlustratii());
        this.textImprimerie.setText(this.carte.getImprimerie());
        this.comboTipCoperta.select(comboTipCoperta.indexOf(this.carte.getTipCoperta().name()));
        this.comboLimba.select(comboLimba.indexOf(this.carte.getLimba().name()));
        this.comboLimbaOriginala.select(comboLimbaOriginala.indexOf(this.carte.getLimbaOriginala().name()));
        this.comboTraducereDin.select(comboTraducereDin.indexOf(this.carte.getTraducereDin().name()));
        this.textGoodreadsUrl.setText(this.carte.getGoodreadsUrl());
        this.textWikiUrl.setText(this.carte.getWikiUrl());

        if (!isViewEnabled()) {
            WidgetCompositeUtil.enableGUI(getContainer(), false);
            WidgetCompositeUtil.enableGUI(getCompHIRE(), false);
            getContainer().setEnabled(true);
        }
    }

    @Override
    protected void saveData() {
        this.carte.setTitlu(this.textTitlu.getText());
        this.carte.setTitluOriginal(this.textTitluOriginal.getText());
        this.carte.setAutori(compositeAutori.getValoriIntroduse());
        this.carte.setEditura(textEditura.getText());
        this.carte.setAnAparitie(comboAnAparitie.getText());
        this.carte.setInaltime(Integer.valueOf(textInaltime.getValue().toString()));
        this.carte.setLatime(Integer.valueOf(textLatime.getValue().toString()));
        this.carte.setNrPagini(Integer.valueOf(textNrPagini.getValue().toString()));
        this.carte.setGreutate(Integer.valueOf(textGreutate.getValue().toString()));
        this.carte.setSerie(textSerie.getText());
        this.carte.setEditia(textEditia.getText());
        this.carte.setTraducatori(compositeTraducatori.getValoriIntroduse());
        this.carte.setIsbn(textIsbn.getText());
        this.carte.setAutoriIlustratii(compositeAutoriIlustratii.getValoriIntroduse());
        this.carte.setTehnoredactori(compositeTehnoredactori.getValoriIntroduse());
        this.carte.setDistinctiiAcordate(compositeDistinctiiAcordate.getValoriIntroduse());
        this.carte.setImprimerie(textImprimerie.getText());
        this.carte.setTipCoperta(TipCoperta.valueOf(comboTipCoperta.getText()));
        this.carte.setLimba(Limba.valueOf(comboLimba.getText()));
        this.carte.setLimbaOriginala(Limba.valueOf(comboLimbaOriginala.getText()));
        this.carte.setTraducereDin(Limba.valueOf(comboTraducereDin.getText()));
        this.carte.setCuIlustratii(buttonCuIlustratii.getSelection());
        this.carte.setCuAutograf(buttonCuAutograf.getSelection());
        this.carte.setGoodreadsUrl(textGoodreadsUrl.getText());
        this.carte.setWikiUrl(textWikiUrl.getText());

        carteRepository.save(carte);
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
            if (compositeAutori.getValoriIntroduse().isEmpty()) {
                SWTeXtension.displayMessageW("Nu ati introdus autorul!");
                compositeAutori.getTextSearch().setFocus();
                return false;
            }
            if (StringUtils.isEmpty(this.textTitlu.getText())) {
                SWTeXtension.displayMessageW("Titlul cartii nu este introdus!");
                textTitlu.setFocus();
                return false;
            }
        } catch (Exception exc) {
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

    @Override
    protected Class<? extends AbstractDB> getClazz() {
        return Carte.class;
    }
}
