package com.papao.books.view.custom;

import com.papao.books.model.ImagePath;
import com.papao.books.view.AppImages;
import com.papao.books.view.menu.HelpBrowser;
import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.view.AbstractView;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

public class ImageSelectorComposite extends Composite implements Observer {

    private Label labelImage;
    private ImageViewer previewShell;
    private boolean imageChanged;
    private String imagesFolder;
    private String fileName;
    private final int WIDTH = 180;
    private final int HEIGHT = 200;
    private String startUrl = "https://www.google.ro/search?q=&tbm=isch";

    private static String SWT_FULL_IMAGE = "SWT_FULL_IMAGE";
    private static String OS_FILE = "OS_FILE";
    private static String WEB_FILE = "WEB_FILE";
    private static final Logger logger = LoggerFactory.getLogger(ImageSelectorComposite.class);

    public ImageSelectorComposite(Composite parent, Image fullImage, String fileName, final String imagesFolder) {
        super(parent, SWT.NONE);
        this.imagesFolder = imagesFolder;
        this.fileName = fileName;

        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).extendedMargins(5, 5, 5, 5).applyTo(this);
        GridDataFactory.fillDefaults().grab(false, false).hint(190, 240).applyTo(this);

        labelImage = new Label(this, SWT.NONE);
        GridDataFactory.fillDefaults().hint(180, 200).applyTo(labelImage);
        labelImage.addListener(SWT.MouseDown, new Listener() {
            @Override
            public void handleEvent(Event event) {
                displayImage(event);
            }
        });

        final Menu menu = new Menu(getShell(), SWT.POP_UP);
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

        final ToolBar bar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).grab(true, false).applyTo(bar);

        final ToolItem itemLocalSelection = new ToolItem(bar, SWT.PUSH);
        itemLocalSelection.setImage(AppImages.getImage16(AppImages.IMG_SEARCH));
        itemLocalSelection.setHotImage(AppImages.getImage16Focus(AppImages.IMG_SEARCH));
        itemLocalSelection.setText("Local");
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
        itemWebSelection.setText("Web");
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

    private void webImageSearch() {
        final HelpBrowser hb = new HelpBrowser(getShell(), startUrl, true);
        hb.getShell().addListener(SWT.Close, new Listener() {
            @Override
            public void handleEvent(Event event) {
                removeImage();
                ImagePath result = hb.getResult();
                if (result != null) {
                    try {
                        String localFilePath = serializeWebImage(result);
                        loadLocalImage(localFilePath);
                        labelImage.setData(WEB_FILE, result.getFilePath());
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                        SWTeXtension.displayMessageW("Imaginea selectata este invalida sau nu a putut fi incarcata!");
                    }
                }
            }
        });
        hb.open();
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
        }
    }

    private void selectImage() {
        FileDialog dlg;
        try {
            dlg = new FileDialog(getShell(), SWT.OPEN);
            dlg.setFilterExtensions(new String[]{"*.jpg;*.png;*.jpeg;*.bmp;*.gif"});
            dlg.setFilterNames(new String[]{"Imagini (*.*)"});
            String selectedFile = dlg.open();
            loadLocalImage(selectedFile);
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            SWTeXtension.displayMessageW("Imaginea selectata este invalida sau nu a putut fi incarcata!");
        }
    }

    private void loadLocalImage(String localPath) throws IOException {
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
        this.setSize(this.computeSize(SWT.DEFAULT, SWT.DEFAULT));
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

    public String getWebPath() {
        if (imageChanged && labelImage.getData(WEB_FILE) instanceof String) {
            return (String) labelImage.getData(WEB_FILE);
        }
        return null;
    }

    private String serializeWebImage(ImagePath imagePath) throws IOException {
        URL url = new URL(imagePath.getFilePath());
        InputStream in = new BufferedInputStream(url.openStream());
        final String localPath = imagesFolder + "/" + imagePath.getFileName();
        OutputStream out = new BufferedOutputStream(new FileOutputStream(localPath));
        for (int i; (i = in.read()) != -1; ) {
            out.write(i);
        }
        in.close();
        out.close();
        return localPath;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof AbstractView) {
            String valueChanged = ((AbstractView) o).getObservableProperty();
            if (valueChanged != null) {
                String query = valueChanged.replace(" ", "+");
                startUrl = "https://www.google.ro/search?q=" + query + "&tbm=isch";
            }
        }
    }
}
