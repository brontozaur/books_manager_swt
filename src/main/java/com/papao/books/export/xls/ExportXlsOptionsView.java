package com.papao.books.export.xls;

import com.papao.books.export.AbstractExportView;
import com.papao.books.model.config.ExportXlsSetting;
import com.papao.books.view.AppImages;
import com.papao.books.view.auth.EncodeLive;
import com.papao.books.view.custom.DirectorySelectorComposite;
import com.papao.books.view.interfaces.AbstractIConfigAdapter;
import com.papao.books.view.interfaces.ConfigurationException;
import com.papao.books.view.interfaces.IConfig;
import com.papao.books.view.util.*;
import com.papao.books.view.view.AbstractView;
import com.papao.books.view.view.ColumnsChooserComposite;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;

public class ExportXlsOptionsView extends AbstractExportView {

    private final static String[] EXTENSIONS = new String[]{
            ExportXls.XLS_EXTENSION, ExportXls.XLSX_EXTENSION};

    private final static String ITEM_OPTIONS = "Optiuni export";
    private final static String ITEM_TABLE_COLS = "Coloane selectabile";
    private final static String[] ITEMS = new String[]{
            ExportXlsOptionsView.ITEM_OPTIONS, ExportXlsOptionsView.ITEM_TABLE_COLS};
    private final ExportXlsSettings settings;
    private Button buttonExportPathAuto;
    private ExportXlsSetting exportXlsSetting;

    public ExportXlsOptionsView(final Shell parent, final ExportXlsSettings settings) {
        super(parent);

        this.settings = settings;
        exportXlsSetting = SettingsController.getExportXlsSetting();
        if (exportXlsSetting == null) {
            exportXlsSetting = new ExportXlsSetting();
        }

        for (String str : ExportXlsOptionsView.ITEMS) {
            new TableItem(this.leftTable, SWT.NONE).setText(str);
        }

        this.mapComponents.put(ExportXlsOptionsView.ITEM_OPTIONS, new ExportSettings());
        this.mapComponents.put(ExportXlsOptionsView.ITEM_TABLE_COLS, new TableColsSettings());

        this.leftTable.select(0);
        this.leftTable.notifyListeners(SWT.Selection, new Event());
    }

    @Override
    public void reset() {
        exportXlsSetting.reset();
        for (IConfig cfg : this.mapComponents.values()) {
            cfg.populateFields();
        }
    }

    @Override
    public void actionPerformed(final String catName) {
        this.rightForm.setContent((Composite) this.mapComponents.get(catName));
    }

    @Override
    public void customizeView() {
        setShellText("Optiuni export Excel");
        setShellStyle(SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE);
        setShellImage(AppImages.getImage16(AppImages.IMG_EXCEL));
        setViewOptions(AbstractView.ADD_CANCEL | AbstractView.ADD_OK);
        setBigViewImage(AppImages.getImage24(AppImages.IMG_EXCEL));
        setBigViewMessage("Configurare export date in format MS Excel");
        setShowSaveOKMessage(false);
    }

    private class ExportSettings extends AbstractIConfigAdapter {
        private Combo comboExtension;
        private Button buttonShowHeader;
        private Text textSheetName;
        private Text textFileName;
        private Text textHeaderName;
        private Button buttonAutoResizeCols;
        private DirectorySelectorComposite dsc;

