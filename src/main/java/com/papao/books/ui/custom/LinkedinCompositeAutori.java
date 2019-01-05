package com.papao.books.ui.custom;

import com.papao.books.controller.AutorController;
import com.papao.books.model.AbstractMongoDB;
import com.papao.books.model.Autor;
import com.papao.books.ui.carte.AutorView;
import com.papao.books.ui.util.ColorUtil;
import com.papao.books.ui.view.AbstractView;
import org.bson.types.ObjectId;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
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

    public LinkedinCompositeAutori(Composite parent, final List<ObjectId> autori) {
        super(parent, SWT.BORDER);

        if (autori != null) {
            this.autori = AutorController.findByIdsOrderByNumeComplet(autori);
        }

        this.setBackground(ColorUtil.COLOR_WHITE);

        GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 1, 0).spacing(2, 0).numColumns(1).equalWidth(false).applyTo(this);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(this);

        ComboImage.CIDescriptor descriptor = new ComboImage.CIDescriptor();
        descriptor.setAddContentProposal(true);
        descriptor.setClazz(Autor.class);
        descriptor.setTextMethodName("getNumeComplet");
        descriptor.setToolItemStyle(ComboImage.ADD_ADD | ComboImage.ADD_MOD);
        descriptor.setInput(AutorController.findAll());

        comboAutor = new ComboImage(this, descriptor);
        comboAutor.setBackground(ColorUtil.COLOR_WHITE);
        comboAutor.getItemAdd().getParent().setBackground(ColorUtil.COLOR_WHITE);
        GridDataFactory.fillDefaults().grab(false, false).hint(150, SWT.DEFAULT).applyTo(comboAutor);
        GridDataFactory.fillDefaults().grab(false, false).hint(120, SWT.DEFAULT).applyTo(comboAutor.getCombo());
        ((GridLayout) comboAutor.getLayout()).marginBottom = 0;
        ((GridLayout) comboAutor.getLayout()).marginTop = 2;
        ((GridLayout) comboAutor.getLayout()).marginLeft = 3;
        comboAutor.getItemAdd().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                Autor autor = new Autor();
                autor.setNumeComplet(comboAutor.getText());
                AutorView view = new AutorView(getShell(), autor, AbstractView.MODE_ADD);
                view.open();
                if (view.getUserAction() == SWT.CANCEL) {
                    return;
                }
                Autor newAutor = view.getAutor();
                comboAutor.setInput(AutorController.findAll());
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
                AutorView view = new AutorView(getShell(), autor, AbstractView.MODE_MODIFY);
                view.open();
                if (view.getUserAction() == SWT.CANCEL) {
                    return;
                }
                comboAutor.setInput(AutorController.findAll());
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
        RowLayoutFactory.fillDefaults().extendedMargins(3, 5, 2, 3).spacing(1).pack(true).wrap(true).applyTo(compSelections);

        populateFields();
    }

    private void populateFields() {
        for (Autor autor : autori) {
            createClosableCanvas(autor, false);
        }
        if (autori.size() > 0) {
            layoutEverything(false);
        }
    }

    private void createOrModifyClosableCanvas(Autor autor) {
        Control[] controls = compSelections.getChildren();
        for (Control control : controls) {
            if (control instanceof ClosableCanvas) {
                ClosableCanvas canvas = (ClosableCanvas) control;
                AbstractMongoDB dataObject = canvas.getDataObject();
                if (dataObject.getId().equals(autor.getId())) {
                    canvas.setText(autor.getNumeSiTitlu());
                    canvas.setDataObject(autor);
                    layoutEverything(true);
                    return;
                }
            }
        }
        createClosableCanvas(autor, true);
    }

    private void layoutEverything(boolean computeShell) {
        compSelections.setSize(compSelections.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        LinkedinCompositeAutori.this.setSize(compSelections.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        LinkedinCompositeAutori.this.getParent().layout();
        if (computeShell) {
            getShell().setSize(getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT));
        }
    }

    private void createClosableCanvas(Autor autor, boolean layoutParent) {
        if (autor != null && (!autori.contains(autor) || !layoutParent)) {
            final ClosableCanvas canvas = new ClosableCanvas(compSelections, autor.getNumeSiTitlu());
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
                        comboAutor.setInput(AutorController.findAll());
                    }
                    layoutEverything(true);
                }
            });
            if (layoutParent) {
                layoutEverything(true);
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
                searchTerm.append(",");
            }
            searchTerm.append(autor.getNumeSiTitlu());
        }
        return searchTerm.toString();
    }

    public void setAutori(List<ObjectId> ids) {
        this.autori.clear();
        if (ids != null && !ids.isEmpty()) {
            this.autori = AutorController.findByIdsOrderByNumeComplet(ids);
        }
        populateFields();
    }

    @Override
    public boolean setFocus() {
        comboAutor.getCombo().setFocus();
        return super.setFocus();
    }

    public ComboImage getComboAutor() {
        return comboAutor;
    }
}