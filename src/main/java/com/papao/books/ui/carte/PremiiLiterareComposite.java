package com.papao.books.ui.carte;

import com.papao.books.model.PremiuLiterar;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.view.AbstractView;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;
import java.util.List;

public class PremiiLiterareComposite extends Composite {

    private Table table;
    private List<PremiuLiterar> input;
    private List<PremiuLiterar> result = new ArrayList<>();
    private ToolItem itemDel;
    private ToolItem itemMod;

    public PremiiLiterareComposite(Composite parent, List<PremiuLiterar> input) {
        super(parent, SWT.NONE);
        this.input = input;

        GridLayoutFactory.fillDefaults().numColumns(1).margins(0, 0).applyTo(this);
        GridDataFactory.fillDefaults().grab(false, false).hint(200, 150).minSize(200, 150).applyTo(this);

        addComponents();
        populateFields();
    }

    private void addComponents() {
        ToolBar bar = new ToolBar(this, SWT.FLAT | SWT.NO_FOCUS);
        ToolItem itemAdd = new ToolItem(bar, SWT.NONE);
        itemAdd.setImage(AppImages.getImage16(AppImages.IMG_PLUS));
        itemAdd.setHotImage(AppImages.getImage16Focus(AppImages.IMG_PLUS));
        itemAdd.setToolTipText("Adăugare");
        itemAdd.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                PremiuLiterarView view = new PremiuLiterarView(getShell(), new PremiuLiterar(), AbstractView.MODE_ADD);
                view.open();
                if (view.getUserAction() == SWT.CANCEL) {
                    return;
                }
                TableItem item = new TableItem(table, SWT.NONE);
                item.setText(new String[]{view.getPremiuLiterar().getAn(), view.getPremiuLiterar().getPremiu()});
                item.setData(view.getPremiuLiterar());
                result.add(view.getPremiuLiterar());
            }
        });

        itemMod = new ToolItem(bar, SWT.NONE);
        itemMod.setImage(AppImages.getImage16(AppImages.IMG_MODIFICARE));
        itemMod.setHotImage(AppImages.getImage16Focus(AppImages.IMG_MODIFICARE));
        itemMod.setToolTipText("Modificare");
        itemMod.setEnabled(false);
        itemMod.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                modify();
            }
        });

        itemDel = new ToolItem(bar, SWT.NONE);
        itemDel.setImage(AppImages.getImage16(AppImages.IMG_CANCEL));
        itemDel.setHotImage(AppImages.getImage16Focus(AppImages.IMG_CANCEL));
        itemDel.setToolTipText("Ștergere");
        itemDel.setEnabled(false);
        itemDel.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (table.getSelectionCount() == 0) {
                    return;
                }
                final TableItem selected = table.getSelection()[0];
                result.remove((PremiuLiterar) selected.getData());
                selected.dispose();
            }
        });

        table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(table);
        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                itemDel.setEnabled(table.getSelectionCount() > 0);
                itemMod.setEnabled(table.getSelectionCount() > 0);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                super.widgetDefaultSelected(e);
                itemDel.setEnabled(table.getSelectionCount() > 0);
                itemMod.setEnabled(table.getSelectionCount() > 0);
                modify();
            }
        });

        TableColumn column = new TableColumn(table, SWT.LEFT);
        column.setText("An");
        column.setResizable(true);
        column.setWidth(50);

        column = new TableColumn(table, SWT.LEFT);
        column.setText("Premiu");
        column.setResizable(true);
        column.setWidth(200);
    }

    private void modify() {
        if (table.getSelectionCount() == 0) {
            return;
        }
        final TableItem item = table.getSelection()[0];
        PremiuLiterar premiu = (PremiuLiterar) item.getData();
        PremiuLiterarView view = new PremiuLiterarView(getShell(), premiu, AbstractView.MODE_ADD);
        view.open();
        if (view.getUserAction() == SWT.CANCEL) {
            return;
        }
        PremiuLiterar modifiedPremiu = view.getPremiuLiterar();
        item.setText(new String[]{modifiedPremiu.getAn(), modifiedPremiu.getPremiu()});
        item.setData(modifiedPremiu);
        result.remove(premiu);
        result.add(modifiedPremiu);
    }

    private void populateFields() {
        for (PremiuLiterar premiu : input) {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(new String[]{premiu.getAn(), premiu.getPremiu()});
            item.setData(premiu);
            result.add(premiu);
        }
    }

    public List<PremiuLiterar> getResult() {
        return result;
    }

    public Table getTable() {
        return this.table;
    }

}
