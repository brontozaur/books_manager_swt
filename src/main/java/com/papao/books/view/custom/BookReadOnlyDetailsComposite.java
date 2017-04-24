package com.papao.books.view.custom;

import com.github.haixing_hu.swt.starrating.StarRating;
import com.mongodb.gridfs.GridFSDBFile;
import com.papao.books.controller.AutorController;
import com.papao.books.controller.BookController;
import com.papao.books.controller.UserController;
import com.papao.books.model.Carte;
import com.papao.books.model.DocumentData;
import com.papao.books.view.auth.EncodeLive;
import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.util.FontUtil;
import com.papao.books.view.view.SWTeXtension;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;
import org.mihalis.opal.notify.Notifier;

import java.io.IOException;
import java.util.Observable;

public class BookReadOnlyDetailsComposite extends Observable{

    private Composite mainComp;
    private ScrolledComposite scrolledComposite;
    private AutorController autorController;
    private UserController userController;
    private BookController bookController;

    private CLabel rightLabelTitle;
    private ImageSelectorComposite rightFrontCoverImageComposite;
    private LinkedInUrlsComposite rightWebResourcesComposite;
    private LinkedinCompositeAutoriLinks rightAutoriComposite;
    private LinkedInSimpleValuesComposite genLiterarComposite;
    private LinkedInSimpleValuesComposite taguriComposite;
    private StarRating bookRating;
    private int ratingValue = 0;
    private Carte carte;

    public BookReadOnlyDetailsComposite(Composite parent,
                                        AutorController autorController,
                                        BookController bookController,
                                        UserController userController) {
        this.autorController = autorController;
        this.userController = userController;
        this.bookController = bookController;

        scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.BORDER);
        mainComp = new Composite(scrolledComposite, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(0, 0).spacing(0, 0).applyTo(mainComp);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(mainComp);

        scrolledComposite.setContent(mainComp);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                Rectangle r = scrolledComposite.getClientArea();
                scrolledComposite.setMinSize(mainComp.computeSize(r.width, SWT.DEFAULT));
            }
        });


        addComponents();
    }

    private void addComponents() {
        rightLabelTitle = new CLabel(mainComp, SWT.CENTER);
        rightLabelTitle.setFont(FontUtil.TAHOMA14_NORMAL);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(rightLabelTitle);
        GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 25).span(2, 1).applyTo(rightLabelTitle);
        rightLabelTitle.setBackground(ColorUtil.COLOR_ALBASTRU_INCHIS);
        rightLabelTitle.setForeground(ColorUtil.COLOR_WHITE);

        Composite temp = new Composite(mainComp, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(2, 2, 5, 5).applyTo(temp);
        GridDataFactory.fillDefaults().span(2, 1).align(SWT.CENTER, SWT.BEGINNING).applyTo(temp);

        bookRating = new StarRating(temp, SWT.READ_ONLY, StarRating.Size.SMALL, 5);
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(bookRating);
        bookRating.addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (carte == null || ratingValue == bookRating.getCurrentNumberOfStars()) {
                    return;
                }
                ratingValue = bookRating.getCurrentNumberOfStars();
                userController.saveBookRatingForCurrentUser(carte.getId(), ratingValue);
                Notifier.notify("Notificare", "Nota a fost salvata cu succes!");
                setChanged();
                notifyObservers();
            }
        });

        rightFrontCoverImageComposite = new ImageSelectorComposite(temp, null, null, autorController.getAppImagesFolder());
        rightFrontCoverImageComposite.getLabelImage().addListener(SWT.Paint, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (rightFrontCoverImageComposite.imageChanged() && carte != null) {
                    autorController.removeDocument(carte.getCopertaFata().getId());
                    carte.setCopertaFata(null);
                    try {
                        carte.setCopertaFata(autorController.saveDocument(rightFrontCoverImageComposite));
                        carte = bookController.save(carte);
                        setChanged();
                        notifyObservers();

                        rightFrontCoverImageComposite.setImageChanged(false);
                    } catch (IOException e) {
                        SWTeXtension.displayMessageE(e.getMessage(), e);
                    }
                }
            }
        });

        label("Web");
        rightWebResourcesComposite = new LinkedInUrlsComposite(mainComp, null);

        label(" Autori");
        rightAutoriComposite = new LinkedinCompositeAutoriLinks(mainComp, null, autorController);

        label("Gen");
        genLiterarComposite = new LinkedInSimpleValuesComposite(mainComp);

        label(" Taguri");
        taguriComposite = new LinkedInSimpleValuesComposite(mainComp);
    }

    public void populateFields(Carte carte) {
        this.carte = carte;
        if (carte.getTitlu().length() > 40) {
            rightLabelTitle.setText(carte.getTitlu().substring(0, 35) + "...");
        } else {
            rightLabelTitle.setText(carte.getTitlu());
        }
        rightLabelTitle.setToolTipText(carte.getTitlu());

        displayImage(carte.getCopertaFata(), rightFrontCoverImageComposite);
        rightWebResourcesComposite.setCarte(carte);
        rightAutoriComposite.setAutori(carte.getIdAutori());
        genLiterarComposite.setValues(carte.getGenLiterar());
        taguriComposite.setValues(carte.getTags());
        mainComp.pack();

        Rectangle r = scrolledComposite.getClientArea();
        scrolledComposite.setMinSize(mainComp.computeSize(r.width, SWT.DEFAULT));

        bookRating.setCurrentNumberOfStars(userController.getPersonalRating(EncodeLive.getIdUser(), carte.getId()));
        ratingValue = bookRating.getCurrentNumberOfStars();
    }

    private void displayImage(final DocumentData coverDescriptor, ImageSelectorComposite imageSelectorComposite) {
        Image image = null;
        String imageName = null;
        if (coverDescriptor != null && coverDescriptor.getId() != null) {
            GridFSDBFile dbFile = bookController.getDocumentData(coverDescriptor.getId());
            if (dbFile != null) {
                image = new Image(Display.getDefault(), dbFile.getInputStream());
                imageName = dbFile.getFilename();
            }
        }
        imageSelectorComposite.setImage(image, imageName);
    }

    private void label(String labelName) {
        Label label = new Label(mainComp, SWT.NONE);
        label.setText(labelName);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.BEGINNING).applyTo(label);
    }

    public Carte getCarte() {
        return carte;
    }
}
