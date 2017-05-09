package com.papao.books.export;

import com.papao.books.ui.interfaces.ConfigurationException;
import com.papao.books.ui.interfaces.IConfig;
import com.papao.books.ui.interfaces.IReset;
import com.papao.books.ui.util.WidgetCompositeUtil;
import com.papao.books.ui.util.WidgetCursorUtil;
import com.papao.books.ui.view.AbstractCView;
import com.papao.books.ui.view.AbstractView;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.widgets.*;

import java.util.Map;
import java.util.TreeMap;

public abstract class AbstractExportView extends AbstractCView implements IReset, Listener {

    private final static Logger logger = Logger.getLogger(AbstractExportView.class);

    protected final Map<String, IConfig> mapComponents = new TreeMap<String, IConfig>();
    protected ViewForm rightForm;
    protected Table leftTable;

    public AbstractExportView(final Shell parent) {
        super(parent, AbstractView.MODE_NONE);
        addComponents();
    }

    private void addComponents() {
        Composite compLeft;
        SashForm sash;

        sash = new SashForm(getContainer(), SWT.HORIZONTAL | SWT.SMOOTH);
        sash.SASH_WIDTH = 4;
        GridDataFactory.fillDefaults().grab(true, true).minSize(600, 400).applyTo(sash);

        compLeft = new Composite(sash, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(compLeft);
        GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).margins(0, 0).applyTo(compLeft);

        this.leftTable = new Table(compLeft, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).span(3, 1).applyTo(this.leftTable);
        new TableColumn(this.leftTable, SWT.NONE).setWidth(120);
        this.leftTable.addListener(SWT.Selection, this);
        WidgetCursorUtil.addHandCursorListener(this.leftTable);

        this.rightForm = new ViewForm(sash, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(this.rightForm);

        sash.setWeights(new int[]{
                1, 2});

        WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());
    }

    @Override
    public final void handleEvent(final Event e) {
        if (e.type == SWT.Selection) {
            if (e.widget == this.leftTable) {
                if (this.leftTable.getSelectionCount() <= 0) {
                    return;
                }
                actionPerformed(this.leftTable.getSelection()[0].getText());
            }
        }
    }

    public abstract void actionPerformed(final String catName);

    @Override
    protected final void saveData() throws ConfigurationException {
        for (IConfig cfg : this.mapComponents.values()) {
            cfg.save();
        }
    }

    @Override
    protected final boolean validate() {
        for (IConfig cfg : this.mapComponents.values()) {
            if (!cfg.validate()) {
                actionPerformed(cfg.getCatName());
                return false;
            }
        }
        return true;
    }
}
