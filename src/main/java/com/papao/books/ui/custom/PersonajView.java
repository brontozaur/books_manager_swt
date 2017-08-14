package com.papao.books.ui.custom;

import com.novocode.naf.swt.custom.BalloonNotification;
import com.papao.books.model.Personaj;
import com.papao.books.ui.interfaces.INavigation;
import com.papao.books.ui.util.WidgetCompositeUtil;
import com.papao.books.ui.view.AbstractCView;
import com.papao.books.ui.view.AbstractView;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class PersonajView extends AbstractCView implements INavigation {

    private Personaj personaj;
    private Table table;
    private int selIndex;

    private Text textNume;
    private Text textRol;
    private Text textDescriere;

    public PersonajView(final Shell parent, final Personaj personaj, final Table table, final int viewMode) {
        super(parent, viewMode);
        this.personaj = personaj;
        this.table = table;
        this.selIndex = table.getSelectionIndex();

        addComponents();
        populateFields();

        WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());
    }

    private void addComponents() {
        setWidgetLayout(new GridLayout(2, false));
        getContainer().setLayout(getWidgetLayout());

        new Label(getContainer(), SWT.NONE).setText("Nume");
        this.textNume = new Text(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(textNume);

        new Label(getContainer(), SWT.NONE).setText("Rol");
        this.textRol = new Text(getContainer(), SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(textRol);

        Label tmp = new Label(getContainer(), SWT.NONE);
        tmp.setText("Descriere");
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(tmp);

        this.textDescriere = new Text(getContainer(), SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().grab(true, true).hint(300, 200).span(1, 1).applyTo(this.textDescriere);

        WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());

        final Listener navigationListener = new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                if (e.stateMask == SWT.ALT) {
                    if (e.keyCode == SWT.ARROW_RIGHT) {
                        goForward();
                    }
                    if (e.keyCode == SWT.ARROW_LEFT) {
                        goBack();
                    }
                }
            }
        };

        getShell().getDisplay().addFilter(SWT.KeyDown, navigationListener);

        getShell().addListener(SWT.Dispose, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                e.widget.getDisplay().removeFilter(SWT.KeyDown, navigationListener);
            }
        });
    }

    private void populateFields() {
        this.textNume.setText(StringUtils.defaultIfBlank(this.personaj.getNume(), ""));
        this.textRol.setText(StringUtils.defaultIfBlank(this.personaj.getRol(), ""));
        this.textDescriere.setText(StringUtils.defaultIfBlank(this.personaj.getDescriere(), ""));

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
        setObjectName("personaj");
        setCreateUpperCompRightArea(true);
    }

    @Override
    protected void saveData() {
        this.personaj.setNume(this.textNume.getText());
        this.personaj.setRol(this.textRol.getText());
        this.personaj.setDescriere(textDescriere.getText());
    }

    @Override
    protected boolean validate() {
        try {
            if (StringUtils.isEmpty(this.textNume.getText())) {
                BalloonNotification.showNotification(textDescriere, "Notificare", "Numele nu este introdus!", 1500);
                return false;
            }
        } catch (Exception exc) {
            return false;
        }
        return true;
    }

    public Personaj getPersonaj() {
        return personaj;
    }

    @Override
    public void goBack() {
        if ((this.table == null) || this.table.isDisposed()) {
            return;
        }
        if ((this.selIndex - 1) < 0) {
            this.selIndex = this.table.getItemCount();
        }
        this.table.setSelection(--this.selIndex);
        if (this.table.getSelectionCount() <= 0) {
            this.selIndex = 0;
            if (this.table.getItemCount() > 0) {
                this.table.setSelection(0);
            } else {
                return;
            }
        }

        final TableItem item = this.table.getSelection()[0];
        this.personaj = (Personaj) item.getData();
        populateFields();
    }

    @Override
    public void goForward() {
        if ((this.table == null) || this.table.isDisposed()) {
            return;
        }
        if ((this.selIndex < 0) || ((this.selIndex + 1) > (this.table.getItemCount() - 1))) {
            this.selIndex = -1;
        }
        this.table.setSelection(++this.selIndex);
        if (this.table.getSelectionCount() <= 0) {
            this.selIndex = 0;
            if (this.table.getItemCount() > 0) {
                this.table.setSelection(0);
            } else {
                return;
            }
        }
        final TableItem item = this.table.getSelection()[0];
        this.personaj = (Personaj) item.getData();
        populateFields();
    }
}
