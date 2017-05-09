package com.papao.books.ui.util;

import com.papao.books.model.AbstractMongoDB;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.*;

public final class ObjectUtil {

    private static Logger logger = Logger.getLogger(ObjectUtil.class);

    private ObjectUtil() {
    }

    /**
     * @param clazz      o clasa
     * @param methodName numele metodei
     * @return metoda sau arunca exceptie daca nu o gaseste
     * @throws NoSuchMethodException daca nu exista o metoda cu numele specificat, FARA argumente
     *                               <p>
     *                               Atentie. Metoda asta e capabila sa returneze doar metode fara paramteri. Pt metode cu un nr oarecare de parametri, apelati {@link #getMethod(Class, String, Class[])}
     *                               </p>
     */
    public static Method getMethod(final Class<?> clazz, final String methodName) throws NoSuchMethodException {
        return ObjectUtil.getMethod(clazz, methodName, (Class<?>[]) null);
    }

    /**
     * @param clazz      clasa
     * @param methodName numele metodei
     * @param paramTypes aray-ul de tipuri asociat parametrilor metodei
     * @return {@link Method}
     * @throws NoSuchMethodException se arunca o exceptie daca nu exista metoda specificata
     */
    public static Method getMethod(final Class<?> clazz, final String methodName, final Class<?>[] paramTypes) throws NoSuchMethodException {
        Method meth = null;
        try {
            meth = clazz.getMethod(methodName, paramTypes);
            if (meth == null) {
                throw new NoSuchMethodException("The specified object doesnt contain the [ " + methodName + "() ] method. \nPlease specify a valid method for class [" + clazz.getCanonicalName() + "]");
            }
        } catch (NoSuchMethodException exc) {
            logger.error(exc, exc);
            throw exc;
        }
        return meth;
    }

    public static List<AbstractMongoDB> sort(final Map<Long, ? extends AbstractMongoDB> input, final Comparator<AbstractMongoDB> comparator) {
        ArrayList<AbstractMongoDB> result = new ArrayList<AbstractMongoDB>();
        if (input == null) {
            return result;
        }

        result.addAll(input.values());

        if (comparator == null) {
            return result;
        }
        Collections.sort(result, comparator);
        return result;
    }

    public static Object copy(final Object orig) {
        return ObjectCloner.copy(orig);
    }

}
