package com.papao.books.export.html;

import com.lowagie.text.FontFactory;
import com.papao.books.controller.SettingsController;
import com.papao.books.export.AbstractExportView;
import com.papao.books.model.config.ExportHtmlSetting;
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

public class ExportHtmlOptionsView extends AbstractExportView {

    private static final Logger logger = Logger.getLogger(ExportHtmlOptionsView.class);

    private final static String ITEM_OPTIONS = "Optiuni export";
    private final static String ITEM_TABLE_COLS = "Coloane selectabile";
    private final static String[] ITEMS = new String[]{
            ExportHtmlOptionsView.ITEM_OPTIONS, ExportHtmlOptionsView.ITEM_TABLE_COLS};

    public final static com.lowagie.text.Font HTML_FONT = new com.lowagie.text.Font(
            com.lowagie.text.Font.TIMES_ROMAN,
            12);
    public final static String HTML_FONT_ALIAS = "trilulilucrododiluHTML";
    public final static int MAX_ROWS_4_WARNING = 1000;

    private final ExportHtmlSettings settings;
    private Button buttonExportPathAuto;
    private ExportHtmlSetting exportHtmlSetting;

    public ExportHtmlOptionsView(final Shell parent, final ExportHtmlSettings settings) {
        super(parent);
        this.settings = settings;
        exportHtmlSetting = SettingsController.getExportHtmlSetting();
        if (exportHtmlSetting == null) {
            exportHtmlSetting = new ExportHtmlSetting();
        }

        for (String str : ExportHtmlOptionsView.ITEMS) {
            new TableItem(this.leftTable, SWT.NONE).setText(str);
        }

        this.mapComponents.put(ExportHtmlOptionsView.ITEM_OPTIONS, new ExportSettings());
        this.mapComponents.put(ExportHtmlOptionsView.ITEM_TABLE_COLS, new TableColsSettings());

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
        setShellImage(AppImages.getImage16(AppImages.IMG_BROWSER));
        setBigViewImage(AppImages.getImage24(AppImages.IMG_BROWSER));
        setBigViewMessage("Configurare export date în format HTML");
        setShellText("Opțiuni export HTML");
        setShowSaveOKMessage(false);
    }

    @Override
    public void reset() {
        exportHtmlSetting.reset();
        for (IConfig cfg : this.mapComponents.values()) {
            cfg.populateFields();
        }
    }

    private class ExportSettings extends AbstractIConfigAdapter {
        private Button buttonShowTitle;
        private Button buttonShowNrCrt;
        private Button buttonShowHeader;
        private Button buttonShowGradient;
        private DirectorySelectorComposite dsc;
        private Text textTitleName;
        private FontSelectorComposite fs;
        private Text textFileName;
        private com.lowagie.text.Font font;

        protected ExportSettings() {
            super(ExportHtmlOptionsView.this.rightForm);
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
            labelName.setText(ExportHtmlOptionsView.ITEM_OPTIONS);
            labelName.setFont(FontUtil.TAHOMA12_BOLD);
            GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(labelName);

            groupOptions = new Group(this, SWT.NONE);
            groupOptions.setText("Opțiuni export");
            gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
            groupOptions.setLayoutData(gd);
            groupOptions.setLayout(new GridLayout(1, true));

            new Label(groupOptions, SWT.NONE).setText("Nume fișier");

            this.textFileName = new Text(groupOptions, SWT.BORDER);
            GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(false, false).hint(250,
                    SWT.DEFAULT).minSize(250, SWT.DEFAULT).applyTo(this.textFileName);
            this.textFileName.addListener(SWT.FocusIn, this);
            this.textFileName.addListener(SWT.Modify, this);

            ExportHtmlOptionsView.this.buttonExportPathAuto = new Button(groupOptions, SWT.CHECK);
            ExportHtmlOptionsView.this.buttonExportPathAuto.setText("cale automată export");
            ExportHtmlOptionsView.this.buttonExportPathAuto.addListener(SWT.Selection, this);
            GridDataFactory.fillDefaults().applyTo(ExportHtmlOptionsView.this.buttonExportPathAuto);
            this.dsc = new DirectorySelectorComposite(groupOptions);

            new Label(groupOptions, SWT.NONE).setText("Font tabelă");
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
            this.buttonShowNrCrt.setText("Afișare coloană pentru număr curent");
            WidgetCursorUtil.addHandCursorListener(this.buttonShowNrCrt);
            this.buttonShowNrCrt.addListener(SWT.Selection, this);

            this.buttonShowHeader = new Button(groupOptions, SWT.CHECK);
            GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).span(((GridLayout) groupOptions.getLayout()).numColumns,
                    1).applyTo(this.buttonShowHeader);
            this.buttonShowHeader.setText("Afișare antet tabel");
            WidgetCursorUtil.addHandCursorListener(this.buttonShowHeader);
            this.buttonShowHeader.addListener(SWT.Selection, this);

