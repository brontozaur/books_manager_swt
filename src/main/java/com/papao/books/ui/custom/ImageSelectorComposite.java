package com.papao.books.ui.custom;

import com.papao.books.ApplicationService;
import com.papao.books.model.ImagePath;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.menu.WebBrowser;
import com.papao.books.ui.util.ColorUtil;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

import java.io.*;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

public class ImageSelectorComposite extends Observable implements Observer {

    private static final Logger logger = Logger.getLogger(ImageSelectorComposite.class);

    private static String SWT_FULL_IMAGE = "SWT_FULL_IMAGE";
    private static String OS_FILE = "OS_FILE";
    private static String WEB_FILE = "WEB_FILE";

    private ObjectId imageId;
    private Label labelImage;
    private ImageViewer previewShell;
    private boolean imageChanged;
    private String fileName;
    private final int WIDTH = 160;
    private final int HEIGHT = 180;
    private Composite mainComposite;
    private String searchTerm;

    public ImageSelectorComposite(Composite parent, Image fullImage, String fileName) {
        this.fileName = fileName;

        this.mainComposite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).extendedMargins(5, 5, 5, 5).applyTo(this.mainComposite);
        GridDataFactory.fillDefaults().grab(false, false).hint(WIDTH + 15, HEIGHT + 40).applyTo(this.mainComposite);

        labelImage = new Label(this.mainComposite, SWT.NONE);
        GridDataFactory.fillDefaults().hint(WIDTH, HEIGHT).align(SWT.CENTER, SWT.FILL).applyTo(labelImage);
        labelImage.addListener(SWT.MouseDown, new Listener() {
            @Override
            public void handleEvent(Event event) {
                displayImage(event);
            }
        });
        DropTarget dt = new DropTarget(labelImage, DND.DROP_DEFAULT | DND.DROP_MOVE);
        dt.setTransfer(new Transfer[]{ImageTransfer.getInstance(), FileTransfer.getInstance()});
        dt.addDropListener(new DropTargetAdapter() {
            @Override
            public final void drop(final DropTargetEvent event) {
                if (event.data instanceof String[] && ((String[]) event.data).length > 0) {
                    try {
                        removeImage();
                        loadLocalImage(((String[]) event.data)[0]);
                    } catch (IOException | SWTException e) {
                        logger.error(e.getMessage(), e);
                        SWTeXtension.displayMessageE("Imaginea nu a putut fi incarcata!", e);
                    }
                }
            }
        });

