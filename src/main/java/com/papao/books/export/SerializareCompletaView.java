package com.papao.books.export;

import com.inamik.utils.SimpleTableFormatter;
import com.inamik.utils.TableFormatter;
import com.mongodb.gridfs.GridFSDBFile;
import com.papao.books.ApplicationService;
import com.papao.books.controller.ApplicationController;
import com.papao.books.controller.ReportController;
import com.papao.books.model.ApplicationReport;
import com.papao.books.model.Carte;
import com.papao.books.model.CarteExport;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.auth.EncodeLive;
import com.papao.books.ui.custom.CWaitDlgClassic;
import com.papao.books.ui.custom.DirectorySelectorComposite;
import com.papao.books.ui.interfaces.AbstractIConfigAdapter;
import com.papao.books.ui.interfaces.IConfig;
import com.papao.books.ui.util.*;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.ColumnsChooserCompositeString;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

public class SerializareCompletaView extends AbstractExportView implements Listener {

    private static final Logger logger = Logger.getLogger(SerializareCompletaView.class);

    private final static String ITEM_OPTIONS = "Optiuni export";
    private final static String ITEM_TABLE_COLS = "Coloane selectabile";
    private final static String[] ITEMS = new String[]{
            ITEM_OPTIONS, ITEM_TABLE_COLS};

    private boolean showBorder = true;
    private boolean showNrCrt = true;
    private boolean showTitle = true;
    private String titleName = "Carti";
    private String fileName;
    private boolean serializareImagini = true;
    private boolean exportPathAuto = true;
    private String selectedDirPath;
    private String selectedImagesFolder;

    private int[] gridDims;
    private int[] gridAligns;
    private int[] gridOrder;
    private boolean[] gridVisibility;
    List<String> fieldNames;

    // todo export simplu option (nume autori + " - " + nume carte
    // todo checkbox pt serializare imagini
    // todo view design and logic
    // todo selectable property order (use table?)

