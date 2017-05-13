package com.papao.books.ui.custom;

import com.papao.books.model.BlankMongoDbObject;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.providers.ComboElementContentProvider;
import com.papao.books.ui.providers.ContentProposalProvider;
import com.papao.books.ui.util.ColorUtil;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ComboImage2 extends Composite implements Listener {

    private static Logger logger = Logger.getLogger(ComboImage2.class);

    private static final int SIZE = 19;

    private Label imageLabel;
    private Combo combo;
    private ComboViewer viewer;
    private ToolItem itemAdd;
    private ToolItem itemMod;
    private ToolItem itemDel;

    private Object lastId;
    private String labelName;

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

    /**
     * If specified, the combo input elements must be instance of {@link ComboImageElement}
     */
    public final static int ADD_IMAGE = 1 << 4;

    public final static int ADD_CONTENT_PROPOSAL = 1 << 5;

    private boolean hasImage;
    private boolean hasAdd;
    private boolean hasMod;
    private boolean hasDel;
    private boolean hasContentProposal;
    private boolean hasToolBar;
    private boolean hasLabel;

    public ComboImage2(final Composite parent, int style) {
        this(parent, style, null);
    }

    public ComboImage2(final Composite parent, int style, String labelName) {
        super(parent, SWT.NONE);

        hasAdd = (style & ADD_ADD) == ADD_ADD;
        hasMod = (style & ADD_MOD) == ADD_MOD;
        hasDel = (style & ADD_DEL) == ADD_DEL;
        hasToolBar = hasAdd || hasMod || hasDel;
        hasImage = (style & ADD_IMAGE) == ADD_IMAGE;
        hasContentProposal = (style & ADD_CONTENT_PROPOSAL) == ADD_CONTENT_PROPOSAL;
        hasLabel = labelName != null;
        this.labelName = labelName;

        addComponents();
    }

    private void addComponents() {
        int numCols = 1;
        if (hasImage) {
            numCols++;
        }
        if (hasToolBar) {
            numCols++;
        }
        if (hasLabel) {
            numCols++;
        }
        GridDataFactory.fillDefaults().grab(false, false).align(SWT.BEGINNING, SWT.CENTER).applyTo(this);
        GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(numCols).margins(0, 0).extendedMargins(0, 0, 0, 0).spacing(0, 0).applyTo(this);

        if (hasLabel) {
            new Label(this, SWT.NONE).setText(labelName + "  ");
        }

        if (hasImage) {
            setImageLabel(new Label(this, SWT.BORDER));
            GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).hint(ComboImage2.SIZE, ComboImage2.SIZE).grab(false, true).minSize(ComboImage2.SIZE,
                    ComboImage2.SIZE).applyTo(getImageLabel());
            getImageLabel().addListener(SWT.Dispose, this);
        }

        int comboStyle = SWT.DROP_DOWN;
        if (!hasContentProposal) {
            comboStyle |= SWT.READ_ONLY;
        }
        setCombo(new Combo(this, comboStyle));
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, true).hint(100, SWT.DEFAULT).applyTo(getCombo());

        setViewer(new ComboViewer(getCombo()));
        getViewer().setContentProvider(new ComboElementContentProvider());
        getViewer().setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(final Object element) {
                return ((ComboElement) element).getText();
            }
        });
        getCombo().addListener(SWT.Selection, this);
        SWTeXtension.addColoredFocusListener(getCombo(), ColorUtil.COLOR_FOCUS_YELLOW);
        if (hasContentProposal) {
            ContentProposalProvider.addContentProposal(getCombo(), getCombo().getItems(), true);
        }

        if (!hasToolBar) {
            return;
        }
        ToolBar bar = new ToolBar(this, SWT.FLAT | SWT.NO_FOCUS);
        if (hasAdd) {
            this.itemAdd = new ToolItem(bar, SWT.NONE);
            this.itemAdd.setImage(AppImages.getImage16(AppImages.IMG_PLUS));
            this.itemAdd.setHotImage(AppImages.getImage16Focus(AppImages.IMG_PLUS));
            this.itemAdd.setToolTipText("Adaugare");
        }
        if (hasMod) {
            this.itemMod = new ToolItem(bar, SWT.NONE);
            this.itemMod.setImage(AppImages.getImage16(AppImages.IMG_MODIFICARE));
            this.itemMod.setHotImage(AppImages.getImage16Focus(AppImages.IMG_MODIFICARE));
            this.itemMod.setToolTipText("Modificare");
            this.itemMod.setEnabled(false);
        }
        if (hasDel) {
            this.itemDel = new ToolItem(bar, SWT.NONE);
            this.itemDel.setImage(AppImages.getImage16(AppImages.IMG_CANCEL));
            this.itemDel.setHotImage(AppImages.getImage16Focus(AppImages.IMG_CANCEL));
            this.itemDel.setToolTipText("Stergere");
            this.itemDel.setEnabled(false);
        }

        getCombo().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                enableOps();
            }
        });
    }

    private void enableOps() {
        if (itemMod != null) {
            itemMod.setEnabled(getSelectedElement() != null);
        }
        if (itemDel != null) {
            itemDel.setEnabled(getSelectedElement() != null);
        }
    }

    public void setInput(Collection<? extends ComboElement> input) {
        getViewer().setInput(input);
        enableOps();
        if (input == null) {
            return;
        }
        if (hasContentProposal) {
            ContentProposalProvider.addContentProposal(getCombo(), getCombo().getItems(), true);
        }
    }

    private void setImage(final Object element) {
        Image result = AppImages.getImage(AppImages.IMAGE_NOT_FOUND_16X16, ComboImage2.SIZE, ComboImage2.SIZE);
        try {
            if ((getImageLabel() == null) || getImageLabel().isDisposed()) {
                return;
            }
            if ((getImageLabel().getImage() != null) && !getImageLabel().getImage().isDisposed()
                    && !AppImages.IMAGE_NOT_FOUND_16X16.equals(getImageLabel().getImage())) {
                getImageLabel().getImage().dispose();
            }
            if (element == null || !(element instanceof ComboImageElement)) {
                getImageLabel().setImage(result);
                return;
            }
            result = ((ComboImageElement) element).getImage();
            if ((result == null) || result.isDisposed()) {
                result = AppImages.getImage(AppImages.IMAGE_NOT_FOUND_16X16, ComboImage2.SIZE, ComboImage2.SIZE);
                if ((getImageLabel() != null) && !getImageLabel().isDisposed()) {
                    getImageLabel().setImage(result);
                    return;
                }
            }

            result = AppImages.getImage(result, ComboImage2.SIZE, ComboImage2.SIZE);
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

    public final ComboElement getSelectedElement() {
        if (getCombo().getSelectionIndex() == -1) {
            return null;
        }
        return (ComboElement) getViewer().getElementAt(getCombo().getSelectionIndex());
    }

    public final Object getSelectedObjectId() {
        return getSelectedElement() instanceof ComboIdElement ? ((ComboIdElement) getSelectedElement()).getId() : null;
    }

    public final void select(final String str) {
        if ((str == null) || this.combo.isDisposed() || (this.combo.indexOf(str) == -1)) {
            return;
        }
        this.combo.select(this.combo.indexOf(str));
        if (getSelectedObjectId() != null && getSelectedObjectId().equals(this.lastId)) {
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

    public final void setItems(String[] items) {
        if (items == null) {
            setInput(null);
            return;
        }
        java.util.List<ComboElement> convertedList = new ArrayList<>();
        for (String item : items) {
            convertedList.add(new ComboElementDummy(item));
        }
        setInput(convertedList);
    }

    private ComboElement[] getElements(Object input) {
        try {
            if (input == null) {
                return new ComboElement[0];
            }
            ComboElement[] elements;
            if (input instanceof Collection) {
                elements = ((Collection<ComboElement>) input).toArray(new ComboElement[((Collection) input).size()]);
            } else if (input instanceof Object[]) {
                elements = (ComboElement[]) input;
            } else if (input instanceof Map<?, ?>) {
                return getElements(((Map<?, ?>) input).values());
            } else {
                elements = new ComboElement[]{(ComboElement) input};
            }
            return elements;
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return new ComboElement[0];
        }
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

    private final static BlankMongoDbObject[] getBlankObjectsInput(final String[] str) {
        if (str == null) {
            return new BlankMongoDbObject[0];
        }
        BlankMongoDbObject[] bdo = new BlankMongoDbObject[str.length];
        for (int i = 0; i < str.length; i++) {
            bdo[i] = new BlankMongoDbObject(str[i]);
        }
        return bdo;
    }

    public final ComboElement[] getInput() {
        return (ComboElement[]) getViewer().getInput();
    }
}
