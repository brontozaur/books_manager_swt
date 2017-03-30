package com.papao.books.ui.custom;

import com.papao.books.model.AbstractDB;
import com.papao.books.model.BlankDbObject;
import com.papao.books.util.AtomUtils;
import com.papao.books.util.ObjectUtil;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;

public class AdbSelectorData {

	private static Logger logger = Logger.getLogger(AdbSelectorData.class);

	private String labelName;
	private String[] tableCols;
	private int[] tableDims;
	private String[] getterMethods;
	private String uiTextMethod;
	private String prefsKey;
	private Class<? extends AbstractDB> clazz;
	private Map<String, ? extends AbstractDB> cacheMap = new HashMap<>();
	private Map<String, ? extends AbstractDB> selectedMap = new HashMap<>();
	private String inputShellName;
	private String databaseColumnForFilter;
	public static final String SEPARATOR = ";";
	private boolean addLabel;

	public AdbSelectorData() {

	}

	/**
	 * Numele label-ului care va fi creat inainte de text, daca {@link AdbSelectorData#isAddLabel()}
	 * returneaza true. Altfel, poate fi null.
	 * 
	 * @param labelName
	 */
	public void setLabelName(final String labelName) {
		this.labelName = labelName;
	}

	/**
	 * Numele label-ului care va fi creat inainte de text, daca {@link AdbSelectorData#isAddLabel()}
	 * returneaza true. Altfel, poate fi null.
	 * 
	 * @param
	 */
	public String getLabelName() {
		return this.labelName;
	}

	/**
	 * Array-ul cu coloane, folosit in input-ul de selectie, atat pt headerul coloanelor din tabela,
	 * cat si pt combo-ul de selectie rapida, pt bifarea elementelor in functie de valoarea
	 * introdusa in campul de cautare rapida. Trebuie sa fie de aceeasi dimensiune cu
	 * {@link AdbSelectorData#getGetterMethods()}, pt a invoca o metoda pt fiecare coloana. Nu poate
	 * fi null sau empty.
	 * 
	 * @return
	 */
	public String[] getTableCols() {
		return this.tableCols;
	}

	/**
	 * Array-ul cu coloane, folosit in input-ul de selectie, atat pt headerul coloanelor din tabela,
	 * cat si pt combo-ul de selectie rapida, pt bifarea elementelor in functie de valoarea
	 * introdusa in campul de cautare rapida. Trebuie sa fie de aceeasi dimensiune cu
	 * {@link AdbSelectorData#getGetterMethods()}, pt a invoca o metoda pt fiecare coloana si
	 * {@link AdbSelectorData#getTableDims()}. Nu poate fi null sau empty.
	 * 
	 * @return
	 */
	public void setTableCols(final String[] tableCols) {
		this.tableCols = tableCols;
	}

	public void setTableCols(final String tableColumn) {
		setTableCols(new String[] {
			tableColumn });
	}

	/**
	 * Array-ul cu dimensiunile coloanelor din tabela de pe input-ul de selectie. Trebuie sa fie de
	 * aceeasi dimensiune cu {@link AdbSelectorData#getGetterMethods()} si
	 * {@link AdbSelectorData#getTableCols()}. Nu poate fi null sau empty.
	 * 
	 * @return
	 */
	public int[] getTableDims() {
		return this.tableDims;
	}

	/**
	 * Array-ul cu dimensiunile coloanelor din tabela de pe input-ul de selectie. Trebuie sa fie de
	 * aceeasi dimensiune cu {@link AdbSelectorData#getGetterMethods()} si
	 * {@link AdbSelectorData#getTableCols()}. Nu poate fi null sau empty.
	 * 
	 * @return
	 */
	public void setTableDims(final int[] tableDims) {
		this.tableDims = tableDims;
	}

	/**
	 * Array-ul cu metodele obiectului specificat prin {@link AdbSelectorData#setClazz(Class)}.
	 * Acestea vor fi invocate reflexiv ca sa afiseze proprietatile dbo-ului, in tabela din input-ul
	 * de selectie. Trebuie sa fie de aceeasi dimensiune cu {@link AdbSelectorData#getTableDims()}
	 * si {@link AdbSelectorData#getTableCols()}. Nu poate fi null sau empty.
	 * 
	 * @return
	 */
	public String[] getGetterMethods() {
		return this.getterMethods;
	}

