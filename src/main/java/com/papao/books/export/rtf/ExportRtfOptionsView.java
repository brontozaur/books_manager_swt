package com.papao.books.export.rtf;

import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.papao.books.controller.SettingsController;
import com.papao.books.export.AbstractExportView;
import com.papao.books.model.config.ExportRtfSetting;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.auth.EncodeLive;
import com.papao.books.ui.custom.DirectorySelectorComposite;
import com.papao.books.ui.custom.FontSelectorComposite;
import com.papao.books.ui.interfaces.AbstractIConfigAdapter;
import com.papao.books.ui.interfaces.IConfig;
import com.papao.books.ui.util.ColorUtil;
import com.papao.books.ui.util.FontUtil;
import com.papao.books.ui.util.WidgetCompositeUtil;
import com.papao.books.ui.util.WidgetCursorUtil;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.ColumnsChooserComposite;
import com.papao.books.ui.view.SWTeXtension;
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

public class ExportRtfOptionsView extends AbstractExportView {

    private static final Logger logger = Logger.getLogger(ExportRtfOptionsView.class);

    private final static String ITEM_OPTIONS = "Optiuni export";
    private final static String ITEM_TABLE_COLS = "Coloane selectabile";

    private final static String[] ITEMS = new String[]{
            ExportRtfOptionsView.ITEM_OPTIONS, ExportRtfOptionsView.ITEM_TABLE_COLS};

    private static Map<String, Rectangle> sizes;
    private static String[] PAGE_SIZES;

    public final static com.lowagie.text.Font RTF_FONT = new com.lowagie.text.Font(
            com.lowagie.text.Font.TIMES_ROMAN,
            12);

    public final static String RTF_FONT_ALIAS = "trilulilucrododiluRTF";

    public final static int MAX_ROWS_4_WARNING = 3500;
    private ExportRtfSetting exportRtfSetting;

    static {
        ExportRtfOptionsView.getPageSizes();
    }

    private final ExportRtfSettings settings;
    private Button buttonExportPathAuto;

    public ExportRtfOptionsView(final Shell parent, final ExportRtfSettings settings) {
        super(parent);
        this.settings = settings;
        exportRtfSetting = SettingsController.getExportRtfSetting();
        if (exportRtfSetting == null) {
            exportRtfSetting = new ExportRtfSetting();
        }

        for (String str : ExportRtfOptionsView.ITEMS) {
            new TableItem(this.leftTable, SWT.NONE).setText(str);
        }

        this.mapComponents.put(ExportRtfOptionsView.ITEM_OPTIONS, new ExportSettings());
        this.mapComponents.put(ExportRtfOptionsView.ITEM_TABLE_COLS, new TableColsSettings());

        this.leftTable.select(0);
        this.leftTable.notifyListeners(SWT.Selection, new Event());
    }

    @Override
    public void actionPerformed(final String catName) {
        this.rightForm.setContent((Composite) this.mapComponents.get(catName));
    }

    @Override
    public void customizeView() {
        setShellStyle(SWT.MIN | SWT.CLOSE | SWT.RESIZE);
        setViewOptions(AbstractView.ADD_OK | AbstractView.ADD_CANCEL);
        setShellImage(AppImages.getImage16(AppImages.IMG_WORD2));
        setBigViewImage(AppImages.getImage24(AppImages.IMG_WORD2));
        setBigViewMessage("Configurare export date in format RTF");
        setShellText("Optiuni export RTF");
        setShowSaveOKMessage(false);
    }

    @Override
    public void reset() {
        exportRtfSetting.reset();
        for (IConfig cfg : this.mapComponents.values()) {
            cfg.populateFields();
        }
    }

