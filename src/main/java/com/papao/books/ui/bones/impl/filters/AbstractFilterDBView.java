package com.papao.books.ui.bones.impl.filters;

import com.papao.books.ui.AppImages;
import com.papao.books.ui.bones.filter.AbstractBoneFilter;
import com.papao.books.ui.bones.impl.view.AbstractCView;
import com.papao.books.ui.custom.AdbSelectorComposite;
import com.papao.books.ui.interfaces.IEncodeReset;
import com.papao.books.ui.util.WidgetCompositeUtil;
import com.papao.books.ui.util.WidgetCursorUtil;
import com.papao.books.ui.view.AbstractView;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public abstract class AbstractFilterDBView extends AbstractCView implements IEncodeReset {

	private static Logger logger = Logger.getLogger(AbstractFilterDBView.class);

	private AbstractBoneFilter filtru;
	private DateChooserCombo comboDataMin;
	private DateChooserCombo comboDataMax;
	private Button buttonEnable;
	private Button buttonFiltruDataDinamic;
	private Button buttonUseDataStart;
	private Button buttonUseDataStop;
	private AdbSelectorComposite userComposite;
	private AdbSelectorComposite modulComposite;
	private AdbSelectorComposite valuteComposite;
	private AdbSelectorComposite banciComposite;
	private Composite groupFiltreSelectie;
	private Composite groupFiltreValori;

	public AbstractFilterDBView(final Shell parent,
								final AbstractBoneFilter filtru,
								final String shellText) {
		super(parent, AbstractView.MODE_NONE);
		setFiltru(filtru);

		parent.setText(shellText);

		((GridLayout) (getContainer().getLayout())).numColumns = 1;

		this.buttonEnable = new Button(getContainer(), SWT.CHECK);
		this.buttonEnable.setText("Utilizeaza filtrul");
		WidgetCursorUtil.addHandCursorListener(this.buttonEnable);
		this.buttonEnable.addListener(SWT.Selection, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				WidgetCompositeUtil.enableGUI(getContainer(), getButtonEnable().getSelection());
				getButtonEnable().setEnabled(true);
				if (getButtonEnable().getSelection()) {
					WidgetCompositeUtil.enableGUI(getButtonFiltruDataDinamic().getParent(),
							!getButtonFiltruDataDinamic().getSelection());
					getButtonFiltruDataDinamic().setEnabled(true);
					if (!getButtonFiltruDataDinamic().getSelection()) {
						AbstractFilterDBView.this.comboDataMin.setEnabled(AbstractFilterDBView.this.buttonUseDataStart.getSelection());
						AbstractFilterDBView.this.comboDataMax.setEnabled(AbstractFilterDBView.this.buttonUseDataStop.getSelection());
					}
				}
			}
		});

		addComponents();

		populateFields();
	}

	public abstract void addComponents();

	public abstract void populateFields();

	@Override
	public void customizeView() {
		setUseCoords(false);
		setUseDocking(false);
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.RESIZE);
		setViewOptions(AbstractView.ADD_CANCEL | AbstractView.ADD_OK);
		setShellImage(AppImages.getImage16(AppImages.IMG_FILTRU));
		setBigViewImage(AppImages.getImage24(AppImages.IMG_FILTRU));
		setShowSaveOKMessage(false);
	}

