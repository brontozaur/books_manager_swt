package com.papao.books.export.pdf;

import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfWriter;
import com.papao.books.export.AbstractExportView;
import com.papao.books.view.AppImages;
import com.papao.books.view.auth.EncodeLive;
import com.papao.books.view.custom.DirectorySelectorComposite;
import com.papao.books.view.custom.FontSelectorComposite;
import com.papao.books.view.interfaces.AbstractIConfigAdapter;
import com.papao.books.view.interfaces.ConfigurationException;
import com.papao.books.view.interfaces.IConfig;
import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.util.FontUtil;
import com.papao.books.view.util.WidgetCompositeUtil;
import com.papao.books.view.util.WidgetCursorUtil;
import com.papao.books.view.view.AbstractView;
import com.papao.books.view.view.ColumnsChooserComposite;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

public class ExportPdfOptionsView extends AbstractExportView {

    private static final Logger logger = Logger.getLogger(ExportPdfOptionsView.class);
	private final static String ITEM_OPTIONS = "Optiuni export";
	private final static String ITEM_TABLE_COLS = "Coloane selectabile";
	private final static String[] ITEMS = new String[] {
			ExportPdfOptionsView.ITEM_OPTIONS, ExportPdfOptionsView.ITEM_TABLE_COLS };

	private static Map<String, Rectangle> sizes;
	private static String[] PAGE_SIZES;

	static {
		ExportPdfOptionsView.getPageSizes();
	}

	private final ExportPdfSettings settings;
	private Button buttonExportPathAuto;

	public ExportPdfOptionsView(final Shell parent, final ExportPdfSettings settings) {
		super(parent);
		this.settings = settings;

		for (String str : ExportPdfOptionsView.ITEMS) {
			new TableItem(this.leftTable, SWT.NONE).setText(str);
		}

		this.mapComponents.put(ExportPdfOptionsView.ITEM_OPTIONS, new ExportSettings());
		this.mapComponents.put(ExportPdfOptionsView.ITEM_TABLE_COLS, new TableColsSettings());

		this.leftTable.select(0);
		this.leftTable.notifyListeners(SWT.Selection, new Event());
	}

	@Override
	public void customizeView() {
		setShellText("Optiuni export PDF");
		setShellStyle(SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE);
		setShellImage(AppImages.getImage16(AppImages.IMG_ADOBE));
		setViewOptions(AbstractView.ADD_CANCEL | AbstractView.ADD_OK);
		setBigViewImage(AppImages.getImage24(AppImages.IMG_ADOBE));
		setBigViewMessage("Configurare export date in format PDF");
		setShowSaveOKMessage(false);
	}

	@Override
	public void reset() {
		ExportPdfPrefs.reset();
		for (IConfig cfg : this.mapComponents.values()) {
			cfg.populateFields();
		}
	}

	@Override
	public final void actionPerformed(final String catName) {
		this.rightForm.setContent((Composite) this.mapComponents.get(catName));
	}

	private class ExportSettings extends AbstractIConfigAdapter {
		private Button buttonShowTitle;
		private Button buttonShowNrCrt;
		private Button buttonShowHeader;
		private Button buttonShowGradient;
		private Button buttonShowPageNumbers;
		private DirectorySelectorComposite dsc;
		private Text textTitleName;
		private Combo comboPDFVersion;
		private Combo comboOrientation;
		private Combo comboPageSize;
		private FontSelectorComposite fs;
		private Slider slider;
		private Text textFileName;
		private com.itextpdf.text.Font font;

