package com.papao.books.view.carte;

import com.papao.books.model.AbstractDB;
import com.papao.books.model.Autor;
import com.papao.books.model.Carte;
import com.papao.books.repository.CarteRepository;
import com.papao.books.view.bones.impl.view.AbstractCSaveView;
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

public class CarteView extends AbstractCSaveView {

    private Carte carte;
    private CarteRepository carteRepository;

    private Text textAutor;
    private Text textTitlu;

    public CarteView(final Shell parent, final Carte carte, final CarteRepository carteRepository, final int viewMode) {
        super(parent, viewMode, carte.getId());
        this.carte = carte;
        this.carteRepository = carteRepository;

        addComponents();
        populateFields();
    }

    private void addComponents() {
        setWidgetLayout(new GridLayout(2, false));
        getContainer().setLayout(getWidgetLayout());

        new Label(getContainer(), SWT.NONE).setText("Autor");
        this.textAutor = new Text(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).span(1, 1).applyTo(this.textAutor);

        new Label(getContainer(), SWT.NONE).setText("Titlu");
        this.textTitlu = new Text(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).span(1, 1).applyTo(this.textTitlu);

        WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());
    }

    private void populateFields() {
        this.textAutor.setText(StringUtils.defaultIfBlank(this.carte.getAutor() != null ? carte.getAutor().getNume() : "", ""));
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
        setObjectName("utilizator");
    }

    @Override
    protected void saveData() {
        if (carte.getAutor() != null) {
            carte.setAutor(new Autor());
        }
        this.carte.getAutor().setNume(this.textAutor.getText());
        this.carte.setTitlu(this.textTitlu.getText());
        carteRepository.save(carte);
    }

    @Override
    protected boolean validate() {
        try {
            if (StringUtils.isEmpty(this.textTitlu.getText())) {
                SWTeXtension.displayMessageW("Titlul cartii nu este introdus!");
                this.textAutor.setFocus();
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