//
//	public final void addValuteSelection() {
//		throw new UnsupportedOperationException("addValuteSelection() not implemented");
//	}
//
//	public final void populateValute() {
//		this.valuteComposite.populate(getFiltru());
//	}
//
//    public final void saveValute() {
//		this.valuteComposite.save(getFiltru());
//	}
//
//	public final void addDateSelection() {
//		Group compData;
//
//		compData = new Group(getContainer(), SWT.NONE);
//		compData.setText("Perioada afisare");
//		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).margins(5, 5).applyTo(compData);
//		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(compData);
//
//		this.buttonFiltruDataDinamic = new Button(compData, SWT.CHECK);
//		this.buttonFiltruDataDinamic.setText("management automat al perioadei");
//		WidgetCursorUtil.addHandCursorListener(this.buttonFiltruDataDinamic);
//		this.buttonFiltruDataDinamic.setSelection(true);
//		GridDataFactory.fillDefaults().grab(false, false).align(SWT.BEGINNING, SWT.BEGINNING).span(3,
//				1).applyTo(this.buttonFiltruDataDinamic);
//		this.buttonFiltruDataDinamic.addListener(SWT.Selection, new Listener() {
//			@Override
//			public final void handleEvent(final Event e) {
//				if (getButtonFiltruDataDinamic().getSelection()) {
//					getButtonUseDataStart().setSelection(true);
//					getButtonUseDataStop().setSelection(true);
//					getComboDataMin().setValue(EncodeLive.getUtilDateLoginDayOne());
//					getComboDataMax().setValue(EncodeLive.getUtilDateLogin());
//				}
//				WidgetCompositeUtil.enableGUI(getButtonFiltruDataDinamic().getParent(),
//						!getButtonFiltruDataDinamic().getSelection());
//				getButtonFiltruDataDinamic().setEnabled(true);
//			}
//		});
//
//		Label temp = new Label(compData, SWT.NONE);
//		GridDataFactory.fillDefaults().hint(30, SWT.DEFAULT).applyTo(temp);
//
//		this.buttonUseDataStart = new Button(compData, SWT.CHECK);
//		this.buttonUseDataStart.setText("Data minima");
//		WidgetCursorUtil.addHandCursorListener(this.buttonUseDataStart);
//		this.buttonUseDataStart.setSelection(true);
//		this.buttonUseDataStart.addListener(SWT.Selection, new Listener() {
//			@Override
//			public final void handleEvent(final Event e) {
//				getComboDataMin().setEnabled(getButtonUseDataStart().getSelection());
//			}
//		});
//
//		setComboDataMin(new DateChooserCombo(compData, SWT.BORDER));
//		getComboDataMin().setLocale(EncodeLive.ROMANIAN_LOCALE);
//		getComboDataMin().setGridVisible(DateChooser.GRID_NONE);
//		getComboDataMin().setFormatter(new DateFormatter(
//			FiltruAplicatie.getAppDateFormat(),
//			EncodeLive.ROMANIAN_LOCALE));
//		GridDataFactory.fillDefaults().grab(false, false).align(SWT.BEGINNING, SWT.CENTER).applyTo(this.comboDataMin);
//
//		temp = new Label(compData, SWT.NONE);
//		GridDataFactory.fillDefaults().hint(30, SWT.DEFAULT).applyTo(temp);
//
//		this.buttonUseDataStop = new Button(compData, SWT.CHECK);
//		this.buttonUseDataStop.setText("Data maxima");
//		WidgetCursorUtil.addHandCursorListener(this.buttonUseDataStop);
//		this.buttonUseDataStop.setSelection(true);
//		this.buttonUseDataStop.addListener(SWT.Selection, new Listener() {
//			@Override
//			public final void handleEvent(final Event e) {
//				getComboDataMax().setEnabled(getButtonUseDataStop().getSelection());
//			}
//		});
//
//		setComboDataMax(new DateChooserCombo(compData, SWT.BORDER));
//		getComboDataMax().setLocale(EncodeLive.ROMANIAN_LOCALE);
//		getComboDataMax().setGridVisible(DateChooser.GRID_NONE);
//		getComboDataMax().setFormatter(new DateFormatter(
//			FiltruAplicatie.getAppDateFormat(),
//			EncodeLive.ROMANIAN_LOCALE));
//		GridDataFactory.fillDefaults().grab(false, false).align(SWT.BEGINNING, SWT.CENTER).applyTo(this.comboDataMax);
//	}
//
//	public final boolean saveDateSelection() {
//		try {
//			getFiltru().putBoolean(AbstractBoneFilter.DATE_IS_MANAGED_BY_SYSTEM,
//					this.buttonFiltruDataDinamic.getSelection());
//
//			getFiltru().putBoolean(AbstractBoneFilter.DATE_USE_DATA_MIN,
//					this.buttonUseDataStart.getSelection());
//			if (this.buttonFiltruDataDinamic.getSelection()) {
//				getFiltru().put(AbstractBoneFilter.DATA_MIN,
//						EncodeLive.getSQLDateLoginDayOne().toString());
//			} else if (getComboDataMin().getValue() != null) {
//				getFiltru().put(AbstractBoneFilter.DATA_MIN,
//						BorgDateUtil.getFormattedDateStr(getComboDataMin().getValue(),
//								FiltruAplicatie.getAppDateFormat()).toString());
//			} else {
//				getFiltru().put(AbstractBoneFilter.DATA_MIN, "");
//			}
//
//			getFiltru().putBoolean(AbstractBoneFilter.DATE_USE_DATA_MAX,
//					this.buttonUseDataStop.getSelection());
//			if (this.buttonFiltruDataDinamic.getSelection()) {
//				getFiltru().put(AbstractBoneFilter.DATA_MAX,
//						EncodeLive.getSQLDateLogin().toString());
//			} else if (getComboDataMax().getValue() != null) {
//				getFiltru().put(AbstractBoneFilter.DATA_MAX,
//						BorgDateUtil.getFormattedDateStr(getComboDataMax().getValue(),
//								FiltruAplicatie.getAppDateFormat()).toString());
//			} else {
//				getFiltru().put(AbstractBoneFilter.DATA_MAX, "");
//			}
//
//		}
//		catch (Exception exc) {
//			SQLLibrary.processErr(exc, logger);
//			return false;
//		}
//		return true;
//	}
//
//	public final void populateDateValues() {
//		this.buttonFiltruDataDinamic.setSelection(getFiltru().isDateManagedBySystem());
//
//		this.buttonUseDataStart.setSelection(getFiltru().isUsingDataMin());
//		this.buttonUseDataStop.setSelection(getFiltru().isUsingDataMax());
//
//		if (this.buttonFiltruDataDinamic.getSelection()) {
//			getComboDataMin().setValue(EncodeLive.getUtilDateLoginDayOne());
//			getComboDataMax().setValue(EncodeLive.getUtilDateLogin());
//		} else {
//			try {
//				getComboDataMin().setValue(new SimpleDateFormat(
//					FiltruAplicatie.getAppDateFormat(),
//					EncodeLive.ROMANIAN_LOCALE).parse(getFiltru().getDataMinima()));
//			}
//			catch (ParseException exc) {
//				SQLLibrary.processErr(exc, logger);
//				getComboDataMin().setValue(EncodeLive.getUtilDateLoginDayOne());
//			}
//			try {
//				getComboDataMax().setValue(new SimpleDateFormat(
//					FiltruAplicatie.getAppDateFormat(),
//					EncodeLive.ROMANIAN_LOCALE).parse(getFiltru().getDataMaxima()));
//			}
//			catch (ParseException exc) {
//				SQLLibrary.processErr(exc, logger);
//				getComboDataMin().setValue(EncodeLive.getUtilDateLogin());
//			}
//		}
//
//		if (this.buttonEnable.getSelection()) {
//			WidgetCompositeUtil.enableGUI(this.buttonFiltruDataDinamic.getParent(),
//					!this.buttonFiltruDataDinamic.getSelection());
//			WidgetCompositeUtil.enableGUI(this.buttonFiltruDataDinamic.getParent(),
//					!this.buttonFiltruDataDinamic.getSelection());
//			this.buttonFiltruDataDinamic.setEnabled(true);
//		}
//
//		if (this.comboDataMin.getEnabled()) {
//			this.comboDataMin.setEnabled(this.buttonUseDataStart.getSelection());
//		}
//		if (this.comboDataMax.getEnabled()) {
//			this.comboDataMax.setEnabled(this.buttonUseDataStop.getSelection());
//		}
//	}

	@Override
    protected final boolean validate() {
		return true;
	}

	@Override
	public final void reset() {
		getFiltru().resetDbFilter();
		populateFields();
	}

	public AbstractBoneFilter getFiltru() {
		return this.filtru;
	}

	public final void setFiltru(final AbstractBoneFilter filtru) {
		this.filtru = filtru;
	}

	public final Button getButtonEnable() {
		return this.buttonEnable;
	}

	public final void setButtonEnable(final Button buttonEnable) {
		this.buttonEnable = buttonEnable;
	}

	public final Button getButtonFiltruDataDinamic() {
		return this.buttonFiltruDataDinamic;
	}

	public final void setButtonFiltruDataDinamic(final Button buttonFiltruDataDinamic) {
		this.buttonFiltruDataDinamic = buttonFiltruDataDinamic;
	}

	public final DateChooserCombo getComboDataMin() {
		return this.comboDataMin;
	}

	public final void setComboDataMin(final DateChooserCombo comboDataMin) {
		this.comboDataMin = comboDataMin;
	}

	public final DateChooserCombo getComboDataMax() {
		return this.comboDataMax;
	}

	public final void setComboDataMax(final DateChooserCombo comboDataMax) {
		this.comboDataMax = comboDataMax;
	}

	public final Button getButtonUseDataStart() {
		return this.buttonUseDataStart;
	}

	public final void setButtonUseDataStart(final Button buttonUseDataStart) {
		this.buttonUseDataStart = buttonUseDataStart;
	}

	public final Button getButtonUseDataStop() {
		return this.buttonUseDataStop;
	}

	public final void setButtonUseDataStop(final Button buttonUseDataStop) {
		this.buttonUseDataStop = buttonUseDataStop;
	}

	private Composite createPGroup(final String text, final int numCols) {
		final Group group = new Group(getContainer(), SWT.NONE);
		group.setText(text);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).span(2, 1).applyTo(group);
		GridLayoutFactory.fillDefaults().numColumns(numCols).equalWidth(true).spacing(SWT.DEFAULT,
				0).margins(5, 5).applyTo(group);
		return group;
	}

	public Composite getCompFiltreSelectie() {
		if (this.groupFiltreSelectie == null) {
			this.groupFiltreSelectie = createPGroup("Filtre selectie", 1);
		}
		return this.groupFiltreSelectie;
	}

	public Composite getCompFiltreValori() {
		if (this.groupFiltreValori == null) {
			this.groupFiltreValori = createPGroup("Filtre valori", 2);
		}
		return this.groupFiltreValori;
	}
}
