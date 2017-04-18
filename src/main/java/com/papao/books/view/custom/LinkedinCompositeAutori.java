package com.papao.books.view.custom;

import com.papao.books.controller.AutorController;
import com.papao.books.model.AbstractMongoDB;
import com.papao.books.model.Autor;
import com.papao.books.view.carte.AutorView;
import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.view.AbstractView;
import org.bson.types.ObjectId;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LinkedinCompositeAutori extends Composite {

    private ComboImage comboAutor;
    private Composite compSelections;
    private List<Autor> autori = Collections.emptyList();
    private final AutorController autorController;

    public LinkedinCompositeAutori(Composite parent, final List<ObjectId> autori, final AutorController autorController) {
        super(parent, SWT.BORDER);
        this.autorController = autorController;

        if (autori != null) {
            this.autori = autorController.findByIds(autori);
        }

        this.setBackground(ColorUtil.COLOR_WHITE);

        GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 1, 0).spacing(2, 0).numColumns(1).equalWidth(false).applyTo(this);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this);

        ComboImage.CIDescriptor descriptor = new ComboImage.CIDescriptor();
        descriptor.setAddContentProposal(true);
        descriptor.setClazz(Autor.class);
        descriptor.setTextMethodName("getNumeComplet");
        descriptor.setToolItemStyle(ComboImage.ADD_ADD | ComboImage.ADD_MOD);
        descriptor.setInput(autorController.findAll());

        comboAutor = new ComboImage(this, descriptor);
        GridDataFactory.fillDefaults().grab(true, false).indent(5, 0).align(SWT.FILL, SWT.CENTER).applyTo(comboAutor);
        comboAutor.getItemAdd().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                AutorView view = new AutorView(getShell(), new Autor(), autorController, AbstractView.MODE_ADD);
                view.open();
                if (view.getUserAction() == SWT.CANCEL) {
                    return;
                }
                Autor newAutor = view.getAutor();
                comboAutor.setInput(autorController.findAll());
                createClosableCanvas(newAutor, true);
            }
        });
        comboAutor.getItemMod().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (!(comboAutor.getSelectedElement() instanceof Autor)) {
                    return;
                }
                Autor autor = (Autor) comboAutor.getSelectedElement();
                AutorView view = new AutorView(getShell(), autor, autorController, AbstractView.MODE_MODIFY);
                view.open();
                if (view.getUserAction() == SWT.CANCEL) {
                    return;
                }
                comboAutor.setInput(autorController.findAll());
                createOrModifyClosableCanvas(autor);
            }
        });
        comboAutor.getCombo().addListener(SWT.KeyUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (event.keyCode == SWT.CR && comboAutor.getSelectedObjectId() != null) {
                    createClosableCanvas((Autor) comboAutor.getSelectedElement(), true);
                }
            }
        });

        compSelections = new Composite(this, SWT.NONE);
        compSelections.setBackground(ColorUtil.COLOR_WHITE);
        GridDataFactory.fillDefaults().grab(true, true).hint(230, SWT.DEFAULT).applyTo(compSelections);
        RowLayoutFactory.fillDefaults().extendedMargins(5, 5, 5, 5).spacing(1).pack(true).wrap(true).applyTo(compSelections);

        populateFields();
    }

    private void populateFields() {
        for (Autor autor : autori) {
            createClosableCanvas(autor, false);
        }
        if (autori.size() > 0) {
            layoutEverything();
        }
    }

    private void createOrModifyClosableCanvas(Autor autor) {
        Control[] controls = compSelections.getChildren();
        for (Control control : controls) {
            if (control instanceof ClosableCanvas) {
                ClosableCanvas canvas = (ClosableCanvas) control;
                AbstractMongoDB dataObject = canvas.getDataObject();
                if (dataObject.getId().equals(autor.getId())) {
                    canvas.setText(autor.getNumeComplet());
                    canvas.setDataObject(autor);
                    layoutEverything();
                    return;
                }
            }
        }
        createClosableCanvas(autor, true);
    }

    private void layoutEverything() {
        compSelections.setSize(compSelections.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        LinkedinCompositeAutori.this.setSize(compSelections.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        LinkedinCompositeAutori.this.getParent().layout();
        getShell().setSize(getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    private void createClosableCanvas(Autor autor, boolean layoutParent) {
        if (autor != null && (!autori.contains(autor) || !layoutParent)) {
            final ClosableCanvas canvas = new ClosableCanvas(compSelections, autor.getNumeComplet());
            canvas.setDataObject(autor);
            if (layoutParent) {
                autori.add(autor);
            }
            comboAutor.getCombo().clearSelection();
            comboAutor.getCombo().setText("");
            canvas.getItemClose().addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    if (!comboAutor.isDisposed()) {
                        autori.remove((Autor) canvas.getDataObject());
                        comboAutor.setInput(autorController.findAll());
                    }
                    layoutEverything();
                }
            });
            if (layoutParent) {
                layoutEverything();
            }
        } else {
            comboAutor.getCombo().clearSelection();
            comboAutor.getCombo().setText("");
        }
    }

    public Composite getCompSelections() {
        return this.compSelections;
    }

    public List<ObjectId> getSelectedIds() {
        List<ObjectId> selectedIds = new ArrayList<>();
        for (Autor autor : autori) {
            selectedIds.add(autor.getId());
        }
        return selectedIds;
    }

    public String getGoogleSearchTerm() {
        StringBuilder searchTerm = new StringBuilder();
        for (Autor autor : autori) {
            if (searchTerm.length() > 0) {
                searchTerm.append("+");
            }
            searchTerm.append(autor.getNumeComplet());
        }
        return searchTerm.toString();
    }
}