package com.papao.books.ui.custom;

import com.papao.books.ApplicationService;
import com.papao.books.controller.SettingsController;
import com.papao.books.model.Carte;
import com.papao.books.model.Personaj;
import com.papao.books.model.config.TableSetting;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.EncodePlatform;
import com.papao.books.ui.interfaces.IAdd;
import com.papao.books.ui.interfaces.IDelete;
import com.papao.books.ui.interfaces.IModify;
import com.papao.books.ui.util.ColorUtil;
import com.papao.books.ui.util.ObjectUtil;
import com.papao.books.ui.util.WidgetTableUtil;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;

import java.util.Observable;
import java.util.Observer;

public class CartePersonajTableComposite extends Composite implements Observer, IAdd, IModify, IDelete {

    private static final Logger logger = Logger.getLogger(CartePersonajTableComposite.class);

    private Table table;
    private ToolItem itemAdd;
    private ToolItem itemMod;
    private ToolItem itemDel;
    private Carte carte = null;

    private static final String TABLE_KEY = "personajeTable";

    public CartePersonajTableComposite(Composite parent) {
        super(parent, SWT.NONE);
        this.carte = carte;

        GridLayoutFactory.fillDefaults().numColumns(1).margins(0, 0).spacing(0, 0).extendedMargins(2, 0, 3, 0).applyTo(this);
        GridDataFactory.fillDefaults().grab(true, true).hint(300, 150).applyTo(this);

        addComponents();
        populateFields();
        enableOps();
    }

    private void addComponents() {
        ToolBar barOps = new ToolBar(this, SWT.FLAT | SWT.NO_FOCUS | SWT.RIGHT);
        itemAdd = new ToolItem(barOps, SWT.NONE);
        itemAdd.setImage(AppImages.getImage16(AppImages.IMG_PLUS));
        itemAdd.setHotImage(AppImages.getImage16Focus(AppImages.IMG_PLUS));
        itemAdd.setToolTipText("Adăugare");
        itemAdd.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                add();
            }
        });

        itemMod = new ToolItem(barOps, SWT.NONE);
        itemMod.setImage(AppImages.getImage16(AppImages.IMG_MODIFICARE));
        itemMod.setHotImage(AppImages.getImage16Focus(AppImages.IMG_MODIFICARE));
        itemMod.setToolTipText("Modificare");
        itemMod.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                modify();
            }
        });

        itemDel = new ToolItem(barOps, SWT.NONE);
        itemDel.setImage(AppImages.getImage16(AppImages.IMG_CANCEL));
        itemDel.setHotImage(AppImages.getImage16Focus(AppImages.IMG_CANCEL));
        itemDel.setToolTipText("Ștergere");
        itemDel.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                delete();
            }
        });

        table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(table);
        SWTeXtension.addSelectAllListener(table);
        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                enableOps();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                super.widgetDefaultSelected(e);
                modify();
            }
        });
        SWTeXtension.addKeyDownListeners(table, this);

        TableSetting setting = SettingsController.getTableSetting(3, getClass(), TABLE_KEY);
        int[] dims = setting.getWidths();
        int[] aligns = setting.getAligns();
        boolean[] visible = setting.getVisibility();

        TableColumn column = new TableColumn(table, SWT.LEFT);
        column.setText("Nume");
        column.setResizable(true);
        column.setWidth(visible[0] ? dims[0] : 0);
        column.setAlignment(aligns[0]);

        column = new TableColumn(table, SWT.LEFT);
        column.setText("Rol");
        column.setResizable(true);
        column.setWidth(visible[1] ? dims[1] : 0);
        column.setAlignment(aligns[1]);

        column = new TableColumn(table, SWT.LEFT);
        column.setText("Descriere");
        column.setResizable(true);
        column.setWidth(visible[2] ? dims[2] : 0);
        column.setAlignment(aligns[2]);

        SWTeXtension.addColoredFocusListener(table, ColorUtil.COLOR_FOCUS_YELLOW);
        WidgetTableUtil.customizeTable(this.table, getClass(), TABLE_KEY);
    }

    @Override
    public boolean add() {
        PersonajView personajView = new PersonajView(getShell(), new Personaj(), table, AbstractView.MODE_ADD);
        personajView.open();
        if (personajView.getUserAction() == SWT.OK) {
            Personaj personaj = personajView.getPersonaj();
            showItem(null, personaj);

            carte.getPersonaje().add(personaj);
            ApplicationService.getBookController().save(carte);
            SWTeXtension.displayMessageI("Personajul a fost salvat cu succes!");
        }
        enableOps();
        return true;
    }

    @Override
    public boolean modify() {
        if (table.getSelectionCount() != 1) {
            return false;
        }
        TableItem item = table.getSelection()[0];
        Personaj personaj = (Personaj) ObjectUtil.copy(item.getData());
        PersonajView personajView = new PersonajView(getShell(), personaj, table, AbstractView.MODE_MODIFY);
        personajView.open();
        if (personajView.getUserAction() == SWT.OK) {
            personaj = personajView.getPersonaj();
            showItem(table.getSelection()[0], personaj);
            carte.getPersonaje().remove((Personaj) table.getSelection()[0].getData());
            carte.getPersonaje().add(personaj);
            ApplicationService.getBookController().save(carte);
            SWTeXtension.displayMessageI("Personajul a fost salvat cu succes!");
        }
        enableOps();
        return true;
    }

    @Override
    public boolean delete() {
        final int selectionCount = table.getSelectionCount();
        if (selectionCount == 0) {
            return false;
        }
        if (SWTeXtension.displayMessageQ("Sunteți siguri că doriți să ștergeți " + (selectionCount > 1 ? "personajele selectate?" : "personajul selectat?")) == SWT.NO) {
            return false;
        }
        for (TableItem item : table.getSelection()) {
            Personaj personaj = (Personaj) item.getData();
            carte.getPersonaje().remove(personaj);
            item.dispose();
        }
        ApplicationService.getBookController().save(carte);
        SWTeXtension.displayMessageI("Am șters " + selectionCount + (selectionCount > 1 ? " personaje." : " personaj."));
        enableOps();
        return true;
    }

    private void populateFields() {
        if (carte == null) {
            table.clearAll();
            enableOps();
            return;
        }
        for (Personaj personaj : carte.getPersonaje()) {
            showItem(null, personaj);
        }
    }

    private void showItem(TableItem item, Personaj personaj) {
        if (table.isDisposed()) {
            return;
        }
        if (item == null || item.isDisposed()) {
            item = new TableItem(table, SWT.NONE);
        }
        item.setText(0, personaj.getNume());
        item.setText(1, personaj.getRol());
        item.setText(2, personaj.getDescriere());
        item.setData(personaj);
    }

    private void enableOps() {
        itemAdd.setEnabled(carte != null && carte.getId() != null);
        itemMod.setEnabled(table.getSelectionCount() == 1);
        itemDel.setEnabled(table.getSelectionCount() > 0);
    }

    private void setCarte(Carte carte) {
        this.carte = carte;
        table.removeAll();
        populateFields();
        enableOps();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof EncodePlatform) {
            setCarte((Carte) ((EncodePlatform) o).getObservableObject());
        }
    }
}
