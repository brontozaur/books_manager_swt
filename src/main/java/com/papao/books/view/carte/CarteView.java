package com.papao.books.view.carte;

import com.papao.books.model.AbstractDB;
import com.papao.books.model.BlankDbObject;
import com.papao.books.model.Carte;
import com.papao.books.repository.CarteRepository;
import com.papao.books.view.bones.impl.view.AbstractCSaveView;
import com.papao.books.view.custom.ComboImage;
import com.papao.books.view.custom.LinkedinComposite;
import com.papao.books.view.providers.ContentProposalProvider;
import com.papao.books.view.util.WidgetCompositeUtil;
import com.papao.books.view.view.AbstractView;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
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
    private FormattedText textLungime;
    private FormattedText textLatime;
    private FormattedText textGreutate;
    private FormattedText textNrPagini;
    private Text textSerie;
    private LinkedinComposite compositeTraducatori;
    private String isbn;
    private Button buttonCuIlustratii;
    private Button buttonCuAutograf;
    private LinkedinComposite compositeAutoriIlustratii;
    private LinkedinComposite compositeTehnoredactori;
    private String tiparitLa;
    private ComboImage comboTipCoperta;
    private ComboImage comboLimba;
    private ComboImage comboTraducereDin;
    private ComboImage comboLimbaOriginala;
    private LinkedinComposite compositeDistinctiiAcordate;

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
        setWidgetLayout(new GridLayout(2, false));
        getContainer().setLayout(getWidgetLayout());

        Label labelAutor = new Label(getContainer(), SWT.NONE);
        labelAutor.setText("Autori");
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(labelAutor);
        this.compositeAutori = new LinkedinComposite(getContainer(), mongoTemplate.getCollection("carte").distinct("autori"), carte.getAutori());

        new Label(getContainer(), SWT.NONE).setText("Titlu");
        this.textTitlu = new Text(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().hint(100, SWT.DEFAULT).grab(false, false).applyTo(this.textTitlu);

        new Label(getContainer(), SWT.NONE).setText("Editura");
        this.textEditura = new Text(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().hint(100, SWT.DEFAULT).grab(false, false).applyTo(this.textEditura);
        ContentProposalProvider.addContentProposal(textEditura, mongoTemplate.getCollection("carte").distinct("editura"));

        List<BlankDbObject> aniObjects = new ArrayList<>();
        for (int i = 1900; i< Calendar.getInstance().get(Calendar.YEAR);i++) {
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
        GridDataFactory.fillDefaults().hint(50, SWT.DEFAULT).grab(false, false).applyTo(this.comboAnAparitie);

        WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());
    }

    private void populateFields() {
        this.textTitlu.setText(StringUtils.defaultIfBlank(this.carte.getTitlu(), ""));
        this.textEditura.setText(StringUtils.defaultIfBlank(this.carte.getEditura(), ""));
        this.comboAnAparitie.select(this.carte.getAnAparitie());

        if (!isViewEnabled()) {
            WidgetCompositeUtil.enableGUI(getContainer(), false);
            WidgetCompositeUtil.enableGUI(getCompHIRE(), false);
            getContainer().setEnabled(true);
        }
    }

    @Override
    public final void customizeView() {
        setShellStyle(SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE);
        setViewOptions(AbstractView.ADD_CANCEL | AbstractView.ADD_OK);
        setObjectName("carte");
    }

    @Override
    protected void saveData() {
        this.carte.setTitlu(this.textTitlu.getText());
        this.carte.setAutori(compositeAutori.getValoriIntroduse());
        this.carte.setEditura(textEditura.getText());
        this.carte.setAnAparitie(comboAnAparitie.getText());
        carteRepository.save(carte);
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
