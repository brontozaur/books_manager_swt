package com.papao.books.view.custom;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.papao.books.model.GridFsImageData;
import com.papao.books.view.AppImages;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.bson.BSON;
import org.bson.types.ObjectId;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageViewerComposite extends Composite {

    private final static Logger logger = LoggerFactory.getLogger(ImageViewerComposite.class);

    private final static String[] imageExtensions = new FileNameExtensionFilter(
            "Image files", ImageIO.getReaderFileSuffixes()).getExtensions();

    private Composite stackComposite;
    private GridFS gridFS;
    private ToolItem frontCoverDisplayToolItem;
    private ToolItem backCoverDisplayToolItem;
    private GridFsImageData frontCoverData;
    private GridFsImageData backCoverData;
    private ToolBar frontCoverDisplayToolBar;
    private ToolBar backCoverDisplayToolBar;

    public ImageViewerComposite(Composite parent, GridFS gridFS, final GridFsImageData frontCoverData, final GridFsImageData backCoverData) {
        super(parent, SWT.NONE);

        this.gridFS = gridFS;
        this.frontCoverData = frontCoverData;
        this.backCoverData = backCoverData;

        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(0, 0).applyTo(this);
        GridDataFactory.fillDefaults().hint(164, 164).grab(false, false).applyTo(this);

        ToolBar bar = new ToolBar(this, SWT.RIGHT);
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(bar);
        ToolItem toolItemFrontCover = new ToolItem(bar, SWT.FLAT);
        toolItemFrontCover.setImage(AppImages.getImage16(AppImages.IMG_ARROW_LEFT));
        toolItemFrontCover.setHotImage(AppImages.getImage16Focus(AppImages.IMG_ARROW_LEFT));
        toolItemFrontCover.setText("Fata");
        toolItemFrontCover.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                ((StackLayout) stackComposite.getLayout()).topControl = stackComposite.getChildren()[0];
                stackComposite.layout();
            }
        });

        ToolItem toolItemBackCover = new ToolItem(bar, SWT.RIGHT);
        toolItemBackCover.setImage(AppImages.getImage16(AppImages.IMG_ARROW_RIGHT));
        toolItemBackCover.setHotImage(AppImages.getImage16Focus(AppImages.IMG_ARROW_RIGHT));
        toolItemBackCover.setText("Spate");
        toolItemBackCover.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                ((StackLayout) stackComposite.getLayout()).topControl = stackComposite.getChildren()[1];
                stackComposite.layout();
            }
        });

        stackComposite = new Composite(this, SWT.NONE);
        stackComposite.setLayout(new StackLayout());

        frontCoverDisplayToolBar = new ToolBar(stackComposite, SWT.PUSH | SWT.VERTICAL | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(false, false).minSize(164, SWT.DEFAULT).align(SWT.FILL, SWT.FILL).span(1, 6).applyTo(frontCoverDisplayToolBar);

        frontCoverDisplayToolItem = new ToolItem(frontCoverDisplayToolBar, SWT.NONE);
        frontCoverDisplayToolItem.setImage(AppImages.IMAGE_BLANK_128x128);
        frontCoverDisplayToolItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                selectImage((ToolItem) event.widget);
            }
        });

        backCoverDisplayToolBar = new ToolBar(stackComposite, SWT.PUSH | SWT.VERTICAL | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(false, false).minSize(164, SWT.DEFAULT).align(SWT.FILL, SWT.FILL).span(1, 6).applyTo(backCoverDisplayToolBar);

        backCoverDisplayToolItem = new ToolItem(backCoverDisplayToolBar, SWT.NONE);
        backCoverDisplayToolItem.setImage(AppImages.IMAGE_NOT_FOUND_32X32);
        backCoverDisplayToolItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                selectImage((ToolItem) event.widget);
            }
        });

        ((StackLayout) stackComposite.getLayout()).topControl = frontCoverDisplayToolBar;

        populateFields();
    }

    private void populateFields() {
        if (frontCoverData != null) {
            GridFSDBFile imageForOutput = gridFS.findOne(frontCoverData.getId());
            if (imageForOutput != null) {
                Image fullImage = new Image(Display.getDefault(), imageForOutput.getInputStream());
                Image resizedImage = AppImages.getImage(fullImage, 164, 164);
                fullImage.dispose();
                frontCoverDisplayToolItem.setImage(resizedImage);
            }
        }

        if (backCoverData != null) {
            GridFSDBFile imageForOutput = gridFS.findOne(backCoverData.getId());
            if (imageForOutput != null) {
                Image fullImage = new Image(Display.getDefault(), imageForOutput.getInputStream());
                Image resizedImage = AppImages.getImage(fullImage, 164, 164);
                fullImage.dispose();
                backCoverDisplayToolItem.setImage(resizedImage);
            }
        }
    }

    private void selectImage(ToolItem uiControlForImage) {
        FileDialog dlg;
        try {
            dlg = new FileDialog(getShell(), SWT.OPEN);
            dlg.setFilterExtensions(new String[]{"*.png", "*.jpg"});
            dlg.setFilterNames(new String[]{"Imagini *.png", "Imagini *.jpg"});
            String selectedFile = dlg.open();
            if (StringUtils.isEmpty(selectedFile)) {
                return;
            }
            Image fullImage = new Image(Display.getDefault(), selectedFile);
            Image resizedImage = AppImages.getImage(fullImage, 164, 164);

            uiControlForImage.setImage(resizedImage);
            fullImage.dispose();
            GridFSDBFile savedFile = saveImage(selectedFile);
            if (uiControlForImage == frontCoverDisplayToolItem) {
                if (savedFile == null) {
                    frontCoverData = null;
                } else {
                    frontCoverData = new GridFsImageData();
                    frontCoverData.setFileName(savedFile.getFilename());
                    frontCoverData.setId((ObjectId) savedFile.getId());
                }
            } else {
                if (savedFile == null) {
                    backCoverData = null;
                } else {
                    backCoverData = new GridFsImageData();
                    backCoverData.setFileName(savedFile.getFilename());
                    backCoverData.setId((ObjectId) savedFile.getId());
                }
            }
            uiControlForImage.setData(selectedFile);
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            SWTeXtension.displayMessageW("Imaginea selectata este invalida sau nu a putut fi incarcata!");
        }
    }

    private GridFSDBFile saveImage(String imagePath) throws IOException {
        File file = new File(imagePath);
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        GridFSInputFile gfsFile = gridFS.createFile(file);
        gfsFile.setFilename(file.getName());
        gfsFile.setContentType(Files.probeContentType(Paths.get(imagePath)));
        DBObject meta = new BasicDBObject();
        meta.put("fileName", file.getName());
        meta.put("fileOriginalFilePath", imagePath);
        meta.put("fileSize", file.length());
        gfsFile.setMetaData(meta);
        gfsFile.save();

        return gridFS.findOne((ObjectId) gfsFile.getId());
    }

    public GridFsImageData getFrontCoverData() {
        return frontCoverData;
    }

    public GridFsImageData getBackCoverData() {
        return backCoverData;
    }
}
