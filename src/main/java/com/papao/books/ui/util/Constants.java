package com.papao.books.ui.util;

public final class Constants {

    private Constants() {
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
