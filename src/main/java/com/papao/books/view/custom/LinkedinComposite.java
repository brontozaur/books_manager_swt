package com.papao.books.view.custom;

import com.papao.books.model.Autor;
import com.papao.books.view.providers.ContentProposalProvider;
import org.apache.commons.lang3.ArrayUtils;
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
    private String[] proposals;
    private List<String> numeAutori = new ArrayList<>();
    private List<Autor> autoriList;
    private String[] bookAutors;

    public LinkedinComposite(Composite parent, final String[] proposals, List<Autor> autoriList, String[] bookAutors) {
        super(parent, SWT.NONE);

        this.proposals = proposals;
        this.autoriList = autoriList;
        this.bookAutors = bookAutors;
        this.setLayout(new RowLayout(SWT.HORIZONTAL));
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).hint(350, SWT.DEFAULT).applyTo(this);

        textSearch = new Text(this, SWT.BORDER);
        RowDataFactory.swtDefaults().hint(300, SWT.DEFAULT).applyTo(textSearch);
        ContentProposalProvider.addContentProposal(textSearch, proposals, false);
        textSearch.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                final String widgetText = textSearch.getText();
                if (ArrayUtils.contains(LinkedinComposite.this.proposals, widgetText)) {
                    if (!numeAutori.contains(widgetText)) {
                        final ClosableCanvas canvas = new ClosableCanvas(LinkedinComposite.this, widgetText);
                        numeAutori.add(widgetText);
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
                                    numeAutori.remove(numeAutori.indexOf(((ClosableCanvas) event.widget).getText()));
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
        for (String autor: bookAutors) {
            final ClosableCanvas canvas = new ClosableCanvas(LinkedinComposite.this, autor);
            numeAutori.add(autor);
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
                        numeAutori.remove(numeAutori.indexOf(((ClosableCanvas) event.widget).getText()));
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

    public List<String> getIdAutori() {
        List<String> selectedAutori = new ArrayList<>();
        for (String numeAutor : numeAutori) {
            for (Autor autor : autoriList) {
                if (numeAutor.equals(autor.getNume())) {
                    selectedAutori.add(autor.getId());
                }
            }
        }
        return selectedAutori;
    }
}
