package com.papao.books.ui.user;

import com.novocode.naf.swt.custom.BalloonNotification;
import com.papao.books.controller.UserController;
import com.papao.books.model.User;
import com.papao.books.ui.util.WidgetCompositeUtil;
import com.papao.books.ui.view.AbstractCSaveView;
import com.papao.books.ui.view.AbstractView;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class UserView extends AbstractCSaveView {

    private User user;

    private Text textNume;
    private Text textPrenume;

    public UserView(final Shell parent, final User user, final int viewMode) {
        super(parent, viewMode, user.getId());
        this.user = user;

        addComponents();
        populateFields();
    }

    private void addComponents() {
        setWidgetLayout(new GridLayout(2, false));
        getContainer().setLayout(getWidgetLayout());

        new Label(getContainer(), SWT.NONE).setText("Nume");
        this.textNume = new Text(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).span(1, 1).applyTo(this.textNume);

        new Label(getContainer(), SWT.NONE).setText("Prenume");
        this.textPrenume = new Text(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).span(1, 1).applyTo(this.textPrenume);

        WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());
    }

    private void populateFields() {
        this.textNume.setText(StringUtils.defaultIfBlank(this.user.getNume(), ""));
        this.textPrenume.setText(StringUtils.defaultIfBlank(this.user.getPrenume(), ""));

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
        this.user.setNume(this.textNume.getText());
        this.user.setPrenume(this.textPrenume.getText());
        UserController.save(user);
    }

    @Override
    protected boolean validate() {
        try {
            if (StringUtils.isEmpty(this.textNume.getText())) {
//                SWTeXtension.displayMessageW("Numele nu este introdus!");
                BalloonNotification.showNotification(textNume, "Notificare", "Numele nu este introdus!", 1500);
                return false;
            }
            if (StringUtils.isEmpty(this.textPrenume.getText())) {
//                SWTeXtension.displayMessageW("Numele nu este introdus!");
                BalloonNotification.showNotification(textPrenume, "Notificare", "Prenumele nu este introdus!", 1500);
                return false;
            }
        } catch (Exception exc) {
            return false;
        }
        return true;
    }

    public final User getUser() {
        return this.user;
    }

    private void setUser(final User user) {
        this.user = user;
    }

}
