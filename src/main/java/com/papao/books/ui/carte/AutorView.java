package com.papao.books.ui.carte;

import com.mongodb.gridfs.GridFSDBFile;
import com.papao.books.ApplicationService;
import com.papao.books.controller.ApplicationController;
import com.papao.books.controller.AutorController;
import com.papao.books.model.AnLunaZiData;
import com.papao.books.model.Autor;
import com.papao.books.model.GenLiterar;
import com.papao.books.ui.custom.AnLunaZiComposite;
import com.papao.books.ui.custom.ImageSelectorComposite;
import com.papao.books.ui.custom.LinkedinComposite;
import com.papao.books.ui.providers.ContentProposalProvider;
import com.papao.books.ui.util.WidgetCompositeUtil;
import com.papao.books.ui.view.AbstractCSaveView;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AutorView extends AbstractCSaveView {

    private Autor autor;

    private Text textNume;
    private Text textTitlu;
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

    public AutorView(final Shell parent, final Autor autor, final int viewMode) {
        super(parent, viewMode, autor.getId());
        this.autor = autor;

        addComponents();
        populateFields();
    }

    private void addComponents() {
        setWidgetLayout(new GridLayout(2, false));
        getContainer().setLayout(getWidgetLayout());

        Composite compLeft = new Composite(getContainer(), SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(4).applyTo(compLeft);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(compLeft);

        label(compLeft, "Nume");
        this.textNume = new Text(compLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(3, 1).minSize(350, SWT.DEFAULT).applyTo(this.textNume);
        textNume.addListener(SWT.KeyUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
                markAsChanged();
            }
        });

        label(compLeft, "Titlu");
        this.textTitlu = new Text(compLeft, SWT.BORDER);
        ContentProposalProvider.addContentProposal(this.textTitlu, ApplicationController.getDistinctFieldAsContentProposal(ApplicationService.getApplicationConfig().getAutoriCollectionName(), "titlu"));
        GridDataFactory.fillDefaults().grab(true, false).span(3, 1).minSize(350, SWT.DEFAULT).applyTo(this.textTitlu);

        label(compLeft, "Data nasterii", true);
        this.dataNasteriiComposite = new AnLunaZiComposite(compLeft, autor.getDataNasterii(true));
        GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(this.dataNasteriiComposite);

        label(compLeft, "Data mortii", true);
        this.dataMortiiComposite = new AnLunaZiComposite(compLeft, autor.getDataMortii(true));
        GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(this.dataMortiiComposite);

        label(compLeft, "Gen literar");
        this.genLiterarComposite = new LinkedinComposite(compLeft,
                GenLiterar.class, autor.getGenLiterar());
        ((GridData) this.genLiterarComposite.getLayoutData()).horizontalSpan = 3;
        ((GridData) this.genLiterarComposite.getLayoutData()).widthHint = 350;

        label(compLeft, "Website");
        this.textWebsite = new Text(compLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(textWebsite);
        this.textWebsite.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                encodeUrl((Text) event.widget);
            }
        });

        label(compLeft, "Facebook");
        this.textFacebook = new Text(compLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(textFacebook);
        this.textFacebook.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                encodeUrl((Text) event.widget);
            }
        });

        label(compLeft, "Twitter");
        this.textTwitter = new Text(compLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(textTwitter);
        this.textTwitter.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                encodeUrl((Text) event.widget);
            }
        });

        label(compLeft, "Wiki");
        this.textWiki = new Text(compLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(textWiki);
        this.textWiki.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                encodeUrl((Text) event.widget);
            }
        });

        label(compLeft, "Loc nastere");
        this.textLocNastere = new Text(compLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this.textLocNastere);

        label(compLeft, "Tara");
        this.textTara = new Text(compLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this.textTara);
        ContentProposalProvider.addContentProposal(textTara, ApplicationController.getDistinctFieldAsContentProposal(ApplicationService.getApplicationConfig().getAutoriCollectionName(), "tara"));

        label(compLeft, "Descriere");
        this.textDescriere = new Text(compLeft, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().grab(true, true).hint(300, 100).span(3, 1).applyTo(textDescriere);

        Composite compImage = new Composite(getContainer(), SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(compImage);
        GridDataFactory.fillDefaults().grab(false, false).align(SWT.END, SWT.BEGINNING).applyTo(compImage);

        Image mainImage = null;
        String imageName = null;
        if (autor.getMainImage() != null) {
            GridFSDBFile image = ApplicationController.getDocumentData(autor.getMainImage().getId());
            if (image != null) {
                imageName = image.getFilename();
                mainImage = new Image(Display.getDefault(), image.getInputStream());
            }
        }

        this.mainImageComposite = new ImageSelectorComposite(compImage, mainImage, imageName, ApplicationService.getApplicationConfig().getAppImagesFolder());
        this.addObserver(mainImageComposite);

        WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());
    }

    private void label(Composite parent, String labelName) {
        label(parent, labelName, false);
    }

    private void label(Composite parent, String labelName, boolean verticallyAlignCenter) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(labelName);
        GridDataFactory.fillDefaults().align(SWT.END, verticallyAlignCenter ? SWT.CENTER : SWT.BEGINNING).applyTo(label);
    }

    @Override
    public String getObservableProperty() {
        return observableProperty;
    }

    private void populateFields() {
        this.textNume.setText(this.autor.getNumeComplet());
        this.textTitlu.setText(this.autor.getTitlu());
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
        this.autor.setTitlu(this.textTitlu.getText());
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
            this.autor.setMainImage(ApplicationController.saveDocument(mainImageComposite));
        }

        AutorController.save(autor);
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
