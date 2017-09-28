package com.papao.books.ui.custom;

import com.papao.books.controller.SettingsController;
import com.papao.books.controller.UserController;
import com.papao.books.model.Carte;
import com.papao.books.model.Citat;
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
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;

import java.util.Observable;
import java.util.Observer;

public class CarteCitateTableComposite extends Composite implements Observer, IAdd, IModify, IDelete {

    private static final Logger logger = Logger.getLogger(CarteCitateTableComposite.class);

    private Table table;
    private ToolItem itemAdd;
    private ToolItem itemMod;
    private ToolItem itemDel;
    private Carte carte = null;

    private static final String TABLE_KEY = "citateTable";

    public CarteCitateTableComposite(Composite parent) {
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
        itemAdd.setToolTipText("Adaugare");
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
        column.setWidth(visible[0] ? dims[0]: 0);
        column.setAlignment(aligns[0]);

        column = new TableColumn(table, SWT.LEFT);
        column.setText("Citat");
        column.setResizable(true);
        column.setWidth(visible[1] ? dims[1]: 0);
        column.setAlignment(aligns[1]);

        SWTeXtension.addColoredFocusListener(table, ColorUtil.COLOR_FOCUS_YELLOW);
        WidgetTableUtil.customizeTable(this.table, getClass(), TABLE_KEY);
    }

    @Override
    public boolean add() {
        CitatView citatView = new CitatView(getShell(), new Citat(), table, AbstractView.MODE_ADD);
        citatView.open();
        if (citatView.getUserAction() == SWT.OK) {
            Citat citat = citatView.getCitat();
            showItem(null, citat);

            UserActivity userActivity = UserController.getUserActivity(EncodeLive.getIdUser(), carte.getId());
            if (userActivity == null) {
                userActivity = new UserActivity();
                userActivity.setUserId(EncodeLive.getIdUser());
                userActivity.setBookId(carte.getId());
            }
            userActivity.getCitate().add(citat);
            UserController.saveUserActivity(userActivity);
            SWTeXtension.displayMessageI("Citatul a fost salvat cu succes!");
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
        Citat citat = (Citat) ObjectUtil.copy(item.getData());
        CitatView citatView = new CitatView(getShell(), citat, table, AbstractView.MODE_MODIFY);
        citatView.open();
        if (citatView.getUserAction() == SWT.OK) {
            UserActivity userActivity = UserController.getUserActivity(EncodeLive.getIdUser(), carte.getId());
            userActivity.getCitate().remove((Citat)table.getSelection()[0].getData());
            citat = citatView.getCitat();
            showItem(table.getSelection()[0], citat);
            userActivity.getCitate().add(citat);
            UserController.saveUserActivity(userActivity);
            SWTeXtension.displayMessageI("Citatul a fost salvat cu succes!");
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
        if (SWTeXtension.displayMessageQ("Sunteti siguri ca doriti sa stergeti " + (selectionCount > 1 ? "citatele selectate?" : "citatul selectat?")) == SWT.NO) {
            return false;
        }
        for (TableItem item : table.getSelection()) {
            Citat citat = (Citat) item.getData();
            UserActivity userActivity = UserController.getUserActivity(EncodeLive.getIdUser(), carte.getId());
            userActivity.getCitate().remove(citat);
            UserController.saveUserActivity(userActivity);
            item.dispose();
        }
        SWTeXtension.displayMessageI("Am sters " + (selectionCount > 1 ? "citatele." : "citat."));
        enableOps();
        return true;
    }

    private void populateFields() {
        if (carte == null || carte.getId() == null) {
            table.clearAll();
            enableOps();
            return;
        }
        UserActivity userActivity = UserController.getUserActivity(EncodeLive.getIdUser(), carte.getId());
        if (userActivity != null && !userActivity.getCitate().isEmpty()) {
            for (Citat citat : userActivity.getCitate()) {
                showItem(null, citat);
            }
        }
    }

    private void showItem(TableItem item, Citat citat) {
        if (table.isDisposed()) {
            return;
        }
        if (item == null || item.isDisposed()) {
            item = new TableItem(table, SWT.NONE);
        }
        item.setText(0, citat.getNrPagina());
        item.setText(1, citat.getContent());
        item.setData(citat);
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
