package com.papao.books.view.searcheable;

import com.papao.books.StringSetting;
import com.papao.books.controller.SettingsController;
import org.aspencloud.widgets.ACW;
import org.aspencloud.widgets.cdatepicker.CDatepickerCombo;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeStampSearch extends AbstractSearchType {

    public static final int TYPE_DATE = 1;
    public static final int TYPE_TIME = 2;
    public static final int TYPE_TIMESTAMP = 3;

    private CDatepickerCombo textDataMin;
    private CDatepickerCombo textDataMax;

    /**
     * @param searchSystem
     * @param colName
     * @param widgetStyle  <ul>
     *                     <li>{@link #TYPE_DATE} - will allow only date selection</li>
     *                     <li>{@link #TYPE_TIME} - will allow only time selection</li>
     *                     <li>{@link #TYPE_TIMESTAMP} - will allow date and time selection</li>
     *                     </ul>
     */
    public TimeStampSearch(final BorgSearchSystem searchSystem, final String colName, final int widgetStyle) {
        super(searchSystem, colName, widgetStyle);
    }

    @Override
    protected void createContents() {
        Composite comp;

        final boolean isTimeStamp = this.dateWidgetStyle == TYPE_TIMESTAMP;

        int numCols = 1;
        if (!isTimeStamp) {
            numCols = 3;
        }

        comp = new Composite(this, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(numCols).equalWidth(false).extendedMargins(5, 0, 0, 0).spacing(0, 0).applyTo(comp);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).span(3, 1).applyTo(comp);

        this.textDataMin = new CDatepickerCombo(comp, ACW.BORDER | ACW.DROP_DOWN, Locale.getDefault());
        this.textDataMin.getCDatepicker().setGridVisible(true);
        this.textDataMin.setFormat(getFormat());
        if (isTimeStamp) {
            GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).applyTo(this.textDataMin);
        } else {
            GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(81, SWT.DEFAULT).hint(81, SWT.DEFAULT).applyTo(this.textDataMin);
        }

        if (!isTimeStamp) {
            new Label(comp, SWT.NONE).setText(" - ");
        }

        this.textDataMax = new CDatepickerCombo(comp, ACW.BORDER | ACW.DROP_DOWN, Locale.getDefault());
        this.textDataMax.getCDatepicker().setGridVisible(true);
        this.textDataMax.setFormat(getFormat());
        if (isTimeStamp) {
            GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).applyTo(this.textDataMax);
        } else {
            GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).minSize(81, SWT.DEFAULT).hint(81, SWT.DEFAULT).applyTo(this.textDataMax);
        }

    }

    private String getFormat() {
        switch (this.dateWidgetStyle) {
            case TYPE_DATE: {
                return SettingsController.getString(StringSetting.APP_DATE_FORMAT);
            }
            case TYPE_TIME: {
                return SettingsController.getString(StringSetting.APP_TIME_FORMAT);
            }
            case TYPE_TIMESTAMP: {
                return SettingsController.getString(StringSetting.APP_DATE_FORMAT).concat(" ").concat(SettingsController.getString(StringSetting.APP_DATE_FORMAT));
            }
            default:
                throw new IllegalArgumentException("invalid widget style [" + this.dateWidgetStyle + "]");
        }
    }

    public Object getMin() {
        switch (this.dateWidgetStyle) {
            case TYPE_DATE: {
                if (this.textDataMin.getSelection() == null) {
                    return null;
                }
                return new java.sql.Date(this.textDataMin.getSelection().getTime());
            }
            case TYPE_TIME: {
                if (this.textDataMin.getSelection() == null) {
                    return null;
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(this.textDataMin.getSelection());
                cal.set(Calendar.YEAR, 0);
                cal.set(Calendar.MONTH, 0);
                cal.set(Calendar.DAY_OF_MONTH, 0);
                return new Time(cal.getTime().getTime());
            }
            case TYPE_TIMESTAMP: {
                if (this.textDataMin.getSelection() == null) {
                    return null;
                }
                return new Timestamp(this.textDataMin.getSelection().getTime());
            }
            default:
                throw new IllegalArgumentException("invalid widget style [" + this.dateWidgetStyle + "]");
        }
    }

    public Object getMax() {
        switch (this.dateWidgetStyle) {
            case TYPE_DATE: {
                if (this.textDataMax.getSelection() == null) {
                    return null;
                }
                return new java.sql.Date(this.textDataMax.getSelection().getTime());
            }
            case TYPE_TIME: {
                if (this.textDataMax.getSelection() == null) {
                    return null;
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(this.textDataMax.getSelection());
                cal.set(Calendar.YEAR, 0);
                cal.set(Calendar.MONTH, 0);
                cal.set(Calendar.DAY_OF_MONTH, 0);
                return new Time(cal.getTime().getTime());
            }
            case TYPE_TIMESTAMP: {
                if (this.textDataMax.getSelection() == null) {
                    return null;
                }
                return new Timestamp(this.textDataMax.getSelection().getTime());
            }
            default:
                throw new IllegalArgumentException("invalid widget style [" + this.dateWidgetStyle + "]");
        }
    }

    @Override
    public boolean isModified() {
        final Object tmpMin = getMin();
        final Object tmpMax = getMax();
        if ((tmpMin == null) && (tmpMax == null)) {
            getLabelName().setForeground(AbstractSearchType.FILTRU_INACTIV);
            return false;
        }
        if (tmpMin == null) {
            boolean result = tmpMax != null;
            getLabelName().setForeground(result ? AbstractSearchType.FILTRU_ACTIV : AbstractSearchType.FILTRU_INACTIV);
            return result;
        }
        if (tmpMax == null) {
            getLabelName().setForeground(AbstractSearchType.FILTRU_ACTIV);
            return true;
        }
        boolean result = false;
        if (this.dateWidgetStyle == TYPE_DATE) {
            Calendar calMin = Calendar.getInstance();
            calMin.setTime((Date) tmpMin);
            calMin.set(Calendar.HOUR_OF_DAY, 0);
            calMin.set(Calendar.MINUTE, 0);
            calMin.set(Calendar.SECOND, 0);
            calMin.set(Calendar.MILLISECOND, 0);

            Calendar calMax = Calendar.getInstance();
            calMax.setTime((Date) tmpMax);
            calMax.set(Calendar.HOUR_OF_DAY, 0);
            calMax.set(Calendar.MINUTE, 0);
            calMax.set(Calendar.SECOND, 0);
            calMax.set(Calendar.MILLISECOND, 0);

            result = calMin.before(calMax);
        } else if (this.dateWidgetStyle == TYPE_TIMESTAMP) {
            Calendar calMin = Calendar.getInstance();
            calMin.setTime((Date) tmpMin);
            calMin.set(Calendar.MILLISECOND, 0);

            Calendar calMax = Calendar.getInstance();
            calMax.setTime((Date) tmpMax);
            calMax.set(Calendar.MILLISECOND, 0);
            result = calMin.before(calMax);
        } else {
            result = ((Date) tmpMin).before((Date) tmpMax);
        }
        getLabelName().setForeground(result ? AbstractSearchType.FILTRU_ACTIV : AbstractSearchType.FILTRU_INACTIV);
        return result;
    }

    @Override
    public boolean compareValues(Object valueToBeCompared) {
        if (valueToBeCompared == null) {
            return true;
        }
        switch (this.dateWidgetStyle) {
            case TYPE_DATE: {
                return compareJavaSQLDates((java.sql.Date) getMin(), (java.sql.Date) getMax(), (java.sql.Date) valueToBeCompared);
            }
            case TYPE_TIME:
            case TYPE_TIMESTAMP: {
                return compareJavaUtilDates((Date) getMin(), (Date) getMax(), (Date) valueToBeCompared);
            }
            default:
                throw new IllegalArgumentException("invalid widget style [" + this.dateWidgetStyle + "]");
        }
    }

}
