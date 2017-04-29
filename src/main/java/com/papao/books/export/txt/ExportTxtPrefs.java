package com.papao.books.export.txt;

import com.papao.books.view.auth.EncodeLive;
import com.papao.books.view.util.FilterUtil;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public final class ExportTxtPrefs {

    private ExportTxtPrefs() {}

    public final static String TXT_EXPORT_PATH = "txt.export.path";
    public final static String TXT_EXPORT_PATH_DEFAULT = EncodeLive.getReportsDir();

    public final static String TXT_EXPORT_PATH_AUTO = "txt.export.path.auto";
    public final static boolean TXT_EXPORT_PATH_AUTO_DEFAULT = true;

    public final static String TXT_IS_SHOWING_NR_CRT = "txt.is.showing.nr.crt";
    public final static boolean TXT_IS_SHOWING_NR_CRT_DEFAULT = true;

    public final static String TXT_TABLE_HAS_BORDER = "txt.table.has.border";
    public final static boolean TXT_TABLE_HAS_BORDER_DEFAULT = true;

    public final static String TXT_IS_USING_TITLE = "txt.is.using.title";
    public final static boolean TXT_IS_USING_TITLE_DEFAULT = false;

    private final static Preferences prefs = FilterUtil.getNodeExportTxtCurrentUser();

    /**
     * static methods for accessing private filter prefs
     */

    public static void put(final String key, final String value) throws IllegalArgumentException {
        FilterUtil.put(key, value, ExportTxtPrefs.prefs);
    }

    public static void putBoolean(final String key, final boolean value) throws IllegalArgumentException {
        FilterUtil.putBoolean(key, value, ExportTxtPrefs.prefs);
    }

    public static void putByteArray(final String key, final byte[] value) throws IllegalArgumentException {
        FilterUtil.putByteArray(key, value, ExportTxtPrefs.prefs);
    }

    public static void putDouble(final String key, final double value) throws IllegalArgumentException {
        FilterUtil.putDouble(key, value, ExportTxtPrefs.prefs);
    }

    public static void putFloat(final String key, final float value) throws IllegalArgumentException {
        FilterUtil.putFloat(key, value, ExportTxtPrefs.prefs);
    }

    public static void putInt(final String key, final int value) throws IllegalArgumentException {
        FilterUtil.putInt(key, value, ExportTxtPrefs.prefs);
    }

    public static void putLong(final String key, final long value) throws IllegalArgumentException {
        FilterUtil.putLong(key, value, ExportTxtPrefs.prefs);
    }

    public static void flush() throws BackingStoreException {
        FilterUtil.flush(ExportTxtPrefs.prefs);
    }

    public static void reset() {
        ExportTxtPrefs.put(ExportTxtPrefs.TXT_EXPORT_PATH, ExportTxtPrefs.TXT_EXPORT_PATH_DEFAULT);
        ExportTxtPrefs.putBoolean(ExportTxtPrefs.TXT_EXPORT_PATH_AUTO, ExportTxtPrefs.TXT_EXPORT_PATH_AUTO_DEFAULT);
        ExportTxtPrefs.putBoolean(ExportTxtPrefs.TXT_IS_SHOWING_NR_CRT, ExportTxtPrefs.TXT_IS_SHOWING_NR_CRT_DEFAULT);
        ExportTxtPrefs.putBoolean(ExportTxtPrefs.TXT_IS_USING_TITLE, ExportTxtPrefs.TXT_IS_USING_TITLE_DEFAULT);
        ExportTxtPrefs.putBoolean(ExportTxtPrefs.TXT_TABLE_HAS_BORDER, ExportTxtPrefs.TXT_TABLE_HAS_BORDER_DEFAULT);
    }

    public static String getExportPath() {
        if (ExportTxtPrefs.isUsingAutoExportPath()) {
            return EncodeLive.getReportsDir();
        }
        String path = ExportTxtPrefs.prefs.get(ExportTxtPrefs.TXT_EXPORT_PATH, ExportTxtPrefs.TXT_EXPORT_PATH_DEFAULT);
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        File f = new File(path);
        if (!f.exists() || !f.isDirectory()) {
            return ExportTxtPrefs.TXT_EXPORT_PATH_DEFAULT;
        }
        return path;
    }

    public static boolean isUsingAutoExportPath() {
        return ExportTxtPrefs.prefs.getBoolean(ExportTxtPrefs.TXT_EXPORT_PATH_AUTO, ExportTxtPrefs.TXT_EXPORT_PATH_AUTO_DEFAULT);
    }

    public static boolean isShowingNrCrt() {
        return ExportTxtPrefs.prefs.getBoolean(ExportTxtPrefs.TXT_IS_SHOWING_NR_CRT, ExportTxtPrefs.TXT_IS_SHOWING_NR_CRT_DEFAULT);
    }

    public static boolean isHavingBorder() {
        return ExportTxtPrefs.prefs.getBoolean(ExportTxtPrefs.TXT_TABLE_HAS_BORDER, ExportTxtPrefs.TXT_TABLE_HAS_BORDER_DEFAULT);
    }

    public static boolean isUsingTitle() {
        return ExportTxtPrefs.prefs.getBoolean(ExportTxtPrefs.TXT_IS_USING_TITLE, ExportTxtPrefs.TXT_IS_USING_TITLE_DEFAULT);
    }

}
