package com.papao.books.view.custom;

import com.github.haixing_hu.swt.starrating.StarRating;
import com.papao.books.controller.BookController;
import com.papao.books.controller.UserController;
import com.papao.books.model.Carte;
import com.papao.books.view.AppImages;
import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.RowDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

import java.util.Observable;

public class ImageViewComposite extends Observable {

    private Label labelImage;
    private ImageViewer previewShell;
    private final int WIDTH = 150;
    private final int HEIGHT = 180;
    private BookController bookController;
    private UserController userController;
    private StarRating starRating;
    private CLabel labelName;
    private Composite mainComposite;
    private Carte carte;

    private static String SWT_FULL_IMAGE = "SWT_FULL_IMAGE";
    private static final Logger logger = Logger.getLogger(ImageViewComposite.class);

    public ImageViewComposite(Composite parent, BookController controller, final UserController userController, final Carte carte) {
        this.bookController = controller;
        this.userController = userController;
        this.carte = carte;

        mainComposite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).extendedMargins(10, 10, 5, 0).spacing(2, 2).applyTo(mainComposite);
        RowDataFactory.swtDefaults().hint(WIDTH + 20, HEIGHT + 50).applyTo(mainComposite);
        mainComposite.setBackground(ColorUtil.COLOR_WHITE);

        starRating = new StarRating(mainComposite, SWT.READ_ONLY, StarRating.Size.SMALL, 5);
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(starRating);
        starRating.setBackground(ColorUtil.COLOR_WHITE);
        starRating.addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (carte == null || userController.getPersonalRating(carte.getId()) == starRating.getCurrentNumberOfStars()) {
                    return;
                }
                userController.saveBookRatingForCurrentUser(carte.getId(), starRating.getCurrentNumberOfStars());
                SWTeXtension.displayMessageI("Nota a fost salvata cu succes!");
                setChanged();
                notifyObservers();
            }
        });

        labelImage = new Label(mainComposite, SWT.NONE);
//        labelImage.addListener(SWT.MouseHover, new Listener() {
//            @Override
//            public void handleEvent(Event event) {
//                displayImage(event);
//            }
//        });
        GridDataFactory.fillDefaults().hint(WIDTH, HEIGHT).align(SWT.CENTER, SWT.CENTER).applyTo(labelImage);
        labelImage.addListener(SWT.Paint, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                e.gc.setForeground(ColorUtil.COLOR_BLACK);
                e.gc.drawRoundRectangle(0,
                        0,
                        labelImage.getBounds().width - 1,
                        labelImage.getBounds().height - 1,
                        3,
                        3);

            }
        });
        labelImage.addListener(SWT.MouseEnter, new Listener() {
            @Override
            public void handleEvent(Event event) {
                labelImage.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_HAND));
                mainComposite.setBackground(ColorUtil.COLOR_ALBASTRU_INCHIS);
                mainComposite.setForeground(ColorUtil.COLOR_WHITE);
                for (Control control : mainComposite.getChildren()) {
                    if (control == labelImage) {
                        continue;
                    }
                    control.setBackground(ColorUtil.COLOR_ALBASTRU_INCHIS);
                    control.setForeground(ColorUtil.COLOR_WHITE);
                }
            }
        });
        labelImage.addListener(SWT.MouseExit, new Listener() {
            @Override
            public void handleEvent(Event event) {
                labelImage.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_ARROW));
                mainComposite.setBackground(ColorUtil.COLOR_WHITE);
                mainComposite.setForeground(ColorUtil.COLOR_BLACK);
                for (Control control : mainComposite.getChildren()) {
                    if (control == labelImage) {
                        continue;
                    }
                    control.setBackground(ColorUtil.COLOR_WHITE);
                    control.setForeground(ColorUtil.COLOR_BLACK);
                }
            }
        });
        labelImage.addListener(SWT.MouseDown, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                ImageViewComposite.this.setChanged();
                ImageViewComposite.this.notifyObservers();
            }
        });

        labelName = new CLabel(mainComposite, SWT.CENTER);
        labelName.setBackground(ColorUtil.COLOR_WHITE);
        GridDataFactory.fillDefaults().hint(WIDTH, SWT.DEFAULT).align(SWT.CENTER, SWT.END).applyTo(labelName);

        populateFields(carte);

        mainComposite.addListener(SWT.Dispose, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (labelImage.getData() != null) {
                    ((Image) labelImage.getData(SWT_FULL_IMAGE)).dispose();
                }
                if (labelImage.getImage() != null) {
                    labelImage.getImage().dispose();
                }
            }
        });

        populateFields(carte);
    }

    public void populateFields(Carte carte) {
        Image fullImage = bookController.getImage(carte.getCopertaFata());
        if (fullImage != null) {
            labelImage.setData(SWT_FULL_IMAGE, fullImage);
            labelImage.setData(carte.getCopertaFata().getFileName());
            Image resizedImage = AppImages.getImage(fullImage, WIDTH, HEIGHT);
            labelImage.setImage(resizedImage);
        } else {
            labelImage.setText(" fara \n  imagine");
        }

        starRating.setCurrentNumberOfStars(userController.getPersonalRating(carte.getId()));
        labelName.setText(carte.getTitlu());
        labelImage.setToolTipText(bookController.getBookAuthorNames(carte) + " - " + carte.getTitlu());
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

    public Carte getCarte() {
        return this.carte;
    }
}
