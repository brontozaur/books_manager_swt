package com.papao.books.export.xls;

import com.papao.books.view.auth.EncodeLive;
import com.papao.books.view.util.ConfigController;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public final class ExportXlsPrefs {

    private ExportXlsPrefs() {}

    public final static String XLS_EXPORT_PATH = "xls.export.path";
    public final static String XLS_EXPORT_PATH_DEFAULT = EncodeLive.getReportsDir();

    public final static String XLS_EXPORT_PATH_AUTO = "xls.export.path.auto";
    public final static boolean XLS_EXPORT_PATH_AUTO_DEFAULT = true;

    public final static String XLS_EXPORT_EXTENSION = "xls.export.ext";
    public final static String XLS_EXPORT_EXTENSION_DEFAULT = ExportXls.XLS_EXTENSION;

    public final static String XLS_AUTO_RESIZE_COLS = "xls.auto.resize.cols";
    public final static boolean XLS_AUTO_RESIZE_COLS_DEFAULT = true;

    public final static String XLS_IS_SHOWING_HEADER = "xls.is.showing.header";
    public final static boolean XLS_IS_SHOWING_HEADER_DEFAULT = true;

    private final static Preferences prefs = ConfigController.getNodeExportExcelCurrentUser();

    /**
     * static methods for accessing private filter prefs
     */

    public static void put(final String key, final String value) throws IllegalArgumentException {
        ConfigController.put(key, value, ExportXlsPrefs.prefs);
    }

    public static void putBoolean(final String key, final boolean value) throws IllegalArgumentException {
        ConfigController.putBoolean(key, value, ExportXlsPrefs.prefs);
    }

    public static void putByteArray(final String key, final byte[] value) throws IllegalArgumentException {
        ConfigController.putByteArray(key, value, ExportXlsPrefs.prefs);
    }

    public static void putDouble(final String key, final double value) throws IllegalArgumentException {
        ConfigController.putDouble(key, value, ExportXlsPrefs.prefs);
    }

    public static void putFloat(final String key, final float value) throws IllegalArgumentException {
        ConfigController.putFloat(key, value, ExportXlsPrefs.prefs);
    }

    public static void putInt(final String key, final int value) throws IllegalArgumentException {
        ConfigController.putInt(key, value, ExportXlsPrefs.prefs);
    }

    public static void putLong(final String key, final long value) throws IllegalArgumentException {
        ConfigController.putLong(key, value, ExportXlsPrefs.prefs);
    }

    public static void flush() throws BackingStoreException {
        ConfigController.flush(ExportXlsPrefs.prefs);
    }

    public static void reset() {
        ExportXlsPrefs.put(ExportXlsPrefs.XLS_EXPORT_PATH, ExportXlsPrefs.XLS_EXPORT_PATH_DEFAULT);
        ExportXlsPrefs.put(ExportXlsPrefs.XLS_EXPORT_EXTENSION, ExportXlsPrefs.XLS_EXPORT_EXTENSION_DEFAULT);
        ExportXlsPrefs.putBoolean(ExportXlsPrefs.XLS_AUTO_RESIZE_COLS, ExportXlsPrefs.XLS_AUTO_RESIZE_COLS_DEFAULT);
        ExportXlsPrefs.putBoolean(ExportXlsPrefs.XLS_IS_SHOWING_HEADER, ExportXlsPrefs.XLS_IS_SHOWING_HEADER_DEFAULT);
        ExportXlsPrefs.putBoolean(ExportXlsPrefs.XLS_EXPORT_PATH_AUTO, ExportXlsPrefs.XLS_EXPORT_PATH_AUTO_DEFAULT);
    }

    public static String getExportPath() {
        if (ExportXlsPrefs.isUsingAutoExportPath()) {
            return EncodeLive.getReportsDir();
        }
        String path = ExportXlsPrefs.prefs.get(ExportXlsPrefs.XLS_EXPORT_PATH, ExportXlsPrefs.XLS_EXPORT_PATH_DEFAULT);
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        File f = new File(path);
        if (!f.exists() || !f.isDirectory()) {
            return ExportXlsPrefs.XLS_EXPORT_PATH_DEFAULT;
        }
        return path;
    }

    public static boolean isUsingAutoExportPath() {
        return ExportXlsPrefs.prefs.getBoolean(ExportXlsPrefs.XLS_EXPORT_PATH_AUTO, ExportXlsPrefs.XLS_EXPORT_PATH_AUTO_DEFAULT);
    }

    public static String getExtension() {
        return ExportXlsPrefs.prefs.get(ExportXlsPrefs.XLS_EXPORT_EXTENSION, ExportXlsPrefs.XLS_EXPORT_EXTENSION_DEFAULT);
    }

    public static boolean isShowingHeader() {
        return ExportXlsPrefs.prefs.getBoolean(ExportXlsPrefs.XLS_IS_SHOWING_HEADER, ExportXlsPrefs.XLS_IS_SHOWING_HEADER_DEFAULT);
    }

    public static boolean isAutoResizeCols() {
        return ExportXlsPrefs.prefs.getBoolean(ExportXlsPrefs.XLS_AUTO_RESIZE_COLS, ExportXlsPrefs.XLS_AUTO_RESIZE_COLS_DEFAULT);
    }

}