        protected ExportSettings() {
            super(ExportXlsOptionsView.this.rightForm);
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
            labelName.setText(ExportXlsOptionsView.ITEM_OPTIONS);
            labelName.setFont(FontUtil.TAHOMA12_BOLD);
            GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(labelName);

            groupOptions = new Group(this, SWT.NONE);
            groupOptions.setText("Optiuni export");
            gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
            groupOptions.setLayoutData(gd);
            groupOptions.setLayout(new GridLayout(1, true));

            temp = new Label(groupOptions, SWT.NONE);
            temp.setText("Nume fisier");
            GridDataFactory.fillDefaults().applyTo(temp);

            Composite comp = new Composite(groupOptions, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(comp);
            GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(0, 0).applyTo(comp);

            this.textFileName = new Text(comp, SWT.BORDER);
            GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(false, false).hint(250,
                    SWT.DEFAULT).minSize(250, SWT.DEFAULT).applyTo(this.textFileName);
            this.textFileName.addListener(SWT.FocusIn, this);
            this.textFileName.addListener(SWT.Modify, this);

            this.comboExtension = new Combo(comp, SWT.BORDER | SWT.READ_ONLY);
            GridDataFactory.fillDefaults().align(SWT.END, SWT.BEGINNING).grab(false, false).hint(50,
                    SWT.DEFAULT).applyTo(this.comboExtension);
            this.comboExtension.setItems(ExportXlsOptionsView.EXTENSIONS);
            this.comboExtension.select(0);
            this.comboExtension.addListener(SWT.FocusIn, this);

            ExportXlsOptionsView.this.buttonExportPathAuto = new Button(groupOptions, SWT.CHECK);
            ExportXlsOptionsView.this.buttonExportPathAuto.setText("cale automata export");
            ExportXlsOptionsView.this.buttonExportPathAuto.addListener(SWT.Selection, this);

            this.dsc = new DirectorySelectorComposite(groupOptions, false);

            temp = new Label(groupOptions, SWT.NONE);
            temp.setText("Text antet");

            this.textHeaderName = new Text(groupOptions, SWT.BORDER);
            GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(false, false).hint(250,
                    SWT.DEFAULT).minSize(250, SWT.DEFAULT).applyTo(this.textHeaderName);
            this.textHeaderName.addListener(SWT.FocusIn, this);

            temp = new Label(groupOptions, SWT.NONE);
            temp.setText("Denumire sheet");

            this.textSheetName = new Text(groupOptions, SWT.BORDER);
            GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(false, false).hint(250,
                    SWT.DEFAULT).minSize(250, SWT.DEFAULT).applyTo(this.textSheetName);
            this.textSheetName.addListener(SWT.FocusIn, this);

            this.buttonAutoResizeCols = new Button(groupOptions, SWT.CHECK);
            GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).span(((GridLayout) groupOptions.getLayout()).numColumns,
                    1).applyTo(this.buttonAutoResizeCols);
            this.buttonAutoResizeCols.setText("Redimensionare automata a coloanelor");
            WidgetCursorUtil.addHandCursorListener(this.buttonAutoResizeCols);

            this.buttonShowHeader = new Button(groupOptions, SWT.CHECK);
            GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).span(((GridLayout) groupOptions.getLayout()).numColumns,
                    1).applyTo(this.buttonShowHeader);
            this.buttonShowHeader.setText("Afisare header (primele 3 linii din fiecare sheet)");
            WidgetCursorUtil.addHandCursorListener(this.buttonShowHeader);
        }

        @Override
        public final void populateFields() {
            this.textFileName.setText(ExportXlsOptionsView.this.settings.getNumeFisier());
            if (this.comboExtension.indexOf(exportXlsSetting.getExtension()) != -1) {
                this.comboExtension.select(this.comboExtension.indexOf(exportXlsSetting.getExtension()));
            } else {
                this.comboExtension.select(0);
            }
            this.textHeaderName.setText(ExportXlsOptionsView.this.settings.getTitlu());
            this.textSheetName.setText(ExportXlsOptionsView.this.settings.getSheetName());
            this.buttonAutoResizeCols.setSelection(exportXlsSetting.isAutoResizeCols());
            this.buttonShowHeader.setSelection(exportXlsSetting.isShowHeader());
            ExportXlsOptionsView.this.buttonExportPathAuto.setSelection(exportXlsSetting.isAutomaticExportPath());
            if (ExportXlsOptionsView.this.buttonExportPathAuto.getSelection()) {
                this.dsc.setDirPath(EncodeLive.getReportsDir());
            } else {
                this.dsc.setDirPath(exportXlsSetting.getExportDir());
            }
            this.dsc.getItemSelectie().setEnabled(!ExportXlsOptionsView.this.buttonExportPathAuto.getSelection());
        }

        @Override
        public final void save() throws ConfigurationException {
            String numeFisier = this.textFileName.getText();
            if (StringUtils.isEmpty(numeFisier)) {
                numeFisier = "RaportXLS_" + System.currentTimeMillis();
            }
            exportXlsSetting.setAutomaticExportPath(buttonExportPathAuto.getSelection());
            exportXlsSetting.setAutoResizeCols(buttonAutoResizeCols.getSelection());
            exportXlsSetting.setExportDir(this.dsc.getSelectedDirPath());
            exportXlsSetting.setExtension(comboExtension.getText());
            exportXlsSetting.setShowHeader(buttonShowHeader.getSelection());

            ExportXlsOptionsView.this.settings.setNumeFisier(this.dsc.getSelectedDirPath().concat(numeFisier).concat(this.comboExtension.getText()));
            ExportXlsOptionsView.this.settings.setSheetName(this.textSheetName.getText());
            ExportXlsOptionsView.this.settings.setTitlu(this.textHeaderName.getText());
        }

