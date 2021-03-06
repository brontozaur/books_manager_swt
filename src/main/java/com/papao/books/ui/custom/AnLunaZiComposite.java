package com.papao.books.ui.custom;

import com.papao.books.model.AnLunaZiData;
import com.papao.books.ui.util.BorgDateUtil;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class AnLunaZiComposite extends Composite {

    private Text textAn;
    private Combo comboLuna;
    private Combo comboZi;
    private AnLunaZiData data;

    public AnLunaZiComposite(Composite parent, AnLunaZiData data) {
        super(parent, SWT.NONE);
        this.data = data;

        GridLayoutFactory.fillDefaults().numColumns(data.isShowLabels() ? 5 : 3).margins(0, 0).spacing(5, SWT.DEFAULT).equalWidth(false).applyTo(this);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(this);

        textAn = new Text(this, SWT.BORDER);
        textAn.setMessage("an");
        GridDataFactory.fillDefaults().hint(30, SWT.DEFAULT).applyTo(textAn);
        textAn.addListener(SWT.KeyUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (StringUtils.isNumeric(textAn.getText())) {
                    AnLunaZiComposite.this.data.setAn(Integer.valueOf(textAn.getText()));
                    handleDayChange();
                }
            }
        });

        if (data.isShowLabels()) {
            new Label(this, SWT.NONE).setText("luna");
        }
        comboLuna = new Combo(this, SWT.READ_ONLY);
        GridDataFactory.fillDefaults().hint(50, SWT.DEFAULT).applyTo(textAn);
        comboLuna.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                AnLunaZiComposite.this.data.setLuna(comboLuna.getSelectionIndex() + 1);
                handleDayChange();
            }
        });
        comboLuna.setItems(BorgDateUtil.LUNILE);

        if (data.isShowLabels()) {
            new Label(this, SWT.NONE).setText("zi");
        }
        comboZi = new Combo(this, SWT.BORDER);
        GridDataFactory.fillDefaults().hint(50, SWT.DEFAULT).applyTo(comboZi);
        comboZi.addListener(SWT.KeyUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (StringUtils.isNumeric(comboZi.getText())) {
                    int zi = Integer.valueOf(comboZi.getText());
                    if (zi > 0 && zi <= BorgDateUtil.getMaxZileInLuna(AnLunaZiComposite.this.data.getLuna(), AnLunaZiComposite.this.data.getAn())) {
                        AnLunaZiComposite.this.data.setZi(zi);
                    } else {
                        comboZi.setText("");
                    }
                } else {
                    comboZi.setText("");
                }
            }
        });

        populateFields();
    }

    private void handleDayChange() {
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

    private void populateFields() {
        if (data.getAn() > 0) {
            this.textAn.setText(String.valueOf(data.getAn()));
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
