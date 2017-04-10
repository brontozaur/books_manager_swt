package com.papao.books.view.custom;

import com.papao.books.view.providers.ContentProposalProvider;
import com.papao.books.view.util.ColorUtil;
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
    private List<String> valoriInitiale;
    private Composite compSelections;

    public LinkedinComposite(Composite parent, final List<String> proposals, List<String> valoriInitiale) {
        super(parent, SWT.BORDER);
        this.setBackground(ColorUtil.COLOR_WHITE);

        this.valoriInitiale = valoriInitiale;
        GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 1, 0).spacing(SWT.DEFAULT, 0).numColumns(2).equalWidth(false).applyTo(this);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this);

        textSearch = new Text(this, SWT.SEARCH);
        textSearch.setMessage("Cautare/adaugare. Validare cu Enter.");
        GridDataFactory.fillDefaults().grab(true, false).indent(5, 0).align(SWT.FILL, SWT.CENTER).applyTo(textSearch);

        Button buttonAdd = new Button(this, SWT.PUSH);
        buttonAdd.setText("Adauga");
        GridDataFactory.fillDefaults().align(SWT.END, SWT.END).applyTo(buttonAdd);
        buttonAdd.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                final String text = textSearch.getText().trim();
                if (StringUtils.isNotEmpty(text)) {
                    createClosableCanvas(text, true);
                }
            }
        });

        compSelections = new Composite(this, SWT.BORDER);
        compSelections.setBackground(ColorUtil.COLOR_WHITE);
        GridDataFactory.fillDefaults().grab(true, true).hint(350, SWT.DEFAULT).span(2, 1).applyTo(compSelections);
        RowLayoutFactory.fillDefaults().margins(2, 2).spacing(1).pack(true).wrap(true).applyTo(compSelections);

        ContentProposalProvider.addContentProposal(textSearch, proposals);
        textSearch.addListener(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event event) {
                final String widgetText = textSearch.getText().trim();
                if (StringUtils.isNotEmpty(widgetText) && event.keyCode == SWT.CR) {
                    createClosableCanvas(widgetText, true);
                }
            }
        });

        populateFields();
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
            canvas.addListener(SWT.Dispose, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    if (!textSearch.isDisposed()) {
                        textSearch.setText("");
                        valoriIntroduse.remove(valoriIntroduse.indexOf(((ClosableCanvas) event.widget).getText()));
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
}
