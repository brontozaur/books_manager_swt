package com.papao.books.ui.custom;

import com.papao.books.controller.SettingsController;
import com.papao.books.controller.UserController;
import com.papao.books.model.Carte;
import com.papao.books.model.Notita;
import com.papao.books.model.UserActivity;
import com.papao.books.model.config.TableSetting;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.EncodePlatform;
import com.papao.books.ui.auth.EncodeLive;
import com.papao.books.ui.interfaces.IAdd;
import com.papao.books.ui.interfaces.IDelete;
import com.papao.books.ui.interfaces.IModify;
import com.papao.books.ui.util.ColorUtil;
import com.papao.books.ui.util.ObjectUtil;
import com.papao.books.ui.util.WidgetTableUtil;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.SWTeXtension;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;

import java.util.Observable;
import java.util.Observer;

public class CarteNotiteTableComposite extends Composite implements Observer, IAdd, IModify, IDelete {

    private Table table;
    private ToolItem itemAdd;
    private ToolItem itemMod;
    private ToolItem itemDel;
    private Carte carte = null;

    private static final String TABLE_KEY = "notiteTable";

    public CarteNotiteTableComposite(Composite parent) {
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
        itemDel.setToolTipText("Stergere");
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

        TableSetting setting = SettingsController.getTableSetting(2, getClass(), TABLE_KEY);
        int[] dims = setting.getWidths();
        int[] aligns = setting.getAligns();
        boolean[] visible = setting.getVisibility();


        TableColumn column = new TableColumn(table, SWT.LEFT);
        column.setText("Nr. pagină");
        column.setResizable(true);
        column.setWidth(visible[0] ? dims[0] : 0);
        column.setAlignment(aligns[0]);

        column = new TableColumn(table, SWT.LEFT);
        column.setText("Conținut");
        column.setResizable(true);
        column.setWidth(visible[1] ? dims[1] : 0);
        column.setAlignment(aligns[1]);

        SWTeXtension.addColoredFocusListener(table, ColorUtil.COLOR_FOCUS_YELLOW);
        WidgetTableUtil.customizeTable(this.table, getClass(), TABLE_KEY);
    }

    @Override
    public boolean add() {
        NotitaView view = new NotitaView(getShell(), new Notita(), table, AbstractView.MODE_ADD);
        view.open();
        if (view.getUserAction() == SWT.OK) {
            Notita notita = view.getNotita();
            showItem(null, notita);

            UserActivity userActivity = UserController.getUserActivity(EncodeLive.getIdUser(), carte.getId());
            if (userActivity == null) {
                userActivity = new UserActivity();
                userActivity.setUserId(EncodeLive.getIdUser());
                userActivity.setBookId(carte.getId());
            }
            userActivity.getNotite().add(notita);
            UserController.saveUserActivity(userActivity);
            SWTeXtension.displayMessageI("Notița a fost salvată cu succes!");
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
        Notita notita = (Notita) ObjectUtil.copy(item.getData());
        NotitaView notitaView = new NotitaView(getShell(), notita, table, AbstractView.MODE_MODIFY);
        notitaView.open();
        if (notitaView.getUserAction() == SWT.OK) {
            UserActivity userActivity = UserController.getUserActivity(EncodeLive.getIdUser(), carte.getId());
            userActivity.getNotite().remove((Notita) table.getSelection()[0].getData());
            notita = notitaView.getNotita();
            showItem(table.getSelection()[0], notita);
            userActivity.getNotite().add(notita);
            UserController.saveUserActivity(userActivity);
            SWTeXtension.displayMessageI("Notița a fost salvată cu succes!");
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
        if (SWTeXtension.displayMessageQ("Sunteti siguri că doriți să ștergeți " + (selectionCount > 1 ? "notițele selectate?" : "notița selectată?")) == SWT.NO) {
            return false;
        }
        for (TableItem item : table.getSelection()) {
            Notita notita = (Notita) item.getData();
            UserActivity userActivity = UserController.getUserActivity(EncodeLive.getIdUser(), carte.getId());
            userActivity.getNotite().remove(notita);
            UserController.saveUserActivity(userActivity);
            item.dispose();
        }
        SWTeXtension.displayMessageI("Am șters " + (selectionCount > 1 ? "notițe." : "notiță."));
        enableOps();
        return true;
    }

    private void populateFields() {
        if (carte == null) {
            table.clearAll();
            enableOps();
            return;
        }
        UserActivity userActivity = UserController.getUserActivity(EncodeLive.getIdUser(), carte.getId());
        if (userActivity != null && !userActivity.getNotite().isEmpty()) {
            for (Notita notita : userActivity.getNotite()) {
                showItem(null, notita);
            }
        }
    }

    private void showItem(TableItem item, Notita notita) {
        if (table.isDisposed()) {
            return;
        }
        if (item == null || item.isDisposed()) {
            item = new TableItem(table, SWT.NONE);
        }
        item.setText(0, notita.getNrPagina());
        item.setText(1, notita.getContent());
        item.setData(notita);
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
