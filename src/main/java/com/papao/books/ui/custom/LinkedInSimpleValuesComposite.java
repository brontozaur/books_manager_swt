package com.papao.books.ui.custom;

import com.papao.books.ui.events.CanvasEventDto;
import com.papao.books.ui.searcheable.BookSearchType;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class LinkedInSimpleValuesComposite extends Observable {

    private final List<String> values = new ArrayList<>();
    private final BookSearchType searchType;
    private Composite mainComposite;
    private CanvasEventDto canvasEventDto = null;

    public LinkedInSimpleValuesComposite(Composite parent, @NonNull BookSearchType searchType) {

        this.searchType = searchType;

        this.mainComposite = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).hint(100, SWT.DEFAULT).applyTo(mainComposite);
        RowLayoutFactory.fillDefaults().extendedMargins(3, 5, 2, 2).spacing(1).margins(0, 0).pack(true).wrap(true).applyTo(mainComposite);
    }

    private void populateFields() {
        for (String valoareInitiala : values) {
            SimpleCanvas canvas = new SimpleCanvas(mainComposite, valoareInitiala);
            canvas.getLink().addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    canvasEventDto = new CanvasEventDto(searchType, canvas.getText());
                    LinkedInSimpleValuesComposite.this.setChanged();
                    LinkedInSimpleValuesComposite.this.notifyObservers();
                }
            });
        }
        if (values.size() > 0) {
            layoutEverything();
        }
    }

    public CanvasEventDto getCanvasEventDto() {
        return canvasEventDto;
    }

    private void layoutEverything() {
        mainComposite.setSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        mainComposite.layout();
        mainComposite.getParent().layout();
    }

    public void setValues(List values) {
        this.values.clear();
        for (Control children : mainComposite.getChildren()) {
            children.dispose();
        }
        if (values != null) {
            for (Object value : values) {
                this.values.add(String.valueOf(value));
            }
        }
        populateFields();
    }

}