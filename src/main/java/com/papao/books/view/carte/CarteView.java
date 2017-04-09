package com.papao.books.view.carte;

import com.papao.books.model.AbstractDB;
import com.papao.books.model.Autor;
import com.papao.books.model.Carte;
import com.papao.books.repository.AutorRepository;
import com.papao.books.repository.CarteRepository;
import com.papao.books.view.bones.impl.view.AbstractCSaveView;
import com.papao.books.view.custom.LinkedinComposite;
import com.papao.books.view.util.WidgetCompositeUtil;
import com.papao.books.view.view.AbstractView;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

public class CarteView extends AbstractCSaveView {

    private Carte carte;
    private CarteRepository carteRepository;
    private AutorRepository autorRepository;
    private MongoTemplate mongoTemplate;

    private LinkedinComposite compositeAutor;
    private Text textTitlu;

    public CarteView(final Shell parent, final Carte carte,
                     final CarteRepository carteRepository,
                     final AutorRepository autorRepository,
                     MongoTemplate mongoTemplate,
                     final int viewMode) {
        super(parent, viewMode, carte.getId());
        this.carte = carte;
        this.carteRepository = carteRepository;
        this.mongoTemplate = mongoTemplate;
        this.autorRepository = autorRepository;

        addComponents();
        populateFields();
    }

    private void addComponents() {
        setWidgetLayout(new GridLayout(2, false));
        getContainer().setLayout(getWidgetLayout());

        Label labelAutor = new Label(getContainer(), SWT.NONE);
        labelAutor.setText("Autor");
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(labelAutor);
//        List<String> autori = mongoTemplate.getCollection("carte").distinct("autor.nume");
        List<Autor> autoriList = autorRepository.findAll();
        String[] numeAutori = carte.getNumeAutoriList(autorRepository.findAll());
        this.compositeAutor = new LinkedinComposite(getContainer(), numeAutori, autoriList, carte.getNumeAutoriList(autorRepository.findByIdInOrderByNumeAsc(carte.getAutori())));

        new Label(getContainer(), SWT.NONE).setText("Titlu");
        this.textTitlu = new Text(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().grab(false, false).hint(150, SWT.DEFAULT).applyTo(this.textTitlu);

        WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());
    }

    private void populateFields() {
        this.textTitlu.setText(StringUtils.defaultIfBlank(this.carte.getTitlu(), ""));

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
        this.carte.setAutori(compositeAutor.getIdAutori());
        carteRepository.save(carte);
    }

    @Override
    protected boolean validate() {
        try {
            if (StringUtils.isEmpty(this.textTitlu.getText())) {
                SWTeXtension.displayMessageW("Titlul cartii nu este introdus!");
                this.compositeAutor.getTextSearch().setFocus();
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
