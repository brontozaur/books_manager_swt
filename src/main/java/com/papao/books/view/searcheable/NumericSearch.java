package com.papao.books.view.searcheable;

import com.papao.books.view.util.NumberUtil;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.NumberFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import java.math.BigDecimal;

public class NumericSearch extends AbstractSearchType {

    private FormattedText textMin;
    private FormattedText textMax;

    public NumericSearch(final BorgSearchSystem searchSystem, final String colName) {
        super(searchSystem, colName);
    }

    @Override
    public final void createContents() {
        Composite comp;

        comp = new Composite(this, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).extendedMargins(5, 0, 0, 0).spacing(3, SWT.DEFAULT).applyTo(comp);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(comp);

        this.textMin = new FormattedText(comp, SWT.BORDER);
        this.textMin.setFormatter(NumberUtil.getFormatter(4, true));
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(73, SWT.DEFAULT).hint(73, SWT.DEFAULT).applyTo(this.textMin.getControl());
        ((NumberFormatter) this.textMin.getFormatter()).setFixedLengths(false, true);

        new Label(comp, SWT.NONE).setText("-");

        this.textMax = new FormattedText(comp, SWT.BORDER);
        this.textMax.setFormatter(NumberUtil.getFormatter(4, true));
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(73, SWT.DEFAULT).hint(73, SWT.DEFAULT).applyTo(this.textMax.getControl());
        ((NumberFormatter) this.textMax.getFormatter()).setFixedLengths(false, true);

        this.textMin.getControl().addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                getSearchSystem().getSearchButton().notifyListeners(SWT.MouseUp, new Event());
            }
        });
        this.textMax.getControl().addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                getSearchSystem().getSearchButton().notifyListeners(SWT.MouseUp, new Event());
            }
        });
    }

    public final double getMinValue() {
        double minValue = Double.MIN_VALUE;
        try {
            if (this.textMin.getValue() != null) {
                minValue = new BigDecimal(this.textMin.getValue().toString()).doubleValue();
            }
            if (minValue == 0) {
                minValue = Double.MIN_VALUE;
            }
        } catch (NumberFormatException exc) {
            minValue = Double.MIN_VALUE;
        }
        return minValue;
    }

    public final double getMaxValue() {
        double maxValue = Double.MAX_VALUE;
        try {
            if (this.textMax.getValue() != null) {
                maxValue = new BigDecimal(this.textMax.getValue().toString()).doubleValue();
            }
            if (maxValue == 0) {
                maxValue = Double.MAX_VALUE;
            }
        } catch (NumberFormatException exc) {
            maxValue = Double.MAX_VALUE;
        }
        return maxValue;
    }

    @Override
    public boolean isModified() {
        boolean result = (getMinValue() != Double.MIN_VALUE) || (getMaxValue() != Double.MAX_VALUE);
        getLabelName().setForeground(result ? AbstractSearchType.FILTRU_ACTIV : AbstractSearchType.FILTRU_INACTIV);
        return result;
    }

    @Override
    public boolean compareValues(Object valueToBeCompared) {
        if (valueToBeCompared == null) {
            return true;
        }
        return compareNumbers(getMinValue(), getMaxValue(), Double.valueOf(valueToBeCompared.toString()));
    }

}
