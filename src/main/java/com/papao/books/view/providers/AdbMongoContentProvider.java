package com.papao.books.view.providers;

import com.papao.books.model.AbstractMongoDB;
import org.eclipse.jface.viewers.*;

import java.util.Collection;
import java.util.Map;

public class AdbMongoContentProvider implements IStructuredContentProvider {
    /**
     * Array-ul de elemente de tip Class<? extends AbstractDB> care va fi folosit de {@link ColumnViewer} pentru afisare in tabela asociata.
     */
    private AbstractMongoDB[] elements;

    @Override
    @SuppressWarnings("unchecked")
    public AbstractMongoDB[] getElements(final Object inputElement) {
        if (inputElement instanceof Collection) {
            this.elements = ((Collection<AbstractMongoDB>) inputElement).toArray(new AbstractMongoDB[((Collection<AbstractMongoDB>) inputElement).size()]);
            return this.elements;
        } else if (inputElement instanceof AbstractMongoDB[]) {
            this.elements = (AbstractMongoDB[]) inputElement;
            return this.elements;
        } else if (inputElement instanceof Map<?, ?>) {
            return getElements(((Map<?, ?>) inputElement).values());
        } else {
            this.elements = new AbstractMongoDB[] {
                (AbstractMongoDB) inputElement };
            return this.elements;
        }
    }

    public AbstractMongoDB[] getElements() {
        return this.elements;
    }

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        this.elements = getElements(newInput);
    }

    @Override
    public void dispose() {
    }

    /**
     * acum vom verifica daca exista unul sau mai multe filtre setate pe viewer-ul curent, si daca
     * exista, vom retine doar elementele care corespund conditiilor de filtrare, pentru a exporta
     * doar ceea ce este vizibil in tabela. Se va observa mai jos ca acelasi lucru se face pentru
     * sortarea elementelor, dar acolo procedeul este infinit mai simplu. Chestia din paragraful
     * urmator a fost sugerata de metoda {@link StructuredViewer#getFilteredChildren}.
     * <p>
     * Ideea a fost sa vad cum se face intern sortarea, pentru a apela aici codul relevant, care sa-mi filtreze elementele ce vor fi exportate.
     * </p>
     * 
     * @param oldInput
     *            indiferent de implementare, valoarea referita trebuie sa fie {@link AdbMongoContentProvider#elements}
     * @param viewer
     *            viewerul asociat tabelei de pe care se face exportul.
     */
    public static AbstractMongoDB[] getFilteredInput(final AbstractMongoDB[] oldInput, final ColumnViewer viewer) {
        if ((oldInput == null) || (viewer == null) || viewer.getControl().isDisposed() || (viewer.getFilters() == null)
                || (viewer.getFilters().length == 0)) {
            return oldInput;
        }
        AbstractMongoDB[] filteredInput = oldInput;
        for (ViewerFilter f : viewer.getFilters()) {
            filteredInput = (AbstractMongoDB[])f.filter(viewer, viewer.getInput(), filteredInput);
        }
        AbstractMongoDB[] result = new AbstractMongoDB[filteredInput.length];
        System.arraycopy(filteredInput, 0, result, 0, filteredInput.length);
        return result;
    }

}