    private static void getPageSizes() {
        try {
            Field[] fields = PageSize.class.getDeclaredFields();
            ExportRtfOptionsView.sizes = new TreeMap<String, Rectangle>();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getName().indexOf('_') == -1) {
                    try {
                        ExportRtfOptionsView.sizes.put(fields[i].getName(),
                                PageSize.getRectangle(fields[i].getName()));
                    } catch (Exception exc) {
                        logger.warn(exc);
                        continue;
                    }
                }
            }
            ExportRtfOptionsView.PAGE_SIZES = new String[ExportRtfOptionsView.sizes.size()];
            int i = 0;
            for (String str : ExportRtfOptionsView.sizes.keySet()) {
                ExportRtfOptionsView.PAGE_SIZES[i++] = str;
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            SWTeXtension.displayMessageEGeneric(exc);
        }
    }

    private class ExportSettings extends AbstractIConfigAdapter {
        private Button buttonShowTitle;
        private Button buttonShowNrCrt;
        private Button buttonShowHeader;
        private Button buttonShowGradient;
        private Button buttonShowPageNumbers;
        private DirectorySelectorComposite dsc;
        private Text textTitleName;
        private Combo comboOrientation;
        private Combo comboPageSize;
        private FontSelectorComposite fs;
        private Text textFileName;
        private com.lowagie.text.Font font;

        protected ExportSettings() {
            super(ExportRtfOptionsView.this.rightForm);
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
            labelName.setText(ExportRtfOptionsView.ITEM_OPTIONS);
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

            ExportRtfOptionsView.this.buttonExportPathAuto = new Button(groupOptions, SWT.CHECK);
            ExportRtfOptionsView.this.buttonExportPathAuto.setText("cale automata export");
            ExportRtfOptionsView.this.buttonExportPathAuto.addListener(SWT.Selection, this);
            GridDataFactory.fillDefaults().applyTo(ExportRtfOptionsView.this.buttonExportPathAuto);
            this.dsc = new DirectorySelectorComposite(groupOptions);

            new Label(groupOptions, SWT.NONE).setText("Orientare");
            this.comboOrientation = new Combo(groupOptions, SWT.BORDER | SWT.READ_ONLY);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(this.comboOrientation);
            this.comboOrientation.setItems(ExportRtfSetting.ORIENTATIONS);
            this.comboOrientation.addListener(SWT.FocusIn, this);

            new Label(groupOptions, SWT.NONE).setText("Dimensiune");
            this.comboPageSize = new Combo(groupOptions, SWT.BORDER | SWT.READ_ONLY);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(this.comboPageSize);
            this.comboPageSize.setItems(ExportRtfOptionsView.PAGE_SIZES);
            this.comboPageSize.addListener(SWT.FocusIn, this);

            new Label(groupOptions, SWT.NONE).setText("Font tabela");
            this.fs = new FontSelectorComposite(groupOptions);
            this.fs.getTextSelectie().addListener(SWT.FocusIn, this);

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
        public final boolean validate() {
            if (ExportRtfOptionsView.this.settings.getNrOfItems() > ExportRtfOptionsView.MAX_ROWS_4_WARNING) {
                if (SWTeXtension.displayMessageQ("Atentie. Pentru mai mult de "
                                + ExportRtfOptionsView.MAX_ROWS_4_WARNING
                                + " linii exportate, incarcarea fisierului spre vizualizare poate sa dureze mai mult ca de obicei. "
                                + "Se recomanda salvarea in alt format (de ex. *.pdf sau *.xls). Doriti sa continuati exportul curent?",
                        "Posibila operatie de lunga durata") == SWT.NO) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public final void populateFields() {
            this.textFileName.setText(ExportRtfOptionsView.this.settings.getNumeFisier());
            this.buttonShowTitle.setSelection(exportRtfSetting.isShowTitle());
            this.textTitleName.setText(ExportRtfOptionsView.this.settings.getTitlu());
            ExportRtfOptionsView.this.buttonExportPathAuto.setSelection(exportRtfSetting.isAutomaticExportPath());
            if (ExportRtfOptionsView.this.buttonExportPathAuto.getSelection()) {
                this.dsc.setDirPath(EncodeLive.getReportsDir());
            } else {
                this.dsc.setDirPath(exportRtfSetting.getExportDir());
            }
            this.dsc.getItemSelectie().setEnabled(!ExportRtfOptionsView.this.buttonExportPathAuto.getSelection());
            if (this.comboOrientation.indexOf(exportRtfSetting.getPageOrientation()) != -1) {
                this.comboOrientation.select(this.comboOrientation.indexOf(exportRtfSetting.getPageOrientation()));
            } else {
                this.comboOrientation.select(this.comboOrientation.indexOf(ExportRtfSetting.PORTRAIT));
            }
            if (this.comboPageSize.indexOf(exportRtfSetting.getPageSize()) != -1) {
                this.comboPageSize.select(this.comboPageSize.indexOf(exportRtfSetting.getPageSize()));
            } else {
                this.comboPageSize.select(this.comboPageSize.indexOf("A4"));
            }
            if ((this.fs.getSelectedFont() != null) && !this.fs.getSelectedFont().isDisposed()) {
                this.fs.getSelectedFont().dispose();
            }
            try {
                this.fs.setNewFont(new Font(
                        Display.getDefault(),
                        exportRtfSetting.getFontNameUser(),
                        exportRtfSetting.getFontSize(),
                        exportRtfSetting.getFontStyle()));
            } catch (Exception exc) {
                logger.error(exc, exc);
                this.fs.setNewFont(new Font(Display.getDefault(), "Times New Roman", 10, SWT.NONE));
            }
            this.textTitleName.setEnabled(this.buttonShowTitle.getSelection());
            this.buttonShowNrCrt.setSelection(exportRtfSetting.isShowNrCrt());
            this.buttonShowHeader.setSelection(exportRtfSetting.isShowHeader());
            this.buttonShowGradient.setSelection(exportRtfSetting.isShowGrayEffect());
            this.buttonShowPageNumbers.setSelection(exportRtfSetting.isShowPageNumber());
        }

        @Override
        public String getCatName() {
            return ExportRtfOptionsView.ITEM_OPTIONS;
        }

        @Override
        public final void handleEvent(final Event e) {
            if (e.type == SWT.FocusIn) {
                if (e.widget == this.textFileName) {
                    updateDetailMessage("Numele fisierului care va fi exportat.");
                } else if (e.widget == this.textTitleName) {
                    updateDetailMessage("Un antent pentru fisierul rezultat.");
                } else if (e.widget == this.comboOrientation) {
                    updateDetailMessage("Alegeti orientarea paginilor din fisier.");
                } else if (e.widget == this.comboPageSize) {
                    updateDetailMessage("Alegeti dimensiunea paginilor din fisier.");
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
                } else if (e.widget == ExportRtfOptionsView.this.buttonExportPathAuto) {
                    if (ExportRtfOptionsView.this.buttonExportPathAuto.getSelection()) {
                        this.dsc.setDirPath(EncodeLive.getReportsDir().concat(File.separator));
                    } else {
                        this.dsc.setDirPath(exportRtfSetting.getExportDir());
                    }
                    this.dsc.getItemSelectie().setEnabled(!ExportRtfOptionsView.this.buttonExportPathAuto.getSelection());
                }
            }
        }

        @Override
        public final void save() {
            String numeFisier = this.textFileName.getText();
            if (StringUtils.isEmpty(numeFisier)) {
                numeFisier = "RaportRTF_" + System.currentTimeMillis();
            }

            exportRtfSetting.setAutomaticExportPath(buttonExportPathAuto.getSelection());
            exportRtfSetting.setShowTitle(buttonShowTitle.getSelection());
            exportRtfSetting.setShowNrCrt(buttonShowNrCrt.getSelection());
            exportRtfSetting.setShowHeader(buttonShowHeader.getSelection());
            exportRtfSetting.setShowGrayEffect(buttonShowGradient.getSelection());
            exportRtfSetting.setShowPageNumber(buttonShowPageNumbers.getSelection());
            exportRtfSetting.setPageSize(comboPageSize.getText());
            exportRtfSetting.setPageOrientation(comboOrientation.getText());

            ExportRtfOptionsView.this.settings.setNumeFisier(this.dsc.getSelectedDirPath().concat(numeFisier));
            ExportRtfOptionsView.this.settings.setTitlu(this.textTitleName.getText());
            ExportRtfOptionsView.this.settings.setPageSize(ExportRtfOptionsView.sizes.get(this.comboPageSize.getText()));
            if (ExportRtfOptionsView.this.settings.getPageSize() == null) {
                ExportRtfOptionsView.this.settings.setPageSize(PageSize.A4);
            }
            if (this.comboOrientation.getText().equals(ExportRtfSetting.LANDSCAPE)) {
                ExportRtfOptionsView.this.settings.setPageSize(ExportRtfOptionsView.this.settings.getPageSize().rotate());
            }

            if (!EncodeLive.IS_MAC) {
                translateFont();
            }

            SettingsController.saveExportRtfSetting(exportRtfSetting);
        }

        private void translateFont() {
            this.font = ExportRtfOptionsView.RTF_FONT;
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
                        ExportRtfOptionsView.RTF_FONT_ALIAS);

                int style = com.lowagie.text.Font.UNDEFINED;
                if ((swtFont.getFontData()[0].getStyle() & SWT.BOLD) == SWT.BOLD) {
                    style |= com.lowagie.text.Font.BOLD;
                } else if ((swtFont.getFontData()[0].getStyle() & SWT.ITALIC) == SWT.ITALIC) {
                    style |= com.lowagie.text.Font.ITALIC;
                }
                this.font = FontFactory.getFont(ExportRtfOptionsView.RTF_FONT_ALIAS,
                        swtFont.getFontData()[0].getHeight(),
                        style);
                exportRtfSetting.setFontName(fontName);
                exportRtfSetting.setFontNameUser(swtFont.getFontData()[0].getName());
                exportRtfSetting.setFontSize(swtFont.getFontData()[0].getHeight());
                exportRtfSetting.setFontStyle(swtFont.getFontData()[0].getStyle());
            } catch (Exception exc) {
                logger.warn(exc);
                SWTeXtension.displayMessageEGeneric(exc);
                this.font = ExportRtfOptionsView.RTF_FONT;
            } finally {
                ExportRtfOptionsView.this.settings.setiTextFont(this.font);
            }
        }
    }

    private class TableColsSettings extends AbstractIConfigAdapter {

        private ColumnsChooserComposite chooser;

        protected TableColsSettings() {
            super(ExportRtfOptionsView.this.rightForm);
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
            labelName.setText(ExportRtfOptionsView.ITEM_TABLE_COLS);
            labelName.setFont(FontUtil.TAHOMA12_BOLD);
            GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(labelName);

            this.chooser = new ColumnsChooserComposite(
                    this,
                    ExportRtfOptionsView.this.settings.getSwtTable(),
                    ExportRtfOptionsView.this.settings.getClazz(),
                    ExportRtfOptionsView.this.settings.getSufix());

            WidgetCompositeUtil.addColoredFocusListener2Childrens(this);
        }

        @Override
        public final void populateFields() {
            this.chooser.reset();
        }

        @Override
        public String getCatName() {
            return ExportRtfOptionsView.ITEM_TABLE_COLS;
        }

        @Override
        public final boolean validate() {
            return this.chooser.validate();
        }

        @Override
        public final void save() {
            this.chooser.save(false);
            ExportRtfOptionsView.this.settings.setAligns(this.chooser.getAligns());
            ExportRtfOptionsView.this.settings.setDims(this.chooser.getDims());
            ExportRtfOptionsView.this.settings.setSelection(this.chooser.getSelection());
            ExportRtfOptionsView.this.settings.setOrder(this.chooser.getOrder());
        }

    }

    public ExportRtfSettings getSettings() {
        return this.settings;
    }

}