		protected ExportSettings() {
			super(ExportPdfOptionsView.this.rightForm);
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

			labelName = new CLabel(this, SWT.BORDER);
			labelName.setBackground(ColorUtil.COLOR_ALBASTRU_FACEBOOK);
			labelName.setForeground(ColorUtil.COLOR_WHITE);
			labelName.setText(ExportPdfOptionsView.ITEM_OPTIONS);
			labelName.setFont(FontUtil.TAHOMA12_BOLD);
			GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(labelName);

			groupOptions = new Group(this, SWT.NONE);
			groupOptions.setText("Optiuni export");
			gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
			groupOptions.setLayoutData(gd);
			groupOptions.setLayout(new GridLayout(1, true));

			new Label(groupOptions, SWT.NONE).setText("Nume fisier");

			this.textFileName = new Text(groupOptions, SWT.BORDER);
			GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(false, false).hint(250,
					SWT.DEFAULT).minSize(250, SWT.DEFAULT).applyTo(this.textFileName);
			this.textFileName.addListener(SWT.FocusIn, this);
			this.textFileName.addListener(SWT.Modify, this);

			ExportPdfOptionsView.this.buttonExportPathAuto = new Button(groupOptions, SWT.CHECK);
			ExportPdfOptionsView.this.buttonExportPathAuto.setText("cale automata export");
			ExportPdfOptionsView.this.buttonExportPathAuto.addListener(SWT.Selection, this);
			GridDataFactory.fillDefaults().applyTo(ExportPdfOptionsView.this.buttonExportPathAuto);

			this.dsc = new DirectorySelectorComposite(groupOptions, false);

			new Label(groupOptions, SWT.NONE).setText("Orientare");
			this.comboOrientation = new Combo(groupOptions, SWT.BORDER | SWT.READ_ONLY);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(this.comboOrientation);
			this.comboOrientation.setItems(ExportPdfPrefs.ORIENTATIONS);
			this.comboOrientation.addListener(SWT.FocusIn, this);

			new Label(groupOptions, SWT.NONE).setText("Dimensiune");
			this.comboPageSize = new Combo(groupOptions, SWT.BORDER | SWT.READ_ONLY);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(this.comboPageSize);
			this.comboPageSize.setItems(ExportPdfOptionsView.PAGE_SIZES);
			this.comboPageSize.addListener(SWT.FocusIn, this);

			new Label(groupOptions, SWT.NONE).setText("Versiune");
			this.comboPDFVersion = new Combo(groupOptions, SWT.BORDER | SWT.READ_ONLY);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(this.comboPDFVersion);
			this.comboPDFVersion.setItems(ExportPdfPrefs.VERSIONS);
			this.comboPDFVersion.addListener(SWT.FocusIn, this);

			new Label(groupOptions, SWT.NONE).setText("Font tabela");
			this.fs = new FontSelectorComposite(groupOptions);
			this.fs.getTextSelectie().addListener(SWT.FocusIn, this);

			new Label(groupOptions, SWT.NONE).setText("Nivel compresie");
			this.slider = new Slider(groupOptions, SWT.HORIZONTAL);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(this.slider);
			this.slider.setMinimum(0);
			this.slider.setMaximum(100);
			this.slider.setIncrement(10);
			this.slider.setPageIncrement(10);
			this.slider.addListener(SWT.Selection, this);

			this.buttonShowTitle = new Button(groupOptions, SWT.CHECK);
			GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).span(((GridLayout) groupOptions.getLayout()).numColumns,
					1).applyTo(this.buttonShowTitle);
			this.buttonShowTitle.setText("Titlu");
			WidgetCursorUtil.addHandCursorListener(this.buttonShowTitle);
			this.buttonShowTitle.addListener(SWT.Selection, this);

			this.textTitleName = new Text(groupOptions, SWT.BORDER);
			GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(false, false).span(((GridLayout) groupOptions.getLayout()).numColumns - 1,
					1).hint(250, SWT.DEFAULT).minSize(250, SWT.DEFAULT).applyTo(this.textTitleName);
			this.textTitleName.addListener(SWT.FocusIn, this);
			this.textTitleName.setEnabled(false);

			this.buttonShowNrCrt = new Button(groupOptions, SWT.CHECK);
			GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).span(((GridLayout) groupOptions.getLayout()).numColumns,
					1).applyTo(this.buttonShowNrCrt);
			this.buttonShowNrCrt.setText("Afisare coloana pentru numar curent");
			WidgetCursorUtil.addHandCursorListener(this.buttonShowNrCrt);
			this.buttonShowNrCrt.addListener(SWT.Selection, this);

