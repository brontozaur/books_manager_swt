package com.papao.books.view.custom;

import com.papao.books.model.Carte;
import com.papao.books.view.menu.WebBrowser;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class LinkedInUrlsComposite extends Composite {

    private Carte carte;

    public LinkedInUrlsComposite(Composite parent, final Carte carte) {
        super(parent, SWT.NONE);

        this.carte = carte;

        GridDataFactory.fillDefaults().grab(false, false).hint(parent.getSize().x, SWT.DEFAULT).applyTo(this);
        RowLayoutFactory.fillDefaults().extendedMargins(3, 5, 2, 3).spacing(1).margins(0,0).pack(true).wrap(true).applyTo(this);

        populateFields();
    }

    private void populateFields() {
        boolean layout = false;
        if (carte == null) {
            return;
        }
        if (StringUtils.isNotEmpty(carte.getGoodreadsUrl())) {
            createLink(carte.getGoodreadsUrl(), "goodreads.com");
            layout = true;
        }
        if (StringUtils.isNotEmpty(carte.getWebsite())) {
            createLink(carte.getWebsite(), "pagina oficiala");
            layout = true;
        }
        if (StringUtils.isNotEmpty(carte.getWikiUrl())) {
            createLink(carte.getWikiUrl(), "wikipedia.com");
            layout = true;
        }
        if (layout) {
            layoutEverything();
        }
    }

    private void layoutEverything() {
        LinkedInUrlsComposite.this.setSize(this.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        LinkedInUrlsComposite.this.getParent().layout();
    }

    private void createLink(String url, String uiText) {
        final Link link = new Link(this, SWT.NONE);
        link.setText("<a>" + uiText + " </a>");
        link.setData(url);
        link.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                String url = (String) event.widget.getData();
                new WebBrowser(getShell(), url, false).open(true, false);
            }
        });
    }

    public void setCarte(Carte carte) {
        this.carte = carte;
        for (Control control : getChildren()) {
            control.dispose();
        }
        populateFields();
    }
}