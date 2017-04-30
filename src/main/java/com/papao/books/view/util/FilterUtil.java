package com.papao.books.view.util;

import com.papao.books.FiltruAplicatie;
import com.papao.books.model.config.WindowSetting;
import com.papao.books.repository.AbstractSettingRepository;
import com.papao.books.view.auth.EncodeLive;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

@Component
public final class FilterUtil {

    private static AbstractSettingRepository settingRepository;

    @Autowired
	public FilterUtil(AbstractSettingRepository settingRepository) {
	    this.settingRepository = settingRepository;
    }

	public final static String NODE_LV1_BORG = "borg_application";
	// si lista ar putea continua, in mod inutil :D
	// de aceea am redus si ceea ce exista :D [ADM]
	public final static String NODE_LV5_COORDS = "coords";
	public final static String NODE_LV5_W_TABLES = "wtables";
	public final static String NODE_LV5_EXPORT = "export";
	public final static String NODE_LV5_VIEWER_FILTERS = "filters";

	public final static String KEY_LAST_SQL_ENGINE = "last.sql.engine.name";
	public final static String KEY_LAST_SQL_ENGINE_DEFAULT = "";
	public final static String KEY_LAST_USER_ID = "last.user.id";
	public final static long KEY_LAST_USER_ID_DEFAULT = Constants.ID_SUPER_ADMIN;
	public final static String KEY_LAST_DB = "last.db.name";
	public final static String KEY_LAST_DB_DEFAULT = "";
	public final static String KEY_LAST_USER_NAME = "last.user.name";
	public final static String KEY_LAST_USER_NAME_DEFAULT = "Super Admin";

	public final static String GRID_VISIBLE_COLS = "cols.visible";
	public final static String GRID_COLS_ALIGNS = "cols.aligns";
	public final static String GRID_COLS_WIDTHS = "cols.widths";
	public final static String GRID_COLS_ORDER = "cols.order";

	private static Logger logger = Logger.getLogger(FilterUtil.class);

	/**
	 * node processing and mapping - start. There are 2 types here : current user nodes, and absolute nodes.
	 */

	/**
	 * @return the root node of the app, located in the root of JavaPrefs.
	 *         <p>
	 *         Path: <b>\borg_application </b>
	 *         </p>
	 *         <ul>
	 *         <li>Windows7 x32: HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\Prefs</li>
	 *         <li>Windows7 x64: HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\Prefs</li>
	 *         </ul>
	 */
	public static Preferences getNodeBorgRoot() {
		return Preferences.systemRoot().node(FilterUtil.NODE_LV1_BORG);
	}

	/**
	 * @return the current db node, using saved key of the last db used, or default if none.
	 *         <p>
	 *         Path : <b>\borg_application ->last.db.name</b>
	 *         </p>
	 */
	public static Preferences getNodeCurrentDb() {
		return FilterUtil.getNodeBorgRoot().node(FilterUtil.getNodeBorgRoot().get(FilterUtil.KEY_LAST_DB, FilterUtil.KEY_LAST_DB_DEFAULT));
	}

	/**
	 * @return current user name, using profile and db to create the path, and saved key of the last user id used, or {@link AppConstants#ID_SUPER_ADMIN} if none.
	 *         <p>
	 *         Path : <b>\borg_application\last_persistence_unit_name ->lastUserId</b>
	 *         </p>
	 */
	public static Preferences getNodeCurrentUser() {
		return FilterUtil.getNodeCurrentDb().node(String.valueOf(FilterUtil.getLastUserId()));
	}

	public static Preferences getNodeUserRoot(final long idUser) {
		return FilterUtil.getNodeCurrentDb().node(String.valueOf(idUser));
	}

	/**
	 * @return the coords node, containing windows sizes and location. Will be created if none.
	 *         <p>
	 *         Path : <b>\borg_application\last_persistence_unit_name\currentUserId\coords</b>
	 *         </p>
	 */
	public static Preferences getNodeCoordsCurrentUser() {
		return FilterUtil.getNodeCurrentUser().node(FilterUtil.NODE_LV5_COORDS);
	}

	public static Preferences getNodeViewerFilterCurrentUser() {
		return FilterUtil.getNodeCurrentUser().node(FilterUtil.NODE_LV5_VIEWER_FILTERS);
	}

