package com.papao.books.ui.custom;

import com.novocode.naf.swt.custom.BalloonNotification;
import com.papao.books.model.Capitol;
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

public class CapitolView extends AbstractCView implements INavigation {

    private Capitol capitol;
    private Table table;
    private int selIndex;

    private Text textTitlu;
    private Text textNrPagina;
    private Text textNrCapitol;
    private Text textMotto;

    public CapitolView(final Shell parent, final Capitol capitol, final Table table, final int viewMode) {
        super(parent, viewMode);
        this.capitol = capitol;
        this.table = table;
        this.selIndex = table.getSelectionIndex();

        addComponents();
        populateFields();
    }

    private void addComponents() {
        setWidgetLayout(new GridLayout(1, false));
        getContainer().setLayout(getWidgetLayout());

        Composite temp = new Composite(getContainer(), SWT.NONE);
        GridLayoutFactory.fillDefaults().margins(0, 0).numColumns(4).applyTo(temp);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(temp);

        new Label(temp, SWT.NONE).setText("Nr capitol");
        this.textNrCapitol = new Text(temp, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(false, false).hint(50, SWT.DEFAULT).applyTo(textNrCapitol);

        new Label(temp, SWT.NONE).setText("Titlu");
        this.textTitlu = new Text(temp, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(textTitlu);

        temp = new Composite(getContainer(), SWT.NONE);
        GridLayoutFactory.fillDefaults().margins(0, 0).numColumns(3).applyTo(temp);

        new Label(temp, SWT.NONE).setText("Nr pagina");
        this.textNrPagina = new Text(temp, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(false, false).hint(50, SWT.DEFAULT).applyTo(textNrPagina);

        new Label(temp, SWT.NONE).setText("Motto");

        this.textMotto = new Text(getContainer(), SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().grab(true, true).hint(300, 200).span(1, 1).applyTo(this.textMotto);

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
        this.textNrCapitol.setText(StringUtils.defaultIfBlank(this.capitol.getNr(), ""));
        this.textTitlu.setText(StringUtils.defaultIfBlank(this.capitol.getTitlu(), ""));
        this.textNrPagina.setText(StringUtils.defaultIfBlank(this.capitol.getPagina(), ""));
        this.textMotto.setText(StringUtils.defaultIfBlank(this.capitol.getMotto(), ""));

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
        setObjectName("capitol");
        setCreateUpperCompRightArea(true);
    }

    @Override
    protected void saveData() {
        this.capitol = new Capitol(textNrCapitol.getText(), textTitlu.getText(), textNrPagina.getText(), textMotto.getText());
    }

    @Override
    protected boolean validate() {
        try {
            if (StringUtils.isEmpty(this.textTitlu.getText())) {
                BalloonNotification.showNotification(textTitlu, "Notificare", "Titlul nu este introdus!", 1500);
                return false;
            }
        } catch (Exception exc) {
            return false;
        }
        return true;
    }

    public Capitol getCapitol() {
        return this.capitol;
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
        this.capitol = (Capitol) item.getData();
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
        this.capitol = (Capitol) item.getData();
        populateFields();
    }
}