            this.buttonShowGradient = new Button(groupOptions, SWT.CHECK);
            GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).span(((GridLayout) groupOptions.getLayout()).numColumns,
                    1).applyTo(this.buttonShowGradient);
            this.buttonShowGradient.setText("Alternare culori în tabel");
            WidgetCursorUtil.addHandCursorListener(this.buttonShowGradient);
            this.buttonShowGradient.addListener(SWT.Selection, this);

            WidgetCompositeUtil.addColoredFocusListener2Childrens(this);
        }

        @Override
        public final boolean validate() {
            if (getSettings().getNrOfItems() > ExportHtmlOptionsView.MAX_ROWS_4_WARNING) {
                if (SWTeXtension.displayMessageQ("Atenție. Pentru mai mult de "
                                + ExportHtmlOptionsView.MAX_ROWS_4_WARNING
                                + " linii exportate, este foarte posibil ca browserul să le afișeze extrem de încet. "
                                + "Se recomandă salvarea în alt format (de ex. *.pdf sau *.xls). Doriți să continuați exportul curent?",
                        "Posibilă operație de lungă durată") == SWT.NO) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public final void populateFields() {
            this.textFileName.setText(ExportHtmlOptionsView.this.settings.getNumeFisier());
            this.buttonShowTitle.setSelection(exportHtmlSetting.isShowTitle());
            this.textTitleName.setText(ExportHtmlOptionsView.this.settings.getTitlu());
            ExportHtmlOptionsView.this.buttonExportPathAuto.setSelection(exportHtmlSetting.isAutomaticExportPath());
            if (ExportHtmlOptionsView.this.buttonExportPathAuto.getSelection()) {
                this.dsc.setDirPath(EncodeLive.getReportsDir());
            } else {
                this.dsc.setDirPath(exportHtmlSetting.getExportDir());
            }
            this.dsc.getItemSelectie().setEnabled(!ExportHtmlOptionsView.this.buttonExportPathAuto.getSelection());
            if ((this.fs.getSelectedFont() != null) && !this.fs.getSelectedFont().isDisposed()) {
                this.fs.getSelectedFont().dispose();
            }
            try {
                this.fs.setNewFont(new Font(
                        Display.getDefault(),
                        exportHtmlSetting.getFontNameUser(),
                        exportHtmlSetting.getFontSize(),
                        exportHtmlSetting.getFontStyle()));
            } catch (Exception exc) {
                logger.error(exc, exc);
                this.fs.setNewFont(new Font(Display.getDefault(), "Times New Roman", 10, SWT.NONE));
            }
            this.textTitleName.setEnabled(this.buttonShowTitle.getSelection());
            this.buttonShowNrCrt.setSelection(exportHtmlSetting.isShowNrCrt());
            this.buttonShowHeader.setSelection(exportHtmlSetting.isShowHeader());
            this.buttonShowGradient.setSelection(exportHtmlSetting.isShowGrayEffect());
        }

        @Override
        public String getCatName() {
            return ExportHtmlOptionsView.ITEM_OPTIONS;
        }

        @Override
        public final void handleEvent(final Event e) {
            super.handleEvent(e);
            if (e.type == SWT.FocusIn) {
                if (e.widget == this.textFileName) {
                    updateDetailMessage("Numele fisierului care va fi exportat.");
                } else if (e.widget == this.textTitleName) {
                    updateDetailMessage("Un antent pentru fisierul rezultat.");
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
                } else if (e.widget == ExportHtmlOptionsView.this.buttonExportPathAuto) {
                    if (ExportHtmlOptionsView.this.buttonExportPathAuto.getSelection()) {
                        this.dsc.setDirPath(EncodeLive.getReportsDir().concat(File.separator));
                    } else {
                        this.dsc.setDirPath(exportHtmlSetting.getExportDir());
                    }
                    this.dsc.getItemSelectie().setEnabled(!ExportHtmlOptionsView.this.buttonExportPathAuto.getSelection());
                }
            }
        }

        @Override
        public final void save() {
            String numeFisier = this.textFileName.getText();
            if (StringUtils.isEmpty(numeFisier)) {
                numeFisier = "RaportHTML_" + System.currentTimeMillis();
            }
            exportHtmlSetting.setExportDir(this.dsc.getSelectedDirPath());
            exportHtmlSetting.setAutomaticExportPath(buttonExportPathAuto.getSelection());
            exportHtmlSetting.setShowTitle(buttonShowTitle.getSelection());
            exportHtmlSetting.setShowNrCrt(buttonShowNrCrt.getSelection());
            exportHtmlSetting.setShowHeader(buttonShowHeader.getSelection());
            exportHtmlSetting.setShowGrayEffect(buttonShowGradient.getSelection());

            ExportHtmlOptionsView.this.settings.setNumeFisier(this.dsc.getSelectedDirPath().concat(numeFisier));
            ExportHtmlOptionsView.this.settings.setTitlu(this.textTitleName.getText());

            if (!EncodeLive.IS_MAC) {
                translateFont();
            }

            SettingsController.saveExportHtmlSetting(exportHtmlSetting);
        }

        private void translateFont() {
            this.font = ExportHtmlOptionsView.HTML_FONT;
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
                        ExportHtmlOptionsView.HTML_FONT_ALIAS);

                int style = com.lowagie.text.Font.UNDEFINED;
                if ((swtFont.getFontData()[0].getStyle() & SWT.BOLD) == SWT.BOLD) {
                    style |= com.lowagie.text.Font.BOLD;
                } else if ((swtFont.getFontData()[0].getStyle() & SWT.ITALIC) == SWT.ITALIC) {
                    style |= com.lowagie.text.Font.ITALIC;
                }
                this.font = FontFactory.getFont(ExportHtmlOptionsView.HTML_FONT_ALIAS, swtFont.getFontData()[0].getHeight(), style);
                exportHtmlSetting.setFontName(fontName);
                exportHtmlSetting.setFontNameUser(swtFont.getFontData()[0].getName());
                exportHtmlSetting.setFontSize(swtFont.getFontData()[0].getHeight());
                exportHtmlSetting.setFontStyle(swtFont.getFontData()[0].getStyle());
            } catch (Exception exc) {
                SWTeXtension.displayMessageEGeneric(exc);
                this.font = ExportHtmlOptionsView.HTML_FONT;
            } finally {
                ExportHtmlOptionsView.this.settings.setiTextFont(this.font);
            }
        }
    }

    private class TableColsSettings extends AbstractIConfigAdapter {

        private ColumnsChooserComposite chooser;

        protected TableColsSettings() {
            super(ExportHtmlOptionsView.this.rightForm);
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
            labelName.setText(ExportHtmlOptionsView.ITEM_TABLE_COLS);
            labelName.setFont(FontUtil.TAHOMA12_BOLD);
            GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(labelName);

            this.chooser = new ColumnsChooserComposite(
                    this,
                    ExportHtmlOptionsView.this.settings.getSwtTable(),
                    ExportHtmlOptionsView.this.settings.getClazz(),
                    ExportHtmlOptionsView.this.settings.getSufix());

            WidgetCompositeUtil.addColoredFocusListener2Childrens(this);
        }

        @Override
        public final void populateFields() {
            this.chooser.reset();
        }

        @Override
        public String getCatName() {
            return ExportHtmlOptionsView.ITEM_TABLE_COLS;
        }

        @Override
        public final boolean validate() {
            return this.chooser.validate();
        }

        @Override
        public final void save() {
            this.chooser.save(false);
            ExportHtmlOptionsView.this.settings.setAligns(this.chooser.getAligns());
            ExportHtmlOptionsView.this.settings.setDims(this.chooser.getDims());
            ExportHtmlOptionsView.this.settings.setSelection(this.chooser.getSelection());
            ExportHtmlOptionsView.this.settings.setOrder(this.chooser.getOrder());
        }

    }

    public ExportHtmlSettings getSettings() {
        return this.settings;
    }

}
