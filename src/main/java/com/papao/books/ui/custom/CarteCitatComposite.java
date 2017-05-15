package com.papao.books.ui.custom;

import com.papao.books.controller.UserController;
import com.papao.books.model.Carte;
import com.papao.books.model.Citat;
import com.papao.books.model.UserActivity;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.EncodePlatform;
import com.papao.books.ui.auth.EncodeLive;
import com.papao.books.ui.view.SWTeXtension;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import static com.papao.books.ui.custom.ComboImage2.*;

public class CarteCitatComposite extends Composite implements Observer {

    private ComboImage2 citateCombo;
    private Text textCitat;
    private Button buttonSave;
    private Button buttonCancel;
    private Carte carte;

    public CarteCitatComposite(Composite parent) {
        super(parent, SWT.NONE);

        GridLayoutFactory.fillDefaults().numColumns(1).spacing(0, 2).extendedMargins(5, 5, 3, 0).applyTo(this);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this);

        addComponents();
    }

    private void addComponents() {
        citateCombo = new ComboImage2(this, ADD_ADD | ADD_DEL | ADD_CONTENT_PROPOSAL, "Citat");
        ((GridData) citateCombo.getLayoutData()).horizontalAlignment = SWT.BEGINNING;
        ((GridData) citateCombo.getLayoutData()).widthHint = 250;
        citateCombo.getCombo().addListener(SWT.KeyUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (event.keyCode == SWT.CR) {
                    populateFields((Citat) citateCombo.getSelectedElement());
                }
            }
        });
        citateCombo.getItemAdd().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                add();
            }
        });
        citateCombo.getItemDel().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                del();
            }
        });

        this.textCitat = new Text(this, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().grab(true, true).hint(300, SWT.DEFAULT).applyTo(this.textCitat);
        this.textCitat.addListener(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (SWTeXtension.getSaveTrigger(e)) {
                    save();
                }
            }
        });
        this.textCitat.addListener(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (SWTeXtension.getNewTrigger(e)) {
                    add();
                }
            }
        });

        Composite compButtons = new Composite(this, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(0, 0).equalWidth(true).applyTo(compButtons);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.END).applyTo(compButtons);

        this.buttonSave = new Button(compButtons, SWT.PUSH);
        this.buttonSave.setText("&Salvare");
        this.buttonSave.setToolTipText("Salvare date/confirmare actiune");
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

    private void add() {
        if (carte == null) {
            SWTeXtension.displayMessageW("Selectati o carte prima data!");
            return;
        }
        citateCombo.getCombo().deselectAll();
        citateCombo.getCombo().setText("");
        textCitat.setEnabled(true);
        textCitat.setText("");
        textCitat.setFocus();
    }

    private void del() {
        if (carte == null) {
            SWTeXtension.displayMessageW("Selectati o carte prima data!");
            return;
        }
        Citat citat = (Citat) citateCombo.getSelectedElement();
        if (citat == null) {
            SWTeXtension.displayMessageW("Selectati un citat prima data!");
            return;
        }
        UserActivity userActivity = UserController.getUserActivity(EncodeLive.getIdUser(), carte.getId());
        if (userActivity == null) {
            SWTeXtension.displayMessageE("Citatul selectat nu mai exista!", null);
            populateFields(null);
            return;
        }
        userActivity.getCitate().remove(citat);
        UserController.saveUserActivity(userActivity);
        SWTeXtension.displayMessageI("Citatul a fost sters cu succes!");
        populateFields(null);
    }

    private void save() {
        UserActivity userActivity = UserController.getUserActivity(EncodeLive.getIdUser(), carte.getId());
        if (userActivity == null) {
            userActivity = new UserActivity();
            userActivity.setUserId(EncodeLive.getIdUser());
            userActivity.setBookId(carte.getId());
        }
        Citat citat = (Citat) citateCombo.getSelectedElement();
        if (citat == null) {
            citat = new Citat();
            userActivity.getCitate().add(citat);
        }
        if (userActivity.getCitate().contains(citat)) {
            userActivity.getCitate().remove(citat);
        }
        citat.setContent(textCitat.getText());
        userActivity.getCitate().add(citat);
        UserController.saveUserActivity(userActivity);
        populateFields(citat);
        SWTeXtension.displayMessageI("Citatul a fost salvat cu succes!");
    }

    private void cancel() {
        Citat citat = (Citat) citateCombo.getSelectedElement();
        if (citat != null) {
            textCitat.setText(citat.getText());
        } else {
            textCitat.setText("");
            citateCombo.getCombo().deselectAll();
            citateCombo.getCombo().setText("");
        }
        textCitat.setEnabled(false);
    }

    private void setInput(Collection<ComboElement> input) {
        citateCombo.setInput(input);
    }

    private void setCarte(Carte carte) {
        this.carte = carte;
        populateFields(null);
    }

    private void populateFields(Citat citat) {
        citateCombo.setInput(null);
        textCitat.setText("");
        if (carte == null) {
            return;
        }
        UserActivity userActivity = UserController.getUserActivity(EncodeLive.getIdUser(), carte.getId());
        if (userActivity != null) {
            citateCombo.setInput(userActivity.getCitate());
            if (citat != null) {
                citateCombo.select(citat.getText());
            } else if (citateCombo.getCombo().getItemCount() > 0) {
                citateCombo.getCombo().select(0);
                citat = (Citat) citateCombo.getSelectedElement();
            }
        }
        if (citat != null) {
            textCitat.setText(citat.getText());
            textCitat.setFocus();
            textCitat.setSelection(textCitat.getText().length());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof EncodePlatform) {
            setCarte((Carte) ((EncodePlatform) o).getObservableObject());
        }
    }
}
