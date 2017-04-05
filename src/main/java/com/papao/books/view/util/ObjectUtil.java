package com.papao.books.view.util;

import com.papao.books.model.AbstractDB;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

public final class ObjectUtil {

	private static Logger logger = Logger.getLogger(ObjectUtil.class);

	private ObjectUtil() {}

	/**
	 * @param clazz
	 *            o clasa
	 * @param methodName
	 *            numele metodei
	 * @return metoda sau arunca exceptie daca nu o gaseste
	 * @throws NoSuchMethodException
	 *             daca nu exista o metoda cu numele specificat, FARA argumente
	 *             <p>
	 *             Atentie. Metoda asta e capabila sa returneze doar metode fara paramteri. Pt metode cu un nr oarecare de parametri, apelati {@link #getMethod(Class, String, Class[])}
	 *             </p>
	 */
	public static Method getMethod(final Class<?> clazz, final String methodName) throws NoSuchMethodException {
		return ObjectUtil.getMethod(clazz, methodName, (Class<?>[]) null);
	}

	/**
	 * @param clazz
	 *            clasa
	 * @param methodName
	 *            numele metodei
	 * @param paramTypes
	 *            aray-ul de tipuri asociat parametrilor metodei
	 * @return {@link Method}
	 * @throws NoSuchMethodException
	 *             se arunca o exceptie daca nu exista metoda specificata
	 */
	public static Method getMethod(final Class<?> clazz, final String methodName, final Class<?>[] paramTypes) throws NoSuchMethodException {
		Method meth = null;
		try {
			meth = clazz.getMethod(methodName, paramTypes);
			if (meth == null) {
				throw new NoSuchMethodException("The specified object doesnt contain the [ " + methodName + "() ] method. \nPlease specify a valid method for class [" + clazz.getCanonicalName() + "]");
			}
		}
		catch (NoSuchMethodException exc) {
			logger.error(exc, exc);
			throw exc;
		}
		return meth;
	}

	public static List<AbstractDB> sort(final Map<Long, ? extends AbstractDB> input, final Comparator<AbstractDB> comparator) {
		ArrayList<AbstractDB> result = new ArrayList<AbstractDB>();
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

	/**
	 * Utility for making deep copies (vs. clone()'s shallow copies) of objects. Objects are first serialized and then deserialized. Error checking is fairly minimal in this implementation. If an
	 * object is encountered that cannot be serialized (or that references an object that cannot be serialized) an error is printed to System.err and null is returned. Depending on your specific
	 * application, it might make more sense to have copy(...) re-throw the exception. A later version of this class includes some minor optimizations. with respect for the source :
	 * http://javatechniques.com/blog/faster-deep-copies-of-java-objects/ Returns a copy of the object, or null if the object cannot be serialized.
	 */
	public static Object copy(final Object orig) {
		Object obj = null;
		ByteArrayOutputStream bos = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		try {
			/**
			 * Write the object out to a byte array
			 */
			bos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(bos);
			out.writeObject(orig);
			out.close();

			/**
			 * Make an input stream from the byte array and read a copy of the object back in.
			 */
			in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
			obj = in.readObject();
		}
		catch (IOException e) {
			logger.error(e, e);
		}
		catch (ClassNotFoundException cnfe) {
			logger.error(cnfe, cnfe);
		}
		finally {
			if (bos != null) {
				try {
					bos.close();
				}
				catch (IOException exc) {
					logger.error(exc, exc);
				}
			}
			if (out != null) {
				try {
					out.close();
				}
				catch (IOException exc) {
					logger.error(exc, exc);
				}
			}
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException exc) {
					logger.error(exc, exc);
				}
			}
		}
		return obj;
	}

}
