package com.papao.books.model;

import com.papao.books.view.util.ObjectUtil;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Comparator;

public final class AdbComparator {

	private static Logger logger = Logger.getLogger(AdbComparator.class);

    private AdbComparator() {
        super();
    }

    public static Comparator<AbstractDB> getComparator(final Class<? extends AbstractDB> clazz, final String methodName) {
        Comparator<AbstractDB> comparator = null;
        try {
            final Method meth = ObjectUtil.getMethod(clazz, methodName);
            if (meth != null) {
                comparator = new Comparator<AbstractDB>() {
                    @Override
                    public int compare(final AbstractDB addFirst, final AbstractDB adbSecond) {
                        int result;
                        try {
                            Object valueOne = meth.invoke(addFirst, (Object[]) null);
                            Object valueTwo = meth.invoke(adbSecond, (Object[]) null);
                            return AdbComparator.compareValues(valueOne, valueTwo);
                        } catch (Exception exc) {
                            result = -1;
							logger.error(exc.getMessage(), exc);
                        }
                        return result;
                    }
                };
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
        return comparator;
    }

    @SuppressWarnings({
            "unchecked", "rawtypes" })
    public static int compareValues(final Object valueOne, final Object valueTwo) throws Exception {
        if (valueOne == null) {
            return -1;
        } else if (valueTwo == null) {
            return 1;
        }
        if (valueOne instanceof Comparable) {
            return ((Comparable) valueOne).compareTo(valueTwo);
        }
        return valueOne.toString().compareTo(valueTwo.toString());
    }

}
