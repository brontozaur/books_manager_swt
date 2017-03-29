package com.papao.books.ui.custom;

import com.papao.books.model.AbstractDB;
import com.papao.books.model.BlankDbObject;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.providers.AdbContentProvider;
import com.papao.books.ui.providers.ContentProposalProvider;
import com.papao.books.ui.view.SWTeXtension;
import com.papao.books.util.ColorUtil;
import com.papao.books.util.ObjectUtil;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * @category Custom hand-maded SWT components.
 * @since Encode Borg v.1.0!!!!!
 *        <P>
 *        Description : Custom implementation for issues regarding a combo. It provides the following :
 *        <ol>
 *        <li>A hook with the {@link AbstractDB} object used to represent in combo, by invoking some of the object methods.</li>
 *        <li>Able to return {@link AbstractDB}'s id or entire {@link AbstractDB} object, based on selection</li>
 *        <li>A way of invoking a method on the {@link AbstractDB} to get an image attribute of the object, and display the image in a resize format</li>
 *        <li>Hooked up with {@link ComboViewer} and {@link AdbContentProvider} to provide a mechanism of automatically added combo items</li>
 *        <li>Accepts all kind of inputs (collections of {@link AbstractDB} elements, via {@link AdbContentProvider} : {@link Collection},
 *        {@link AbstractDB}[], {@link Map} )</li>
 *        <li>Supports automatic content proposals via {@link ContentProposalProvider} based on the actual combo items provided by the viewer.
 *        Proposal behaviour may be passed via object's constructor,or, at a later time by calling the {@link #()} method</li>
 *        <li>Selection in combo is enhanced to support a {@link String} or a {@link Long} ( {@link AbstractDB}'s currently selected object id)</li>
 *        <li>Cleans up after himself regarding images displayed in the image label, if the case</li>
 *        <li>Extends {@link Composite}, which makes it easy to call in parents with {@link GridLayout} layout</li>
 *        <li>Implements {@link Listener} interface, to perform tasks, so no external events are usually required to be triggered for a proper
 *        selection.</li>
 *        <li>Provides acces to the {@link Combo} widget, so external callers may fire widget's listeners to perform extra tasks, if required</li>
 *        <li>Lightweight, get the job done with an absolut minimum of system resources</li>
 *        <li>Being a selection widget, the main user action is selection. Following this principle, the {@link SWT#READ_ONLY} flag is passed
 *        automatically to the {@link Combo} widget, if {@linkplain ComboImage#} flag is not set</li>
 *        <li>When hooked up with a {@link ContentProposalProvider}, the {@link ComboImage#lastId} takes care of the repeated and useless fire of the
 *        {@link SWT#Selection} listeners, added to the {@link Combo} widget by callers.</li>
 *        <li>Allows creation of additional 3 toolItems for add, modify or delete operations, by specifying in constructor one or more bitwise params
 *        {@link #ADD_ADD}, {@link #ADD_MOD} and/or {@link #ADD_DEL} in the <code>toolItemStyle</code> parameter. Warning : the action performed by
 *        these toolitems on selection must be specified by the caller.</li>
 *        </ol>
 */
public class ComboImage extends Composite implements Listener {

	private static Logger logger = Logger.getLogger(ComboImage.class);

    private Combo combo;
    private Label imageLabel;
    private ComboViewer viewer;
    private ToolItem itemAdd;
    private ToolItem itemMod;
    private ToolItem itemDel;
    private final CIDescriptor descriptor;

    /**
     * variabila asta retine ultimul obiect selectat, pentru a evita notificari succesive ale unor
     * listenere externe, care, in cel mai rau caz :D ar face nishte selecturi in db. Aceste actiuni
     * ar fi generate automat de listenerul de pe SWT.KeyUp adaugat de un eventual mecanism de
     * autocomplete, cum e cazul lui {@link ContentProposalProvider}. Cum am spus in zeci de
     * randuri, editarea in combo ESTE O MARE TAMPENIE!! dar whatever, e comod pt user.
     */
    private long lastId = 0;

    private static final int SIZE = 19;
    /**
     * if specified, it creates the add toolItem
     */
    public final static int ADD_ADD = 1 << 1;
    /**
     * if specified, it creates the mod toolItem
     */
    public final static int ADD_MOD = 1 << 2;
    /**
     * if specified, it creates the del toolItem
     */
    public final static int ADD_DEL = 1 << 3;

    public ComboImage(final Composite parent, final CIDescriptor descriptor) {
        super(parent, SWT.NONE);
        this.descriptor = descriptor;
        if (this.descriptor.getInput() instanceof String[]) {
            this.descriptor.setInput(ComboImage.getBlankObjectsInput((String[]) this.descriptor.getInput()));
            this.descriptor.setClazz(BlankDbObject.class);
        }
        this.descriptor.setInput(getElements());

        addComponents();
    }

    private void addComponents() {
        final boolean hasImageLabel = this.descriptor.getImageMethodName() != null;
        final boolean hasToolBar = this.descriptor.getToolItemStyle() != SWT.NONE;
        int numCols = 1;
        if (hasImageLabel) {
            numCols++;
        }
        if (hasToolBar) {
            numCols++;
        }
        GridDataFactory.fillDefaults().grab(false, false).align(SWT.BEGINNING, SWT.CENTER).applyTo(this);
        GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(numCols).margins(0, 0).extendedMargins(0, 0, 0, 0).spacing(0, 0).applyTo(this);

        if (hasImageLabel) {
            setImageLabel(new Label(this, SWT.BORDER));
            GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).hint(ComboImage.SIZE, ComboImage.SIZE).grab(false, true).minSize(ComboImage.SIZE,
                    ComboImage.SIZE).applyTo(getImageLabel());
            getImageLabel().addListener(SWT.Dispose, this);
        }

        int comboStyle = SWT.DROP_DOWN;
        if (!this.descriptor.isAddContentProposal()) {
            comboStyle |= SWT.READ_ONLY;
        }
        setCombo(new Combo(this, comboStyle));
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, true).hint(100, SWT.DEFAULT).applyTo(getCombo());

        setViewer(new ComboViewer(getCombo()));
        getViewer().setContentProvider(new AdbContentProvider());
        getViewer().setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(final Object element) {
                Method meth;
                try {
                    meth = ObjectUtil.getMethod(ComboImage.this.descriptor.getClazz(), ComboImage.this.descriptor.getTextMethodName());
                    return (String) meth.invoke(element, (Object[]) null);
                } catch (Exception exc) {
                    logger.error(exc.getMessage(), exc);
                    return "";
                }
            }
        });
        getCombo().addListener(SWT.Selection, this);
        getViewer().setInput(this.descriptor.getInput());
        SWTeXtension.addColoredFocusListener(getCombo(), ColorUtil.COLOR_FOCUS_YELLOW);
        if (this.descriptor.isAddContentProposal()) {
            ContentProposalProvider.addContentProposal(getCombo(), getCombo().getItems(), true);
        }

        if (this.descriptor.getToolItemStyle() == SWT.NONE) {
            return;
        }
        ToolBar bar = new ToolBar(this, SWT.FLAT);
        if ((this.descriptor.getToolItemStyle() & ComboImage.ADD_ADD) == ComboImage.ADD_ADD) {
            this.itemAdd = new ToolItem(bar, SWT.NONE);
            this.itemAdd.setImage(AppImages.getImage16(AppImages.IMG_PLUS));
            this.itemAdd.setHotImage(AppImages.getImage16Focus(AppImages.IMG_PLUS));
            this.itemAdd.setToolTipText("Adaugare");
        }
        if ((this.descriptor.getToolItemStyle() & ComboImage.ADD_MOD) == ComboImage.ADD_MOD) {
            this.itemMod = new ToolItem(bar, SWT.NONE);
            this.itemMod.setImage(AppImages.getImage16(AppImages.IMG_MODIFICARE));
            this.itemMod.setHotImage(AppImages.getImage16Focus(AppImages.IMG_MODIFICARE));
            this.itemMod.setToolTipText("Modificare");
        }
        if ((this.descriptor.getToolItemStyle() & ComboImage.ADD_DEL) == ComboImage.ADD_DEL) {
            this.itemDel = new ToolItem(bar, SWT.NONE);
            this.itemDel.setImage(AppImages.getImage16(AppImages.IMG_CANCEL));
            this.itemDel.setHotImage(AppImages.getImage16Focus(AppImages.IMG_CANCEL));
            this.itemDel.setToolTipText("Stergere");
        }
    }

    public void setImage(final Object element) {
        Image result = AppImages.getImage(AppImages.IMAGE_NOT_FOUND_16X16, ComboImage.SIZE, ComboImage.SIZE);
        try {
            if ((getImageLabel() == null) || getImageLabel().isDisposed()) {
                return;
            }
            if ((getImageLabel().getImage() != null) && !getImageLabel().getImage().isDisposed()
                    && !AppImages.IMAGE_NOT_FOUND_16X16.equals(getImageLabel().getImage())) {
                getImageLabel().getImage().dispose();
            }
            if (element == null) {
                getImageLabel().setImage(result);
                return;
            }
            Method meth = ObjectUtil.getMethod(ComboImage.this.descriptor.getClazz(), ComboImage.this.descriptor.getImageMethodName());
            result = (Image) meth.invoke(element, (Object[]) null);
            if ((result == null) || result.isDisposed()) {
                result = AppImages.getImage(AppImages.IMAGE_NOT_FOUND_16X16, ComboImage.SIZE, ComboImage.SIZE);
                if ((getImageLabel() != null) && !getImageLabel().isDisposed()) {
                    getImageLabel().setImage(result);
                    return;
                }
            }

            result = AppImages.getImage(result, ComboImage.SIZE, ComboImage.SIZE);
            if ((getImageLabel() != null) && !getImageLabel().isDisposed()) {
                getImageLabel().setImage(result);
            }
        } catch (Exception exc) {
            result = AppImages.IMAGE_NOT_FOUND_16X16;
            if ((getImageLabel() != null) && !getImageLabel().isDisposed()) {
                getImageLabel().setImage(result);
            }
            logger.error(exc.getMessage(), exc);
        }
    }

    @Override
    public final void handleEvent(final Event e) {
        if ((e.type == SWT.Selection) && (e.widget == getCombo())) {
            setImage(getSelectedElement());
        }
        if ((e.type == SWT.Dispose) && (e.widget == getImageLabel())) {
            if ((getImageLabel().getImage() != null) && !getImageLabel().getImage().isDisposed()
                    && (!AppImages.IMAGE_NOT_FOUND_16X16.equals(getImageLabel().getImage()))) {
                getImageLabel().getImage().dispose();
            }
        }
    }

    public final AbstractDB getSelectedElement() {
        if (getCombo().getSelectionIndex() == -1) {
            return null;
        }
        return (AbstractDB) getViewer().getElementAt(getCombo().getSelectionIndex());
    }

    public final long getSelectedObjectId() {
        return getSelectedElement() != null ? getSelectedElement().getId() : 0;
    }

    public final void select(final String str) {
        if ((str == null) || this.combo.isDisposed() || (this.combo.indexOf(str) == -1)) {
            return;
        }
        this.combo.select(this.combo.indexOf(str));
        if (getSelectedObjectId() != this.lastId) {
            this.lastId = getSelectedObjectId();
            getCombo().notifyListeners(SWT.Selection, new Event());
        }
    }

    public final void addContentProposals() {
        /**
         * eliminam listenerii adaugati de content proposal-ul vechi.
         */
        Listener[] oldListeners = getCombo().getListeners(SWT.KeyUp);
        if ((oldListeners != null) && (oldListeners.length > 0)) {
            for (Listener lis : oldListeners) {
                getCombo().removeListener(SWT.KeyUp, lis);
            }
        }
        oldListeners = getCombo().getListeners(SWT.MouseDown);
        if ((oldListeners != null) && (oldListeners.length > 0)) {
            for (Listener lis : oldListeners) {
                removeListener(SWT.MouseDown, lis);
            }
        }
        ContentProposalProvider.addContentProposal(getCombo(), getCombo().getItems(), true);
    }

    public final void select(final long id) {
        if (getCombo().getItemCount() <= 0) {
            return;
        }
        for (int i = 0; i < getCombo().getItemCount(); i++) {
            AbstractDB element = (AbstractDB) getViewer().getElementAt(i);
            if (element.getId() == id) {
                getCombo().select(i);
                if (element.getId() != this.lastId) {
                    this.lastId = element.getId();
                    getCombo().notifyListeners(SWT.Selection, new Event());
                }
            }
        }
    }

    private AbstractDB[] getElements() {
        try {
            if ((this.descriptor == null) || (this.descriptor.getInput() == null)) {
                return new AbstractDB[0];
            }
            Object inputElement = this.descriptor.getInput();
            AbstractDB[] elements;
            if (inputElement instanceof Collection) {
                elements = ((Collection<AbstractDB>) inputElement).toArray(new AbstractDB[((Collection<AbstractDB>) inputElement).size()]);
            } else if (inputElement instanceof AbstractDB[]) {
                elements = (AbstractDB[]) inputElement;
            } else if (inputElement instanceof Map<?, ?>) {
                this.descriptor.setInput(((Map<?, ?>) inputElement).values());
                return getElements();
            } else {
                elements = new AbstractDB[] {
                    (AbstractDB) inputElement };
            }
            if (elements == null) {
                elements = new AbstractDB[0];
            }
            if (this.descriptor.isAddEmptyElement()) {
                AbstractDB[] temp = new AbstractDB[elements.length + 1];
                temp[0] = this.descriptor.getClazz().newInstance();
                System.arraycopy(elements, 0, temp, 1, elements.length);
                return temp;
            }
            return elements;
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return new AbstractDB[0];
        }
    }

    public final void setInput(final Object input) {
        this.descriptor.setInput(input);
        if (this.descriptor.getInput() instanceof String[]) {
            this.descriptor.setInput(ComboImage.getBlankObjectsInput((String[]) this.descriptor.getInput()));
            this.descriptor.setClazz(BlankDbObject.class);
        }
        if (input == null) {
            return;
        }
        this.descriptor.setInput(getElements());
        this.viewer.setInput(this.descriptor.getInput());
        if (this.descriptor.isAddContentProposal()) {
            ContentProposalProvider.addContentProposal(getCombo(), getCombo().getItems(), true);
        }
    }

    public final void setItems(String[] items) {
        setInput(items);
    }

    public final String getText() {
        return getCombo().getText();
    }

    public final Combo getCombo() {
        return this.combo;
    }

    private final void setCombo(final Combo combo) {
        this.combo = combo;
    }

    public final Label getImageLabel() {
        return this.imageLabel;
    }

    private final void setImageLabel(final Label imageLabel) {
        this.imageLabel = imageLabel;
    }

    public final ComboViewer getViewer() {
        return this.viewer;
    }

    private final void setViewer(final ComboViewer viewer) {
        this.viewer = viewer;
    }

    /**
     * @return item add or null, if not specified
     */
    public final ToolItem getItemAdd() {
        return this.itemAdd;
    }

    /**
     * @return item mod or null, if not specified
     */
    public final ToolItem getItemMod() {
        return this.itemMod;
    }

    public final int getSelectionIndex() {
        return this.combo.getSelectionIndex();
    }

    /**
     * @return item del or null, if not specified
     */
    public final ToolItem getItemDel() {
        return this.itemDel;
    }

    private final static BlankDbObject[] getBlankObjectsInput(final String[] str) {
        if (str == null) {
            return new BlankDbObject[0];
        }
        BlankDbObject[] bdo = new BlankDbObject[str.length];
        for (int i = 0; i < str.length; i++) {
            bdo[i] = new BlankDbObject(str[i]);
        }
        return bdo;
    }

    public final AbstractDB[] getInput() {
        return (AbstractDB[]) this.descriptor.getInput();
    }

    public static class CIDescriptor {
        private Class<? extends AbstractDB> clazz;
        private String textMethodName;
        private String imageMethodName;
        private Object input;
        private boolean addContentProposal;
        private int toolItemStyle = SWT.NONE;
        private boolean addEmptyElement;

        public final Class<? extends AbstractDB> getClazz() {
            return this.clazz;
        }

        public final void setClazz(final Class<? extends AbstractDB> clazz) {
            this.clazz = clazz;
        }

        public final String getTextMethodName() {
            return this.textMethodName;
        }

        public final void setTextMethodName(final String textMethodName) {
            this.textMethodName = textMethodName;
        }

        public final String getImageMethodName() {
            return this.imageMethodName;
        }

        public final void setImageMethodName(final String imageMethodName) {
            this.imageMethodName = imageMethodName;
        }

        public final Object getInput() {
            return this.input;
        }

        public final void setInput(final Object input) {
            this.input = input;
        }

        public final boolean isAddContentProposal() {
            return this.addContentProposal;
        }

        public final void setAddContentProposal(final boolean addContentProposal) {
            this.addContentProposal = addContentProposal;
        }

        public final int getToolItemStyle() {
            return this.toolItemStyle;
        }

        public final void setToolItemStyle(final int toolItemStyle) {
            this.toolItemStyle = toolItemStyle;
        }

        public final boolean isAddEmptyElement() {
            return this.addEmptyElement;
        }

        public final void setAddEmptyElement(final boolean addEmptyElement) {
            this.addEmptyElement = addEmptyElement;
        }
    }
}
