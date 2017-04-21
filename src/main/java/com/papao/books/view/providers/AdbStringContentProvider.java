package com.papao.books.view.providers;

import com.papao.books.model.AbstractDB;
import org.eclipse.jface.viewers.*;

import java.util.Collection;
import java.util.Map;

public class AdbStringContentProvider implements IStructuredContentProvider {
    /**
     * Array-ul de elemente de tip Class<? extends AbstractDB> care va fi folosit de {@link ColumnViewer} pentru afisare in tabela asociata.
     */
    private AbstractDB[] elements;

    @Override
    @SuppressWarnings("unchecked")
    public AbstractDB[] getElements(final Object inputElement) {
        if (inputElement instanceof Collection) {
            this.elements = ((Collection<AbstractDB>) inputElement).toArray(new AbstractDB[((Collection<AbstractDB>) inputElement).size()]);
            return this.elements;
        } else if (inputElement instanceof AbstractDB[]) {
            this.elements = (AbstractDB[]) inputElement;
            return this.elements;
        } else if (inputElement instanceof Map<?, ?>) {
            return getElements(((Map<?, ?>) inputElement).values());
        } else {
            this.elements = new AbstractDB[] {
                (AbstractDB) inputElement };
            return this.elements;
        }
    }

    public AbstractDB[] getElements() {
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
     *            indiferent de implementare, valoarea referita trebuie sa fie {@link AdbStringContentProvider#elements}
     * @param viewer
     *            viewerul asociat tabelei de pe care se face exportul.
     */
    public static AbstractDB[] getFilteredInput(final AbstractDB[] oldInput, final ColumnViewer viewer) {
        if ((oldInput == null) || (viewer == null) || viewer.getControl().isDisposed() || (viewer.getFilters() == null)
                || (viewer.getFilters().length == 0)) {
            return oldInput;
        }
        AbstractDB[] filteredInput = oldInput;
        for (ViewerFilter f : viewer.getFilters()) {
            filteredInput = (AbstractDB[])f.filter(viewer, viewer.getInput(), filteredInput);
        }
        AbstractDB[] result = new AbstractDB[filteredInput.length];
        System.arraycopy(filteredInput, 0, result, 0, filteredInput.length);
        return result;
    }

}
