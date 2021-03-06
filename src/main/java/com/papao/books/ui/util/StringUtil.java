package com.papao.books.ui.util;

import com.papao.books.ui.auth.EncodeLive;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

public final class StringUtil {

    private static Logger logger = Logger.getLogger(StringUtil.class);

    private static final Collator ROMANIAN_COLLATOR = Collator.getInstance(EncodeLive.ROMANIAN_LOCALE);

    private StringUtil() {
    }

    public static int romanianCompare(String a, String b) {
        return ROMANIAN_COLLATOR.compare(StringUtils.defaultString(a, ""), StringUtils.defaultString(b, ""));
    }

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
                    mapStr.put(k++, buff.toString());
                    buff = new StringBuffer();
                } else if (strParam.endsWith(next)) {
                    mapStr.put(k++, buff.toString());
                    buff = new StringBuffer();
                }
                result = new String[mapStr.size()];
                int i = 0;
                for (String s : mapStr.values()) {
                    result[i++] = s;
                }
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return null;
        }
        return result;
    }

    public static boolean compareStrings(final String sirCautat,
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
            return str2.contains(str1);
        }
        // comportamentul default, daca nu s-a bifat nimic
        return str2.contains(str1);
    }

    public static boolean compareStrings(final String sirCautat, final String domeniuCautare) {
        return StringUtil.compareStrings(sirCautat, domeniuCautare, false, false, false);
    }

    public static boolean compareStrings(final String sirCautat[], final String domeniuCautare) {
        if (sirCautat == null) {
            return false;
        }
        for (String aSirCautat : sirCautat) {
            if (StringUtil.compareStrings(aSirCautat, domeniuCautare, false, false, false)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an array of strings, each of which is a substring of string formed by splitting it on boundaries formed by the <CODE>token</CODE>.
     *
     * @param text  the text to split
     * @param token delimiters of string parts
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
     * @param c un caracter
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

    public static String decodeUrl(String url) {
        try {
            return URLDecoder.decode(url, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
            return url;
        }
    }
}
