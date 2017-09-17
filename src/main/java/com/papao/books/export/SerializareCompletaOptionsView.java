package com.papao.books.export;

import com.papao.books.ApplicationService;
import com.papao.books.model.Carte;
import com.papao.books.model.CarteExport;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.auth.EncodeLive;
import com.papao.books.ui.custom.DirectorySelectorComposite;
import com.papao.books.ui.interfaces.AbstractIConfigAdapter;
import com.papao.books.ui.interfaces.IConfig;
import com.papao.books.ui.util.*;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.ColumnsChooserCompositeString;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SerializareCompletaOptionsView extends AbstractExportView implements Listener {

    private final static String ITEM_OPTIONS = "Optiuni export";
    private final static String ITEM_TABLE_COLS = "Coloane selectabile";
    private final static String[] ITEMS = new String[]{
            ITEM_OPTIONS, ITEM_TABLE_COLS};

    boolean showBorder = false;
    boolean showNrCrt = false;
    boolean showTitle = false;
    boolean simpleExport = true;
    String titleName = "Carti";
    String fileName;
    boolean serializareImagini = false;
    String selectedImagesFolder;
    List<FieldColumnValue> selectedFields = new ArrayList<>();
    List<String> sortProperties = new ArrayList<>();
    private boolean exportPathAuto = true;

    public SerializareCompletaOptionsView(Shell parent) {
        super(parent);

        for (String str : ITEMS) {
            new TableItem(this.leftTable, SWT.NONE).setText(str);
        }

        this.mapComponents.put(ITEM_OPTIONS, new ExportSettings());
        this.mapComponents.put(ITEM_TABLE_COLS, new TableColsSettings());

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
        setBigViewMessage("Serializare completa carti, autori si coperta fata pentru carti");
        setShowSaveOKMessage(false);
    }

    @Override
    public void reset() {
        for (IConfig cfg : this.mapComponents.values()) {
            cfg.populateFields();
        }
    }

    private class ExportSettings extends AbstractIConfigAdapter {
        private Text textFileName;
        private DirectorySelectorComposite dsc;
        private Button buttonShowNrCrt;
        private Button buttonExportPathAuto;
        private Button buttonShowBorder;
        private Button buttonSimpleExport;
        private Button buttonShowTitle;
        private Text textTitleName;
        private DirectorySelectorComposite dscSerializareImagini;
        private Button buttonSerializareImagini;

        protected ExportSettings() {
            super(rightForm);
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
            labelName.setText(ITEM_OPTIONS);
            labelName.setFont(FontUtil.TAHOMA12_BOLD);
            GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(labelName);

            this.buttonSimpleExport = new Button(this, SWT.CHECK);
            this.buttonSimpleExport.setText("Export simplu");
            WidgetCursorUtil.addHandCursorListener(this.buttonSimpleExport);

            groupOptions = new Group(this, SWT.NONE);
            groupOptions.setText("Optiuni export");
            gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
            groupOptions.setLayoutData(gd);
            groupOptions.setLayout(new GridLayout(1, true));

            buttonSimpleExport.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    WidgetCompositeUtil.enableGUI(groupOptions, !buttonSimpleExport.getSelection());
                }
            });

            temp = new Label(groupOptions, SWT.NONE);
            temp.setText("Nume fisier");

            this.textFileName = new Text(groupOptions, SWT.BORDER);
            GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(false, false).hint(250,
                    SWT.DEFAULT).minSize(250, SWT.DEFAULT).applyTo(this.textFileName);
            this.textFileName.addListener(SWT.FocusIn, this);
            this.textFileName.addListener(SWT.Modify, this);

            buttonExportPathAuto = new Button(groupOptions, SWT.CHECK);
            buttonExportPathAuto.setText("cale automata export");
            buttonExportPathAuto.addListener(SWT.Selection, this);

            this.dsc = new DirectorySelectorComposite(groupOptions);

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

            this.buttonSerializareImagini = new Button(groupOptions, SWT.CHECK);
            this.buttonSerializareImagini.setText("Serializare imagini");
            WidgetCursorUtil.addHandCursorListener(this.buttonSerializareImagini);

            this.dscSerializareImagini = new DirectorySelectorComposite(groupOptions);
            this.dscSerializareImagini.setDirPath(ApplicationService.getApplicationConfig().getAppImagesExportFolder());
        }

        @Override
        public final void populateFields() {
            this.textFileName.setText("Carti_" + System.currentTimeMillis());
            buttonExportPathAuto.setSelection(exportPathAuto);
            if (buttonExportPathAuto.getSelection()) {
                this.dsc.setDirPath(EncodeLive.getReportsDir());
            }
            this.dsc.getItemSelectie().setEnabled(!buttonExportPathAuto.getSelection());

            this.dscSerializareImagini.setDirPath(ApplicationService.getApplicationConfig().getAppImagesExportFolder());
            this.dscSerializareImagini.getItemSelectie().setEnabled(!buttonSerializareImagini.getSelection());

            this.buttonShowNrCrt.setSelection(showNrCrt);
            this.buttonShowTitle.setSelection(showTitle);
            this.textTitleName.setText(titleName);
            this.textTitleName.setEnabled(this.buttonShowTitle.getSelection());
            this.buttonShowBorder.setSelection(showBorder);
            this.buttonSerializareImagini.setSelection(serializareImagini);

        }

        @Override
        public final void save() {
            String numeFisier = this.textFileName.getText();
            if (StringUtils.isEmpty(numeFisier)) {
                numeFisier = "RaportTXT_" + System.currentTimeMillis();
            }
            exportPathAuto = buttonExportPathAuto.getSelection();
            showBorder = buttonShowBorder.getSelection();
            showNrCrt = buttonShowNrCrt.getSelection();
            showTitle = buttonShowTitle.getSelection();
            serializareImagini = buttonSerializareImagini.getSelection();
            selectedImagesFolder = dscSerializareImagini.getSelectedDirPath();
            titleName = this.textTitleName.getText();
            fileName = this.dsc.getSelectedDirPath().concat(numeFisier);
            simpleExport = buttonSimpleExport.getSelection();
        }

        @Override
        public String getCatName() {
            return ITEM_OPTIONS;
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
                } else if (e.widget == buttonExportPathAuto) {
                    if (buttonExportPathAuto.getSelection()) {
                        this.dsc.setDirPath(EncodeLive.getReportsDir().concat(File.separator));
                    }
                    this.dsc.getItemSelectie().setEnabled(!buttonExportPathAuto.getSelection());
                } else if (e.widget == buttonSerializareImagini) {
                    if (buttonSerializareImagini.getSelection()) {
                        this.dscSerializareImagini.setDirPath(ApplicationService.getApplicationConfig().getAppOutFolder().concat(File.separator));
                    }
                    this.dscSerializareImagini.getItemSelectie().setEnabled(!buttonSerializareImagini.getSelection());
                }
            } else if (e.type == SWT.FocusIn) {
                if (e.widget == this.textFileName) {
                    updateDetailMessage("Numele fisierului care va fi exportat.");
                }
            }
        }
    }

    private class TableColsSettings extends AbstractIConfigAdapter {

        private ColumnsChooserCompositeString chooser;

        protected TableColsSettings() {
            super(rightForm);
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
            labelName.setText(ITEM_TABLE_COLS);
            labelName.setFont(FontUtil.TAHOMA12_BOLD);
            GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(labelName);

            this.chooser = new ColumnsChooserCompositeString(this, ObjectUtil.getFieldNames(new CarteExport(new Carte())), true);

            WidgetCompositeUtil.addColoredFocusListener2Childrens(this);
        }

        @Override
        public final void populateFields() {
            this.chooser.reset();
        }

        @Override
        public String getCatName() {
            return ITEM_TABLE_COLS;
        }

        @Override
        public final boolean validate() {
            return this.chooser.validate();
        }

        @Override
        public final void save() {
            this.chooser.save();
            boolean[] gridVisibility = this.chooser.getSelection();
            int[] gridOrder = this.chooser.getOrder();
            for (int i = 0; i < chooser.getColumnNames().size(); i++) {
                if (gridVisibility[gridOrder[i]]) {
                    String fieldName = chooser.getColumnNames().get(gridOrder[i]);
                    int align = this.chooser.getAligns()[gridOrder[i]];
                    int width = this.chooser.getDims()[gridOrder[i]];
                    selectedFields.add(new FieldColumnValue(fieldName, align, width, gridOrder[i]));
                    if (this.chooser.getSort()[gridOrder[i]]) {
                        sortProperties.add(fieldName);
                    }
                }
            }
        }
    }
}
