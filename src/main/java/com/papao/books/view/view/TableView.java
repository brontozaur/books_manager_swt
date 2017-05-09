package com.papao.books.view.view;

import com.papao.books.view.interfaces.IHelp;
import com.papao.books.view.interfaces.INavigation;
import com.papao.books.view.interfaces.IReset;
import com.papao.books.view.util.WidgetCompositeUtil;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class TableView extends AbstractCView implements IReset, IHelp,
        INavigation {

    private String[] value;
    private final Table table;
    private int selIndex = -1;
    private final Text[] texte;

    public TableView(final Shell parent, final Table table, final TableItem item, final int viewMode) {
        super(parent, viewMode);

        this.table = table;
        this.selIndex = table.getSelectionIndex();
        this.texte = new Text[table.getColumnCount()];

        setWidgetLayout(new GridLayout(2, false));
        getContainer().setLayout(getWidgetLayout());

        setValue(new String[table.getColumnCount()]);

        drawTable(item);

        WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());

        getToolItemBack().setEnabled(getViewMode() == AbstractView.MODE_MODIFY);
        getToolItemNext().setEnabled(getViewMode() == AbstractView.MODE_MODIFY);

        if (this.texte.length > 0) {
            this.texte[0].setFocus();
        }

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

        getWidget().getDisplay().addFilter(SWT.KeyDown, navigationListener);

        getWidget().addListener(SWT.Dispose, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                e.widget.getDisplay().removeFilter(SWT.KeyDown, navigationListener);
            }
        });
    }

    private void drawTable(final TableItem tableItem) {
        final TableColumn[] cols = this.table.getColumns();
        for (int i = 0; i < cols.length; i++) {
            new Label(getContainer(), SWT.NONE).setText(cols[i].getText());
            this.texte[i] = new Text(getContainer(), SWT.BORDER);
            if (tableItem != null) {
                this.texte[i].setText(tableItem.getText(i));
            }
            GridDataFactory.fillDefaults().grab(true, false).applyTo(this.texte[i]);
        }
    }

    @Override
    public void customizeView() {
        setShellStyle(SWT.MIN | SWT.CLOSE | SWT.RESIZE);
        setViewOptions(AbstractView.ADD_CANCEL | AbstractView.ADD_OK);
        setShowSaveOKMessage(false);
        setUseCoords(false);
        setObjectName("valoare");
    }

    @Override
    public final void reset() {
        for (int i = 0; i < this.texte.length; i++) {
            this.texte[i].setText("");
            getValue()[i] = "";
        }
    }

    @Override
    protected void saveData() {
        for (int i = 0; i < this.texte.length; i++) {
            getValue()[i] = this.texte[i].getText();
        }
    }

    @Override
    public final void showHelp() {
        SWTeXtension.displayMessageI("In campurile din fereastra sunt afisate datele inregistrarii selectate.");
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
        if (getViewMode() != AbstractView.MODE_VIEW) {
            setViewMode(AbstractView.MODE_MODIFY);
        }

        final TableItem item = this.table.getSelection()[0];
        for (int i = 0; i < this.table.getColumnCount(); i++) {
            this.texte[i].setText(item.getText(i));
        }
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
        if (getViewMode() != AbstractView.MODE_VIEW) {
            setViewMode(AbstractView.MODE_MODIFY);
        }
        final TableItem item = this.table.getSelection()[0];
        for (int i = 0; i < this.table.getColumnCount(); i++) {
            this.texte[i].setText(item.getText(i));
        }
    }

    @Override
    protected boolean validate() {
        return true;
    }

    public final String[] getValue() {
        return this.value;
    }

    public final void setValue(final String[] value) {
        this.value = value;
    }

}
