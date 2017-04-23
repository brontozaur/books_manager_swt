package com.papao.books.view.auth;

import com.papao.books.view.util.Constants;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.Locale;

public final class EncodeLive {

    private final static Logger logger = Logger.getLogger(EncodeLive.class);

    public static final String APP_REPORTS_DIR = "reports";
    public static final String APP_LOGS_ROOT = "logs";
    public static final String APP_XML_CURS_VALUTAR = "updateCurs";
    public static final String APP_STARTUP_ROOT = "startup";
    public static final String APP_ACCOUNTS_ROOT = "accounts";
    public static final String APP_LIBS_ROOT = "libs";
    public static final String APP_EXECUTABLE_DIR = "app";
    public static final String APP_TEMP_DIR = "temp";
    public final static String ERROR_CREATING_DIR = "Nu s-a putut crea directorul ";

    /**
     * numele directorului din logs, care va retine fisiere generate de un printStream, pentru cazul
     * in care log4J este indisponibil.
     */
    public static final String APP_LOGS_FATAL = "fatal_errors";

    private static Calendar calendar = Calendar.getInstance();
    private static int zi = EncodeLive.calendar.get(Calendar.DAY_OF_MONTH);
    private static int luna = EncodeLive.calendar.get(Calendar.MONTH) + 1;
    private static int an = EncodeLive.calendar.get(Calendar.YEAR);

    private static String idUser;
    private static String currentUserName;
    private static String currentDb;
    private static long idValutaDefault;

    public static final Locale ROMANIAN_LOCALE = new Locale("ro", "RO");

    private EncodeLive() {
    }

    public static void init() {
        StringBuilder dataLogare = new StringBuilder();
        StringBuilder oraLogare = new StringBuilder();
        dataLogare.append(EncodeLive.getAppYear());
        dataLogare.append('_');
        if (EncodeLive.getAppMonth() < 10) {
            dataLogare.append(0);
        }
        dataLogare.append(EncodeLive.getAppMonth());
        dataLogare.append('_');
        if (EncodeLive.getAppDay() < 10) {
            dataLogare.append(0);
        }
        dataLogare.append(EncodeLive.getAppDay());

        Calendar appCalendar = Calendar.getInstance();

        oraLogare.append(appCalendar.get(Calendar.HOUR));
        oraLogare.append(':');
        oraLogare.append(appCalendar.get(Calendar.MINUTE));
        oraLogare.append(':');
        if (appCalendar.get(Calendar.AM_PM) == Calendar.AM) {
            oraLogare.append("AM");
        } else {
            oraLogare.append("PM");
        }

        logger.info("\t_._Encode Systems online on " + dataLogare.toString() + " at "
                + oraLogare.toString() + "_._");
        logger.info("\tLocation : " + System.getProperties().getProperty("user.dir"));
        logger.info("\tSystem running on : " + Constants.getJavaRuntimeName());
        logger.info("\tJava home : " + Constants.getJavaHome());
        logger.info("\tJava version : " + Constants.getJavaVersion());
        logger.info("\tSWT version : " + Constants.getSWTVersion());
        logger.info("\tTimeZone : " + Constants.getUserTimeZone());
        logger.info("\tOS : " + Constants.getOSName() + " v. " + Constants.getOSVersion() + ", architecture : " + Constants.getOSArchitecture());
    }

    public static String getReportsDir() {
        StringBuilder filePath;
        File maker;
        try {
            filePath = new StringBuilder();

            filePath.append(EncodeLive.getUserDir());

            filePath.append(EncodeLive.APP_REPORTS_DIR);
            filePath.append(File.separator);
            maker = new File(filePath.toString());
            if (!maker.exists() && !maker.mkdir()) {
                throw new IOException(EncodeLive.ERROR_CREATING_DIR + maker.getCanonicalPath());
            }

            filePath.append(EncodeLive.getAppYear());
            filePath.append(File.separator);
            maker = new File(filePath.toString());
            if (!maker.exists() && !maker.mkdir()) {
                throw new IOException(EncodeLive.ERROR_CREATING_DIR + maker.getCanonicalPath());
            }

            if (EncodeLive.getAppMonth() < 10) {
                filePath.append('0');
            }
            filePath.append(EncodeLive.getAppMonth());
            filePath.append(File.separator);
            maker = new File(filePath.toString());
            if (!maker.exists() && !maker.mkdir()) {
                throw new IOException(EncodeLive.ERROR_CREATING_DIR + maker.getCanonicalPath());
            }

            if (EncodeLive.getAppDay() < 10) {
                filePath.append('0');
            }
            filePath.append(EncodeLive.getAppDay());
            filePath.append(File.separator);
            maker = new File(filePath.toString());
            if (!maker.exists() && !maker.mkdir()) {
                throw new IOException(EncodeLive.ERROR_CREATING_DIR + maker.getCanonicalPath());
            }

            return maker.getCanonicalPath();
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return System.getProperty("user.dir");
        }
    }

