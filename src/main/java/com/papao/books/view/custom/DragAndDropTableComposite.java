package com.papao.books.view.custom;

import com.mongodb.gridfs.GridFSDBFile;
import com.papao.books.controller.BookController;
import com.papao.books.model.Carte;
import com.papao.books.model.DocumentData;
import com.papao.books.view.AppImages;
import com.papao.books.view.util.BorgDateUtil;
import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.util.ObjectUtil;
import com.papao.books.view.view.SWTeXtension;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DragAndDropTableComposite extends Composite {

    private static final Logger logger = LoggerFactory.getLogger(DragAndDropTableComposite.class);

    private Table table;
    private List<DocumentData> result = new ArrayList<>();
    private ToolItem itemDel;
    private boolean changed;
    private BookController controller;
    private ToolItem itemView;
    private Carte carte = null;
    private ToolItem itemAdd;
    private boolean permanentChanges;

    public DragAndDropTableComposite(Composite parent, BookController controller, Carte carte, boolean permanentChanges) {
        super(parent, SWT.NONE);
        this.controller = controller;
        this.carte = carte;
        this.permanentChanges = permanentChanges;

        GridLayoutFactory.fillDefaults().numColumns(1).margins(0, 0).applyTo(this);
        GridDataFactory.fillDefaults().grab(false, false).hint(300, 150).applyTo(this);

        addComponents();
        populateFields();
        enableOps();
    }

    private void addComponents() {
        ToolBar bar = new ToolBar(this, SWT.FLAT | SWT.NO_FOCUS | SWT.RIGHT);
        itemAdd = new ToolItem(bar, SWT.NONE);
        itemAdd.setImage(AppImages.getImage16(AppImages.IMG_PLUS));
        itemAdd.setHotImage(AppImages.getImage16Focus(AppImages.IMG_PLUS));
        itemAdd.setToolTipText("Adaugare");
        itemAdd.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                FileDialog dlg;
                try {
                    dlg = new FileDialog(getShell(), SWT.OPEN);
                    dlg.setFilterNames(new String[]{"Toate fisierele (*.*)"});
                    String filePath = dlg.open();
                    if (filePath == null) {
                        return;
                    }
                    loadFileIntoTable(filePath);
                } catch (Exception exc) {
                    logger.error(exc.getMessage(), exc);
                    SWTeXtension.displayMessageE("A intervenit o eroare la incarcarea fisierului!", exc);
                }
            }
        });

        itemView = new ToolItem(bar, SWT.NONE);
        itemView.setImage(AppImages.getImage16(AppImages.IMG_MOD_VIZUALIZARE));
        itemView.setHotImage(AppImages.getImage16Focus(AppImages.IMG_MOD_VIZUALIZARE));
        itemView.setToolTipText("Vizualizare");
        itemView.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                viewDocument();
            }
        });

        itemDel = new ToolItem(bar, SWT.NONE);
        itemDel.setImage(AppImages.getImage16(AppImages.IMG_CANCEL));
        itemDel.setHotImage(AppImages.getImage16Focus(AppImages.IMG_CANCEL));
        itemDel.setToolTipText("Stergere");
        itemDel.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                removeSelected();
            }
        });

        table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(table);
        table.setToolTipText("Tabela suporta drag and drop multiplu pentru orice tip de fisier, \ncu detectia tipului acestora.");
        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                enableOps();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                super.widgetDefaultSelected(e);
                viewDocument();
            }
        });

        TableColumn column = new TableColumn(table, SWT.LEFT);
        column.setText("Nume");
        column.setResizable(true);
        column.setWidth(170);

        column = new TableColumn(table, SWT.LEFT);
        column.setText("Tip");
        column.setResizable(true);
        column.setWidth(100);

        column = new TableColumn(table, SWT.RIGHT);
        column.setText("Marime (kb)");
        column.setResizable(true);
        column.setWidth(75);

        column = new TableColumn(table, SWT.CENTER);
        column.setText("Data upload");
        column.setResizable(true);
        column.setWidth(120);

        column = new TableColumn(table, SWT.LEFT);
        column.setText("Cale fisier");
        column.setResizable(true);
        column.setWidth(350);

        DropTarget dt = new DropTarget(table, DND.DROP_DEFAULT | DND.DROP_MOVE);
        dt.setTransfer(new Transfer[]{ImageTransfer.getInstance(), FileTransfer.getInstance()});
        dt.addDropListener(new DropTargetAdapter() {
            @Override
            public final void drop(final DropTargetEvent event) {
                if (event.data instanceof String[] && ((String[]) event.data).length > 0) {
                    String[] fileNames = (String[]) event.data;
                    for (int i = 0; i < fileNames.length; i++) {
                        try {
                            loadFileIntoTable(fileNames[i]);
                        } catch (IOException | SWTException e) {
                            logger.error(e.getMessage(), e);
                            SWTeXtension.displayMessageE("Fisierul " + fileNames[i] + "nu a putut fi incarcat!", e);
                        }
                    }
                }
            }
        });

        SWTeXtension.addColoredFocusListener(table, ColorUtil.COLOR_FOCUS_YELLOW);
    }

    private void removeSelected() {
        final int selectionCount = table.getSelectionCount();
        if (selectionCount == 0) {
            return;
        }
        changed = true;
        for (TableItem item : table.getSelection()) {
            DocumentData doc = (DocumentData) item.getData();
            if (permanentChanges) {
                carte.getDocuments().remove(doc);
                logger.info("Am sters un document atasat cartii " + carte.getTitlu());
            }
            result.remove(doc);
            item.dispose();
        }
        if (permanentChanges) {
            controller.getRepository().save(carte);
            SWTeXtension.displayMessageI("Am sters " + selectionCount + " documente atasate cartii curente");
        }
    }

    private void loadFileIntoTable(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("Cannot find the file " + filePath);
        }
        for (DocumentData doc : result) {
            if (file.length() == doc.getLength()) {
                logger.error("File " + filePath + " already exists!");
                SWTeXtension.displayMessageW("File " + filePath + " already exists!");
                return;
            }
        }
        DocumentData dd = new DocumentData();
        dd.setFilePath(file.getPath());
        dd.setFileName(file.getName());
        dd.setContentType(Files.probeContentType(Paths.get(file.getPath())));
        dd.setId(null);
        dd.setLength(file.length());
        dd.setUploadDate(new Date());
        result.add(dd);
        createTableItem(dd);
        if (permanentChanges) {
            this.carte.getDocuments().add(controller.saveDocument(file, null, null, dd.getContentType()));
            controller.save(this.carte);
        } else {
            changed = true;
        }
    }

    private void viewDocument() {
        if (table.getSelectionCount() != 1) {
            return;
        }
        TableItem item = table.getSelection()[0];
        if (item.getData("file") instanceof String) {
            File file = new File(item.getData("file").toString());
            if (file.isFile() && file.exists()) {
                Program.launch(file.getAbsolutePath());
                return;
            }
        }
        GridFSDBFile fsdbFile = (GridFSDBFile) item.getData("fs");
        if (fsdbFile != null) {
            if (tryToExportAndOpenFromGridFs(fsdbFile, item)) {
                return;
            }
        }
        tryToExportAndOpenFromLocalFilesystem(item);
    }

    private boolean tryToExportAndOpenFromLocalFilesystem(TableItem item) {
        DocumentData documentData = (DocumentData) item.getData();
        String filePath = "";
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(documentData.getFilePath());
            filePath = controller.getAppOutFolder() + "/" + documentData.getFileName();
            out = new FileOutputStream(new File(filePath));
            StreamUtils.copy(in, out);
            item.setData("file", filePath);
            Program.launch(filePath);
            return true;
        } catch (IOException ioex) {
            logger.error(ioex.getMessage(), ioex);
            SWTeXtension.displayMessageE(ioex.getMessage(), ioex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return false;
    }

    private boolean tryToExportAndOpenFromGridFs(GridFSDBFile fsdbFile, TableItem item) {
        String filePath = "";
        InputStream in = null;
        OutputStream out = null;
        try {
            in = fsdbFile.getInputStream();
            filePath = controller.getAppOutFolder() + "/" + fsdbFile.getFilename();
            out = new FileOutputStream(new File(filePath));
            StreamUtils.copy(in, out);
            item.setData("file", filePath);
            Program.launch(filePath);
            return true;
        } catch (IOException ioex) {
            logger.error(ioex.getMessage(), ioex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return false;
    }

    private void populateFields() {
        this.result = (List<DocumentData>) ObjectUtil.copy(carte.getDocuments());
        for (DocumentData document : result) {
            createTableItem(document);
        }
    }

    private void createTableItem(DocumentData document) {
        if (table.isDisposed()) {
            return;
        }
        GridFSDBFile gridFsFile = null;
        if (document.getId() != null) {
            gridFsFile = controller.getDocumentData(document.getId());
            if (gridFsFile != null) {
                document.setFilePath(gridFsFile.getMetaData().get("localFilePath") + "");
                document.setContentType(gridFsFile.getContentType());
                document.setUploadDate(gridFsFile.getUploadDate());
                document.setLength(gridFsFile.getLength());
            }
        }
        TableItem item = new TableItem(table, SWT.NONE);
        item.setText(0, document.getFileName() != null ? document.getFileName() : "");
        item.setText(1, document.getContentType() != null ? document.getContentType() : "");
        item.setText(2, document.getSizeInKb());
        item.setText(3, document.getUploadDate() != null ?
                BorgDateUtil.getFormattedDateStr(document.getUploadDate(), "yyyy-MM-dd HH:mm:ss") : "");
        item.setText(4, document.getFilePath() != null ? document.getFilePath() : "");
        item.setData(document);
        item.setData("fs", gridFsFile);
    }

    public List<DocumentData> getResult() throws IOException {
        return result;
    }

    private void enableOps() {
        itemAdd.setEnabled(carte != null && carte.getId() != null);
        itemView.setEnabled(table.getSelectionCount() == 1);
        itemDel.setEnabled(table.getSelectionCount() > 0);
    }

    public Table getTable() {
        return this.table;
    }

    public boolean isChanged() {
        return this.changed;
    }

    public void setCarte(Carte carte) {
        this.carte = carte;
        table.removeAll();
        populateFields();
        enableOps();
    }
}
