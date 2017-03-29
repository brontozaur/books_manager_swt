package com.papao.books.ui.searcheable;

import com.papao.books.model.AbstractDB;
import com.papao.books.ui.custom.AdbSelectorComposite;
import com.papao.books.ui.custom.AdbSelectorData;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;

import java.util.TreeMap;

public class DboSearch extends AbstractSearchType {

	private static Logger logger = Logger.getLogger(DboSearch.class);

    private final AdbSelectorData data;
    private final AdbSelectorComposite selectorComp;

    public DboSearch(final BorgSearchSystem searchSystem, final AdbSelectorData data, final String colName) {
        super(searchSystem, colName);
        this.data = data;
        data.setSelectedMap(new TreeMap<Long, AbstractDB>());
        GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).extendedMargins(2, 0, 0, 0).spacing(1, 0).applyTo(this);
        this.selectorComp = new AdbSelectorComposite(this, this.data);

        setSize(computeSize(getSize().x, SWT.DEFAULT));
    }

    @Override
    public final void createContents() {}

    public final boolean containsValue(final Object value) {
        long numericValue = 0;
        if (value instanceof Integer) {
            numericValue = Long.valueOf((Integer) value);
        } else if (value instanceof Long) {
            numericValue = (Long) value;
        } else {
			logger.warn("Value [" + value + "] was NOT a number in search");
            return false;
        }
        if (!this.selectorComp.getDataTransport().getSelectedMap().isEmpty()) {
            return this.selectorComp.getDataTransport().getSelectedMap().get(numericValue) != null;
        }
        return true;
    }

    @Override
    public boolean isModified() {
        boolean result = !this.data.getSelectedMap().isEmpty() && (this.data.getSelectedMap().size() != this.data.getCacheMap().size());
        getLabelName().setForeground(result ? AbstractSearchType.FILTRU_ACTIV : AbstractSearchType.FILTRU_INACTIV);
        return result;
    }

    @Override
    public boolean compareValues(Object valueToBeCompared) {
        if (valueToBeCompared == null) {
            return false;
        }
        return containsValue(valueToBeCompared);
    }
}
