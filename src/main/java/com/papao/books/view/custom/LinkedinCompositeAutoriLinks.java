package com.papao.books.view.custom;

import com.papao.books.ApplicationService;
import com.papao.books.model.Autor;
import com.papao.books.view.carte.AutorView;
import com.papao.books.view.view.AbstractView;
import org.bson.types.ObjectId;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

import java.util.Collections;
import java.util.List;

public class LinkedinCompositeAutoriLinks extends Composite {

    private List<Autor> autori = Collections.emptyList();

    public LinkedinCompositeAutoriLinks(Composite parent, final List<ObjectId> autori) {
        super(parent, SWT.NONE);

        if (autori != null) {
            this.autori = ApplicationService.getAutorController().findByIds(autori);
        }

        GridDataFactory.fillDefaults().grab(false, false).hint(parent.getSize().x, SWT.DEFAULT).applyTo(this);
        RowLayoutFactory.fillDefaults().extendedMargins(3, 5, 2, 3).spacing(1).margins(0, 0).pack(true).wrap(true).applyTo(this);

        populateFields();
    }

    private void populateFields() {
        for (int i = 0; i < autori.size(); i++) {
            createLink(autori.get(i), i == autori.size() - 1);
        }
        if (autori.size() > 0) {
            layoutEverything();
        }
    }


    private void layoutEverything() {
        this.setSize(this.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        this.getParent().layout();
    }

    private void createLink(Autor autor, boolean last) {
        final Link link = new Link(this, SWT.NONE);
        link.setText("<a>" + autor.getNumeComplet() + (last ? "</a>" : "</a>, "));
        link.setData(autor);
        link.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                Autor autor = (Autor) event.widget.getData();
                autor = ApplicationService.getAutorController().findOne(autor.getId()); //reload from db
                new AutorView(getShell(), autor, AbstractView.MODE_MODIFY).open();
            }
        });
    }

    public void setAutori(List<ObjectId> ids) {
        this.autori.clear();
        if (ids != null && !ids.isEmpty()) {
            this.autori = ApplicationService.getAutorController().findByIds(ids);
        }
        for (Control control : getChildren()) {
            control.dispose();
        }
        populateFields();
    }

    public String getGoogleSearchTerm() {
        StringBuilder searchTerm = new StringBuilder();
        for (Autor autor : autori) {
            if (searchTerm.length() > 0) {
                searchTerm.append(",");
            }
            searchTerm.append(autor.getNumeComplet());
        }
        return searchTerm.toString();
    }
}