        final Menu menu = new Menu(this.mainComposite.getShell(), SWT.POP_UP);
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Fisier local");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                removeImage();
                selectImage();
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Internet");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                webImageSearch();
            }
        });

        final ToolBar bar = new ToolBar(this.mainComposite, SWT.FLAT | SWT.RIGHT);
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.END).grab(true, false).applyTo(bar);

        final ToolItem itemLocalSelection = new ToolItem(bar, SWT.PUSH);
        itemLocalSelection.setImage(AppImages.getImage16(AppImages.IMG_SEARCH));
        itemLocalSelection.setHotImage(AppImages.getImage16Focus(AppImages.IMG_SEARCH));
        itemLocalSelection.setToolTipText("Selectie imagine de pe calculator");
        itemLocalSelection.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                selectImage();
            }
        });

        ToolItem itemWebSelection = new ToolItem(bar, SWT.PUSH);
        itemWebSelection.setImage(AppImages.getImage16(AppImages.IMG_BROWSER));
        itemWebSelection.setHotImage(AppImages.getImage16Focus(AppImages.IMG_BROWSER));
        itemWebSelection.setToolTipText("Cautare imagine pe internet");
        itemWebSelection.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                webImageSearch();
            }
        });

        ToolItem itemRemove = new ToolItem(bar, SWT.PUSH);
        itemRemove.setImage(AppImages.getImage16(AppImages.IMG_CANCEL));
        itemRemove.setHotImage(AppImages.getImage16Focus(AppImages.IMG_CANCEL));
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
        this.mainComposite.setSize(this.mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        this.mainComposite.addListener(SWT.Paint, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                e.gc.setForeground(ColorUtil.COLOR_BLACK);
                e.gc.drawRoundRectangle(0,
                        0,
                        mainComposite.getClientArea().width - 1,
                        mainComposite.getClientArea().height - 1,
                        3,
                        3);

            }
        });
    }

    private void webImageSearch() {
        final WebBrowser hb = new WebBrowser(this.mainComposite.getShell(), searchTerm);
        hb.getShell().addListener(SWT.Close, new Listener() {
            @Override
            public void handleEvent(Event event) {
                ImagePath result = hb.getResult();
                if (result != null) {
                    try {
                        removeImage();
                        String localFilePath = serializeWebImage(result);
                        loadLocalImage(localFilePath);
                        labelImage.setData(WEB_FILE, result.getFilePath());
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        SWTeXtension.displayMessageE("Imaginea selectata este invalida sau nu a putut fi incarcata!", e);
                    }
                }
            }
        });
        hb.open(true, false);
    }

    public Label getLabelImage() {
        return this.labelImage;
    }

    private void populateFields(Image fullImage) {
        labelImage.setData(SWT_FULL_IMAGE, fullImage);
        labelImage.setData(OS_FILE, null);
        labelImage.setData(WEB_FILE, null);
        labelImage.setData(fileName);
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
                previewShell.setBucketId(imageId);
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
            labelImage.setData(WEB_FILE, null);
            labelImage.setData(null);
            if (labelImage.getImage() != null && !labelImage.getImage().isDisposed()) {
                labelImage.getImage().dispose();
            }
            labelImage.setImage(null);
            imageChanged = true;
            setChanged();
            notifyObservers();
        }
    }

    private void selectImage() {
        FileDialog dlg;
        try {
            dlg = new FileDialog(this.mainComposite.getShell(), SWT.OPEN);
            dlg.setFilterExtensions(new String[]{"*.jpg;*.png;*.jpeg;*.bmp;*.gif"});
            dlg.setFilterNames(new String[]{"Imagini (*.*)"});
            String selectedFile = dlg.open();
            loadLocalImage(selectedFile);
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            SWTeXtension.displayMessageW("Imaginea selectata este invalida sau nu a putut fi incarcata!");
        }
    }

    private void loadLocalImage(String localPath) throws IOException, SWTException {
        if (StringUtils.isEmpty(localPath)) {
            return;
        }
        File file = new File(localPath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("Cannot find the file " + localPath);
        }
        Image fullImage = new Image(Display.getDefault(), localPath);
        labelImage.setData(SWT_FULL_IMAGE, fullImage);
        labelImage.setData(OS_FILE, file);
        labelImage.setData(WEB_FILE, null);
        labelImage.setData(file.getName());
        Image resizedImage = AppImages.getImage(fullImage, WIDTH, HEIGHT);
        labelImage.setImage(resizedImage);
        imageChanged = true;
        this.mainComposite.setSize(this.mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        setChanged();
        notifyObservers();
    }

    public boolean imageChanged() {
        return imageChanged;
    }

    public void setImageChanged(boolean imageChanged) {
        this.imageChanged = imageChanged;
    }

    public File getSelectedFile() {
        if (imageChanged && labelImage.getData(OS_FILE) instanceof File) {
            return (File) labelImage.getData(OS_FILE);
        }
        return null;
    }

    public String getWebPath() {
        if (imageChanged && labelImage.getData(WEB_FILE) instanceof String) {
            return (String) labelImage.getData(WEB_FILE);
        }
        return null;
    }

    private String serializeWebImage(ImagePath imagePath) throws IOException {
        URL url = new URL(imagePath.getFilePath());
        InputStream in = new BufferedInputStream(url.openStream());
        final String localPath = ApplicationService.getApplicationConfig().getAppImagesFolder() + "/" + imagePath.getFileName();
        OutputStream out = new BufferedOutputStream(new FileOutputStream(localPath));
        for (int i; (i = in.read()) != -1; ) {
            out.write(i);
        }
        in.close();
        out.close();
        return localPath;
    }

    public void setImage(Image image, String imageName) {
        this.fileName = imageName;
        if (image == null) {
            if (labelImage.getImage() != null && !labelImage.getImage().isDisposed()) {
                labelImage.getImage().dispose();
            }
            labelImage.setImage(null);
            labelImage.setData(SWT_FULL_IMAGE, null);
            labelImage.setData(OS_FILE, null);
            labelImage.setData(WEB_FILE, null);
            labelImage.setData(null);
            return;
        }
        populateFields(image);
    }

    @Override
    public void update(Observable o, Object arg) {
        String valueChanged = null;
        if (o instanceof AbstractView) {
            valueChanged = ((AbstractView) o).getObservableProperty();
        } else if (o instanceof BookReadOnlyDetailsComposite) {
            valueChanged = ((BookReadOnlyDetailsComposite) o).getObservableProperty();
        }
        if (valueChanged != null) {
            this.searchTerm = valueChanged.replace(" ", "+");
        }
    }

    public void setImageId(ObjectId imageId) {
        this.imageId = imageId;
        if (this.previewShell != null) {
            this.previewShell.setBucketId(imageId);
        }
    }

    public GridData getLayoutData() {
        return (GridData) this.mainComposite.getLayoutData();
    }
}
