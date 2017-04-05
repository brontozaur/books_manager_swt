package com.papao.books.view.util;

import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.NumberFormatter;

import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * Nu creati formattere statice pentru {@link FormattedText} aici. Efectele vor fi mai mult decat neasteptate :-).
 */
public final class NumberUtil {

    public final static String FORMATTER_TIME = "HH:mm";

    private NumberUtil() {}

    public static String formatNumber(final double number, final int digits) {
        final NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(digits);
        nf.setGroupingUsed(true);
        nf.setMinimumFractionDigits(digits);
        nf.setRoundingMode(RoundingMode.HALF_EVEN);
        return nf.format(number);
    }

    public static String getPattern(final boolean isLei) {
        int nrZecimale = 2;
        String baseStr = "#,##0.";
        for (int i = 0; i < nrZecimale; i++) {
            baseStr = baseStr.concat("#");
        }
        return baseStr;
    }

    public static String getPattern(int digits, final boolean allowNegativeValues) {
        StringBuilder sb = new StringBuilder(100);
        if (allowNegativeValues) {
            sb.append('-');
        }
        sb.append("#,##0");
        if (digits > 0) {
            sb.append(".");
        }
        for (int i = 0; i < digits; i++) {
            sb.append('#');
        }
        return sb.toString();
    }

    public static MathContext getMathContext(final int digits) {
        return new MathContext(digits, RoundingMode.HALF_EVEN);
    }

    public static NumberFormatter getFormatter(final boolean isLei) {
        return new NumberFormatter(NumberUtil.getPattern(isLei));
    }

    public static NumberFormatter getFormatter(final String format) {
        return new NumberFormatter(format);
    }

    public static NumberFormatter getFormatter(final int digits, final boolean allowNegativeValues) {
        return new NumberFormatter(NumberUtil.getPattern(digits, allowNegativeValues));
    }
}
