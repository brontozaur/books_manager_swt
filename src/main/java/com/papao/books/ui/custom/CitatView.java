package com.papao.books.ui.custom;

import com.novocode.naf.swt.custom.BalloonNotification;
import com.papao.books.model.Citat;
import com.papao.books.ui.auth.EncodeLive;
import com.papao.books.ui.interfaces.INavigation;
import com.papao.books.ui.util.WidgetCompositeUtil;
import com.papao.books.ui.view.AbstractCView;
import com.papao.books.ui.view.AbstractView;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class CitatView extends AbstractCView implements INavigation {

    private Citat citat;
    private Table table;
    private int selIndex;

    private Text textNrPagina;
    private Text textContent;

    public CitatView(final Shell parent, final Citat citat, final Table table, final int viewMode) {
        super(parent, viewMode);
        this.citat = citat;
        this.table = table;
        this.selIndex = table.getSelectionIndex();

        addComponents();
        populateFields();
    }

    private void addComponents() {
        setWidgetLayout(new GridLayout(2, false));
        getContainer().setLayout(getWidgetLayout());

        Composite temp = new Composite(getContainer(), SWT.NONE);
        GridLayoutFactory.fillDefaults().margins(0, 0).numColumns(2).applyTo(temp);
        GridDataFactory.fillDefaults().span(2, 1).applyTo(temp);

        new Label(temp, SWT.NONE).setText("Nr pagina");
        this.textNrPagina = new Text(temp, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(false, false).hint(50, SWT.DEFAULT).applyTo(textNrPagina);

        Label tmp = new Label(getContainer(), SWT.NONE);
        tmp.setText("Continut   ");
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(tmp);

        this.textContent = new Text(getContainer(), SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().grab(true, true).hint(300, 200).span(1, 1).applyTo(this.textContent);

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
        this.textNrPagina.setText(StringUtils.defaultIfBlank(this.citat.getNrPagina(), ""));
        this.textContent.setText(StringUtils.defaultIfBlank(this.citat.getContent(), ""));

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
        setObjectName("citat");
        setCreateUpperCompRightArea(true);
    }

    @Override
    protected void saveData() {
        this.citat.setNrPagina(this.textNrPagina.getText());
        this.citat.setContent(this.textContent.getText());
        this.citat.setCreatedBy(EncodeLive.getIdUser().toString());
    }

    @Override
    protected boolean validate() {
        try {
            if (StringUtils.isEmpty(this.textContent.getText())) {
                BalloonNotification.showNotification(textContent, "Notificare", "Citatul nu este introdus!", 1500);
                return false;
            }
        } catch (Exception exc) {
            return false;
        }
        return true;
    }

    public Citat getCitat() {
        return citat;
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
        this.citat = (Citat) item.getData();
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
        this.citat = (Citat) item.getData();
        populateFields();
    }
}
