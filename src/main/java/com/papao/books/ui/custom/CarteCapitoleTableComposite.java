package com.papao.books.ui.custom;

import com.papao.books.ApplicationService;
import com.papao.books.controller.SettingsController;
import com.papao.books.model.Capitol;
import com.papao.books.model.Carte;
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

public class CarteCapitoleTableComposite extends Composite implements Observer, IAdd, IModify, IDelete {

    private static final Logger logger = Logger.getLogger(CarteCapitoleTableComposite.class);

    private Table table;
    private ToolItem itemAdd;
    private ToolItem itemMod;
    private ToolItem itemDel;
    private Carte carte = null;

    private final static int IDX_NR_CAPITOL = 0;
    private final static int IDX_TITLU = 1;
    private final static int IDX_NR_PAGINA = 2;
    private final static int IDX_MOTTO = 3;

    private static final String TABLE_KEY = "capitoleTable";

    public CarteCapitoleTableComposite(Composite parent) {
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

        TableSetting setting = SettingsController.getTableSetting(4, getClass(), TABLE_KEY);
        int[] dims = setting.getWidths();
        int[] aligns = setting.getAligns();
        boolean[] visible = setting.getVisibility();

        TableColumn column = new TableColumn(table, SWT.LEFT);
        column.setText("Nr. capitol");
        column.setResizable(true);
        column.setWidth(visible[IDX_NR_CAPITOL] ? dims[IDX_NR_CAPITOL] : 0);
        column.setAlignment(aligns[IDX_NR_CAPITOL]);

        column = new TableColumn(table, SWT.LEFT);
        column.setText("Titlu");
        column.setResizable(true);
        column.setWidth(visible[IDX_TITLU] ? dims[IDX_TITLU] : 0);
        column.setAlignment(aligns[IDX_TITLU]);

        column = new TableColumn(table, SWT.LEFT);
        column.setText("Nr pagină");
        column.setResizable(true);
        column.setWidth(visible[IDX_NR_PAGINA] ? dims[IDX_NR_PAGINA] : 0);
        column.setAlignment(aligns[IDX_NR_PAGINA]);

        column = new TableColumn(table, SWT.LEFT);
        column.setText("Motto");
        column.setResizable(true);
        column.setWidth(visible[IDX_MOTTO] ? dims[IDX_MOTTO] : 0);
        column.setAlignment(aligns[IDX_MOTTO]);

        SWTeXtension.addColoredFocusListener(table, ColorUtil.COLOR_FOCUS_YELLOW);
        WidgetTableUtil.customizeTable(this.table, getClass(), TABLE_KEY);
    }

    @Override
    public boolean add() {
        CapitolView capitolView = new CapitolView(getShell(), new Capitol(), table, AbstractView.MODE_ADD);
        capitolView.open();
        if (capitolView.getUserAction() == SWT.OK) {
            Capitol capitol = capitolView.getCapitol();
            showItem(null, capitol);

            carte.getCapitole().add(capitol);
            ApplicationService.getBookController().save(carte);
            SWTeXtension.displayMessageI("Capitolul a fost salvat cu succes!");
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
        Capitol capitol = (Capitol) ObjectUtil.copy(item.getData());
        CapitolView capitolView = new CapitolView(getShell(), capitol, table, AbstractView.MODE_MODIFY);
        capitolView.open();
        if (capitolView.getUserAction() == SWT.OK) {
            carte.getCapitole().remove((Capitol)table.getSelection()[0].getData());
            capitol = capitolView.getCapitol();
            showItem(table.getSelection()[0], capitol);
            carte.getCapitole().add(capitol);
            ApplicationService.getBookController().save(carte);
            SWTeXtension.displayMessageI("Capitolul a fost salvat cu succes!");
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
        if (SWTeXtension.displayMessageQ("Sunteti siguri ca doriti sa stergeti " + (selectionCount > 1 ? "capitolele selectate?" : "capitolul selectat?")) == SWT.NO) {
            return false;
        }
        for (TableItem item : table.getSelection()) {
            Capitol capitol = (Capitol) item.getData();
            carte.getCapitole().remove(capitol);
            item.dispose();
        }
        ApplicationService.getBookController().save(carte);
        SWTeXtension.displayMessageI("Am sters " + selectionCount + (selectionCount > 1 ? " capitole." : " capitol."));
        enableOps();
        return true;
    }

    private void populateFields() {
        if (carte == null) {
            table.clearAll();
            enableOps();
            return;
        }
        for (Capitol capitol : carte.getCapitole()) {
            showItem(null, capitol);
        }
    }

    private void showItem(TableItem item, Capitol capitol) {
        if (table.isDisposed()) {
            return;
        }
        if (item == null || item.isDisposed()) {
            item = new TableItem(table, SWT.NONE);
        }
        item.setText(IDX_NR_CAPITOL, capitol.getNr());
        item.setText(IDX_NR_PAGINA, capitol.getPagina());
        item.setText(IDX_TITLU, capitol.getTitlu());
        item.setText(IDX_MOTTO, capitol.getMotto());
        item.setData(capitol);
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
