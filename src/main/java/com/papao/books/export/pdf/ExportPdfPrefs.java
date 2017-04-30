package com.papao.books.export.pdf;

import com.papao.books.view.auth.EncodeLive;
import com.papao.books.view.util.ConfigController;
import org.eclipse.swt.SWT;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public final class ExportPdfPrefs {

    private ExportPdfPrefs() {}

    /**
     * raw materials
     */

    public final static String[] VERSIONS = new String[] {
            "1.2 (Acrobat 3.0+)", "1.3 (Acrobat 4.0+)", "1.4 (Acrobat 5.0+)", "1.5 (Acrobat 6.0+)", "1.6 (Acrobat 7.0+)", "1.7 (Acrobat 8.0+)" };
    public final static String PORTRAIT = "Normal (Portrait)";
    public final static String LANDSCAPE = "Panoramic (Landscape)";
    public final static String[] ORIENTATIONS = new String[] {
            ExportPdfPrefs.PORTRAIT, ExportPdfPrefs.LANDSCAPE };

    /**
     * preferences keys
     */
    public final static String PDF_EXPORT_PATH = "pdf.export.path";
    public final static String PDF_EXPORT_PATH_DEFAULT = EncodeLive.getReportsDir();

    public final static String PDF_EXPORT_PATH_AUTO = "pdf.export.path.auto";
    public final static boolean PDF_EXPORT_PATH_AUTO_DEFAULT = true;

    public final static String PDF_PAGE_ORIENTATION = "pdf.page.orientation";
    public final static String PDF_PAGE_ORIENTATION_DEFAULT = ExportPdfPrefs.PORTRAIT;

    public final static String PDF_PAGE_SIZE = "pdf.page.size";
    public final static String PDF_PAGE_SIZE_DEFAULT = "A4";

    public final static String PDF_VERSION = "pdf.doc.version";
    public final static String PDF_VERSION_DEFAULT = ExportPdfPrefs.VERSIONS[0];

    public final static String PDF_FONT_NAME = "pdf.font.name";
    public final static String PDF_FONT_NAME_DEFAULT = "timesi.ttf";

    public final static String PDF_FONT_NAME_USER = "pdf.font.name.user";
    public final static String PDF_FONT_NAME_USER_DEFAULT = "Times New Roman";

    public final static String PDF_FONT_SIZE = "pdf.font.size";
    public final static int PDF_FONT_SIZE_DEFAULT = 10;

    public final static String PDF_FONT_STYLE = "pdf.font.style";
    public final static int PDF_FONT_STYLE_DEFAULT = SWT.NORMAL;

    public final static String PDF_COMPRESSION = "pdf.compression";
    public final static int PDF_COMPRESSION_DEFAULT = 9;

    public final static String PDF_IS_USING_TITLE = "pdf.is.using.title";
    public final static boolean PDF_IS_USING_TITLE_DEFAULT = true;

    public final static String PDF_IS_USING_NR_CRT = "pdf.is.using.nr.crt";
    public final static boolean PDF_IS_USING_NR_CRT_DEFAULT = true;

    public final static String PDF_IS_SHOWING_HEADER = "pdf.is.showing.header";
    public final static boolean PDF_IS_SHOWING_HEADER_DEFAULT = true;

    public final static String PDF_IS_USING_GRAY_EFFECT = "pdf.is.using.gray.effect";
    public final static boolean PDF_IS_USING_GRAY_EFFECT_DEFAULT = false;

    public final static String PDF_IS_USING_PAGE_NO = "pdf.is.using.page.no";
    public final static boolean PDF_IS_USING_PAGE_NO_DEFAULT = true;

    private final static Preferences prefs = ConfigController.getNodeExportPdfCurrentUser();

    /**
     * static methods for accessing private filter prefs
     */

    public static void put(final String key, final String value) throws IllegalArgumentException {
        ConfigController.put(key, value, ExportPdfPrefs.prefs);
    }

    public static void putBoolean(final String key, final boolean value) throws IllegalArgumentException {
        ConfigController.putBoolean(key, value, ExportPdfPrefs.prefs);
    }

    public static void putByteArray(final String key, final byte[] value) throws IllegalArgumentException {
        ConfigController.putByteArray(key, value, ExportPdfPrefs.prefs);
    }

    public static void putDouble(final String key, final double value) throws IllegalArgumentException {
        ConfigController.putDouble(key, value, ExportPdfPrefs.prefs);
    }

    public static void putFloat(final String key, final float value) throws IllegalArgumentException {
        ConfigController.putFloat(key, value, ExportPdfPrefs.prefs);
    }

    public static void putInt(final String key, final int value) throws IllegalArgumentException {
        ConfigController.putInt(key, value, ExportPdfPrefs.prefs);
    }

    public static void putLong(final String key, final long value) throws IllegalArgumentException {
        ConfigController.putLong(key, value, ExportPdfPrefs.prefs);
    }

    public static void flush() throws BackingStoreException {
        ConfigController.flush(ExportPdfPrefs.prefs);
    }

    public static void reset() {
        ExportPdfPrefs.putInt(ExportPdfPrefs.PDF_COMPRESSION, ExportPdfPrefs.PDF_COMPRESSION_DEFAULT);
        ExportPdfPrefs.put(ExportPdfPrefs.PDF_EXPORT_PATH, ExportPdfPrefs.PDF_EXPORT_PATH_DEFAULT);
        ExportPdfPrefs.put(ExportPdfPrefs.PDF_FONT_NAME, ExportPdfPrefs.PDF_FONT_NAME_DEFAULT);
        ExportPdfPrefs.put(ExportPdfPrefs.PDF_FONT_NAME_USER, ExportPdfPrefs.PDF_FONT_NAME_USER_DEFAULT);
        ExportPdfPrefs.putInt(ExportPdfPrefs.PDF_FONT_SIZE, ExportPdfPrefs.PDF_FONT_SIZE_DEFAULT);
        ExportPdfPrefs.putInt(ExportPdfPrefs.PDF_FONT_STYLE, ExportPdfPrefs.PDF_FONT_STYLE_DEFAULT);
        ExportPdfPrefs.putBoolean(ExportPdfPrefs.PDF_IS_SHOWING_HEADER, ExportPdfPrefs.PDF_IS_SHOWING_HEADER_DEFAULT);
        ExportPdfPrefs.putBoolean(ExportPdfPrefs.PDF_IS_USING_GRAY_EFFECT, ExportPdfPrefs.PDF_IS_USING_GRAY_EFFECT_DEFAULT);
        ExportPdfPrefs.putBoolean(ExportPdfPrefs.PDF_IS_USING_NR_CRT, ExportPdfPrefs.PDF_IS_USING_NR_CRT_DEFAULT);
        ExportPdfPrefs.putBoolean(ExportPdfPrefs.PDF_IS_USING_PAGE_NO, ExportPdfPrefs.PDF_IS_USING_PAGE_NO_DEFAULT);
        ExportPdfPrefs.putBoolean(ExportPdfPrefs.PDF_IS_USING_TITLE, ExportPdfPrefs.PDF_IS_USING_TITLE_DEFAULT);
        ExportPdfPrefs.put(ExportPdfPrefs.PDF_PAGE_ORIENTATION, ExportPdfPrefs.PDF_PAGE_ORIENTATION_DEFAULT);
        ExportPdfPrefs.put(ExportPdfPrefs.PDF_PAGE_SIZE, ExportPdfPrefs.PDF_PAGE_SIZE_DEFAULT);
        ExportPdfPrefs.put(ExportPdfPrefs.PDF_VERSION, ExportPdfPrefs.PDF_VERSION_DEFAULT);
        ExportPdfPrefs.putBoolean(ExportPdfPrefs.PDF_EXPORT_PATH_AUTO, ExportPdfPrefs.PDF_EXPORT_PATH_AUTO_DEFAULT);
    }

    /**
     * convenience props accesors
     */

    public static String getExportPath() {
        if (ExportPdfPrefs.isUsingAutoExportPath()) {
            return EncodeLive.getReportsDir();
        }
        String path = ExportPdfPrefs.prefs.get(ExportPdfPrefs.PDF_EXPORT_PATH, ExportPdfPrefs.PDF_EXPORT_PATH_DEFAULT);
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        File f = new File(path);
        if (!f.exists() || !f.isDirectory()) {
            return ExportPdfPrefs.PDF_EXPORT_PATH_DEFAULT;
        }
        return path;
    }

    public static boolean isUsingAutoExportPath() {
        return ExportPdfPrefs.prefs.getBoolean(ExportPdfPrefs.PDF_EXPORT_PATH_AUTO, ExportPdfPrefs.PDF_EXPORT_PATH_AUTO_DEFAULT);
    }

    public static String getPageOrientation() {
        return ExportPdfPrefs.prefs.get(ExportPdfPrefs.PDF_PAGE_ORIENTATION, ExportPdfPrefs.PDF_PAGE_ORIENTATION_DEFAULT);
    }

    public static String getPageSize() {
        return ExportPdfPrefs.prefs.get(ExportPdfPrefs.PDF_PAGE_SIZE, ExportPdfPrefs.PDF_PAGE_SIZE_DEFAULT);
    }

    public static String getDocVersion() {
        return ExportPdfPrefs.prefs.get(ExportPdfPrefs.PDF_VERSION, ExportPdfPrefs.PDF_VERSION_DEFAULT);
    }

    public static String getFontName() {
        return ExportPdfPrefs.prefs.get(ExportPdfPrefs.PDF_FONT_NAME, ExportPdfPrefs.PDF_FONT_NAME_DEFAULT);
    }

    public static String getFontNameUser() {
        return ExportPdfPrefs.prefs.get(ExportPdfPrefs.PDF_FONT_NAME_USER, ExportPdfPrefs.PDF_FONT_NAME_USER_DEFAULT);
    }

    public static int getFontStyle() {
        return ExportPdfPrefs.prefs.getInt(ExportPdfPrefs.PDF_FONT_STYLE, ExportPdfPrefs.PDF_FONT_STYLE_DEFAULT);
    }

    public static int getFontSize() {
        return ExportPdfPrefs.prefs.getInt(ExportPdfPrefs.PDF_FONT_SIZE, ExportPdfPrefs.PDF_FONT_SIZE_DEFAULT);
    }

    public static int getCompression() {
        return ExportPdfPrefs.prefs.getInt(ExportPdfPrefs.PDF_COMPRESSION, ExportPdfPrefs.PDF_COMPRESSION_DEFAULT);
    }

    public static boolean isUsingTitle() {
        return ExportPdfPrefs.prefs.getBoolean(ExportPdfPrefs.PDF_IS_USING_TITLE, ExportPdfPrefs.PDF_IS_USING_TITLE_DEFAULT);
    }

    public static boolean isUsingNrCrt() {
        return ExportPdfPrefs.prefs.getBoolean(ExportPdfPrefs.PDF_IS_USING_NR_CRT, ExportPdfPrefs.PDF_IS_USING_NR_CRT_DEFAULT);
    }

    public static boolean isShowingHeader() {
        return ExportPdfPrefs.prefs.getBoolean(ExportPdfPrefs.PDF_IS_SHOWING_HEADER, ExportPdfPrefs.PDF_IS_SHOWING_HEADER_DEFAULT);
    }

    public static boolean isUsingGrayEffect() {
        return ExportPdfPrefs.prefs.getBoolean(ExportPdfPrefs.PDF_IS_USING_GRAY_EFFECT, ExportPdfPrefs.PDF_IS_USING_GRAY_EFFECT_DEFAULT);
    }

    public static boolean isUsingPageNo() {
        return ExportPdfPrefs.prefs.getBoolean(ExportPdfPrefs.PDF_IS_USING_PAGE_NO, ExportPdfPrefs.PDF_IS_USING_PAGE_NO_DEFAULT);
    }

}
