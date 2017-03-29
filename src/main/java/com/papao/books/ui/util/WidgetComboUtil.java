package com.papao.books.ui.util;

import com.papao.books.model.AbstractDB;
import com.papao.books.util.Constants;
import com.papao.books.util.ObjectUtil;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Combo;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class WidgetComboUtil {

	private static Logger logger = Logger.getLogger(WidgetComboUtil.class);

    private WidgetComboUtil() {}

    public static void populateCombo(final Combo combo,
                                     final List<? extends AbstractDB> elements,
                                     final String reflexiveMethod,
                                     final boolean addAll,
                                     final boolean isFeminin) {
        Method meth;
        try {
            if ((combo == null) || combo.isDisposed()) {
                return;
            }
            if ((elements == null) || elements.isEmpty()) {
                return;
            }
            meth = ObjectUtil.getMethod(elements.get(0).getClass(), reflexiveMethod);
            if (meth == null) {
                return;
            }
            if (addAll) {
				combo.add(isFeminin ? Constants.TOATE : Constants.TOTI);
            }
            final Iterator<? extends AbstractDB> iter = elements.iterator();
            while (iter.hasNext()) {
                Object valueOne = meth.invoke(iter.next(), (Object[]) null);
                combo.add(valueOne != null ? valueOne.toString() : "");
            }
            combo.setData(elements);
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public static void populateCombo(final Combo combo,
                                     final Map<Long, ? extends AbstractDB> elements,
                                     final String reflexiveMethod,
                                     final boolean addAll,
                                     final boolean isFeminin) {
        Method meth;
        try {
            if ((combo == null) || combo.isDisposed()) {
                return;
            }
            if ((elements == null) || elements.isEmpty()) {
                return;
            }
            meth = ObjectUtil.getMethod(elements.get((long) 0).getClass(), reflexiveMethod);
            if (meth == null) {
                return;
            }
            if (addAll) {
				combo.add(isFeminin ? Constants.TOATE : Constants.TOTI);
            }
            final Iterator<? extends AbstractDB> iter = elements.values().iterator();
            while (iter.hasNext()) {
                Object valueOne = meth.invoke(iter.next(), (Object[]) null);
                combo.add(valueOne != null ? valueOne.toString() : "");
            }
            combo.setData(elements);
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

}
