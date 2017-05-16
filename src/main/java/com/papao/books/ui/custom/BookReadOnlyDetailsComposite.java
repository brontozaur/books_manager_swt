package com.papao.books.ui.custom;

import com.github.haixing_hu.swt.starrating.StarRating;
import com.mongodb.gridfs.GridFSDBFile;
import com.papao.books.ApplicationService;
import com.papao.books.config.StringSetting;
import com.papao.books.controller.ApplicationController;
import com.papao.books.controller.SettingsController;
import com.papao.books.controller.UserController;
import com.papao.books.model.Carte;
import com.papao.books.model.CarteCitita;
import com.papao.books.model.DocumentData;
import com.papao.books.model.UserActivity;
import com.papao.books.ui.EncodePlatform;
import com.papao.books.ui.auth.EncodeLive;
import com.papao.books.ui.util.ColorUtil;
import com.papao.books.ui.util.FontUtil;
import com.papao.books.ui.view.SWTeXtension;
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
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.Observer;

public class BookReadOnlyDetailsComposite extends Observable implements Observer {

    private Composite mainComp;
    private ScrolledComposite scrolledComposite;

    private CLabel rightLabelTitle;
    private ImageSelectorComposite rightFrontCoverImageComposite;
    private LinkedInUrlsComposite rightWebResourcesComposite;
    private LinkedinCompositeAutoriLinks rightAutoriComposite;
    private LinkedInSimpleValuesComposite genLiterarComposite;
    private LinkedInSimpleValuesComposite taguriComposite;
    private Label createdAtLabel;
    private Label updatedAtLabel;
    private StarRating bookRating;
    private int ratingValue = 0;
    private Carte carte;
    private String observableProperty = null;
    private Button buttonCitita;
    private DateChooserCustom readStartDate;
    private DateChooserCustom readEndDate;

    public BookReadOnlyDetailsComposite(Composite parent) {

        scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
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
        scrolledComposite.addListener(SWT.Activate, new Listener() {
            public void handleEvent(Event e) {
                scrolledComposite.setFocus();
            }
        });

        addComponents();
    }

