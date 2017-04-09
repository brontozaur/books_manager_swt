package com.papao.books.view.custom;

import com.papao.books.view.providers.ContentProposalProvider;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.RowDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import java.util.ArrayList;
import java.util.List;

public class LinkedinComposite extends Composite {

    private Text textSearch;
    private List<String> valoriIntroduse = new ArrayList<>();
    private List<String> valoriInitiale;

    public LinkedinComposite(Composite parent, final List<String> proposals, List<String> valoriInitiale) {
        super(parent, SWT.NONE);

        this.valoriInitiale = valoriInitiale;
        this.setLayout(new RowLayout(SWT.HORIZONTAL));
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).hint(350, SWT.DEFAULT).applyTo(this);

        textSearch = new Text(this, SWT.SEARCH);
        textSearch.setMessage("Cautare/adaugare. Validare cu Enter.");
        RowDataFactory.swtDefaults().hint(300, SWT.DEFAULT).applyTo(textSearch);
        ContentProposalProvider.addContentProposal(textSearch, proposals);
        textSearch.addListener(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event event) {
                final String widgetText = textSearch.getText();
                if (StringUtils.isNotEmpty(widgetText) && event.keyCode == SWT.CR) {
                    if (!valoriIntroduse.contains(widgetText)) {
                        final ClosableCanvas canvas = new ClosableCanvas(LinkedinComposite.this, widgetText);
                        valoriIntroduse.add(widgetText);
                        textSearch.setText("");
                        canvas.moveAbove(textSearch);
                        canvas.addListener(SWT.Dispose, new Listener() {
                            @Override
                            public void handleEvent(Event event) {
                                getShell().setSize(getShell().getBounds().width, getShell().getBounds().height - canvas.getBounds().height);
                                LinkedinComposite.this.layout();
                                LinkedinComposite.this.getParent().layout();
                                if (!textSearch.isDisposed()) {
                                    textSearch.setText("");
                                    valoriIntroduse.remove(valoriIntroduse.indexOf(((ClosableCanvas) event.widget).getText()));
                                }
                            }
                        });
                        getShell().setSize(getShell().getBounds().width, getShell().getBounds().height + canvas.getBounds().height);
                        LinkedinComposite.this.layout();
                        LinkedinComposite.this.getParent().layout();
                    } else {
                        textSearch.setText("");
                    }
                }
            }
        });

        populateFields();
    }

    private void populateFields() {
        int additionalShellHeight = 0;
        for (String valoareInitiala : valoriInitiale) {
            final ClosableCanvas canvas = new ClosableCanvas(LinkedinComposite.this, valoareInitiala);
            valoriIntroduse.add(valoareInitiala);
            textSearch.setText("");
            canvas.moveAbove(textSearch);
            additionalShellHeight = canvas.getBounds().height;
            canvas.addListener(SWT.Dispose, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    getShell().setSize(getShell().getBounds().width, getShell().getBounds().height - canvas.getBounds().height);
                    LinkedinComposite.this.layout();
                    LinkedinComposite.this.getParent().layout();
                    if (!textSearch.isDisposed()) {
                        textSearch.setText("");
                        valoriIntroduse.remove(valoriIntroduse.indexOf(((ClosableCanvas) event.widget).getText()));
                    }
                }
            });
        }
        getShell().setSize(getShell().getBounds().width, getShell().getBounds().height + additionalShellHeight);
        LinkedinComposite.this.layout();
        LinkedinComposite.this.getParent().layout();
    }

    public Text getTextSearch() {
        return this.textSearch;
    }

    public List<String> getValoriIntroduse() {
        return valoriIntroduse;
    }
}
