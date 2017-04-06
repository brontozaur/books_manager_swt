package com.papao.books.view.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

public final class StringUtil {

	private static Logger logger = Logger.getLogger(StringUtil.class);

	private StringUtil() {}

	public static String capitalizeCharAtIdx(final String str, final int idx) {
		if (StringUtils.isEmpty(str) || (str.length() < idx)) {
			return str;
		}
		return str.replaceFirst(String.valueOf(str.charAt(idx)), (String.valueOf(str.charAt(idx))).toUpperCase());
	}

	public static String decapitalizeCharAtIdx(final String str, final int idx) {
		if (StringUtils.isEmpty(str) || (str.length() < idx)) {
			return str;
		}
		return str.replaceFirst(String.valueOf(str.charAt(idx)), (String.valueOf(str.charAt(idx))).toLowerCase());
	}

	/**
	 * @return widget suggested size, so that widget will still pe capable to render all the associated text
	 */
	public static String[] splitStrByDelimAndLength(final String strParam, final String delim, final int blockSize) {
		String[] result = null;
		TreeMap<Integer, String> mapStr = new TreeMap<Integer, String>();
		try {
			StringTokenizer t = new StringTokenizer(strParam, delim, true);
			StringBuffer buff = new StringBuffer();
			int k = 0;
			while (t.hasMoreTokens()) {
				String next = t.nextToken();
				buff.append(next);
				if (buff.length() > blockSize) {
					mapStr.put(Integer.valueOf(k++), buff.toString());
					buff = new StringBuffer();
				} else if (strParam.endsWith(next)) {
					mapStr.put(Integer.valueOf(k++), buff.toString());
					buff = new StringBuffer();
				}
				result = new String[mapStr.size()];
				int i = 0;
				for (Iterator<String> iter = mapStr.values().iterator(); iter.hasNext();) {
					result[i++] = iter.next();
				}
			}
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
			return null;
		}
		return result;
	}

	public static boolean compareStrings(	final String sirCautat,
											final String domeniuCautare,
											final boolean contains,
											final boolean startsWith,
											final boolean endsWith) {
		if (StringUtils.isEmpty(sirCautat)) {
			return true;
		}
		String str1 = sirCautat;
		String str2 = domeniuCautare;
			str1 = sirCautat.toUpperCase();
			str2 = domeniuCautare.toUpperCase();

		if (startsWith) {
			return str2.startsWith(str1);
		} else if (endsWith) {
			return str2.endsWith(str1);
		} else if (contains) {
			return str2.indexOf(str1) != -1;
		}
		// comportamentul default, daca nu s-a bifat nimic
		return str2.indexOf(str1) != -1;
	}

	public static boolean compareStrings(final String sirCautat, final String domeniuCautare) {
		return StringUtil.compareStrings(sirCautat, domeniuCautare, false, false, false);
	}

	public static boolean compareStrings(final String sirCautat[], final String domeniuCautare) {
		if (sirCautat == null) {
			return false;
		}
		for (int i = 0; i < sirCautat.length; i++) {
			if (StringUtil.compareStrings(sirCautat[i], domeniuCautare, false, false, false)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns an array of strings, each of which is a substring of string formed by splitting it on boundaries formed by the <CODE>token</CODE>.
	 * 
	 * @param text
	 *            the text to split
	 * @param token
	 *            delimiters of string parts
	 * @return an array of string
	 */
	public static String[] splitString(final String text, final String token) {
		int fromHere = 0;
		int toHere;
		Vector<String> temp = new Vector<String>();
		while (text.indexOf(token, fromHere) != -1) {
			toHere = text.indexOf(token, fromHere);
			String z = text.substring(fromHere, toHere);
			temp.addElement(z);
			fromHere = toHere + token.length();
		}
		toHere = text.length();
		temp.addElement(text.substring(fromHere, toHere));
		if (temp.firstElement().length() == 0) {
			return new String[0];
		}
		String[] splitted_string = new String[temp.size()];
		temp.copyInto(splitted_string);
		return splitted_string;
	}

	public static String replaceZerosAfterDot(final String param) {
		if (param == null) {
			return null;
		}
		String str = param;
		if (str.indexOf('.') == -1) {
			return param;
		}
		if (str.endsWith(".")) {
			return str.substring(0, str.length() - 1);
		}
		while (str.endsWith("0")) {
			str = str.substring(0, str.length() - 1);
		}
		if (str.endsWith(".")) {
			return str.substring(0, str.length() - 1);
		}
		return str;
	}

	/**
	 * @param c
	 *            un caracter
	 * @return codul ASCII, prin conversia lui la int, care e automata
	 */
	public static int getAsciiCode(final char c) {
		return c;
	}

	public static String getShortedStr(final String str, final int maxChars) {
		if (str == null) {
			return "";
		}
		if (str.length() <= maxChars) {
			return str;
		}
		return str.substring(0, maxChars).concat(".");
	}

}