	/**
	 * @return the node where table cols, sizes and aligs for every window that has a table are saved. Will be created if none.
	 *         <p>
	 *         Path : <b>\borg_application\last_persistence_unit_name\currentUserId\wtables</b>
	 *         </p>
	 */
	public static Preferences getNodeCoordsWTables() {
		return FilterUtil.getNodeCurrentUser().node(FilterUtil.NODE_LV5_W_TABLES);
	}

	/**
	 * @param idNode
	 *            id-ul unui modul, tab sau bone din aplicatie.
	 * @return nodul aferent id-ului, pt userul curent.
	 *         <p>
	 *         Path : <b>\borg\lastDbName\lastUserId\<tt>idNode</tt></b>
	 *         </p>
	 */
	public static Preferences getNodeUserSettings(final int idNode) {
		return FilterUtil.getNodeCurrentUser().node(String.valueOf(idNode));
	}

	/**
	 * @return the export root node, containing subnodes for each export type prefs. Will be created if none.
	 *         <p>
	 *         Path : <b>\borg\lastDbName\lastUserId\export</b>
	 *         </p>
	 */
	public static Preferences getNodeExportCurrentUser() {
		return FilterUtil.getNodeCurrentUser().node(FilterUtil.NODE_LV5_EXPORT);
	}

	public static Preferences getNodeExportTxtCurrentUser() {
		return FilterUtil.getNodeExportCurrentUser().node("text");
	}

	public static Preferences getNodeExportPdfCurrentUser() {
		return FilterUtil.getNodeExportCurrentUser().node("pdf");
	}

	public static Preferences getNodeExportExcelCurrentUser() {
		return FilterUtil.getNodeExportCurrentUser().node("excel");
	}

	public static Preferences getNodeExportHtmlCurrentUser() {
		return FilterUtil.getNodeExportCurrentUser().node("html");
	}

	public static Preferences getNodeExportRtfCurrentUser() {
		return FilterUtil.getNodeExportCurrentUser().node("rtf");
	}

	/**
	 * node processing and mapping - end
	 */

