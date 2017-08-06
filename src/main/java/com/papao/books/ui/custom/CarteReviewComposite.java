package com.papao.books.ui.custom;

import com.papao.books.controller.UserController;
import com.papao.books.model.Carte;
import com.papao.books.model.UserActivity;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.EncodePlatform;
import com.papao.books.ui.auth.EncodeLive;
import com.papao.books.ui.view.SWTeXtension;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

import java.util.Observable;
import java.util.Observer;

public class CarteReviewComposite extends Composite implements Observer {

    private Text textDescriere;
    private Button buttonSave;
    private Button buttonCancel;
    private Carte carte;

    public CarteReviewComposite(Composite parent) {
        super(parent, SWT.NONE);

        GridLayoutFactory.fillDefaults().numColumns(2).spacing(0, 2).extendedMargins(5, 5, 3, 0).applyTo(this);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this);

        addComponents();
    }

    private void addComponents() {
        this.textDescriere = new Text(this, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().grab(true, true).span(2, 1).hint(300, SWT.DEFAULT).applyTo(this.textDescriere);

        Composite compButtons = new Composite(this, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(0, 0).equalWidth(true).applyTo(compButtons);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.END).span(5, 1).applyTo(compButtons);

        this.buttonSave = new Button(compButtons, SWT.PUSH);
        this.buttonSave.setText("&Salvare");
        this.buttonSave.setToolTipText("Salvare modificari");
        this.buttonSave.setImage(AppImages.getImage16(AppImages.IMG_OK));
        GridDataFactory.fillDefaults().grab(false, false).align(SWT.END, SWT.END).applyTo(this.buttonSave);
        SWTeXtension.addImageChangeListener16(this.buttonSave, AppImages.IMG_OK);
        this.buttonSave.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                save();
            }
        });

        this.buttonCancel = new Button(compButtons, SWT.PUSH);
        this.buttonCancel.setText("Inchide");
        this.buttonCancel.setToolTipText("Renuntare");
        this.buttonCancel.setImage(AppImages.getImage16(AppImages.IMG_CANCEL));
        GridDataFactory.fillDefaults().grab(false, false).align(SWT.END, SWT.END).applyTo(this.buttonCancel);
        SWTeXtension.addImageChangeListener16(this.buttonCancel, AppImages.IMG_CANCEL);
        this.buttonCancel.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                cancel();
            }
        });
    }

    private void save() {
        if (this.carte == null) {
            return;
        }
        UserActivity userActivity = UserController.getUserActivity(EncodeLive.getIdUser(), carte.getId());
        if (userActivity == null) {
            userActivity = new UserActivity();
            userActivity.setBookId(this.carte.getId());
            userActivity.setUserId(EncodeLive.getIdUser());
        }
        userActivity.setReview(this.textDescriere.getText());
        UserController.saveUserActivity(userActivity);
        SWTeXtension.displayMessageI("Comentariul a fost salvat cu succes!");
    }

    private void cancel() {
        textDescriere.setText("");
    }

    private void setCarte(Carte carte) {
        this.carte = carte;
        populateFields();
    }

    private void populateFields() {
        textDescriere.setText("");
        if (carte == null) {
            return;
        }
        UserActivity userActivity = UserController.getUserActivity(EncodeLive.getIdUser(), carte.getId());
        if (userActivity != null) {
            textDescriere.setText(userActivity.getReview());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof EncodePlatform) {
            setCarte((Carte) ((EncodePlatform) o).getObservableObject());
        }
    }
}