    public SerializareCompletaView(Shell parent) {
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
            } else {
//                this.dsc.setDirPath(exportTxtSetting.getExportDir());
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
            selectedDirPath = this.dsc.getSelectedDirPath();
            showBorder = buttonShowBorder.getSelection();
            showNrCrt = buttonShowNrCrt.getSelection();
            showTitle = buttonShowTitle.getSelection();
            serializareImagini = buttonSerializareImagini.getSelection();
            selectedImagesFolder = dscSerializareImagini.getSelectedDirPath();
            titleName = this.textTitleName.getText();
            fileName = this.dsc.getSelectedDirPath().concat(numeFisier);
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
                    } else {
//                        this.dsc.setDirPath(exportTxtSetting.getExportDir());
                    }
                    this.dsc.getItemSelectie().setEnabled(!buttonExportPathAuto.getSelection());
                } else if (e.widget == buttonSerializareImagini) {
                    if (buttonSerializareImagini.getSelection()) {
                        this.dscSerializareImagini.setDirPath(ApplicationService.getApplicationConfig().getAppOutFolder().concat(File.separator));
                    } else {
//                        this.dsc.setDirPath(exportTxtSetting.getExportDir());
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

            this.chooser = new ColumnsChooserCompositeString(this, ObjectUtil.getFieldNames(new CarteExport(new Carte())));

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
            gridAligns = this.chooser.getAligns();
            gridDims = this.chooser.getDims();
            gridVisibility = this.chooser.getSelection();
            gridOrder = this.chooser.getOrder();
            fieldNames = this.chooser.getColumnNames();
        }
    }

    private String getExtension(String filePath) {
        if (filePath == null) {
            return ".jpg";
        }
        if (filePath.contains(".")) {
            return filePath.substring(filePath.lastIndexOf("."));
        }
        return ".jpg";
    }

    private void export() {
        PrintStream ps = null;
        CWaitDlgClassic dlgClassic = null;
        try {
            if (!validate()) {
                return;
            }

            boolean atLeastOneCol = false;
            for (int i = 0; i < gridVisibility.length; i++) {
                if (gridVisibility[i]) {
                    atLeastOneCol = true;
                    break;
                }
            }

            if (!atLeastOneCol) {
                gridVisibility[0] = true;
            }


            dlgClassic = new CWaitDlgClassic("Va rugam asteptati generarea fisierului...");
            File output;

            if (StringUtils.isEmpty(fileName)) {
                fileName = "Carti_" + System.currentTimeMillis();
            }

            if (fileName.toLowerCase().endsWith(".txt")) {
                output = new File(fileName);
            } else {
                output = new File(fileName + ".txt");
            }

            Map<Integer, Integer> dims = new TreeMap<Integer, Integer>();
            for (int i = 0; i < fieldNames.size(); i++) {
                if (gridVisibility[gridOrder[i]]) {
                    dims.put(i, gridDims[gridOrder[i]]);
                }
            }
            int[] sizes;
            int w = 0;
            if (showNrCrt) {
                sizes = new int[dims.size() + 1];
                sizes[0] = 70;
                w = 1;
            } else {
                sizes = new int[dims.size()];
            }
            for (Iterator<Integer> it = dims.values().iterator(); it.hasNext(); ) {
                sizes[w] = it.next();
                w++;
            }

            SimpleTableFormatter tf = new SimpleTableFormatter(showBorder);
            tf.nextRow();
            if (showNrCrt) {
                tf.nextCell(TableFormatter.ALIGN_CENTER, TableFormatter.VALIGN_CENTER);
                tf.addLine("Nr crt.");
            }
            for (int i = 0; i < sizes.length; i++) {
                if (gridVisibility[gridOrder[i]]) {
                    tf.nextCell(getAlign(gridAligns[gridOrder[i]]), TableFormatter.VALIGN_CENTER);
                    tf.addLine(fieldNames.get(gridOrder[i]));
                }
            }

            List<Carte> allBooks = ApplicationService.getBookController().getRepository().findAll();
            List<CarteExport> exportBooks = new ArrayList<>();

            for (int i = 0; i < allBooks.size(); i++) {
                Carte carte = allBooks.get(i);
                CarteExport ce = new CarteExport(carte);
                exportBooks.add(ce);
                if (serializareImagini) {
                    if (carte.getCopertaFata().exists()) {
                        GridFSDBFile image = ApplicationController.getDocumentData(carte.getCopertaFata().getId());
                        if (image != null) {
                            String temp = selectedImagesFolder + File.separator +
                                    ce.getAutori() + " - " + ce.getTitlu().replaceAll("[^a-zA-Z0-9.-]", "_") + getExtension((String) image.getMetaData().get("localFilePath"));
                            File file = new File(temp);
                            image.writeTo(file);
                        } else {
                            logger.error("Image not found for book with id " + ce.getId());
                        }
                    }
                }
            }

            exportBooks.sort(Comparator.comparing(CarteExport::getAutori));

            dlgClassic.setMax(exportBooks.size());
            dlgClassic.open();

            tf.nextRow();
            if (showNrCrt) {
                tf.nextCell();
                tf.addLine("");
            }
            for (int i = 0; i < sizes.length; i++) {
                tf.nextCell();
                tf.addLine("");
            }

            for (int i = 0; i < exportBooks.size(); i++) {
                if (i % 5 == 0) {
                    dlgClassic.advance(i);
                    Display.getDefault().readAndDispatch();
                }
                tf.nextRow();
                if (showNrCrt) {
                    tf.nextCell();
                    tf.addLine(String.valueOf(i + 1));
                }
                CarteExport carteExport = exportBooks.get(i);

                for (int j = 0; j < sizes.length; j++) {
                    tf.nextCell();
                    Method method = ObjectUtil.getMethod(CarteExport.class, "get" + StringUtils.capitalize(fieldNames.get(j)));
                    tf.addLine((String) method.invoke(carteExport, (Object[]) null));
                }
            }

            dlgClassic.close();
            String[] tbl = tf.getFormattedTable();

            logger.info("ExportTXT content to file : " + fileName);
            ps = new PrintStream(output);

            if (showTitle) {
                ps.println(titleName);
                ps.println();
            }

            for (int i = 0, size = tbl.length; i < size; i++) {
                ps.println("\t" + tbl[i]);
                if (showBorder) {
                    if (i == 2) {
                        ps.println();
                        i++;
                    }
                } else if (i == 0) {
                    ps.println();
                }
            }
            ps.println();
            ps.println("Raport generat cu Books Manager, https://github.com/brontozaur");
            ps.close();
            logger.info("ExportTXT content to file completed succesfully.");

            ApplicationReport dbRap = new ApplicationReport();
            dbRap.setCale(output.getCanonicalPath());
            dbRap.setIdUser(EncodeLive.getIdUser());
            dbRap.setNume(titleName);
            dbRap.setType(ExportType.TXT);

            ReportController.save(dbRap);

            VizualizareRapoarte.showRaport(dbRap);
        } catch (Exception exc) {
            if (dlgClassic != null) {
                dlgClassic.close();
            }
            logger.error(exc.getMessage(), exc);
            SWTeXtension.displayMessageE("A intervenit o eroare la generarea fisierului.", exc);
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (dlgClassic != null) {
                dlgClassic.close();
            }
        }
    }

    private static int getAlign(final int swtConstant) {
        if (swtConstant == SWT.LEFT) {
            return TableFormatter.ALIGN_LEFT;
        } else if (swtConstant == SWT.RIGHT) {
            return TableFormatter.ALIGN_RIGHT;
        } else if (swtConstant == SWT.CENTER) {
            return TableFormatter.ALIGN_CENTER;
        }
        return TableFormatter.ALIGN_DEFAULT;
    }
}
