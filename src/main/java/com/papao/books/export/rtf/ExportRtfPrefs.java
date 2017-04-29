package com.papao.books.export.rtf;

import com.papao.books.view.auth.EncodeLive;
import com.papao.books.view.util.FilterUtil;
import org.eclipse.swt.SWT;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public final class ExportRtfPrefs {

    private ExportRtfPrefs() {}

    /**
     * raw materials
     */

    public final static String PORTRAIT = "Normal (Portrait)";
    public final static String LANDSCAPE = "Panoramic (Landscape)";
    public final static String[] ORIENTATIONS = new String[] {
            ExportRtfPrefs.PORTRAIT, ExportRtfPrefs.LANDSCAPE };

    /**
     * preferences keys
     */
    public final static String RTF_EXPORT_PATH = "rtf.export.path";
    public final static String RTF_EXPORT_PATH_DEFAULT = EncodeLive.getReportsDir();

    public final static String RTF_EXPORT_PATH_AUTO = "rtf.export.path.auto";
    public final static boolean RTF_EXPORT_PATH_AUTO_DEFAULT = true;

    public final static String RTF_PAGE_ORIENTATION = "rtf.page.orientation";
    public final static String RTF_PAGE_ORIENTATION_DEFAULT = ExportRtfPrefs.PORTRAIT;

    public final static String RTF_PAGE_SIZE = "rtf.page.size";
    public final static String RTF_PAGE_SIZE_DEFAULT = "A4";

    public final static String RTF_FONT_NAME = "rtf.font.name";
    public final static String RTF_FONT_NAME_DEFAULT = "timesi.ttf";

    public final static String RTF_FONT_NAME_USER = "rtf.font.name.user";
    public final static String RTF_FONT_NAME_USER_DEFAULT = "Times New Roman";

    public final static String RTF_FONT_SIZE = "rtf.font.size";
    public final static int RTF_FONT_SIZE_DEFAULT = 10;

    public final static String RTF_FONT_STYLE = "rtf.font.style";
    public final static int RTF_FONT_STYLE_DEFAULT = SWT.NORMAL;

    public final static String RTF_IS_USING_TITLE = "rtf.is.using.title";
    public final static boolean RTF_IS_USING_TITLE_DEFAULT = true;

    public final static String RTF_IS_USING_NR_CRT = "rtf.is.using.nr.crt";
    public final static boolean RTF_IS_USING_NR_CRT_DEFAULT = true;

    public final static String RTF_IS_SHOWING_HEADER = "rtf.is.showing.header";
    public final static boolean RTF_IS_SHOWING_HEADER_DEFAULT = true;

    public final static String RTF_IS_USING_GRAY_EFFECT = "rtf.is.using.gray.effect";
    public final static boolean RTF_IS_USING_GRAY_EFFECT_DEFAULT = false;

    public final static String RTF_IS_USING_PAGE_NO = "rtf.is.using.page.no";
    public final static boolean RTF_IS_USING_PAGE_NO_DEFAULT = true;

    private final static Preferences prefs = FilterUtil.getNodeExportRtfCurrentUser();

    /**
     * static methods for accessing private filter prefs
     */

    public static void put(final String key, final String value) throws IllegalArgumentException {
        FilterUtil.put(key, value, ExportRtfPrefs.prefs);
    }

    public static void putBoolean(final String key, final boolean value) throws IllegalArgumentException {
        FilterUtil.putBoolean(key, value, ExportRtfPrefs.prefs);
    }

    public static void putByteArray(final String key, final byte[] value) throws IllegalArgumentException {
        FilterUtil.putByteArray(key, value, ExportRtfPrefs.prefs);
    }

    public static void putDouble(final String key, final double value) throws IllegalArgumentException {
        FilterUtil.putDouble(key, value, ExportRtfPrefs.prefs);
    }

    public static void putFloat(final String key, final float value) throws IllegalArgumentException {
        FilterUtil.putFloat(key, value, ExportRtfPrefs.prefs);
    }

    public static void putInt(final String key, final int value) throws IllegalArgumentException {
        FilterUtil.putInt(key, value, ExportRtfPrefs.prefs);
    }

    public static void putLong(final String key, final long value) throws IllegalArgumentException {
        FilterUtil.putLong(key, value, ExportRtfPrefs.prefs);
    }

    public static void flush() throws BackingStoreException {
        FilterUtil.flush(ExportRtfPrefs.prefs);
    }

    public static void reset() {
        ExportRtfPrefs.put(ExportRtfPrefs.RTF_EXPORT_PATH, ExportRtfPrefs.RTF_EXPORT_PATH_DEFAULT);
        ExportRtfPrefs.put(ExportRtfPrefs.RTF_FONT_NAME, ExportRtfPrefs.RTF_FONT_NAME_DEFAULT);
        ExportRtfPrefs.put(ExportRtfPrefs.RTF_FONT_NAME_USER, ExportRtfPrefs.RTF_FONT_NAME_USER_DEFAULT);
        ExportRtfPrefs.putInt(ExportRtfPrefs.RTF_FONT_SIZE, ExportRtfPrefs.RTF_FONT_SIZE_DEFAULT);
        ExportRtfPrefs.putInt(ExportRtfPrefs.RTF_FONT_STYLE, ExportRtfPrefs.RTF_FONT_STYLE_DEFAULT);
        ExportRtfPrefs.putBoolean(ExportRtfPrefs.RTF_IS_SHOWING_HEADER, ExportRtfPrefs.RTF_IS_SHOWING_HEADER_DEFAULT);
        ExportRtfPrefs.putBoolean(ExportRtfPrefs.RTF_IS_USING_GRAY_EFFECT, ExportRtfPrefs.RTF_IS_USING_GRAY_EFFECT_DEFAULT);
        ExportRtfPrefs.putBoolean(ExportRtfPrefs.RTF_IS_USING_NR_CRT, ExportRtfPrefs.RTF_IS_USING_NR_CRT_DEFAULT);
        ExportRtfPrefs.putBoolean(ExportRtfPrefs.RTF_IS_USING_PAGE_NO, ExportRtfPrefs.RTF_IS_USING_PAGE_NO_DEFAULT);
        ExportRtfPrefs.putBoolean(ExportRtfPrefs.RTF_IS_USING_TITLE, ExportRtfPrefs.RTF_IS_USING_TITLE_DEFAULT);
        ExportRtfPrefs.put(ExportRtfPrefs.RTF_PAGE_ORIENTATION, ExportRtfPrefs.RTF_PAGE_ORIENTATION_DEFAULT);
        ExportRtfPrefs.put(ExportRtfPrefs.RTF_PAGE_SIZE, ExportRtfPrefs.RTF_PAGE_SIZE_DEFAULT);
        ExportRtfPrefs.putBoolean(ExportRtfPrefs.RTF_EXPORT_PATH_AUTO, ExportRtfPrefs.RTF_EXPORT_PATH_AUTO_DEFAULT);
    }

    /**
     * convenience props accesors
     */

    public static String getExportPath() {
        if (ExportRtfPrefs.isUsingAutoExportPath()) {
            return EncodeLive.getReportsDir();
        }
        String path = ExportRtfPrefs.prefs.get(ExportRtfPrefs.RTF_EXPORT_PATH, ExportRtfPrefs.RTF_EXPORT_PATH_DEFAULT);
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        File f = new File(path);
        if (!f.exists() || !f.isDirectory()) {
            return ExportRtfPrefs.RTF_EXPORT_PATH_DEFAULT;
        }
        return path;
    }

    public static boolean isUsingAutoExportPath() {
        return ExportRtfPrefs.prefs.getBoolean(ExportRtfPrefs.RTF_EXPORT_PATH_AUTO, ExportRtfPrefs.RTF_EXPORT_PATH_AUTO_DEFAULT);
    }

    public static String getPageOrientation() {
        return ExportRtfPrefs.prefs.get(ExportRtfPrefs.RTF_PAGE_ORIENTATION, ExportRtfPrefs.RTF_PAGE_ORIENTATION_DEFAULT);
    }

    public static String getPageSize() {
        return ExportRtfPrefs.prefs.get(ExportRtfPrefs.RTF_PAGE_SIZE, ExportRtfPrefs.RTF_PAGE_SIZE_DEFAULT);
    }

    public static String getFontName() {
        return ExportRtfPrefs.prefs.get(ExportRtfPrefs.RTF_FONT_NAME, ExportRtfPrefs.RTF_FONT_NAME_DEFAULT);
    }

    public static String getFontNameUser() {
        return ExportRtfPrefs.prefs.get(ExportRtfPrefs.RTF_FONT_NAME_USER, ExportRtfPrefs.RTF_FONT_NAME_USER_DEFAULT);
    }

    public static int getFontStyle() {
        return ExportRtfPrefs.prefs.getInt(ExportRtfPrefs.RTF_FONT_STYLE, ExportRtfPrefs.RTF_FONT_STYLE_DEFAULT);
    }

    public static int getFontSize() {
        return ExportRtfPrefs.prefs.getInt(ExportRtfPrefs.RTF_FONT_SIZE, ExportRtfPrefs.RTF_FONT_SIZE_DEFAULT);
    }

    public static boolean isUsingTitle() {
        return ExportRtfPrefs.prefs.getBoolean(ExportRtfPrefs.RTF_IS_USING_TITLE, ExportRtfPrefs.RTF_IS_USING_TITLE_DEFAULT);
    }

    public static boolean isUsingNrCrt() {
        return ExportRtfPrefs.prefs.getBoolean(ExportRtfPrefs.RTF_IS_USING_NR_CRT, ExportRtfPrefs.RTF_IS_USING_NR_CRT_DEFAULT);
    }

    public static boolean isShowingHeader() {
        return ExportRtfPrefs.prefs.getBoolean(ExportRtfPrefs.RTF_IS_SHOWING_HEADER, ExportRtfPrefs.RTF_IS_SHOWING_HEADER_DEFAULT);
    }

    public static boolean isUsingGrayEffect() {
        return ExportRtfPrefs.prefs.getBoolean(ExportRtfPrefs.RTF_IS_USING_GRAY_EFFECT, ExportRtfPrefs.RTF_IS_USING_GRAY_EFFECT_DEFAULT);
    }

    public static boolean isUsingPageNo() {
        return ExportRtfPrefs.prefs.getBoolean(ExportRtfPrefs.RTF_IS_USING_PAGE_NO, ExportRtfPrefs.RTF_IS_USING_PAGE_NO_DEFAULT);
    }

}
