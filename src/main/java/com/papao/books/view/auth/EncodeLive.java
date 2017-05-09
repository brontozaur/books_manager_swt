package com.papao.books.view.auth;

import com.papao.books.view.util.Constants;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.eclipse.swt.SWT;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public final class EncodeLive {

    private final static Logger logger = Logger.getLogger(EncodeLive.class);

    public static final String APP_REPORTS_DIR = "reports";
    public static final String APP_LOGS_ROOT = "logs";
    public static final String APP_ACCOUNTS_ROOT = "accounts";
    public static final String APP_TEMP_DIR = "temp";
    public final static String ERROR_CREATING_DIR = "Nu s-a putut crea directorul ";

    public static final boolean IS_MAC = "carbon".equals(SWT.getPlatform()) || "cocoa".equals(SWT.getPlatform());

    private static Calendar calendar = Calendar.getInstance();
    private static int zi = EncodeLive.calendar.get(Calendar.DAY_OF_MONTH);
    private static int luna = EncodeLive.calendar.get(Calendar.MONTH) + 1;
    private static int an = EncodeLive.calendar.get(Calendar.YEAR);

    private static ObjectId idUser;
    private static String currentUserName;

    public static final Locale ROMANIAN_LOCALE = new Locale("ro", "RO");

    private static boolean notificationUsingOpalStyle;

    private EncodeLive() {
    }

    public static void init() {
        StringBuilder dataLogare = new StringBuilder();
        StringBuilder oraLogare = new StringBuilder();
        dataLogare.append(EncodeLive.an);
        dataLogare.append('_');
        if (EncodeLive.luna < 10) {
            dataLogare.append(0);
        }
        dataLogare.append(EncodeLive.luna);
        dataLogare.append('_');
        if (EncodeLive.zi < 10) {
            dataLogare.append(0);
        }
        dataLogare.append(EncodeLive.zi);

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

            filePath.append(EncodeLive.an);
            filePath.append(File.separator);
            maker = new File(filePath.toString());
            if (!maker.exists() && !maker.mkdir()) {
                throw new IOException(EncodeLive.ERROR_CREATING_DIR + maker.getCanonicalPath());
            }

            if (EncodeLive.luna < 10) {
                filePath.append('0');
            }
            filePath.append(EncodeLive.luna);
            filePath.append(File.separator);
            maker = new File(filePath.toString());
            if (!maker.exists() && !maker.mkdir()) {
                throw new IOException(EncodeLive.ERROR_CREATING_DIR + maker.getCanonicalPath());
            }

            if (EncodeLive.zi < 10) {
                filePath.append('0');
            }
            filePath.append(EncodeLive.zi);
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

    public static ObjectId getIdUser() {
        return EncodeLive.idUser;
    }

    public static void setIdUser(ObjectId idUser) {
        EncodeLive.idUser = idUser;
    }

    public static String getCurrentUserName() {
        return EncodeLive.currentUserName;
    }

    public static void setCurrentUserName(final String currentUserName) {
        EncodeLive.currentUserName = currentUserName;
    }

    public static boolean isNotificationUsingOpalStyle() {
        return notificationUsingOpalStyle;
    }

    public static void setNotificationUsingNotifier(boolean notificationUsingOpalStyle) {
        EncodeLive.notificationUsingOpalStyle = notificationUsingOpalStyle;
    }
}
