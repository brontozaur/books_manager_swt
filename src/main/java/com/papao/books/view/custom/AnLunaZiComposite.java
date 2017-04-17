package com.papao.books.view.custom;

import com.papao.books.model.AnLunaZiData;
import com.papao.books.view.util.BorgDateUtil;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class AnLunaZiComposite extends Composite {

    private Combo comboAn;
    private Combo comboLuna;
    private Combo comboZi;
    private AnLunaZiData data;

    public AnLunaZiComposite(Composite parent, AnLunaZiData data) {
        super(parent, SWT.NONE);
        this.data = data;

        GridLayoutFactory.fillDefaults().numColumns(data.isShowLabels() ? 6 : 3).margins(0,0).spacing(2, SWT.DEFAULT).equalWidth(false).applyTo(this);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(this);

        if (data.isShowLabels()) {
            new Label(this, SWT.NONE).setText("an");
        }
        comboAn = new Combo(this, SWT.BORDER);
        GridDataFactory.fillDefaults().hint(75, SWT.DEFAULT).applyTo(comboAn);
        comboAn.addListener(SWT.KeyUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (StringUtils.isNumeric(comboAn.getText())) {
                    AnLunaZiComposite.this.data.setAn(Integer.valueOf(comboAn.getText()));
                }
            }
        });
        if (data.isShowLabels()) {
            new Label(this, SWT.NONE).setText("luna");
        }

        comboLuna = new Combo(this, SWT.READ_ONLY);
        comboLuna.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                AnLunaZiComposite.this.data.setLuna(comboLuna.getSelectionIndex() + 1);

                int dayIndex = comboZi.indexOf(String.valueOf(AnLunaZiComposite.this.data.getZi()));
                comboZi.removeAll();
                final int maxDays = BorgDateUtil.getMaxZileInLuna(comboLuna.getSelectionIndex(), AnLunaZiComposite.this.data.getAn());
                for (int i = 0; i < maxDays; i++) {
                    comboZi.add(String.valueOf(i + 1));
                }
                if (dayIndex != -1) {
                    comboZi.select(dayIndex);
                } else {
                    AnLunaZiComposite.this.data.setZi(0);
                }
            }
        });
        comboLuna.setItems(BorgDateUtil.LUNILE);
        if (data.isShowLabels()) {
            new Label(this, SWT.NONE).setText("zi");
        }
        comboZi = new Combo(this, SWT.READ_ONLY);
        GridDataFactory.fillDefaults().hint(75, SWT.DEFAULT).applyTo(comboZi);
        comboZi.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                AnLunaZiComposite.this.data.setZi(comboZi.getSelectionIndex() + 1);
            }
        });

        populateFields();
    }

    private void populateFields() {
        if (data.getAn() > 0) {
            this.comboAn.setText(String.valueOf(data.getAn()));
        }
        if (data.getLuna() > 0) {
            this.comboLuna.select(data.getLuna() - 1);
        }
        if (data.getLuna() > 0) {
            final int maxDays = BorgDateUtil.getMaxZileInLuna(comboLuna.getSelectionIndex(), data.getAn());
            for (int i = 0; i < maxDays; i++) {
                comboZi.add(String.valueOf(i + 1));
            }

            if (data.getZi() > 0) {
                this.comboZi.select(comboZi.indexOf(String.valueOf(data.getZi())));
            }
        }
    }

    public AnLunaZiData getValues() {
        return data;
    }

}