	/**
	 * Array-ul cu metodele obiectului specificat prin {@link AdbSelectorData#setClazz(Class)}.
	 * Acestea vor fi invocate reflexiv ca sa afiseze proprietatile dbo-ului, in tabela din input-ul
	 * de selectie. Trebuie sa fie de aceeasi dimensiune cu {@link AdbSelectorData#getTableDims()}
	 * si {@link AdbSelectorData#getTableCols()}. Nu poate fi null sau empty.
	 * 
	 * @return
	 */
	public void setGetterMethods(final String[] methods) {
		this.getterMethods = methods;
	}

	/**
	 * Clasa unui obiect ce extinde {@link AbstractDB}.
	 * 
	 * @return
	 */
	public Class<? extends AbstractDB> getClazz() {
		return this.clazz;
	}

	/**
	 * Clasa unui obiect ce extinde {@link AbstractDB}.
	 * 
	 * @return
	 */
	public void setClazz(final Class<? extends AbstractDB> clazz) {
		this.clazz = clazz;
	}

	/**
	 * Multimea elementelor care vor forma inregistrarile din input-ul de selectie. De regula se va
	 * prefera obtinerea ei dintr-un cache, gen {@link }, pt a se diminua traficul cu baza
	 * de date.
	 * 
	 * @return
	 */
	public Map<String, ? extends AbstractDB> getCacheMap() {
		return this.cacheMap;
	}

	/**
	 * Multimea elementelor care vor forma inregistrarile din input-ul de selectie. De regula se va
	 * prefera obtinerea ei dintr-un cache, gen {@link }, pt a se diminua traficul cu baza
	 * de date.
	 * 
	 * @return
	 */
	public void setCacheMap(final Map<String, ? extends AbstractDB> cacheMap) {
		this.cacheMap = cacheMap;
	}

	/**
	 * This will allow the use of another Map with the cached elements, by converting them into
	 * {@link BlankDbObject}. Since the class, the getterMethods, the dims, etc are known, for
	 * convenience, they are attributed here.
	 * 
	 * @param cacheMap
	 */
	public void setCacheMapForString(final Map<String, String> cacheMap) {
		this.clazz = BlankDbObject.class;
		this.getterMethods = new String[] {
			BlankDbObject.EXTERNAL_REFLECT_GET_NAME };
		this.tableDims = new int[] {
			150 };
		this.uiTextMethod = BlankDbObject.EXTERNAL_REFLECT_GET_NAME;
		if ((cacheMap == null) || cacheMap.isEmpty()) {
			this.cacheMap = new HashMap<>();
		} else {
			this.cacheMap.clear();
			Map<String, AbstractDB> tmpMap = new HashMap<>();
			for (Iterator<Map.Entry<String, String>> it = cacheMap.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, String> entry = it.next();
				BlankDbObject dummy = new BlankDbObject(entry.getValue(), entry.getKey());
				tmpMap.put(dummy.getId(), dummy);
			}
			setCacheMap(tmpMap);
		}
	}

	/**
	 * Numele shell-ului din fereastra de selectie inregistrari.
	 * 
	 * @return
	 */
	public String getInputShellName() {
		return this.inputShellName;
	}

	/**
	 * Numele shell-ului din fereastra de selectie inregistrari.
	 * 
	 * @return
	 */
	public void setInputShellName(final String inputShellName) {
		this.inputShellName = inputShellName;
	}

	/**
	 * Un map de obiecte ce extind {@link AbstractDB}, alese de user din inputul de selectie.
	 * Atentie! Mapul contine obiecte intregi, pentru ca se va invoca metoda
	 * {@link AdbSelectorData#getUiTextMethod()} pt a afisa elementele selectate, separate prin
	 * {@link #SEPARATOR}. Metoda nu poate returna un map null. Daca map-ul este gol, sau egal ca
	 * dimensiune cu dimensiunea {@link #getCacheMap()}, filtrul este dezactivat, se considera
	 * echivalente selectia totala cu selectie zero.
	 * 
	 * @return
	 */
	public Map<String, ? extends AbstractDB> getSelectedMap() {
		if (this.selectedMap == null) {
			this.selectedMap = new TreeMap<String, AbstractDB>();
		}
		return this.selectedMap;
	}

	/**
	 * Un map de obiecte ce extind {@link AbstractDB}, alese de user din inputul de selectie.
	 * Atentie! Mapul contine obiecte intregi, pentru ca se va invoca metoda
	 * {@link AdbSelectorData#getUiTextMethod()} pt a afisa elementele selectate, separate prin
	 * {@link #SEPARATOR}. Metoda nu poate returna un map null. Daca map-ul este gol, sau egal ca
	 * dimensiune cu dimensiunea {@link #getCacheMap()}, filtrul este dezactivat, se considera
	 * echivalente selectia totala cu selectie zero.
	 * 
	 * @return
	 */
	public void setSelectedMap(final Map<String, ? extends AbstractDB> selectedMap) {
		this.selectedMap = selectedMap;
	}

