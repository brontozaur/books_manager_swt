package com.papao.books.view.custom;

import com.papao.books.view.providers.ContentProposalProvider;
import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.util.EnumHelper;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.RowDataFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;
import java.util.List;

public class LinkedinComposite extends Composite {

    private Text textSearch;
    private List<String> valoriIntroduse = new ArrayList<>();
    private List<String> valoriInitiale = new ArrayList<>();
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

        GridDataFactory.fillDefaults().grab(true, false).hint(230, SWT.DEFAULT).applyTo(this);
        RowLayoutFactory.fillDefaults().extendedMargins(5, 5, 2, 2).spacing(1).pack(true).wrap(true).applyTo(this);

        textSearch = new Text(this, SWT.SEARCH);
        textSearch.setMessage("Cautare...");
        RowDataFactory.swtDefaults().hint(140, SWT.DEFAULT).applyTo(textSearch);

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
            layoutEverything(false);
        }
    }

    private void layoutEverything(boolean computeShell) {
        Control firstChild = this.getChildren()[0];
        if (firstChild != textSearch) {
            textSearch.moveAbove(firstChild);
        }
        LinkedinComposite.this.setSize(computeSize(SWT.DEFAULT, SWT.DEFAULT));
        LinkedinComposite.this.getParent().layout();
        if (computeShell) {
            getShell().setSize(getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT));
        }
    }

    private void createClosableCanvas(String text, boolean layoutParent) {
        if (!valoriIntroduse.contains(text)) {
            final ClosableCanvas canvas = new ClosableCanvas(this, text);
            valoriIntroduse.add(text);
            textSearch.setText("");
            canvas.getItemClose().addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    if (!textSearch.isDisposed()) {
                        textSearch.setText("");
                        valoriIntroduse.remove(valoriIntroduse.indexOf(canvas.getText()));
                    }
                    layoutEverything(true);
                }
            });
            if (layoutParent) {
                layoutEverything(true);
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