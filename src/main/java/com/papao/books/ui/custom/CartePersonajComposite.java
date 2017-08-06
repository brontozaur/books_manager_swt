package com.papao.books.ui.custom;

import com.papao.books.ApplicationService;
import com.papao.books.model.Carte;
import com.papao.books.model.Personaj;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.EncodePlatform;
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

public class CartePersonajComposite extends Composite implements Observer {

    private ComboImage2 personajeCombo;
    private Text textNume;
    private Text textRol;
    private Text textDescriere;
    private Button buttonSave;
    private Button buttonCancel;
    private Carte carte;
    private boolean handleSave;

    public CartePersonajComposite(Composite parent, boolean handleSave) {
        super(parent, SWT.NONE);
        this.handleSave = handleSave;

        GridLayoutFactory.fillDefaults().numColumns(2).spacing(0, 2).extendedMargins(5, 5, 3, 0).applyTo(this);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this);

        addComponents();
    }

    private void addComponents() {
        personajeCombo = new ComboImage2(this, ADD_ADD | ADD_DEL | ADD_CONTENT_PROPOSAL, "Personaj");
        ((GridData) personajeCombo.getLayoutData()).horizontalAlignment = SWT.BEGINNING;
        ((GridData) personajeCombo.getLayoutData()).widthHint = 250;
        ((GridData) personajeCombo.getLayoutData()).horizontalSpan = 2;
        personajeCombo.getCombo().addListener(SWT.KeyUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (event.keyCode == SWT.CR) {
                    populateFields((Personaj) personajeCombo.getSelectedElement());
                }
            }
        });
        personajeCombo.getItemAdd().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                add();
            }
        });
        personajeCombo.getItemDel().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                del();
            }
        });

        Label tmp = new Label(this, SWT.NONE);
        tmp.setText("Nume");
        GridDataFactory.fillDefaults().hint(62, SWT.DEFAULT).applyTo(tmp);
        this.textNume = new Text(this, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(textNume);

        tmp = new Label(this, SWT.NONE);
        tmp.setText("Rol");
        GridDataFactory.fillDefaults().hint(62, SWT.DEFAULT).applyTo(tmp);
        this.textRol = new Text(this, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(textRol);

        this.textDescriere = new Text(this, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().grab(true, true).span(2, 1).hint(300, SWT.DEFAULT).applyTo(this.textDescriere);
        this.textDescriere.addListener(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (SWTeXtension.getSaveTrigger(e)) {
                    save();
                }
            }
        });
        this.textDescriere.addListener(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (SWTeXtension.getAddTrigger(e)) {
                    add();
                }
            }
        });

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

    private void add() {
        if (carte == null) {
            SWTeXtension.displayMessageW("Selectati o carte prima data!");
            return;
        }
        personajeCombo.getCombo().deselectAll();
        personajeCombo.getCombo().setText("");
        textDescriere.setText("");
        textNume.setText("");
        textRol.setText("");
        textNume.setFocus();
    }

    private void del() {
        if (carte == null) {
            SWTeXtension.displayMessageW("Selectati o carte prima data!");
            return;
        }
        Personaj personaj = (Personaj) personajeCombo.getSelectedElement();
        if (personaj == null) {
            SWTeXtension.displayMessageW("Selectati un personaj prima data!");
            return;
        }
        carte.getPersonaje().remove(personaj);
        if (handleSave) {
            ApplicationService.getBookController().save(carte);
            SWTeXtension.displayMessageI("Personajul a fost sters cu succes!");
            populateFields(null);
        } else {
            personajeCombo.setInput(carte.getPersonaje());
        }
    }

    private void save() {
        Personaj personaj = new Personaj();
        personaj.setNume(textNume.getText());
        personaj.setDescriere(textDescriere.getText());
        personaj.setRol(textRol.getText());
        carte.getPersonaje().add(personaj);
        if (handleSave) {
            carte = ApplicationService.getBookController().save(carte);
            personajeCombo.setInput(carte.getPersonaje());
            personajeCombo.select(personaj.getText());
            SWTeXtension.displayMessageI("Personajul a fost salvat cu succes!");
        } else {
            populateFields(personaj);
        }
    }

    private void cancel() {
        Personaj personaj = (Personaj) personajeCombo.getSelectedElement();
        if (personaj != null) {
            textNume.setText(personaj.getText());
            textRol.setText(personaj.getText());
            textDescriere.setText(personaj.getDescriere());
        } else {
            textNume.setText("");
            textRol.setText("");
            textDescriere.setText("");
            personajeCombo.getCombo().deselectAll();
            personajeCombo.getCombo().setText("");
        }
    }

    private void setInput(Collection<ComboElement> input) {
        personajeCombo.setInput(input);
    }

    private void setCarte(Carte carte) {
        this.carte = carte;
        populateFields(null);
    }

    private void populateFields(Personaj personaj) {
        personajeCombo.setInput(null);
        textNume.setText("");
        textRol.setText("");
        textDescriere.setText("");
        if (carte == null) {
            return;
        }
        if (carte != null) {
            personajeCombo.setInput(carte.getPersonaje());
            if (personaj != null) {
                personajeCombo.select(personaj.getText());
            } else if (personajeCombo.getCombo().getItemCount() > 0) {
                personajeCombo.getCombo().select(0);
                personaj = (Personaj) personajeCombo.getSelectedElement();
            }
        }
        if (personaj != null) {
            textNume.setText(personaj.getNume());
            textRol.setText(personaj.getRol());
            textDescriere.setText(personaj.getDescriere());
            textDescriere.setFocus();
            textDescriere.setSelection(textDescriere.getText().length());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof EncodePlatform) {
            setCarte((Carte) ((EncodePlatform) o).getObservableObject());
        }
    }
}
