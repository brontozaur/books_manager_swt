package com.papao.books.util;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public final class BorgDateUtil {

	private static Logger logger = Logger.getLogger(BorgDateUtil.class);

    public static final String AM = "AM";
    public static final String PM = "PM";

    public static final String DUMINICA = "Duminica";
    public static final String LUNI = "Luni";
    public static final String MARTI = "Marti";
    public static final String MIERCURI = "Miercuri";
    public static final String JOI = "Joi";
    public static final String VINERI = "Vineri";
    public static final String SAMBATA = "Sambata";

    public static final String[] ZILELE_SAPTAMANII = new String[] {
            BorgDateUtil.DUMINICA, BorgDateUtil.LUNI, BorgDateUtil.MARTI, BorgDateUtil.MIERCURI, BorgDateUtil.JOI, BorgDateUtil.VINERI,
            BorgDateUtil.SAMBATA, BorgDateUtil.DUMINICA };

    public static final String IANUARIE = "Ianuarie";
    public static final String FEBRUARIE = "Februarie";
    public static final String MARTIE = "Martie";
    public static final String APRILIE = "Aprilie";
    public static final String MAI = "Mai";
    public static final String IUNIE = "Iunie";
    public static final String IULIE = "Iulie";
    public static final String AUGUST = "August";
    public static final String SEPTEMBRIE = "Septembrie";
    public static final String OCTOMBRIE = "Octombrie";
    public static final String NOIEMBRIE = "Noiembrie";
    public static final String DECEMBRIE = "Decembrie";

    public static final String[] LUNILE = new String[] {
            BorgDateUtil.IANUARIE, BorgDateUtil.FEBRUARIE, BorgDateUtil.MARTIE, BorgDateUtil.APRILIE, BorgDateUtil.MAI, BorgDateUtil.IUNIE,
            BorgDateUtil.IULIE, BorgDateUtil.AUGUST, BorgDateUtil.SEPTEMBRIE, BorgDateUtil.OCTOMBRIE, BorgDateUtil.NOIEMBRIE, BorgDateUtil.DECEMBRIE };

    public static final String IAN = "Ian";
    public static final String FEB = "Feb";
    public static final String MAR = "Mar";
    public static final String APR = "Apr";
    public static final String MAI_ = "Mai";
    public static final String IUN = "Iun";
    public static final String IUL = "Iul";
    public static final String AUG = "Aug";
    public static final String SEP = "Sep";
    public static final String OCT = "Oct";
    public static final String NOI = "Noi";
    public static final String DEC = "Dec";

    public static final String[] LUNILE_SCURT = new String[] {
            BorgDateUtil.IAN, BorgDateUtil.FEB, BorgDateUtil.MAR, BorgDateUtil.APR, BorgDateUtil.MAI_, BorgDateUtil.IUN, BorgDateUtil.IUL,
            BorgDateUtil.AUG, BorgDateUtil.SEP, BorgDateUtil.OCT, BorgDateUtil.NOI, BorgDateUtil.DEC };

    private static final String ERR_NULL_DATE_VALUE = "Cannot get a valid date from a null value..";

    private static long ONE_SECOND = 1000L;

    private static long ONE_MINUTE = 60 * BorgDateUtil.ONE_SECOND;

    private static long ONE_HOUR = 60 * BorgDateUtil.ONE_MINUTE;

    private static long ONE_DAY = 24 * BorgDateUtil.ONE_HOUR;

    private BorgDateUtil() {}

    public static int getMaxZileInLuna(final int luna, final int an) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, luna);
        cal.set(Calendar.YEAR, an);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * @param date
     *            a non-null date
     * @return a Romanian formatted DAY_OF_WEEK
     *         <p>
     *         This method works. However, when the Date doesnt change, try to initialize the Calendar first and call the String getDayInRO(final
     *         Calendar calendar) instead. This will give us some performance boost, since the calendar is not instantiated with every iteration.
     */
    public static String getDayInRO(final Date date) {
        String result;
        try {
            if (date == null) {
                throw new IllegalArgumentException(BorgDateUtil.ERR_NULL_DATE_VALUE);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            result = BorgDateUtil.ZILELE_SAPTAMANII[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        } catch (Exception exc) {
            result = "";
			logger.error(exc.getMessage(), exc);
        }
        return result;
    }

    public static String getDayInRO(final Calendar calendar) {
        String result;
        try {
            if (calendar == null) {
                throw new IllegalArgumentException(BorgDateUtil.ERR_NULL_DATE_VALUE);
            }
            result = BorgDateUtil.ZILELE_SAPTAMANII[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        } catch (Exception exc) {
            result = "";
            logger.error(exc.getMessage(), exc);
        }
        return result;
    }

    /**
     * @param date
     *            a non-null date
     * @return a Romanian formatted DAY_OF_WEEK
     *         <p>
     *         This method works. However, when the Date doesnt change, try to initialize the Calendar first and call the String getDayInRO(final
     *         Calendar calendar) instead. This will give us some performance boost, since the calendar is not instantiated with every iteration.
     */
    public static String getMonthInRO(final Date date, final boolean shortName) {
        String result;
        try {
            if (date == null) {
                throw new IllegalArgumentException(BorgDateUtil.ERR_NULL_DATE_VALUE);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            if (shortName) {
                result = BorgDateUtil.LUNILE_SCURT[calendar.get(Calendar.MONTH)];
            } else {
                result = BorgDateUtil.LUNILE[calendar.get(Calendar.MONTH)];
            }
        } catch (Exception exc) {
            result = "";
            logger.error(exc.getMessage(), exc);
        }
        return result;
    }

    public static String getMonthInRO(final Calendar calendar, final boolean shortName) {
        String result;
        try {
            if (calendar == null) {
                throw new IllegalArgumentException(BorgDateUtil.ERR_NULL_DATE_VALUE);
            }
            if (shortName) {
                result = BorgDateUtil.LUNILE_SCURT[calendar.get(Calendar.MONTH)];
            } else {
                result = BorgDateUtil.LUNILE[calendar.get(Calendar.MONTH)];
            }
        } catch (Exception exc) {
            result = "";
            logger.error(exc.getMessage(), exc);
        }
        return result;
    }

    public static String formatTimeFromMillis(final long time_param, final char h_symbol, final char min_symbol, final char s_symbol) {
        long ore, minute, secunde;
        long time = time_param;
        StringBuilder result = new StringBuilder();
        try {
            ore = time / BorgDateUtil.ONE_HOUR;
            time = time - ore * BorgDateUtil.ONE_HOUR;
            minute = time / BorgDateUtil.ONE_MINUTE;
            time = time - minute * BorgDateUtil.ONE_MINUTE;
            secunde = time / BorgDateUtil.ONE_SECOND;
            result.append(h_symbol);
            result.append(":");
            if (ore < 10) {
                result.append("0");
            }
            result.append(ore);

            result.append(" ");

            result.append(min_symbol);
            result.append(":");
            if (minute < 10) {
                result.append("0");
            }
            result.append(minute);

            result.append(" ");

            result.append(s_symbol);
            result.append(":");
            if (secunde < 10) {
                result.append("0");
            }
            result.append(secunde);
        } catch (Exception exc) {
            result.delete(0, result.length());
            logger.error(exc.getMessage(), exc);
        }
        return result.toString();
    }

    /**
     * @param time_param
     * @return nr de ore, rotunjit
     */
    public static long getOre(final long time_param) {
        return time_param / BorgDateUtil.ONE_HOUR;
    }

    /**
     * @param time_param
     * @return se scad orele, dupa care se intoarce nr de minute, rotunjit
     */
    public static long getMinute(final long time_param) {
        final long time = time_param;
        return (time - BorgDateUtil.getOre(time) * BorgDateUtil.ONE_HOUR) / BorgDateUtil.ONE_MINUTE;
    }

    /**
     * @param time_param
     * @return se scad orele si minutele, dupa care se intoarce nr de secunde, rotunjit
     */
    public static long getSecunde(final long time_param) {
        final long time = time_param;
        return (time - BorgDateUtil.getOre(time) * BorgDateUtil.ONE_HOUR - BorgDateUtil.getMinute(time) * BorgDateUtil.ONE_MINUTE)
                / BorgDateUtil.ONE_SECOND;
    }

    public static String getFormattedDateStr(final Date data, final String format) {
        if (data == null) {
            return null;
        }
        return DateFormatUtils.format(data, format);
    }

    public static java.sql.Date getDayOneDate(final Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return new java.sql.Date(cal.getTimeInMillis());
    }

    public static java.sql.Date getSQLDate_YYYY_MM_DD(final Date data) {
        if (data == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        StringBuilder sb = new StringBuilder();
        sb.append(cal.get(Calendar.YEAR));
        sb.append("-");
        sb.append(cal.get(Calendar.MONTH) + 1);
        sb.append("-");
        sb.append(cal.get(Calendar.DAY_OF_MONTH));
        return java.sql.Date.valueOf(sb.toString());
    }

    public static String getSQLDate_YYYY_MM_DD_Str(final Date data) throws ParseException {
        java.sql.Date parsedDate = BorgDateUtil.getSQLDate_YYYY_MM_DD(data);
        if (parsedDate == null) {
            throw new ParseException("unable to parse a null date", 0);
        }
        return parsedDate.toString();
    }

    public static long daysBetween(final Date startDate, final Date endDate) {
        if ((endDate == null) || (startDate == null)) {
            return 0;
        }
        return ((endDate.getTime() - startDate.getTime() + BorgDateUtil.ONE_HOUR) / BorgDateUtil.ONE_DAY);
    }

    public static long hoursBetween(final Date startDate, final Date endDate) {
        if ((endDate == null) || (startDate == null)) {
            return 0;
        }
        return ((endDate.getTime() - startDate.getTime() + BorgDateUtil.ONE_MINUTE) / BorgDateUtil.ONE_HOUR);
    }

    public static long minutesBetween(final Date startDate, final Date endDate) {
        if ((endDate == null) || (startDate == null)) {
            return 0;
        }
        return ((endDate.getTime() - startDate.getTime() + BorgDateUtil.ONE_SECOND) / BorgDateUtil.ONE_MINUTE);
    }

    public static long secondsBetween(final Date startDate, final Date endDate) {
        if ((endDate == null) || (startDate == null)) {
            return 0;
        }
        return ((endDate.getTime() - startDate.getTime() + 1) / BorgDateUtil.ONE_SECOND);
    }

    public static long miliSecondsBetween(final Date startDate, final Date endDate) {
        if ((endDate == null) || (startDate == null)) {
            return 0;
        }
        return endDate.getTime() - startDate.getTime();
    }
}
