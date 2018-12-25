package com.papao.books.ui.custom;

import com.papao.books.ApplicationService;
import com.papao.books.controller.ApplicationController;
import com.papao.books.model.CarteSerie;
import com.papao.books.ui.providers.ContentProposalProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.springframework.util.StringUtils;

public class CarteSerieComposite extends Composite {

    private Text textSerie;
    private Text textVolum;

    public CarteSerieComposite(Composite parent, CarteSerie carteSerie) {
        super(parent, SWT.NONE);

        GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).spacing(0, 0).applyTo(this);

        this.textSerie = new Text(this, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).hint(100, SWT.DEFAULT).applyTo(this.textSerie);
        ContentProposalProvider.addContentProposal(textSerie, ApplicationController.getDistinctFieldAsContentProposal(ApplicationService.getApplicationConfig().getBooksCollectionName(), "serie.nume"));

        new Label(this, SWT.NONE).setText("#");

        this.textVolum = new Text(this, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(false, false).hint(35, SWT.DEFAULT).applyTo(this.textVolum);

        this.textSerie.setText(carteSerie.getNume().trim());
        this.textVolum.setText(carteSerie.getVolum().trim());
    }

    @Override
    public boolean setFocus() {
        return textSerie.setFocus();
    }

    public CarteSerie getCarteSerie() {
        if(StringUtils.isEmpty(this.textSerie.getText())) {
            return null;
        }
        return new CarteSerie(this.textSerie.getText(), this.textVolum.getText());
    }
}
