package com.papao.books.ui.custom;

import com.mongodb.gridfs.GridFSDBFile;
import com.papao.books.ApplicationService;
import com.papao.books.controller.ApplicationController;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.util.WidgetCompositeUtil;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;

import java.io.IOException;

public class ImageViewer {

    private final static Logger logger = Logger.getLogger(ImageViewer.class);
    private Shell shell;
    private ObjectId bucketId;
    private String imageName;

    public ImageViewer(ImageData imageData) {
        show(imageData, null);
    }

    public ImageViewer(Image image) {
        show(null, image);
    }

    private void show(ImageData imageData, Image image) {
        try {
            shell = new Shell();
            shell.setText("Vizualizare imagine");
            shell.setImage(AppImages.getImage16(AppImages.IMG_SHOW));
            shell.setLayout(new GridLayout());
            shell.addListener(SWT.KeyDown, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    if (event.keyCode == SWT.ESC) {
                        shell.close();
                    }
                }
            });

            if (image == null) {
                image = new Image(Display.getDefault(), imageData);
            }

            final int imageWith = image.getBounds().width;
            final int imageHeight = image.getBounds().height;

            org.eclipse.swt.graphics.Rectangle monitorBounds = Display.getDefault().getPrimaryMonitor().getBounds();

            final int monitorWidth = monitorBounds.width;
            final int monitorHeight = monitorBounds.height;

            final boolean needsBorders = imageWith > monitorWidth - 50 || imageHeight > monitorHeight - 50;

            final ScrolledComposite sc = new ScrolledComposite(shell, needsBorders ? SWT.H_SCROLL | SWT.V_SCROLL : SWT.NONE);
            GridDataFactory.fillDefaults().grab(true, true).applyTo(sc);
            sc.addListener(SWT.Activate, new Listener() {
                public void handleEvent(Event e) {
                    sc.setFocus();
                }
            });
            sc.addListener(SWT.KeyDown, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    if (event.keyCode == SWT.ESC) {
                        shell.close();
                    }
                }
            });

            Label imgLabel = new Label(sc, SWT.NONE);
            imgLabel.setImage(image);
            imgLabel.setSize(imgLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            imgLabel.addListener(SWT.MouseDown, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    if (event.button == 1) {
                        shell.close();
                    }
                }
            });
            sc.setContent(imgLabel);

            final Menu imageSaveMenu = new Menu(getShell(), SWT.POP_UP);
            MenuItem menuItem = new MenuItem(imageSaveMenu, SWT.PUSH);
            menuItem.setText("Salvare imagine");
            menuItem.setImage(AppImages.getImage16(AppImages.IMG_OK));
            menuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event event) {
                    saveImage();
                }
            });

            imgLabel.addListener(SWT.MenuDetect, new Listener() {
                @Override
                public final void handleEvent(final Event event) {
                    if (!imageSaveMenu.isDisposed()) {
                        imageSaveMenu.setVisible(true);
                    }
                }
            });

            if (needsBorders) {
                shell.setSize(monitorWidth * 80 / 100, monitorHeight * 80 / 100);
            } else {
                sc.setSize(imageWith, imageHeight);
                shell.setSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            }
            WidgetCompositeUtil.centerInDisplayForFixedWidthsShells(shell);
            shell.open();
            sc.notifyListeners(SWT.Activate, new Event());
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            SWTeXtension.displayMessageE(exc.getMessage(), exc);
        }
    }

    private void saveImage() {
        if (this.bucketId == null) {
            SWTeXtension.displayMessageI("Id imagine invalid!");
            return;
        }
        GridFSDBFile imageDb = ApplicationController.getDocumentData(this.bucketId);
        if (imageDb == null) {
            SWTeXtension.displayMessageI("Imagine invalida cu id " + this.bucketId);
            return;
        }
        try {
            String imagePath = ApplicationService.getApplicationConfig().getAppImagesExportFolder() + "/" + this.imageName;
            imageDb.writeTo(imagePath);
            Program.launch(imagePath);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            SWTeXtension.displayMessageE(e.getMessage(), e);
        }
    }

    public void setBucketId(ObjectId id) {
        this.bucketId = id;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
        shell.setText(imageName);
    }

    public Shell getShell() {
        return this.shell;
    }

    public void open() {
        this.shell.open();
    }
}