    private void addComponents() {
        rightLabelTitle = new CLabel(mainComp, SWT.CENTER | SWT.BORDER);
        rightLabelTitle.setFont(FontUtil.TAHOMA12_NORMAL);
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
                UserController.saveBookRatingForCurrentUser(carte.getId(), ratingValue);
                SWTeXtension.displayMessageI("Nota a fost salvata cu succes!");
                setChanged();
                notifyObservers();
            }
        });

        rightFrontCoverImageComposite = new ImageSelectorComposite(temp, null, null);
        this.addObserver(rightFrontCoverImageComposite);
        rightFrontCoverImageComposite.addObserver(this);
        rightFrontCoverImageComposite.getLabelImage().addListener(SWT.Paint, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (rightFrontCoverImageComposite.imageChanged() && carte != null) {
                    ApplicationController.removeDocument(carte.getCopertaFata().getId());
                    carte.setCopertaFata(null);
                    try {
                        carte.setCopertaFata(ApplicationController.saveDocument(rightFrontCoverImageComposite));
                        carte = ApplicationService.getBookController().save(carte);
                        setChanged();
                        notifyObservers();

                        rightFrontCoverImageComposite.setImageChanged(false);
                    } catch (IOException e) {
                        SWTeXtension.displayMessageE(e.getMessage(), e);
                    }
                }
            }
        });

        temp = new Composite(mainComp, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).spacing(5, 2).applyTo(temp);
        GridDataFactory.fillDefaults().span(2, 1).grab(true, true).applyTo(temp);

        label("Web", temp);
        rightWebResourcesComposite = new LinkedInUrlsComposite(temp, null);

        label(" Autori", temp);
        rightAutoriComposite = new LinkedinCompositeAutoriLinks(temp, null);

        label("Gen", temp);
        genLiterarComposite = new LinkedInSimpleValuesComposite(temp);

        label(" Taguri", temp);
        taguriComposite = new LinkedInSimpleValuesComposite(temp);

        label("Creata la", temp);
        createdAtLabel = new Label(temp, SWT.NONE);

        label("Modificata la", temp);
        updatedAtLabel = new Label(temp, SWT.NONE);

        buttonCitita = new Button(temp, SWT.CHECK);
        buttonCitita.setText("citita");
        buttonCitita.setEnabled(false);
        GridDataFactory.fillDefaults().span(2, 1).applyTo(buttonCitita);
        buttonCitita.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                readStartDate.setEnabled(buttonCitita.getSelection());
                readEndDate.setEnabled(buttonCitita.getSelection());
                saveUserActivity();
            }
        });

        Composite comp = new Composite(temp, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).spacing(0, 0).applyTo(comp);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).span(2, 1).applyTo(comp);

        readStartDate = new DateChooserCustom(comp);
        readStartDate.setEnabled(false);
        readStartDate.getFormattedText().addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                saveUserActivity();
            }
        });

        new Label(comp, SWT.NONE).setText(" - ");

        readEndDate = new DateChooserCustom(comp);
        readEndDate.setEnabled(false);
        readEndDate.getFormattedText().addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                saveUserActivity();
            }
        });
    }

    private void saveUserActivity() {
        UserActivity userActivity = UserController.getUserActivity(EncodeLive.getIdUser(), carte.getId());
        if (userActivity == null) {
            userActivity = new UserActivity();
            userActivity.setBookId(carte.getId());
            userActivity.setUserId(EncodeLive.getIdUser());
        }
        CarteCitita carteCitita = userActivity.getCarteCitita();
        if (carteCitita == null) {
            userActivity.setCarteCitita(new CarteCitita());
        }
        userActivity.getCarteCitita().setCitita(buttonCitita.getSelection());
        userActivity.getCarteCitita().setDataStart(readStartDate.getValue());
        userActivity.getCarteCitita().setDataStop(readEndDate.getValue());
        UserController.saveUserActivity(userActivity);
    }

    @Async
    private void populateFields(Carte carte) {
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
        final String dateFormat = SettingsController.getString(StringSetting.APP_DATE_FORMAT) + " " + SettingsController.getString(StringSetting.APP_TIME_FORMAT);
        final DateFormat df = new SimpleDateFormat(dateFormat);

        createdAtLabel.setText(carte.getCreatedAt() != null ? df.format(carte.getCreatedAt()) : "");
        updatedAtLabel.setText(carte.getUpdatedAt() != null ? df.format(carte.getUpdatedAt()) : "");

        UserActivity userActivity = UserController.getUserActivity(EncodeLive.getIdUser(), carte.getId());
        CarteCitita carteCitita = null;
        if (userActivity != null) {
            carteCitita = userActivity.getCarteCitita();
            if (carteCitita.getDataStart() != null) {
                readStartDate.setValue(carteCitita.getDataStart());
            }
            if (carteCitita.getDataStop() != null) {
                readEndDate.setValue(carteCitita.getDataStop());
            }
        }
        buttonCitita.setEnabled(carte.getId() != null);
        buttonCitita.setSelection(carteCitita != null && carteCitita.isCitita());
        readStartDate.setEnabled(buttonCitita.getEnabled() && buttonCitita.getSelection());
        readEndDate.setEnabled(buttonCitita.getEnabled() && buttonCitita.getSelection());

        mainComp.pack();

        Rectangle r = scrolledComposite.getClientArea();
        scrolledComposite.setMinSize(mainComp.computeSize(r.width, SWT.DEFAULT));

        bookRating.setCurrentNumberOfStars(UserController.getPersonalRating(EncodeLive.getIdUser(), carte.getId()));
        ratingValue = bookRating.getCurrentNumberOfStars();
        observableProperty = rightAutoriComposite.getGoogleSearchTerm() + " - " + carte.getTitlu();

        setChanged();
        notifyObservers();
    }

    private void displayImage(final DocumentData coverDescriptor, ImageSelectorComposite imageSelectorComposite) {
        Image image = null;
        String imageName = null;
        if (coverDescriptor != null && coverDescriptor.getId() != null) {
            GridFSDBFile dbFile = ApplicationController.getDocumentData(coverDescriptor.getId());
            if (dbFile != null) {
                image = new Image(Display.getDefault(), dbFile.getInputStream());
                imageName = dbFile.getFilename();
            }
        }
        imageSelectorComposite.setImage(image, imageName);
    }

    private void label(String labelName, Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(labelName);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.BEGINNING).applyTo(label);
    }

    public Carte getCarte() {
        return carte;
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof ImageGalleryComposite) {
            ImageGalleryComposite gallery = (ImageGalleryComposite) observable;
            populateFields(gallery.getSelected());
        } else if (observable instanceof ImageSelectorComposite) {
            setChanged();
            notifyObservers();
        } else if (observable instanceof EncodePlatform) {
            populateFields((Carte) ((EncodePlatform) observable).getObservableObject());
        }
    }

    public String getObservableProperty() {
        return this.observableProperty;
    }
}
