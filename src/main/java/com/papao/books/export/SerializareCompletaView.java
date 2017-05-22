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
import com.papao.books.ui.util.ObjectUtil;
import com.papao.books.ui.util.WidgetCursorUtil;
import com.papao.books.ui.view.AbstractCViewAdapter;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.widgets.pgroup.FormGroupStrategy;
import org.eclipse.nebula.widgets.pgroup.PGroup;
import org.eclipse.nebula.widgets.pgroup.TwisteToggleRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ExpandAdapter;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

public class SerializareCompletaView extends AbstractCViewAdapter implements Listener {

    private static final Logger logger = Logger.getLogger(SerializareCompletaView.class);

    private DirectorySelectorComposite dsc;
    private DirectorySelectorComposite dscSerializareImagini;
    private Text textFileName;
    private Button buttonExportPathAuto;
    private Button buttonShowNrCrt;
    private Button buttonShowBorder;
    private Button buttonShowTitle;
    private Button buttonSerializareImagini;
    private Text textTitleName;
    private PGroup groupProperties;
    private Map<String, Button> buttonsMap = new HashMap<>();

    // todo export simplu option (nume autori + " - " + nume carte
    // todo checkbox pt serializare imagini
    // todo view design and logic
    // todo selectable property order (use table?)

    public SerializareCompletaView(Shell parent) {
        super(parent, MODE_NONE);

        addComponents();
    }

    private void addComponents() {
        getButtonOk().setText("Export");
        SWTeXtension.addImageChangeListener(getButtonOk(), AppImages.IMG_EXPORT, 16);

        Group groupOptions = new Group(getContainer(), SWT.NONE);
        groupOptions.setText("Optiuni export");
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        groupOptions.setLayoutData(gd);
        groupOptions.setLayout(new GridLayout(1, true));

        Label temp = new Label(groupOptions, SWT.NONE);
        temp.setText("Nume fisier");

        this.textFileName = new Text(groupOptions, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(false, false).hint(250,
                SWT.DEFAULT).minSize(250, SWT.DEFAULT).applyTo(this.textFileName);

        this.buttonExportPathAuto = new Button(groupOptions, SWT.CHECK);
        this.buttonExportPathAuto.setText("cale automata export");

        this.dsc = new DirectorySelectorComposite(groupOptions);
        this.dsc.setDirPath(ApplicationService.getApplicationConfig().getAppOutFolder());

        this.buttonShowNrCrt = new Button(groupOptions, SWT.CHECK);
        this.buttonShowNrCrt.setText("Afisare coloana pentru numar curent");
        WidgetCursorUtil.addHandCursorListener(this.buttonShowNrCrt);

        this.buttonShowBorder = new Button(groupOptions, SWT.CHECK);
        this.buttonShowBorder.setText("Afisare margini celule");
        WidgetCursorUtil.addHandCursorListener(this.buttonShowBorder);

        this.buttonSerializareImagini = new Button(groupOptions, SWT.CHECK);
        this.buttonSerializareImagini.setText("Serializare imagini");
        WidgetCursorUtil.addHandCursorListener(this.buttonSerializareImagini);

        this.dscSerializareImagini = new DirectorySelectorComposite(groupOptions);
        this.dscSerializareImagini.setDirPath(ApplicationService.getApplicationConfig().getAppImagesExportFolder());

        this.buttonShowTitle = new Button(groupOptions, SWT.CHECK);
        this.buttonShowTitle.setText("Afisare denumire raport");
        WidgetCursorUtil.addHandCursorListener(this.buttonShowTitle);
        this.buttonShowTitle.addListener(SWT.Selection, this);

        this.textTitleName = new Text(groupOptions, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(false, false).hint(250,
                SWT.DEFAULT).minSize(250, SWT.DEFAULT).applyTo(this.textTitleName);

        this.groupProperties = new PGroup(getContainer(), SWT.SMOOTH | SWT.RIGHT);
        this.groupProperties.setStrategy(new FormGroupStrategy());
        this.groupProperties.setLinePosition(SWT.BOTTOM);
        this.groupProperties.setToggleRenderer(new TwisteToggleRenderer());

        GridLayoutFactory.fillDefaults().numColumns(4).equalWidth(false).margins(10, 10).applyTo(groupProperties);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(groupProperties);
        this.groupProperties.setText("Proprietati selectabile");


        Composite compFilters = new Composite(groupProperties, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.END).span(4, 1).grab(true,
                false).applyTo(compFilters);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(compFilters);

        ToolItem itemSelectAll = new ToolItem(new ToolBar(compFilters, SWT.FLAT | SWT.RIGHT), SWT.NONE);
        itemSelectAll.setImage(AppImages.getImage16(AppImages.IMG_SELECT_ALL));
        itemSelectAll.setHotImage(AppImages.getImage16Focus(AppImages.IMG_SELECT_ALL));
        itemSelectAll.setToolTipText("Selectare totala");
        itemSelectAll.setText("Selectare totala");
        itemSelectAll.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                selectAll(groupProperties, true);
            }
        });

