package com.papao.books.view.searcheable;

import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.util.StringUtil;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class SimpleTextSearch extends AbstractSearchType {

    private Text textCriteriuOK;

    public SimpleTextSearch(final BorgSearchSystem searchSystem, final String colName) {
        super(searchSystem, colName);
    }

    @Override
    public final void createContents() {
        Composite comp;

        comp = new Composite(this, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).extendedMargins(5, 0, 0, 0).spacing(0, 0).applyTo(comp);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(comp);

        this.textCriteriuOK = new Text(comp, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).hint(85, SWT.DEFAULT).minSize(85, SWT.DEFAULT).applyTo(this.textCriteriuOK);
        SWTeXtension.addColoredFocusListener(this.textCriteriuOK, ColorUtil.COLOR_FOCUS_YELLOW);
        this.textCriteriuOK.setToolTipText("Introduceti una sau mai multe valori (separate prin virgula)");

        this.textCriteriuOK.addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                getSearchSystem().getSearchButton().notifyListeners(SWT.MouseUp, new Event());
            }
        });
        this.textCriteriuOK.setFocus();
    }

    public String[] getValue() {
        if ((this.textCriteriuOK == null) || this.textCriteriuOK.isDisposed()) {
            return new String[0];
        }
        return this.textCriteriuOK.getText().replaceAll(" ", "").split(",");
    }

    @Override
    public boolean isModified() {
        boolean result = StringUtils.isNotEmpty(this.textCriteriuOK.getText());
        getLabelName().setForeground(result ? AbstractSearchType.FILTRU_ACTIV : AbstractSearchType.FILTRU_INACTIV);
        return result;
    }

    public final boolean compareStrings(final String full) {
        if ((getValue() == null) || ((getValue().length == 1) && StringUtils.isEmpty(getValue()[0]))) {
            return true;
        }
        final int lenght = getValue().length;
        for (int i = 0; i < lenght; i++) {
            if (compareStrings(getValue()[i], full)) {
                return true;
            }
        }
        return false;
    }

    public final boolean compareStrings(final String sirCautat, final String full) {
        return StringUtil.compareStrings(sirCautat, full);
    }

    @Override
    public boolean compareValues(Object valueToBeCompared) {
        if (valueToBeCompared == null) {
            return true;
        }
        return compareStrings(valueToBeCompared.toString());
    }
}
