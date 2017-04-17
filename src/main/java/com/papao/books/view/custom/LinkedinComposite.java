package com.papao.books.view.custom;

import com.papao.books.view.providers.ContentProposalProvider;
import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.util.EnumHelper;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;
import java.util.List;

public class LinkedinComposite extends Composite {

    private Text textSearch;
    private List<String> valoriIntroduse = new ArrayList<>();
    private List<String> valoriInitiale = new ArrayList<>();
    private Composite compSelections;
    private List<String> proposals = new ArrayList<>();
    private Class<? extends Enum> enumClass;

    public LinkedinComposite(Composite parent, Class<? extends Enum> enumClass, List valoriInitiale) {
        super(parent, SWT.BORDER);
        this.enumClass = enumClass;
        this.proposals = EnumHelper.getValuesArray(enumClass);
        for (Object enumValue : valoriInitiale) {
            this.valoriInitiale.add(((Enum) enumValue).name());
        }

        addComponents();
        populateFields();
    }

    public LinkedinComposite(Composite parent, final List<String> proposals, List<String> valoriInitiale) {
        super(parent, SWT.BORDER);
        this.proposals = proposals;
        this.valoriInitiale = valoriInitiale;

        addComponents();
        populateFields();
    }

    private void addComponents() {
        this.setBackground(ColorUtil.COLOR_WHITE);

        GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 1, 0).spacing(2, 0).numColumns(2).equalWidth(false).applyTo(this);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this);

        textSearch = new Text(this, SWT.SEARCH);
        textSearch.setMessage("Cautare...");
        GridDataFactory.fillDefaults().grab(true, false).indent(5, 0).align(SWT.FILL, SWT.CENTER).applyTo(textSearch);

        Button buttonAdd = new Button(this, SWT.PUSH);
        buttonAdd.setText("Adauga");
        GridDataFactory.fillDefaults().align(SWT.END, SWT.END).applyTo(buttonAdd);
        buttonAdd.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                final String text = textSearch.getText().trim();
                if (validateText(text)) {
                    createClosableCanvas(text, true);
                }
            }
        });

        compSelections = new Composite(this, SWT.NONE);
        compSelections.setBackground(ColorUtil.COLOR_WHITE);
        GridDataFactory.fillDefaults().grab(true, true).hint(230, SWT.DEFAULT).span(2, 1).applyTo(compSelections);
        RowLayoutFactory.fillDefaults().extendedMargins(5, 5, 0, 5).spacing(1).pack(true).wrap(true).applyTo(compSelections);

        ContentProposalProvider.addContentProposal(textSearch, proposals);
        textSearch.addListener(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event event) {
                final String widgetText = textSearch.getText().trim();
                if (event.keyCode == SWT.CR && validateText(widgetText)) {
                    createClosableCanvas(widgetText, true);
                }
            }
        });
    }

    private boolean validateText(String text) {
        if (enumClass == null) {
            return StringUtils.isNotEmpty(text);
        }
        return proposals.contains(text);
    }

    private void populateFields() {
        for (String valoareInitiala : valoriInitiale) {
            createClosableCanvas(valoareInitiala, false);
        }
        if (valoriInitiale.size() > 0) {
            layoutEverything();
        }
    }

    private void layoutEverything() {
        compSelections.setSize(compSelections.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        LinkedinComposite.this.setSize(compSelections.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        LinkedinComposite.this.getParent().layout();
        getShell().setSize(getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    private void createClosableCanvas(String text, boolean layoutParent) {
        if (!valoriIntroduse.contains(text)) {
            final ClosableCanvas canvas = new ClosableCanvas(compSelections, text);
            valoriIntroduse.add(text);
            textSearch.setText("");
            canvas.getItemClose().addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    if (!textSearch.isDisposed()) {
                        textSearch.setText("");
                        valoriIntroduse.remove(valoriIntroduse.indexOf(canvas.getText()));
                    }
                    layoutEverything();
                }
            });
            if (layoutParent) {
                layoutEverything();
            }
        } else {
            textSearch.setText("");
        }
    }

    public Text getTextSearch() {
        return this.textSearch;
    }

    public List<String> getValoriIntroduse() {
        return valoriIntroduse;
    }

    public List<Enum> getEnumValues() {
        List<Enum> selectedEnumValues = new ArrayList<>();
        for (String value : valoriIntroduse) {
            Enum enumValue = EnumHelper.getEnum(enumClass, value);
            if (enumValue == null) {
                throw new EnumConstantNotPresentException(enumClass, "Enumeration " + enumClass + " does not contain enum value " + value);
            }
            selectedEnumValues.add(enumValue);
        }
        return selectedEnumValues;
    }
}