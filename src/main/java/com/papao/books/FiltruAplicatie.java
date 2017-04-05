package com.papao.books;

import com.papao.books.view.util.ColorUtil;
import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public final class FiltruAplicatie {

	private static Logger logger = Logger.getLogger(FiltruAplicatie.class);

    private final static String DEFAULT_DATE_FORMATTER = "yyyy-MM-dd";

    private final static String DEFAULT_TIME_FORMATTER = "HH:mm:ss";

    /**
     * Format afisare data. Exemplu "aa-ll-zz"
     */
    public final static String KEY_APP_DATE_FORMAT = "date.format";
    public static final String[] AVAILABLE_DATE_FORMATS = new String[] {
            "yyyy-MM-dd", "yyyy/MM/dd", "yyyy.MM.dd", "dd-MM-yyyy", "dd/MM/yyyy", "dd.MM.yyyy", "yy-MM-dd", "yy/MM/dd", "yy.MM.dd", "dd-MM-yy",
            "dd/MM/yy", "dd.MM.yy", "EEEE, dd MMM yyyy", "dd-MMMM-yyyy" };
    public final static String KEY_APP_DATE_FORMAT_DEFAULT = DEFAULT_DATE_FORMATTER;

    public final static String KEY_APP_TIME_FORMAT = "time.format";
    public static final String[] AVAILABLE_TIME_FORMATS = new String[] {
            "HH:mm:ss", "HH:mm", "hh:mm:ss a", "hh:mm a" };
    public final static String KEY_APP_TIME_FORMAT_DEFAULT = DEFAULT_TIME_FORMATTER;

    /**
     * lansare help ca html sau CHM. Valori corecte : CHM, HTML. Default : HTML.
     */
    public final static String KEY_HELP_FORMAT = "help.format";
    public static final String[] AVAILABLE_HELP_FORMATS = new String[] {
            "HTML", "CHM" };
    public static final String DEFAULT_KEY_HELP_FORMAT = FiltruAplicatie.AVAILABLE_HELP_FORMATS[0];

    /**
     * proprietate care gestioneaza la desenarea fiecarei ferestre, daca se afiseaza detalii pe fond
     * alb, in partea de sus a acesteia
     */
    public final static String KEY_RICH_APP_WINDOWS = "windows.rich";
    public final static boolean KEY_RICH_APP_WINDOWS_DEFAULT = true;

    /**
     * proprietate ce conditioneaza restaurarea pozitiei si locatiei avute de fiecare/orice
     * fereastra, folosind ultimele coordonate/dimensiuni, daca exista
     */
    public final static String KEY_WINDOWS_USE_COORDS = "windows.use.coords";
    public final static boolean KEY_WINDOWS_USE_COORDS_DEFAULT = false;

    /**
     * proprietate ce conditioneaza restaurarea coloanelor vizibile, a alinierii si a dimensiunii
     * configurate pt acestea, pentru orice tabela sau tree din aplicatie.
     */
    public final static String KEY_TABLE_USE_PREFS = "table.use.prefs";
    public final static boolean KEY_TABLE_USE_PREFS_DEFAULT = true;

    /**
     * proprietate care gestioneaza daca se cere sau nu o confirmare la inchiderea unei ferestre,
     * daca butonul de save nu este disposed.
     */
    public final static String KEY_WINDOWS_ASK_ON_CLOSE = "windows.ask.on.close";
    public final static boolean KEY_WINDOWS_ASK_ON_CLOSE_DEFAULT = false;

    /**
     * proprietate care gestioneaza apelarea unui reset() + mode_add pe orice fereastra care are
     * butonul de save() + implementeaza IEncodeReset. Daca e bifata chestia asta, dupa salvare
     * forma curenta va suferi un reset() si intra pe modul de adaugare noua inregistrare.
     */
    public final static String KEY_WINDOWS_REENTER_DATA = "windows.reenter.data";
    public final static boolean KEY_WINDOWS_REENTER_DATA_DEFAULT = false;

    /**
     * proprietate care gestioneaza folosirea sau nu a componentei din Tray.
     */
    public final static String KEY_APP_IS_USING_TRAY = "app.use.tray";
    public final static boolean KEY_APP_IS_USING_TRAY_DEFAULT = false;

    public final static String KEY_APP_TRAY_SEND_MESSAGES = "tray.sends.messages";
    public final static boolean KEY_APP_TRAY_SEND_MESSAGES_DEFAULT = true;

    public final static String KEY_APP_REPORTS_SHOW_OPTIONS = "reports.show.options";
    public final static boolean KEY_APP_REPORTS_SHOW_OPTIONS_DEFAULT = true;

    /**
     * configurare numar de zecimale afisabile pentru sumele in valuta
     */
    public final static String KEY_ZECIMALE_VALUTA = "zecimale.valuta";
    public final static int KEY_ZECIMALE_VALUTA_DEFAULT = 4;

    /**
     * configurare numar de zecimale afisabile pentru sumele in lei
     */
    public final static String KEY_ZECIMALE_LEI = "zecimale.lei";
    public final static int KEY_ZECIMALE_LEI_DEFAULT = 2;

    /**
     * configurare numar de zecimale afisabile pentru curs valuta
     */
    public final static String KEY_ZECIMALE_CURS = "zecimale.curs";
    public final static int KEY_ZECIMALE_CURS_DEFAULT = 4;

    /**
     * configurare numar de zecimale afisabile pentru cantitati
     */
    public final static String KEY_ZECIMALE_CANTITATE = "zecimale.cantitate";
    public final static int KEY_ZECIMALE_CANTITATE_DEFAULT = 4;

    public final static String KEY_LEFT_TREE_SHOW_ALL = "left.tree.show.all";
    public final static boolean KEY_LEFT_TREE_SHOW_ALL_DEFAULT = true;

    public final static String KEY_LEFT_TREE_SHOW_RECENT_ACTIVITY = "left.tree.show.recent.ops";
    public final static boolean KEY_LEFT_TREE_SHOW_RECENT_ACTIVITY_DEFAULT = true;

    public final static String KEY_AUTOPOPULATE_TABS = "tabs.autopopulate";
    public final static boolean KEY_AUTOPOPULATE_TABS_DEFAULT = true;

    public final static String KEY_USE_COUNT = "tabs.usecount";
    public final static boolean KEY_USE_COUNT_DEFAULT = false;

    public final static String KEY_COUNT_NUMBER = "tabs.count.number";
    public final static int KEY_COUNT_NUMBER_DEFAULT = 0;

    public static final String LAST_VISITED_ATOM = "last.visited.atom";

    public final static String KEY_HIGHLIGHT_COLOR_RGB = "highlight.color.rgb";
    public final static Color HIGHLIGHT_COLOR_DEFAULT = ColorUtil.COLOR_ALBASTRU_DESCHIS_PHEX;
    private static Color HIGHLIGHT_COLOR = null;
    public final static String KEY_HIGHLIGHT_COLOR_RGB_DEFAULT = FiltruAplicatie.HIGHLIGHT_COLOR_DEFAULT.getRed() + ","
            + FiltruAplicatie.HIGHLIGHT_COLOR_DEFAULT.getGreen() + "," + FiltruAplicatie.HIGHLIGHT_COLOR_DEFAULT.getBlue();

    public final static String KEY_HIGHLIGHT_SEARCH_USE_BOLD = "highlight.font.bold";
    public final static boolean KEY_HIGHLIGHT_SEARCH_USE_BOLD_DEFAULT = true;

    public final static String KEY_HIGHLIGHT_SEARCH_USE_COLOR = "highlight.use.color";
    public final static boolean KEY_HIGHLIGHT_SEARCH_USE_COLOR_DEFAULT = true;

    public final static String KEY_VALUTA_DOC = "valuta.doc.default";

    private FiltruAplicatie() {}

    /**
     * Filter easy getters of filter keys.
     */
    public static boolean isWindowsAskingOnClose() {
        return KEY_WINDOWS_ASK_ON_CLOSE_DEFAULT;
    }

    public static boolean isWindowsReenteringData() {
        return KEY_WINDOWS_REENTER_DATA_DEFAULT;
    }

    public static boolean isWindowsUsingRichWindows() {
        return KEY_RICH_APP_WINDOWS_DEFAULT;
    }

    public static boolean isWindowsUsingCoords() {
        return KEY_WINDOWS_USE_COORDS_DEFAULT;
    }

    public static boolean isTableUsingPrefs() {
        return KEY_TABLE_USE_PREFS_DEFAULT;
    }

    public static boolean isLeftTreeShowingAll() {
        return KEY_LEFT_TREE_SHOW_ALL_DEFAULT;
    }

    public static boolean isAutopopulateTabs() {
        return KEY_AUTOPOPULATE_TABS_DEFAULT;
    }

    public static boolean isTabsUsingCount() {
        return KEY_USE_COUNT_DEFAULT;
    }

    public static String getAppDateFormat() {
        return KEY_APP_DATE_FORMAT_DEFAULT;
    }

    public static String getAppTimeFormat() {
        return KEY_APP_TIME_FORMAT_DEFAULT;
    }

    public static String getAppTimestampFormat() {
        return FiltruAplicatie.getAppDateFormat().concat(" ").concat(FiltruAplicatie.getAppTimeFormat());
    }

    public static boolean isHighlightUsingBold() {
        return KEY_HIGHLIGHT_SEARCH_USE_BOLD_DEFAULT;
    }

    public static boolean isHighlightUsingColor() {
        return KEY_HIGHLIGHT_SEARCH_USE_COLOR_DEFAULT;
    }

    public static Color getHighlightColor() {
        try {
            String[] rgbCodes = KEY_HIGHLIGHT_COLOR_RGB_DEFAULT.split(",");
            int red = Integer.parseInt(rgbCodes[0]);
            int green = Integer.parseInt(rgbCodes[1]);
            int blue = Integer.parseInt(rgbCodes[2]);
            if ((red == FiltruAplicatie.HIGHLIGHT_COLOR_DEFAULT.getRed()) && (green == FiltruAplicatie.HIGHLIGHT_COLOR_DEFAULT.getGreen())
                    && (blue == FiltruAplicatie.HIGHLIGHT_COLOR_DEFAULT.getBlue())) {
                return FiltruAplicatie.HIGHLIGHT_COLOR_DEFAULT;
            }
            FiltruAplicatie.HIGHLIGHT_COLOR = new Color(Display.getDefault(), red, green, blue);
            return FiltruAplicatie.HIGHLIGHT_COLOR;
        } catch (Exception exc) {
			logger.warn("error getting the highlight rgb!");
            return FiltruAplicatie.HIGHLIGHT_COLOR_DEFAULT;
        }
    }

    public static boolean isLeftTreeShowRecentActivity() {
        return true;
    }
}
