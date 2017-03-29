package com.papao.books.ui.searcheable;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.*;

import java.util.Calendar;
import java.util.Date;

public abstract class AbstractSearchType extends Composite implements Listener {

	private final BorgSearchSystem searchSystem;
	private final String colName;
	public ViewerFilter filter;
	private Label labelNume;
	protected final static Color FILTRU_ACTIV = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
	protected final static Color FILTRU_INACTIV = Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);
	protected final int dateWidgetStyle;

	protected AbstractSearchType(final BorgSearchSystem searchSystem, final String colName) {
		this(searchSystem, colName, -1);
	}

	/**
	 * @param searchSystem
	 * @param colName
	 * @param widgetStyle
	 *            <ul>
	 *            <li>{@link #TYPE_DATE} - will allow only date selection</li>
	 *            <li>{@link #TYPE_TIME} - will allow only time selection</li>
	 *            <li>{@link #TYPE_TIMESTAMP} - will allow date and time selection</li>
	 *            </ul>
	 */
	protected AbstractSearchType(	final BorgSearchSystem searchSystem,
									final String colName,
									final int dateWidgetStyle) {
		super(searchSystem.getCompCriterii(), SWT.NONE);
		this.searchSystem = searchSystem;
		this.colName = colName;
		this.dateWidgetStyle = dateWidgetStyle;

		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).span(1, 1).applyTo(this);
		GridLayoutFactory.fillDefaults().numColumns(4).equalWidth(false).extendedMargins(2, 2, 0, 0).spacing(2,
				0).applyTo(this);

		addComponents();
		setVisible(searchSystem.isColumnVisibleInPrefs(colName));
	}

	private void addComponents() {
		this.labelNume = new Label(this, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).minSize(50, SWT.DEFAULT).hint(50,
				SWT.DEFAULT).grab(false, false).applyTo(this.labelNume);
		this.labelNume.setToolTipText(this.colName);
		if (this.colName.length() <= 8) {
			this.labelNume.setText(this.colName);
		} else {
			this.labelNume.setText(this.colName.substring(0, 8) + ".");
		}

		createContents();

		this.getParent().layout();
		if (this.getParent().getParent() != null) {
			this.getParent().getParent().layout();
		}
		this.addListener(SWT.Dispose, this);
	}

	protected final Label getLabelName() {
		return this.labelNume;
	}

	protected abstract void createContents();

	public abstract boolean isModified();

	public abstract boolean compareValues(final Object valueToBeCompared);

	@Override
	public final void handleEvent(final Event e) {
		if (e.type == SWT.Dispose) {
			if (this.searchSystem.isDisposed() || this.searchSystem.filtreComposite.isDisposed()) {
				return;
			}
			Composite parent = this.getParent();
			this.searchSystem.getDataTransport().getSelectedMap().remove(this.colName.hashCode());
			this.searchSystem.filtreComposite.setTextInfoValue(this.searchSystem.filtreComposite.getDataTransport().getSelectionAsText());

			if ((this.searchSystem.getViewer() != null) && (this.filter != null)) {
				this.searchSystem.getViewer().removeFilter(this.filter);
				this.searchSystem.computeTotal();
			}
			parent.layout();
			if (parent.getParent() != null) {
				parent.getParent().layout();
			}
			this.searchSystem.getCompCriterii().notifyListeners(SWT.Resize, e);
		}
	}

	protected final BorgSearchSystem getSearchSystem() {
		return this.searchSystem;
	}

	protected final static boolean compareNumbers(	final double valMin,
													final double valMax,
													final double valObj) {
		boolean result = true;
		if (valMin != Double.MIN_VALUE) {
			result = valObj >= valMin;
		}
		if (!result) {
			return false;
		}
		if (valMax != Double.MAX_VALUE) {
			result = valObj <= valMax;
		}
		return result;
	}

	protected final static boolean compareJavaSQLDates(	final java.sql.Date dataMin,
														final java.sql.Date dataMax,
														final java.sql.Date dataObj) {
		if ((dataMin == null) && (dataMax == null)) {
			return true;
		}
		if (dataObj == null) {
			return true;
		}
		Calendar calObj = Calendar.getInstance();
		calObj.setTime(dataObj);
		calObj.set(Calendar.HOUR_OF_DAY, 0);
		calObj.set(Calendar.MINUTE, 0);
		calObj.set(Calendar.SECOND, 0);
		calObj.set(Calendar.MILLISECOND, 0);

		boolean result = true;
		if (dataMin != null) {
			Calendar calMin = Calendar.getInstance();
			calMin.setTime(dataMin);
			calMin.set(Calendar.HOUR_OF_DAY, 0);
			calMin.set(Calendar.MINUTE, 0);
			calMin.set(Calendar.SECOND, 0);
			calMin.set(Calendar.MILLISECOND, 0);
			result = calObj.getTimeInMillis() >= calMin.getTimeInMillis();
		}
		if (!result) {
			return false;
		}
		if (dataMax != null) {
			Calendar calMax = Calendar.getInstance();
			calMax.setTime(dataMax);
			calMax.set(Calendar.HOUR_OF_DAY, 0);
			calMax.set(Calendar.MINUTE, 0);
			calMax.set(Calendar.SECOND, 0);
			calMax.set(Calendar.MILLISECOND, 0);
			result = calObj.getTimeInMillis() <= calMax.getTimeInMillis();
		}
		return result;
	}

	protected final static boolean compareJavaUtilDates(final Date dataMin,
														final Date dataMax,
														final Date dataObj) {
		if ((dataMin == null) && (dataMax == null)) {
			return true;
		}
		if (dataObj == null) {
			return true;
		}
		Calendar calObj = Calendar.getInstance();
		calObj.setTime(dataObj);
		calObj.set(Calendar.MILLISECOND, 0);

		boolean result = true;
		if (dataMin != null) {
			Calendar calMin = Calendar.getInstance();
			calMin.setTime(dataMin);
			calMin.set(Calendar.MILLISECOND, 0);
			result = calObj.getTimeInMillis() >= calMin.getTimeInMillis();
		}
		if (!result) {
			return false;
		}
		if (dataMax != null) {
			Calendar calMax = Calendar.getInstance();
			calMax.setTime(dataMax);
			calMax.set(Calendar.MILLISECOND, 0);
			result = calObj.getTimeInMillis() <= calMax.getTimeInMillis();
		}
		return result;
	}

	public final String getColName() {
		return this.colName;
	}
}
