package com.papao.books.view.custom;

import com.papao.books.view.AppImages;
import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ImageSelectorComposite extends Composite {

    private Label labelImage;
    private ImageViewer previewShell;
    private boolean imageChanged;
    private String imageName;
    private final int WIDTH = 180;
    private final int HEIGHT = 200;

    private static String SWT_FULL_IMAGE = "SWT_FULL_IMAGE";
    private static String OS_FILE = "OS_FILE";
    private static final Logger logger = LoggerFactory.getLogger(ImageSelectorComposite.class);

    public ImageSelectorComposite(Composite parent, Image fullImage, String imageName) {
        super(parent, SWT.NONE);
        this.imageName = imageName;

        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).extendedMargins(5, 5, 5, 5).applyTo(this);
        GridDataFactory.fillDefaults().grab(false, false).hint(190, 240).applyTo(this);

        labelImage = new Label(this, SWT.NONE);
        GridDataFactory.fillDefaults().hint(180, 200).applyTo(labelImage);
        labelImage.addListener(SWT.MouseEnter, new Listener() {
            @Override
            public void handleEvent(Event event) {
                displayImage(event);
            }
        });

        ToolBar bar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true, false).applyTo(bar);

        ToolItem itemSelectie = new ToolItem(bar, SWT.PUSH);
        itemSelectie.setImage(AppImages.getImage16(AppImages.IMG_SEARCH));
        itemSelectie.setHotImage(AppImages.getImage16Focus(AppImages.IMG_SEARCH));
        itemSelectie.setText("Selectie");
        itemSelectie.setToolTipText("Selectie imagine");
        itemSelectie.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                selectImage();
            }
        });

        ToolItem itemRemove = new ToolItem(bar, SWT.PUSH);
        itemRemove.setImage(AppImages.getImage16(AppImages.IMG_CANCEL));
        itemRemove.setHotImage(AppImages.getImage16Focus(AppImages.IMG_CANCEL));
        itemRemove.setText("Stergere");
        itemRemove.setToolTipText("Sterge imagine");
        itemRemove.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                removeImage();
            }
        });

        if (fullImage != null && !fullImage.isDisposed()) {
            populateFields(fullImage);
        }
        this.setSize(this.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        this.addListener(SWT.Paint, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                e.gc.setForeground(ColorUtil.COLOR_BLACK);
                e.gc.drawRoundRectangle(0,
                        0,
                        getClientArea().width - 1,
                        getClientArea().height - 1,
                        3,
                        3);

            }
        });
    }

    private void populateFields(Image fullImage) {
        labelImage.setData(SWT_FULL_IMAGE, fullImage);
        labelImage.setData(OS_FILE, null);
        labelImage.setData(imageName);
        Image resizedImage = AppImages.getImage(fullImage, WIDTH, HEIGHT);
        labelImage.setImage(resizedImage);
    }

    private void displayImage(Event event) {
        if (event.widget.getData(SWT_FULL_IMAGE) instanceof Image) {
            Image image = (Image) event.widget.getData(SWT_FULL_IMAGE);
            if (!image.isDisposed()) {
                if (previewShell != null && !previewShell.getShell().isDisposed()) {
                    previewShell.getShell().close();
                    previewShell = null;
                }
                previewShell = new ImageViewer(image);
                previewShell.setImageName(labelImage.getData() + "");
                previewShell.open();
            }
        }
    }

    private void removeImage() {
        Object fullImage = labelImage.getData(SWT_FULL_IMAGE);
        if (fullImage instanceof Image) {
            if (!((Image) fullImage).isDisposed()) {
                ((Image) fullImage).dispose();
            }
            labelImage.setData(SWT_FULL_IMAGE, null);
            labelImage.setData(OS_FILE, null);
            labelImage.setData(null);
            if (labelImage.getImage() != null && !labelImage.getImage().isDisposed()) {
                labelImage.getImage().dispose();
            }
            labelImage.setImage(null);
            imageChanged = true;
        }
    }

    private void selectImage() {
        FileDialog dlg;
        try {
            dlg = new FileDialog(getShell(), SWT.OPEN);
            dlg.setFilterExtensions(new String[]{"*.jpg;*.png;*.jpeg;*.bmp;*.gif"});
            dlg.setFilterNames(new String[]{"Imagini (*.*)"});
            String selectedFile = dlg.open();
            if (StringUtils.isEmpty(selectedFile)) {
                return;
            }
            File file = new File(selectedFile);
            if (!file.exists() || !file.isFile()) {
                throw new IOException("Cannot find the file " + selectedFile);
            }
            Image fullImage = new Image(Display.getDefault(), selectedFile);
            labelImage.setData(SWT_FULL_IMAGE, fullImage);
            labelImage.setData(OS_FILE, file);
            labelImage.setData(file.getName());
            Image resizedImage = AppImages.getImage(fullImage, WIDTH, HEIGHT);
            labelImage.setImage(resizedImage);
            imageChanged = true;
            this.setSize(this.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            SWTeXtension.displayMessageW("Imaginea selectata este invalida sau nu a putut fi incarcata!");
        }
    }

    public boolean imageChanged() {
        return imageChanged;
    }

    public File getSelectedFile() {
        if (imageChanged && labelImage.getData(OS_FILE) instanceof File) {
            return (File) labelImage.getData(OS_FILE);
        }
        return null;
    }
}
