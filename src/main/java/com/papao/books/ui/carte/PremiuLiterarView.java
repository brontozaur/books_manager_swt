package com.papao.books.ui.carte;

import com.papao.books.model.PremiuLiterar;
import com.papao.books.ui.util.WidgetCompositeUtil;
import com.papao.books.ui.view.AbstractCSaveView;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class PremiuLiterarView extends AbstractCSaveView {

    private PremiuLiterar premiuLiterar;

    private Text textAn;
    private Text textPremiu;

    public PremiuLiterarView(final Shell parent, final PremiuLiterar premiuLiterar, final int viewMode) {
        super(parent, viewMode, new ObjectId());
        this.premiuLiterar = premiuLiterar;

        addComponents();
        populateFields();
    }

    private void addComponents() {
        setWidgetLayout(new GridLayout(2, false));
        getContainer().setLayout(getWidgetLayout());

        new Label(getContainer(), SWT.NONE).setText("An");
        textAn = new Text(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().minSize(50, SWT.DEFAULT).hint(50, SWT.DEFAULT).applyTo(textAn);
        textAn.addListener(SWT.KeyUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (!StringUtils.isNumeric(textAn.getText())) {
                    textAn.setText("");
                }
            }
        });

        new Label(getContainer(), SWT.NONE).setText("Premiu");
        this.textPremiu = new Text(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(this.textPremiu);

        WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());
    }

    private void populateFields() {
        this.textAn.setText(premiuLiterar.getAn());
        this.textPremiu.setText(premiuLiterar.getPremiu());

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
        setObjectName("premiu literar");
    }

    @Override
    protected void saveData() {
        this.premiuLiterar.setAn(this.textAn.getText());
        this.premiuLiterar.setPremiu(this.textPremiu.getText());
    }

    @Override
    protected boolean validate() {
        try {
            if (StringUtils.isEmpty(this.textPremiu.getText())) {
                SWTeXtension.displayMessageW("Nu ați introdus denumirea premiului!");
                this.textPremiu.setFocus();
                return false;
            }
        } catch (Exception exc) {
            return false;
        }
        return true;
    }

    public final PremiuLiterar getPremiuLiterar() {
        return this.premiuLiterar;
    }

}
