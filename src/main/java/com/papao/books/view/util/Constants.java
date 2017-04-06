package com.papao.books.view.util;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public final class Constants {

    private Constants() {
    }

    private static Logger logger = Logger.getLogger(Constants.class);

    public final static long ID_SUPER_ADMIN = 1;
    public final static long ID_MAINTENANCE_USER = 2;

    public final static long ID_TARA_DEFAULT = 1;
    public final static long ID_VALUTA_DEFAULT = 1;

    public final static String BANCA_DEFAULT = "NBOR";
    public static final String CURS_BNR_SITE = "http://www.bnro.ro/nbrfxrates.xml";

    public final static String NOT_AVAILABLE = "N/A";

    public final static int[] EMPTY_INTEGER = new int[0];
    public final static boolean[] EMPTY_BOOLEAN = new boolean[0];
    public final static String[] EMPTY_STRING = new String[0];

    public final static String YES_STR = "DA";
    public final static String NO_STR = "NU";

    public final static Map<Integer, String> mapYesNo = new TreeMap<Integer, String>();

    static {
        mapYesNo.put(1, YES_STR);
        mapYesNo.put(0, NO_STR);
    }

    public final static int LEVEL_EXCEPTION = 0;
    public final static int LEVEL_WARNING = 1;
    public final static String LEVEL_EXCEPTION_STR = "Exceptie";
    public final static String LEVEL_WARNING_STR = "Avertizare";

    public static Map<Integer, String> mapLevels = new HashMap<Integer, String>();

    public final static int PRIORITATE_MICA = 0;
    public final static int PRIORITATE_MEDIE = 1;
    public final static int PRIORITATE_MARE = 2;
    public final static int PRIORITATE_ACUM = 3;
    public final static String PRIORITATE_MICA_STR = "Mica";
    public final static String PRIORITATE_MEDIE_STR = "Medie";
    public final static String PRIORITATE_MARE_STR = "Mare";
    public final static String PRIORITATE_ACUM_STR = "Acum!";

    public final static String STATUS_REZOLVATA = "Rezolvata";
    public final static String STATUS_NEREZOLVATA = "Nerezolvata";

    public static String decodeErrLevel(final int errLevel) {
        String str = "Necunoscut";
        if (errLevel == LEVEL_EXCEPTION) {
            str = LEVEL_EXCEPTION_STR;
        } else if (errLevel == LEVEL_WARNING) {
            str = LEVEL_WARNING_STR;
        }
        return str;
    }

    public static String decodePrio(final int prio) {
        String str = "";
        if (prio == PRIORITATE_MICA) {
            str = PRIORITATE_MICA_STR;
        } else if (prio == PRIORITATE_MEDIE) {
            str = PRIORITATE_MEDIE_STR;
        } else if (prio == PRIORITATE_MARE) {
            str = PRIORITATE_MARE_STR;
        } else if (prio == PRIORITATE_ACUM) {
            str = PRIORITATE_ACUM_STR;
        }
        return str;
    }


    public final static String ENCODE_SHELL_TEXT = "Books Manager \u00A9 brontozaur";
    public final static String TOTI = "- To\u0163i -";
    public final static String TOATE = "- Toate -";

    public static String getOSName() {
        return System.getProperty("os.name");
    }

    public static String getOSVersion() {
        return System.getProperty("os.version");
    }

    public static String getOSArchitecture() {
        return System.getProperty("os.arch");
    }

    public static String getJavaVersion() {
        return System.getProperty("java.version");
    }

    public static String getJavaRuntimeName() {
        return System.getProperty("java.runtime.name");
    }

    public static String getJavaHome() {
        return System.getProperty("java.home");
    }

    public static String getUserTimeZone() {
        return System.getProperty("user.timezone");
    }

    public static String getSWTVersion() {
        return String.valueOf(org.eclipse.swt.SWT.getVersion());
    }
}
