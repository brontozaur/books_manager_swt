package com.papao.books.export.txt;

import com.papao.books.export.AbstractExportView;
import com.papao.books.view.AppImages;
import com.papao.books.view.auth.EncodeLive;
import com.papao.books.view.custom.DirectorySelectorComposite;
import com.papao.books.view.interfaces.AbstractIConfigAdapter;
import com.papao.books.view.interfaces.ConfigurationException;
import com.papao.books.view.interfaces.IConfig;
import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.util.FontUtil;
import com.papao.books.view.util.WidgetCompositeUtil;
import com.papao.books.view.util.WidgetCursorUtil;
import com.papao.books.view.view.AbstractView;
import com.papao.books.view.view.ColumnsChooserComposite;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;

public class ExportTxtOptionsView extends AbstractExportView {

	private final static String ITEM_OPTIONS = "Optiuni export";
	private final static String ITEM_TABLE_COLS = "Coloane selectabile";
	private final static String[] ITEMS = new String[] {
			ExportTxtOptionsView.ITEM_OPTIONS, ExportTxtOptionsView.ITEM_TABLE_COLS };

	private final ExportTxtSettings settings;
	private Button buttonExportPathAuto;

	public ExportTxtOptionsView(final Shell parent, final ExportTxtSettings settings) {
		super(parent);

		this.settings = settings;

		for (String str : ExportTxtOptionsView.ITEMS) {
			new TableItem(this.leftTable, SWT.NONE).setText(str);
		}

		this.mapComponents.put(ExportTxtOptionsView.ITEM_OPTIONS, new ExportSettings());
		this.mapComponents.put(ExportTxtOptionsView.ITEM_TABLE_COLS, new TableColsSettings());

		this.leftTable.select(0);
		this.leftTable.notifyListeners(SWT.Selection, new Event());
	}

	@Override
	public void actionPerformed(final String catName) {
		this.rightForm.setContent((Composite) this.mapComponents.get(catName));
	}

	@Override
	public void customizeView() {
		setShellText("Optiuni export text");
		setShellStyle(SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE);
		setShellImage(AppImages.getImage16(AppImages.IMG_EXPORT));
		setViewOptions(AbstractView.ADD_CANCEL | AbstractView.ADD_OK);
		setBigViewImage(AppImages.getImage24(AppImages.IMG_EXPORT));
		setBigViewMessage("Configurare export date in format text");
		setShowSaveOKMessage(false);
	}

	@Override
	public void reset() {
		ExportTxtPrefs.reset();
		for (IConfig cfg : this.mapComponents.values()) {
			cfg.populateFields();
		}
	}

	private class ExportSettings extends AbstractIConfigAdapter {
		private Text textFileName;
		private DirectorySelectorComposite dsc;
		private Button buttonShowNrCrt;
		private Button buttonShowBorder;
		private Button buttonShowTitle;
		private Text textTitleName;

		protected ExportSettings() {
			super(ExportTxtOptionsView.this.rightForm);
			GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(10,
					5,
					SWT.DEFAULT,
					SWT.DEFAULT).applyTo(this);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
			createContents();
			populateFields();
		}

