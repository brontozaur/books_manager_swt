package com.papao.books.export.txt;

import com.papao.books.controller.SettingsController;
import com.papao.books.export.AbstractExportView;
import com.papao.books.model.config.ExportTxtSetting;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.auth.EncodeLive;
import com.papao.books.ui.custom.DirectorySelectorComposite;
import com.papao.books.ui.interfaces.AbstractIConfigAdapter;
import com.papao.books.ui.interfaces.IConfig;
import com.papao.books.ui.util.ColorUtil;
import com.papao.books.ui.util.FontUtil;
import com.papao.books.ui.util.WidgetCompositeUtil;
import com.papao.books.ui.util.WidgetCursorUtil;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.ColumnsChooserComposite;
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
    private final static String[] ITEMS = new String[]{
            ExportTxtOptionsView.ITEM_OPTIONS, ExportTxtOptionsView.ITEM_TABLE_COLS};

    private final ExportTxtSettings settings;
    private Button buttonExportPathAuto;
    private ExportTxtSetting exportTxtSetting;

    public ExportTxtOptionsView(final Shell parent, final ExportTxtSettings settings) {
        super(parent);

        this.settings = settings;
        exportTxtSetting = SettingsController.getExportTxtSetting();
        if (exportTxtSetting == null) {
            exportTxtSetting = new ExportTxtSetting();
        }

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
        setBigViewMessage("Configurare export date în format text");
        setShowSaveOKMessage(false);
    }

    @Override
    public void reset() {
        exportTxtSetting.reset();
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
            groupOptions.setText("Opțiuni export");
            gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
            groupOptions.setLayoutData(gd);
            groupOptions.setLayout(new GridLayout(1, true));

            temp = new Label(groupOptions, SWT.NONE);
            temp.setText("Nume fișier");

            this.textFileName = new Text(groupOptions, SWT.BORDER);
            GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(false, false).hint(250,
                    SWT.DEFAULT).minSize(250, SWT.DEFAULT).applyTo(this.textFileName);
            this.textFileName.addListener(SWT.FocusIn, this);
            this.textFileName.addListener(SWT.Modify, this);

            ExportTxtOptionsView.this.buttonExportPathAuto = new Button(groupOptions, SWT.CHECK);
            ExportTxtOptionsView.this.buttonExportPathAuto.setText("cale automată export");
            ExportTxtOptionsView.this.buttonExportPathAuto.addListener(SWT.Selection, this);

            this.dsc = new DirectorySelectorComposite(groupOptions);

            this.buttonShowNrCrt = new Button(groupOptions, SWT.CHECK);
            this.buttonShowNrCrt.setText("Afișare coloană pentru număr curent");
            WidgetCursorUtil.addHandCursorListener(this.buttonShowNrCrt);
            this.buttonShowNrCrt.addListener(SWT.Selection, this);

            this.buttonShowBorder = new Button(groupOptions, SWT.CHECK);
            this.buttonShowBorder.setText("Afișare margini celule");
            WidgetCursorUtil.addHandCursorListener(this.buttonShowBorder);

            this.buttonShowTitle = new Button(groupOptions, SWT.CHECK);
            this.buttonShowTitle.setText("Afișare denumire raport");
            WidgetCursorUtil.addHandCursorListener(this.buttonShowTitle);
            this.buttonShowTitle.addListener(SWT.Selection, this);

            this.textTitleName = new Text(groupOptions, SWT.BORDER);
            GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(false, false).hint(250,
                    SWT.DEFAULT).minSize(250, SWT.DEFAULT).applyTo(this.textTitleName);
        }

        @Override
        public final void populateFields() {
            this.textFileName.setText(ExportTxtOptionsView.this.settings.getNumeFisier());
            ExportTxtOptionsView.this.buttonExportPathAuto.setSelection(exportTxtSetting.isAutomaticExportPath());
            if (ExportTxtOptionsView.this.buttonExportPathAuto.getSelection()) {
                this.dsc.setDirPath(EncodeLive.getReportsDir());
            } else {
                this.dsc.setDirPath(exportTxtSetting.getExportDir());
            }
            this.dsc.getItemSelectie().setEnabled(!ExportTxtOptionsView.this.buttonExportPathAuto.getSelection());

            this.buttonShowNrCrt.setSelection(exportTxtSetting.isShowNrCrt());
            this.buttonShowTitle.setSelection(exportTxtSetting.isShowTitle());
            this.textTitleName.setText(ExportTxtOptionsView.this.settings.getTitlu());
            this.textTitleName.setEnabled(this.buttonShowTitle.getSelection());
            this.buttonShowBorder.setSelection(exportTxtSetting.isHasBorder());
        }

        @Override
        public final void save() {
            String numeFisier = this.textFileName.getText();
            if (StringUtils.isEmpty(numeFisier)) {
                numeFisier = "RaportTXT_" + System.currentTimeMillis();
            }
            exportTxtSetting.setAutomaticExportPath(buttonExportPathAuto.getSelection());
            exportTxtSetting.setExportDir(this.dsc.getSelectedDirPath());
            exportTxtSetting.setHasBorder(buttonShowBorder.getSelection());
            exportTxtSetting.setShowNrCrt(buttonShowNrCrt.getSelection());
            exportTxtSetting.setShowTitle(buttonShowTitle.getSelection());
            SettingsController.saveExportTxtSetting(exportTxtSetting);

            settings.setNumeFisier(this.dsc.getSelectedDirPath().concat(numeFisier));
            settings.setTitlu(this.textTitleName.getText());
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
                        updateDetailMessage("Prima coloana a raportului va afișa numărul curent al elementelor.");
                    } else {
                        updateDetailMessage("Nu se vor numerota elementele afișate.");
                    }
                } else if (e.widget == this.buttonShowTitle) {
                    this.textTitleName.setEnabled(this.buttonShowTitle.getSelection());
                } else if (e.widget == ExportTxtOptionsView.this.buttonExportPathAuto) {
                    if (ExportTxtOptionsView.this.buttonExportPathAuto.getSelection()) {
                        this.dsc.setDirPath(EncodeLive.getReportsDir().concat(File.separator));
                    } else {
                        this.dsc.setDirPath(exportTxtSetting.getExportDir());
                    }
                    this.dsc.getItemSelectie().setEnabled(!ExportTxtOptionsView.this.buttonExportPathAuto.getSelection());
                }
            } else if (e.type == SWT.FocusIn) {
                if (e.widget == this.textFileName) {
                    updateDetailMessage("Numele fițierului care va fi exportat.");
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
                    ExportTxtOptionsView.this.settings.getTableKey());

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
        public final void save() {
            this.chooser.save(false);
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
