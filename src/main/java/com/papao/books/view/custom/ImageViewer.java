package com.papao.books.view.custom;

import com.papao.books.view.AppImages;
import com.papao.books.view.util.WidgetCompositeUtil;
import com.papao.books.view.view.SWTeXtension;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.java2d.HeadlessGraphicsEnvironment;

import java.awt.*;

public class ImageViewer {

    private final static Logger logger = LoggerFactory.getLogger(ImageViewer.class);
    private Shell shell;

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

            GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle monitorBounds;
            if (environment.isHeadlessInstance()) {
                monitorBounds = ((HeadlessGraphicsEnvironment) environment).getSunGraphicsEnvironment().getScreenDevices()[0].getConfigurations()[0].getBounds();
            } else {
                GraphicsDevice[] devices = environment.getScreenDevices();
                monitorBounds = devices[0].getDefaultConfiguration().getBounds();
            }

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
            sc.setContent(imgLabel);

            if (needsBorders) {
                shell.setSize(monitorWidth * 80 / 100, monitorHeight * 80 / 100);
            } else {
                sc.setSize(imageWith, imageHeight);
                shell.setSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            }
            WidgetCompositeUtil.centerInDisplay(shell);
            shell.open();
            sc.notifyListeners(SWT.Activate, new Event());
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            SWTeXtension.displayMessageE(exc.getMessage(), exc);
        }
    }

    public void setImageName(String imageName) {
        shell.setText("Vizualizare imagine [" + imageName + "]");
    }

    public Shell getShell() {
        return this.shell;
    }

    public void open() {
        this.shell.open();
    }
}