        ToolItem itemDeselectAll = new ToolItem(itemSelectAll.getParent(), SWT.NONE);
        itemDeselectAll.setImage(AppImages.getImage16(AppImages.IMG_DESELECT_ALL));
        itemDeselectAll.setHotImage(AppImages.getImage16Focus(AppImages.IMG_DESELECT_ALL));
        itemDeselectAll.setToolTipText("Deselectare totala");
        itemDeselectAll.setText("Deselectare totala");
        itemDeselectAll.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                selectAll(groupProperties, false);
            }
        });

        final List<String> fieldNames = ObjectUtil.getFieldNames(new CarteExport(new Carte()));
        for (String fieldName : fieldNames) {
            Button btn = new Button(groupProperties, SWT.CHECK);
            btn.setText(fieldName);
            buttonsMap.put(fieldName, btn);
        }

        groupProperties.setExpanded(false);

        groupProperties.addExpandListener(new ExpandAdapter() {
            @Override
            public void itemCollapsed(ExpandEvent e) {
                super.itemCollapsed(e);
                getShell().setSize(getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT));
            }

            @Override
            public void itemExpanded(ExpandEvent e) {
                super.itemExpanded(e);
                getShell().setSize(getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT));
            }
        });
    }

    private void selectAll(Composite parent, boolean select) {
        Control[] children = parent.getChildren();
        for (Control child : children) {
            if (child instanceof Button) {
                ((Button) child).setSelection(select);
            }
        }
    }

    private List<String> getSelectedFields() {
        List<String> selectedButtons = new ArrayList<>();
        for (Button btn : buttonsMap.values()) {
            if (btn.getSelection()) {
                selectedButtons.add(btn.getText());
            }
        }
        return selectedButtons;
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
            File folder = new File(this.dsc.getSelectedDirPath());
            if (!folder.isDirectory()) {
                SWTeXtension.displayMessageW("Folderul selectat este invalid!");
                return;
            }

            final List<String> fieldNames = getSelectedFields();
            int cols = fieldNames.size();
            if (cols == 0) {
                SWTeXtension.displayMessageW("Selectati cel putin o coloana!");
                return;
            }

            dlgClassic = new CWaitDlgClassic("Va rugam asteptati generarea fisierului...");
            String fileName;
            File output;

            final boolean hasBorder = buttonShowBorder.getSelection();
            final boolean showNrCrt = buttonShowNrCrt.getSelection();
            final boolean hasTitle = buttonShowTitle.getSelection();
            final String titleName = textTitleName.getText();
            final boolean serializareImagini = buttonSerializareImagini.getSelection();

            if (StringUtils.isNotEmpty(textFileName.getText())) {
                fileName = textFileName.getText() + "_" + System.currentTimeMillis();
            } else {
                fileName = "Carti_" + System.currentTimeMillis();
            }

            if (fileName.toLowerCase().endsWith(".txt")) {
                output = new File(fileName);
            } else {
                output = new File(fileName + ".txt");
            }

            TableFormatter tf = new SimpleTableFormatter(hasBorder);
            tf.nextRow();
            if (showNrCrt) {
                tf.nextCell(TableFormatter.ALIGN_CENTER, TableFormatter.VALIGN_CENTER);
                tf.addLine("Nr crt.");
            }
            for (int i = 0; i < fieldNames.size(); i++) {
                tf.nextCell(TableFormatter.ALIGN_CENTER, TableFormatter.VALIGN_CENTER);
                tf.addLine(fieldNames.get(i));
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
                            String temp = this.dscSerializareImagini.getSelectedDirPath() + File.separator +
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
            for (int i = 0; i < cols; i++) {
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

                for (int j = 0; j < cols; j++) {
                    tf.nextCell();
                    Method method = ObjectUtil.getMethod(CarteExport.class, "get" + StringUtils.capitalize(fieldNames.get(j)));
                    tf.addLine((String) method.invoke(carteExport, (Object[]) null));
                }
            }

            dlgClassic.close();
            String[] tbl = tf.getFormattedTable();

            logger.info("ExportTXT content to file : " + fileName);
            ps = new PrintStream(output);

            if (hasTitle) {
                ps.println(titleName);
                ps.println();
            }

            for (int i = 0, size = tbl.length; i < size; i++) {
                ps.println("\t" + tbl[i]);
                if (hasBorder) {
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

    @Override
    protected void customizeView() {
        setViewOptions(ADD_CANCEL | ADD_OK);
        setShellText("Serializare completa");
        setShellImage(AppImages.getImage16(AppImages.IMG_EXPORT));
        setBigViewImage(AppImages.getImage24(AppImages.IMG_EXPORT));
        setBigViewMessage("Serializare completa carti, autori si coperta fata pentru carti");
    }

    @Override
    protected boolean validate() {
        if (this.dsc.getSelectedDirPath() == null) {
            SWTeXtension.displayMessageW("Nu ati ales directorul unde se vor salva imaginile si fisierul de export!");
            return false;
        }
        return true;
    }

    @Override
    protected void saveData() {
        export();
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
                    this.dsc.setDirPath(ApplicationService.getApplicationConfig().getAppOutFolder() + "/full_export" + System.currentTimeMillis());
                }
                this.dsc.getItemSelectie().setEnabled(!buttonExportPathAuto.getSelection());
            }
        } else if (e.type == SWT.FocusIn) {
            if (e.widget == this.textFileName) {
                updateDetailMessage("Numele fisierului care va fi exportat.");
            }
        }
    }
}
