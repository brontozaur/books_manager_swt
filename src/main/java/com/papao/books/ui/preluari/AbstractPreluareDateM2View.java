package com.papao.books.ui.preluari;

import com.papao.books.controller.SettingsController;
import com.papao.books.export.ExportType;
import com.papao.books.export.Exporter;
import com.papao.books.model.config.GeneralSetting;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.auth.EncodeLive;
import com.papao.books.ui.custom.CWaitDlgClassic;
import com.papao.books.ui.custom.ProgressBarComposite;
import com.papao.books.ui.interfaces.IExport;
import com.papao.books.ui.interfaces.IHelp;
import com.papao.books.ui.interfaces.IReset;
import com.papao.books.ui.util.ColorUtil;
import com.papao.books.ui.util.WidgetTableUtil;
import com.papao.books.ui.util.importers.ReadExcelFileWithJXL;
import com.papao.books.ui.util.importers.ReadExcelFileWithPOI;
import com.papao.books.ui.util.importers.ReadTabDelimitedFile;
import com.papao.books.ui.view.AbstractCViewAdapter;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.InfoView;
import com.papao.books.ui.view.SWTeXtension;
import com.papao.books.ui.view.TableView;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Descriere : Orice implementare a acestei clase va trebui sa specifice cod doar pentru metodele
 * {@link AbstractPreluareDateM2View#save2Db()} si {@link com.papao.books.ui.view.AbstractView#validate()} pentru a avea
 * implementata o noua metoda de preluare. </p>
 */
public abstract class AbstractPreluareDateM2View extends AbstractCViewAdapter implements IReset,
        IHelp, IExport {

    private static final Logger logger = Logger.getLogger(AbstractPreluareDateM2View.class);

    private ToolItem itemOpenFile;
    protected ToolItem itemValidateFile;
    protected ToolItem itemPreluare;
    private ProgressBarComposite cpBar;
    public boolean ready4Import = false;
    public String[] columnLabels;
    public String[] columnDescriptions;
    protected Table tableDocumente;
    private Text textDelimitator;
    private String delimitator = " - ";

    public AbstractPreluareDateM2View(final Shell parent,
                                      final String[] columnLabels,
                                      final String[] columnDescriptions) {
        super(parent, AbstractView.MODE_NONE);

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

    @Override
    protected void customizeView() {
        setViewOptions(AbstractView.ADD_CANCEL);
        setBigViewImage(AppImages.getImage24(AppImages.IMG_IMPORT));
        setCreateUpperCompLeftArea(true);
        setCreateUpperCompRightArea(true);
    }

    private void createToolBarItems() {
        this.itemOpenFile = new ToolItem(getMainToolBar(), SWT.PUSH);
        itemOpenFile.setImage(AppImages.getImage24(AppImages.IMG_IMPORT));
        itemOpenFile.setHotImage(AppImages.getImage24Focus(AppImages.IMG_IMPORT));
        itemOpenFile.setToolTipText("Import fișier (prima linie este rezervată pentru denumirea coloanelor)");
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

        this.itemValidateFile = new ToolItem(getMainToolBar(), SWT.PUSH);
        itemValidateFile.setImage(AppImages.getImage24(AppImages.IMG_OK));
        itemValidateFile.setHotImage(AppImages.getImage24Focus(AppImages.IMG_OK));
        itemValidateFile.setToolTipText("Validare fișier");
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

        this.itemPreluare = new ToolItem(getMainToolBar(), SWT.PUSH);
        itemPreluare.setImage(AppImages.getImage24(AppImages.IMG_EXPORT));
        itemPreluare.setHotImage(AppImages.getImage24Focus(AppImages.IMG_EXPORT));
        itemPreluare.setToolTipText("Start preluare");
        itemPreluare.setText("&Preluare");
        itemPreluare.setEnabled(false);
        itemPreluare.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                preluareDate();
            }
        });
    }

    private void createRightArea() {
        Composite rightArea = new Composite(getUpperCompRightArea(), SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(3).applyTo(rightArea);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.CENTER).applyTo(rightArea);

        new Label(rightArea, SWT.NONE).setText("Delimitator:");
        textDelimitator = new Text(rightArea, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(false, false).hint(30, SWT.DEFAULT).applyTo(textDelimitator);
        textDelimitator.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                delimitator = textDelimitator.getText();
            }
        });

        GeneralSetting importDelimiterSetting = SettingsController.getGeneralSetting("importDelimiter");
        if (importDelimiterSetting != null) {
            textDelimitator.setText((String) importDelimiterSetting.getValue());
        } else {
            textDelimitator.setText(delimitator);
        }

        this.cpBar = new ProgressBarComposite(
                rightArea,
                Integer.MAX_VALUE,
                ColorUtil.COLOR_ROSU_SEMI_ROSU,
                SWT.SMOOTH);
        cpBar.setVisible(false);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(cpBar);

        ((CBanner) getUpperComp()).setRightWidth(200);
    }

    private void addComponents() {

        createToolBarItems();
        createRightArea();

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
                    if (SWTeXtension.getDeleteTrigger(e)) {
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
                                && (SWTeXtension.displayMessageQ("Atenție. Prin importare datele din tabelă se pierd. Continuați?",
                                "Confirmare import fișier") == SWT.NO)) {
                            return;
                        }
                        loadDataFile(fileList[0]);
                        if (!tableDocumente.isDisposed()) {
                            updateDetailMessage("Aveți " + tableDocumente.getItemCount()
                                    + " înregistrări în tabel.");
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
                SWTeXtension.displayMessageW("Conținut invalid. Verificați structura textului pe care \ndoriți să-l importați și delimitatorul ales.");
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
                        idx++;
                        menu.getItem(idx++).setEnabled(true); // paste from clipboard
                    } catch (Exception exc) {
                        logger.error(exc, exc);
                        SWTeXtension.displayMessageEGeneric(exc);
                    }
                }
            });

            menuItem = new MenuItem(menu, SWT.NONE);
            menuItem.setText("Adăugare");
            menuItem.setImage(AppImages.getImage16(AppImages.IMG_PLUS));
            menuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    add();
                    if (!tableDocumente.isDisposed()) {
                        updateDetailMessage("Aveți " + tableDocumente.getItemCount()
                                + " înregistrări în tabel.");
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
            menuItem.setText("Ștergere	Del");
            menuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    del();
                    if (!tableDocumente.isDisposed()) {
                        updateDetailMessage("Aveți " + tableDocumente.getItemCount()
                                + " înregistrări în tabel.");
                    }
                }
            });

            new MenuItem(menu, SWT.SEPARATOR);
            menuItem = new MenuItem(menu, SWT.NONE);
            menuItem.setText("Lipire	Ctrl+V");
            menuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    if (StringUtils.isEmpty(delimitator) && columnLabels.length > 1) {
                        updateDetailMessage("Nu ati introdus delimitatorul!");
                        textDelimitator.setFocus();
                        return;
                    }
                    pasteFromClipboard();
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
                if (SWTeXtension.displayMessageQ("Sunteți sigur că doriți să ștergeți liniile importate?",
                        "Ștergere linii importate") == SWT.NO) {
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
            dlg.setMessageLabel("Încarcare date în tabelă...");
            dlg.open();
            for (int i = 0; i < values.size(); i++) {
                final String[] str = values.get(i);
                if (str.length != this.columnLabels.length) {
                    dlg.close();
                    SWTeXtension.displayMessageW("Pe o linie din fișierul importat există un număr diferit de valori față de numărul de coloane din tabelă. Importul nu este posibil.");
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
        Exporter.export(ExportType.TXT, tableDocumente, getShell().getText(), getClass(), "");
    }

    @Override
    public final void exportPDF() {
        Exporter.export(ExportType.PDF, tableDocumente, getShell().getText(), getClass(), "");
    }

    @Override
    public final void exportExcel() {
        Exporter.export(ExportType.XLS, tableDocumente, getShell().getText(), getClass(), "");
    }

    @Override
    public final void exportRTF() {
        Exporter.export(ExportType.RTF, tableDocumente, getShell().getText(), getClass(), "");
    }

    @Override
    public final void exportHTML() {
        Exporter.export(ExportType.HTML, tableDocumente, getShell().getText(), getClass(), "");
    }

}
