package com.papao.books.ui.custom;

import com.papao.books.ApplicationService;
import com.papao.books.model.Capitol;
import com.papao.books.model.Carte;
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

public class CapitoleComposite extends Composite implements Observer {

    private ComboImage2 capitoleCombo;
    private Text textTitluCapitol;
    private Text textNrCapitol;
    private Text textNrPagina;
    private Text textMotto;
    private Carte carte;
    private boolean handleSave;

    public CapitoleComposite(Composite parent, boolean handleSave) {
        super(parent, SWT.NONE);

        this.handleSave = handleSave;

        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).spacing(0, 2).extendedMargins(5, 5, 3, 0).applyTo(this);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this);

        addComponents();
    }

    private void addComponents() {
        new Label(this, SWT.NONE).setText("Lista capitole");
        capitoleCombo = new ComboImage2(this, ADD_ADD | ADD_DEL | ADD_CONTENT_PROPOSAL);
        ((GridData) capitoleCombo.getLayoutData()).horizontalAlignment = SWT.BEGINNING;
        ((GridData) capitoleCombo.getLayoutData()).widthHint = 250;
        ((GridData) capitoleCombo.getLayoutData()).horizontalSpan = 1;

        capitoleCombo.getCombo().addListener(SWT.KeyUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (event.keyCode == SWT.CR) {
                    populateFields((Capitol) capitoleCombo.getSelectedElement());
                }
            }
        });
        capitoleCombo.getItemAdd().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                add();
            }
        });
        capitoleCombo.getItemDel().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                del();
            }
        });

        new Label(this, SWT.NONE).setText("Nr capitol");
        this.textNrCapitol = new Text(this, SWT.BORDER);

        new Label(this, SWT.NONE).setText("Titlu");
        this.textTitluCapitol = new Text(this, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(false, false).minSize(100, SWT.DEFAULT).hint(100, SWT.DEFAULT).applyTo(textTitluCapitol);

        new Label(this, SWT.NONE).setText("Pagina");
        this.textNrPagina = new Text(this, SWT.BORDER);

        new Label(this, SWT.NONE).setText("Motto");
        new Label(this, SWT.NONE);

        this.textMotto = new Text(this, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(this.textMotto);
        this.textMotto.addListener(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (SWTeXtension.getSaveTrigger(e)) {
                    save();
                }
            }
        });
        this.textMotto.addListener(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (SWTeXtension.getAddTrigger(e)) {
                    add();
                }
            }
        });

        Composite compButtons = new Composite(this, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(0, 0).equalWidth(true).applyTo(compButtons);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.END).span(2, 1).applyTo(compButtons);

        Button buttonSave = new Button(compButtons, SWT.PUSH);
        buttonSave.setText("&Salvare");
        buttonSave.setToolTipText("Salvare modificari");
        buttonSave.setImage(AppImages.getImage16(AppImages.IMG_OK));
        GridDataFactory.fillDefaults().grab(false, false).align(SWT.END, SWT.END).applyTo(buttonSave);
        SWTeXtension.addImageChangeListener16(buttonSave, AppImages.IMG_OK);
        buttonSave.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                save();
            }
        });

        Button buttonCancel = new Button(compButtons, SWT.PUSH);
        buttonCancel.setText("Inchide");
        buttonCancel.setToolTipText("Renuntare");
        buttonCancel.setImage(AppImages.getImage16(AppImages.IMG_CANCEL));
        GridDataFactory.fillDefaults().grab(false, false).align(SWT.END, SWT.END).applyTo(buttonCancel);
        SWTeXtension.addImageChangeListener16(buttonCancel, AppImages.IMG_CANCEL);
        buttonCancel.addListener(SWT.Selection, new Listener() {
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
        capitoleCombo.getCombo().deselectAll();
        capitoleCombo.getCombo().setText("");
        textMotto.setEnabled(true);
        textMotto.setText("");
        textNrCapitol.setEnabled(true);
        textNrCapitol.setText("");
        textTitluCapitol.setEnabled(true);
        textTitluCapitol.setText("");
        textNrPagina.setEnabled(true);
        textNrPagina.setText("");
        textNrCapitol.setFocus();
    }

    private void del() {
        if (carte == null) {
            SWTeXtension.displayMessageW("Selectati o carte prima data!");
            return;
        }
        Capitol capitol = (Capitol) capitoleCombo.getSelectedElement();
        if (capitol == null) {
            SWTeXtension.displayMessageW("Selectati un capitol prima data!");
            return;
        }
        carte.getCapitole().remove(capitol);
        if (handleSave) {
            ApplicationService.getBookController().save(carte);
            SWTeXtension.displayMessageI("Capitolul a fost sters cu succes!");
            populateFields(null);
        } else {
            capitoleCombo.setInput(carte.getCapitole());
        }
    }

    private void save() {
        Capitol capitol = (Capitol) capitoleCombo.getSelectedElement();
        if (carte.getCapitole().contains(capitol)) {
            carte.getCapitole().remove(capitol);
        }
        capitol = new Capitol(textNrCapitol.getText(), textTitluCapitol.getText(), textNrPagina.getText(), textMotto.getText());
        carte.getCapitole().add(capitol);
        if (handleSave) {
            carte = ApplicationService.getBookController().save(carte);
            capitoleCombo.setInput(carte.getCapitole());
            capitoleCombo.select(capitol.getText());
            SWTeXtension.displayMessageI("Capitolul a fost salvat cu succes!");
        } else {
            populateFields(capitol);
        }
    }

    private void cancel() {
        Capitol capitol = (Capitol) capitoleCombo.getSelectedElement();
        if (capitol != null) {
            textTitluCapitol.setText(capitol.getTitlu());
            textNrPagina.setText(capitol.getPagina());
            textMotto.setText(capitol.getMotto());
            textNrCapitol.setText(capitol.getNr());
        } else {
            textTitluCapitol.setText("");
            textMotto.setText("");
            textNrPagina.setText("");
            textNrCapitol.setText("");
            capitoleCombo.getCombo().deselectAll();
            capitoleCombo.getCombo().setText("");
        }
        textNrCapitol.setEnabled(false);
        textTitluCapitol.setEnabled(false);
        textMotto.setEnabled(false);
        textNrPagina.setEnabled(false);
    }

    private void setInput(Collection<ComboElement> input) {
        capitoleCombo.setInput(input);
    }

    private void setCarte(Carte carte) {
        this.carte = carte;
        populateFields(null);
    }

    private void populateFields(Capitol capitol) {
        capitoleCombo.setInput(null);
        textMotto.setText("");
        textNrPagina.setText("");
        textNrCapitol.setText("");
        textTitluCapitol.setText("");
        if (carte == null) {
            return;
        }
        capitoleCombo.setInput(carte.getCapitole());
        if (capitol != null) {
            capitoleCombo.select(capitol.getText());
        } else if (capitoleCombo.getCombo().getItemCount() > 0) {
            capitoleCombo.getCombo().select(0);
            capitol = (Capitol) capitoleCombo.getSelectedElement();
        }
        if (capitol != null) {
            textNrPagina.setText(capitol.getPagina());
            textMotto.setText(capitol.getMotto());
            textTitluCapitol.setText(capitol.getTitlu());
            textNrCapitol.setText(capitol.getNr());
            textTitluCapitol.setFocus();
            textMotto.setSelection(textTitluCapitol.getText().length());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof EncodePlatform) {
            setCarte((Carte) ((EncodePlatform) o).getObservableObject());
        }
    }
}