			this.buttonShowHeader = new Button(groupOptions, SWT.CHECK);
			GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).span(((GridLayout) groupOptions.getLayout()).numColumns,
					1).applyTo(this.buttonShowHeader);
			this.buttonShowHeader.setText("Afisare antet tabel");
			WidgetCursorUtil.addHandCursorListener(this.buttonShowHeader);
			this.buttonShowHeader.addListener(SWT.Selection, this);

			this.buttonShowGradient = new Button(groupOptions, SWT.CHECK);
			GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).span(((GridLayout) groupOptions.getLayout()).numColumns,
					1).applyTo(this.buttonShowGradient);
			this.buttonShowGradient.setText("Alternare culori in tabel");
			WidgetCursorUtil.addHandCursorListener(this.buttonShowGradient);
			this.buttonShowGradient.addListener(SWT.Selection, this);

			this.buttonShowPageNumbers = new Button(groupOptions, SWT.CHECK);
			GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).span(((GridLayout) groupOptions.getLayout()).numColumns,
					1).applyTo(this.buttonShowPageNumbers);
			this.buttonShowPageNumbers.setText("Afisare numere de pagina in document");
			WidgetCursorUtil.addHandCursorListener(this.buttonShowPageNumbers);
			this.buttonShowPageNumbers.addListener(SWT.Selection, this);

			WidgetCompositeUtil.addColoredFocusListener2Childrens(this);
		}

		@Override
		public String getCatName() {
			return ExportPdfOptionsView.ITEM_OPTIONS;
		}

		@Override
		public final boolean validate() {
			if (ExportPdfOptionsView.this.settings.getNrOfItems() > ExportPdf.MAX_ROWS_PDF) {
				if (SWTeXtension.displayMessageQ("Atentie. Se vor genera foarte multe pagini, si este posibil ca raportul sa "
						+ "necesite un timp indelungat pentru generare, iar cantitatea de memorie ceruta sa fie mai mare decat de obicei. Continuam?",
						"Posibila operatie de lunga durata") == SWT.NO) {
					return false;
				}
			}
			return true;
		}

		@Override
        public final void save() {
			String numeFisier = this.textFileName.getText();
            if (StringUtils.isEmpty(numeFisier)) {
				numeFisier = "RaportPDF_" + System.currentTimeMillis();
			}
			if (!ExportPdfOptionsView.this.buttonExportPathAuto.getSelection()) {
				ExportPdfPrefs.put(ExportPdfPrefs.PDF_EXPORT_PATH, this.dsc.getSelectedDirPath());
			}
			ExportPdfPrefs.putBoolean(ExportPdfPrefs.PDF_EXPORT_PATH_AUTO,
					ExportPdfOptionsView.this.buttonExportPathAuto.getSelection());
			ExportPdfOptionsView.this.settings.setNumeFisier(this.dsc.getSelectedDirPath().concat(numeFisier));
			ExportPdfOptionsView.this.settings.setTitlu(this.textTitleName.getText());
			ExportPdfPrefs.putBoolean(ExportPdfPrefs.PDF_IS_USING_TITLE,
					this.buttonShowTitle.getSelection());
			ExportPdfPrefs.putInt(ExportPdfPrefs.PDF_COMPRESSION, this.slider.getSelection() / 10);
			ExportPdfOptionsView.this.settings.setPageSize(ExportPdfOptionsView.sizes.get(this.comboPageSize.getText()));
			if (ExportPdfOptionsView.this.settings.getPageSize() == null) {
				ExportPdfOptionsView.this.settings.setPageSize(PageSize.A4);
			}
			ExportPdfPrefs.put(ExportPdfPrefs.PDF_PAGE_SIZE, this.comboPageSize.getText());
			ExportPdfPrefs.putBoolean(ExportPdfPrefs.PDF_IS_USING_NR_CRT,
					this.buttonShowNrCrt.getSelection());
			ExportPdfPrefs.putBoolean(ExportPdfPrefs.PDF_IS_SHOWING_HEADER,
					this.buttonShowHeader.getSelection());
			ExportPdfPrefs.putBoolean(ExportPdfPrefs.PDF_IS_USING_GRAY_EFFECT,
					this.buttonShowGradient.getSelection());
			ExportPdfPrefs.putBoolean(ExportPdfPrefs.PDF_IS_USING_PAGE_NO,
					this.buttonShowPageNumbers.getSelection());
			ExportPdfPrefs.put(ExportPdfPrefs.PDF_PAGE_ORIENTATION, this.comboOrientation.getText());
			if (this.comboOrientation.getText().equals(ExportPdfPrefs.LANDSCAPE)) {
				ExportPdfOptionsView.this.settings.setPageSize(ExportPdfOptionsView.this.settings.getPageSize().rotate());
			}
			PdfName pdfVersion;
			switch (this.comboPDFVersion.getSelectionIndex()) {
				case 0:
					pdfVersion = PdfWriter.PDF_VERSION_1_2;
					break;
				case 1:
					pdfVersion = PdfWriter.PDF_VERSION_1_3;
					break;
				case 2:
					pdfVersion = PdfWriter.PDF_VERSION_1_4;
					break;
				case 3:
					pdfVersion = PdfWriter.PDF_VERSION_1_5;
					break;
				case 4:
					pdfVersion = PdfWriter.PDF_VERSION_1_6;
					break;
				case 5:
					pdfVersion = PdfWriter.PDF_VERSION_1_7;
					break;
				default:
					pdfVersion = PdfWriter.PDF_VERSION_1_7;
			}
			ExportPdfOptionsView.this.settings.setPdfVersion(pdfVersion);
			ExportPdfPrefs.put(ExportPdfPrefs.PDF_VERSION, this.comboPDFVersion.getText());

			translateFont();
		}

		@Override
		public final void populateFields() {
			this.textFileName.setText(ExportPdfOptionsView.this.settings.getNumeFisier());
			this.buttonShowTitle.setSelection(ExportPdfPrefs.isUsingTitle());
			this.textTitleName.setText(ExportPdfOptionsView.this.settings.getTitlu());
			ExportPdfOptionsView.this.buttonExportPathAuto.setSelection(ExportPdfPrefs.isUsingAutoExportPath());
			if (ExportPdfOptionsView.this.buttonExportPathAuto.getSelection()) {
				this.dsc.setDirPath(EncodeLive.getReportsDir());
			} else {
				this.dsc.setDirPath(ExportPdfPrefs.getExportPath());
			}
			this.dsc.getItemSelectie().setEnabled(!ExportPdfOptionsView.this.buttonExportPathAuto.getSelection());
			if (this.comboOrientation.indexOf(ExportPdfPrefs.getPageOrientation()) != -1) {
				this.comboOrientation.select(this.comboOrientation.indexOf(ExportPdfPrefs.getPageOrientation()));
			} else {
				this.comboOrientation.select(this.comboOrientation.indexOf(ExportPdfPrefs.PORTRAIT));
			}
			if (this.comboPageSize.indexOf(ExportPdfPrefs.getPageSize()) != -1) {
				this.comboPageSize.select(this.comboPageSize.indexOf(ExportPdfPrefs.getPageSize()));
			} else {
				this.comboPageSize.select(this.comboPageSize.indexOf("A4"));
			}
			if (this.comboPDFVersion.indexOf(ExportPdfPrefs.getDocVersion()) != -1) {
				this.comboPDFVersion.select(this.comboPDFVersion.indexOf(ExportPdfPrefs.getDocVersion()));
			} else {
				this.comboPDFVersion.select(0);
			}
			this.slider.setSelection(ExportPdfPrefs.getCompression() * 10);
			if ((this.fs.getSelectedFont() != null) && !this.fs.getSelectedFont().isDisposed()) {
				this.fs.getSelectedFont().dispose();
			}
			try {
				this.fs.setNewFont(new Font(
					Display.getDefault(),
					ExportPdfPrefs.getFontNameUser(),
					ExportPdfPrefs.getFontSize(),
					ExportPdfPrefs.getFontStyle()));
			}
			catch (Exception exc) {
                logger.error(exc, exc);
				this.fs.setNewFont(new Font(Display.getDefault(), "Times New Roman", 10, SWT.NONE));
			}
			this.textTitleName.setEnabled(this.buttonShowTitle.getSelection());
			this.buttonShowNrCrt.setSelection(ExportPdfPrefs.isUsingNrCrt());
			this.buttonShowHeader.setSelection(ExportPdfPrefs.isShowingHeader());
			this.buttonShowGradient.setSelection(ExportPdfPrefs.isUsingGrayEffect());
			this.buttonShowPageNumbers.setSelection(ExportPdfPrefs.isUsingPageNo());
		}

		@Override
		public final void handleEvent(final Event e) {
			super.handleEvent(e);
			if (e.type == SWT.FocusIn) {
				if (e.widget == this.textFileName) {
					updateDetailMessage("Numele fisierului care va fi exportat.");
				} else if (e.widget == this.textTitleName) {
					updateDetailMessage("Un antent pentru fisierul rezultat.");
				} else if (e.widget == this.comboOrientation) {
					updateDetailMessage("Alegeti orientarea paginilor din fisier.");
				} else if (e.widget == this.comboPageSize) {
					updateDetailMessage("Alegeti dimensiunea paginilor din fisier.");
				} else if (e.widget == this.comboPDFVersion) {
					updateDetailMessage("Alegeti versiunea de fisier. Atentie : fiecare valoare "
							+ "necesita pentru vizualizare o versiune minima de AcrobatReader.");
				} else if (e.widget == this.fs.getTextSelectie()) {
					updateDetailMessage("Fontul celulelor este \'"
							+ this.fs.getTextSelectie().getText().substring(0,
									this.fs.getTextSelectie().getText().lastIndexOf(','))
							+ "\', de dimensiune "
							+ this.fs.getTextSelectie().getText().substring(this.fs.getTextSelectie().getText().lastIndexOf(',') + 1,
									this.fs.getTextSelectie().getText().length()));
				}
			} else if (e.type == SWT.Selection) {
				if (e.widget == this.buttonShowTitle) {
					this.textTitleName.setEnabled(this.buttonShowTitle.getSelection());
				} else if (e.widget == this.slider) {
					updateDetailMessage("Nivel compresie selectat : "
							+ (this.slider.getSelection() / 10) + " din 9 (maxim)");
				} else if (e.widget == this.buttonShowNrCrt) {
					if (this.buttonShowNrCrt.getSelection()) {
						updateDetailMessage("Prima coloana a raportului va afisa numarul curent al elementelor.");
					} else {
						updateDetailMessage("Nu se vor numerota elementele afisate.");
					}
				} else if (e.widget == this.buttonShowHeader) {
					if (this.buttonShowHeader.getSelection()) {
						updateDetailMessage("Se afiseaza antet pentru tabela");
					} else {
						updateDetailMessage("Tabela din raport nu va contine antet");
					}
				} else if (e.widget == this.buttonShowGradient) {
					if (this.buttonShowGradient.getSelection()) {
						updateDetailMessage("Foloseste alternarea culorilor, pentru liniile din tabela");
					} else {
						updateDetailMessage("Tabela contine doar linii albe");
					}
				} else if (e.widget == this.buttonShowPageNumbers) {
					if (this.buttonShowPageNumbers.getSelection()) {
						updateDetailMessage("Paginile se numeroteaza (ex. Pag. 4/10)");
					} else {
						updateDetailMessage("Nu se afiseaza numere de pagina");
					}
				} else if (e.widget == ExportPdfOptionsView.this.buttonExportPathAuto) {
					if (ExportPdfOptionsView.this.buttonExportPathAuto.getSelection()) {
						this.dsc.setDirPath(EncodeLive.getReportsDir().concat(File.separator));
					} else {
						this.dsc.setDirPath(ExportPdfPrefs.getExportPath());
					}
					this.dsc.getItemSelectie().setEnabled(!ExportPdfOptionsView.this.buttonExportPathAuto.getSelection());
				}
			}
		}

		private void translateFont() {
			this.font = ExportPdf.PDF_FONT;
			if ((this.fs.getFont() == null) || this.fs.getFont().isDisposed()) {
				return;
			}
			Font swtFont = this.fs.getSelectedFont();
			try {
				String osDir = System.getProperty("user.home");
                if (StringUtils.isEmpty(osDir)) {
					return;
				}
				File f = new File(osDir.substring(0, 1) + ":\\windows\\fonts");
				File[] ff = f.listFiles(new FileFilter() {
					@Override
					public boolean accept(final File pathname) {
						if ((pathname == null)
								|| (!pathname.getName().endsWith(".ttf") && !pathname.getName().endsWith(".TTF"))) {
							return false;
						}
						return true;
					}
				});
                if (ff == null) {
                    ff = new File[0];
                }
				String fontName = "";
				for (File temp : ff) {
					if (temp.getName().toUpperCase(EncodeLive.ROMANIAN_LOCALE).trim().startsWith(swtFont.getFontData()[0].getName().toUpperCase(EncodeLive.ROMANIAN_LOCALE).substring(0,
							3))) {
						fontName = temp.getName();
						break;
					}
				}
                if (StringUtils.isEmpty(fontName)) {
					return;
				}

				FontFactory.register(f.getCanonicalPath() + File.separator + fontName,
						ExportPdf.PDF_FONT_ALIAS);

				int style = com.itextpdf.text.Font.UNDEFINED;
				if ((swtFont.getFontData()[0].getStyle() & SWT.BOLD) == SWT.BOLD) {
					style |= com.itextpdf.text.Font.BOLD;
				} else if ((swtFont.getFontData()[0].getStyle() & SWT.ITALIC) == SWT.ITALIC) {
					style |= com.itextpdf.text.Font.ITALIC;
				}
				this.font = FontFactory.getFont(ExportPdf.PDF_FONT_ALIAS,
						swtFont.getFontData()[0].getHeight() - 2,
						style);
				ExportPdfPrefs.put(ExportPdfPrefs.PDF_FONT_NAME, fontName);
				ExportPdfPrefs.put(ExportPdfPrefs.PDF_FONT_NAME_USER,
						swtFont.getFontData()[0].getName());
				ExportPdfPrefs.putInt(ExportPdfPrefs.PDF_FONT_SIZE,
						swtFont.getFontData()[0].getHeight());
				ExportPdfPrefs.putInt(ExportPdfPrefs.PDF_FONT_STYLE,
						swtFont.getFontData()[0].getStyle());
			}
			catch (Exception exc) {
				SWTeXtension.displayMessageEGeneric(exc);
				this.font = ExportPdf.PDF_FONT;
			}
			finally {
				ExportPdfOptionsView.this.settings.setFont(this.font);
			}
		}

	}

	private class TableColsSettings extends AbstractIConfigAdapter {

		private ColumnsChooserComposite chooser;

		protected TableColsSettings() {
			super(ExportPdfOptionsView.this.rightForm);
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
			labelName.setText(ExportPdfOptionsView.ITEM_TABLE_COLS);
			labelName.setFont(FontUtil.TAHOMA12_BOLD);
			GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(labelName);

			this.chooser = new ColumnsChooserComposite(
				this,
				ExportPdfOptionsView.this.settings.getSwtTable(),
				ExportPdfOptionsView.this.settings.getClazz(),
				ExportPdfOptionsView.this.settings.getSufix());

			WidgetCompositeUtil.addColoredFocusListener2Childrens(this);
		}

		@Override
		public final void populateFields() {
			this.chooser.reset();
		}

		@Override
		public String getCatName() {
			return ExportPdfOptionsView.ITEM_TABLE_COLS;
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
			ExportPdfOptionsView.this.settings.setAligns(this.chooser.getAligns());
			ExportPdfOptionsView.this.settings.setDims(this.chooser.getDims());
			ExportPdfOptionsView.this.settings.setSelection(this.chooser.getSelection());
			ExportPdfOptionsView.this.settings.setOrder(this.chooser.getOrder());
		}
	}

	private static void getPageSizes() {
		try {
			Field[] fields = PageSize.class.getDeclaredFields();
			ExportPdfOptionsView.sizes = new TreeMap<String, Rectangle>();
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].getName().indexOf('_') == -1) {
					ExportPdfOptionsView.sizes.put(fields[i].getName(),
							PageSize.getRectangle(fields[i].getName()));
				}
			}
			ExportPdfOptionsView.PAGE_SIZES = new String[ExportPdfOptionsView.sizes.size()];
			int i = 0;
			for (String str : ExportPdfOptionsView.sizes.keySet()) {
				ExportPdfOptionsView.PAGE_SIZES[i++] = str;
			}
		}
		catch (Exception exc) {
            SWTeXtension.displayMessageEGeneric(exc);
		}
	}

	public ExportPdfSettings getSettings() {
		return this.settings;
	}

}