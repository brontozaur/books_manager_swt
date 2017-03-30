package com.papao.books.ui.bones.filter;

import com.papao.books.auth.EncodeLive;
import com.papao.books.model.AbstractDB;
import com.papao.books.ui.bones.impl.filters.AbstractFilterViewMode;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public abstract class AbstractBoneFilter {

	private static Logger logger = Logger.getLogger(AbstractBoneFilter.class);
	public static final String COL_XDB_DATA = "data";

	private Preferences prefs;
	private final int idBone;
	private final Class<? extends AbstractDB> classObject;

	public AbstractBoneFilter(final Class<? extends AbstractDB> clazz, final int boneId) {
		super();
		this.classObject = clazz;
		this.idBone = boneId;
	}

	public Class<? extends AbstractDB> getClassObject() {
		return this.classObject;
	}

	public final static String INNER_RIGHT_SASH_STYLE = "right.sash.style";
	public final static int INNER_RIGHT_SASH_STYLE_DEFAULT = SWT.VERTICAL;

	public final static String IS_SHOWING_TREE = "is.showing.tree";
	public final static boolean IS_SHOWING_TREE_DEFAULT = true;

	public final static String TREE_VIEW_MODE = "tree.view.mode";
	public final static int TREE_VIEW_MODE_DEFAULT = 0;

	public final static String IS_TREE_SHOWING_ELEMENT_COUNT = "is.tree.showing.element.count";
	public final static boolean IS_TREE_SHOWING_ELEMENT_COUNT_DEFAULT = true;

	public final static String TREE_DATE_STYLE = "tree.date.format";
	public final static int TREE_DATE_STYLE_DEFAULT = AbstractFilterViewMode.AFISARE_FULL;

	public final static String DATA_MIN = "db.data.min";
	public final static String DATA_MIN_DEFAULT = EncodeLive.getSQLDateLoginDayOne().toString();

	public final static String DATA_MAX = "db.data.max";
	public final static String DATA_MAX_DEFAULT = EncodeLive.getSQLDateLogin().toString();

	public final static String MODULE = "db.module";
	public final static String USERS = "db.users";
	public final static String VALUTE = "db.valute";
	public final static String BANCI = "db.banci";
	public final static String NR_DOC = "db.nr.doc";

	public final static String BAR_OPS_STYLE = "bar.ops.style";
	public final static int BAR_OPS_STYLE_DEFAULT = SWT.NONE;

	public final static String LEFT_TREE_ALIGNMENT = "left.tree.align";
	public final static int LEFT_TREE_ALIGNMENT_DEFAULT = SWT.LEFT;

	public final static String BAR_OPS_IS_SHOWING_TEXT = "is.bar.ops.showing.text";
	public final static boolean BAR_OPS_IS_SHOWING_TEXT_DEFAULT = true;

	public final static String DATE_IS_MANAGED_BY_SYSTEM = "db.is.date.managed.by.system";
	public final static boolean DATE_IS_MANAGED_BY_SYSTEM_DEFAULT = true;

	public final static String DATE_USE_DATA_MIN = "db.is.using.data.min";
	public final static boolean DATE_USE_DATA_MIN_DEFAULT = true;

	public final static String DATE_USE_DATA_MAX = "db.is.using.data.max";
	public final static boolean DATE_USE_DATA_MAX_DEFAULT = true;

	public final static String IS_FILTER_ENABLED = "db.is.filter.enabled";
	public final static boolean IS_FILTER_ENABLED_DEFAULT = true;

	public final Preferences getPrefs() {
		return this.prefs;
	}

	public final void put(final String key, final String value) throws IllegalArgumentException {
		getPrefs().put(key, value);
	}

	public final void putBoolean(final String key, final boolean value) throws IllegalArgumentException {
		getPrefs().putBoolean(key, value);
	}

	public final void putByteArray(final String key, final byte[] value) throws IllegalArgumentException {
		getPrefs().putByteArray(key, value);
	}

	public final void putDouble(final String key, final double value) throws IllegalArgumentException {
		getPrefs().putDouble(key, value);
	}

	public final void putFloat(final String key, final float value) throws IllegalArgumentException {
		getPrefs().putFloat(key, value);
	}

	public final void putInt(final String key, final int value) throws IllegalArgumentException {
		getPrefs().putInt(key, value);
	}

	public final void putLong(final String key, final long value) throws IllegalArgumentException {
		getPrefs().putLong(key, value);
	}

	public final void flush() throws BackingStoreException {
		getPrefs().flush();
	}

	public abstract void resetViewMode();

	public abstract void resetDbFilter();

	public abstract void resetWidgetStyle();

	public abstract String parse();

	public final int getInnerRightSashStyle() {
		return getPrefs().getInt(AbstractBoneFilter.INNER_RIGHT_SASH_STYLE, AbstractBoneFilter.INNER_RIGHT_SASH_STYLE_DEFAULT);
	}

	public final boolean isShowingTree() {
		return getPrefs().getBoolean(AbstractBoneFilter.IS_SHOWING_TREE, AbstractBoneFilter.IS_SHOWING_TREE_DEFAULT);
	}

	public final int getTreeViewMode() {
		return getPrefs().getInt(AbstractBoneFilter.TREE_VIEW_MODE, AbstractBoneFilter.TREE_VIEW_MODE_DEFAULT);
	}

	public final boolean isTreeShowingElementCount() {
		return getPrefs().getBoolean(AbstractBoneFilter.IS_TREE_SHOWING_ELEMENT_COUNT, AbstractBoneFilter.IS_TREE_SHOWING_ELEMENT_COUNT_DEFAULT);
	}

	public final int getTreeDateFormatIndex() {
		return getPrefs().getInt(AbstractBoneFilter.TREE_DATE_STYLE, AbstractBoneFilter.TREE_DATE_STYLE_DEFAULT);
	}

	public final String getDataMinima() {
		return getPrefs().get(AbstractBoneFilter.DATA_MIN, AbstractBoneFilter.DATA_MIN_DEFAULT);
	}

	public final String getDataMaxima() {
		return getPrefs().get(AbstractBoneFilter.DATA_MAX, AbstractBoneFilter.DATA_MAX_DEFAULT);
	}

	public final int getBarOpsStyle() {
		return getPrefs().getInt(AbstractBoneFilter.BAR_OPS_STYLE, AbstractBoneFilter.BAR_OPS_STYLE_DEFAULT);
	}

	public final int getTreeAlignment() {
		return getPrefs().getInt(AbstractBoneFilter.LEFT_TREE_ALIGNMENT, AbstractBoneFilter.LEFT_TREE_ALIGNMENT_DEFAULT);
	}

	public final boolean isBarOpsShowingText() {
		return getPrefs().getBoolean(AbstractBoneFilter.BAR_OPS_IS_SHOWING_TEXT, AbstractBoneFilter.BAR_OPS_IS_SHOWING_TEXT_DEFAULT);
	}

	public final boolean isDateManagedBySystem() {
		return getPrefs().getBoolean(AbstractBoneFilter.DATE_IS_MANAGED_BY_SYSTEM, AbstractBoneFilter.DATE_IS_MANAGED_BY_SYSTEM_DEFAULT);
	}

	public final boolean isUsingDataMin() {
		return getPrefs().getBoolean(AbstractBoneFilter.DATE_USE_DATA_MIN, AbstractBoneFilter.DATE_USE_DATA_MIN_DEFAULT);
	}

	public final boolean isUsingDataMax() {
		return getPrefs().getBoolean(AbstractBoneFilter.DATE_USE_DATA_MAX, AbstractBoneFilter.DATE_USE_DATA_MAX_DEFAULT);
	}

	public final boolean isFilterEnabled() {
		return getPrefs().getBoolean(AbstractBoneFilter.IS_FILTER_ENABLED, AbstractBoneFilter.IS_FILTER_ENABLED_DEFAULT);
	}

	public final static String parseKeyDbDataMaxBySystem() {
		StringBuilder parsed = new StringBuilder();
		parsed.append(" AND ");
		parsed.append(AbstractBoneFilter.COL_XDB_DATA);
		parsed.append(" <= '");
		parsed.append(EncodeLive.getSQLDateLogin().toString());
		parsed.append("'");
		return parsed.toString();
	}

	public final static String parseKeyDbDataMinBySystem() {
		StringBuilder parsed = new StringBuilder();
		parsed.append(" AND ");
		parsed.append(AbstractBoneFilter.COL_XDB_DATA);
		parsed.append(" >= '");
		parsed.append(EncodeLive.getSQLDateLoginDayOne().toString());
		parsed.append("'");
		return parsed.toString();
	}
}