		@Override
		public final void createContents() {
			Group groupOptions;
			GridData gd;
			CLabel labelName;
			Label temp;

			labelName = new CLabel(this, SWT.BORDER);
			labelName.setBackground(ColorUtil.COLOR_ALBASTRU_FACEBOOK);
			labelName.setForeground(ColorUtil.COLOR_WHITE);
			labelName.setText(ExportTxtOptionsView.ITEM_OPTIONS);
			labelName.setFont(FontUtil.TAHOMA12_BOLD);
			GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(labelName);

			groupOptions = new Group(this, SWT.NONE);
			groupOptions.setText("Optiuni export");
			gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
			groupOptions.setLayoutData(gd);
			groupOptions.setLayout(new GridLayout(1, true));

			temp = new Label(groupOptions, SWT.NONE);
			temp.setText("Nume fisier");

			this.textFileName = new Text(groupOptions, SWT.BORDER);
			GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(false, false).hint(250,
					SWT.DEFAULT).minSize(250, SWT.DEFAULT).applyTo(this.textFileName);
			this.textFileName.addListener(SWT.FocusIn, this);
			this.textFileName.addListener(SWT.Modify, this);

			ExportTxtOptionsView.this.buttonExportPathAuto = new Button(groupOptions, SWT.CHECK);
			ExportTxtOptionsView.this.buttonExportPathAuto.setText("cale automata export");
			ExportTxtOptionsView.this.buttonExportPathAuto.addListener(SWT.Selection, this);

			this.dsc = new DirectorySelectorComposite(groupOptions, false);

			this.buttonShowNrCrt = new Button(groupOptions, SWT.CHECK);
			this.buttonShowNrCrt.setText("Afisare coloana pentru numar curent");
			WidgetCursorUtil.addHandCursorListener(this.buttonShowNrCrt);
			this.buttonShowNrCrt.addListener(SWT.Selection, this);

			this.buttonShowBorder = new Button(groupOptions, SWT.CHECK);
			this.buttonShowBorder.setText("Afisare margini celule");
			WidgetCursorUtil.addHandCursorListener(this.buttonShowBorder);

			this.buttonShowTitle = new Button(groupOptions, SWT.CHECK);
			this.buttonShowTitle.setText("Afisare denumire raport");
			WidgetCursorUtil.addHandCursorListener(this.buttonShowTitle);
			this.buttonShowTitle.addListener(SWT.Selection, this);

			this.textTitleName = new Text(groupOptions, SWT.BORDER);
			GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(false, false).hint(250,
					SWT.DEFAULT).minSize(250, SWT.DEFAULT).applyTo(this.textTitleName);
		}

		@Override
		public final void populateFields() {
			this.textFileName.setText(ExportTxtOptionsView.this.settings.getNumeFisier());
			ExportTxtOptionsView.this.buttonExportPathAuto.setSelection(ExportTxtPrefs.isUsingAutoExportPath());
			if (ExportTxtOptionsView.this.buttonExportPathAuto.getSelection()) {
				this.dsc.setDirPath(EncodeLive.getReportsDir());
			} else {
				this.dsc.setDirPath(ExportTxtPrefs.getExportPath());
			}
			this.dsc.getItemSelectie().setEnabled(!ExportTxtOptionsView.this.buttonExportPathAuto.getSelection());

			this.buttonShowNrCrt.setSelection(ExportTxtPrefs.isShowingNrCrt());
			this.buttonShowTitle.setSelection(ExportTxtPrefs.isUsingTitle());
			this.textTitleName.setText(ExportTxtOptionsView.this.settings.getTitlu());
			this.textTitleName.setEnabled(this.buttonShowTitle.getSelection());
			this.buttonShowBorder.setSelection(ExportTxtPrefs.isHavingBorder());
		}

		@Override
        public final void save() throws ConfigurationException {
			String numeFisier = this.textFileName.getText();
            if (StringUtils.isEmpty(numeFisier)) {
				numeFisier = "RaportTXT_" + System.currentTimeMillis();
			}
			if (!ExportTxtOptionsView.this.buttonExportPathAuto.getSelection()) {
				ExportTxtPrefs.put(ExportTxtPrefs.TXT_EXPORT_PATH, this.dsc.getSelectedDirPath());
			}
			ExportTxtPrefs.putBoolean(ExportTxtPrefs.TXT_EXPORT_PATH_AUTO,
					ExportTxtOptionsView.this.buttonExportPathAuto.getSelection());
			ExportTxtOptionsView.this.settings.setNumeFisier(this.dsc.getSelectedDirPath().concat(numeFisier));
			ExportTxtPrefs.putBoolean(ExportTxtPrefs.TXT_IS_SHOWING_NR_CRT,
					this.buttonShowNrCrt.getSelection());
			ExportTxtPrefs.putBoolean(ExportTxtPrefs.TXT_IS_USING_TITLE,
					this.buttonShowTitle.getSelection());
			ExportTxtOptionsView.this.settings.setTitlu(this.textTitleName.getText());
			ExportTxtPrefs.putBoolean(ExportTxtPrefs.TXT_TABLE_HAS_BORDER,
					this.buttonShowBorder.getSelection());
		}

