package com.papao.books.view.carte;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.papao.books.controller.BookController;
import com.papao.books.model.*;
import com.papao.books.view.AppImages;
import com.papao.books.view.bones.impl.view.AbstractCSaveView;
import com.papao.books.view.custom.ImageSelectorComposite;
import com.papao.books.view.custom.LinkedinComposite;
import com.papao.books.view.providers.ContentProposalProvider;
import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.util.NumberUtil;
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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;

public class CarteView extends AbstractCSaveView {

    private static final Logger logger = LoggerFactory.getLogger(CarteView.class);

    private Carte carte;
    private final BookController controller;

    private Composite compLeft;
    private Composite compRight;

    private Text textTitlu;
    private Text textEditura;
    private Text textAnAparitie;
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
    private Text textGoodreadsUrl;
    private Text textWikiUrl;
    private ImageSelectorComposite frontCoverComposite;
    private ImageSelectorComposite backCoverComposite;

    public CarteView(final Shell parent, final Carte carte,
                     final BookController carteController,
                     final int viewMode) {
        super(parent, viewMode, carte.getId());
        this.carte = carte;
        this.controller = carteController;

        addComponents();
        populateFields();

        textTitlu.setFocus();
    }

    private void addComponents() {

        CTabFolder tabFolder = new CTabFolder(getContainer(), SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(tabFolder);
        tabFolder.setSimple(true);
        tabFolder.setUnselectedImageVisible(true);
        tabFolder.setUnselectedCloseVisible(false);
        tabFolder.setMinimizeVisible(false);
        tabFolder.setMaximizeVisible(false);
        tabFolder.setSelectionBackground(ColorUtil.COLOR_SYSTEM);

        CTabItem firstTabItem = new CTabItem(tabFolder, SWT.NONE);
        firstTabItem.setText("Informatii esentiale");
        firstTabItem.setImage(AppImages.getImage16(AppImages.IMG_HOME));
        tabFolder.setSelection(firstTabItem);

        compLeft = new Composite(tabFolder, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).equalWidth(false).applyTo(compLeft);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(compLeft);

        createCompLeftComponents(compLeft);
        firstTabItem.setControl(compLeft);

        CTabItem secondaryTabItem = new CTabItem(tabFolder, SWT.NONE);
        secondaryTabItem.setText("Alte detalii");
        secondaryTabItem.setImage(AppImages.getImage16(AppImages.IMG_EXPAND));

        compRight = new Composite(tabFolder, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(5).margins(5, 5).equalWidth(false).applyTo(compRight);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(compRight);

        createCompRightComponents(compRight);
        secondaryTabItem.setControl(compRight);

        WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());
    }

    private void createCompLeftComponents(Composite parent) {

        Composite mainCompLeft = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).spacing(0, 0).applyTo(mainCompLeft);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(mainCompLeft);

        Composite topCompLeft = new Composite(mainCompLeft, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).extendedMargins(0, 0, 0, 5 ).applyTo(topCompLeft);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(topCompLeft);

        frontCoverComposite = new ImageSelectorComposite(parent, getSWTImage(carte.getCopertaFata().getId()), carte.getCopertaFata().getFileName());
        GridData data = (GridData) frontCoverComposite.getLayoutData();
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = false;
        data.verticalAlignment = SWT.BEGINNING;
        data.horizontalAlignment = SWT.BEGINNING;

        new Label(topCompLeft, SWT.NONE).setText("Titlu");
        this.textTitlu = new Text(topCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.textTitlu);

        new Label(topCompLeft, SWT.NONE).setText("Titlu original");
        this.textTitluOriginal = new Text(topCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.textTitluOriginal);

        new Label(topCompLeft, SWT.NONE).setText("Goodreads url");
        this.textGoodreadsUrl = new Text(topCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.textGoodreadsUrl);

        new Label(topCompLeft, SWT.NONE).setText("Wikipedia url");
        this.textWikiUrl = new Text(topCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.textWikiUrl);

        Composite bottomCompLeft = new Composite(mainCompLeft, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).margins(0, 0).applyTo(bottomCompLeft);
        GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(bottomCompLeft);

        Composite bottomLeftCompLeft = new Composite(mainCompLeft, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(bottomLeftCompLeft);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(bottomLeftCompLeft);

        new Label(bottomLeftCompLeft, SWT.NONE).setText("Editura");
        this.textEditura = new Text(bottomLeftCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(1, 1).applyTo(this.textEditura);
        ContentProposalProvider.addContentProposal(textEditura, controller.getDistinctFieldAsContentProposal("editura"));

        new Label(bottomLeftCompLeft, SWT.NONE).setText("Serie");
        this.textSerie = new Text(bottomLeftCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(1, 1).applyTo(this.textSerie);
        ContentProposalProvider.addContentProposal(textSerie, controller.getDistinctFieldAsContentProposal("serie"));

        new Label(bottomLeftCompLeft, SWT.NONE).setText("Tip coperta");
        comboTipCoperta = new Combo(bottomLeftCompLeft, SWT.READ_ONLY);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).applyTo(this.comboTipCoperta);
        comboTipCoperta.setItems(TipCoperta.getComboItems());

        Composite bottomRightCompLeft = new Composite(mainCompLeft, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).extendedMargins(5, 0, 0, 0).applyTo(bottomRightCompLeft);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(bottomRightCompLeft);


        new Label(bottomRightCompLeft, SWT.NONE).setText("ISBN");
        this.textIsbn = new Text(bottomRightCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(1, 1).applyTo(this.textIsbn);

        this.buttonCuIlustratii = new Button(bottomRightCompLeft, SWT.CHECK);
        buttonCuIlustratii.setText("cu ilustratii");

        this.buttonCuAutograf = new Button(bottomRightCompLeft, SWT.CHECK);
        buttonCuAutograf.setText("cu autograf");

        new Label(bottomRightCompLeft, SWT.NONE).setText("Editia");
        this.textEditia = new Text(bottomRightCompLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(1, 1).applyTo(this.textEditia);

        new Label(bottomRightCompLeft, SWT.NONE).setText("Nr pagini");
        this.textNrPagini = new FormattedText(bottomRightCompLeft, SWT.BORDER);
        this.textNrPagini.setFormatter(new IntegerFormatter());
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(50, SWT.DEFAULT).hint(50, SWT.DEFAULT).applyTo(this.textNrPagini.getControl());
        ((NumberFormatter) this.textNrPagini.getFormatter()).setFixedLengths(false, true);

        new Label(bottomRightCompLeft, SWT.NONE).setText("Limba");
        comboLimba = new Combo(bottomRightCompLeft, SWT.READ_ONLY);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).applyTo(this.comboLimba);
        comboLimba.setItems(Limba.getComboItems());

        new Label(bottomRightCompLeft, SWT.NONE).setText("An aparitie");
        textAnAparitie = new Text(bottomRightCompLeft, SWT.BORDER);
        ContentProposalProvider.addContentProposal(textAnAparitie, controller.getDistinctFieldAsContentProposal("anAparitie"));
        this.textNrPagini.setFormatter(NumberUtil.getFormatter(0, true));
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(75, SWT.DEFAULT).hint(75, SWT.DEFAULT).applyTo(this.textAnAparitie);

    }

    private Image getSWTImage(ObjectId imageId) {
        GridFSDBFile imageForOutput = controller.getImageData(imageId);
        if (imageForOutput != null) {
            return new Image(Display.getDefault(), imageForOutput.getInputStream());
        }
        return null;
    }

    private void createCompRightComponents(Composite parent) {
        Label labelAutoriIlustratii = new Label(parent, SWT.NONE);
        labelAutoriIlustratii.setText("Autori ilustratii");
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(labelAutoriIlustratii);
        this.compositeAutoriIlustratii = new LinkedinComposite(parent, controller.getDistinctFieldAsContentProposal("autoriIlustratii"), carte.getAutoriIlustratii());

        Label labelTraducatori = new Label(parent, SWT.NONE);
        labelTraducatori.setText("Traducatori");
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(labelTraducatori);
        this.compositeTraducatori = new LinkedinComposite(parent, controller.getDistinctFieldAsContentProposal("traducatori"), carte.getTraducatori());
        this.compositeTraducatori.getTextSearch().addTraverseListener(new TraverseListener() {
            @Override
            public void keyTraversed(TraverseEvent e) {
                if (e.keyCode == SWT.TAB) {
                    textTitluOriginal.setFocus();
                }
            }
        });

        backCoverComposite = new ImageSelectorComposite(parent, getSWTImage(carte.getCopertaSpate().getId()), carte.getCopertaSpate().getFileName());
        ((GridData) backCoverComposite.getLayoutData()).verticalSpan = 6;

        Label labelTehnoredactori = new Label(parent, SWT.NONE);
        labelTehnoredactori.setText("Tehnoredactori");
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(labelTehnoredactori);
        this.compositeTehnoredactori = new LinkedinComposite(parent, controller.getDistinctFieldAsContentProposal("tehnoredactori"), carte.getTehnoredactori());

        new Label(parent, SWT.NONE).setText("Lungime (cm)");
        this.textInaltime = new FormattedText(parent, SWT.BORDER);
        this.textInaltime.setFormatter(NumberUtil.getFormatter(0, true));
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(50, SWT.DEFAULT).hint(50, SWT.DEFAULT).applyTo(this.textInaltime.getControl());
        ((NumberFormatter) this.textInaltime.getFormatter()).setFixedLengths(false, true);

        new Label(parent, SWT.NONE).setText("Latime (cm)");
        this.textLatime = new FormattedText(parent, SWT.BORDER);
        this.textLatime.setFormatter(NumberUtil.getFormatter(0, true));
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(50, SWT.DEFAULT).hint(50, SWT.DEFAULT).applyTo(this.textLatime.getControl());
        ((NumberFormatter) this.textLatime.getFormatter()).setFixedLengths(false, true);

        new Label(parent, SWT.NONE).setText("Greutate (kg)");
        this.textGreutate = new FormattedText(parent, SWT.BORDER);
        this.textGreutate.setFormatter(NumberUtil.getFormatter(2, true));
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(50, SWT.DEFAULT).hint(50, SWT.DEFAULT).applyTo(this.textGreutate.getControl());
        ((NumberFormatter) this.textGreutate.getFormatter()).setFixedLengths(false, true);

        new Label(parent, SWT.NONE).setText("Imprimerie");
        this.textImprimerie = new Text(parent, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.textImprimerie);

        new Label(parent, SWT.NONE).setText("Limba originala");
        comboLimbaOriginala = new Combo(parent, SWT.READ_ONLY);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false)
                .applyTo(this.comboLimbaOriginala);
        comboLimbaOriginala.setItems(Limba.getComboItems());

        new Label(parent, SWT.NONE).setText("Traducere din");
        comboTraducereDin = new Combo(parent, SWT.READ_ONLY);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false)
                .applyTo(this.comboTraducereDin);
        comboTraducereDin.setItems(Limba.getComboItems());
    }

    private void populateFields() {
        this.textTitlu.setText(this.carte.getTitlu());
        this.textTitluOriginal.setText(this.carte.getTitluOriginal());
        this.textEditura.setText(this.carte.getEditura());
        this.textAnAparitie.setText(this.carte.getAnAparitie());
        this.textInaltime.setValue(this.carte.getInaltime());
        this.textLatime.setValue(this.carte.getLatime());
        this.textNrPagini.setValue(this.carte.getNrPagini());
        this.textGreutate.setValue(this.carte.getGreutate());
        this.textSerie.setText(this.carte.getSerie());
        this.textEditia.setText(this.carte.getEditia());
        this.textIsbn.setText(this.carte.getIsbn10());
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
    protected void saveData() throws Exception {
        this.carte.setTitlu(this.textTitlu.getText());
        this.carte.setTitluOriginal(this.textTitluOriginal.getText());
        this.carte.setEditura(textEditura.getText());
        this.carte.setAnAparitie(textAnAparitie.getText());
        this.carte.setInaltime(Integer.valueOf(textInaltime.getValue().toString()));
        this.carte.setLatime(Integer.valueOf(textLatime.getValue().toString()));
        this.carte.setNrPagini(Integer.valueOf(textNrPagini.getValue().toString()));
        this.carte.setGreutate(Double.valueOf(textGreutate.getValue().toString()));
        this.carte.setSerie(textSerie.getText());
        this.carte.setEditia(textEditia.getText());
        this.carte.setTraducatori(compositeTraducatori.getValoriIntroduse());
        this.carte.setIsbn10(textIsbn.getText());
        this.carte.setAutoriIlustratii(compositeAutoriIlustratii.getValoriIntroduse());
        this.carte.setTehnoredactori(compositeTehnoredactori.getValoriIntroduse());
        this.carte.setImprimerie(textImprimerie.getText());
        this.carte.setTipCoperta(TipCoperta.valueOf(comboTipCoperta.getText()));
        this.carte.setLimba(Limba.valueOf(comboLimba.getText()));
        this.carte.setLimbaOriginala(Limba.valueOf(comboLimbaOriginala.getText()));
        this.carte.setTraducereDin(Limba.valueOf(comboTraducereDin.getText()));
        this.carte.setCuIlustratii(buttonCuIlustratii.getSelection());
        this.carte.setCuAutograf(buttonCuAutograf.getSelection());
        this.carte.setGoodreadsUrl(textGoodreadsUrl.getText());
        this.carte.setWikiUrl(textWikiUrl.getText());

        if (frontCoverComposite.imageChanged()) {
            controller.removeImageData(carte.getCopertaFata().getId());
            carte.setCopertaFata(null);

            if (frontCoverComposite.getSelectedFile() != null) {
                GridFSDBFile frontCover = saveImage(frontCoverComposite.getSelectedFile());
                DocumentData copertaFata = new DocumentData();
                copertaFata.setId((ObjectId) frontCover.getId());
                copertaFata.setFileName(frontCover.getFilename());
                carte.setCopertaFata(copertaFata);
            }
        }

        if (backCoverComposite.imageChanged()) {
            controller.removeImageData(carte.getCopertaSpate().getId());
            carte.setCopertaSpate(null);

            if (backCoverComposite.getSelectedFile() != null) {
                GridFSDBFile backCover = saveImage(backCoverComposite.getSelectedFile());
                DocumentData copertaSpate = new DocumentData();
                copertaSpate.setId((ObjectId) backCover.getId());
                copertaSpate.setFileName(backCover.getFilename());
                carte.setCopertaSpate(copertaSpate);
            }
        }

        controller.save(carte);
    }

    private GridFSDBFile saveImage(File imageFile) throws IOException {
        GridFSInputFile gfsFile = controller.createFile(imageFile);
        gfsFile.setFilename(imageFile.getName());
        gfsFile.setContentType(new MimetypesFileTypeMap().getContentType(imageFile));
        DBObject meta = new BasicDBObject();
        meta.put("fileOriginalFilePath", imageFile.getAbsolutePath());
        gfsFile.setMetaData(meta);
        gfsFile.save();
        return controller.getImageData((ObjectId) gfsFile.getId());
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

    @Override
    protected Class<? extends AbstractDB> getClazz() {
        return Carte.class;
    }
}