        @Override
        public String getCatName() {
            return ExportXlsOptionsView.ITEM_OPTIONS;
        }

        @Override
        public final boolean validate() {
            if (this.comboExtension.getText().equals(ExportXls.XLSX_EXTENSION)) {
                if (ExportXlsOptionsView.this.settings.getNrOfItems() > ExportXls.MAX_ELEMENTS_FOR_XLSX) {
                    if (SWTeXtension.displayMessageQ("Ati selectat formatul Excel 2007(.xlsx) si va fi generat un fisier care va contine "
                                    + ExportXlsOptionsView.this.settings.getNrOfItems()
                                    + " elemente. Aceste conditii necesita o cantitate considerabila de memorie pentru a se putea efectua. Continuati?",
                            "Posibila operatie de lunga durata") == SWT.NO) {
                        return false;
                    }
                }
            }
            if (this.buttonAutoResizeCols.getSelection()
                    && (ExportXlsOptionsView.this.settings.getNrOfItems() > ExportXls.MAX_ELEMENTS_FOR_AUTO_RESIZE)) {
                if (SWTeXtension.displayMessageQ("Ajustarea automata a dimensiunilor coloanelor pentru mai mult de "
                                + ExportXls.MAX_ELEMENTS_FOR_AUTO_RESIZE
                                + " elemente poate fi o operatie lenta. Doriti sa continuati?",
                        "Posibila operatie de lunga durata") == SWT.NO) {
                    return false;
                }
            }
            if (ExportXlsOptionsView.this.settings.getNrOfItems() > ExportXls.MAX_ROWS) {
                if (SWTeXtension.displayMessageQ("Se vor genera "
                                + ((ExportXlsOptionsView.this.settings.getNrOfItems() / ExportXls.MAX_ROWS) + 1)
                                + " sheet-uri distincte pentru salvarea informatiilor."
                                + " Este necesar ca o cantitate de aproximativ "
                                + (130 * ((ExportXlsOptionsView.this.settings.getNrOfItems() / ExportXls.MAX_ROWS) + 1))
                                + " MB RAM sa fie alocata aplicatiei, "
                                + "pentru ca exportul sa se poata realiza. "
                                + "(Valoarea sugerata poate sa difere in functie de numarul de coloane din "
                                + "tabela, lungimea textului afisat in celule si configuratia sistemului dvs.) Continuam?",
                        "Posibila operatie de lunga durata") == SWT.NO) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public final void handleEvent(final Event e) {
            super.handleEvent(e);
            if (e.type == SWT.Selection) {
                if (e.widget == ExportXlsOptionsView.this.buttonExportPathAuto) {
                    if (ExportXlsOptionsView.this.buttonExportPathAuto.getSelection()) {
                        this.dsc.setDirPath(EncodeLive.getReportsDir().concat(File.separator));
                    } else {
                        this.dsc.setDirPath(exportXlsSetting.getExportDir());
                    }
                    this.dsc.getItemSelectie().setEnabled(!ExportXlsOptionsView.this.buttonExportPathAuto.getSelection());
                }
            }
        }
    }

    private class TableColsSettings extends AbstractIConfigAdapter {

        private ColumnsChooserComposite chooser;

        protected TableColsSettings() {
            super(ExportXlsOptionsView.this.rightForm);
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
            labelName.setText(ExportXlsOptionsView.ITEM_TABLE_COLS);
            labelName.setFont(FontUtil.TAHOMA12_BOLD);
            GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(labelName);

            this.chooser = new ColumnsChooserComposite(
                    this,
                    ExportXlsOptionsView.this.settings.getSwtTable(),
                    ExportXlsOptionsView.this.settings.getClazz(),
                    ExportXlsOptionsView.this.settings.getSufix());

            WidgetCompositeUtil.addColoredFocusListener2Childrens(this);
        }

        @Override
        public final void populateFields() {
            this.chooser.reset();
        }

        @Override
        public String getCatName() {
            return ExportXlsOptionsView.ITEM_TABLE_COLS;
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
            ExportXlsOptionsView.this.settings.setAligns(this.chooser.getAligns());
            ExportXlsOptionsView.this.settings.setDims(this.chooser.getDims());
            ExportXlsOptionsView.this.settings.setSelection(this.chooser.getSelection());
            ExportXlsOptionsView.this.settings.setOrder(this.chooser.getOrder());
        }

    }

    public ExportXlsSettings getSettings() {
        return this.settings;
    }

}