    public static String getAppTempDirDir() {
        StringBuilder filePath;
        File maker;
        try {
            filePath = new StringBuilder();

            filePath.append(EncodeLive.getUserDir());

            filePath.append(EncodeLive.APP_TEMP_DIR);
            filePath.append(File.separator);
            maker = new File(filePath.toString());
            if (!maker.exists() && !maker.mkdir()) {
                throw new IOException(EncodeLive.ERROR_CREATING_DIR + maker.getCanonicalPath());
            }
            return maker.getCanonicalPath().concat(File.separator);
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return System.getProperty("user.dir");
        }
    }

    public static String getXMLCursValutarDir() {
        StringBuilder filePath;
        File maker;
        try {
            filePath = new StringBuilder();

            filePath.append(EncodeLive.getUserDir());

            filePath.append(EncodeLive.APP_XML_CURS_VALUTAR);
            filePath.append(File.separator);

            maker = new File(filePath.toString());
            if (!maker.exists() && !maker.mkdir()) {
                throw new IOException(EncodeLive.ERROR_CREATING_DIR + maker.getCanonicalPath());
            }
            return maker.getCanonicalPath();
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return System.getProperty("user.dir");
        }
    }

    public static String getUserDir() {
        StringBuilder filePath;
        File maker;
        try {
            filePath = new StringBuilder();

            filePath.append(EncodeLive.getAppDir());
            filePath.append(EncodeLive.APP_ACCOUNTS_ROOT);
            filePath.append(File.separator);
            maker = new File(filePath.toString());
            if (!maker.exists() && !maker.mkdir()) {
                throw new IOException(EncodeLive.ERROR_CREATING_DIR + maker.getCanonicalPath());
            }

            filePath.append(File.separator);
            maker = new File(filePath.toString());
            if (!maker.exists() && !maker.mkdir()) {
                throw new IOException(EncodeLive.ERROR_CREATING_DIR + maker.getCanonicalPath());
            }

            return filePath.toString();
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return null;
        }
    }

    public static String getLogsRootDir() {
        StringBuilder filePath;
        File maker;
        try {
            filePath = new StringBuilder();

            filePath.append(EncodeLive.getUserDir());
            filePath.append(EncodeLive.APP_LOGS_ROOT);
            filePath.append(File.separator);
            maker = new File(filePath.toString());
            if (!maker.exists() && !maker.mkdir()) {
                throw new IOException(EncodeLive.ERROR_CREATING_DIR + maker.getCanonicalPath());
            }
            return filePath.toString();
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return null;
        }
    }

    public static String getAppDir() {
        return System.getProperties().getProperty("user.dir") + File.separator;
    }

    public static int getAppDay() {
        return EncodeLive.zi;
    }

    public static void setAppDay(final int zi) {
        EncodeLive.zi = zi;
        EncodeLive.calendar.set(Calendar.DAY_OF_MONTH, zi);
    }

    public static int getAppMonth() {
        return EncodeLive.luna;
    }

    public static void setAppMonth(final int luna) {
        EncodeLive.luna = luna;
        EncodeLive.calendar.set(Calendar.MONTH, luna - 1);
    }

    public static int getAppYear() {
        return EncodeLive.an;
    }

    public static void setAppYear(final int an) {
        EncodeLive.an = an;
        EncodeLive.calendar.set(Calendar.YEAR, an);
    }

    public static boolean isLeapYear() {
        return ((EncodeLive.getAppYear() % 4) == 0);
    }

    public static Date getSQLDateLogin() {
        StringBuilder sb = new StringBuilder();
        sb.append(EncodeLive.getAppYear());
        sb.append("-");
        if (EncodeLive.getAppMonth() < 10) {
            sb.append(0);
        }
        sb.append(EncodeLive.getAppMonth());
        sb.append("-");
        if (EncodeLive.getAppDay() < 10) {
            sb.append(0);
        }
        sb.append(EncodeLive.getAppDay());
        return Date.valueOf(sb.toString());
    }

    public static Date getSQLDateLoginMinusX(final int nrOfDaysToSubtract) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(EncodeLive.getSQLDateLogin());
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - nrOfDaysToSubtract);
        return new Date(cal.getTime().getTime());
    }

    public static Date getSQLDateLoginDayOne() {
        return Date.valueOf(EncodeLive.getAppYear() + "-"
                + (EncodeLive.getAppMonth() < 10 ? "0" : "") + EncodeLive.getAppMonth() + "-"
                + "01");
    }

    public static java.util.Date getUtilDateLogin() {
        return EncodeLive.calendar.getTime();
    }

    public static java.util.Date getUtilDateLoginDayOne() {
        Calendar cal = (Calendar) EncodeLive.calendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public static String getIdUser() {
        if (idUser == null) {
            return "";
        }
        return EncodeLive.idUser;
    }

    public static void setIdUser(String idUser) {
        EncodeLive.idUser = idUser;
    }

    public static String getCurrentUserName() {
        return EncodeLive.currentUserName;
    }

    public static void setCurrentUserName(final String currentUserName) {
        EncodeLive.currentUserName = currentUserName;
    }

    public static String getCurrentDb() {
        return EncodeLive.currentDb;
    }

    public static void setCurrentDb(final String currentDb) {
        EncodeLive.currentDb = currentDb;
    }

    public static long getIdValutaDefault() {
        return idValutaDefault;
    }

    public static void setIdValutaDefault(long idValutaDefault) {
        EncodeLive.idValutaDefault = idValutaDefault;
    }

}