		@Override
		public String getCatName() {
			return ExportTxtOptionsView.ITEM_OPTIONS;
		}

		@Override
		public final void handleEvent(final Event e) {
			if (e.type == SWT.Selection) {
				if (e.widget == this.buttonShowNrCrt) {
					if (this.buttonShowNrCrt.getSelection()) {
						updateDetailMessage("Prima coloana a raportului va afisa numarul curent al elementelor.");
					} else {
						updateDetailMessage("Nu se vor numerota elementele afisate.");
					}
				} else if (e.widget == this.buttonShowTitle) {
					this.textTitleName.setEnabled(this.buttonShowTitle.getSelection());
				} else if (e.widget == ExportTxtOptionsView.this.buttonExportPathAuto) {
					if (ExportTxtOptionsView.this.buttonExportPathAuto.getSelection()) {
						this.dsc.setDirPath(EncodeLive.getReportsDir().concat(File.separator));
					} else {
						this.dsc.setDirPath(ExportTxtPrefs.getExportPath());
					}
					this.dsc.getItemSelectie().setEnabled(!ExportTxtOptionsView.this.buttonExportPathAuto.getSelection());
				}
			} else if (e.type == SWT.FocusIn) {
				if (e.widget == this.textFileName) {
					updateDetailMessage("Numele fisierului care va fi exportat.");
				}
			}
		}
	}

	private class TableColsSettings extends AbstractIConfigAdapter {

		private ColumnsChooserComposite chooser;

		protected TableColsSettings() {
			super(ExportTxtOptionsView.this.rightForm);
			GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(10,
					5,
					SWT.DEFAULT,
					SWT.DEFAULT).applyTo(this);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
			createContents();
		}

		@Override
		public final void createContents() {
			CLabel labelName;

			labelName = new CLabel(this, SWT.BORDER);
			labelName.setBackground(ColorUtil.COLOR_ALBASTRU_FACEBOOK);
			labelName.setForeground(ColorUtil.COLOR_WHITE);
			labelName.setText(ExportTxtOptionsView.ITEM_TABLE_COLS);
			labelName.setFont(FontUtil.TAHOMA12_BOLD);
			GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(labelName);

			this.chooser = new ColumnsChooserComposite(
				this,
				ExportTxtOptionsView.this.settings.getSwtTable(),
				ExportTxtOptionsView.this.settings.getClazz(),
				ExportTxtOptionsView.this.settings.getSufix());

			WidgetCompositeUtil.addColoredFocusListener2Childrens(this);
		}

		@Override
		public final void populateFields() {
			this.chooser.reset();
		}

		@Override
		public String getCatName() {
			return ExportTxtOptionsView.ITEM_TABLE_COLS;
		}

		@Override
		public final boolean validate() {
			return this.chooser.validate();
		}

		@Override
        public final void save() throws ConfigurationException {
			if (!this.chooser.save(false)) {
                throw new ConfigurationException("eroare la salvare selectiei");
			}
			ExportTxtOptionsView.this.settings.setAligns(this.chooser.getAligns());
			ExportTxtOptionsView.this.settings.setDims(this.chooser.getDims());
			ExportTxtOptionsView.this.settings.setSelection(this.chooser.getSelection());
			ExportTxtOptionsView.this.settings.setOrder(this.chooser.getOrder());
		}
	}

	public ExportTxtSettings getSettings() {
		return this.settings;
	}

}