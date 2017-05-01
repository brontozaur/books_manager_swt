package com.papao.books.view.preluari;

import com.papao.books.controller.ApplicationReportController;
import com.papao.books.export.ExportType;
import com.papao.books.export.Exporter;
import com.papao.books.view.AppImages;
import com.papao.books.view.auth.EncodeLive;
import com.papao.books.view.bones.impl.view.AbstractCViewAdapter;
import com.papao.books.view.custom.CWaitDlgClassic;
import com.papao.books.view.custom.ProgressBarComposite;
import com.papao.books.view.interfaces.IEncodeExport;
import com.papao.books.view.interfaces.IEncodeHelp;
import com.papao.books.view.interfaces.IEncodeReset;
import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.util.WidgetTableUtil;
import com.papao.books.view.util.importers.ReadExcelFileWithJXL;
import com.papao.books.view.util.importers.ReadExcelFileWithPOI;
import com.papao.books.view.util.importers.ReadTabDelimitedFile;
import com.papao.books.view.view.AbstractView;
import com.papao.books.view.view.InfoView;
import com.papao.books.view.view.SWTeXtension;
import com.papao.books.view.view.TableView;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Descriere : Orice implementare a acestei clase va trebui sa specifice cod doar pentru metodele
 * {@link AbstractPreluareDateM2View#save2Db()} si {@link com.papao.books.view.view.AbstractView#validate()} pentru a avea
 * implementata o noua metoda de preluare. </p>
 */
public abstract class AbstractPreluareDateM2View extends AbstractCViewAdapter implements IEncodeReset,
        IEncodeHelp, IEncodeExport {

    private static final Logger logger = Logger.getLogger(AbstractPreluareDateM2View.class);

    private ToolItem itemOpenFile;
    protected ToolItem itemValidateFile;
    protected ToolItem itemPreluare;
    private ToolBar barOps;
    private ProgressBarComposite cpBar;
    protected Composite compSuport;
    public boolean ready4Import = false;
    public String[] columnLabels;
    public String[] columnDescriptions;
    protected Table tableDocumente;
    private ApplicationReportController controller;
    private Text textDelimitator;
    private String delimitator;

    public AbstractPreluareDateM2View(final Shell parent,
                                      final String[] columnLabels,
                                      final String[] columnDescriptions,
                                      ApplicationReportController controller) {
        super(parent, AbstractView.MODE_NONE);
        this.controller = controller;

        if (columnLabels == null) {
            this.columnLabels = new String[0];
        } else {
            this.columnLabels = columnLabels.clone();
        }
        if (columnDescriptions == null) {
            this.columnDescriptions = new String[0];
        } else {
            this.columnDescriptions = columnDescriptions.clone();
        }

        addComponents();
    }

    private void addComponents() {
        this.compSuport = new Composite(getContainer(), SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this.compSuport);
        GridLayoutFactory.fillDefaults().numColumns(6).equalWidth(false).applyTo(this.compSuport);

        barOps = new ToolBar(this.compSuport, SWT.RIGHT | SWT.FLAT);
        GridDataFactory.fillDefaults().grab(false, false).align(SWT.FILL, SWT.CENTER).applyTo(this.barOps);

        this.itemOpenFile = new ToolItem(barOps, SWT.PUSH);
        itemOpenFile.setImage(AppImages.getImage16(AppImages.IMG_IMPORT));
        itemOpenFile.setHotImage(AppImages.getImage16Focus(AppImages.IMG_IMPORT));
        itemOpenFile.setToolTipText("Import fisier (prima linie este rezervata pentru denumirea coloanelor)");
        itemOpenFile.setText("&Import");
        itemOpenFile.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                loadDataFile(null);
                if (!tableDocumente.isDisposed()) {
                    updateDetailMessage("Aveti " + tableDocumente.getItemCount()
                            + " inregistrari in tabel.");
                }
            }
        });

        this.itemValidateFile = new ToolItem(barOps, SWT.PUSH);
        itemValidateFile.setImage(AppImages.getImage16(AppImages.IMG_OK));
        itemValidateFile.setHotImage(AppImages.getImage16Focus(AppImages.IMG_OK));
        itemValidateFile.setToolTipText("Validare fisier");
        itemValidateFile.setText("&Validare");
        itemValidateFile.setEnabled(false);
        itemValidateFile.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                try {
                    tableDocumente.setFocus();
                    AbstractPreluareDateM2View.this.ready4Import = validate();
                    itemPreluare.setEnabled(AbstractPreluareDateM2View.this.ready4Import);
                    if (AbstractPreluareDateM2View.this.ready4Import) {
                        if (!tableDocumente.isDisposed()) {
                            updateDetailMessage("Aveti " + tableDocumente.getItemCount()
                                    + " inregistrari valide in tabel.");
                        }
                    } else {
                        if (!tableDocumente.isDisposed()) {
                            updateDetailMessage("Aveti " + tableDocumente.getItemCount()
                                    + " inregistrari in tabel.");
                        }
                    }
                } catch (Exception exc) {
                    logger.error(exc.getMessage(), exc);
                    SWTeXtension.displayMessageEGeneric(exc);
                }
            }
        });

        this.itemPreluare = new ToolItem(barOps, SWT.PUSH);
        itemPreluare.setImage(AppImages.getImage16(AppImages.IMG_EXPORT));
        itemPreluare.setHotImage(AppImages.getImage16Focus(AppImages.IMG_EXPORT));
        itemPreluare.setToolTipText("Start preluare");
        itemPreluare.setText("&Preluare");
        itemPreluare.setEnabled(false);
        itemPreluare.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                preluareDate();
            }
        });

        new Label(compSuport, SWT.NONE).setText("Delimitator:");
        textDelimitator = new Text(compSuport, SWT.BORDER);
        textDelimitator.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                delimitator = textDelimitator.getText();
            }
        });

        this.cpBar = new ProgressBarComposite(
                this.compSuport,
                Integer.MAX_VALUE,
                ColorUtil.COLOR_ROSU_SEMI_ROSU,
                SWT.SMOOTH);
        cpBar.setVisible(false);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(cpBar);

        this.tableDocumente = new Table(getContainer(), SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION
                | SWT.H_SCROLL | SWT.V_SCROLL);
        tableDocumente.setLinesVisible(true);
        tableDocumente.setHeaderVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).hint(550, 300).applyTo(tableDocumente);
        SWTeXtension.addColoredFocusListener(tableDocumente, null);
        WidgetTableUtil.addCustomGradientSelectionListenerToTable(tableDocumente, null, null);
        tableDocumente.setMenu(createTableMenu());
        tableDocumente.addListener(SWT.KeyDown, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                if (e.widget == tableDocumente) {
                    if ((e.stateMask & (EncodeLive.IS_MAC ? SWT.COMMAND : SWT.CTRL)) != 0) {
                        if ((e.keyCode == 'A') || (e.keyCode == 'a')) {
                            tableDocumente.selectAll();
                        } else if ((e.keyCode == 'V') || (e.keyCode == 'v')) {
                            if (StringUtils.isEmpty(delimitator) && columnLabels.length > 1) {
                                updateDetailMessage("Nu ati introdus delimitatorul!");
                                textDelimitator.setFocus();
                                return;
                            }
                            pasteFromClipboard();
                        }
                    }
                    if (e.character == SWT.DEL) {
                        del();
                        if (!tableDocumente.isDisposed()) {
                            updateDetailMessage("Aveti " + tableDocumente.getItemCount()
                                    + " inregistrari in tabel.");
                        }
                    }
                }
            }
        });
        tableDocumente.addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                mod();
            }
        });

        TableColumn col;
        for (String str : this.columnLabels) {
            col = new TableColumn(tableDocumente, SWT.NONE);
            col.setText(str);
            col.setWidth(150);
        }
        col = new TableColumn(tableDocumente, SWT.NONE);
        col.setText("Rezultat");
        col.setWidth(250);

		/*
         * definim acum capabilitatile de Drag and Drop ale tabelei
		 */

        DropTarget dt = new DropTarget(tableDocumente, DND.DROP_DEFAULT | DND.DROP_MOVE);
        dt.setTransfer(new Transfer[]{
                FileTransfer.getInstance()});
        dt.addDropListener(new DropTargetAdapter() {
            @Override
            public final void drop(final DropTargetEvent event) {
                String fileList[] = null;
                FileTransfer ft = FileTransfer.getInstance();
                if (ft.isSupportedType(event.currentDataType)) {
                    fileList = (String[]) event.data;
                    if ((fileList != null) && (fileList.length > 0)) {
                        if ((tableDocumente.getItemCount() > 0)
                                && (SWTeXtension.displayMessageQ("Atentie. Prin importare datele din tabela se pierd. Continuati?",
                                "Confirmare import fisier") == SWT.NO)) {
                            return;
                        }
                        loadDataFile(fileList[0]);
                        if (!tableDocumente.isDisposed()) {
                            updateDetailMessage("Aveti " + tableDocumente.getItemCount()
                                    + " inregistrari in tabel.");
                        }
                    }
                }
            }
        });
    }

    protected void pasteFromClipboard() {
        clearTable(true);
        Clipboard clipboard = new Clipboard(Display.getDefault());
        String plainText = (String) clipboard.getContents(TextTransfer.getInstance());
        clipboard.dispose();
        String[] rows = plainText.split("\n");

        for (String row : rows) {
            String[] values = columnLabels.length > 1 ? row.split(delimitator) : new String[]{row};
            if (values.length != this.columnLabels.length) {
                SWTeXtension.displayMessageW("Continut invalid. Verificati structura textului pe care \ndoriti sa-l importati si delimitatorul ales.");
                clearTable(false);
                return;
            }
            new TableItem(tableDocumente, SWT.NONE).setText(values);
        }
        itemValidateFile.setEnabled(tableDocumente.getItemCount() > 0);
        itemPreluare.setEnabled(false);
    }

    private Menu createTableMenu() {
        if ((tableDocumente == null) || tableDocumente.isDisposed()) {
            return null;
        }
        final Menu menu = new Menu(tableDocumente);
        MenuItem menuItem;
        try {
            menu.addListener(SWT.Show, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    int idx = 0;
                    final int selIdx = tableDocumente.getSelectionIndex();
                    try {
                        menu.getItem(idx++).setEnabled(tableDocumente.getColumnCount() > 0); // add
                        menu.getItem(idx++).setEnabled(selIdx != -1); // mod
                        menu.getItem(idx++).setEnabled(selIdx != -1); // del
                    } catch (Exception exc) {
                        logger.error(exc, exc);
                        SWTeXtension.displayMessageEGeneric(exc);
                    }
                }
            });

            menuItem = new MenuItem(menu, SWT.NONE);
            menuItem.setText("Adaugare");
            menuItem.setImage(AppImages.getImage16(AppImages.IMG_PLUS));
            menuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    add();
                    if (!tableDocumente.isDisposed()) {
                        updateDetailMessage("Aveti " + tableDocumente.getItemCount()
                                + " inregistrari in tabel.");
                    }
                }
            });

            menuItem = new MenuItem(menu, SWT.NONE);
            menuItem.setText("Modificare");
            menuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    mod();
                }
            });

            menuItem = new MenuItem(menu, SWT.NONE);
            menuItem.setText("Stergere	Del");
            menuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    del();
                    if (!tableDocumente.isDisposed()) {
                        updateDetailMessage("Aveti " + tableDocumente.getItemCount()
                                + " inregistrari in tabel.");
                    }
                }
            });
        } catch (Exception exc) {
            logger.error(exc, exc);
            SWTeXtension.displayMessageEGeneric(exc);
        }
        return menu;
    }

    private void preluareDate() {
        try {
            if ((tableDocumente == null) || tableDocumente.isDisposed()) {
                return;
            }
            if (tableDocumente.getItemCount() == 0) {
                SWTeXtension.displayMessageW("Nu avem ce prelua...");
                return;
            }
            long time = System.currentTimeMillis();
            save2Db();
            logger.info("Preluare executata OK in "
                    + ((System.currentTimeMillis() - time) / 1000D) + " sec");
        } catch (Exception exc) {
            logger.error(exc, exc);
            SWTeXtension.displayMessageEGeneric(exc);
        }
    }

    protected final void clearTable(final boolean ask) {
        try {
            if ((tableDocumente == null) || tableDocumente.isDisposed()) {
                itemPreluare.setEnabled(false);
                return;
            }
            if ((tableDocumente.getItemCount() > 0) && ask) {
                if (SWTeXtension.displayMessageQ("Sunteti sigur ca doriti sa stergeti liniile importate?",
                        "Stergere linii importate") == SWT.NO) {
                    return;
                }
            }
            itemPreluare.setEnabled(false);
            itemValidateFile.setEnabled(false);
            tableDocumente.removeAll();
            if (cpBar.isVisible()) {
                cpBar.setVisible(false);
            }
            this.ready4Import = false;
            if (!tableDocumente.isDisposed()) {
                updateDetailMessage("Aveti " + tableDocumente.getItemCount()
                        + " inregistrari in tabel.");
            }
        } catch (Exception exc) {
            logger.error(exc, exc);
            SWTeXtension.displayMessageEGeneric(exc);
        }
    }

    /**
     * @return a ArrayList<String[]> not empty in case of succes or empty otherwise.
     */
    protected final List<String[]> loadFileContentIntoMemory(final String filePath) {
        FileDialog dlg;
        String fileName;
        List<String[]> values = new ArrayList<>();
        try {
            if (StringUtils.isEmpty(filePath)) {
                dlg = new FileDialog(tableDocumente.getShell(), SWT.OPEN);
                dlg.setFilterNames(new String[]{
                        "Text (Delimitat de tabulatori)(*.txt)",
                        "XLS (Registru de lucru Excel)(*.xls)",
                        "CSV (Delimitat de virgule) (*.csv)"});
                dlg.setFilterExtensions(new String[]{
                        "*.txt", "*.xls", "*.csv"});
                fileName = dlg.open();
            } else {
                fileName = filePath;
            }
            if (StringUtils.isEmpty(fileName)) {
                return values;
            }
            setDlgMessage("Incarcare fisier date....");
            if (fileName.toLowerCase().endsWith("xls")) {
                try {
                    values = ReadExcelFileWithJXL.readExcelFile(fileName);
                } catch (Exception exc) {
                    values = ReadExcelFileWithPOI.readExcelFile(fileName);
                }
            } else if (fileName.toLowerCase().endsWith("txt")) {
                values = ReadTabDelimitedFile.readTabDelimitedFile(fileName,
                        delimitator);
            } else if (fileName.toLowerCase().endsWith("csv")) {
                values = ReadTabDelimitedFile.readTabDelimitedFile(fileName,
                        delimitator);
            } else {
                values = new ArrayList<String[]>();
            }

            if (values == null) {
                values = new ArrayList<String[]>();
            }

            tableDocumente.setToolTipText(fileName);

            return values;
        } catch (Exception exc) {
            closeDlg();
            values.clear();
            SWTeXtension.displayMessageE("A intervenit o eroare la deschiderea fisierului!", exc);
            return values;
        } finally {
            closeDlg();
        }
    }

    protected final void add() {
        TableView view = new TableView(
                tableDocumente.getShell(),
                tableDocumente,
                null,
                AbstractView.MODE_ADD);
        view.open();
        if (view.getUserAction() == SWT.CANCEL) {
            return;
        }
        String[] values = view.getValue();

        TableItem item = new TableItem(tableDocumente, SWT.NONE);
        item.setText(values);

        itemValidateFile.setEnabled(true);
        itemPreluare.setEnabled(false);
    }

    protected final void mod() {
        if (tableDocumente.getSelectionIndex() == -1) {
            return;
        }
        final TableItem item = tableDocumente.getSelection()[0];
        TableView view = new TableView(
                tableDocumente.getShell(),
                tableDocumente,
                item,
                AbstractView.MODE_MODIFY);
        view.open();
        if (view.getUserAction() == SWT.CANCEL) {
            return;
        }
        String[] values = view.getValue();
        if (tableDocumente.getSelectionIndex() == -1) {
            return;
        }
        tableDocumente.getItem(tableDocumente.getSelectionIndex()).setText(values);

        itemValidateFile.setEnabled(true);
        itemPreluare.setEnabled(false);
    }

    protected final void del() {
        if (tableDocumente.getSelectionIndex() == -1) {
            return;
        }
        final TableItem[] items = tableDocumente.getSelection();
        for (TableItem item : items) {
            item.dispose();
        }
        itemValidateFile.setEnabled(tableDocumente.getItemCount() > 0);
        itemPreluare.setEnabled(false);
        itemValidateFile.setEnabled(true);
        itemPreluare.setEnabled(false);
    }

    private void loadDataFile(final String filePath) {
        TableItem item;
        CWaitDlgClassic dlg = null;
        try {
            if (StringUtils.isEmpty(delimitator) && columnLabels.length > 1) {
                updateDetailMessage("Nu ati introdus delimitatorul!");
                textDelimitator.setFocus();
                return;
            }
            List<String[]> values = loadFileContentIntoMemory(filePath);
            tableDocumente.removeAll();
            dlg = new CWaitDlgClassic(values.size());
            dlg.setMessageLabel("Incarcare date in tabela...");
            dlg.open();
            for (int i = 0; i < values.size(); i++) {
                final String[] str = values.get(i);
                if (str.length != this.columnLabels.length) {
                    dlg.close();
                    SWTeXtension.displayMessageW("Pe o linie din fisierul importat exista un numar diferit de valori fata de numarul de coloane din tabela. Importul nu este posibil.");
                    clearTable(false);
                    return;
                }
                item = new TableItem(tableDocumente, SWT.NONE);
                item.setText(str);
                dlg.advance(i);
            }
            itemValidateFile.setEnabled(tableDocumente.getItemCount() > 0);
            itemPreluare.setEnabled(false);
            dlg.close();
        } catch (Exception exc) {
            if (dlg != null) {
                dlg.close();
            }
            SWTeXtension.displayMessageE("A intervenit o eroare la deschiderea fisierului.", exc);
        } finally {
            if (dlg != null) {
                dlg.close();
            }
        }
    }

    /**
     * This is where all the db action take place!
     *
     * @return
     */
    protected abstract void save2Db();

    protected void setDelimitator(String delimitator) {
        this.delimitator = delimitator;
    }

    protected String getDelimitator() {
        return delimitator;
    }

    @Override
    public final void reset() {
        clearTable(true);
    }

    @Override
    public final void showHelp() {
        new InfoView(
                getToolItemHelp().getParent().getShell(),
                AbstractPreluareDateM2View.this.columnLabels,
                AbstractPreluareDateM2View.this.columnDescriptions).open();

    }

    @Override
    public final void exportTxt() {
        Exporter.export(ExportType.TXT, tableDocumente, getShell().getText(), getClass(), "", controller);
    }

    @Override
    public final void exportPDF() {
        Exporter.export(ExportType.PDF, tableDocumente, getShell().getText(), getClass(), "", controller);
    }

    @Override
    public final void exportExcel() {
        Exporter.export(ExportType.XLS, tableDocumente, getShell().getText(), getClass(), "", controller);
    }

    @Override
    public final void exportRTF() {
        Exporter.export(ExportType.RTF, tableDocumente, getShell().getText(), getClass(), "", controller);
    }

    @Override
    public final void exportHTML() {
        Exporter.export(ExportType.HTML, tableDocumente, getShell().getText(), getClass(), "", controller);
    }

}
