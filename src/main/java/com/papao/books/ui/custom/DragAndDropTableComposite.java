package com.papao.books.ui.custom;

import com.mongodb.gridfs.GridFSDBFile;
import com.papao.books.ApplicationService;
import com.papao.books.controller.ApplicationController;
import com.papao.books.model.Carte;
import com.papao.books.model.DocumentData;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.EncodePlatform;
import com.papao.books.ui.util.BorgDateUtil;
import com.papao.books.ui.util.ColorUtil;
import com.papao.books.ui.util.FileTypeDetector;
import com.papao.books.ui.util.ObjectUtil;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class DragAndDropTableComposite extends Composite implements Observer {

    private static final Logger logger = Logger.getLogger(DragAndDropTableComposite.class);
    private static final String SWT_FULL_IMAGE = "swt_full_image";

    private Table table;
    private List<DocumentData> result = new ArrayList<>();
    private List<DocumentData> deleted = new ArrayList<>();
    private ToolItem itemDel;
    private boolean changed;
    private ToolItem itemView;
    private Carte carte = null;
    private ToolItem itemAdd;
    private boolean permanentChanges;
    private ImageViewer previewShell;
    private Composite barOpsParent;
    private ToolBar barOps;

    public DragAndDropTableComposite(Composite parent,
                                     Carte carte,
                                     boolean permanentChanges) {
        this(parent, null, carte, permanentChanges);
    }

    public DragAndDropTableComposite(Composite parent,
                                     Composite barOpsParent,
                                     Carte carte,
                                     boolean permanentChanges) {
        super(parent, SWT.NONE);
        this.carte = carte;
        this.permanentChanges = permanentChanges;
        if (barOpsParent == null) {
            this.barOpsParent = this;
        } else {
            this.barOpsParent = barOpsParent;
        }

        GridLayoutFactory.fillDefaults().numColumns(1).margins(0, 0).spacing(0, 0).extendedMargins(2, 0, 3, 0).applyTo(this);
        GridDataFactory.fillDefaults().grab(true, true).hint(300, 150).applyTo(this);

        addComponents();
        populateFields();
        enableOps();
    }

    private void addComponents() {
        barOps = new ToolBar(barOpsParent != null ? barOpsParent : this, SWT.FLAT | SWT.NO_FOCUS | SWT.RIGHT);
        itemAdd = new ToolItem(barOps, SWT.NONE);
        itemAdd.setImage(AppImages.getImage16(AppImages.IMG_PLUS));
        itemAdd.setHotImage(AppImages.getImage16Focus(AppImages.IMG_PLUS));
        itemAdd.setToolTipText("Adăugare");
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

        itemView = new ToolItem(barOps, SWT.NONE);
        itemView.setImage(AppImages.getImage16(AppImages.IMG_MOD_VIZUALIZARE));
        itemView.setHotImage(AppImages.getImage16Focus(AppImages.IMG_MOD_VIZUALIZARE));
        itemView.setToolTipText("Vizualizare");
        itemView.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                viewDocument();
            }
        });

        itemDel = new ToolItem(barOps, SWT.NONE);
        itemDel.setImage(AppImages.getImage16(AppImages.IMG_CANCEL));
        itemDel.setHotImage(AppImages.getImage16Focus(AppImages.IMG_CANCEL));
        itemDel.setToolTipText("Ștergere");
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
        table.setToolTipText("Tabela suportă drag and drop multiplu pentru orice tip de fișier, \ncu detecția tipului acestora.");
        SWTeXtension.addSelectAllListener(table);
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
        table.addListener(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (SWTeXtension.getDeleteTrigger(event)) {
                    removeSelected();
                }
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
        column.setText("Mărime (kb)");
        column.setResizable(true);
        column.setWidth(75);

        column = new TableColumn(table, SWT.CENTER);
        column.setText("Data upload");
        column.setResizable(true);
        column.setWidth(120);

        column = new TableColumn(table, SWT.LEFT);
        column.setText("Cale fișier");
        column.setResizable(true);
        column.setWidth(350);

        DropTarget dt = new DropTarget(table, DND.DROP_DEFAULT | DND.DROP_MOVE);
        dt.setTransfer(new Transfer[]{ImageTransfer.getInstance(), FileTransfer.getInstance()});
        dt.addDropListener(new DropTargetAdapter() {
            @Override
            public final void drop(final DropTargetEvent event) {
                if (event.data instanceof String[] && ((String[]) event.data).length > 0) {
                    String[] fileNames = (String[]) event.data;
                    CWaitDlgClassic waitDlgClassic = new CWaitDlgClassic("Incarcare fisiere", fileNames.length);
                    waitDlgClassic.open();
                    for (int i = 0; i < fileNames.length; i++) {
                        try {
                            loadFileIntoTable(fileNames[i]);
                        } catch (IOException | SWTException e) {
                            logger.error(e.getMessage(), e);
                            SWTeXtension.displayMessageE("Fisierul " + fileNames[i] + "nu a putut fi incarcat!", e);
                        } finally {
                            waitDlgClassic.advance();
                        }
                    }
                    waitDlgClassic.close();
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
                logger.info("Am șters un document atașat cărții " + carte.getId());
                ApplicationController.removeDocument(doc.getId());
            } else {
                deleted.add(doc);
            }
            result.remove(doc);
            item.dispose();
        }
        if (permanentChanges) {
            ApplicationService.getBookController().getRepository().save(carte);
            SWTeXtension.displayMessageI("Am șters " + selectionCount + " documente atașate cărții curente.");
        }
        enableOps();
    }

    private void loadFileIntoTable(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("Cannot find the file " + filePath);
        }
        for (DocumentData doc : result) {
            if (file.length() == doc.getLength()) {
                logger.error("File " + filePath + " already exists!");
                SWTeXtension.displayMessageW("Fisierul " + filePath + " există deja!");
                return;
            }
        }
        DocumentData dd = new DocumentData();
        dd.setFilePath(file.getPath());
        dd.setFileName(file.getName());
        dd.setContentType(new FileTypeDetector().probeContentType(Paths.get(file.getPath())));
        dd.setId(null);
        dd.setLength(file.length());
        dd.setUploadDate(new Date());
        if (permanentChanges) {
            dd = ApplicationController.saveDocument(file, null, dd.getContentType());
            this.carte.getDocuments().add(dd);
            ApplicationService.getBookController().save(this.carte);
        } else {
            changed = true;
        }
        result.add(dd);
        createTableItem(dd);
    }

    private boolean displayImage(TableItem item) {
        if (item.getData(SWT_FULL_IMAGE) instanceof Image) {
            Image image = (Image) item.getData(SWT_FULL_IMAGE);
            if (!image.isDisposed()) {
                if (previewShell != null && !previewShell.getShell().isDisposed()) {
                    previewShell.getShell().close();
                    previewShell = null;
                }
                previewShell = new ImageViewer(image);
                previewShell.setImageName(((DocumentData) item.getData()).getFileName());
                previewShell.setBucketId(((DocumentData) item.getData()).getId());
                previewShell.open();
            }
            return true;
        }
        return false;
    }

    private void viewDocument() {
        try {
            if (table.getSelectionCount() != 1) {
                return;
            }
            TableItem item = table.getSelection()[0];
            if (displayImage(item)) {
                return;
            }
            if (item.getData("file") instanceof String) {
                File file = new File(item.getData("file").toString());
                if (file.isFile() && file.exists()) {
                    final boolean isImage = new FileTypeDetector().probeContentType(Paths.get(file.getAbsolutePath())).contains("image");
                    item.setData("isImage", isImage);
                    if (isImage) {
                        Image fullImage = new Image(Display.getDefault(), file.getAbsolutePath());
                        item.setData(SWT_FULL_IMAGE, fullImage);
                        if (displayImage(item)) {
                            return;
                        }
                    }
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
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            SWTeXtension.displayMessageE("A intervenit o eroare la afisarea documentului!", exc);
        }
    }

    private boolean tryToExportAndOpenFromLocalFilesystem(TableItem item) {
        DocumentData documentData = (DocumentData) item.getData();
        String filePath = "";
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(documentData.getFilePath());
            filePath = ApplicationService.getApplicationConfig().getAppOutFolder() + "/" + documentData.getFileName();
            out = new FileOutputStream(new File(filePath));
            StreamUtils.copy(in, out);
            item.setData("file", filePath);
            final boolean isImage = new FileTypeDetector().probeContentType(Paths.get(filePath)).contains("image");
            item.setData("isImage", isImage);
            if (isImage) {
                Image fullImage = new Image(Display.getDefault(), filePath);
                item.setData(SWT_FULL_IMAGE, fullImage);
                if (displayImage(item)) {
                    return true;
                }
            }
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
            filePath = ApplicationService.getApplicationConfig().getAppOutFolder() + "/" + fsdbFile.getFilename();
            out = new FileOutputStream(new File(filePath));
            StreamUtils.copy(in, out);
            item.setData("file", filePath);
            final boolean isImage = new FileTypeDetector().probeContentType(Paths.get(filePath)).contains("image");
            item.setData("isImage", isImage);
            if (isImage) {
                Image fullImage = new Image(Display.getDefault(), filePath);
                item.setData(SWT_FULL_IMAGE, fullImage);
                displayImage(item);
            } else {
                Program.launch(filePath);
            }
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

    @Async
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
            gridFsFile = ApplicationController.getDocumentData(document.getId());
            if (gridFsFile != null) {
                document.setFilePath(gridFsFile.getMetaData().get("localFilePath") + "");
                document.setContentType(gridFsFile.getContentType());
                document.setUploadDate(gridFsFile.getUploadDate());
                document.setLength(gridFsFile.getLength());
            }
        }
        final TableItem item = new TableItem(table, SWT.NONE);
        item.setText(0, document.getFileName() != null ? document.getFileName() : "");
        item.setText(1, document.getContentType() != null ? document.getContentType() : "");
        if (document.getFilePath().lastIndexOf(".") != -1) {
            item.setImage(1, AppImages.getImageForExtension(document.getFilePath().substring(document.getFilePath().lastIndexOf("."))));
        }
        item.setText(2, document.getSizeInKb());
        item.setText(3, document.getUploadDate() != null ?
                BorgDateUtil.getFormattedDateStr(document.getUploadDate(), "yyyy-MM-dd HH:mm:ss") : "");
        item.setText(4, document.getFilePath() != null ? document.getFilePath() : "");
        item.setData(document);
        item.setData("fs", gridFsFile);
        item.addListener(SWT.Dispose, new Listener() {
            @Override
            public void handleEvent(Event event) {
                Object image = item.getData(SWT_FULL_IMAGE);
                if (image instanceof Image) {
                    Image tmp = (Image) image;
                    if (!tmp.isDisposed()) {
                        tmp.dispose();
                        ;
                    }
                }
                event.doit = true;
            }
        });
    }

    public List<DocumentData> getResult() throws IOException {
        return result;
    }

    public List<DocumentData> getDeleted() {
        return deleted;
    }

    private void enableOps() {
        itemAdd.setEnabled(carte != null && carte.getId() != null);
        itemView.setEnabled(table.getSelectionCount() == 1);
        itemDel.setEnabled(table.getSelectionCount() > 0);
    }

    public boolean isChanged() {
        return this.changed;
    }

    private void setCarte(Carte carte) {
        this.carte = carte;
        table.removeAll();
        populateFields();
        enableOps();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof EncodePlatform) {
            setCarte((Carte) ((EncodePlatform) o).getObservableObject());
        }
    }
}
