package com.papao.books.export.html;

import com.papao.books.view.auth.EncodeLive;
import com.papao.books.view.util.FilterUtil;
import org.eclipse.swt.SWT;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public final class ExportHtmlPrefs {

    private ExportHtmlPrefs() {}

    /**
     * preferences keys
     */
    public final static String HTML_EXPORT_PATH = "html.export.path";
    public final static String HTML_EXPORT_PATH_DEFAULT = EncodeLive.getReportsDir();

    public final static String HTML_EXPORT_PATH_AUTO = "html.export.path.auto";
    public final static boolean HTML_EXPORT_PATH_AUTO_DEFAULT = true;

    public final static String HTML_FONT_NAME = "html.font.name";
    public final static String HTML_FONT_NAME_DEFAULT = "timesi.ttf";

    public final static String HTML_FONT_NAME_USER = "html.font.name.user";
    public final static String HTML_FONT_NAME_USER_DEFAULT = "Times New Roman";

    public final static String HTML_FONT_SIZE = "html.font.size";
    public final static int HTML_FONT_SIZE_DEFAULT = 10;

    public final static String HTML_FONT_STYLE = "html.font.style";
    public final static int HTML_FONT_STYLE_DEFAULT = SWT.NORMAL;

    public final static String HTML_IS_USING_TITLE = "html.is.using.title";
    public final static boolean HTML_IS_USING_TITLE_DEFAULT = true;

    public final static String HTML_IS_USING_NR_CRT = "html.is.using.nr.crt";
    public final static boolean HTML_IS_USING_NR_CRT_DEFAULT = true;

    public final static String HTML_IS_SHOWING_HEADER = "html.is.showing.header";
    public final static boolean HTML_IS_SHOWING_HEADER_DEFAULT = true;

    public final static String HTML_IS_USING_GRAY_EFFECT = "html.is.using.gray.effect";
    public final static boolean HTML_IS_USING_GRAY_EFFECT_DEFAULT = false;

    private final static Preferences prefs = FilterUtil.getNodeExportHtmlCurrentUser();

    /**
     * static methods for accessing private filter prefs
     */

    public static void put(final String key, final String value) throws IllegalArgumentException {
        FilterUtil.put(key, value, ExportHtmlPrefs.prefs);
    }

    public static void putBoolean(final String key, final boolean value) throws IllegalArgumentException {
        FilterUtil.putBoolean(key, value, ExportHtmlPrefs.prefs);
    }

    public static void putByteArray(final String key, final byte[] value) throws IllegalArgumentException {
        FilterUtil.putByteArray(key, value, ExportHtmlPrefs.prefs);
    }

    public static void putDouble(final String key, final double value) throws IllegalArgumentException {
        FilterUtil.putDouble(key, value, ExportHtmlPrefs.prefs);
    }

    public static void putFloat(final String key, final float value) throws IllegalArgumentException {
        FilterUtil.putFloat(key, value, ExportHtmlPrefs.prefs);
    }

    public static void putInt(final String key, final int value) throws IllegalArgumentException {
        FilterUtil.putInt(key, value, ExportHtmlPrefs.prefs);
    }

    public static void putLong(final String key, final long value) throws IllegalArgumentException {
        FilterUtil.putLong(key, value, ExportHtmlPrefs.prefs);
    }

    public static void flush() throws BackingStoreException {
        FilterUtil.flush(ExportHtmlPrefs.prefs);
    }

    public static void reset() {
        ExportHtmlPrefs.put(ExportHtmlPrefs.HTML_EXPORT_PATH, ExportHtmlPrefs.HTML_EXPORT_PATH_DEFAULT);
        ExportHtmlPrefs.put(ExportHtmlPrefs.HTML_FONT_NAME, ExportHtmlPrefs.HTML_FONT_NAME_DEFAULT);
        ExportHtmlPrefs.put(ExportHtmlPrefs.HTML_FONT_NAME_USER, ExportHtmlPrefs.HTML_FONT_NAME_USER_DEFAULT);
        ExportHtmlPrefs.putInt(ExportHtmlPrefs.HTML_FONT_SIZE, ExportHtmlPrefs.HTML_FONT_SIZE_DEFAULT);
        ExportHtmlPrefs.putInt(ExportHtmlPrefs.HTML_FONT_STYLE, ExportHtmlPrefs.HTML_FONT_STYLE_DEFAULT);
        ExportHtmlPrefs.putBoolean(ExportHtmlPrefs.HTML_IS_SHOWING_HEADER, ExportHtmlPrefs.HTML_IS_SHOWING_HEADER_DEFAULT);
        ExportHtmlPrefs.putBoolean(ExportHtmlPrefs.HTML_IS_USING_GRAY_EFFECT, ExportHtmlPrefs.HTML_IS_USING_GRAY_EFFECT_DEFAULT);
        ExportHtmlPrefs.putBoolean(ExportHtmlPrefs.HTML_IS_USING_NR_CRT, ExportHtmlPrefs.HTML_IS_USING_NR_CRT_DEFAULT);
        ExportHtmlPrefs.putBoolean(ExportHtmlPrefs.HTML_IS_USING_TITLE, ExportHtmlPrefs.HTML_IS_USING_TITLE_DEFAULT);
        ExportHtmlPrefs.putBoolean(ExportHtmlPrefs.HTML_EXPORT_PATH_AUTO, ExportHtmlPrefs.HTML_EXPORT_PATH_AUTO_DEFAULT);
    }

    /**
     * convenience props accesors
     */

    public static String getExportPath() {
        if (ExportHtmlPrefs.isUsingAutoExportPath()) {
            return EncodeLive.getReportsDir();
        }
        String path = ExportHtmlPrefs.prefs.get(ExportHtmlPrefs.HTML_EXPORT_PATH, ExportHtmlPrefs.HTML_EXPORT_PATH_DEFAULT);
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        File f = new File(path);
        if (!f.exists() || !f.isDirectory()) {
            return ExportHtmlPrefs.HTML_EXPORT_PATH_DEFAULT;
        }
        return path;
    }

    public static boolean isUsingAutoExportPath() {
        return ExportHtmlPrefs.prefs.getBoolean(ExportHtmlPrefs.HTML_EXPORT_PATH_AUTO, ExportHtmlPrefs.HTML_EXPORT_PATH_AUTO_DEFAULT);
    }

    public static String getFontName() {
        return ExportHtmlPrefs.prefs.get(ExportHtmlPrefs.HTML_FONT_NAME, ExportHtmlPrefs.HTML_FONT_NAME_DEFAULT);
    }

    public static String getFontNameUser() {
        return ExportHtmlPrefs.prefs.get(ExportHtmlPrefs.HTML_FONT_NAME_USER, ExportHtmlPrefs.HTML_FONT_NAME_USER_DEFAULT);
    }

    public static int getFontStyle() {
        return ExportHtmlPrefs.prefs.getInt(ExportHtmlPrefs.HTML_FONT_STYLE, ExportHtmlPrefs.HTML_FONT_STYLE_DEFAULT);
    }

    public static int getFontSize() {
        return ExportHtmlPrefs.prefs.getInt(ExportHtmlPrefs.HTML_FONT_SIZE, ExportHtmlPrefs.HTML_FONT_SIZE_DEFAULT);
    }

    public static boolean isUsingTitle() {
        return ExportHtmlPrefs.prefs.getBoolean(ExportHtmlPrefs.HTML_IS_USING_TITLE, ExportHtmlPrefs.HTML_IS_USING_TITLE_DEFAULT);
    }

    public static boolean isUsingNrCrt() {
        return ExportHtmlPrefs.prefs.getBoolean(ExportHtmlPrefs.HTML_IS_USING_NR_CRT, ExportHtmlPrefs.HTML_IS_USING_NR_CRT_DEFAULT);
    }

    public static boolean isShowingHeader() {
        return ExportHtmlPrefs.prefs.getBoolean(ExportHtmlPrefs.HTML_IS_SHOWING_HEADER, ExportHtmlPrefs.HTML_IS_SHOWING_HEADER_DEFAULT);
    }

    public static boolean isUsingGrayEffect() {
        return ExportHtmlPrefs.prefs.getBoolean(ExportHtmlPrefs.HTML_IS_USING_GRAY_EFFECT, ExportHtmlPrefs.HTML_IS_USING_GRAY_EFFECT_DEFAULT);
    }
}