	public static void checkValue(final String key, final Object value) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException("cannot set value on null key");
		}
		if (value == null) {
			throw new IllegalArgumentException("value cannot be null");
		}
	}

	/**
	 * basic Preferences acces using user key
	 */

	public static void put(final String key, final String value, final Preferences prefs) throws IllegalArgumentException {
		FilterUtil.checkValue(key, value);
		prefs.put(key, value);
	}

	public static void putBoolean(final String key, final boolean value, final Preferences prefs) throws IllegalArgumentException {
		FilterUtil.checkValue(key, value);
		prefs.putBoolean(key, value);
	}

	public static void putByteArray(final String key, final byte[] value, final Preferences prefs) throws IllegalArgumentException {
		FilterUtil.checkValue(key, value);
		prefs.putByteArray(key, value);
	}

	public static void putDouble(final String key, final double value, final Preferences prefs) throws IllegalArgumentException {
		FilterUtil.checkValue(key, value);
		prefs.putDouble(key, value);
	}

	public static void putFloat(final String key, final float value, final Preferences prefs) throws IllegalArgumentException {
		FilterUtil.checkValue(key, value);
		prefs.putFloat(key, value);
	}

	public static void putInt(final String key, final int value, final Preferences prefs) throws IllegalArgumentException {
		FilterUtil.checkValue(key, value);
		prefs.putInt(key, value);
	}

	public static void putLong(final String key, final long value, final Preferences prefs) throws IllegalArgumentException {
		FilterUtil.checkValue(key, value);
		prefs.putLong(key, value);
	}

	public static void flush(final Preferences prefs) throws BackingStoreException {
		prefs.flush();
	}

	/**
	 * @param prefs
	 *            the {@link Preferences} object to get from
	 * @param linkedValues
	 *            all possible values
	 * @param key
	 *            the key to be used in prefs
	 * @return the linked map of id-name of the visible objects
	 */
	public static Map<Integer, String> getVisibleObjects(final Preferences prefs, final Map<Integer, String> linkedValues, final String key) {
		Map<Integer, String> mapResult = new TreeMap<Integer, String>();
		final String visibleObjects = prefs.get(key, "");
		if (StringUtils.isNotEmpty(visibleObjects)) {
			try {
				String[] selected = visibleObjects.split(",");
				for (int i = 0; i < selected.length; i++) {
					int id = Integer.parseInt(selected[i]);
					mapResult.put(id, linkedValues.get(id));
				}
			}
			catch (Exception exc) {
				mapResult.clear();
				logger.warn(exc);
			}
		}
		return mapResult;
	}

	/**
	 * @param prefs
	 *            prefs the {@link Preferences} object to save to
	 * @param mapVisibleObjects
	 *            the actual visible map of object
	 * @param key
	 *            the property key
	 * @return true if ok, false if something goes wrong.
	 */
	public static boolean saveVisibleObjects(final Preferences prefs, final Map<Integer, String> mapVisibleObjects, final String key) {
		if (mapVisibleObjects == null) {
			return false;
		}
		StringBuilder selected = new StringBuilder();
		if (!mapVisibleObjects.isEmpty()) {
			final Iterator<Integer> it = mapVisibleObjects.keySet().iterator();
			int i = 0;
			while (it.hasNext()) {
				if (i > 0) {
					selected.append(",");
				}
				selected.append(it.next());
				i++;
			}
		}
		prefs.put(key, selected.toString());
		return true;
	}

	/**
	 * convenient access methods
	 */

	public static long getLastUserId() {
		return FilterUtil.getNodeCurrentDb().getLong(FilterUtil.KEY_LAST_USER_ID, FilterUtil.KEY_LAST_USER_ID_DEFAULT);
	}

	public static String getLastUserName() {
		return FilterUtil.getNodeCurrentDb().get(FilterUtil.KEY_LAST_USER_NAME, FilterUtil.KEY_LAST_USER_NAME_DEFAULT);
	}

	/**
	 * @param clazz
	 *            orice clasa
	 * @param sufix
	 *            un String ce specifica ce anume se doreste (alinierile, dimensiunile sau coloanele vizibile)
	 * @param sufix2
	 *            o terminatie oarecare, de regula un nr sau un nume de tabela, pt cazul in care intr-o clasa sunt mai multe grid-uri. Valoarea <code>null</code> indica faptul ca nu se va folosi
	 *            valoarea, si (probabil) avem setari salvate pe un singur grid, in cadrul clasei.
	 * @return o cheie ce mapeaza o anumita valoare (setare), salvata pe nodul {@link #getNodeCoordsWTables()}, de genul
	 *         <p>
	 *         /Bone/Conta/Situatie/Conturi/Filter#cols.widths
	 *         </p>
	 */
	private static String getGridKey(final Class<?> clazz, final String sufix, final String sufix2) {
		String key = clazz.getSimpleName().concat("#").concat(sufix);
		if (sufix2 != null) {
			key = key.concat("#").concat(sufix2);
		}
		return key;
	}

	/**
	 * @param dims
	 *            un array de integer, ce contine dimensiunile in pixeli ale coloanelor grid-ului
	 * @param clazz
	 *            clasa in care e declarat grid-ul. Atentie : doar numele simplu al clasei, nu calea canonica, pt a evita limitarea de 80 de caractere impusa pe cheia din
	 *            {@link Preferences#MAX_KEY_LENGTH}.
	 *            <p>
	 *            A se apela metoda asta daca exista un singur grid pe clasa param. In caz contrar, vezi {@link #saveDims(int[], Class, String)}
	 *            </p>
	 */
	public static void saveDims(final int[] dims, final Class<?> clazz) {
		FilterUtil.saveDims(dims, clazz, null);
	}

	/**
	 * @param dims
	 *            un array de integer, ce contine dimensiunile in pixeli ale coloanelor grid-ului
	 * @param clazz
	 *            clasa in care e declarat grid-ul. Atentie : doar numele simplu al clasei, nu calea canonica, pt a evita limitarea de 80 de caractere impusa pe cheia din
	 *            {@link Preferences#MAX_KEY_LENGTH}.
	 * @param sufix2
	 *            un string cu rol de delimitator, daca pe clasa param tb sa salvam setarile mai multor grid-uri. Poate fi null.
	 */
	public static void saveDims(final int[] dims, final Class<?> clazz, final String sufix2) {
		Preferences prefs = FilterUtil.getNodeCoordsWTables();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < dims.length; i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(String.valueOf(dims[i]));
		}
		prefs.put(FilterUtil.getGridKey(clazz, FilterUtil.GRID_COLS_WIDTHS, sufix2), sb.toString());
	}

	public static int[] getSavedGridDims(final int colsLength, final Class<?> clazz) {
		return FilterUtil.getSavedGridDims(colsLength, clazz, null);
	}

	public static int[] getSavedGridDims(final int colsLength, final Class<?> clazz, final String sufix2) {
		if (!FiltruAplicatie.isTableUsingPrefs()) {
			int[] dims = new int[colsLength];
			Arrays.fill(dims, 100);
			return dims;
		}
		String prop = FilterUtil.getNodeCoordsWTables().get(FilterUtil.getGridKey(clazz, FilterUtil.GRID_COLS_WIDTHS, sufix2), "");
		String[] colsDims = prop.split(",");
		int[] dims = new int[colsLength];
		if ((colsDims.length != colsLength) || ((colsDims.length == 1) && StringUtils.isEmpty(colsDims[0]))) {
			Arrays.fill(dims, 100);
			return dims;
		}
		try {
			for (int i = 0; i < colsDims.length; i++) {
				dims[i] = Integer.parseInt(colsDims[i]);
			}
		}
		catch (NumberFormatException exc) {
			logger.warn(exc);
			Arrays.fill(dims, 100);
			return dims;
		}
		return dims;
	}

	/**
	 * @param orderArray
	 *            un array de integer, ce contine ordinea coloanelor
	 * @param clazz
	 *            clasa in care e declarat grid-ul. Atentie : doar numele simplu al clasei, nu calea canonica, pt a evita limitarea de 80 de caractere impusa pe cheia din
	 *            {@link Preferences#MAX_KEY_LENGTH}.
	 *            <p>
	 *            A se apela metoda asta daca exista un singur grid pe clasa param. In caz contrar, vezi {@link #saveOrder(int[], Class, String)}
	 *            </p>
	 */
	public static void saveOrder(final int[] orderArray, final Class<?> clazz) {
		FilterUtil.saveOrder(orderArray, clazz, null);
	}

	/**
	 * @param orderArray
	 *            un array de integer, ce contine ordinea coloanelor
	 * @param clazz
	 *            clasa in care e declarat grid-ul. Atentie : doar numele simplu al clasei, nu calea canonica, pt a evita limitarea de 80 de caractere impusa pe cheia din
	 *            {@link Preferences#MAX_KEY_LENGTH}.
	 * @param sufix2
	 *            un string cu rol de delimitator, daca pe clasa param tb sa salvam setarile mai multor grid-uri. Poate fi null.
	 */
	public static void saveOrder(final int[] orderArray, final Class<?> clazz, final String sufix2) {
		Preferences prefs = FilterUtil.getNodeCoordsWTables();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < orderArray.length; i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(String.valueOf(orderArray[i]));
		}
		prefs.put(FilterUtil.getGridKey(clazz, FilterUtil.GRID_COLS_ORDER, sufix2), sb.toString());
	}

	public static int[] getSavedGridColumnOrder(final int colsLength, final Class<?> clazz) {
		return FilterUtil.getSavedGridColumnOrder(colsLength, clazz, null);
	}

	public static int[] getSavedGridColumnOrder(final int colsLength, final Class<?> clazz, final String sufix2) {
		if (!FiltruAplicatie.isTableUsingPrefs()) {
			int[] order = new int[colsLength];
			for (int i = 0; i < colsLength; i++) {
				order[i] = i;
			}
			return order;
		}
		String prop = FilterUtil.getNodeCoordsWTables().get(FilterUtil.getGridKey(clazz, FilterUtil.GRID_COLS_ORDER, sufix2), "");
		String[] colsOrder = prop.split(",");
		int[] order = new int[colsLength];
		if ((colsOrder.length != colsLength) || ((colsOrder.length == 1) && StringUtils.isEmpty(colsOrder[0]))) {
			for (int i = 0; i < colsLength; i++) {
				order[i] = i;
			}
			return order;
		}
		try {
			for (int i = 0; i < colsOrder.length; i++) {
				int ordine = Integer.parseInt(colsOrder[i]);
				if ((ordine < 0) || (ordine > colsLength)) {
					for (int j = 0; j < colsLength; j++) {
						order[j] = j;
					}
					return order;
				}
				order[i] = ordine;
			}
		}
		catch (NumberFormatException exc) {
			logger.warn(exc);
			for (int i = 0; i < colsLength; i++) {
				order[i] = i;
			}
			return order;
		}
		return order;
	}

	public static boolean[] getSavedVisibleCols(final int colsLength, final Class<?> clazz) {
		return FilterUtil.getSavedVisibleCols(colsLength, clazz, null);
	}

	public static boolean[] getSavedVisibleCols(final int colsLength, final Class<?> clazz, final String sufix2) {
		if (!FiltruAplicatie.isTableUsingPrefs()) {
			boolean[] visibleCols = new boolean[colsLength];
			Arrays.fill(visibleCols, true);
			return visibleCols;
		}
		String prop = FilterUtil.getNodeCoordsWTables().get(FilterUtil.getGridKey(clazz, FilterUtil.GRID_VISIBLE_COLS, sufix2), "");
		char[] visibleCols = prop.toCharArray();
		boolean[] b = new boolean[colsLength];
		if (visibleCols.length != colsLength) {
			Arrays.fill(b, true);
			return b;
		}
		for (int i = 0; i < visibleCols.length; i++) {
			b[i] = (visibleCols[i] == '1') ? true : false;
		}
		return b;
	}

	public static void saveVisibleCols(final boolean[] visibleCols, final Class<?> clazz) {
		FilterUtil.saveVisibleCols(visibleCols, clazz, null);
	}

	public static void saveVisibleCols(final boolean[] visibleCols, final Class<?> clazz, final String sufix2) {
		Preferences prefs = FilterUtil.getNodeCoordsWTables();
		StringBuilder sb = new StringBuilder();
		for (boolean b : visibleCols) {
			sb.append(b ? "1" : "0");
		}
		prefs.put(FilterUtil.getGridKey(clazz, FilterUtil.GRID_VISIBLE_COLS, sufix2), sb.toString());
	}

	public static void saveAligns(final int[] aligns, final Class<?> clazz) {
		FilterUtil.saveAligns(aligns, clazz, null);
	}

	public static void saveAligns(final int[] aligns, final Class<?> clazz, final String sufix2) {
		Preferences prefs = FilterUtil.getNodeCoordsWTables();
		final boolean[] visible = FilterUtil.getSavedVisibleCols(aligns.length, clazz, sufix2);
		final int[] savedAligns = FilterUtil.getSavedGridAligns(aligns.length, clazz, sufix2);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < aligns.length; i++) {
			if (i > 0) {
				sb.append(",");
			}
			if (visible[i]) {
				sb.append(String.valueOf(aligns[i]));
			} else {
				sb.append(String.valueOf(savedAligns[i]));
			}
		}
		prefs.put(FilterUtil.getGridKey(clazz, FilterUtil.GRID_COLS_ALIGNS, sufix2), sb.toString());
	}

	public static int[] getSavedGridAligns(final int colsLength, final Class<?> clazz) {
		return FilterUtil.getSavedGridAligns(colsLength, clazz, null);
	}

	public static int[] getSavedGridAligns(final int colsLength, final Class<?> clazz, final String sufix2) {
		if (!FiltruAplicatie.isTableUsingPrefs()) {
			int[] aligns = new int[colsLength];
			Arrays.fill(aligns, SWT.LEFT);
			return aligns;
		}
		String prop = FilterUtil.getNodeCoordsWTables().get(FilterUtil.getGridKey(clazz, FilterUtil.GRID_COLS_ALIGNS, sufix2), "");
		String[] colsAligns = prop.split(",");
		int[] dims = new int[colsLength];
		if ((colsAligns.length != colsLength) || ((colsAligns.length == 1) && StringUtils.isEmpty(colsAligns[0]))) {
			Arrays.fill(dims, SWT.LEFT);
			return dims;
		}
		try {
			for (int i = 0; i < colsAligns.length; i++) {
				dims[i] = Integer.parseInt(colsAligns[i]);
			}
		}
		catch (NumberFormatException exc) {
			logger.warn(exc);
			Arrays.fill(dims, SWT.LEFT);
			return dims;
		}
		return dims;
	}

	public static WindowSetting getWindowSetting(String windowKey) {
	    return settingRepository.getWindowSetting(windowKey, EncodeLive.getIdUser());
    }

	public static void saveWindowCoords(Rectangle bounds, String windowKey){
        WindowSetting setting = getWindowSetting(windowKey);
        if (setting == null) {
            setting = new WindowSetting();
        }
        setting.setX(bounds.x);
        setting.setY(bounds.y);
        setting.setWidth(bounds.width);
        setting.setHeight(bounds.height);
        setting.setWindowKey(windowKey);
        settingRepository.save(setting);
    }
}
