package com.papao.books.view.preluari;

import com.papao.books.controller.ApplicationReportController;
import com.papao.books.export.ExportType;
import com.papao.books.export.Exporter;
import com.papao.books.view.AppImages;
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
    private ToolItem itemValidateFile;
    private ToolItem itemPreluare;
    private ToolBar barOps;
    private ProgressBarComposite cpBar;
    protected Composite compSuport;
    public boolean ready4Import = false;
    public String[] columnLabels;
    public String[] columnDescriptions;
    private Table tableDocumente;
    private ApplicationReportController controller;

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
        GridLayoutFactory.fillDefaults().numColumns(4).equalWidth(false).applyTo(this.compSuport);

        setBarOps(new ToolBar(this.compSuport, SWT.RIGHT | SWT.FLAT));
        GridDataFactory.fillDefaults().grab(false, false).align(SWT.FILL, SWT.CENTER).applyTo(this.barOps);

        setItemOpenFile(new ToolItem(getBarOps(), SWT.PUSH));
        getItemOpenFile().setImage(AppImages.getImage16(AppImages.IMG_IMPORT));
        getItemOpenFile().setHotImage(AppImages.getImage16Focus(AppImages.IMG_IMPORT));
        getItemOpenFile().setToolTipText("Import fisier (prima linie este rezervata pentru denumirea coloanelor)");
        getItemOpenFile().setText("&Import");
        getItemOpenFile().addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                loadDataFile(null);
                if (!getTableDocumente().isDisposed()) {
                    updateDetailMessage("Aveti " + getTableDocumente().getItemCount()
                            + " inregistrari in tabel.");
                }
            }
        });

        setItemValidateFile(new ToolItem(getBarOps(), SWT.PUSH));
        getItemValidateFile().setImage(AppImages.getImage16(AppImages.IMG_OK));
        getItemValidateFile().setHotImage(AppImages.getImage16Focus(AppImages.IMG_OK));
        getItemValidateFile().setToolTipText("Validare fisier");
        getItemValidateFile().setText("&Validare");
        getItemValidateFile().setEnabled(false);
        getItemValidateFile().addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                try {
                    getTableDocumente().setFocus();
                    AbstractPreluareDateM2View.this.ready4Import = validate();
                    getItemPreluare().setEnabled(AbstractPreluareDateM2View.this.ready4Import);
                    if (AbstractPreluareDateM2View.this.ready4Import) {
                        if (!getTableDocumente().isDisposed()) {
                            updateDetailMessage("Aveti " + getTableDocumente().getItemCount()
                                    + " inregistrari valide in tabel.");
                        }
                    } else {
                        if (!getTableDocumente().isDisposed()) {
                            updateDetailMessage("Aveti " + getTableDocumente().getItemCount()
                                    + " inregistrari in tabel.");
                        }
                    }
                } catch (Exception exc) {
                    logger.error(exc.getMessage(), exc);
                    SWTeXtension.displayMessageEGeneric(exc);
                }
            }
        });

        setItemPreluare(new ToolItem(getBarOps(), SWT.PUSH));
        getItemPreluare().setImage(AppImages.getImage16(AppImages.IMG_EXPORT));
        getItemPreluare().setHotImage(AppImages.getImage16Focus(AppImages.IMG_EXPORT));
        getItemPreluare().setToolTipText("Start preluare");
        getItemPreluare().setText("&Preluare");
        getItemPreluare().setEnabled(false);
        getItemPreluare().addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                preluareDate();
            }
        });

        setCpBar(new ProgressBarComposite(
                this.compSuport,
                Integer.MAX_VALUE,
                ColorUtil.COLOR_ROSU_SEMI_ROSU,
                SWT.SMOOTH));
        getCpBar().setVisible(false);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(getCpBar());

        setTableDocumente(new Table(getContainer(), SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION
                | SWT.H_SCROLL | SWT.V_SCROLL));
        getTableDocumente().setLinesVisible(true);
        getTableDocumente().setHeaderVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).hint(640, 480).applyTo(getTableDocumente());
        SWTeXtension.addColoredFocusListener(getTableDocumente(), null);
        WidgetTableUtil.addCustomGradientSelectionListenerToTable(getTableDocumente(), null, null);
        getTableDocumente().setMenu(createTableMenu());
        getTableDocumente().addListener(SWT.KeyDown, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                if (e.widget == getTableDocumente()) {
                    if ((e.stateMask & SWT.CTRL) != 0) {
                        if ((e.keyCode == 'A') || (e.keyCode == 'a')) {
                            getTableDocumente().selectAll();
                        }
                    }
                    if (e.character == SWT.DEL) {
                        del();
                        if (!getTableDocumente().isDisposed()) {
                            updateDetailMessage("Aveti " + getTableDocumente().getItemCount()
                                    + " inregistrari in tabel.");
                        }
                    }
                }
            }
        });
        getTableDocumente().addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                mod();
            }
        });

        TableColumn col;
        for (String str : this.columnLabels) {
            col = new TableColumn(getTableDocumente(), SWT.NONE);
            col.setText(str);
            col.setWidth(100);
        }

		/*
         * definim acum capabilitatile de Drag and Drop ale tabelei
		 */

        DropTarget dt = new DropTarget(getTableDocumente(), DND.DROP_DEFAULT | DND.DROP_MOVE);
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
                        if ((getTableDocumente().getItemCount() > 0)
                                && (SWTeXtension.displayMessageQ("Atentie. Prin importare datele din tabela se pierd. Continuati?",
                                "Confirmare import fisier") == SWT.NO)) {
                            return;
                        }
                        loadDataFile(fileList[0]);
                        if (!getTableDocumente().isDisposed()) {
                            updateDetailMessage("Aveti " + getTableDocumente().getItemCount()
                                    + " inregistrari in tabel.");
                        }
                    }
                }
            }
        });
    }

    private Menu createTableMenu() {
        if ((getTableDocumente() == null) || getTableDocumente().isDisposed()) {
            return null;
        }
        final Menu menu = new Menu(getTableDocumente());
        MenuItem menuItem;
        try {
            menu.addListener(SWT.Show, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    int idx = 0;
                    final int selIdx = getTableDocumente().getSelectionIndex();
                    try {
                        menu.getItem(idx++).setEnabled(getTableDocumente().getColumnCount() > 0); // add
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
                    if (!getTableDocumente().isDisposed()) {
                        updateDetailMessage("Aveti " + getTableDocumente().getItemCount()
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
                    if (!getTableDocumente().isDisposed()) {
                        updateDetailMessage("Aveti " + getTableDocumente().getItemCount()
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
            if ((getTableDocumente() == null) || getTableDocumente().isDisposed()) {
                return;
            }
            if (getTableDocumente().getItemCount() == 0) {
                SWTeXtension.displayMessageW("Nu avem ce prelua...");
                return;
            }
            long time = System.currentTimeMillis();
            save2Db();
            logger.info("Preluare executata OK in "
                    + ((System.currentTimeMillis() - time) / 1000D) + " sec");
            SWTeXtension.displayMessageI("Preluarea s-a efectuat cu succes!");
            getShell().close();
        } catch (Exception exc) {
            logger.error(exc, exc);
            SWTeXtension.displayMessageEGeneric(exc);
        }
    }

    protected final void clearTable(final boolean ask) {
        try {
            if ((getTableDocumente() == null) || getTableDocumente().isDisposed()) {
                getItemPreluare().setEnabled(false);
                return;
            }
            if ((getTableDocumente().getItemCount() > 0) && ask) {
                if (SWTeXtension.displayMessageQ("Sunteti sigur ca doriti sa stergeti liniile importate?",
                        "Stergere linii importate") == SWT.NO) {
                    return;
                }
            }
            getItemPreluare().setEnabled(false);
            getItemValidateFile().setEnabled(false);
            getTableDocumente().removeAll();
            if (getCpBar().isVisible()) {
                getCpBar().setVisible(false);
            }
            this.ready4Import = false;
            if (!getTableDocumente().isDisposed()) {
                updateDetailMessage("Aveti " + getTableDocumente().getItemCount()
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
                dlg = new FileDialog(getTableDocumente().getShell(), SWT.OPEN);
                dlg.setFilterNames(new String[]{
                        "XLS (Registru de lucru Excel)(*.xls)",
                        "Text (Delimitat de tabulatori)(*.txt)",
                        "CSV (Delimitat de virgule) (*.csv)"});
                dlg.setFilterExtensions(new String[]{
                        "*.xls", "*.txt", "*.csv"});
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
                        ReadTabDelimitedFile.DELIMITER_TAB);
            } else if (fileName.toLowerCase().endsWith("csv")) {
                values = ReadTabDelimitedFile.readTabDelimitedFile(fileName,
                        ReadTabDelimitedFile.DELIMITER_COMMA);
            } else {
                values = new ArrayList<String[]>();
            }

            if (values == null) {
                values = new ArrayList<String[]>();
            }

            getTableDocumente().setToolTipText(fileName);

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
                getTableDocumente().getShell(),
                getTableDocumente(),
                null,
                AbstractView.MODE_ADD);
        view.open();
        if (view.getUserAction() == SWT.CANCEL) {
            return;
        }
        String[] values = view.getValue();

        TableItem item = new TableItem(getTableDocumente(), SWT.NONE);
        item.setText(values);

        getItemValidateFile().setEnabled(true);
        getItemPreluare().setEnabled(false);
    }

    protected final void mod() {
        if (getTableDocumente().getSelectionIndex() == -1) {
            return;
        }
        final TableItem item = getTableDocumente().getSelection()[0];
        TableView view = new TableView(
                getTableDocumente().getShell(),
                getTableDocumente(),
                item,
                AbstractView.MODE_MODIFY);
        view.open();
        if (view.getUserAction() == SWT.CANCEL) {
            return;
        }
        String[] values = view.getValue();
        if (getTableDocumente().getSelectionIndex() == -1) {
            return;
        }
        getTableDocumente().getItem(getTableDocumente().getSelectionIndex()).setText(values);

        getItemValidateFile().setEnabled(true);
        getItemPreluare().setEnabled(false);
    }

    protected final void del() {
        if (getTableDocumente().getSelectionIndex() == -1) {
            return;
        }
        final TableItem[] items = getTableDocumente().getSelection();
        for (TableItem item : items) {
            item.dispose();
        }
        getItemValidateFile().setEnabled(getTableDocumente().getItemCount() > 0);
        getItemPreluare().setEnabled(false);
        getItemValidateFile().setEnabled(true);
        getItemPreluare().setEnabled(false);
    }

    private void loadDataFile(final String filePath) {
        TableItem item;
        CWaitDlgClassic dlg = null;
        try {
            List<String[]> values = loadFileContentIntoMemory(filePath);
            getTableDocumente().removeAll();
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
                item = new TableItem(getTableDocumente(), SWT.NONE);
                item.setText(str);
                dlg.advance(i);
            }
            getItemValidateFile().setEnabled(getTableDocumente().getItemCount() > 0);
            getItemPreluare().setEnabled(false);
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
    public abstract void save2Db();

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

    public final ToolItem getItemOpenFile() {
        return this.itemOpenFile;
    }

    public final void setItemOpenFile(final ToolItem itemOpenFile) {
        this.itemOpenFile = itemOpenFile;
    }

    public final ToolItem getItemValidateFile() {
        return this.itemValidateFile;
    }

    public final void setItemValidateFile(final ToolItem itemValidateFile) {
        this.itemValidateFile = itemValidateFile;
    }

    public final ToolItem getItemPreluare() {
        return this.itemPreluare;
    }

    public final void setItemPreluare(final ToolItem itemPreluare) {
        this.itemPreluare = itemPreluare;
    }

    public final ToolBar getBarOps() {
        return this.barOps;
    }

    public final void setBarOps(final ToolBar barOps) {
        this.barOps = barOps;
    }

    public final ProgressBarComposite getCpBar() {
        return this.cpBar;
    }

    public final void setCpBar(final ProgressBarComposite cpBar) {
        this.cpBar = cpBar;
    }

    public final Table getTableDocumente() {
        return this.tableDocumente;
    }

    public final void setTableDocumente(final Table tableDocumente) {
        this.tableDocumente = tableDocumente;
    }

    @Override
    public final void exportTxt() {
        Exporter.export(ExportType.TXT, getTableDocumente(), getShell().getText(), getClass(), controller);
    }

    @Override
    public final void exportPDF() {
        Exporter.export(ExportType.PDF, getTableDocumente(), getShell().getText(), getClass(), controller);
    }

    @Override
    public final void exportExcel() {
        Exporter.export(ExportType.XLS, getTableDocumente(), getShell().getText(), getClass(), controller);
    }

    @Override
    public final void exportRTF() {
        Exporter.export(ExportType.RTF, getTableDocumente(), getShell().getText(), getClass(), controller);
    }

    @Override
    public final void exportHTML() {
        Exporter.export(ExportType.HTML, getTableDocumente(), getShell().getText(), getClass(), controller);
    }

}