	/**
	 * Numele coloanei din baza de date a obiectului de tip {@link #getClazz()}. Va fi folosita la
	 * formarea select-ului in baza de date, pt clauza WHERE {@link #getDatabaseColumnForFilter()}
	 * IN (...). Daca nu se foloseste componenta pt un filtru, nu e nevoie sa fie specificata o
	 * valoare.
	 * 
	 * @return
	 */
	public String getDatabaseColumnForFilter() {
		return this.databaseColumnForFilter;
	}

	/**
	 * Numele coloanei din baza de date a obiectului de tip {@link #getClazz()}. Va fi folosita la
	 * formarea select-ului in baza de date, pt clauza WHERE {@link #getDatabaseColumnForFilter()}
	 * IN (...). Daca nu se foloseste componenta pt un filtru, nu e nevoie sa fie specificata o
	 * valoare.
	 * 
	 * @return
	 */
	public void setDatabaseColumnForFilter(final String databaseColumnForFilter) {
		this.databaseColumnForFilter = databaseColumnForFilter;
	}

	/**
	 * Avand x elemente selectate, se va parcurge lista, si se va forma lista cu numele/codul,etc
	 * elementelor selectate, separate prin {@link #SEPARATOR}. Acest String va fi afisat in text-ul
	 * din {@link AdbSelectorComposite}, pt informarea utilizatorului
	 * 
	 * @return
	 */
	public final String getSelectionAsText() {
		final Map<String, ? extends AbstractDB> selected = getSelectedMap();
		StringBuilder sb = new StringBuilder(500);
		for (Iterator<? extends AbstractDB> it = selected.values().iterator(); it.hasNext();) {
			AbstractDB adb = it.next();
			String adbName = null;
			Method meth;
			try {
				meth = ObjectUtil.getMethod(getClazz(), getUiTextMethod());
				adbName = (String) meth.invoke(adb, (Object[]) null);
				sb.append(adbName).append(AdbSelectorData.SEPARATOR);
			}
			catch (Exception exc) {
				logger.error(exc.getMessage(), exc);
			}
		}
		return sb.toString();
	}

	/**
	 * Numele metodei care va fi invocata pe tipul {@link #getClazz()} pentru a forma
	 * {@link AdbSelectorData#getSelectionAsText()} string. De regula "getCod", sau "getName", etc.,
	 * in functie de obiect.
	 * 
	 * @return
	 */
	public final String getUiTextMethod() {
		return this.uiTextMethod;
	}

	/**
	 * Numele metodei care va fi invocata pe tipul {@link #getClazz()} pentru a forma
	 * {@link AdbSelectorData#getSelectionAsText()} string. De regula "getCod", sau "getName", etc.,
	 * in functie de obiect.
	 * 
	 * @return
	 */
	public final void setUiTextMethod(final String uiTextMethod) {
		this.uiTextMethod = uiTextMethod;
	}

	/**
	 * Numele nodului din cadrul API-ului {@link Preferences}. E folosit atat pt a salva id-urile
	 * elementelor selectate, cat si pt recitirea acestora, de catre un {@link }.
	 * 
	 * @return
	 */
	public final String getPrefsKey() {
		return this.prefsKey;
	}

	/**
	 * Numele nodului din cadrul API-ului {@link Preferences}. E folosit atat pt a salva id-urile
	 * elementelor selectate, cat si pt recitirea acestora, de catre un {@link }.
	 * 
	 * @return
	 */
	public final void setPrefsKey(final String prefsKey) {
		this.prefsKey = prefsKey;
	}

	/**
	 * Daca e true, se va crea un label pe composite-ul {@link AdbSelectorComposite}, avand numele
	 * {@link #getLabelName()}
	 * 
	 * @return
	 */
	public final boolean isAddLabel() {
		return this.addLabel;
	}

	/**
	 * Daca e true, se va crea un label pe composite-ul {@link AdbSelectorComposite}, avand numele
	 * {@link #getLabelName()}
	 * 
	 * @return
	 */
	public final void setAddLabel(final boolean addLabel) {
		this.addLabel = addLabel;
	}

	public static AdbSelectorData getModuleData(final boolean addLabel) {
		AdbSelectorData data = new AdbSelectorData();
		data.setCacheMapForString(AtomUtils.mapModules);
		if (addLabel) {
			data.setAddLabel(true);
			data.setLabelName("Module");
		}
		data.setInputShellName("Selectie module");
		data.setTableCols("Module");
		return data;
	}
}