package com.papao.books.view.carte;

import com.mongodb.gridfs.GridFSDBFile;
import com.papao.books.controller.AutorController;
import com.papao.books.model.AnLunaZiData;
import com.papao.books.model.Autor;
import com.papao.books.model.GenLiterar;
import com.papao.books.view.bones.impl.view.AbstractCSaveView;
import com.papao.books.view.custom.AnLunaZiComposite;
import com.papao.books.view.custom.ImageSelectorComposite;
import com.papao.books.view.custom.LinkedinComposite;
import com.papao.books.view.providers.ContentProposalProvider;
import com.papao.books.view.util.WidgetCompositeUtil;
import com.papao.books.view.view.AbstractView;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AutorView extends AbstractCSaveView {

    private Autor autor;

    private Text textNume;
    private AnLunaZiComposite dataNasteriiComposite;
    private AnLunaZiComposite dataMortiiComposite;
    private Text textLocNastere;
    private Text textTara;
    private ImageSelectorComposite mainImageComposite;
    private LinkedinComposite genLiterarComposite;
    private Text textWebsite;
    private Text textTwitter;
    private Text textFacebook;
    private Text textWiki;
    private Text textDescriere;
    private String observableProperty;

    private AutorController controller;

    public AutorView(final Shell parent, final Autor autor, AutorController controller, final int viewMode) {
        super(parent, viewMode, autor.getId());
        this.autor = autor;
        this.controller = controller;

        addComponents();
        populateFields();
    }

    private void addComponents() {
        setWidgetLayout(new GridLayout(2, false));
        getContainer().setLayout(getWidgetLayout());

        Composite compLeft = new Composite(getContainer(), SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(4).applyTo(compLeft);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(compLeft);

        new Label(compLeft, SWT.NONE).setText("Nume");
        this.textNume = new Text(compLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(3,1).minSize(350, SWT.DEFAULT).applyTo(this.textNume);
        textNume.addListener(SWT.KeyUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
                markAsChanged();
            }
        });

        new Label(compLeft, SWT.NONE).setText("Data nasterii");
        this.dataNasteriiComposite = new AnLunaZiComposite(compLeft, autor.getDataNasterii(true));
        GridDataFactory.fillDefaults().grab(true, false).span(3,1).applyTo(this.dataNasteriiComposite);

        new Label(compLeft, SWT.NONE).setText("Data mortii");
        this.dataMortiiComposite = new AnLunaZiComposite(compLeft, autor.getDataMortii(true));
        GridDataFactory.fillDefaults().grab(true, false).span(3,1).applyTo(this.dataMortiiComposite);

        Label labelGen = new Label(compLeft, SWT.NONE);
        labelGen.setText("Gen literar");
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(labelGen);
        this.genLiterarComposite = new LinkedinComposite(compLeft,
                GenLiterar.class, autor.getGenLiterar());
        GridDataFactory.fillDefaults().grab(true, false).span(3,1).applyTo(this.genLiterarComposite);

        new Label(compLeft, SWT.NONE).setText("Website");
        this.textWebsite = new Text(compLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(3,1).applyTo(textWebsite);
        this.textWebsite.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                encodeUrl((Text)event.widget);
            }
        });

        new Label(compLeft, SWT.NONE).setText("Facebook");
        this.textFacebook = new Text(compLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(3,1).applyTo(textFacebook);
        this.textFacebook.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                encodeUrl((Text)event.widget);
            }
        });

        new Label(compLeft, SWT.NONE).setText("Twitter");
        this.textTwitter = new Text(compLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(3,1).applyTo(textTwitter);
        this.textTwitter.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                encodeUrl((Text)event.widget);
            }
        });

        new Label(compLeft, SWT.NONE).setText("Wiki");
        this.textWiki = new Text(compLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(3,1).applyTo(textWiki);
        this.textWiki.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                encodeUrl((Text)event.widget);
            }
        });

        new Label(compLeft, SWT.NONE).setText("Loc nastere");
        this.textLocNastere = new Text(compLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this.textLocNastere);

        new Label(compLeft, SWT.NONE).setText("Tara");
        this.textTara = new Text(compLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this.textTara);
        ContentProposalProvider.addContentProposal(textTara, controller.getDistinctFieldAsContentProposal(controller.getAutoriCollectionName(), "tara"));

        Label labelDescriere = new Label(compLeft, SWT.NONE);
        labelDescriere.setText("Descriere");
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(labelDescriere);
        this.textDescriere = new Text(compLeft, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().grab(true, true).hint(300, 100).span(3,1).applyTo(textDescriere);

        Composite compImage = new Composite(getContainer(), SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(compImage);
        GridDataFactory.fillDefaults().grab(false, false).align(SWT.END, SWT.BEGINNING).applyTo(compImage);

        Image mainImage = null;
        String imageName = null;
        if (autor.getMainImage() != null) {
            GridFSDBFile image = controller.getImageData(autor.getMainImage().getId());
            if (image != null) {
                imageName = image.getFilename();
                mainImage = new Image(Display.getDefault(), image.getInputStream());
            }
        }

        this.mainImageComposite = new ImageSelectorComposite(compImage, mainImage, imageName, controller.getAppImagesFolder());
        this.addObserver(mainImageComposite);

        WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());
    }

    @Override
    public String getObservableProperty() {
        return observableProperty;
    }

    private void populateFields() {
        this.textNume.setText(this.autor.getNumeComplet());
        this.textWebsite.setText(this.autor.getWebsite());
        this.textFacebook.setText(this.autor.getFacebook());
        this.textTwitter.setText(this.autor.getTwitter());
        this.textDescriere.setText(this.autor.getDescriere());
        this.textWiki.setText(this.autor.getWiki());
        this.textLocNastere.setText(this.autor.getLoculNasterii());
        this.textTara.setText(this.autor.getTara());

        if (!isViewEnabled()) {
            WidgetCompositeUtil.enableGUI(getContainer(), false);
            WidgetCompositeUtil.enableGUI(getCompHIRE(), false);
            getContainer().setEnabled(true);
        }

        markAsChanged();
    }

    @Override
    public final void customizeView() {
        setShellStyle(SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE);
        setViewOptions(AbstractView.ADD_CANCEL | AbstractView.ADD_OK);
        setObjectName("autor");
    }

    @Override
    protected void saveData() throws IOException {
        this.autor.setNumeComplet(this.textNume.getText());
        AnLunaZiData dataNasterii = dataNasteriiComposite.getValues();
        this.autor.setAnNastere(dataNasterii.getAn());
        this.autor.setLunaNastere(dataNasterii.getLuna());
        this.autor.setZiNastere(dataNasterii.getZi());
        AnLunaZiData dataMortii = dataMortiiComposite.getValues();
        this.autor.setAnDeces(dataMortii.getAn());
        this.autor.setLunaDeces(dataMortii.getLuna());
        this.autor.setZiDeces(dataMortii.getZi());
        this.autor.setWebsite(this.textWebsite.getText());
        this.autor.setTwitter(this.textTwitter.getText());
        this.autor.setFacebook(this.textFacebook.getText());
        this.autor.setDescriere(this.textDescriere.getText());
        this.autor.setWiki(this.textWiki.getText());
        List<GenLiterar> genuriLiterare = new ArrayList<>();
        for (Enum value : genLiterarComposite.getEnumValues()) {
            genuriLiterare.add((GenLiterar) value);
        }
        this.autor.setGenLiterar(genuriLiterare);
        this.autor.setLoculNasterii(textLocNastere.getText());
        this.autor.setTara(textTara.getText());
        if (mainImageComposite.getSelectedFile() != null) {
            this.autor.setMainImage(controller.saveDocument(mainImageComposite));
        }

        controller.save(autor);
    }

    private void markAsChanged() {
        observableProperty = textNume.getText();
        if (StringUtils.isNotEmpty(observableProperty)) {
            getBigLabelText().setText(observableProperty);
        }
        setChanged();
        notifyObservers();
    }

    @Override
    protected boolean validate() {
        try {
            if (StringUtils.isEmpty(this.textNume.getText())) {
                SWTeXtension.displayMessageW("Numele nu este introdus!");
                this.textNume.setFocus();
                return false;
            }
        } catch (Exception exc) {
            return false;
        }
        return true;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }
}
